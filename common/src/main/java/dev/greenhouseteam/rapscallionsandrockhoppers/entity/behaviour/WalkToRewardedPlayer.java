package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.EntityGetUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.UUID;

public class WalkToRewardedPlayer extends ExtendedBehaviour<Penguin> {
    private Vec3 runPos = null;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Penguin penguin) {
        UUID playerUuid = BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR);
        Entity rewardedPlayer = EntityGetUtil.getEntityFromUuid(level, playerUuid);
        if (playerUuid == null || rewardedPlayer == null) {
            return false;
        }
        double distToTarget = penguin.distanceToSqr(rewardedPlayer);
        if (distToTarget < 2 * 2) {
            return false;
        }

        Vec3 runPos = DefaultRandomPos.getPosTowards(penguin, 16, 8, rewardedPlayer.position(), Mth.HALF_PI);

        if (runPos == null || rewardedPlayer.distanceToSqr(runPos) > distToTarget)
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