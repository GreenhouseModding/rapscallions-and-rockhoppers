package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.SyncBlockPosLookPacketS2C;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class PenguinShove extends ExtendedBehaviour<Penguin> {
    private Penguin shoveTarget;
    private Vec3 lookPos;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(RockhoppersMemoryModuleTypes.NEAREST_WATER, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Penguin penguin) {
        if (penguin.isInWaterOrBubble() || penguin.isStumbling() || penguin.isBaby() || penguin.getShoveTicks() != Integer.MIN_VALUE || BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.NEAREST_WATER).distManhattan(penguin.blockPosition()) > 4) {
            return false;
        }

        if (penguin.getRandom().nextFloat() < Mth.clamp(penguin.getShoveChance(), 0.0F, 1.0F)) {
            Penguin shoveTarget = BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.NEAREST_VISIBLE_SHOVEABLE);

            if (shoveTarget != null && shoveTarget.distanceTo(penguin) < 1.5)
                this.shoveTarget = shoveTarget;
            else
                return false;

            BlockPos lookPos = BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.NEAREST_WATER);
            if (lookPos != null) {
                this.lookPos = lookPos.getCenter();
                return true;
            }

            this.shoveTarget = null;
            this.lookPos = null;
        }
        return false;
    }

    @Override
    public boolean shouldKeepRunning(Penguin penguin) {
        return !penguin.isStumbling() && !penguin.isInWaterOrBubble() && penguin.getShoveTicks() != Integer.MIN_VALUE;
    }

    @Override
    public void start(Penguin penguin) {
        penguin.lookAt(EntityAnchorArgument.Anchor.FEET, this.lookPos);
        this.shoveTarget.lookAt(EntityAnchorArgument.Anchor.FEET, this.lookPos);

        penguin.setShoveTicks(Penguin.SHOVE_ANIMATION_LENGTH);
        this.shoveTarget.stumbleWithoutInitialAnimation();

        IRockhoppersPlatformHelper.INSTANCE.sendS2CTracking(new SyncBlockPosLookPacketS2C(penguin.getId(), this.shoveTarget.getId(), this.lookPos), penguin);
    }

    @Override
    public void stop(Penguin penguin) {
        this.shoveTarget = null;
        this.lookPos = null;
    }

}
