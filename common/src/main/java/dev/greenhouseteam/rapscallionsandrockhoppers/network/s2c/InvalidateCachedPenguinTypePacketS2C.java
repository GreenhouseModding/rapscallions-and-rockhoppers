package dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record InvalidateCachedPenguinTypePacketS2C(int penguinEntityId, ResourceLocation penguinTypeId) implements CustomPacketPayload {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("invalidate_cached_penguin_type");
    public static final Type<InvalidateCachedPenguinTypePacketS2C> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, InvalidateCachedPenguinTypePacketS2C> STREAM_CODEC = StreamCodec.of(InvalidateCachedPenguinTypePacketS2C::write, InvalidateCachedPenguinTypePacketS2C::new);

    public InvalidateCachedPenguinTypePacketS2C(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readResourceLocation());
    }

    public static void write(FriendlyByteBuf buf, InvalidateCachedPenguinTypePacketS2C packet) {
        buf.writeInt(packet.penguinEntityId());
        buf.writeResourceLocation(packet.penguinTypeId());
    }

    public static InvalidateCachedPenguinTypePacketS2C read(FriendlyByteBuf buf) {
        return new InvalidateCachedPenguinTypePacketS2C(buf.readInt(), buf.readResourceLocation());
    }

    public void handle() {
        // Lambdarised version of this will break on NeoForge.
        Minecraft.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Entity entity = Minecraft.getInstance().level.getEntity(penguinEntityId());

                if (!(entity instanceof Penguin penguin)) {
                    RapscallionsAndRockhoppers.LOG.warn("Could not invalidate cached penguin type for non penguin.");
                    return;
                }

                penguin.setPenguinType(penguinTypeId());
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}