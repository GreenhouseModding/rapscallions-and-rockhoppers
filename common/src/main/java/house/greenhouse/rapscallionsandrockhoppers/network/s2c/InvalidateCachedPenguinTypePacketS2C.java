package house.greenhouse.rapscallionsandrockhoppers.network.s2c;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.entity.PenguinVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record InvalidateCachedPenguinTypePacketS2C(int penguinEntityId, Holder<PenguinVariant> penguinVariant) implements CustomPacketPayload {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("invalidate_cached_penguin_type");
    public static final Type<InvalidateCachedPenguinTypePacketS2C> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, InvalidateCachedPenguinTypePacketS2C> STREAM_CODEC = StreamCodec.of(InvalidateCachedPenguinTypePacketS2C::write, InvalidateCachedPenguinTypePacketS2C::new);

    public InvalidateCachedPenguinTypePacketS2C(RegistryFriendlyByteBuf buf) {
        this(buf.readInt(), PenguinVariant.STREAM_CODEC.decode(buf));
    }

    public static void write(RegistryFriendlyByteBuf buf, InvalidateCachedPenguinTypePacketS2C packet) {
        buf.writeInt(packet.penguinEntityId());
        PenguinVariant.STREAM_CODEC.encode(buf, packet.penguinVariant());
    }

    public void handle() {
        Minecraft.getInstance().execute(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(penguinEntityId());

            if (!(entity instanceof Penguin penguin)) {
                RapscallionsAndRockhoppers.LOG.warn("Could not invalidate cached penguin type for non penguin.");
                return;
            }

            penguin.setVariant(penguinVariant());
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}