package dev.greenhouseteam.rapscallionsandrockhoppers.entity;

import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Penguin extends Animal {
    private static final EntityDataAccessor<Integer> DATA_SHOCKED_TIME = SynchedEntityData.defineId(Penguin.class, EntityDataSerializers.INT);

    public Penguin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
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
        if (!this.level().isClientSide) {
            if (this.getShockedTime() > 0) {
                this.setShockedTime(this.getShockedTime() - 1);
            }
        }
    }

    @Override
    protected void actuallyHurt(DamageSource damageSource, float amount) {
        super.actuallyHurt(damageSource, amount);
        this.setShockedTime(this.random.nextInt(60, 120));
    }

    public void setShockedTime(int shockedTime) {
        this.getEntityData().set(DATA_SHOCKED_TIME, shockedTime);
    }

    public int getShockedTime() {
        return this.getEntityData().get(DATA_SHOCKED_TIME);
    }

    public boolean isShocked() {
        return this.getShockedTime() > 0;
    }
}
