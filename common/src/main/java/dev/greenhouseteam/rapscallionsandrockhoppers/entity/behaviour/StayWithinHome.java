package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class StayWithinHome extends ExtendedBehaviour<Penguin> {
    private int radius = 0;
    private Vec3 runPos = null;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT));
    }

    public StayWithinHome setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Penguin penguin) {
        GlobalPos target = BrainUtils.getMemory(penguin, MemoryModuleType.HOME);
        if (target == null || target.dimension() != level.dimension()) {
            return false;
        }
        double distToTarget = penguin.blockPosition().distSqr(target.pos());
        if (distToTarget < this.radius * this.radius) {
            return false;
        }

        Vec3 runPos = DefaultRandomPos.getPosTowards(penguin, this.radius, 10, target.pos().getCenter(), Mth.HALF_PI);

        if (runPos == null || target.pos().distSqr(BlockPos.containing(runPos)) > distToTarget)
            return false;

        this.runPos = runPos;
        return true;
    }

    @Override
    protected void start(Penguin penguin) {
        BrainUtils.setMemory(penguin, MemoryModuleType.WALK_TARGET, new WalkTarget(this.runPos, 1.0F, 0));
    }

    @Override
    protected void stop(Penguin penguin) {
        this.runPos = null;
    }

}
