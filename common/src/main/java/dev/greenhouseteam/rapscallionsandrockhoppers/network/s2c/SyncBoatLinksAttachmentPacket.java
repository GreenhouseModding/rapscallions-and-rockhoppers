package dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;

public record SyncBoatLinksAttachmentPacket(int entityId, BoatLinksAttachment attachment) implements RapscallionsAndRockhoppersPacketS2C {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("sync_boat_link_attachment");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeNbt(BoatLinksAttachment.CODEC.encodeStart(NbtOps.INSTANCE, attachment).getOrThrow(false, RapscallionsAndRockhoppers.LOG::error));
    }

    public static SyncBoatLinksAttachmentPacket read(FriendlyByteBuf buf) {
        return new SyncBoatLinksAttachmentPacket(buf.readInt(), BoatLinksAttachment.CODEC.decode(NbtOps.INSTANCE, buf.readNbt()).getOrThrow(false, RapscallionsAndRockhoppers.LOG::error).getFirst());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handle() {
        // Lambdarised version of this will break on NeoForge.
        Minecraft.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Entity entity = Minecraft.getInstance().level.getEntity(entityId());

                if (!(entity instanceof Boat boat)) {
                    RapscallionsAndRockhoppers.LOG.warn("Could not sync boat link attachment.");
                    return;
                }

                IRockhoppersPlatformHelper.INSTANCE.getBoatData(boat).setFrom(attachment);
            }
        });
    }
}
