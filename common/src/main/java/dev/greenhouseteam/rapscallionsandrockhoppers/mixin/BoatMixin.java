package dev.greenhouseteam.rapscallionsandrockhoppers.mixin;

import dev.greenhouseteam.rapscallionsandrockhoppers.componability.IBoatData;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Boat.class)
public abstract class BoatMixin extends VehicleEntity {

    @Shadow protected abstract Component getTypeName();

    @Shadow protected abstract void defineSynchedData();

    public BoatMixin(EntityType<?> $$0, Level $$1) {
        super($$0, $$1);
    }


    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    public void rapscallionsandrockhoppers$addBoatHookInteraction(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.getItemInHand(interactionHand).is(RockhoppersItems.BOAT_HOOK)) {
            IBoatData boatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData((Boat)(Object)this);
            if (boatData.getLinkedPlayer() == null) {
                var otherBoats = this.level().getEntitiesOfClass(Boat.class, this.getBoundingBox().inflate(3));
                for (var otherBoat : otherBoats) {
                    IBoatData otherBoatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData((Boat)(Object)this);
                    if (!otherBoat.is(this) && otherBoatData.canLinkTo((Boat)(Object)this)) {
                        if (otherBoatData.getLinkedPlayer() == player) {
                            otherBoatData.setPreviousLinkedBoat((Boat)(Object)this);
                            boatData.setNextLinkedBoat(otherBoat);
                            otherBoatData.setLinkedPlayer(null);
                            if (IRockhoppersPlatformHelper.INSTANCE.isDevelopmentEnvironment()) {
                                player.sendSystemMessage(getTypeName().copy().append(" linked to " + otherBoat.getDropItem().getDescriptionId()));
                                player.sendSystemMessage(Component.translatable("Distance between: " + otherBoat.distanceTo((Boat)(Object)this)));
                            }
                            cir.setReturnValue(InteractionResult.SUCCESS);
                            return;
                        }
                    }
                }
                boatData.setLinkedPlayer(player);
                cir.setReturnValue(InteractionResult.SUCCESS);

            } else {
                cir.setReturnValue(InteractionResult.PASS);
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
            if (distanceBetween > 10) {
                boatData.setLinkedPlayer(null);
                this.spawnAtLocation(RockhoppersItems.BOAT_HOOK);
            }
        }
        rapscallionsandrockhoppers$moveTowardsBoats(boatData.getNextLinkedBoat(), boatData.getPreviousLinkedBoat());
    }

    private void rapscallionsandrockhoppers$moveTowardsBoats(Boat next, Boat previous) {
        IBoatData boatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData((Boat)(Object)this);
        if (next != null) {
            if (next.isAlive()) {
                rapscallionsandrockhoppers$doBoatLinkedMovementTo(next);
            } else {
                IBoatData nextBoatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData((Boat)(Object)this);
                nextBoatData.setPreviousLinkedBoat(null);
                boatData.setNextLinkedBoat(null);
            }
        }
        if (previous != null) {
            if (previous.isAlive()) {
                rapscallionsandrockhoppers$doBoatLinkedMovementTo(previous);
            } else {
                IBoatData previousBoatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData((Boat)(Object)this);
                previousBoatData.setNextLinkedBoat(null);
                boatData.setPreviousLinkedBoat(null);
            }
        }


    }

    private void rapscallionsandrockhoppers$doBoatLinkedMovementTo(Boat other) {
        // METHOD 2:
        var thisPos = this.position();
        var otherPos = other.position();
        var distanceBetween = other.distanceTo((Boat)(Object)this);
        if (distanceBetween <= 3 || distanceBetween > 10) return;
        var distanceFactor = (distanceBetween - 3) / 7;
        // This controls the velocity of the boat, making it quicker the further away it is
        var betweenVec = thisPos.vectorTo(otherPos).scale(IBoatData.HOOK_DAMPENING_FACTOR);
        var thisDelta = betweenVec.normalize().scale(distanceBetween).scale(distanceFactor);
        // If the delta is forcing this backwards, don't do it
        if (thisDelta.dot(this.getDeltaMovement()) < 0) return;
        thisDelta.multiply(1f, 0f, 1f);
        thisDelta.add(0f, this.getDeltaMovement().y(), 0f);
        this.setDeltaMovement(thisDelta);
    }

    private void rapscallionsandrockhoppers$moveTowards(Entity other) {
        var distanceBetween = other.distanceTo((Boat) (Object) this);
        if (distanceBetween > 3) {
            if (this.getDeltaMovement().length() > other.getDeltaMovement().length()) {
                Vec3 thisDelta = this.getDeltaMovement();
                // Vec3 otherDelta = other.getDeltaMovement();
                other.setDeltaMovement(thisDelta);
            } else {
                // Vec3 thisDelta = this.getDeltaMovement();
                Vec3 otherDelta = other.getDeltaMovement();
                this.setDeltaMovement(otherDelta);
            }
        }
    }

    @Shadow public abstract Direction getMotionDirection();

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
