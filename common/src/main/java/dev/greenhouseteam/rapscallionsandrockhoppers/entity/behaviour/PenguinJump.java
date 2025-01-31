package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.Optional;

public class PenguinJump extends ExtendedBehaviour<Penguin> {
    private static final int[] STEPS_TO_CHECK = new int[]{0, 1, 4, 5, 6, 7, 8};
    private boolean breached;

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Penguin penguin) {
        if (penguin.getTimeAllowedToWaterJump() > penguin.tickCount || penguin.getAirSupply() > 260) {
            return false;
        }

        Direction direction = penguin.getMotionDirection();
        int x = direction.getStepX();
        int z = direction.getStepZ();
        BlockPos blockPos = penguin.blockPosition();

        for(int step : STEPS_TO_CHECK) {
            if (!this.waterIsClear(level, blockPos, x, z, step) || !this.surfaceIsClear(level, blockPos, x, z, step)) {
                return false;
            }
        }

        return true;
    }

    private boolean waterIsClear(ServerLevel level, BlockPos pos, int x, int z, int step) {
        BlockPos blockPos = pos.offset(x * step, 0, z * step);
        return level.getFluidState(blockPos).is(FluidTags.WATER) && !level.getBlockState(blockPos).blocksMotion();
    }

    private boolean surfaceIsClear(ServerLevel level, BlockPos pos, int x, int z, int step) {
        BlockPos blockPos = pos.offset(x * step, 1, z * step);
        return level.getBlockState(blockPos).isAir()
                && level.getBlockState(blockPos).isAir();
    }

    @Override
    public boolean shouldKeepRunning(Penguin penguin) {
        double yMovement = penguin.getDeltaMovement().y();
        return (!(yMovement * yMovement < 0.03F) || penguin.getXRot() == 0.0F || !(Math.abs(penguin.getXRot()) < 30.0F) || !penguin.isInWater())
                && !penguin.onGround();
    }

    @Override
    protected void start(Penguin penguin) {
        BrainUtils.setMemory(penguin, RockhoppersMemoryModuleTypes.IS_JUMPING, Unit.INSTANCE);
        Direction direction = penguin.getMotionDirection();
        penguin.setDeltaMovement(penguin.getDeltaMovement().add(direction.getStepX() * 0.6, 0.4, direction.getStepZ() * 0.6));
    }

    @Override
    protected void stop(Penguin penguin) {
        this.breached = false;
        penguin.setXRot(0.0F);
        penguin.setTimeAllowedToWaterJump(Optional.of(penguin.tickCount + Mth.randomBetweenInclusive(penguin.getRandom(), 400, 600)));
        BrainUtils.clearMemories(penguin, RockhoppersMemoryModuleTypes.IS_JUMPING);
    }

    @Override
    public void tick(Penguin penguin) {
        boolean breached = this.breached;
        if (!breached) {
            FluidState fluidState = penguin.level().getFluidState(penguin.blockPosition());
            this.breached = fluidState.is(FluidTags.WATER);
        }

        if (this.breached && !breached) {
            penguin.playSound(penguin.getWaterJumpSound(), 1.0F, 1.0F);
        }

        Vec3 movement = penguin.getDeltaMovement();
        if (movement.y < 0.0F && penguin.getXRot() != 0.0F) {
            penguin.setXRot(Mth.rotLerp(0.2F, penguin.getXRot(), 0.0F));
        } else if (movement.length() > 1.0E-5F) {
            double horizontalDistance = movement.horizontalDistance();
            double atan2 = Math.atan2(-movement.y, horizontalDistance) * 180.0F / Mth.PI;
            penguin.setXRot((float)atan2);
        }
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_PRESENT));
    }
}
