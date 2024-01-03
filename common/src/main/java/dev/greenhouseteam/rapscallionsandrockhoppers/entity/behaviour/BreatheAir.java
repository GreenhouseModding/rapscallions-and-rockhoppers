package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class BreatheAir extends ExtendedBehaviour<Penguin> {
    private BlockPosTracker targetPos;

    public BreatheAir() {
        this.runFor(penguin -> 260);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(SBLMemoryTypes.NEARBY_BLOCKS.get(), MemoryStatus.VALUE_PRESENT));
    }

    protected boolean checkExtraStartConditions(ServerLevel level, Penguin penguin) {
        if (penguin.getAirSupply() > 260) {
            return false;
        }
        Optional<BlockPos> optional = BrainUtils.memoryOrDefault(penguin, SBLMemoryTypes.NEARBY_BLOCKS.get(), List::of).stream().filter(blockPosBlockStatePair -> blockPosBlockStatePair.getSecond().isAir()).map(Pair::getFirst).min(Comparator.comparing(pos -> penguin.blockPosition().distSqr(pos)));
        Vec3 targetPos = optional.map(BlockPos::getCenter).orElseGet(() -> {
            Vec3 penguinPos = penguin.position().add(0, 8, 0);
            if (penguin.level().getBlockState(BlockPos.containing(penguinPos)).isAir()) {
                return penguinPos;
            }
            return BehaviorUtils.getRandomSwimmablePos(penguin, 6, 12);
        });
        if (targetPos != null) {
            this.targetPos = new BlockPosTracker(BlockPos.containing(targetPos));
        }
        return this.targetPos != null;
    }

    @Override
    protected boolean shouldKeepRunning(Penguin penguin) {
        return penguin.getAirSupply() <= 260;
    }

    protected void tick(Penguin penguin) {
        if (BrainUtils.getMemory(penguin, MemoryModuleType.WALK_TARGET) == null || BrainUtils.getMemory(penguin, MemoryModuleType.WALK_TARGET).getTarget() != this.targetPos)
            BrainUtils.setMemory(penguin, MemoryModuleType.WALK_TARGET, new WalkTarget(this.targetPos, 1.5F, 0));
    }
}
