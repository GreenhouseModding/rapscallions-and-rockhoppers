package dev.greenhouseteam.rapscallionsandrockhoppers.mixin;

import dev.greenhouseteam.rapscallionsandrockhoppers.componability.IBoatData;
import dev.greenhouseteam.rapscallionsandrockhoppers.componability.IPlayerData;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersItems;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.EntityGetUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(Boat.class)
public abstract class BoatMixin extends VehicleEntity {

    @Shadow protected abstract Component getTypeName();

    @Shadow protected abstract void defineSynchedData();

    public BoatMixin(EntityType<?> $$0, Level $$1) {
        super($$0, $$1);
    }


    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    public void rapscallionsandrockhoppers$addBoatHookInteraction(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        IBoatData boatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData((Boat) (Object) this);
        IPlayerData playerData = IRockhoppersPlatformHelper.INSTANCE.getPlayerData(player);
        if (boatData.getLinkedPlayer() != null) {
            boatData.setLinkedPlayer(null);
            playerData.removeLinkedBoat(this.getUUID());
            boatData.sync();
            playerData.sync();
            if (!player.getAbilities().instabuild) {
                this.spawnAtLocation(RockhoppersItems.BOAT_HOOK);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }
        if (player.getItemInHand(interactionHand).is(RockhoppersItems.BOAT_HOOK) && (playerData.getLinkedBoats().isEmpty() || player.isShiftKeyDown())) {
            boatData.setLinkedPlayer(player.getUUID());
            playerData.addLinkedBoat(this.getUUID());
            boatData.sync();
            playerData.sync();
            if (!player.getAbilities().instabuild) {
                player.getItemInHand(interactionHand).shrink(1);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }
        if (boatData.getLinkedPlayer() == null && playerData.getLinkedBoats() != null) {
            var otherBoats = playerData.getLinkedBoats();
            for (var otherBoat : otherBoats) {
                IBoatData otherBoatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData(otherBoat);
                if (!otherBoat.is(this) && otherBoatData.canLinkTo((Boat) (Object) this)) {
                    if (otherBoatData.getLinkedPlayer() == player) {
                        otherBoatData.setPreviousLinkedBoat(this.getUUID());
                        boatData.setNextLinkedBoat(otherBoat.getUUID());
                        otherBoatData.setLinkedPlayer(null);
                        playerData.removeLinkedBoat(otherBoat.getUUID());
                        boatData.sync();
                        otherBoatData.sync();
                        playerData.sync();
                        cir.setReturnValue(InteractionResult.SUCCESS);
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void rapscallionsandrockhoppers$addBoatMovementCode(CallbackInfo ci) {
        IBoatData boatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData((Boat)(Object)this);
        if (boatData.getLinkedPlayer() != null) {
            var distanceBetween = boatData.getLinkedPlayer().distanceTo((Boat)(Object)this);
            if (distanceBetween > 3 && distanceBetween < 10) {
                rapscallionsandrockhoppers$moveTowards(boatData.getLinkedPlayer());
            }
            if (distanceBetween > 10 || !boatData.getLinkedPlayer().isAlive()) {
                boatData.setLinkedPlayer(null);
                this.spawnAtLocation(RockhoppersItems.BOAT_HOOK);
            }
        }
        rapscallionsandrockhoppers$moveTowardsBoats(boatData.getNextLinkedBoatUuid(), boatData.getPreviousLinkedBoatUuid(), boatData.getNextLinkedBoat(), boatData.getPreviousLinkedBoat());
    }

    @Unique
    private void rapscallionsandrockhoppers$moveTowardsBoats(UUID nextUuid, UUID previousUuid, Boat next, Boat previous) {
        IBoatData boatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData((Boat)(Object)this);
        if (nextUuid != null) {
            if (next == null || next.isRemoved() && next.distanceTo((Boat)(Object)this) > 16) {
                if (next != null) {
                    IBoatData nextBoatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData((Boat) (Object) this);
                    nextBoatData.setPreviousLinkedBoat(null);
                }
                boatData.setNextLinkedBoat(null);
                this.spawnAtLocation(RockhoppersItems.BOAT_HOOK);
                return;
            }
            rapscallionsandrockhoppers$doBoatLinkedMovementTo(next);
        }
        if (previousUuid != null) {
            if (previous == null || previous.isRemoved() && previous.distanceTo((Boat)(Object)this) > 16) {
                if (previous != null) {
                    IBoatData nextBoatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData((Boat)(Object)this);
                    nextBoatData.setNextLinkedBoat(null);
                }
                boatData.setPreviousLinkedBoat(null);
                this.spawnAtLocation(RockhoppersItems.BOAT_HOOK);
                return;
            }
            rapscallionsandrockhoppers$doBoatLinkedMovementTo(previous);
        }

    }

    @Unique
    private void rapscallionsandrockhoppers$doBoatLinkedMovementTo(Boat other) {
        // METHOD 2:
        var thisPos = this.position();
        var otherPos = other.position();
        var distanceBetween = other.distanceTo((Boat)(Object)this);
        if (distanceBetween <= 3 || distanceBetween > 16) return;
        var distanceFactor = (distanceBetween - 3) / 7;

        if (this.level().isClientSide()) return;
        // This controls the velocity of the boat, making it quicker the further away it is
        var betweenVec = thisPos.vectorTo(otherPos).scale(IBoatData.HOOK_DAMPENING_FACTOR);
        var thisDelta = betweenVec.normalize().scale(distanceBetween).scale(distanceFactor);
        // If the delta is forcing this backwards, don't do it
        // if (thisDelta.dot(this.getDeltaMovement()) < 0) return;
        thisDelta.multiply(1f, 0f, 1f);
        thisDelta.add(0f, this.getDeltaMovement().y(), 0f);
        this.setDeltaMovement(thisDelta);

        if (this.getDeltaMovement().horizontalDistance() > 0.05 && (!this.hasControllingPassenger() || !(this.getControllingPassenger() instanceof Player))) {
            float cross = (float) (otherPos.subtract(thisPos).cross(this.getForward()).y());
            this.setYRot(this.getYRot() + cross);
            this.setYHeadRot(this.getYHeadRot() + cross);
            this.clampRotation(this);
        }
    }

    @Unique
    private void rapscallionsandrockhoppers$moveTowards(Entity other) {
        var thisPos = this.position();
        var otherPos = other.position();
        if (other.getDeltaMovement().horizontalDistance() > 0.05) {
            float rotation = (float) (otherPos.subtract(thisPos).cross(this.getForward()).y() * 0.02);
            this.setYRot(this.getYRot() + rotation);
        }

        if (this.level().isClientSide()) return;
        var distanceBetween = other.distanceTo((Boat) (Object) this);
        if (distanceBetween > 3) {
            var distanceFactor = (distanceBetween - 3) / 7;
            Vec3 delta = this.position().vectorTo(other.position()).normalize().scale(distanceBetween).scale(distanceFactor);
            this.setDeltaMovement(delta);
        }
    }

    @Shadow public abstract Direction getMotionDirection();

    @Shadow private float deltaRotation;

    @Shadow @Nullable public abstract LivingEntity getControllingPassenger();

    @Shadow protected abstract void clampRotation(Entity p_38322_);

    @ModifyArg(method = "controlBoat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private Vec3 rapscallionsandrockhoppers$controlBoat(Vec3 original) {
        IBoatData boatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData((Boat)(Object)this);
        if (boatData.penguinCount() > 0 && boatData.getFollowingPenguins().stream().anyMatch(uuid -> {
            Entity entity = EntityGetUtil.getEntityFromUuid(this.level(), uuid);
            return entity != null && entity.isInWater();
        })) {
            if (original.horizontalDistance() > 0.05) {
                double gaussianX = ((EntityAccessor)this).rapscallionsandrockhoppers$getRandom().nextGaussian() * 0.02;
                double gaussianY = ((EntityAccessor)this).rapscallionsandrockhoppers$getRandom().nextGaussian() * 0.02;
                double gaussianZ = ((EntityAccessor)this).rapscallionsandrockhoppers$getRandom().nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.GLOW, this.getRandomX(1.0), this.getY(0.5), this.getRandomZ(1.0), this.getMotionDirection().getOpposite().getStepX() * gaussianX, gaussianY, this.getMotionDirection().getOpposite().getStepZ() * gaussianZ);
            }
            return original.multiply(1.025F, 0.0F, 1.025F);
        }
        return original;
    }
}
