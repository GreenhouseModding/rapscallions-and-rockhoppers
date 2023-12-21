package dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.OptionalInt;

public class PenguinStumbleGoal extends Goal {
    private static final int STUMBLE_CHANCE = 60;
    private static final int REQUIRED_WALKING_TIME = 80;

    private final Penguin penguin;
    private boolean isRunning = false;

    public PenguinStumbleGoal(Penguin penguin) {
        this.penguin = penguin;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.isRunning || !this.penguin.isInWaterOrBubble() && this.penguin.getWalkStartTime() != Integer.MIN_VALUE && this.penguin.tickCount > REQUIRED_WALKING_TIME + this.penguin.getWalkStartTime() && this.penguin.getRandom().nextInt(STUMBLE_CHANCE) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.penguin.isStumbling();
    }

    @Override
    public void tick() {
        this.penguin.getNavigation().stop();
        if (this.penguin.getStumbleTicks() > Penguin.STUMBLE_ANIMATION_LENGTH) {
            if (!this.penguin.getHasSlid()) {
                float i = (float) (Math.PI / 180.0F);
                float x = -Mth.sin(this.penguin.getYRot() * i) * Mth.cos(this.penguin.getXRot() * i);
                float z =  Mth.cos(this.penguin.getYRot() * i) * Mth.cos(this.penguin.getXRot() * i);
                this.penguin.addDeltaMovement(new Vec3(x, 0, z).normalize().multiply(0.6, 0.0, 0.6));
                this.penguin.hurtMarked = true;
                this.penguin.setHasSlid(true);
            }
        }
        this.penguin.setStumbleTicks(this.penguin.getStumbleTicks() + 1);
    }

    @Override
    public void start() {
        this.penguin.setStumbleTicks(0);
        this.penguin.setStumbleTicksBeforeGettingUp(OptionalInt.of(this.penguin.getRandom().nextIntBetweenInclusive(30, 60)));
        this.isRunning = true;
    }

    @Override
    public void stop() {
        this.penguin.setStumbleTicks(0);
        this.penguin.setStumbleTicksBeforeGettingUp(OptionalInt.empty());
        this.penguin.setHasSlid(false);
        this.isRunning = false;
    }

    public void startWithoutStumbleTicks() {
        this.start();
        this.penguin.setStumbleTicks(Penguin.STUMBLE_ANIMATION_LENGTH + 1);
    }
}
