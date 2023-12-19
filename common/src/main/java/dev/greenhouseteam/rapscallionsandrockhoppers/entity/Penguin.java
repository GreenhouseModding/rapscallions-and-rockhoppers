package dev.greenhouseteam.rapscallionsandrockhoppers.entity;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal.PenguinPanicGoal;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Penguin extends Animal {
    private static final Ingredient FOOD_ITEMS = Ingredient.of(
            Items.INK_SAC, Items.GLOW_INK_SAC
    );
    private static final EntityDataAccessor<Integer> DATA_SHOCKED_TIME = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState waddleAnimationState = new AnimationState();
    public final AnimationState shockArmAnimationState = new AnimationState();
    public final AnimationState waddleExpandAnimationState = new AnimationState();
    public final AnimationState waddleRetractAnimationState = new AnimationState();
    private boolean animationArmState = false;

    public Penguin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(1, new PenguinPanicGoal(this, 1.5));
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
                this.idleAnimationState.animateWhen(!this.walkAnimation.isMoving(), this.tickCount);
                this.waddleAnimationState.animateWhen(this.walkAnimation.isMoving(), this.tickCount);
                this.shockArmAnimationState.animateWhen(this.isShocked() && !this.isDeadOrDying(), this.tickCount);

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
}
