package dev.greenhouseteam.rapscallionsandrockhoppers.entity;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal.PenguinPanicGoal;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal.PenguinStumbleGoal;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersEntityTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class Penguin extends Animal {
    public static final int STUMBLE_ANIMATION_LENGTH = 15;
    public static final int GET_UP_ANIMATION_LENGTH = 20;
    private static final Ingredient FOOD_ITEMS = Ingredient.of(
            Items.INK_SAC, Items.GLOW_INK_SAC
    );
    private static final EntityDataAccessor<Integer> DATA_SHOCKED_TIME = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STUMBLE_TICKS = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STUMBLE_TICKS_BEFORE_GETTING_UP = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState waddleAnimationState = new AnimationState();
    public final AnimationState shockArmAnimationState = new AnimationState();
    public final AnimationState waddleExpandAnimationState = new AnimationState();
    public final AnimationState waddleRetractAnimationState = new AnimationState();
    public final AnimationState stumbleAnimationState = new AnimationState();
    public final AnimationState stumbleGroundAnimationState = new AnimationState();
    public final AnimationState stumbleFallingAnimationState = new AnimationState();
    public final AnimationState stumbleGetUpAnimationState = new AnimationState();

    private boolean animationArmState = false;
    private boolean previousStumbleValue = false;
    private boolean hasSlid = false;
    private int previousStumbleTickCount = 0;

    public Penguin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(0, new PenguinStumbleGoal(this));
        this.goalSelector.addGoal(1, new PenguinPanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, FOOD_ITEMS, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.2));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageableMob) {
        return RapscallionsAndRockhoppersEntityTypes.PENGUIN.create(level);
    }

    @Override
    public SoundEvent getAmbientSound() {
        if (this.isInWaterOrBubble()) {
            return null;
        }
        return RapscallionsAndRockhoppersSoundEvents.PENGUIN_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSource) {
        return RapscallionsAndRockhoppersSoundEvents.PENGUIN_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return RapscallionsAndRockhoppersSoundEvents.PENGUIN_DEATH;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getBlockState(pos.below()).is(Blocks.STONE) || level.getFluidState(pos).is(FluidTags.WATER) ? 10.0F : level.getPathfindingCostFromLightLevels(pos);
    }

    @Override
    public int getExperienceReward() {
        return 0;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return FOOD_ITEMS.test(stack);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_SHOCKED_TIME, 0);
        this.getEntityData().define(DATA_STUMBLE_TICKS, 0);
        this.getEntityData().define(DATA_STUMBLE_TICKS_BEFORE_GETTING_UP, Integer.MIN_VALUE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("ShockedTime", this.getShockedTime());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setShockedTime(compoundTag.getInt("ShockedTime"));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            if (this.getShockedTime() > 0) {
                this.setShockedTime(this.getShockedTime() - 1);
            }
        } else {
            if (this.isInWaterOrBubble()) {
                this.idleAnimationState.stop();
                this.waddleAnimationState.stop();
                this.waddleExpandAnimationState.stop();
                this.waddleRetractAnimationState.stop();
                this.shockArmAnimationState.stop();
            } else {
                this.idleAnimationState.animateWhen(!this.walkAnimation.isMoving() && !this.isStumbling(), this.tickCount);
                this.waddleAnimationState.animateWhen(this.walkAnimation.isMoving() && !this.isStumbling(), this.tickCount);
                this.shockArmAnimationState.animateWhen(this.isShocked() && !this.isStumbling() && !this.isDeadOrDying(), this.tickCount);

                if (this.isStumbling()) {
                    this.stumbleFallingAnimationState.animateWhen(this.getDeltaMovement().y() < -0.1, this.tickCount);

                    if (this.getStumbleTicksBeforeGettingUp() != Integer.MIN_VALUE) {
                        if (this.getStumbleTicks() > STUMBLE_ANIMATION_LENGTH) {
                            this.stumbleAnimationState.stop();
                            this.stumbleGroundAnimationState.animateWhen(this.getStumbleTicks() <= this.getStumbleTicksBeforeGettingUp(), this.tickCount);
                            this.stumbleGetUpAnimationState.animateWhen(this.getStumbleTicks() > this.getStumbleTicksBeforeGettingUp(), this.tickCount);
                        } else if (!this.previousStumbleValue) {
                            this.stumbleAnimationState.start(this.tickCount);
                            this.waddleRetractAnimationState.stop();
                            this.waddleExpandAnimationState.stop();
                            this.animationArmState = false;
                            this.previousStumbleValue = true;
                        }
                    }
                } else if (!this.isStumbling() && this.previousStumbleValue) {
                    this.stumbleAnimationState.stop();
                    this.stumbleGroundAnimationState.stop();
                    this.stumbleGetUpAnimationState.stop();
                    this.stumbleFallingAnimationState.stop();
                    this.previousStumbleValue = false;
                } else {
                    if (!this.animationArmState && this.walkAnimation.isMoving()) {
                        this.waddleRetractAnimationState.stop();
                        this.waddleExpandAnimationState.startIfStopped(this.tickCount);
                        this.animationArmState = true;
                    } else if (this.animationArmState && !this.walkAnimation.isMoving()) {
                        this.waddleExpandAnimationState.stop();
                        this.waddleRetractAnimationState.startIfStopped(this.tickCount);
                        this.animationArmState = false;
                    }
                }

            }
        }
    }

    @Override
    protected void actuallyHurt(DamageSource damageSource, float amount) {
        super.actuallyHurt(damageSource, amount);
        this.setShockedTime(this.random.nextInt(60, 120));
    }

    public void setShockedTime(int shockedTime) {
        this.animationArmState = true;
        this.getEntityData().set(DATA_SHOCKED_TIME, shockedTime);
    }

    public int getShockedTime() {
        return this.getEntityData().get(DATA_SHOCKED_TIME);
    }

    public boolean isShocked() {
        return this.getShockedTime() > 0;
    }

    public void setStumbleTicks(int stumbleTime) {
        this.getEntityData().set(DATA_STUMBLE_TICKS, stumbleTime);
    }

    public int getStumbleTicks() {
        return this.getEntityData().get(DATA_STUMBLE_TICKS);
    }

    public void setStumbleTicksBeforeGettingUp(int ticksBeforeGettingUp) {
        this.getEntityData().set(DATA_STUMBLE_TICKS_BEFORE_GETTING_UP, ticksBeforeGettingUp);
    }

    public int getStumbleTicksBeforeGettingUp() {
        return this.getEntityData().get(DATA_STUMBLE_TICKS_BEFORE_GETTING_UP);
    }

    public boolean isStumbling() {
        return this.getStumbleTicksBeforeGettingUp() != Integer.MIN_VALUE && this.getStumbleTicksBeforeGettingUp() + GET_UP_ANIMATION_LENGTH > this.getStumbleTicks();
    }

    public void setPreviousStumbleTickCount(int stumbleTime) {
        this.previousStumbleTickCount = stumbleTime;
    }

    public int getPreviousStumbleTickCount() {
        return this.previousStumbleTickCount;
    }

    public void setHasSlid(boolean slideValue) {
        this.hasSlid = slideValue;
    }

    public boolean getHasSlid() {
        return this.hasSlid;
    }
}
