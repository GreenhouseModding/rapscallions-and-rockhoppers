package dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.OptionalInt;

public class PenguinStumbleGoal extends Goal {
    private static final int STUMBLE_CHANCE = 60;
    private static final int REQUIRED_WALKING_TIME = 20;

    private final Penguin penguin;
    private boolean isRunning = false;

    public PenguinStumbleGoal(Penguin penguin) {
        this.penguin = penguin;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return !this.penguin.isInWaterOrBubble() && (this.isRunning || this.penguin.getWalkStartTime() != Integer.MIN_VALUE && this.penguin.tickCount > REQUIRED_WALKING_TIME + this.penguin.getWalkStartTime() && this.penguin.getRandom().nextInt(STUMBLE_CHANCE) == 0);
    }

    @Override
    public boolean canContinueToUse() {
        return this.penguin.isStumbling() && !this.penguin.isInWaterOrBubble();
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void tick() {
        this.penguin.getNavigation().stop();
        if (this.penguin.getStumbleTicks() > Penguin.STUMBLE_ANIMATION_LENGTH && !this.penguin.getHasSlid()) {
            float i = Mth.PI / 180.0F;
            float x = -Mth.sin(this.penguin.getYRot() * i) * Mth.cos(this.penguin.getXRot() * i);
            float z = Mth.cos(this.penguin.getYRot() * i) * Mth.cos(this.penguin.getXRot() * i);
            this.penguin.addDeltaMovement(new Vec3(x, 0, z).normalize().multiply(0.6, 0.0, 0.6));
            this.penguin.hurtMarked = true;
            this.penguin.setHasSlid(true);
        }
        this.penguin.setStumbleTicks(this.penguin.getStumbleTicks() + 1);
    }

    @Override
    public void start() {
        if (!this.isRunning) {
            this.penguin.setStumbleTicks(0);
            this.penguin.setStumbleTicksBeforeGettingUp(OptionalInt.of(this.penguin.getRandom().nextIntBetweenInclusive(30, 60)));
            this.isRunning = true;
        }
    }

    @Override
    public void stop() {
        this.penguin.setStumbleTicks(0);
        this.penguin.setStumbleTicksBeforeGettingUp(OptionalInt.empty());
        this.penguin.setHasSlid(false);
        this.isRunning = false;
    }

    public void startWithoutInitialAnimation() {
        this.penguin.setStumbleTicks(Penguin.STUMBLE_ANIMATION_LENGTH);
        this.penguin.setStumbleTicksBeforeGettingUp(OptionalInt.of(this.penguin.getRandom().nextIntBetweenInclusive(30, 60)));
        this.isRunning = true;
    }
}
