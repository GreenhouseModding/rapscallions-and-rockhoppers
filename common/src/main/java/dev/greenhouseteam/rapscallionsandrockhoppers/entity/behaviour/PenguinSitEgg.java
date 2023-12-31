package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.block.PenguinEggBlock;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersBlocks;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class PenguinSitEgg extends ExtendedBehaviour<Penguin> {
    public static final int EGG_CRACK_TIME = 6000; // 5 minutes
    private int eggCrackTime = EGG_CRACK_TIME;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(RockhoppersMemoryModuleTypes.EGG_POS, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected void tick(ServerLevel level, Penguin penguin, long gameTime) {
        super.tick(level, penguin, gameTime);
        BlockPos eggPos = BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.EGG_POS);
        assert eggPos != null;
        if (!level.getBlockState(eggPos).is(RockhoppersBlocks.PENGUIN_EGG)) {
            // This checks if the pos is no longer an egg, if so, clears the memory.
            BrainUtils.clearMemory(penguin, RockhoppersMemoryModuleTypes.EGG_POS);
            BrainUtils.clearMemory(penguin, MemoryModuleType.WALK_TARGET);
            return;
        }
        if (penguin.distanceToSqr(eggPos.getX(), eggPos.getY(), eggPos.getZ()) < 1.2) {
            BrainUtils.setMemory(penguin, MemoryModuleType.WALK_TARGET, new WalkTarget(eggPos, 1.0F, 1));
            eggCrackTime--;
            if (eggCrackTime <= 0) {
                PenguinEggBlock.crackEgg(level.getBlockState(eggPos), level, eggPos);
                eggCrackTime = EGG_CRACK_TIME;
            }
        }
    }

    @Override
    protected boolean shouldKeepRunning(Penguin entity) {
        return BrainUtils.hasMemory(entity, RockhoppersMemoryModuleTypes.EGG_POS);
    }

    protected void start(Penguin penguin) {
        eggCrackTime = EGG_CRACK_TIME;
        BrainUtils.setMemory(penguin, MemoryModuleType.WALK_TARGET, new WalkTarget(BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.EGG_POS), 1.0F, 1));
    }

}
