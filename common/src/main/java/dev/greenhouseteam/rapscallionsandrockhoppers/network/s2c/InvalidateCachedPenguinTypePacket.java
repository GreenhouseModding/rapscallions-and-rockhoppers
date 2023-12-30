package dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record InvalidateCachedPenguinTypePacket(int penguinEntityId) implements RapscallionsAndRockhoppersPacketS2C {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("invalidate_cached_penguin_type");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.penguinEntityId());
    }

    public static InvalidateCachedPenguinTypePacket decode(FriendlyByteBuf buf) {
        return new InvalidateCachedPenguinTypePacket(buf.readInt());
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
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

                penguin.invalidateCachedPenguinType();
            }
        });
    }
}