package dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.JumpGoal;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class PenguinJumpGoal extends JumpGoal {
    private static final int[] STEPS_TO_CHECK = new int[]{0, 1, 6, 7, 8, 9, 10};
    private final Penguin penguin;
    private final int interval;
    private boolean breached;

    public PenguinJumpGoal(Penguin penguin, int interval) {
        this.penguin = penguin;
        this.interval = reducedTickDelay(interval);
    }

    @Override
    public boolean canUse() {
        if (this.penguin.getRandom().nextInt(this.interval) != 0 || !this.penguin.isInWaterOrBubble()) {
            return false;
        } else {
            Direction direction = this.penguin.getMotionDirection();
            int x = direction.getStepX();
            int z = direction.getStepZ();
            BlockPos blockPos = this.penguin.blockPosition();

            for(int step : STEPS_TO_CHECK) {
                if (!this.waterIsClear(blockPos, x, z, step) || !this.surfaceIsClear(blockPos, x, z, step)) {
                    return false;
                }
            }

            return true;
        }
    }

    private boolean waterIsClear(BlockPos pos, int x, int z, int step) {
        BlockPos blockPos = pos.offset(x * step, 0, z * step);
        while (this.penguin.level().getFluidState(blockPos).is(FluidTags.WATER)) {
            blockPos = blockPos.offset(0, 1, 0);
        }
        blockPos = blockPos.offset(0, -1, 0);
        return this.penguin.level().getFluidState(blockPos).is(FluidTags.WATER) && !this.penguin.level().getBlockState(blockPos).blocksMotion();
    }

    private boolean surfaceIsClear(BlockPos pos, int x, int z, int step) {
        BlockPos blockPos = pos.offset(x * step, 1, z * step);
        while (this.penguin.level().getFluidState(blockPos).is(FluidTags.WATER)) {
            blockPos = blockPos.offset(0, 1, 0);
        }
        return this.penguin.level().getBlockState(blockPos).isAir()
                && this.penguin.level().getBlockState(blockPos).isAir();
    }

    @Override
    public boolean canContinueToUse() {
        double yMovement = this.penguin.getDeltaMovement().y;
        return (!(yMovement * yMovement < 0.03F) || this.penguin.getXRot() == 0.0F || !(Math.abs(this.penguin.getXRot()) < 10.0F) || !this.penguin.isInWater())
                && !this.penguin.onGround();
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        Direction direction = this.penguin.getMotionDirection();
        this.penguin.setDeltaMovement(this.penguin.getDeltaMovement().add((double)direction.getStepX() * 1.2, 0.5, (double)direction.getStepZ() * 1.2));
        this.penguin.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.penguin.setXRot(0.0F);
    }

    @Override
    public void tick() {
        boolean breached = this.breached;
        if (!breached) {
            FluidState fluidState = this.penguin.level().getFluidState(this.penguin.blockPosition());
            this.breached = fluidState.is(FluidTags.WATER);
        }

        if (this.breached && !breached) {
            this.penguin.playSound(RapscallionsAndRockhoppersSoundEvents.PENGUIN_JUMP, 1.0F, 1.0F);
        }

        Vec3 movement = this.penguin.getDeltaMovement();
        if (movement.y * movement.y < 0.03F && this.penguin.getXRot() != 0.0F) {
            this.penguin.setXRot(Mth.rotLerp(0.2F, this.penguin.getXRot(), 0.0F));
        } else if (movement.length() > 1.0E-5F) {
            double horizontalDistance = movement.horizontalDistance();
            double atan2 = Math.atan2(-movement.y, horizontalDistance) * 180.0F / (float)Math.PI;
            this.penguin.setXRot((float)atan2);
        }
    }
}
