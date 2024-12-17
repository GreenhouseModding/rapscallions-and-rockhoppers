package dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.attachment.PlayerLinksAttachment;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public record SyncPlayerLinksAttachmentPacketS2C(int entityId, PlayerLinksAttachment attachment) implements CustomPacketPayload {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("sync_player_link_attachment");
    public static final Type<SyncPlayerLinksAttachmentPacketS2C> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerLinksAttachmentPacketS2C> STREAM_CODEC = StreamCodec.of(SyncPlayerLinksAttachmentPacketS2C::write, SyncPlayerLinksAttachmentPacketS2C::new);

    public SyncPlayerLinksAttachmentPacketS2C(FriendlyByteBuf buf) {
        this(buf.readInt(), PlayerLinksAttachment.CODEC.decode(NbtOps.INSTANCE, buf.readNbt()).getOrThrow(result -> {
            RapscallionsAndRockhoppers.LOG.error("Could not decode player link attachment: {}", result);
            return null;
        }).getFirst());
    }

    public static void write(FriendlyByteBuf buf, SyncPlayerLinksAttachmentPacketS2C packet) {
        buf.writeInt(packet.entityId);
        buf.writeNbt(PlayerLinksAttachment.CODEC.encodeStart(NbtOps.INSTANCE, packet.attachment).getOrThrow(result -> {
            RapscallionsAndRockhoppers.LOG.error("Could not encode player link attachment: {}", result);
            return null;
        }));
    }

    public static SyncPlayerLinksAttachmentPacketS2C read(FriendlyByteBuf buf) {
        return new SyncPlayerLinksAttachmentPacketS2C(buf.readInt(), PlayerLinksAttachment.CODEC.decode(NbtOps.INSTANCE, buf.readNbt()).getOrThrow(result -> {
            RapscallionsAndRockhoppers.LOG.error("Could not decode player link attachment: {}", result);
            return null;
        }).getFirst());
    }


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

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
