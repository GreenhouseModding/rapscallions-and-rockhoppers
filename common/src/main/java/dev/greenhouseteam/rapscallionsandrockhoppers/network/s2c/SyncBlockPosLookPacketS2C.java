package dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record SyncBlockPosLookPacketS2C(int entityId, int otherEntityId, Vec3 lookPos) implements CustomPacketPayload {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("sync_x_rotation");
    public static final Type<SyncBlockPosLookPacketS2C> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBlockPosLookPacketS2C> STREAM_CODEC = StreamCodec.of(SyncBlockPosLookPacketS2C::write, SyncBlockPosLookPacketS2C::new);

    public SyncBlockPosLookPacketS2C(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readVec3());
    }

    public static void write(FriendlyByteBuf buf, SyncBlockPosLookPacketS2C packet) {
        buf.writeInt(packet.entityId());
        buf.writeInt(packet.otherEntityId());
        buf.writeVec3(packet.lookPos());
    }

    public static SyncBlockPosLookPacketS2C read(FriendlyByteBuf buf) {
        return new SyncBlockPosLookPacketS2C(buf.readInt(), buf.readInt(), buf.readVec3());
    }

    public void handle() {
        // Lambdarised version of this will break on NeoForge.
        Minecraft.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Entity entity = Minecraft.getInstance().level.getEntity(entityId());
                Entity otherEntity = Minecraft.getInstance().level.getEntity(otherEntityId());

                if (entity == null || otherEntity == null) {
                    RapscallionsAndRockhoppers.LOG.warn("Could not sync rotations of penguins.");
                    return;
                }

                entity.lookAt(EntityAnchorArgument.Anchor.FEET, lookPos());
                otherEntity.lookAt(EntityAnchorArgument.Anchor.FEET, lookPos());
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
