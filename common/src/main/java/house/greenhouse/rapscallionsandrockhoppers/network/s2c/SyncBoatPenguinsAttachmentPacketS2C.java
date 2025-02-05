package house.greenhouse.rapscallionsandrockhoppers.network.s2c;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatPenguinsAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;

public record SyncBoatPenguinsAttachmentPacketS2C(int entityId, BoatPenguinsAttachment attachment) implements CustomPacketPayload {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("sync_boat_penguins_attachment");
    public static final Type<SyncBoatPenguinsAttachmentPacketS2C> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBoatPenguinsAttachmentPacketS2C> STREAM_CODEC = StreamCodec.of(SyncBoatPenguinsAttachmentPacketS2C::write, SyncBoatPenguinsAttachmentPacketS2C::new);

    public SyncBoatPenguinsAttachmentPacketS2C(FriendlyByteBuf buf) {
        this(buf.readInt(), BoatPenguinsAttachment.CODEC.decode(NbtOps.INSTANCE, buf.readNbt()).getOrThrow(result -> {
            RapscallionsAndRockhoppers.LOG.error("Could not decode boat penguins attachment: {}", result);
            return null;
        }).getFirst());
    }

    public static void write(FriendlyByteBuf buf, SyncBoatPenguinsAttachmentPacketS2C packet) {
        buf.writeInt(packet.entityId);
        buf.writeNbt(BoatPenguinsAttachment.CODEC.encodeStart(NbtOps.INSTANCE, packet.attachment).getOrThrow(result -> {
            RapscallionsAndRockhoppers.LOG.error("Could not encode boat penguins attachment: {}", result);
            return null;
        }));
    }

    public void handle() {
        Minecraft.getInstance().execute(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(entityId());

            if (!(entity instanceof Boat boat)) {
                RapscallionsAndRockhoppers.LOG.warn("Could not sync boat penguins attachment.");
                return;
            }

            RapscallionsAndRockhoppers.getHelper().getBoatPenguinData(boat).setFrom(attachment);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
