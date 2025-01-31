package dev.greenhouseteam.rapscallionsandrockhoppers.attachment;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.mixin.BoatAccessor;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.RockhoppersPlatformHelper;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersItems;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.EntityGetUtil;
import net.minecraft.core.UUIDUtil;
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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BoatLinksAttachment {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("boat_links");

    private static final double HOOK_DAMPENING_FACTOR = 0.2D;
    private Set<UUID> penguins;
    private Set<UUID> nextLinkedBoats;
    private Set<UUID> previousLinkedBoats;

    private @Nullable UUID linkedPlayer;
    private @Nullable Boat instance;

    public static final Codec<BoatLinksAttachment> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            UUIDUtil.CODEC_SET.fieldOf("penguins").forGetter(BoatLinksAttachment::getFollowingPenguins),
            UUIDUtil.CODEC_SET.fieldOf("linked_boats_after").forGetter(BoatLinksAttachment::getNextLinkedBoatUuids),
            UUIDUtil.CODEC_SET.fieldOf("linked_boats_before").forGetter(BoatLinksAttachment::getPreviousLinkedBoatUuids)
    ).apply(inst, BoatLinksAttachment::new));

    public BoatLinksAttachment() {
        this.penguins = new HashSet<>();
        this.nextLinkedBoats = new HashSet<>();
        this.previousLinkedBoats = new HashSet<>();
    }

    public BoatLinksAttachment(Set<UUID> penguins, Set<UUID> nextLinkedBoats, Set<UUID> previousLinkedBoats) {
        this.penguins = penguins;
        this.nextLinkedBoats = nextLinkedBoats;
        this.previousLinkedBoats = previousLinkedBoats;
    }

    public void setFrom(BoatLinksAttachment other) {
        this.penguins = other.penguins;
        this.nextLinkedBoats = other.nextLinkedBoats;
        this.previousLinkedBoats = other.previousLinkedBoats;
    }

    public Set<UUID> getNextLinkedBoatUuids() {
        return this.nextLinkedBoats;
    }

    public Set<UUID> getPreviousLinkedBoatUuids() {
        return this.previousLinkedBoats;
    }

    public void clearNextLinkedBoatUuids() {
        this.nextLinkedBoats.clear();
    }

    public void clearPreviousLinkedBoatUuids() {
        this.previousLinkedBoats.clear();
    }

    public @Nullable UUID getLinkedPlayerUuid() {
        return linkedPlayer;
    }

    public void setLinkedPlayer(@Nullable UUID player) {
        this.linkedPlayer = player;
    }

    public void addNextLinkedBoat(@Nullable UUID boat) {
        this.nextLinkedBoats.add(boat);
    }

    public void removeNextLinkedBoat(@Nullable UUID boat) {
        this.nextLinkedBoats.remove(boat);
    }

    public void addPreviousLinkedBoat(@Nullable UUID boat) {
        this.previousLinkedBoats.add(boat);
    }

    public void removePreviousLinkedBoat(@Nullable UUID boat) {
        this.previousLinkedBoats.remove(boat);
    }

    public Set<UUID> getFollowingPenguins() {
        return Set.copyOf(this.penguins);
    }

    public int penguinCount() {
        return this.penguins.size();
    }

    public void addFollowingPenguin(UUID penguinUUID) {
        this.penguins.add(penguinUUID);
    }

    public void removeFollowingPenguin(UUID penguinUUID) {
        this.penguins.remove(penguinUUID);
    }

    public void clearFollowingPenguins() {
        this.penguins.clear();
    }

    public @Nullable Boat getProvider() {
        return instance;
    }

    public void setProvider(Boat boat) {
        if (instance != null)
            return;
        instance = boat;
    }

    public Set<Boat> getNextLinkedBoats() {
        if (getProvider() != null) {
            return this.getNextLinkedBoatUuids().stream().map(uuid -> {
                Entity entity = EntityGetUtil.getEntityFromUuid(this.getProvider().level(), uuid);
                if (entity instanceof Boat boat) {
                    return boat;
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toSet());
        }
        return Set.of();
    }

    public Set<Boat> getPreviousLinkedBoats() {
        if (getProvider() != null) {
            return this.getPreviousLinkedBoatUuids().stream().map(uuid -> {
                Entity entity = EntityGetUtil.getEntityFromUuid(this.getProvider().level(), uuid);
                if (entity instanceof Boat boat) {
                    return boat;
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toSet());
        }
        return Set.of();
    }

    public @Nullable Player getLinkedPlayer() {
        if (getProvider() != null) {
            Entity entity = EntityGetUtil.getEntityFromUuid(this.getProvider().level(), linkedPlayer);
            if (entity instanceof Player player) {
                return player;
            }
        }
        return null;
    }

    public boolean canLinkTo(Boat otherBoat) {
            BoatLinksAttachment otherBoatData = RapscallionsAndRockhoppers.getHelper().getBoatData(otherBoat);
        return !this.getPreviousLinkedBoats().contains(otherBoat) && !this.getNextLinkedBoats().contains(otherBoat) && !otherBoatData.getPreviousLinkedBoats().contains(this.getProvider()) && !otherBoatData.getNextLinkedBoats().contains(this.getProvider());
    }

    public InteractionResult handleInteractionWithBoatHook(Player player, InteractionHand interactionHand) {
        if (this.getProvider() == null) {
            return InteractionResult.PASS;
        }
        PlayerLinksAttachment playerData = RapscallionsAndRockhoppers.getHelper().getPlayerData(player);
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
        if (player.getItemInHand(interactionHand).is(RockhoppersItems.BOAT_HOOK) && (playerData.getLinkedBoats().isEmpty() && this.getPreviousLinkedBoats().isEmpty() && this.getNextLinkedBoats().isEmpty() || player.isShiftKeyDown())) {
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
                BoatLinksAttachment otherBoatData = RapscallionsAndRockhoppers.getHelper().getBoatData(otherBoat);
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

    public void addBoatMovementCode() {
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
                        BoatLinksAttachment nextBoatData = RapscallionsAndRockhoppers.getHelper().getBoatData(next.getSecond());
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
                        BoatLinksAttachment nextBoatData = RapscallionsAndRockhoppers.getHelper().getBoatData(previous.getSecond());
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
        var betweenVec = thisPos.vectorTo(otherPos).scale(BoatLinksAttachment.HOOK_DAMPENING_FACTOR);
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
    public void rapscallionsandrockhoppers$moveTowardsNonBoat(Entity other) {
        var thisPos = this.getProvider().position();
        var otherPos = other.position();
        if (other.getDeltaMovement().horizontalDistance() > 0.05) {
            float cross = (float) (otherPos.subtract(thisPos).cross(this.getProvider().getForward()).y());
            this.getProvider().setYRot(this.getProvider().getYRot() + cross);
        }

        if (this.getProvider().level().isClientSide()) return;
        var distanceBetween = other.position().multiply(1.0, 0.0, 1.0).distanceTo(this.getProvider().position().multiply(1.0, 0.0, 1.0));
        if (distanceBetween > 3) {
            var distanceFactor = (distanceBetween - 3) / 7;
            Vec3 delta = this.getProvider().position().vectorTo(other.position()).normalize().scale(distanceBetween).scale(distanceFactor);
            if (((BoatAccessor)this.getProvider()).rapscallionsandrockhoppers$getStatus() != null && ((BoatAccessor)this.getProvider()).rapscallionsandrockhoppers$getStatus().equals(Boat.Status.IN_WATER) && delta.y() < 0.0)
                delta = new Vec3(delta.x(), this.getProvider().getDeltaMovement().y(), delta.z());
            this.getProvider().setDeltaMovement(delta);
        }
    }

    public void deserializeLegacyData(CompoundTag tag) {
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

    public void sync() {
        if (getProvider() == null || getProvider().level().isClientSide())
            return;
        RapscallionsAndRockhoppers.getHelper().syncBoatData(getProvider());
    }
}
