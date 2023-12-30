package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BreatheAir extends ExtendedBehaviour<Penguin> {
    private Vec3 targetPos;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(SBLMemoryTypes.NEARBY_BLOCKS.get(), MemoryStatus.VALUE_PRESENT));
    }

    protected boolean checkExtraStartConditions(ServerLevel level, Penguin penguin) {
        if (penguin.getAirSupply() > 140) {
            return false;
        }
        Optional<BlockPos> optional = BrainUtils.getMemory(penguin, SBLMemoryTypes.NEARBY_BLOCKS.get()).stream().filter(blockPosBlockStatePair -> blockPosBlockStatePair.getSecond().isAir()).map(Pair::getFirst).min(Comparator.comparing(pos -> penguin.blockPosition().distSqr(pos)));
        if (optional.isPresent()) {
            this.targetPos = optional.get().getCenter();
        } else if (!penguin.getNavigation().isInProgress()) {
            this.targetPos = BehaviorUtils.getRandomSwimmablePos(penguin, 12, 6);
        }
        return targetPos != null;
    }

    protected void start(Penguin penguin) {
        BrainUtils.setMemory(penguin, MemoryModuleType.WALK_TARGET, new WalkTarget(this.targetPos, 1.0F, 0));
    }
}
