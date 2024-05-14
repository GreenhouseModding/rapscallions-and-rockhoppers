package dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import dev.greenhouseteam.rapscallionsandrockhoppers.attachment.PlayerLinksAttachment;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;

public record SyncPlayerLinksAttachmentPacket(int entityId, PlayerLinksAttachment attachment) implements RapscallionsAndRockhoppersPacketS2C {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("sync_player_link_attachment");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeNbt(PlayerLinksAttachment.CODEC.encodeStart(NbtOps.INSTANCE, attachment).getOrThrow(false, RapscallionsAndRockhoppers.LOG::error));
    }

    public static SyncPlayerLinksAttachmentPacket read(FriendlyByteBuf buf) {
        return new SyncPlayerLinksAttachmentPacket(buf.readInt(), PlayerLinksAttachment.CODEC.decode(NbtOps.INSTANCE, buf.readNbt()).getOrThrow(false, RapscallionsAndRockhoppers.LOG::error).getFirst());
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

                if (!(entity instanceof Player player)) {
                    RapscallionsAndRockhoppers.LOG.warn("Could not sync player link attachment.");
                    return;
                }

                IRockhoppersPlatformHelper.INSTANCE.getPlayerData(player).setFrom(attachment);
            }
        });
    }
}
