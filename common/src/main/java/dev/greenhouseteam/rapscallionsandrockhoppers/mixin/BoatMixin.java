package dev.greenhouseteam.rapscallionsandrockhoppers.mixin;

import dev.greenhouseteam.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.EntityGetUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Boat.class)
public abstract class BoatMixin extends VehicleEntity {

    @Shadow protected abstract Component getTypeName();

    @Shadow protected abstract void defineSynchedData();

    public BoatMixin(EntityType<?> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Shadow public abstract Direction getMotionDirection();

    @Shadow @Nullable public abstract LivingEntity getControllingPassenger();

    @Shadow protected abstract Boat.Status getStatus();

    @Inject(method = "tick", at = @At("HEAD"))
    private void rapscallionsandrockhoppers$tickMovement(CallbackInfo ci) {
        IRockhoppersPlatformHelper.INSTANCE.getBoatData((Boat)(Object)this).addBoatMovementCode();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;controlBoat()V", shift = At.Shift.BY, by = 2))
    private void rapscallionsandrockhoppers$addPenguinSpeedBonus(CallbackInfo ci) {
        BoatLinksAttachment boatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData((Boat)(Object)this);
        if (this.getStatus().equals(Boat.Status.IN_WATER) && boatData.penguinCount() > 0 && boatData.getFollowingPenguins().stream().anyMatch(uuid -> {
            Entity entity = EntityGetUtil.getEntityFromUuid(this.level(), uuid);
            return entity != null && entity.isInWater();
        })) {
            if (this.getDeltaMovement().horizontalDistance() > 0.05) {
                double gaussianX = ((EntityAccessor)this).rapscallionsandrockhoppers$getRandom().nextGaussian() * 0.02;
                double gaussianY = ((EntityAccessor)this).rapscallionsandrockhoppers$getRandom().nextGaussian() * 0.02;
                double gaussianZ = ((EntityAccessor)this).rapscallionsandrockhoppers$getRandom().nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.GLOW, this.getRandomX(1.0), this.getY(0.5), this.getRandomZ(1.0), this.getMotionDirection().getOpposite().getStepX() * gaussianX, gaussianY, this.getMotionDirection().getOpposite().getStepZ() * gaussianZ);
            }
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.025, 1.0, 1.025));
        }
    }
}
