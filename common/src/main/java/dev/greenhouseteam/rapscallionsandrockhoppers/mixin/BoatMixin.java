package dev.greenhouseteam.rapscallionsandrockhoppers.mixin;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.BoatLink;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersItems;
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
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Boat.class)
public abstract class BoatMixin extends VehicleEntity implements BoatLink {

    @Shadow private float deltaRotation;

    @Shadow protected abstract Component getTypeName();

    @Shadow public abstract Item getDropItem();

    @Shadow protected abstract void defineSynchedData();

    private Vec3 linkedVelocity = Vec3.ZERO;

    @Unique
    private @Nullable Boat nextLinkedBoat;
    @Unique
    private @Nullable Boat previousLinkedBoat;

    @Unique
    private @Nullable Player linkedPlayer;

    public BoatMixin(EntityType<?> $$0, Level $$1) {
        super($$0, $$1);
    }


    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    public void addBoatHookInteraction(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.getItemInHand(interactionHand).is(RockhoppersItems.BOAT_HOOK)) {
            if (linkedPlayer == null) {
                var otherBoats = this.level().getEntitiesOfClass(Boat.class, this.getBoundingBox().inflate(3));
                for (var otherBoat : otherBoats) {
                    if (!otherBoat.is(this) && ((BoatLink)otherBoat).canLinkTo((Boat)(Object)this)) {
                        if (((BoatLink)otherBoat).getLinkedPlayer() == player) {
                            ((BoatLink)otherBoat).setPreviousLinkedBoat((Boat)(Object)this);
                            setNextLinkedBoat(otherBoat);
                            ((BoatLink)otherBoat).setLinkedPlayer(null);
                            if (IRockhoppersPlatformHelper.INSTANCE.isDevelopmentEnvironment()) {
                                player.sendSystemMessage(getTypeName().copy().append(" linked to " + otherBoat.getDropItem().getDescriptionId()));
                                player.sendSystemMessage(Component.translatable("Distance between: " + otherBoat.distanceTo((Boat)(Object)this)));
                            }
                            cir.setReturnValue(InteractionResult.SUCCESS);
                            return;
                        }
                    }
                }
                linkedPlayer = player;
                cir.setReturnValue(InteractionResult.SUCCESS);

            } else {
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void addBoatMovementCode(CallbackInfo ci) {
        if (linkedPlayer != null) {
            var distanceBetween = linkedPlayer.distanceTo((Boat)(Object)this);
            if (distanceBetween > 3 && distanceBetween < 10) {
//                double x = (linkedPlayer.getX() - this.getX()) / (double)distanceBetween;
//                double y = (linkedPlayer.getY() - this.getY()) / (double)distanceBetween;
//                double z = (linkedPlayer.getZ() - this.getZ()) / (double)distanceBetween;
//                double xTowards = Math.copySign(x * x * HOOK_DAMPENING_FACTOR, x);
//                double yTowards = Math.copySign(y * y * HOOK_DAMPENING_FACTOR, y);
//                double zTowards = Math.copySign(z * z * HOOK_DAMPENING_FACTOR, z);
//                this.setDeltaMovement(
//                        this.getDeltaMovement().add(xTowards, yTowards, zTowards)
//                );
                moveTowards(linkedPlayer);
            }
            if (distanceBetween > 10) {
                linkedPlayer = null;
                this.spawnAtLocation(RockhoppersItems.BOAT_HOOK);
            }
        }
//        if (nextLinkedBoat != null) {
//            moveTowards(nextLinkedBoat);
//        }
//        if (previousLinkedBoat != null) {
//            moveTowards(previousLinkedBoat);
//        }
        moveTowardsBoats(nextLinkedBoat, previousLinkedBoat);
    }

    private void moveTowardsBoats(Boat next, Boat previous) {
        if (next != null) {
            if (next.isAlive()) {
                doBoatLinkedMovementTo(next);
            } else {
                ((BoatLink)next).setPreviousLinkedBoat(null);
                nextLinkedBoat = null;
            }
        }
        if (previous != null) {
            if (previous.isAlive()) {
                doBoatLinkedMovementTo(previous);
            } else {
                ((BoatLink)previous).setNextLinkedBoat(null);
                previousLinkedBoat = null;
            }
        }


    }

    private void doBoatLinkedMovementTo(Boat other) {


        // METHOD 1:
//        if (this.getDeltaMovement().add(other.getDeltaMovement()).lengthSqr() <= this.getDeltaMovement().lengthSqr()) {
//            var distanceBetween = other.distanceTo((Boat)(Object)this);
//            if (distanceBetween <= 3) return;
//            // this means the other boat is moving faster than this boat, and is too far away, so we should move towards it
////            this.getDeltaMovement().relative()
////            double x = (other.getX() - this.getX()) / (double)distanceBetween;
////            double y = (other.getY() - this.getY()) / (double)distanceBetween;
////            double z = (other.getZ() - this.getZ()) / (double)distanceBetween;
////            double xTowards = Math.copySign(x * x * HOOK_DAMPENING_FACTOR, x);
////            double yTowards = Math.copySign(y * y * HOOK_DAMPENING_FACTOR, y);
////            double zTowards = Math.copySign(z * z * HOOK_DAMPENING_FACTOR, z);
////            this.setDeltaMovement(
////                    this.getDeltaMovement().add(xTowards, yTowards, zTowards)
////            );
//        }


        // METHOD 2:
        var thisPos = this.position();
        var otherPos = other.position();
        var distanceBetween = other.distanceTo((Boat)(Object)this);
        if (distanceBetween <= 3 || distanceBetween > 10) return;
        var distanceFactor = (distanceBetween - 3) / 7;
        // This controls the velocity of the boat, making it quicker the further away it is
        var betweenVec = thisPos.vectorTo(otherPos).scale(HOOK_DAMPENING_FACTOR);
        var thisDelta = betweenVec.normalize().scale(distanceBetween).scale(distanceFactor);
        // If the delta is forcing this backwards, don't do it
        if (thisDelta.dot(this.getDeltaMovement()) < 0) return;
        thisDelta.multiply(1f, 0f, 1f);
        thisDelta.add(0f, this.getDeltaMovement().y(), 0f);
        this.setDeltaMovement(thisDelta);

    }

    private void moveTowards(Entity other) {
        var distanceBetween = other.distanceTo((Boat)(Object)this);
        // METHOD 1:
//        if (distanceBetween > 3) {
//            double x = (other.getX() - this.getX()) / (double)distanceBetween;
//            double y = (other.getY() - this.getY()) / (double)distanceBetween;
//            double z = (other.getZ() - this.getZ()) / (double)distanceBetween;
//            double xTowards = Math.copySign(x * x * HOOK_DAMPENING_FACTOR, x);
//            double yTowards = Math.copySign(y * y * HOOK_DAMPENING_FACTOR, y);
//            double zTowards = Math.copySign(z * z * HOOK_DAMPENING_FACTOR, z);
//            this.setDeltaMovement(
//                    this.getDeltaMovement().add(xTowards, yTowards, zTowards)
//            );
//        }
//        if (distanceBetween > 3) {
//            Vec3 towards = other.position().subtract(this.position()).normalize().scale(0.1);
//            this.setDeltaMovement(
//                    this.getDeltaMovement().add(towards)
//            );

//            if (this.getDeltaMovement().length() < other.getDeltaMovement().length() && distanceBetween > 3) {
//                System.out.println("Distance between: " + distanceBetween);
//                double x = (other.getX() - this.getX()) / (double)distanceBetween;
//                double y = (other.getY() - this.getY()) / (double)distanceBetween;
//                double z = (other.getZ() - this.getZ()) / (double)distanceBetween;
//                double xTowards = Math.copySign(x * x * HOOK_DAMPENING_FACTOR, x);
//                double yTowards = Math.copySign(y * y * HOOK_DAMPENING_FACTOR, y);
//                double zTowards = Math.copySign(z * z * HOOK_DAMPENING_FACTOR, z);
//
//                this.setDeltaMovement(
//                        this.getDeltaMovement().add(xTowards, yTowards, zTowards)
//                );
//            }

        if (distanceBetween > 3) {
            if (this.getDeltaMovement().length() > other.getDeltaMovement().length()) {
                Vec3 thisDelta = this.getDeltaMovement();
                Vec3 otherDelta = other.getDeltaMovement();
                other.setDeltaMovement(thisDelta);
            } else {
                Vec3 thisDelta = this.getDeltaMovement();
                Vec3 otherDelta = other.getDeltaMovement();
                this.setDeltaMovement(otherDelta);
            }
        }

//        }
    }

    @Override
    public @Nullable Boat getNextLinkedBoat() {
        return nextLinkedBoat;

    }

    @Override
    public @Nullable Boat getPreviousLinkedBoat() {
        return previousLinkedBoat;
    }

    @Override
    public @Nullable Player getLinkedPlayer() {
        return linkedPlayer;
    }

    @Override
    public void setNextLinkedBoat(Boat boat) {
        this.nextLinkedBoat = boat;
    }

    @Override
    public void setPreviousLinkedBoat(Boat boat) {
        this.previousLinkedBoat = boat;
    }

    @Override
    public void setLinkedPlayer(Player linkedPlayer) {
        this.linkedPlayer = linkedPlayer;
    }
}
