package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class PenguinStumble extends ExtendedBehaviour<Penguin> {
    private boolean hasSlid = false;

    public PenguinStumble() {
        this.runFor(Penguin::getTotalStumbleAnimationLength);
    }

    @Override
    public boolean checkExtraStartConditions(ServerLevel level, Penguin penguin) {
        boolean bl = penguin.isStumbling() || BrainUtils.hasMemory(penguin, MemoryModuleType.WALK_TARGET) && penguin.getRandom().nextFloat() < Mth.clamp(penguin.getStumbleChance(), 0.0F, 1.0F);
        // This has to be done here rather than the start method.
        if (bl && !penguin.isStumbling()) {
            penguin.setStumbleTicks(0);
            penguin.setStumbleTicksBeforeGettingUp(penguin.getRandom().nextIntBetweenInclusive(30, 60));
        }
        return bl;
    }

    @Override
    protected boolean shouldKeepRunning(Penguin penguin) {
        return penguin.getStumbleTicks() <= penguin.getTotalStumbleAnimationLength();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    public void tick(Penguin penguin) {
        if (penguin.getStumbleTicks() > Penguin.STUMBLE_ANIMATION_LENGTH && !this.hasSlid) {
            float i = Mth.PI / 180.0F;
            float x = -Mth.sin(penguin.getYRot() * i) * Mth.cos(penguin.getXRot() * i);
            float z = Mth.cos(penguin.getYRot() * i) * Mth.cos(penguin.getXRot() * i);
            penguin.addDeltaMovement(new Vec3(x, 0, z).normalize().multiply(0.4, 0.0, 0.4));
            penguin.hurtMarked = true;
            this.hasSlid = true;
        }
        penguin.setStumbleTicks(penguin.getStumbleTicks() + 1);
    }

    @Override
    public void start(Penguin penguin) {
        BrainUtils.clearMemories(penguin, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET);
    }

    @Override
    public void stop(Penguin penguin) {
        penguin.setStumbleTicks(Integer.MIN_VALUE);
        penguin.setStumbleTicksBeforeGettingUp(Integer.MIN_VALUE);
        this.hasSlid = false;
    }
}
