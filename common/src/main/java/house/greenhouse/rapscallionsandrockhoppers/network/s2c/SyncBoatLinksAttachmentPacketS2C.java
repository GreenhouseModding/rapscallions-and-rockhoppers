package house.greenhouse.rapscallionsandrockhoppers.network.s2c;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.Optional;

public record SyncBoatLinksAttachmentPacketS2C(int entityId, Optional<BoatLinksAttachment> attachment) implements CustomPacketPayload {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("sync_boat_link_attachment");
    public static final Type<SyncBoatLinksAttachmentPacketS2C> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBoatLinksAttachmentPacketS2C> STREAM_CODEC = StreamCodec.of(SyncBoatLinksAttachmentPacketS2C::write, SyncBoatLinksAttachmentPacketS2C::new);

    public SyncBoatLinksAttachmentPacketS2C(FriendlyByteBuf buf) {
        this(buf.readInt(), ByteBufCodecs.optional(ByteBufCodecs.fromCodec(BoatLinksAttachment.CODEC)).decode(buf));
    }

    public static void write(FriendlyByteBuf buf, SyncBoatLinksAttachmentPacketS2C packet) {
        buf.writeInt(packet.entityId);
        ByteBufCodecs.optional(ByteBufCodecs.fromCodec(BoatLinksAttachment.CODEC)).encode(buf, packet.attachment);
    }

    public void handle() {
        Minecraft.getInstance().execute(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(entityId());

            if (!(entity instanceof Boat boat)) {
                RapscallionsAndRockhoppers.LOG.warn("Could not sync boat link attachment.");
                return;
            }

            if (attachment.isEmpty()) {
                RapscallionsAndRockhoppers.getHelper().removeBoatData(boat);
                return;
            }
            RapscallionsAndRockhoppers.getHelper().getBoatData(boat).setFrom(attachment.get());
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
