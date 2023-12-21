package dev.greenhouseteam.rapscallionsandrockhoppers.entity;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal.PenguinJumpGoal;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal.PenguinPanicGoal;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal.PenguinStrollGoal;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal.PenguinSwapBetweenWaterAndLandGoal;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal.PenguinStumbleGoal;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersEntityTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

public class Penguin extends Animal {
    public static final int STUMBLE_ANIMATION_LENGTH = 15;
    public static final int GET_UP_ANIMATION_LENGTH = 16;

    public static final String TAG_SHOCKED_TIME = "shocked_time";
    public static final String TAG_POINT_OF_INTEREST = "point_of_interest";
    private static final Ingredient FOOD_ITEMS = Ingredient.of(
            Items.INK_SAC, Items.GLOW_INK_SAC
    );
    private static final EntityDataAccessor<Integer> DATA_SHOCKED_TIME = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STUMBLE_TICKS = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<OptionalInt> DATA_STUMBLE_TICKS_BEFORE_GETTING_UP = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState waddleAnimationState = new AnimationState();
    public final AnimationState shockArmAnimationState = new AnimationState();
    public final AnimationState waddleExpandAnimationState = new AnimationState();
    public final AnimationState waddleRetractAnimationState = new AnimationState();
    public final AnimationState stumbleAnimationState = new AnimationState();
    public final AnimationState stumbleGroundAnimationState = new AnimationState();
    public final AnimationState stumbleFallingAnimationState = new AnimationState();
    public final AnimationState stumbleGetUpAnimationState = new AnimationState();
    public final AnimationState swimIdleAnimationState = new AnimationState();
    public final AnimationState swimAnimationState = new AnimationState();

    private boolean animationArmState = false;
    private boolean previousStumbleValue = false;
    private boolean hasSlid = false;
    private BlockPos pointOfInterest;
    private boolean previousWaterValue = false;
    private boolean previousWaterMovementValue = false;
    private int walkStartTime = Integer.MIN_VALUE;


    private PenguinStumbleGoal stumbleGoal;

