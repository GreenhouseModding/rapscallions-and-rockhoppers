package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class JumpTowardsCatch extends ExtendedBehaviour<Penguin> {
    private boolean breached;

    protected boolean checkExtraStartConditions(ServerLevel level, Penguin penguin) {
        return penguin.getTimeAllowedToEat() < penguin.tickCount;
    }

    @Override
    public boolean shouldKeepRunning(Penguin penguin) {
        double yMovement = penguin.getDeltaMovement().y();
        return (!(yMovement * yMovement < 0.03F) || penguin.getXRot() == 0.0F || !(Math.abs(penguin.getXRot()) < 30.0F) || !penguin.isInWater())
                && !penguin.onGround();
    }

    @Override
    protected void start(Penguin penguin) {
        BrainUtils.setMemory(penguin, RockhoppersMemoryModuleTypes.IS_JUMPING, Unit.INSTANCE);
        FishingHook hook = BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.CAUGHT_BOBBER);
        Vec3 movementVec = hook.position().subtract(penguin.position()).normalize();
        penguin.setDeltaMovement(penguin.getDeltaMovement().add(movementVec.x() * 0.8, 0.4, movementVec.z() * 0.8));
    }

    @Override
    protected void stop(Penguin penguin) {
        this.breached = false;
        penguin.setXRot(0.0F);
        penguin.setTimeAllowedToWaterJump(Optional.of(penguin.tickCount + Mth.randomBetweenInclusive(penguin.getRandom(), 400, 600)));
        BrainUtils.clearMemories(penguin, RockhoppersMemoryModuleTypes.IS_JUMPING, RockhoppersMemoryModuleTypes.CAUGHT_BOBBER);
    }

    @Override
    public void tick(Penguin penguin) {
        boolean breached = this.breached;
        if (!breached) {
            FluidState fluidState = penguin.level().getFluidState(penguin.blockPosition());
            this.breached = fluidState.is(FluidTags.WATER);
        }

        if (this.breached && !breached) {
            penguin.playSound(penguin.getWaterJumpSound(), 1.0F, 1.0F);
        }

        if (penguin.getTimeAllowedToEat() < penguin.tickCount) {
            Optional<ItemEntity> item = penguin.level().getEntitiesOfClass(ItemEntity.class, penguin.getBoundingBox().inflate(1.25), itemEntity -> itemEntity.getItem().is(RockhoppersTags.ItemTags.PENGUIN_FOOD_ITEMS)).stream().min(Comparator.comparing(penguin::distanceTo));
            if (item.isPresent()) {
                penguin.setHungryTime(Optional.of(penguin.tickCount + 4800));
                penguin.setTimeAllowedToEat(Optional.of(penguin.tickCount + 400));
                if (penguin.getBoatToFollow() != null) {
                    penguin.incrementFishEaten();
                }
                item.get().discard();
            }
        }

        Vec3 movement = penguin.getDeltaMovement();
        if (movement.y * movement.y < 0.03F && penguin.getXRot() != 0.0F) {
            penguin.setXRot(Mth.rotLerp(0.2F, penguin.getXRot(), 0.0F));
        } else if (movement.length() > 1.0E-5F) {
            double horizontalDistance = movement.horizontalDistance();
            double atan2 = Math.atan2(-movement.y, horizontalDistance) * 180.0F / Mth.HALF_PI;
            penguin.setXRot((float)atan2);
        }
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_PRESENT), Pair.of(RockhoppersMemoryModuleTypes.CAUGHT_BOBBER, MemoryStatus.VALUE_PRESENT));
    }
}
