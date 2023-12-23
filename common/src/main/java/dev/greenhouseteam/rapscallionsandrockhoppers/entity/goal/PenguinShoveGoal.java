package dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.SyncXRotPacketS2C;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IPlatformHelper;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class PenguinShoveGoal extends Goal {
    private static final int RANDOM_CHANCE = 800;
    private static final int START_DELAY_REQUIREMENT = 2;

    private final Penguin penguin;
    private Penguin otherPenguin;
    private BlockPos lookPos;
    private int startDelay = 0;

    public PenguinShoveGoal(Penguin penguin) {
        this.penguin = penguin;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.penguin.isInWaterOrBubble() || this.penguin.isStumbling() || this.penguin.getRandom().nextInt(RANDOM_CHANCE) > 0 || this.penguin.isBaby()) {
            return false;
        }

        List<Penguin> penguins = this.penguin.level().getEntitiesOfClass(Penguin.class, this.penguin.getBoundingBox().inflate(1.5, 1.0, 1.5), EntitySelector.NO_SPECTATORS.and(entity -> entity.isAlive() && !((Penguin)entity).isBaby() && entity != this.penguin));
        Optional<Penguin> optionalPenguin = penguins.stream().min(Comparator.comparingDouble(value -> value.position().distanceTo(this.penguin.position())));

        if (optionalPenguin.isPresent() && !optionalPenguin.get().isInWaterOrBubble() && !optionalPenguin.get().isStumbling()) {
            this.otherPenguin = optionalPenguin.get();
        } else {
            return false;
        }

        Optional<BlockPos> xRot = BlockPos.betweenClosedStream(this.penguin.getBoundingBox().inflate(6.0, 3.0, 6.0)).filter(pos -> this.penguin.level().getFluidState(pos).is(FluidTags.WATER))
                .map(BlockPos::immutable).min(Comparator.comparing(pos -> pos.distManhattan(this.penguin.blockPosition())));

        if (xRot.isPresent()) {
            this.lookPos = xRot.get();
            return true;
        }

        return false;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.penguin.isStumbling() && !this.penguin.isInWaterOrBubble() && (this.startDelay != Integer.MIN_VALUE || this.penguin.getShoveTicks().isPresent() && this.penguin.getShoveTicks().getAsInt() < Penguin.SHOVE_ANIMATION_LENGTH);
    }

    @Override
    public void tick() {
        if (this.penguin.getShoveTicks().isPresent()) {
            this.penguin.incrementShoveTicks();
        } else if (this.startDelay >= START_DELAY_REQUIREMENT) {
            // Utilise start delay because the packet will run late if not.
            if (!this.penguin.isInWaterOrBubble() && !this.penguin.isStumbling() && !this.otherPenguin.isInWaterOrBubble() && !this.otherPenguin.isStumbling()) {
                this.penguin.incrementShoveTicks();
                this.otherPenguin.stumbleWithoutInitialAnimation();
            }
            this.startDelay = Integer.MIN_VALUE;
        }
        if (this.startDelay != Integer.MIN_VALUE) {
            this.startDelay++;
        }
    }

    @Override
    public void start() {
        this.penguin.lookAt(EntityAnchorArgument.Anchor.FEET, lookPos.getCenter());
        this.otherPenguin.lookAt(EntityAnchorArgument.Anchor.FEET, lookPos.getCenter());
        IPlatformHelper.INSTANCE.sendS2CTracking(new SyncXRotPacketS2C(this.penguin.getId(), this.otherPenguin.getId(), this.lookPos), this.penguin);
        this.startDelay = 0;
    }

    @Override
    public void stop() {
        this.otherPenguin = null;
        this.lookPos = null;
        this.penguin.setShoveTicks(OptionalInt.empty());
        this.startDelay = Integer.MIN_VALUE;
    }

}
