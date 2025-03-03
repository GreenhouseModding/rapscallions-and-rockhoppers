package house.greenhouse.rapscallionsandrockhoppers.network.s2c;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.attachment.PlayerLinksAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public record SyncPlayerLinksAttachmentPacketS2C(int entityId, Optional<PlayerLinksAttachment> attachment) implements CustomPacketPayload {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("sync_player_link_attachment");
    public static final Type<SyncPlayerLinksAttachmentPacketS2C> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerLinksAttachmentPacketS2C> STREAM_CODEC = StreamCodec.of(SyncPlayerLinksAttachmentPacketS2C::write, SyncPlayerLinksAttachmentPacketS2C::new);

    public SyncPlayerLinksAttachmentPacketS2C(FriendlyByteBuf buf) {
        this(buf.readInt(), ByteBufCodecs.optional(ByteBufCodecs.fromCodec(PlayerLinksAttachment.CODEC)).decode(buf));
    }

    public static void write(FriendlyByteBuf buf, SyncPlayerLinksAttachmentPacketS2C packet) {
        buf.writeInt(packet.entityId);
        ByteBufCodecs.optional(ByteBufCodecs.fromCodec(PlayerLinksAttachment.CODEC)).encode(buf, packet.attachment);
    }

    public void handle() {
        Minecraft.getInstance().execute(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(entityId());

            if (!(entity instanceof Player player)) {
                RapscallionsAndRockhoppers.LOG.warn("Could not sync player link attachment.");
                return;
            }

            if (attachment.isEmpty()) {
                RapscallionsAndRockhoppers.getHelper().removePlayerData(player);
                return;
            }
            RapscallionsAndRockhoppers.getHelper().getPlayerData(player).setFrom(attachment.get());
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
