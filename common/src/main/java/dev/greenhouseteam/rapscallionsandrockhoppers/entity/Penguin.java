package dev.greenhouseteam.rapscallionsandrockhoppers.entity;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour.PenguinJump;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour.PenguinShove;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour.PenguinStumble;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour.ReturnToHome;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour.SetRandomSwimTarget;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.sensor.NearbyPufferfishSensor;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.sensor.NearbyShoveableSensor;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.sensor.NearbyWaterSensor;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.sensor.PenguinHomeSensor;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.sensor.TickCooldown;
import dev.greenhouseteam.rapscallionsandrockhoppers.mixin.LevelAccessor;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.InvalidateCachedPenguinTypePacket;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersBlocks;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersEntityTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersTags;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersSoundEvents;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.WeightedHolderSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.BreedWithPartner;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Panic;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowTemptation;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToBlock;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyBlocksSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.InWaterSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.ItemTemptingSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyAdultSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Penguin extends Animal implements SmartBrainOwner<Penguin> {
    public static final int STUMBLE_ANIMATION_LENGTH = 30;
    private static final int SWIM_EASE_OUT_ANIMATION_LENGTH = 40;
    public static final int GET_UP_ANIMATION_LENGTH = 60;
    public static final int SHOVE_ANIMATION_LENGTH = 40;

    protected static final Ingredient TEMPTATION_ITEM = Ingredient.of(RockhoppersTags.ItemTags.PENGUIN_TEMPT_ITEMS);
    protected static final Ingredient BREED_ITEM = Ingredient.of(RockhoppersTags.ItemTags.PENGUIN_BREED_ITEMS);
    private static final EntityDataAccessor<String> DATA_TYPE = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_PREVIOUS_TYPE = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_SHOCKED_TIME = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STUMBLE_TICKS = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STUMBLE_TICKS_BEFORE_GETTING_UP = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SHOVE_TICKS = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_STUMBLE_CHANCE = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_SHOVE_CHANCE = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.FLOAT);

    private boolean hasLoggedMissingError = false;
    private PenguinType cachedPenguinType = null;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState waddleAnimationState = new AnimationState();
    public final AnimationState shockArmAnimationState = new AnimationState();
    public final AnimationState waddleArmEaseInAnimationState = new AnimationState();
    public final AnimationState waddleArmEaseOutAnimationState = new AnimationState();
    public final AnimationState stumbleAnimationState = new AnimationState();
    public final AnimationState stumbleGroundAnimationState = new AnimationState();
    public final AnimationState stumbleFallingAnimationState = new AnimationState();
    public final AnimationState stumbleGetUpAnimationState = new AnimationState();
    public final AnimationState swimIdleAnimationState = new AnimationState();
    public final AnimationState swimAnimationState = new AnimationState();
    public final AnimationState swimEaseInAnimationState = new AnimationState();
    public final AnimationState swimEaseOutAnimationState = new AnimationState();
    public final AnimationState shoveAnimationState = new AnimationState();

    private boolean animationArmState = false;
    private boolean animationSwimState = false;
    private long stopEaseOutAnimAt = Long.MIN_VALUE;
    private boolean previousStumbleValue = false;
    private boolean previousWaterValue = false;
    private boolean previousWaterMovementValue = false;
    private int walkStartTime = Integer.MIN_VALUE;

    public Penguin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new PenguinMoveControl(this);
        this.lookControl = new PenguinLookControl(this);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, -1.0F);
        this.setMaxUpStep(1.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putFloat("stumble_chance", this.getStumbleChance());
        compoundTag.putFloat("shove_chance", this.getShoveChance());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setStumbleChance(compoundTag.getFloat("stumble_chance"));
        this.setShoveChance(compoundTag.getFloat("shove_chance"));
        if (compoundTag.contains("type")) {
            ResourceLocation typeKey = new ResourceLocation(compoundTag.getString("type"));
            this.setPenguinType(typeKey);
        } else {
            try {
                this.setPenguinType(getPenguinSpawnTypeGlobal(this.level(), this.blockPosition(), this.getRandom()));
            } catch (Exception ex) {
                RapscallionsAndRockhoppers.LOG.error("Could not load random PenguinType for penguin with UUID '{}'.", this.getStringUUID());
            }
        }

        if (compoundTag.contains("previous_type"))
            this.getEntityData().set(DATA_PREVIOUS_TYPE, compoundTag.getString("previous_type"));
    }

    @Override @NotNull
    protected Brain.Provider<Penguin> brainProvider() {
        return new SmartBrainProvider<>(this, true, false);
    }

    @Override
    public List<ExtendedSensor<Penguin>> getSensors() {
        return ObjectArrayList.of(
                new PenguinHomeSensor(),
                new NearbyBlocksSensor<Penguin>().setRadius(12.0F).setPredicate((blockState, penguin) -> true),
                new NearbyLivingEntitySensor<Penguin>().setRadius(16.0F),
                new NearbyPlayersSensor<Penguin>().setRadius(16.0F),
                new NearbyAdultSensor<>(),
                new TickCooldown(RockhoppersMemoryModuleTypes.WATER_JUMP_COOLDOWN_TICKS).setPredicate((integer, penguin) -> penguin.isInWater()),
                new NearbyWaterSensor().setXZRadius(4).setYRadius(4),
                new NearbyPufferfishSensor(),
                new ItemTemptingSensor<Penguin>().temptedWith((entity, stack) -> TEMPTATION_ITEM.test(stack)),
                new InWaterSensor<>(),
                new HurtBySensor<>(),
                new NearbyShoveableSensor()
        );
    }

    @Override
    public BrainActivityGroup<Penguin> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<Penguin> getIdleTasks() {
        return BrainActivityGroup.<Penguin>idleTasks(
                new Panic<>().speedMod(o -> 2.5F).panicIf((mob, damageSource) -> mob.isFreezing() || mob.isOnFire() || damageSource.getEntity() instanceof LivingEntity || this.isShocked()),
                new BreedWithPartner<>(),
                new SetPlayerLookTarget<>(),
                new SetRandomLookTarget<>().lookChance(ConstantFloat.of(0.6F)),
                new PenguinStumble(),
                new PenguinShove(),
                new FirstApplicableBehaviour<>(
                        new FollowTemptation<>(),
                        new ReturnToHome().setRadius(6).startCondition(penguin -> !penguin.isLeashed()),
                        new OneRandomBehaviour<>(
                                Pair.of(createBoundWalkTarget(4, 3, 6).avoidWaterWhen(penguin -> penguin.getRandom().nextFloat() < 0.98F), 14),
                                Pair.of(new Idle<>().runFor(entity -> entity.getRandom().nextInt(15, 30)), 1)
                        )
                )
        ).onlyStartWithMemoryStatus(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_ABSENT);
    }

    @Override
    public Map<Activity, BrainActivityGroup<? extends Penguin>> getAdditionalTasks() {
        return Map.of(
                Activity.SWIM, new BrainActivityGroup<Penguin>(Activity.SWIM)
                        .priority(10)
                        .behaviours(
                                new SetWalkTargetToBlock<>().closeEnoughWhen((mob, blockPosBlockStatePair) -> 0).predicate((mob, pair) -> (pair.getSecond().getFluidState().isEmpty() || pair.getSecond().is(Blocks.BUBBLE_COLUMN)) && pair.getSecond().isPathfindable(mob.level(), pair.getFirst(), PathComputationType.LAND) || pair.getFirst() == mob.blockPosition().above(8)).startCondition(mob -> mob.getAirSupply() < 140),
                                new Panic<>().panicIf((mob, damageSource) -> mob.isFreezing() || mob.isOnFire() || damageSource.getEntity() instanceof LivingEntity || this.isShocked()),
                                new BreedWithPartner<>(),
                                new FirstApplicableBehaviour<>(
                                        new FollowTemptation<>(),
                                        new PenguinJump(),
                                        new ReturnToHome().setRadius(8).startCondition(penguin -> !penguin.isLeashed() && !BrainUtils.hasMemory(penguin, MemoryModuleType.TEMPTING_PLAYER)),
                                        new SetRandomSwimTarget().avoidLandWhen(penguin -> penguin.getRandom().nextFloat() < 0.98F).setRadius(5, 4).walkTargetPredicate((mob, vec3) -> vec3 == null || !BrainUtils.hasMemory(mob, MemoryModuleType.HOME) || BrainUtils.getMemory(mob, MemoryModuleType.HOME).dimension() == mob.level().dimension() && vec3.distanceTo(BrainUtils.getMemory(mob, MemoryModuleType.HOME).pos().getCenter()) <= 8 && GoalUtils.isSolid(mob, BlockPos.containing(vec3).below()))
                                )
                        ).onlyStartWithMemoryStatus(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_PRESENT)
        );
    }

    private SetRandomWalkTarget<Penguin> createBoundWalkTarget(int xzRadius, int yRadius, int distanceFromHome) {
        return new SetRandomWalkTarget<Penguin>().setRadius(xzRadius, yRadius).walkTargetPredicate((mob, vec3) -> vec3 == null || (!BrainUtils.hasMemory(mob, MemoryModuleType.HOME) || BrainUtils.getMemory(mob, MemoryModuleType.HOME).dimension() == mob.level().dimension() && vec3.distanceTo(BrainUtils.getMemory(mob, MemoryModuleType.HOME).pos().getCenter()) <= distanceFromHome) && GoalUtils.isSolid(mob, BlockPos.containing(vec3).below()));
    }

    @Override
    public List<Activity> getActivityPriorities() {
        return ObjectArrayList.of(Activity.SWIM, Activity.IDLE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_STUMBLE_CHANCE, 0.0F);
        this.getEntityData().define(DATA_STUMBLE_TICKS, Integer.MIN_VALUE);
        this.getEntityData().define(DATA_STUMBLE_TICKS_BEFORE_GETTING_UP, Integer.MIN_VALUE);
        this.getEntityData().define(DATA_SHOVE_CHANCE, 0.0F);
        this.getEntityData().define(DATA_SHOVE_TICKS, Integer.MIN_VALUE);
        this.getEntityData().define(DATA_SHOCKED_TIME, 0);
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
        super.customServerAiStep();
    }

    @Override
    public void tick() {
        if (this.isNoAi()) {
            this.setAirSupply(this.getMaxAirSupply());
            return;
        }
        super.tick();

        if (!previousWaterValue && this.isInWater()) {
            this.setPose(Pose.SWIMMING);
            this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
            if (!this.level().isClientSide()) {
                BrainUtils.setMemory(this, RockhoppersMemoryModuleTypes.WATER_JUMP_COOLDOWN_TICKS, Mth.randomBetweenInclusive(this.getRandom(), 60, 100));
            } else {
                this.stopAllLandAnimations();
            }
            this.previousWaterValue = true;
        } else if (previousWaterValue && !this.isInWater() && this.onGround()) {
            this.setPose(Pose.STANDING);
            this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, -1.0F);
            if (this.level().isClientSide()) {
                this.stopAllWaterAnimations();
            }
            this.previousWaterValue = false;
        }

        if (!this.level().isClientSide()) {
            if (this.isShocked()) {
                this.setShockedTime(this.getShockedTime() - 1);
            }

            if (this.isStumbling()) {
                if (this.getStumbleTicks() > Penguin.STUMBLE_ANIMATION_LENGTH + this.getStumbleTicksBeforeGettingUp() + Penguin.GET_UP_ANIMATION_LENGTH) {
                    this.setStumbleTicks(Integer.MIN_VALUE);
                } else {
                    int previousValue = this.getStumbleTicks() == Integer.MIN_VALUE ? -1 : this.getStumbleTicks();
                    this.setStumbleTicks(previousValue + 1);
                }
            } else if (this.getShoveTicks() != Integer.MIN_VALUE) {
                if (this.getShoveTicks() < 0) {
                    this.setShoveTicks(Integer.MIN_VALUE);
                } else {
                    int previousValue = this.getShoveTicks();
                    this.setShoveTicks(previousValue - 1);
                }
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

                if (!this.animationSwimState && this.walkAnimation.isMoving()) {
                    this.swimEaseOutAnimationState.stop();
                    this.swimEaseInAnimationState.startIfStopped(this.tickCount);
                    this.animationSwimState = true;
                } else if (this.animationSwimState && !this.walkAnimation.isMoving()) {
                    this.swimEaseInAnimationState.stop();
                    this.swimEaseOutAnimationState.startIfStopped(this.tickCount);
                    this.animationSwimState = false;
                }
            } else {
                if (this.swimEaseOutAnimationState.getAccumulatedTime() >= this.stopEaseOutAnimAt && this.swimEaseOutAnimationState.isStarted()) {
                    this.swimEaseOutAnimationState.stop();
                }

                this.idleAnimationState.animateWhen(!this.walkAnimation.isMoving() && !this.isStumbling(), this.tickCount);
                this.waddleAnimationState.animateWhen(this.walkAnimation.isMoving() && !this.isStumbling(), this.tickCount);
                this.shockArmAnimationState.animateWhen(this.isShocked() && !this.isStumbling() && !this.isDeadOrDying(), this.tickCount);

                if (this.isStumbling()) {
                    this.stumbleFallingAnimationState.animateWhen(this.getDeltaMovement().y() < -0.05, this.tickCount);
                    if (!this.previousStumbleValue) {
                        this.previousStumbleValue = true;
                    }
                    if (this.getStumbleTicksBeforeGettingUp() != Integer.MIN_VALUE) {
                        if (this.getStumbleTicks() > STUMBLE_ANIMATION_LENGTH) {
                            this.stumbleAnimationState.stop();
                            this.stumbleGroundAnimationState.animateWhen(!this.isGettingUp(), this.tickCount);
                            this.stumbleGetUpAnimationState.animateWhen(this.isGettingUp(), this.tickCount);
                        } else if (!this.previousStumbleValue) {
                            this.stumbleAnimationState.start(this.tickCount);
                            this.waddleArmEaseOutAnimationState.stop();
                            this.waddleArmEaseInAnimationState.stop();
                            this.animationArmState = false;
                        }
                    }
                } else if (!this.isStumbling() && this.previousStumbleValue) {
                    this.stumbleAnimationState.stop();
                    this.stumbleGroundAnimationState.stop();
                    this.stumbleGetUpAnimationState.stop();
                    this.stumbleFallingAnimationState.stop();
                    this.previousStumbleValue = false;
                } else {
                    this.shoveAnimationState.animateWhen(this.getShoveTicks() != Integer.MIN_VALUE, this.tickCount);

                    if (!this.animationArmState && this.walkAnimation.isMoving()) {
                        this.waddleArmEaseOutAnimationState.stop();
                        this.waddleArmEaseInAnimationState.startIfStopped(this.tickCount);
                        this.animationArmState = true;
                    } else if (this.animationArmState && !this.walkAnimation.isMoving()) {
                        this.waddleArmEaseInAnimationState.stop();
                        this.waddleArmEaseOutAnimationState.startIfStopped(this.tickCount);
                        this.animationArmState = false;
                    }
                }

            }
        }
        this.refreshDimensionsIfShould();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @javax.annotation.Nullable SpawnGroupData spawnGroupData, @javax.annotation.Nullable CompoundTag tag) {
        if (tag == null || !tag.contains("type")) {
            if (getTotalSpawnWeight(level(), this.blockPosition()) > 0) {
                this.setPenguinType(getPenguinSpawnTypeDependingOnBiome(level, this.blockPosition(), this.getRandom()));
            } else {
                this.setPenguinType(getPenguinSpawnTypeGlobal(level, this.blockPosition(), this.getRandom()));
            }
        }

        this.setStumbleChance(Mth.randomBetween(this.getRandom(), 0.0025F, 0.005F));
        this.setShoveChance(Mth.randomBetween(this.getRandom(), 0.001F, 0.0025F));

        return super.finalizeSpawn(level, difficultyInstance, mobSpawnType, spawnGroupData, tag);
    }


    public static boolean canPenguinSpawn(EntityType<? extends Penguin> type, net.minecraft.world.level.LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return !level.getFluidState(pos).is(FluidTags.WATER) && Animal.isBrightEnoughToSpawn(level, pos) && getTotalSpawnWeight(level, pos) > 0;
    }


    public PenguinType getPenguinSpawnTypeDependingOnBiome(net.minecraft.world.level.LevelAccessor level, BlockPos pos, RandomSource random) {
        List<PenguinType> penguinTypes = new ArrayList<>();
        int totalWeight = 0;

        for (PenguinType penguinType : level.registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY)) {
            if (penguinType.spawnBiomes().stream().anyMatch(pt -> pt.holders().contains(level.getBiome(pos)) && pt.weight() > 0)) {
                penguinTypes.add(penguinType);
                totalWeight += penguinType.spawnBiomes().stream().map(WeightedHolderSet::weight).reduce(Integer::sum).orElse(0);
            }
        }

        if (penguinTypes.size() == 1) {
            return penguinTypes.get(0);
        } else if (!penguinTypes.isEmpty()) {
            int r = Mth.nextInt(random, 0, totalWeight - 1);
            for (PenguinType penguinType : penguinTypes) {
                r -= penguinType.spawnBiomes().stream().map(WeightedHolderSet::weight).reduce(Integer::sum).orElse(0);
                if (r < 0) {
                    return penguinType;
                }
            }
        }
        return null;
    }

    public PenguinType getPenguinSpawnTypeGlobal(net.minecraft.world.level.LevelAccessor level, BlockPos pos, RandomSource random) {
        List<PenguinType> penguinTypes = new ArrayList<>();
        int totalWeight = 0;

        for (PenguinType penguinType : level.registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY)) {
            if (penguinType.spawnBiomes().stream().anyMatch(pt -> pt.holders().size() > 0 && pt.weight() > 0)) {
                penguinTypes.add(penguinType);
                totalWeight += penguinType.spawnBiomes().stream().map(WeightedHolderSet::weight).reduce(Integer::sum).orElse(0);
            }
        }

        if (penguinTypes.size() == 1) {
            return penguinTypes.get(0);
        } else if (!penguinTypes.isEmpty()) {
            int r = Mth.nextInt(random, 0, totalWeight - 1);
            for (PenguinType penguinType : penguinTypes) {
                r -= penguinType.spawnBiomes().stream().map(WeightedHolderSet::weight).reduce(Integer::sum).orElse(0);
                if (r < 0) {
                    return penguinType;
                }
            }
        }
        return null;
    }

    public static int getTotalSpawnWeight(net.minecraft.world.level.LevelAccessor level, BlockPos pos) {
        int totalWeight = 0;

        for (PenguinType penguinType : level.registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY)) {
            if (penguinType.spawnBiomes().stream().anyMatch(pt -> pt.holders().contains(level.getBiome(pos)) && pt.weight() > 0)) {
                totalWeight += penguinType.spawnBiomes().stream().map(WeightedHolderSet::weight).reduce(Integer::sum).orElse(0);
            }
        }
        return totalWeight;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, Animal animal) {
        AgeableMob ageablemob = this.getBreedOffspring(level, animal);
        boolean cancelled = IRockhoppersPlatformHelper.INSTANCE.runAndIsBreedEventCancelled(this, animal);
        if (cancelled) {
            this.setAge(6000);
            animal.setAge(6000);
            this.resetLove();
            animal.resetLove();
        } else {
            BlockPos blockPos = this.blockPosition();
            BlockState blockstate = this.level().getBlockState(blockPos);

            // TODO: Make Penguin spawn egg upon finding a loose block.
            if (blockstate.isAir()) {
                this.level().setBlockAndUpdate(blockPos, RockhoppersBlocks.PENGUIN_EGG.defaultBlockState());
                this.level().levelEvent(2001, blockPos, Block.getId(this.level().getBlockState(blockPos)));
            }

            this.finalizeSpawnChildFromBreeding(level, animal, ageablemob);
        }
    }

    @Override
    public int getMaxHeadXRot() {
        return 15;
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
        return new PenguinPathNavigation(this, level);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageableMob) {
        return RockhoppersEntityTypes.PENGUIN.create(level);
    }

    @Override
    public SoundEvent getAmbientSound() {
        if (this.isInWaterOrBubble()) {
            return null;
        }
        Optional<Holder<SoundEvent>> soundEventHolder = this.getPenguinType().sounds().idleSound();
        if (soundEventHolder.isPresent() && soundEventHolder.get().isBound()) {
            return soundEventHolder.get().value();
        }
        return null;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSource) {
        Optional<Holder<SoundEvent>> soundEventHolder = this.getPenguinType().sounds().hurtSound();
        if (soundEventHolder.isPresent() && soundEventHolder.get().isBound()) {
            return soundEventHolder.get().value();
        }
        return null;
    }

    @Override
    public SoundEvent getDeathSound() {
        Optional<Holder<SoundEvent>> soundEventHolder = this.getPenguinType().sounds().deathSound();
        if (soundEventHolder.isPresent() && soundEventHolder.get().isBound()) {
            return soundEventHolder.get().value();
        }
        return null;
    }

    public SoundEvent getWaterJumpSound() {
        Optional<Holder<SoundEvent>> soundEventHolder = this.getPenguinType().sounds().waterJumpSound();
        if (soundEventHolder.isPresent() && soundEventHolder.get().isBound()) {
            return soundEventHolder.get().value();
        }
        return null;
    }

    @Override
    public int getExperienceReward() {
        return 0;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return BREED_ITEM.test(stack);
    }

    private boolean isActivelySwimming() {
        return (this.getDeltaMovement().horizontalDistanceSqr() > 0.02F || Mth.abs((float) this.getDeltaMovement().y()) > 0.02F);
    }

    public void refreshDimensionsIfShould() {
        if (this.isInWaterOrBubble() && this.previousWaterMovementValue ^ this.isActivelySwimming()) {
            this.previousWaterMovementValue = !this.previousWaterMovementValue;
            this.refreshDimensions();
        } else if (this.isStumbling() && this.getStumbleTicksBeforeGettingUp() != Integer.MIN_VALUE && (this.getStumbleTicks() == STUMBLE_ANIMATION_LENGTH + 2 || this.getStumbleTicks() == this.getStumbleTicksBeforeGettingUp() + 5)) {
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
        this.waddleArmEaseInAnimationState.stop();
        this.waddleArmEaseOutAnimationState.stop();
        this.shockArmAnimationState.stop();
        this.stumbleAnimationState.stop();
        this.stumbleFallingAnimationState.stop();
        this.stumbleGroundAnimationState.stop();
        this.stumbleGetUpAnimationState.stop();
        this.animationArmState = false;
    }

    private void stopAllWaterAnimations() {
        this.swimIdleAnimationState.stop();
        this.swimAnimationState.stop();
        if (this.walkAnimation.isMoving()) {
            this.swimEaseOutAnimationState.startIfStopped(this.tickCount);
            this.stopEaseOutAnimAt = this.tickCount + SWIM_EASE_OUT_ANIMATION_LENGTH;
        } else {
            this.swimEaseOutAnimationState.stop();
        }
        this.swimEaseInAnimationState.stop();
        this.animationSwimState = false;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (this.getStumbleTicksBeforeGettingUp() != Integer.MIN_VALUE && this.getStumbleTicks() >= STUMBLE_ANIMATION_LENGTH + 2 && this.getStumbleTicks() < this.getStumbleTicksBeforeGettingUp() + 5) {
            return super.getDimensions(pose).scale(1.33F, 0.5F);
        } else if (this.getPose() == Pose.SWIMMING && this.isActivelySwimming()) {
            return super.getDimensions(pose).scale(1.28333F, 0.7F);
        }
        return super.getDimensions(pose);
    }

    @Override
    protected void actuallyHurt(DamageSource damageSource, float amount) {
        super.actuallyHurt(damageSource, amount);
        this.setShockedTime(this.random.nextInt(60, 120));
        this.setShoveTicks(Integer.MIN_VALUE);
        this.setStumbleTicks(Integer.MIN_VALUE);
    }

    public void stumbleWithoutInitialAnimation() {
        this.setStumbleTicks(Penguin.STUMBLE_ANIMATION_LENGTH + 1);
        this.setStumbleTicksBeforeGettingUp(this.getRandom().nextIntBetweenInclusive(30, 60));
        BrainUtils.setMemory(this, MemoryModuleType.WALK_TARGET, null);
        BrainUtils.setMemory(this, MemoryModuleType.LOOK_TARGET, null);
    }

    @Nullable
    public PenguinType getPenguinType() {
        try {
            if (cachedPenguinType == null) {
                this.cachedPenguinType = this.level().registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY).get(ResourceKey.create(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY, new ResourceLocation(this.getEntityData().get(Penguin.DATA_TYPE))));
            }
            return this.cachedPenguinType;
        } catch (Exception ex) {
            if (!this.hasLoggedMissingError) {
                RapscallionsAndRockhoppers.LOG.error("Could not load PenguinType for penguin with UUID '{}'.", this.getStringUUID());
                this.hasLoggedMissingError = true;
            }
        }
        return null;
    }

    public ResourceLocation getPenguinTypeKey() {
        return new ResourceLocation(this.getEntityData().get(Penguin.DATA_TYPE));
    }

    public void setCustomName(@javax.annotation.Nullable Component name) {
        super.setCustomName(name);
        this.onNameChange(name);
    }

    public void invalidateCachedPenguinType() {
        this.cachedPenguinType = null;
        if (!this.level().isClientSide()) {
            IRockhoppersPlatformHelper.INSTANCE.sendS2CTracking(new InvalidateCachedPenguinTypePacket(this.getId()), this);
        }
    }

    public static void invalidateCachedPenguinTypes(Level level) {
        ((LevelAccessor)level).rapscallionsandrockhoppers$invokeGetEntities().get(EntityTypeTest.forClass(Penguin.class), AbortableIterationConsumer.forConsumer(Penguin::invalidateCachedPenguinType));
    }

    public void setPenguinType(PenguinType penguinType) {
        Registry<PenguinType> registry = this.level().registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY);
        this.getEntityData().set(DATA_TYPE, registry.getKey(penguinType).toString());
        this.getEntityData().set(DATA_PREVIOUS_TYPE, "");
    }

    public void setPenguinType(ResourceLocation penguinTypeKey) {
        Registry<PenguinType> registry = this.level().registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY);
        this.getEntityData().set(DATA_TYPE, penguinTypeKey.toString());
        this.getEntityData().set(DATA_PREVIOUS_TYPE, "");
    }

    public void onNameChange(Component newName) {
        Registry<PenguinType> registry = this.level().registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY);
        Optional<PenguinType> nameReference = registry.stream().filter(penguinType -> penguinType.whenNamed().isPresent() && penguinType.whenNamed().get().equals(ChatFormatting.stripFormatting(newName.getString()))).findFirst();
        if (nameReference.isPresent()) {
            this.getEntityData().set(DATA_PREVIOUS_TYPE, this.getEntityData().get(DATA_TYPE));
            this.getEntityData().set(DATA_TYPE, registry.getKey(nameReference.get()).toString());
        } else if (!this.getEntityData().get(DATA_PREVIOUS_TYPE).isEmpty()) {
            this.getEntityData().set(DATA_TYPE, this.getEntityData().get(DATA_PREVIOUS_TYPE));
            this.getEntityData().set(DATA_PREVIOUS_TYPE, "");
        }
        this.invalidateCachedPenguinType();
    }

    public void setStumbleChance(float stumbleChance) {
        this.getEntityData().set(DATA_STUMBLE_CHANCE, stumbleChance);
    }

    public float getStumbleChance() {
        return this.getEntityData().get(DATA_STUMBLE_CHANCE);
    }

    public int getShockedTime() {
        return this.entityData.get(DATA_SHOCKED_TIME);
    }

    public void setShockedTime(int shockedTime) {
        this.entityData.set(DATA_SHOCKED_TIME, shockedTime);
        this.animationArmState = true;
    }

    public boolean isShocked() {
        return this.entityData.get(DATA_SHOCKED_TIME) > 0;
    }

    public void setStumbleTicks(int stumbleTime) {
        this.entityData.set(DATA_STUMBLE_TICKS, stumbleTime);
    }

    public int getStumbleTicks() {
        return this.entityData.get(DATA_STUMBLE_TICKS);
    }

    public void setStumbleTicksBeforeGettingUp(int ticksBeforeGettingUp) {
        this.entityData.set(DATA_STUMBLE_TICKS_BEFORE_GETTING_UP, ticksBeforeGettingUp);
    }

    public int getStumbleTicksBeforeGettingUp() {
        return this.entityData.get(DATA_STUMBLE_TICKS_BEFORE_GETTING_UP);
    }

    public void setShoveChance(float shoveChance) {
        this.getEntityData().set(DATA_SHOVE_CHANCE, shoveChance);
    }

    public float getShoveChance() {
        return this.getEntityData().get(DATA_SHOVE_CHANCE);
    }

    public void setShoveTicks(int shoveTicks) {
        this.getEntityData().set(DATA_SHOVE_TICKS, shoveTicks);
    }

    public int getShoveTicks() {
        return this.getEntityData().get(DATA_SHOVE_TICKS);
    }

    public boolean isStumbling() {
        return this.getStumbleTicks() != Integer.MIN_VALUE;
    }

    public boolean isGettingUp() {
        return this.getStumbleTicksBeforeGettingUp() != Integer.MIN_VALUE && this.getStumbleTicksBeforeGettingUp() < this.getStumbleTicks();
    }

    public void setWalkStartTime(int walkStartTime) {
        this.walkStartTime = walkStartTime;
    }

    public int getWalkStartTime() {
        return this.walkStartTime;
    }

    public static class PenguinMoveControl extends SmoothSwimmingMoveControl {
        private boolean moveValue = false;

        public PenguinMoveControl(Penguin penguin) {
            super(penguin, 80, 20, 2.0F, 1.0F, false);
        }

        @Override
        public void tick() {
            if (this.canMove()) {
                super.tick();
                this.moveValue = true;
            } else if (this.moveValue) {
                this.mob.setXxa(0.0F);
                this.mob.setYya(0.0F);
                this.mob.setZza(0.0F);
                this.moveValue = false;
            }
        }

        private boolean canMove() {
            return ((Penguin)this.mob).getStumbleTicks() == Integer.MIN_VALUE && ((Penguin)this.mob).getShoveTicks() == Integer.MIN_VALUE;
        }

    }

    public static class PenguinLookControl extends LookControl {
        public PenguinLookControl(Penguin penguin) {
            super(penguin);
        }

        @Override
        public void tick() {
            if (this.canMove()) {
                super.tick();
            }
        }

        private boolean canMove() {
            return ((Penguin)this.mob).getStumbleTicks() == Integer.MIN_VALUE && ((Penguin)this.mob).getShoveTicks() == Integer.MIN_VALUE;
        }

    }

    public static class PenguinPathNavigation extends AmphibiousPathNavigation {
        public PenguinPathNavigation(Mob mob, Level level) {
            super(mob, level);
        }

        @Override
        public boolean canCutCorner(BlockPathTypes pathTypes) {
            return pathTypes != BlockPathTypes.WATER_BORDER && super.canCutCorner(pathTypes);
        }
    }

}
