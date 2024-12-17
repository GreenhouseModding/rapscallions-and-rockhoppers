package dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;

public record SyncBoatLinksAttachmentPacketS2C(int entityId, BoatLinksAttachment attachment) implements CustomPacketPayload {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("sync_boat_link_attachment");
    public static final Type<SyncBoatLinksAttachmentPacketS2C> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBoatLinksAttachmentPacketS2C> STREAM_CODEC = StreamCodec.of(SyncBoatLinksAttachmentPacketS2C::write, SyncBoatLinksAttachmentPacketS2C::new);

    public SyncBoatLinksAttachmentPacketS2C(FriendlyByteBuf buf) {
        this(buf.readInt(), BoatLinksAttachment.CODEC.decode(NbtOps.INSTANCE, buf.readNbt()).getOrThrow(result -> {
            RapscallionsAndRockhoppers.LOG.error("Could not decode boat link attachment: {}", result);
            return null;
        }).getFirst());
    }

    public static void write(FriendlyByteBuf buf, SyncBoatLinksAttachmentPacketS2C packet) {
        buf.writeInt(packet.entityId);
        buf.writeNbt(BoatLinksAttachment.CODEC.encodeStart(NbtOps.INSTANCE, packet.attachment).getOrThrow(result -> {
            RapscallionsAndRockhoppers.LOG.error("Could not encode boat link attachment: {}", result);
            return null;
        }));
    }

    public static SyncBoatLinksAttachmentPacketS2C read(FriendlyByteBuf buf) {
        return new SyncBoatLinksAttachmentPacketS2C(buf.readInt(), BoatLinksAttachment.CODEC.decode(NbtOps.INSTANCE, buf.readNbt()).getOrThrow(result -> {
            RapscallionsAndRockhoppers.LOG.error("Could not decode boat link attachment: {}", result);
            return null;
        }).getFirst());
    }

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

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
