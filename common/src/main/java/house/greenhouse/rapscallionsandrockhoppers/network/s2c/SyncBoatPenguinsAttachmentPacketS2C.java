package house.greenhouse.rapscallionsandrockhoppers.network.s2c;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatPenguinsAttachment;
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

public record SyncBoatPenguinsAttachmentPacketS2C(int entityId, Optional<BoatPenguinsAttachment> attachment) implements CustomPacketPayload {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("sync_boat_penguins_attachment");
    public static final Type<SyncBoatPenguinsAttachmentPacketS2C> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBoatPenguinsAttachmentPacketS2C> STREAM_CODEC = StreamCodec.of(SyncBoatPenguinsAttachmentPacketS2C::write, SyncBoatPenguinsAttachmentPacketS2C::new);

    public SyncBoatPenguinsAttachmentPacketS2C(FriendlyByteBuf buf) {
        this(buf.readInt(), ByteBufCodecs.optional(ByteBufCodecs.fromCodec(BoatPenguinsAttachment.CODEC)).decode(buf));
    }

    public static void write(FriendlyByteBuf buf, SyncBoatPenguinsAttachmentPacketS2C packet) {
        buf.writeInt(packet.entityId);
        ByteBufCodecs.optional(ByteBufCodecs.fromCodec(BoatPenguinsAttachment.CODEC)).encode(buf, packet.attachment);
    }

    public void handle() {
        Minecraft.getInstance().execute(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(entityId());

            if (!(entity instanceof Boat boat)) {
                RapscallionsAndRockhoppers.LOG.warn("Could not sync boat penguins attachment.");
                return;
            }

            if (attachment.isEmpty()) {
                RapscallionsAndRockhoppers.getHelper().removeBoatPenguinData(boat);
                return;
            }
            RapscallionsAndRockhoppers.getHelper().getBoatPenguinData(boat).setFrom(attachment.get());
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
