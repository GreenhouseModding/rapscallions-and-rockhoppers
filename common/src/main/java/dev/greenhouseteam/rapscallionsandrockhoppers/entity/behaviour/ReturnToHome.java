package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class ReturnToHome extends ExtendedBehaviour<Penguin> {
    private int radius = 0;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT));
    }

    public ReturnToHome setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Penguin penguin) {
        return BrainUtils.hasMemory(penguin, MemoryModuleType.HOME) && BrainUtils.getMemory(penguin, MemoryModuleType.HOME).dimension() == penguin.level().dimension() && penguin.blockPosition().distSqr(BrainUtils.getMemory(penguin, MemoryModuleType.HOME).pos()) > this.radius * this.radius;
    }

    protected void start(Penguin penguin) {
        BrainUtils.setMemory(penguin, MemoryModuleType.WALK_TARGET, new WalkTarget(BrainUtils.getMemory(penguin, MemoryModuleType.HOME).pos(), 1.0F, 2));
    }

}
