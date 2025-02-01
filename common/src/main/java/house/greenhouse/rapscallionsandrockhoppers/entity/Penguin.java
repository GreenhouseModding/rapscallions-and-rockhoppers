package house.greenhouse.rapscallionsandrockhoppers.entity;

import com.mojang.datafixers.util.Pair;
import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.block.entity.PenguinEggBlockEntity;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.BreatheAir;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.CoughUpRewards;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.FollowBoat;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.JumpTowardsCatch;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.LeaveBoat;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.PenguinJump;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.PenguinPeck;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.PenguinShove;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.PenguinSitEgg;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.PenguinStumble;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.SetRandomSwimTarget;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.SitAtSurfaceOfWater;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.StayWithinBoat;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.StayWithinHome;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.SwimToFishingBobber;
import house.greenhouse.rapscallionsandrockhoppers.entity.behaviour.WalkToRewardedPlayer;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.BoatToFollowSensor;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.NearbyBobbersSensor;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.NearbyEggSensor;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.NearbyShoveableSensor;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.NearbyWaterSensor;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.PenguinAttackTargetSensor;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.PenguinHomeSensor;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.PlayerToCoughForSensor;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersActivities;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersBlocks;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersEntityTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersSoundEvents;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersTags;
import house.greenhouse.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.AvoidEntity;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowTemptation;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyBlocksSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.*;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Penguin extends Animal implements SmartBrainOwner<Penguin> {
    public static final int STUMBLE_ANIMATION_LENGTH = 30;
    private static final int SWIM_EASE_OUT_ANIMATION_LENGTH = 40;
    public static final int GET_UP_ANIMATION_LENGTH = 40;
    public static final int SHOVE_ANIMATION_LENGTH = 40;
    public static final int PECK_ANIMATION_LENGTH = 40;
    public static final int COUGH_ANIMATION_LENGTH = 40;

    protected static final Ingredient TEMPTATION_ITEM = Ingredient.of(RockhoppersTags.ItemTags.PENGUIN_TEMPT_ITEMS);
    private static final EntityDataAccessor<String> DATA_VARIANT = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_PREVIOUS_VARIANT = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_SHOCKED_TIME = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COUGH_TICKS = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PECK_TICKS = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STUMBLE_TICKS = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STUMBLE_TICKS_BEFORE_GETTING_UP = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SHOVE_TICKS = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_STUMBLE_CHANCE = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_SHOVE_CHANCE = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> DATA_EGG = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.STRING);

    private Holder<PenguinVariant> variant;

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
    public final AnimationState peckAnimationState = new AnimationState();
    public final AnimationState coughUpAnimationState = new AnimationState();

    private boolean areAnimationsWater = false;
    private boolean animationArmState = false;
    private boolean animationSwimState = false;
    private long easeOutAnimTime = Long.MIN_VALUE;
    private long stopEaseOutAnimAt = Long.MIN_VALUE;
    private boolean previousStumbleValue = false;
    private boolean previousWaterValue = false;
    private boolean previousWaterMovementValue = false;
    public Vec3 previousBoatPos = Vec3.ZERO;

    public Penguin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new PenguinMoveControl();
        this.lookControl = new PenguinLookControl();
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putFloat("stumble_chance", this.getStumbleChance());
        compoundTag.putFloat("shove_chance", this.getShoveChance());
        if (BrainUtils.hasMemory(this, MemoryModuleType.HOME)) {
            compoundTag.put("home", GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, BrainUtils.getMemory(this, MemoryModuleType.HOME)).getOrThrow(result -> {
                RapscallionsAndRockhoppers.LOG.error("Memory encoding error in penguin: {}", result);
                return null;
            }));
        }
        if (BrainUtils.hasMemory(this, RockhoppersMemoryModuleTypes.BOAT_TO_FOLLOW))
            compoundTag.putUUID("boat_to_follow", BrainUtils.getMemory(this, RockhoppersMemoryModuleTypes.BOAT_TO_FOLLOW));

        if (BrainUtils.hasMemory(this, RockhoppersMemoryModuleTypes.LAST_FOLLOWING_BOAT_CONTROLLER))
            compoundTag.putUUID("last_following_boat_controller", BrainUtils.getMemory(this, RockhoppersMemoryModuleTypes.LAST_FOLLOWING_BOAT_CONTROLLER));

        if (BrainUtils.hasMemory(this, RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR))
            compoundTag.putUUID("player_to_cough_for", BrainUtils.getMemory(this, RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR));

        if (BrainUtils.hasMemory(this, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_FOLLOW_BOAT))
            compoundTag.putInt("time_allowed_to_follow_boat", BrainUtils.getMemory(this, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_FOLLOW_BOAT));

        if (BrainUtils.hasMemory(this, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_WATER_JUMP))
            compoundTag.putInt("time_allowed_to_water_jump", BrainUtils.getMemory(this, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_WATER_JUMP));

        if (BrainUtils.hasMemory(this, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_EAT))
            compoundTag.putInt("time_allowed_to_eat", BrainUtils.getMemory(this, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_EAT));

        if (BrainUtils.hasMemory(this, RockhoppersMemoryModuleTypes.HUNGRY_TIME))
            compoundTag.putInt("hungry_time", BrainUtils.getMemory(this, RockhoppersMemoryModuleTypes.HUNGRY_TIME));

        if (BrainUtils.hasMemory(this, RockhoppersMemoryModuleTypes.FISH_EATEN))
            compoundTag.putInt("fish_eaten", BrainUtils.getMemory(this, RockhoppersMemoryModuleTypes.FISH_EATEN));

        compoundTag.putString("variant", this.getEntityData().get(DATA_VARIANT));
        if (!this.getEntityData().get(DATA_PREVIOUS_VARIANT).isEmpty())
            compoundTag.putString("previous_variant", this.getEntityData().get(DATA_PREVIOUS_VARIANT));
        if (!this.getEntityData().get(DATA_EGG).isEmpty())
            compoundTag.putString("egg", this.getEntityData().get(DATA_EGG));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setStumbleChance(compoundTag.getFloat("stumble_chance"));
        this.setShoveChance(compoundTag.getFloat("shove_chance"));
        if (compoundTag.contains("home", CompoundTag.TAG_COMPOUND))
            BrainUtils.setMemory(this, MemoryModuleType.HOME, GlobalPos.CODEC.decode(NbtOps.INSTANCE, compoundTag.getCompound("home")).getOrThrow(result -> {
                RapscallionsAndRockhoppers.LOG.error("Memory decoding error in penguin: {}", result);
                return null;
            }).getFirst());
        if (compoundTag.contains("boat_to_follow"))
            this.setBoatToFollow(compoundTag.getUUID("boat_to_follow"));
        else
            this.setBoatToFollow(null);
        if (compoundTag.contains("last_following_boat_controller"))
            BrainUtils.setMemory(this, RockhoppersMemoryModuleTypes.LAST_FOLLOWING_BOAT_CONTROLLER, compoundTag.getUUID("last_following_boat_controller"));
        else
            BrainUtils.clearMemory(this, RockhoppersMemoryModuleTypes.LAST_FOLLOWING_BOAT_CONTROLLER);
        if (compoundTag.contains("player_to_cough_for"))
            BrainUtils.setMemory(this, RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR, compoundTag.getUUID("player_to_cough_for"));
        else
            BrainUtils.clearMemory(this, RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR);

        if (compoundTag.contains("time_allowed_to_follow_boat", CompoundTag.TAG_INT))
            setTimeAllowedToFollowBoat(integerOptional(compoundTag.getInt("time_allowed_to_follow_boat")));
        if (compoundTag.contains("time_allowed_to_water_jump", CompoundTag.TAG_INT))
            setTimeAllowedToWaterJump(integerOptional(compoundTag.getInt("time_allowed_to_water_jump")));
        if (compoundTag.contains("time_allowed_to_eat", CompoundTag.TAG_INT))
            setTimeAllowedToEat(integerOptional(compoundTag.getInt("time_allowed_to_eat")));
        if (compoundTag.contains("hungry_time", CompoundTag.TAG_INT))
            setHungryTime(integerOptional(compoundTag.getInt("hungry_time")));
        if (compoundTag.contains("fish_eaten", CompoundTag.TAG_INT))
            setFishEaten(compoundTag.getInt("fish_eaten"));

        if (compoundTag.contains("previous_variant"))
            getEntityData().set(DATA_PREVIOUS_VARIANT, compoundTag.getString("previous_variant"));
        else if (compoundTag.contains("previous_type")) // Backwards compatibility.
            getEntityData().set(DATA_PREVIOUS_VARIANT, compoundTag.getString("previous_variant"));

        if (compoundTag.contains("variant") || compoundTag.contains("type")) { // 'type' nbt backwards compatibility.
            ResourceLocation typeKey = ResourceLocation.tryParse(compoundTag.contains("variant") ? compoundTag.getString("variant") : compoundTag.getString("type"));
            Optional<Holder.Reference<PenguinVariant>> variant = typeKey == null ? Optional.empty() : level().registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_VARIANT).getHolder(typeKey);
            variant.ifPresent(this::setVariant);
        }
        if (compoundTag.contains("egg") && ResourceLocation.tryParse(compoundTag.getString("egg")) != null)
            setEggPenguinType(compoundTag.getString("egg"));
    }

    private Optional<Integer> integerOptional(int value) {
        if (value == Integer.MIN_VALUE) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    @Override @NotNull
    protected Brain.Provider<Penguin> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<ExtendedSensor<Penguin>> getSensors() {
        return ObjectArrayList.of(
                new BoatToFollowSensor().setUpdatePenguinRadius(9.0F, 6.0F).setRadius(32.0F),
                new PenguinHomeSensor(),
                new NearbyShoveableSensor(),
                new NearbyBlocksSensor<Penguin>().setRadius(12.0F).setPredicate((blockState, penguin) -> true),
                new NearbyLivingEntitySensor<Penguin>().setRadius(16.0F),
                new NearbyPlayersSensor<Penguin>().setRadius(16.0F),
                new NearbyAdultSensor<>(),
                new NearbyWaterSensor().setXZRadius(8).setYRadius(4),
                new PenguinAttackTargetSensor<>(),
                new ItemTemptingSensor<Penguin>().temptedWith((entity, stack) -> TEMPTATION_ITEM.test(stack)),
                new InWaterSensor<Penguin>().setPredicate((entity, entity2) -> entity.isInWaterOrBubble() || BrainUtils.hasMemory(entity, RockhoppersMemoryModuleTypes.IS_JUMPING)),
                new HurtBySensor<>(),
                new NearbyEggSensor(),
                new PlayerToCoughForSensor(),
                new NearbyBobbersSensor()
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
                new PenguinSitEgg().startCondition(penguin -> !penguin.isBaby()).runFor((penguin -> penguin.random.nextInt(3600, 9000))).cooldownFor(penguin -> 1000), // Between 180 and 450 seconds
                new StayWithinHome().setRadius(5).startCondition(penguin -> !penguin.isStumbling() && penguin.getBoatToFollow() == null),
                new SetAttackTarget<Penguin>().attackPredicate(penguin -> BrainUtils.hasMemory(penguin, RockhoppersMemoryModuleTypes.HUNGRY_TIME) && penguin.tickCount > BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.HUNGRY_TIME) - 300),
                new PenguinPeck(8),
                new PenguinShove(),
                new PenguinStumble(),
                new FollowBoat().untilDistance(1.5F),
                new StayWithinBoat().setRadius(4),
                new FirstApplicableBehaviour<>(
                        new FollowTemptation<>(),
                        new SetWalkTargetToAttackTarget<>(),
                        new OneRandomBehaviour<>(
                                Pair.of(new SetRandomWalkTarget<Penguin>().setRadius(4, 3).avoidWaterWhen(penguin -> penguin.getRandom().nextFloat() < 0.98F), 9),
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
                                new Panic<>().panicIf((mob, damageSource) -> mob.isFreezing() || mob.isOnFire() || damageSource.getEntity() instanceof LivingEntity || this.isShocked()),
                                new BreedWithPartner<>(),
                                new StayWithinHome().setRadius(8),
                                new SetAttackTarget<Penguin>().attackPredicate(penguin -> BrainUtils.hasMemory(penguin, RockhoppersMemoryModuleTypes.HUNGRY_TIME) && penguin.tickCount > BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.HUNGRY_TIME) - 300),
                                new PenguinPeck(8),
                                new AvoidEntity<>().avoiding(entity -> entity instanceof Pufferfish),
                                new FirstApplicableBehaviour<>(
                                        new BreatheAir(),
                                        new FollowTemptation<>(),
                                        new SetWalkTargetToAttackTarget<>().startCondition(penguin -> penguin.getAirSupply() >= 260),
                                        new PenguinJump(),
                                        new OneRandomBehaviour<>(
                                                Pair.of(new SetRandomSwimTarget().avoidLandWhen(penguin -> penguin.getRandom().nextFloat() < 0.98F).setRadius(5, 4).walkTargetPredicate((mob, vec3) -> vec3 == null || mob.level().getEntities(EntityTypeTest.forClass(Boat.class), mob.getBoundingBox().move(vec3.subtract(mob.position())).inflate(3.0F, 2.0F, 3.0F), boat -> true).isEmpty()), 19),
                                                Pair.of(new Idle<>().runFor(entity -> entity.getRandom().nextInt(15, 30)), 1)
                                        )
                                )
                        ).onlyStartWithMemoryStatus(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_PRESENT),
                RockhoppersActivities.FOLLOW_BOAT, new BrainActivityGroup<Penguin>(RockhoppersActivities.FOLLOW_BOAT)
                        .priority(10)
                        .behaviours(
                                new LeaveBoat(),
                                new Panic<>().panicIf((mob, damageSource) -> mob.isFreezing() || mob.isOnFire() || damageSource.getEntity() instanceof LivingEntity || this.isShocked()),
                                new SetAttackTarget<Penguin>().attackPredicate(penguin -> {
                                    if  (!BrainUtils.hasMemory(penguin, RockhoppersMemoryModuleTypes.HUNGRY_TIME) || penguin.getAirSupply() < 260) {
                                        return false;
                                    }

                                    if (penguin.getBoatToFollow() != null && (!BrainUtils.hasMemory(penguin, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_EAT) || penguin.tickCount > BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_EAT))) {
                                        // As boats don't have velocity on the server, we must do this.
                                        boolean bl = penguin.previousBoatPos.subtract(penguin.getBoatToFollow().position()).horizontalDistance() > 0.075;
                                        penguin.previousBoatPos = penguin.getBoatToFollow().position();
                                        return bl;
                                    }

                                    return penguin.tickCount > BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.HUNGRY_TIME) - 280;
                                }),
                                new PenguinPeck(8),
                                new BreedWithPartner<>(),
                                new AvoidEntity<>().avoiding(entity -> entity instanceof Pufferfish),
                                new FollowBoat().untilDistance(2.0F).runFor(penguin -> 200),
                                new StayWithinBoat().setRadius(8),
                                new FirstApplicableBehaviour<>(
                                        new BreatheAir(),
                                        new FollowTemptation<>(),
                                        new SetWalkTargetToAttackTarget<>().startCondition(penguin -> penguin.getAirSupply() >= 260),
                                        new PenguinJump(),
                                        new OneRandomBehaviour<>(
                                                Pair.of(new SetRandomSwimTarget().setRadius(6.0F, 4.0F).startCondition(penguin -> penguin.getBoatToFollow() != null && penguin.getBoatToFollow().getDeltaMovement().horizontalDistanceSqr() < 0.05), 19),
                                                Pair.of(new Idle<>().runFor(entity -> entity.getRandom().nextInt(15, 30)), 1)
                                        )
                                )
                        ).onlyStartWithMemoryStatus(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_PRESENT)
                        .onlyStartWithMemoryStatus(RockhoppersMemoryModuleTypes.BOAT_TO_FOLLOW, MemoryStatus.VALUE_PRESENT),
                RockhoppersActivities.COUGH_UP, new BrainActivityGroup<Penguin>(RockhoppersActivities.COUGH_UP)
                        .priority(10)
                        .behaviours(
                                new BreatheAir(),
                                new Panic<>().panicIf((mob, damageSource) -> mob.isFreezing() || mob.isOnFire() || damageSource.getEntity() instanceof LivingEntity || this.isShocked()),
                                new BreedWithPartner<>(),
                                new WalkToRewardedPlayer(),
                                new CoughUpRewards(4),
                                new FollowTemptation<>()
                        )
                        .onlyStartWithMemoryStatus(RockhoppersMemoryModuleTypes.FISH_EATEN, MemoryStatus.VALUE_PRESENT)
                        .onlyStartWithMemoryStatus(RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR, MemoryStatus.VALUE_PRESENT),
                RockhoppersActivities.WAIT_AROUND_BOBBER, new BrainActivityGroup<Penguin>(RockhoppersActivities.WAIT_AROUND_BOBBER)
                        .priority(10)
                        .behaviours(
                                new BreedWithPartner<>(),
                                new SitAtSurfaceOfWater(),
                                new SwimToFishingBobber().setRadius(3),
                                new JumpTowardsCatch()
                        ).onlyStartWithMemoryStatus(RockhoppersMemoryModuleTypes.NEAREST_BOBBERS, MemoryStatus.VALUE_PRESENT)
                        .onlyStartWithMemoryStatus(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT)
        );
    }

    @Override
    public List<Activity> getActivityPriorities() {
        return ObjectArrayList.of(RockhoppersActivities.COUGH_UP, RockhoppersActivities.WAIT_AROUND_BOBBER, RockhoppersActivities.FOLLOW_BOAT, Activity.SWIM, Activity.IDLE);
    }

    public int getFishEaten() {
        return BrainUtils.memoryOrDefault(this, RockhoppersMemoryModuleTypes.FISH_EATEN, () -> 0);
    }

    public void setFishEaten(int fishEatenValue) {
        BrainUtils.setMemory(this, RockhoppersMemoryModuleTypes.FISH_EATEN, fishEatenValue);
    }

    public void incrementFishEaten() {
        BrainUtils.setMemory(this, RockhoppersMemoryModuleTypes.FISH_EATEN, BrainUtils.memoryOrDefault(this, RockhoppersMemoryModuleTypes.FISH_EATEN, () -> 0) + 1);
    }

    public void setTimeAllowedToWaterJump(Optional<Integer> waterJumpCooldownTicks) {
        BrainUtils.setMemory(this, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_WATER_JUMP, waterJumpCooldownTicks.orElse(null));
    }

    public int getTimeAllowedToWaterJump() {
        return BrainUtils.memoryOrDefault(this, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_WATER_JUMP, () -> Integer.MIN_VALUE);
    }

    public void setTimeAllowedToEat(Optional<Integer> eatTicks) {
        BrainUtils.setMemory(this, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_EAT, eatTicks.orElse(null));
    }

    public int getTimeAllowedToEat() {
        return BrainUtils.memoryOrDefault(this, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_EAT, () -> Integer.MIN_VALUE);
    }

    public void setHungryTime(Optional<Integer> leaveTime) {
        BrainUtils.setMemory(this, RockhoppersMemoryModuleTypes.HUNGRY_TIME, leaveTime.orElse(null));
    }

    public int getHungryTime() {
        return BrainUtils.memoryOrDefault(this, RockhoppersMemoryModuleTypes.HUNGRY_TIME, () -> Integer.MIN_VALUE);
    }

    public void setTimeAllowedToFollowBoat(Optional<Integer> boatFollowCooldownTicks) {
        BrainUtils.setMemory(this, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_FOLLOW_BOAT, boatFollowCooldownTicks.orElse(null));
    }

    public int getTimeAllowedToFollowBoat() {
        return BrainUtils.memoryOrDefault(this, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_FOLLOW_BOAT, () -> Integer.MIN_VALUE);
    }

    public void setBoatToFollow(@Nullable UUID boatUuid) {
        BrainUtils.setMemory(this, RockhoppersMemoryModuleTypes.BOAT_TO_FOLLOW, boatUuid);
    }

    public Boat getBoatToFollow() {
        if (!this.level().isClientSide()) {
            Entity entity = ((ServerLevel)this.level()).getEntity(BrainUtils.getMemory(this, RockhoppersMemoryModuleTypes.BOAT_TO_FOLLOW));
            if (entity instanceof Boat boat) {
                return boat;
            }
        }
        return null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, "rapscallionsandrockhoppers:rockhopper");
        builder.define(DATA_PREVIOUS_VARIANT, "");
        builder.define(DATA_STUMBLE_CHANCE, 0.0F);
        builder.define(DATA_STUMBLE_TICKS, Integer.MIN_VALUE);
        builder.define(DATA_STUMBLE_TICKS_BEFORE_GETTING_UP, Integer.MIN_VALUE);
        builder.define(DATA_SHOVE_CHANCE, 0.0F);
        builder.define(DATA_SHOVE_TICKS, Integer.MIN_VALUE);
        builder.define(DATA_SHOCKED_TIME, 0);
        builder.define(DATA_PECK_TICKS, Integer.MIN_VALUE);
        builder.define(DATA_EGG, "");
        builder.define(DATA_COUGH_TICKS, Integer.MIN_VALUE);
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
        super.customServerAiStep();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(RockhoppersTags.ItemTags.PENGUIN_FOOD_ITEMS)) {
            if (this.level().isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            if (this.tickCount > this.getTimeAllowedToEat()) {
                this.setHungryTime(Optional.of(this.tickCount + 4800));
                this.setTimeAllowedToEat(Optional.of(this.tickCount + 400));
                if (this.getBoatToFollow() != null) {
                    this.incrementFishEaten();
                }
                stack.consume(1, player);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(0.5), this.getRandomY() + 0.5, this.getRandomZ(0.5), 7, 0.25, 0.1, 0.25, 0);
                this.playSound(RockhoppersSoundEvents.PENGUIN_EAT);
                return InteractionResult.SUCCESS;
            } else {
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.SMOKE, this.getRandomX(0.5), this.getRandomY() + 0.5, this.getRandomZ(0.5), 7, 0.25, 0.1, 0.25, 0);
                return InteractionResult.CONSUME;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        if (this.isNoAi()) {
            this.setAirSupply(this.getMaxAirSupply());
            return;
        }
        super.tick();

        if (!previousWaterValue && this.isInWater() && this.getVehicle() == null) {
            this.setPose(Pose.SWIMMING);
            if (!this.level().isClientSide()) {
                this.setTimeAllowedToWaterJump(Optional.of(this.tickCount + Mth.randomBetweenInclusive(this.getRandom(), 200, 400)));
            }
            this.previousWaterValue = true;
        } else if (previousWaterValue && (!this.isInWater() && this.onGround() || this.getVehicle() != null)) {
            this.setPose(Pose.STANDING);
            this.previousWaterValue = false;
        }

        if (!this.level().isClientSide()) {
            if (this.isShocked()) {
                this.setShockedTime(this.getShockedTime() - 1);
            }

            if (this.isPecking()) {
                if (this.getPeckTicks() > PECK_ANIMATION_LENGTH) {
                    this.setPeckTicks(Integer.MIN_VALUE);
                } else {
                    this.setPeckTicks(this.getPeckTicks() + 1);
                }
            }

            if (this.isCoughingUpItems()) {
                if (this.getCoughTicks() > COUGH_ANIMATION_LENGTH) {
                    this.setCoughTicks(Integer.MIN_VALUE);
                } else {
                    this.setCoughTicks(this.getCoughTicks() + 1);
                }
            }

            if (this.getShoveTicks() != Integer.MIN_VALUE) {
                if (this.getShoveTicks() < 0) {
                    this.setShoveTicks(Integer.MIN_VALUE);
                } else {
                    int previousValue = this.getShoveTicks();
                    this.setShoveTicks(previousValue - 1);
                }
            }

            if (hasEgg() && onGround() && !isStumbling() && getPose() == Pose.STANDING) {
                if (level().getBlockState(blockPosition()).isAir()
                        && getBlockStateOn().isFaceSturdy(level(), blockPosition(), getDirection())
                        && !level().getBlockState(blockPosition().below()).isAir()) {
                    level().setBlockAndUpdate(blockPosition(), RockhoppersBlocks.PENGUIN_EGG.defaultBlockState());
                    if (level().getBlockEntity(blockPosition()) instanceof PenguinEggBlockEntity penguinEggBlockEntity) {
                        penguinEggBlockEntity.setPenguinVariant(getEggType());
                    }
                    this.setEggPenguinType("");
                }
            }
        } else {
            this.peckAnimationState.animateWhen(this.isPecking(), this.tickCount);

            if (this.getPose() == Pose.SWIMMING) {
                if (!this.areAnimationsWater) {
                    this.stopAllLandAnimations();
                    this.areAnimationsWater = true;
                }
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
                if (this.areAnimationsWater) {
                    this.stopAllWaterAnimations();
                    this.areAnimationsWater = false;
                }

                this.coughUpAnimationState.animateWhen(this.isCoughingUpItems(), this.tickCount);

                if (this.easeOutAnimTime > this.stopEaseOutAnimAt) {
                    this.swimEaseOutAnimationState.stop();
                    this.easeOutAnimTime = Integer.MIN_VALUE;
                    this.stopEaseOutAnimAt = Integer.MIN_VALUE;
                }
                if (this.easeOutAnimTime != Integer.MIN_VALUE) {
                    ++this.easeOutAnimTime;
                }

                this.idleAnimationState.animateWhen(!this.walkAnimation.isMoving() && !this.isStumbling(), this.tickCount);
                this.waddleAnimationState.animateWhen(this.walkAnimation.isMoving() && !this.isStumbling(), this.tickCount);
                this.shockArmAnimationState.animateWhen(this.isShocked() && !this.isStumbling() && !this.isDeadOrDying(), this.tickCount);

                if (this.isStumbling()) {
                    this.stumbleFallingAnimationState.animateWhen(this.getDeltaMovement().y() < -0.1, this.tickCount);
                    if (!this.previousStumbleValue) {
                        this.waddleArmEaseOutAnimationState.stop();
                        this.waddleArmEaseInAnimationState.stop();
                        this.stumbleAnimationState.start(this.tickCount);
                        this.animationArmState = false;
                        this.previousStumbleValue = true;
                    }
                    if (this.getStumbleTicksBeforeGettingUp() != Integer.MIN_VALUE && this.getStumbleTicks() > STUMBLE_ANIMATION_LENGTH) {
                        this.stumbleAnimationState.stop();
                        this.stumbleGroundAnimationState.animateWhen(!this.isGettingUp(), this.tickCount);
                        this.stumbleGetUpAnimationState.animateWhen(this.isGettingUp(), this.tickCount);
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


    public void returnToHome() {
        GlobalPos home = BrainUtils.getMemory(this, MemoryModuleType.HOME);
        if (home != null && this.level().dimension() == home.dimension() && this.blockPosition().distSqr(home.pos()) > 48 * 48) {
            BlockPos randomPos = null;

            int xSection = SectionPos.blockToSectionCoord(home.pos().getX());
            int zSection = SectionPos.blockToSectionCoord(home.pos().getZ());
            if (this.level() instanceof ServerLevel serverLevel && serverLevel.getChunk(xSection, zSection, ChunkStatus.FULL, true) == null) {
                RapscallionsAndRockhoppers.loadNearbyChunks(home.pos(), serverLevel);
            }
            for (int i = 0; i < 10 || !this.level().getBlockState(randomPos).isPathfindable(PathComputationType.WATER); ++i) {
                randomPos = getRandomPos(home.pos());
            }
            if (!this.level().getBlockState(randomPos).isPathfindable(PathComputationType.WATER)) {
                randomPos = null;
            }
            if (randomPos == null) {
                randomPos = home.pos();
                for (int i = 0; i < 10 || !this.level().getBlockState(randomPos).isPathfindable(PathComputationType.LAND); ++i) {
                    randomPos = home.pos().above();
                }
            }
            this.teleportTo(randomPos.getX(), randomPos.getY(), randomPos.getZ());
            if (this.level() instanceof ServerLevel serverLevel) {
                RapscallionsAndRockhoppers.unloadChunks(serverLevel);
            }
        }
    }

    // TODO: There's probably a better algorithm for calculating this... Oh well.
    private BlockPos getRandomPos(BlockPos home) {
        int xOffset = this.getRandom().nextIntBetweenInclusive(8, 12);
        int zOffset = this.getRandom().nextIntBetweenInclusive(8, 12);
        if (this.getRandom().nextBoolean()) {
            xOffset = -xOffset;
        }
        if (this.getRandom().nextBoolean()) {
            zOffset = -zOffset;
        }
        return new BlockPos(home.getX() + xOffset, home.getY(), home.getZ() + zOffset);
    }

    public int getTotalStumbleAnimationLength() {
        if (this.getStumbleTicksBeforeGettingUp() == Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return STUMBLE_ANIMATION_LENGTH + this.getStumbleTicksBeforeGettingUp() + GET_UP_ANIMATION_LENGTH;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData data) {
        if (spawnType != MobSpawnType.STRUCTURE) {
            if (data == null) {
                data = new PenguinGroupData();
            }
            setVariant(((PenguinGroupData)data).getSpawnType(blockPosition(), level, level.getRandom()));
        }
        return super.finalizeSpawn(level, difficulty, spawnType, data);
    }

    public static boolean checkPenguinSpawnRules(EntityType<? extends Penguin> entityType, net.minecraft.world.level.LevelAccessor level, MobSpawnType mobSpawnType, BlockPos pos, RandomSource random) {
        return getTotalSpawnWeight(level, pos) > 0 && Animal.isBrightEnoughToSpawn(level, pos);
    }

    public static int getTotalSpawnWeight(net.minecraft.world.level.LevelAccessor level, BlockPos pos) {
        int totalWeight = 0;

        for (PenguinVariant variant : level.registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_VARIANT)) {
            if (variant.biomes().unwrap().stream().anyMatch(pt -> pt.data().contains(level.getBiome(pos)) && pt.weight().asInt() > 0)) {
                totalWeight += variant.biomes().unwrap().stream().map(holderSetWrapper -> holderSetWrapper.weight().asInt()).reduce(Integer::sum).orElse(0);
            }
        }
        return totalWeight;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, Animal animal) {
        boolean cancelled = RapscallionsAndRockhoppers.getHelper().runAndIsBreedEventCancelled(this, animal);
        if (cancelled) {
            this.setAge(6000);
            animal.setAge(6000);
            this.resetLove();
            animal.resetLove();
        } else {
            var pickedEggType = this.random.nextBoolean() ? this.getTrueType() : ((Penguin) animal).getTrueType();
            this.setEggPenguinType(pickedEggType.toString());;
            this.finalizeSpawnChildFromBreeding(level, animal, null);
        }
    }

    @Override
    public boolean canRide(Entity vehicle) {
        if (vehicle instanceof Boat boat) {
            return RapscallionsAndRockhoppers.getHelper().getBoatData(boat).penguinCount() == 0 && RapscallionsAndRockhoppers.getHelper().getBoatData(boat).getPreviousLinkedBoats().stream().noneMatch(boat1 -> RapscallionsAndRockhoppers.getHelper().getBoatData(boat1).penguinCount() > 0) && RapscallionsAndRockhoppers.getHelper().getBoatData(boat).getNextLinkedBoats().stream().noneMatch(boat1 -> RapscallionsAndRockhoppers.getHelper().getBoatData(boat1).penguinCount() > 0);
        }
        return true;
    }

    @Override
    public int getMaxHeadXRot() {
        return 15;
    }

    @Override
    public int getMaxAirSupply() {
        return 4800;
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
        Penguin baby = RockhoppersEntityTypes.PENGUIN.create(level);
        if (baby != null) {
            if (this.getRandom().nextBoolean())
                baby.setFromParent((Penguin) ageableMob);
            else
                baby.setFromParent(this);
        }
        return baby;
    }

    protected void setFromParent(Penguin penguin) {
        Registry<PenguinVariant> registry = level().registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_VARIANT);
        if (!penguin.getEntityData().get(DATA_PREVIOUS_VARIANT).isEmpty()) {
            try {
                Optional<Holder.Reference<PenguinVariant>> variant = registry.getHolder(ResourceLocation.tryParse(penguin.getEntityData().get(DATA_PREVIOUS_VARIANT)));
                variant.ifPresent(this::setVariant);
            } catch (Exception ex) {
                RapscallionsAndRockhoppers.LOG.error("Failed to set penguin from parent: ", ex);
            }
            return;
        }
        try {
            Optional<Holder.Reference<PenguinVariant>> variant = registry.getHolder(ResourceLocation.tryParse(penguin.getEntityData().get(DATA_VARIANT)));
            variant.ifPresent(this::setVariant);
        } catch (Exception ex) {
            RapscallionsAndRockhoppers.LOG.error("Failed to set penguin from parent: ", ex);
        }
    }

    @Override
    public SoundEvent getAmbientSound() {
        Optional<Holder<SoundEvent>> soundEventHolder = getVariant().value().sounds().ambientSound();
        if (soundEventHolder.isPresent() && soundEventHolder.get().isBound()) {
            return soundEventHolder.get().value();
        }
        return SoundEvents.EMPTY;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSource) {
        Optional<Holder<SoundEvent>> soundEventHolder = getVariant().value().sounds().hurtSound();
        if (soundEventHolder.isPresent() && soundEventHolder.get().isBound()) {
            return soundEventHolder.get().value();
        }
        return SoundEvents.EMPTY;
    }

    @Override
    public SoundEvent getDeathSound() {
        Optional<Holder<SoundEvent>> soundEventHolder = getVariant().value().sounds().deathSound();
        if (soundEventHolder.isPresent() && soundEventHolder.get().isBound()) {
            return soundEventHolder.get().value();
        }
        return SoundEvents.EMPTY;
    }

    public SoundEvent getWaterJumpSound() {
        Optional<Holder<SoundEvent>> soundEventHolder = getVariant().value().sounds().waterJumpSound();
        if (soundEventHolder.isPresent() && soundEventHolder.get().isBound()) {
            return soundEventHolder.get().value();
        }
        return SoundEvents.EMPTY;
    }

    public Holder<PenguinVariant> getVariant() {
        if (variant == null) {
            ResourceLocation variantLocation = ResourceLocation.tryParse(getEntityData().get(DATA_VARIANT));
            if (variantLocation == null)
                variantLocation = RapscallionsAndRockhoppers.asResource("rapscallionsandrockhoppers:rockhopper");
            level().registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_VARIANT).getHolder(variantLocation).ifPresentOrElse(v -> variant = v, () ->
                    variant = level().registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_VARIANT).getHolderOrThrow(RockhoppersResourceKeys.PenguinTypeKeys.ROCKHOPPER));
        }
        return variant;
    }

    public void setCustomName(@Nullable Component name) {
        this.onNameChange(name);
        super.setCustomName(name);
    }

    public void setVariant(Holder<PenguinVariant> value) {
        this.getEntityData().set(DATA_VARIANT, value.unwrapKey().orElseThrow().location().toString());
        if (value.isBound() && value.value().whenNamed().isEmpty()) {
            this.getEntityData().set(DATA_PREVIOUS_VARIANT, "");
        }
    }

    public void onNameChange(Component newName) {
        Registry<PenguinVariant> registry = this.level().registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_VARIANT);
        Optional<Holder.Reference<PenguinVariant>> nameReference = registry.holders().filter(penguinType -> penguinType.value().whenNamed().isPresent() && penguinType.value().whenNamed().get().equals(ChatFormatting.stripFormatting(newName.getString()))).findFirst();
        if (nameReference.isPresent()) {
            if (this.getEntityData().get(DATA_PREVIOUS_VARIANT).isEmpty())
                this.getEntityData().set(DATA_PREVIOUS_VARIANT, this.getEntityData().get(DATA_VARIANT));
            setVariant(nameReference.get());
        } else if (!this.getEntityData().get(DATA_PREVIOUS_VARIANT).isEmpty()) {
            Optional<Holder.Reference<PenguinVariant>> previous = registry.getHolder(ResourceLocation.tryParse(getEntityData().get(DATA_PREVIOUS_VARIANT)));
            if (previous.isPresent()) {
                this.setVariant(previous.get());
                this.getEntityData().set(DATA_PREVIOUS_VARIANT, "");
            }
        }
    }

    @Override
    protected int getBaseExperienceReward() {
        return 0;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(RockhoppersTags.ItemTags.PENGUIN_BREED_ITEMS);
    }


    public void refreshDimensionsIfShould() {
        if (this.isInWater() && this.previousWaterMovementValue ^ this.getPose() == Pose.SWIMMING) {
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
        }
        this.swimEaseInAnimationState.stop();
        this.animationSwimState = false;
    }


    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        if (this.getStumbleTicksBeforeGettingUp() != Integer.MIN_VALUE && this.getStumbleTicks() >= STUMBLE_ANIMATION_LENGTH + 2 && this.getStumbleTicks() < this.getStumbleTicksBeforeGettingUp() + 5) {
            return super.getDefaultDimensions(pose).scale(1.33F, 0.5F);
        } else if (this.getPose() == Pose.SWIMMING) {
            return super.getDefaultDimensions(pose).scale(1.28333F, 0.7F);
        }
        return super.getDefaultDimensions(pose);
    }

    @Override
    protected void actuallyHurt(DamageSource damageSource, float amount) {
        super.actuallyHurt(damageSource, amount);
        this.setShockedTime(this.random.nextInt(60, 120));
        this.setShoveTicks(Integer.MIN_VALUE);
        this.setStumbleTicks(Integer.MIN_VALUE);
        if (this.getHungryTime() != Integer.MIN_VALUE) {
            this.setHungryTime(integerOptional(this.getHungryTime() - 40));
        }
    }

    public void stumbleWithoutInitialAnimation() {
        this.setStumbleTicks(Penguin.STUMBLE_ANIMATION_LENGTH + 1);
        this.setStumbleTicksBeforeGettingUp(this.getRandom().nextIntBetweenInclusive(30, 60));
        BrainUtils.clearMemories(this, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET);
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

    public void setPeckTicks(int peckTime) {
        this.entityData.set(DATA_PECK_TICKS, peckTime);
    }

    public boolean isPecking() {
        return this.entityData.get(DATA_PECK_TICKS) != Integer.MIN_VALUE;
    }

    public int getCoughTicks() {
        return this.entityData.get(DATA_COUGH_TICKS);
    }

    public void setCoughTicks(int peckTime) {
        this.entityData.set(DATA_COUGH_TICKS, peckTime);
    }

    public boolean isCoughingUpItems() {
        return this.entityData.get(DATA_COUGH_TICKS) != Integer.MIN_VALUE;
    }

    public int getPeckTicks() {
        return this.entityData.get(DATA_PECK_TICKS);
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
        return this.isStumbling() && this.getStumbleTicksBeforeGettingUp() != Integer.MIN_VALUE && this.getStumbleTicks() > STUMBLE_ANIMATION_LENGTH + this.getStumbleTicksBeforeGettingUp();
    }

    public void setEggPenguinType(String eggPenguinType) {
        this.getEntityData().set(DATA_EGG, eggPenguinType);
    }
    public boolean hasEgg() {
        return !this.getEntityData().get(DATA_EGG).isEmpty();
    }

    public Holder<PenguinVariant> getEggType() {
        ResourceLocation previousTypeKey = ResourceLocation.tryParse(getEntityData().get(DATA_EGG));
        if (previousTypeKey != null) {
            var variant = level().registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_VARIANT).getHolder(previousTypeKey);
            if (variant.isPresent())
                return variant.get();
        }
        return getVariant();
    }

    /**
     * If the penguin has a previous type, it will return that. Otherwise, it will return the current type.
     * @return The resource location of the penguin's type.
     */
    public Holder<PenguinVariant> getTrueType() {
        if (!this.getEntityData().get(DATA_PREVIOUS_VARIANT).isEmpty()) {
            ResourceLocation previousTypeKey = ResourceLocation.tryParse(getEntityData().get(DATA_PREVIOUS_VARIANT));
            if (previousTypeKey != null) {
                var variant = level().registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_VARIANT).getHolder(previousTypeKey);
                if (variant.isPresent())
                    return variant.get();
            }
        }
        return getVariant();
    }

    public static class PenguinGroupData extends AgeableMobGroupData {
        public PenguinGroupData() {
            super(true);
        }

        public Holder<PenguinVariant> getSpawnType(BlockPos pos, ServerLevelAccessor level, RandomSource random) {
            if (getTotalSpawnWeight(level, pos) > 0)
                return getSpawnTypeDependingOnBiome(level, pos, random);
            return level.registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_VARIANT).getHolderOrThrow(RockhoppersResourceKeys.PenguinTypeKeys.ROCKHOPPER);
        }

        public Holder<PenguinVariant> getSpawnTypeDependingOnBiome(ServerLevelAccessor level, BlockPos pos, RandomSource random) {
            List<Holder<PenguinVariant>> penguinList = new ArrayList<>();
            int totalWeight = 0;

            for (Holder.Reference<PenguinVariant> variant : level.registryAccess().registry(RockhoppersResourceKeys.PENGUIN_VARIANT).orElseThrow().holders().filter(Holder.Reference::isBound).toList()) {
                Optional<WeightedEntry.Wrapper<HolderSet<Biome>>> biome = variant.value().biomes().unwrap().stream().filter(holderSetWrapper -> holderSetWrapper.data().contains(level.getBiome(pos))).findFirst();
                if (biome.isPresent()) {
                    penguinList.add(variant);
                    totalWeight += biome.get().weight().asInt();
                }
            }

            if (penguinList.size() == 1) {
                return penguinList.getFirst();
            } else if (!penguinList.isEmpty()) {
                int r = Mth.nextInt(random, 0, totalWeight - 1);
                for (Holder<PenguinVariant> variant : penguinList) {
                    int max = variant.value().biomes().unwrap().stream().filter(wrapper -> wrapper.data().contains(level.getBiome(pos))).map(wrapper -> wrapper.weight().asInt()).max(Comparator.comparingInt(value -> value)).orElse(0);
                    r -= max;
                    if (r < 0.0)
                        return variant;
                }
            }
            return level.registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_VARIANT).getHolderOrThrow(RockhoppersResourceKeys.PenguinTypeKeys.ROCKHOPPER);
        }
    }

    public class PenguinMoveControl extends SmoothSwimmingMoveControl {
        private boolean moveValue = false;

        public PenguinMoveControl() {
            super(Penguin.this, 80, 20, 2.0F, 1.0F, false);
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
            return !Penguin.this.isStumbling() && Penguin.this.getShoveTicks() == Integer.MIN_VALUE && !Penguin.this.isCoughingUpItems();
        }

    }

    public class PenguinLookControl extends LookControl {
        public PenguinLookControl() {
            super(Penguin.this);
        }

        @Override
        public void tick() {
            if (this.canMove()) {
                super.tick();
            }
        }

        private boolean canMove() {
            return !Penguin.this.isStumbling() && Penguin.this.getShoveTicks() == Integer.MIN_VALUE;
        }

    }

    public static class PenguinPathNavigation extends AmphibiousPathNavigation {
        public PenguinPathNavigation(Mob mob, Level level) {
            super(mob, level);
        }

        @Override
        public boolean canCutCorner(PathType pathTypes) {
            return pathTypes != PathType.WATER_BORDER && super.canCutCorner(pathTypes);
        }
    }

}
