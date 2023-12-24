package dev.greenhouseteam.rapscallionsandrockhoppers.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PathfindUtil {
    @Nullable
    public static Vec3 getRandomLandPos(PathfinderMob mob, int radius, int yRange) {
        Vec3 pos = LandRandomPos.getPos(mob, radius, yRange);

        for(int checkAmount = 0; pos != null && (!mob.level().getBlockState(BlockPos.containing(pos)).isPathfindable(mob.level(), BlockPos.containing(pos).above(), PathComputationType.LAND) || !isLandBlock(mob, BlockPos.containing(pos))) && checkAmount++ < 10; pos = LandRandomPos.getPos(mob, radius, yRange)) {
        }

        return pos;
    }

    private static boolean isLandBlock(PathfinderMob mob, BlockPos pos) {
        return GoalUtils.isSolid(mob, pos.below()) && !GoalUtils.isWater(mob, pos);
    }
}
