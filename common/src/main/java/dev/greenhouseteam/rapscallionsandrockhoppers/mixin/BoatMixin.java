package dev.greenhouseteam.rapscallionsandrockhoppers.mixin;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.componability.IBoatData;
import dev.greenhouseteam.rapscallionsandrockhoppers.componability.IPlayerData;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersItems;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.EntityGetUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import java.util.List;
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
                        otherBoatData.addPreviousLinkedBoat(this.getUUID());
                        boatData.addNextLinkedBoat(otherBoat.getUUID());
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
        if (this.level().isClientSide()) return;
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
        rapscallionsandrockhoppers$moveTowardsBoats(boatData.getNextLinkedBoatUuids(), boatData.getPreviousLinkedBoatUuids());
    }

    @Unique
    private void rapscallionsandrockhoppers$moveTowardsBoats(List<UUID> nextUuids, List<UUID> previousUuids) {
        IBoatData boatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData((Boat)(Object)this);
        if (!nextUuids.isEmpty()) {
            for (Pair<UUID, Boat> next : nextUuids.stream().map(uuid1 -> {
                if (((ServerLevel)this.level()).getEntity(uuid1) instanceof Boat boat) {
                    return Pair.of(uuid1, boat);
                }
                return Pair.of(uuid1, (Boat)null);
            }).toList()) {
                if (next.getSecond() == null || next.getSecond().isRemoved() || next.getSecond().distanceTo((Boat)(Object)this) > 16) {
                    if (next.getSecond() != null) {
                        IBoatData nextBoatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData(next.getSecond());
                        nextBoatData.removePreviousLinkedBoat(this.getUUID());
                    }
                    boatData.removeNextLinkedBoat(next.getFirst());
                    this.spawnAtLocation(RockhoppersItems.BOAT_HOOK);
                    return;
                }
                rapscallionsandrockhoppers$doBoatLinkedMovementTo(next.getSecond());
            }
        }
        if (!previousUuids.isEmpty()) {
            for (Pair<UUID, Boat> previous : previousUuids.stream().map(uuid1 -> {
                if (((ServerLevel)this.level()).getEntity(uuid1) instanceof Boat boat) {
                    return Pair.of(uuid1, boat);
                }
                return Pair.of(uuid1, (Boat)null);
            }).toList()) {
                if (previous.getSecond() == null || previous.getSecond().isRemoved() || previous.getSecond().distanceTo((Boat)(Object)this) > 16) {
                    if (previous.getSecond() != null) {
                        IBoatData nextBoatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData(previous.getSecond());
                        nextBoatData.removePreviousLinkedBoat(this.getUUID());
                    }
                    boatData.removeNextLinkedBoat(previous.getFirst());
                    this.spawnAtLocation(RockhoppersItems.BOAT_HOOK);
                    return;
                }
                rapscallionsandrockhoppers$doBoatLinkedMovementTo(previous.getSecond());
            }
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

    @Shadow private boolean inputDown;

    @Shadow private boolean inputLeft;

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
