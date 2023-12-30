package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersBlocks;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class PenguinSitEgg extends ExtendedBehaviour<Penguin> {
    private int radius = 0;
    private int eggCrackTime = 6000;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(RockhoppersMemoryModuleTypes.EGG_POS, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected void tick(ServerLevel level, Penguin penguin, long gameTime) {
        super.tick(level, penguin, gameTime);
        BlockPos eggPos = BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.EGG_POS);
        assert eggPos != null;
        if (!level.getBlockState(eggPos).is(RockhoppersBlocks.PENGUIN_EGG)) {
            // This checks if the pos is no longer an egg, if so, clears the memory.
            BrainUtils.clearMemory(penguin, RockhoppersMemoryModuleTypes.EGG_POS);
            return;
        }
        if (penguin.distanceToSqr(eggPos.getX(), eggPos.getY(), eggPos.getZ()) < 1.2) {
            level.getPlayers((player) -> true).get(0).sendSystemMessage(Component.literal("Penguin has sat on egg"));
        }
    }

    public PenguinSitEgg setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    @Override
    protected void stop(Penguin entity) {
        super.stop(entity);
        if (entity.level() instanceof ServerLevel level) level.getPlayers((player) -> true).get(0).sendSystemMessage(Component.literal("Stopped penguin sit egg behaviour"));
    }

    protected void start(Penguin penguin) {
        if (penguin.level() instanceof ServerLevel level) level.getPlayers((player) -> true).get(0).sendSystemMessage(Component.literal("Started penguin sit egg behaviour"));
        eggCrackTime = 6000;
        BrainUtils.setMemory(penguin, MemoryModuleType.WALK_TARGET, new WalkTarget(BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.EGG_POS), 1.0F, 2));
    }

}
