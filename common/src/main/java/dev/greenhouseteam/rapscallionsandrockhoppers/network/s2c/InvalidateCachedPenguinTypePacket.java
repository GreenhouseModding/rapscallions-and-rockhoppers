package dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record InvalidateCachedPenguinTypePacket(int penguinEntityId, ResourceLocation penguinTypeId) implements RapscallionsAndRockhoppersPacketS2C {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("invalidate_cached_penguin_type");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.penguinEntityId());
        buf.writeResourceLocation(this.penguinTypeId());
    }

    public static InvalidateCachedPenguinTypePacket read(FriendlyByteBuf buf) {
        return new InvalidateCachedPenguinTypePacket(buf.readInt(), buf.readResourceLocation());
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
                Entity entity = Minecraft.getInstance().level.getEntity(penguinEntityId());

                if (!(entity instanceof Penguin penguin)) {
                    RapscallionsAndRockhoppers.LOG.warn("Could not invalidate cached penguin type for non penguin.");
                    return;
                }

                penguin.setPenguinType(penguinTypeId());
            }
        });
    }
}