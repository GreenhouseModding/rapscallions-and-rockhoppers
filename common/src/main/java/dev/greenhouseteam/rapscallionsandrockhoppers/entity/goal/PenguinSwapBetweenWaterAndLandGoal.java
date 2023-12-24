package dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathComputationType;

import java.util.EnumSet;

public class PenguinSwapBetweenWaterAndLandGoal extends MoveToBlockGoal {
    private static final int SWAP_CHANCE = 600;
    public PenguinSwapBetweenWaterAndLandGoal(Penguin penguin) {
        super(penguin, 1.0F, 16);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }
    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && !this.isReachedTarget();
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(SWAP_CHANCE);
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos pos) {
        return (((Penguin)this.mob).getPointOfInterest() == null || pos.distManhattan(((Penguin) this.mob).getPointOfInterest()) > 3) && (!this.mob.isInWaterOrBubble() && levelReader.getBlockState(pos).isPathfindable(levelReader, pos, PathComputationType.WATER) || this.mob.isInWaterOrBubble() && !GoalUtils.isWater(this.mob, pos) && GoalUtils.isSolid(this.mob, pos.below()) && levelReader.getBlockState(pos).isPathfindable(levelReader, pos, PathComputationType.LAND));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

}
