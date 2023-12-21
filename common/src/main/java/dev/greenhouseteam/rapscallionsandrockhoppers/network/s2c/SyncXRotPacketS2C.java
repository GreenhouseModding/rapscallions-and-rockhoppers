package dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record SyncXRotPacketS2C(int entityId, int otherEntityId, BlockPos lookPos) implements RapscallionsAndRockhoppersPacketS2C {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("sync_x_rotation");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());
        buf.writeInt(this.otherEntityId());
        buf.writeBlockPos(this.lookPos());
    }

    public static SyncXRotPacketS2C decode(FriendlyByteBuf buf) {
        return new SyncXRotPacketS2C(buf.readInt(), buf.readInt(), buf.readBlockPos());
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
                Entity entity = Minecraft.getInstance().level.getEntity(entityId());
                Entity otherEntity = Minecraft.getInstance().level.getEntity(otherEntityId());

                if (entity == null || otherEntity == null) {
                    RapscallionsAndRockhoppers.LOG.warn("Could not sync rotations of penguins.");
                    return;
                }

                entity.lookAt(EntityAnchorArgument.Anchor.FEET, lookPos().getCenter());
                otherEntity.lookAt(EntityAnchorArgument.Anchor.FEET, lookPos().getCenter());
            }
        });
    }
}
