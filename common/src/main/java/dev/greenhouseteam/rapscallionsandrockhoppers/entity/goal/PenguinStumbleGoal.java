package dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PenguinStumbleGoal extends Goal {
    private final Penguin penguin;
    private boolean isRunning;

    public PenguinStumbleGoal(Penguin penguin) {
        this.penguin = penguin;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        int randomChance = Mth.clamp(40 - (((this.penguin.tickCount - this.penguin.getPreviousStumbleTickCount()) / Math.max(1, this.penguin.getPreviousStumbleTickCount()) * 80) / 40), 5, 40);
        return !this.isRunning && !this.penguin.isInWaterOrBubble() && this.penguin.getDeltaMovement().horizontalDistanceSqr() > 1.0E-7 && this.penguin.tickCount > 400 + this.penguin.getPreviousStumbleTickCount() && this.penguin.getRandom().nextInt(randomChance) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.penguin.isStumbling() && this.isRunning;
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
        this.penguin.setStumbleTicksBeforeGettingUp(this.penguin.getRandom().nextIntBetweenInclusive(60, 120));
        this.penguin.setPreviousStumbleTickCount(this.penguin.tickCount);
        this.isRunning = true;
    }

    @Override
    public void stop() {
        this.penguin.setStumbleTicks(0);
        this.penguin.setStumbleTicksBeforeGettingUp(Integer.MIN_VALUE);
        this.penguin.setHasSlid(false);
        this.isRunning = false;
    }
}