    public Penguin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 80, 20, 2.0F, 1.0F, false);
        this.lookControl = new SmoothSwimmingLookControl(this, 20);
        this.setPathfindingMalus(BlockPathTypes.WATER, 4.0F);
    }

    @Override
    public void registerGoals() {
        this.stumbleGoal =  new PenguinStumbleGoal(this);
        this.goalSelector.addGoal(0, this.stumbleGoal);
        this.goalSelector.addGoal(0, new BreathAirGoal(this));
        this.goalSelector.addGoal(1, new PenguinPanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, FOOD_ITEMS, false));
        this.goalSelector.addGoal(4, new PenguinSwapBetweenWaterAndLandGoal(this));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 1.0F));
        this.goalSelector.addGoal(5, new PenguinJumpGoal(this, 120));
        this.goalSelector.addGoal(6, new PenguinStrollGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new AvoidEntityGoal<>(this, Pufferfish.class, 2.0F, 1.0, 1.0));
    }

    @Override
    public int getMaxAirSupply() {
        return 900;
    }

    @Override
    public void setAirSupply(int value) {
        super.setAirSupply(this.getMaxAirSupply());
    }

    @Override
    protected int increaseAirSupply(int value) {
        return super.increaseAirSupply(this.getMaxAirSupply());
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level);
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
        this.getEntityData().define(DATA_STUMBLE_TICKS_BEFORE_GETTING_UP, OptionalInt.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt(TAG_SHOCKED_TIME, this.getShockedTime());
        if (this.getPointOfInterest() != null) {
            compoundTag.put(TAG_POINT_OF_INTEREST, BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.getPointOfInterest()).getOrThrow(false, (s) -> RapscallionsAndRockhoppers.LOG.error("Failed to encode Penguin's Point of Interest: " + s)));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setShockedTime(compoundTag.getInt(TAG_SHOCKED_TIME));
        if (compoundTag.contains(TAG_POINT_OF_INTEREST)) {
            this.setPointOfInterest(BlockPos.CODEC.decode(NbtOps.INSTANCE, compoundTag.get(TAG_POINT_OF_INTEREST)).getOrThrow(false, (s) -> RapscallionsAndRockhoppers.LOG.error("Failed to decode Penguin's Point of Interest: " + s)).getFirst());
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isNoAi()) {
            this.setAirSupply(this.getMaxAirSupply());
            return;
        }

        if (!previousWaterValue && this.isInWaterOrBubble()) {
            Optional<BlockPos> optionalPos = this.level().getEntitiesOfClass(Penguin.class, this.getBoundingBox().inflate(20.0F, 10.0F, 20.0F)).stream().map(Penguin::getPointOfInterest).filter(Objects::nonNull).findFirst();

            if (optionalPos.isEmpty()) {
                optionalPos = Optional.of(this.blockPosition());
            }

            if (canSetNewPointOfInterest(optionalPos.get())) {
                this.setPointOfInterest(optionalPos.get());
            }
            this.previousWaterValue = true;

            this.setPose(Pose.SWIMMING);
        } else if (previousWaterValue && !this.isInWaterOrBubble() && this.onGround()) {
            Optional<BlockPos> optionalPos = this.level().getEntitiesOfClass(Penguin.class, this.getBoundingBox().inflate(20.0F, 10.0F, 20.0F)).stream().map(Penguin::getPointOfInterest).filter(Objects::nonNull).findFirst();

            if (optionalPos.isEmpty()) {
                optionalPos = Optional.of(this.blockPosition());
            }

            if (canSetNewPointOfInterest(optionalPos.get())) {
                this.setPointOfInterest(optionalPos.get());
            }
            this.previousWaterValue = false;
            this.setPose(Pose.STANDING);
        }

        if (this.pointOfInterest == null && this.tickCount % 400 == 0) {
            Optional<BlockPos> optionalPos = this.level().getEntitiesOfClass(Penguin.class, this.getBoundingBox().inflate(20.0F, 10.0F, 20.0F)).stream().map(Penguin::getPointOfInterest).filter(Objects::nonNull).findFirst();

            if (optionalPos.isEmpty()) {
                optionalPos = BlockPos.betweenClosedStream(
                        this.blockPosition().getX() - 24, this.blockPosition().getY() - 12, this.blockPosition().getZ() - 24,
                        this.blockPosition().getX() + 24, this.blockPosition().getY() + 12, this.blockPosition().getZ() + 24
                ).filter(pos -> this.level().getFluidState(pos).is(FluidTags.WATER)).map(BlockPos::immutable).min(Comparator.comparing(pos -> pos.distManhattan(this.blockPosition())));
            }

            optionalPos.ifPresent(this::setPointOfInterest);
        }

        if (!this.level().isClientSide()) {
            if (this.getShockedTime() > 0) {
                this.setShockedTime(this.getShockedTime() - 1);
            }

            if (!this.isInWaterOrBubble()) {
                if ((this.getDeltaMovement().horizontalDistanceSqr() > 0.0F || this.getDeltaMovement().y() > 0.0) && !this.isStumbling()) {
                    if (this.getWalkStartTime() == Integer.MIN_VALUE) {
                        this.setWalkStartTime(this.tickCount);
                    }
                } else {
                    if (this.getWalkStartTime() != Integer.MIN_VALUE) {
                        this.setWalkStartTime(Integer.MIN_VALUE);
                    }
                }
            }
        } else {
            if (this.getPose() == Pose.SWIMMING) {
                this.stopAllLandAnimations();
                this.swimIdleAnimationState.animateWhen(!this.walkAnimation.isMoving(), this.tickCount);
                this.swimAnimationState.animateWhen(this.walkAnimation.isMoving(), this.tickCount);
            } else {
                this.stopAllWaterAnimations();
                this.idleAnimationState.animateWhen(!this.walkAnimation.isMoving() && !this.isStumbling(), this.tickCount);
                this.waddleAnimationState.animateWhen(this.walkAnimation.isMoving() && !this.isStumbling(), this.tickCount);
                this.shockArmAnimationState.animateWhen(this.isShocked() && !this.isStumbling() && !this.isDeadOrDying(), this.tickCount);

                if (this.isStumbling()) {
                    this.stumbleFallingAnimationState.animateWhen(this.getDeltaMovement().y() < -0.1, this.tickCount);

                    if (this.getStumbleTicksBeforeGettingUp().isPresent()) {
                        if (this.getStumbleTicks() > STUMBLE_ANIMATION_LENGTH) {
                            this.stumbleAnimationState.stop();
                            this.stumbleGroundAnimationState.animateWhen(this.getStumbleTicks() <= this.getStumbleTicksBeforeGettingUp().getAsInt(), this.tickCount);
                            this.stumbleGetUpAnimationState.animateWhen(this.getStumbleTicks() > this.getStumbleTicksBeforeGettingUp().getAsInt(), this.tickCount);
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
        this.refreshDimensionsIfShould();
    }

    public void refreshDimensionsIfShould() {
        if (this.isInWaterOrBubble() && (this.previousWaterMovementValue ^ this.getDeltaMovement().horizontalDistanceSqr() > 0.005F)) {
            this.previousWaterMovementValue = !this.previousWaterMovementValue;
            this.refreshDimensions();
        } else if (this.isStumbling() && this.getStumbleTicksBeforeGettingUp().isPresent() && (this.getStumbleTicks() == STUMBLE_ANIMATION_LENGTH + 2 || this.getStumbleTicks() == this.getStumbleTicksBeforeGettingUp().getAsInt() + 5)) {
            this.refreshDimensions();
        }
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isControlledByLocalInstance() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), vec3);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel(vec3);
        }
    }

    private void stopAllLandAnimations() {
        this.idleAnimationState.stop();
        this.waddleAnimationState.stop();
        this.waddleExpandAnimationState.stop();
        this.waddleRetractAnimationState.stop();
        this.shockArmAnimationState.stop();
        this.stumbleAnimationState.stop();
        this.stumbleFallingAnimationState.stop();
        this.stumbleGroundAnimationState.stop();
        this.stumbleGetUpAnimationState.stop();
    }

    private void stopAllWaterAnimations() {
        this.swimIdleAnimationState.stop();
        this.swimAnimationState.stop();
    }

    private boolean canSetNewPointOfInterest(BlockPos pos) {
        if (this.pointOfInterest == null) {
            return true;
        }

        return this.pointOfInterest.distManhattan(pos) > 24.0F;
    }

    @Override
    public void push(Entity entity) {
        if (!this.isPassengerOfSameVehicle(entity) && entity.isSprinting() && !entity.noPhysics && !this.noPhysics && !this.isVehicle() && this.isPushable() && (this.getStumbleTicksBeforeGettingUp().isEmpty() || this.getStumbleTicks() < STUMBLE_ANIMATION_LENGTH)) {
            double xPos = entity.getX() - this.getX();
            double zPos = entity.getZ() - this.getZ();
            double max = Mth.absMax(xPos, zPos);
            if (max >= 0.01F) {
                this.setYRot(entity.getYRot());
                if (!this.level().isClientSide()) {
                    this.stumbleGoal.startWithoutStumbleTicks();
                }

                max = Math.sqrt(max);
                xPos /= max;
                zPos /= max;
                double modifier = 1.0 / max;
                if (modifier > 1.0) {
                    modifier = 1.0;
                }

                xPos *= modifier;
                zPos *= modifier;
                xPos *= 0.05F;
                zPos *= 0.05F;
                if (!entity.isVehicle() && entity.isPushable()) {
                    entity.push(xPos, 0.0, zPos);
                }
            }
        } else {
            super.push(entity);
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (this.getStumbleTicksBeforeGettingUp().isPresent() && this.getStumbleTicks() >= STUMBLE_ANIMATION_LENGTH + 2 && this.getStumbleTicks() < this.getStumbleTicksBeforeGettingUp().getAsInt() + 5) {
            return super.getDimensions(pose).scale(1.33F, 0.5F);
        } else if (this.getPose() == Pose.SWIMMING && this.getDeltaMovement().horizontalDistanceSqr() > 0.005F) {
            return super.getDimensions(pose).scale(1.0F, 0.5F);
        }
        return super.getDimensions(pose);
    }

    @Override
    public boolean isWithinRestriction(BlockPos pos) {
        if (this.getRestrictRadius() == -1.0F) {
            if (this.getPointOfInterest() != null) {
                return this.getPointOfInterest().distSqr(pos) < (32 * 32);
            }
            return true;
        } else {
            return this.getRestrictCenter().distSqr(pos) < (double)(this.getRestrictRadius() * this.getRestrictRadius());
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

    public void setStumbleTicksBeforeGettingUp(OptionalInt ticksBeforeGettingUp) {
        this.getEntityData().set(DATA_STUMBLE_TICKS_BEFORE_GETTING_UP, ticksBeforeGettingUp);
    }

    public OptionalInt getStumbleTicksBeforeGettingUp() {
        return this.getEntityData().get(DATA_STUMBLE_TICKS_BEFORE_GETTING_UP);
    }

    public boolean isStumbling() {
        return this.getStumbleTicksBeforeGettingUp().isPresent() && this.getStumbleTicksBeforeGettingUp().getAsInt() + GET_UP_ANIMATION_LENGTH > this.getStumbleTicks();
    }

    public boolean isStumblingAndNotGettingUp() {
        return this.getStumbleTicksBeforeGettingUp().isPresent() && this.getStumbleTicksBeforeGettingUp().getAsInt() > this.getStumbleTicks();
    }

    public void setWalkStartTime(int walkStartTime) {
        this.walkStartTime = walkStartTime;
    }

    public int getWalkStartTime() {
        return this.walkStartTime;
    }

    public void setHasSlid(boolean slideValue) {
        this.hasSlid = slideValue;
    }

    public boolean getHasSlid() {
        return this.hasSlid;
    }

    public void setPointOfInterest(BlockPos point) {
        this.pointOfInterest = point;
    }

    public BlockPos getPointOfInterest() {
        return this.pointOfInterest;
    }
}
