package dev.greenhouseteam.rapscallionsandrockhoppers.componability;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.mixin.BoatAccessor;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IBoatData {
    ResourceLocation ID = RapscallionsAndRockhoppers.asResource("boat_data");

    double HOOK_DAMPENING_FACTOR = 0.2d;

    default @Nullable Boat getProvider() {
        return null;
    }

    Set<UUID> getNextLinkedBoatUuids();
    void clearNextLinkedBoatUuids();
    Set<UUID> getPreviousLinkedBoatUuids();
    void clearPreviousLinkedBoatUuids();
    @Nullable UUID getLinkedPlayerUuid();
    default Set<Boat> getNextLinkedBoats() {
        return null;
    }
    default Set<Boat> getPreviousLinkedBoats() {
        return null;
    }
    default @Nullable Player getLinkedPlayer() {
        return null;
    }
    void setLinkedPlayer(@Nullable UUID player);
    void addNextLinkedBoat(UUID boat);
    void removeNextLinkedBoat(UUID boat);
    void addPreviousLinkedBoat(UUID boat);
    void removePreviousLinkedBoat(UUID boat);
    default boolean canLinkTo(Boat otherBoat) {
        IBoatData otherBoatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData(otherBoat);
        return !this.getPreviousLinkedBoats().contains(otherBoat) && !this.getNextLinkedBoats().contains(otherBoat) && !otherBoatData.getPreviousLinkedBoats().contains(this.getProvider()) && !otherBoatData.getNextLinkedBoats().contains(this.getProvider());
    }

    default InteractionResult handleInteractionWithBoatHook(Player player, InteractionHand interactionHand) {
        if (this.getProvider() == null) {
            return InteractionResult.PASS;
        }
        IPlayerData playerData = IRockhoppersPlatformHelper.INSTANCE.getPlayerData(player);
        if (this.getLinkedPlayer() == player) {
            this.setLinkedPlayer(null);
            playerData.removeLinkedBoat(this.getProvider().getUUID());
            this.sync();
            playerData.sync();
            if (!player.getAbilities().instabuild) {
                this.getProvider().spawnAtLocation(RockhoppersItems.BOAT_HOOK);
            }
            return InteractionResult.SUCCESS;
        }
        if (player.getItemInHand(interactionHand).is(RockhoppersItems.BOAT_HOOK) && (playerData.getLinkedBoats().isEmpty() || player.isShiftKeyDown())) {
            this.setLinkedPlayer(player.getUUID());
            playerData.addLinkedBoat(this.getProvider().getUUID());
            this.sync();
            playerData.sync();
            if (!player.getAbilities().instabuild) {
                player.getItemInHand(interactionHand).shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        if (this.getLinkedPlayer() == null && !playerData.getLinkedBoats().isEmpty()) {
            var otherBoats = playerData.getLinkedBoats();
            for (var otherBoat : otherBoats) {
                IBoatData otherBoatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData(otherBoat);
                if (!otherBoat.is(this.getProvider()) && otherBoatData.canLinkTo(this.getProvider())) {
                    if (otherBoatData.getLinkedPlayer() == player) {
                        otherBoatData.addPreviousLinkedBoat(this.getProvider().getUUID());
                        this.addNextLinkedBoat(otherBoat.getUUID());
                        otherBoatData.setLinkedPlayer(null);
                        playerData.removeLinkedBoat(otherBoat.getUUID());
                        this.sync();
                        otherBoatData.sync();
                        playerData.sync();
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    default void addBoatMovementCode() {
        if (this.getProvider() == null || this.getProvider().level().isClientSide()) return;
        if (this.getLinkedPlayer() != null) {
            var distanceBetween = this.getLinkedPlayer().distanceTo(this.getProvider());
            if (distanceBetween > 3 && distanceBetween < 10) {
                rapscallionsandrockhoppers$moveTowardsNonBoat(this.getLinkedPlayer());
            }
            if (distanceBetween > 10 || !this.getLinkedPlayer().isAlive()) {
                this.getProvider().spawnAtLocation(RockhoppersItems.BOAT_HOOK);
                this.setLinkedPlayer(null);
            }
        }
        moveTowardsBoats(this.getNextLinkedBoatUuids(), this.getPreviousLinkedBoatUuids());
    }
    @Unique
    private void moveTowardsBoats(Set<UUID> nextUuids, Set<UUID> previousUuids) {
        if (!nextUuids.isEmpty()) {
            for (Pair<UUID, Boat> next : nextUuids.stream().map(uuid1 -> {
                if (((ServerLevel)this.getProvider().level()).getEntity(uuid1) instanceof Boat boat) {
                    return Pair.of(uuid1, boat);
                }
                return Pair.of(uuid1, (Boat)null);
            }).toList()) {
                if (next.getSecond() == null || next.getSecond().isRemoved() || next.getSecond().distanceTo(this.getProvider()) > 16) {
                    if (next.getSecond() != null) {
                        IBoatData nextBoatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData(next.getSecond());
                        nextBoatData.removePreviousLinkedBoat(this.getProvider().getUUID());
                    }
                    this.getProvider().spawnAtLocation(RockhoppersItems.BOAT_HOOK);
                    this.removeNextLinkedBoat(next.getFirst());
                    return;
                }
                doBoatLinkedMovementTo(next.getSecond());
            }
        }
        if (!previousUuids.isEmpty()) {
            for (Pair<UUID, Boat> previous : previousUuids.stream().map(uuid1 -> {
                if (((ServerLevel)this.getProvider().level()).getEntity(uuid1) instanceof Boat boat) {
                    return Pair.of(uuid1, boat);
                }
                return Pair.of(uuid1, (Boat)null);
            }).toList()) {
                if (previous.getSecond() == null || previous.getSecond().isRemoved() || previous.getSecond().distanceTo(this.getProvider()) > 16) {
                    if (previous.getSecond() != null) {
                        IBoatData nextBoatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData(previous.getSecond());
                        nextBoatData.removePreviousLinkedBoat(this.getProvider().getUUID());
                    }
                    this.getProvider().spawnAtLocation(RockhoppersItems.BOAT_HOOK);
                    this.removePreviousLinkedBoat(previous.getFirst());
                    return;
                }
                doBoatLinkedMovementTo(previous.getSecond());
            }
        }

    }

    @Unique
    private void doBoatLinkedMovementTo(Boat other) {
        // METHOD 2:
        var thisPos = this.getProvider().position();
        var otherPos = other.position();
        var distanceBetween = other.distanceTo(this.getProvider());
        if (distanceBetween <= 3 || distanceBetween > 16) return;
        var distanceFactor = (distanceBetween - 3) / 7;

        // This controls the velocity of the boat, making it quicker the further away it is
        var betweenVec = thisPos.vectorTo(otherPos).scale(IBoatData.HOOK_DAMPENING_FACTOR);
        var thisDelta = betweenVec.normalize().scale(distanceBetween).scale(distanceFactor);
        // If the delta is forcing this backwards, don't do it
        // if (thisDelta.dot(this.getDeltaMovement()) < 0) return;
        thisDelta.multiply(1f, 0f, 1f);
        thisDelta.add(0f, this.getProvider().getDeltaMovement().y(), 0f);
        this.getProvider().setDeltaMovement(thisDelta);

        if (this.getProvider().getDeltaMovement().horizontalDistance() > 0.05 && (!this.getProvider().hasControllingPassenger() || !(this.getProvider().getControllingPassenger() instanceof Player))) {
            float cross = (float) (otherPos.subtract(thisPos).cross(this.getProvider().getForward()).y());
            this.getProvider().setYRot(this.getProvider().getYRot() + cross);
        }
    }
    default void rapscallionsandrockhoppers$moveTowardsNonBoat(Entity other) {
        var thisPos = this.getProvider().position();
        var otherPos = other.position();
        if (other.getDeltaMovement().horizontalDistance() > 0.05) {
            float cross = (float) (otherPos.subtract(thisPos).cross(this.getProvider().getForward()).y());
            this.getProvider().setYRot(this.getProvider().getYRot() + cross);
        }

        if (this.getProvider().level().isClientSide()) return;
        var distanceBetween = other.distanceTo(this.getProvider());
        if (distanceBetween > 3) {
            var distanceFactor = (distanceBetween - 3) / 7;
            Vec3 delta = this.getProvider().position().vectorTo(other.position()).normalize().scale(distanceBetween).scale(distanceFactor);
            this.getProvider().setDeltaMovement(delta);
        }
    }

    List<UUID> getFollowingPenguins();
    int penguinCount();
    void addFollowingPenguin(UUID penguinUUID);
    void removeFollowingPenguin(UUID penguinUUID);
    void clearFollowingPenguins();

    default void serialize(CompoundTag tag) {
        if (!this.getNextLinkedBoatUuids().isEmpty()) {
            ListTag boats = new ListTag();
            for (UUID uuid : this.getNextLinkedBoatUuids()) {
                boats.add(NbtUtils.createUUID(uuid));
            }
            tag.put("next_linked_boats", boats);
        }
        if (!this.getPreviousLinkedBoatUuids().isEmpty()) {
            ListTag boats = new ListTag();
            for (UUID uuid : this.getPreviousLinkedBoatUuids()) {
                boats.add(NbtUtils.createUUID(uuid));
            }
            tag.put("previous_linked_boats", boats);
        }
        if (this.getLinkedPlayerUuid() != null) {
            tag.putUUID("linked_player", this.getLinkedPlayerUuid());
        }
        if (this.penguinCount() > 0) {
            ListTag penguins = new ListTag();
            for (UUID uuid : this.getFollowingPenguins()) {
                penguins.add(NbtUtils.createUUID(uuid));
            }
            tag.put("following_penguins", penguins);
        }
    }

    default void deserialize(CompoundTag tag) {
        this.clearNextLinkedBoatUuids();
        if (tag.contains("next_linked_boats", Tag.TAG_LIST)) {
            ListTag boats = tag.getList("next_linked_boats", Tag.TAG_INT_ARRAY);
            for (Tag boat : boats) {
                this.addNextLinkedBoat(NbtUtils.loadUUID(boat));
            }
        }
        this.clearPreviousLinkedBoatUuids();
        if (tag.contains("previous_linked_boats", Tag.TAG_LIST)) {
            ListTag boats = tag.getList("previous_linked_boats", Tag.TAG_INT_ARRAY);
            for (Tag boat : boats) {
                this.addPreviousLinkedBoat(NbtUtils.loadUUID(boat));
            }
        }
        this.clearFollowingPenguins();
        if (tag.contains("following_penguins", Tag.TAG_LIST)) {
            ListTag penguins = tag.getList("following_penguins", Tag.TAG_INT_ARRAY);
            for (Tag penguin : penguins) {
                this.addFollowingPenguin(NbtUtils.loadUUID(penguin));
            }
        }
    }

    default void sync() {

    }
}
