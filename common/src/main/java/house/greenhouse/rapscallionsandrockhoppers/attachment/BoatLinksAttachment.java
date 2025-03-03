package house.greenhouse.rapscallionsandrockhoppers.attachment;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.entity.BoatHookFenceKnotEntity;
import house.greenhouse.rapscallionsandrockhoppers.mixin.BoatAccessor;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersItems;
import house.greenhouse.rapscallionsandrockhoppers.util.EntityGetUtil;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class BoatLinksAttachment {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("boat_links");

    private static final double HOOK_DAMPENING_FACTOR = 0.2D;
    private final Set<UUID> nextLinkedBoats;
    private final Set<UUID> previousLinkedBoats;

    private Optional<UUID> linkedPlayerUuid = Optional.empty();
    private Optional<UUID> hookKnotUuid = Optional.empty();

    private long lastMovementTime;

    public static final Codec<BoatLinksAttachment> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            UUIDUtil.CODEC_SET.fieldOf("linked_boats_after").forGetter(BoatLinksAttachment::getNextLinkedBoatUuids),
            UUIDUtil.CODEC_SET.fieldOf("linked_boats_before").forGetter(BoatLinksAttachment::getPreviousLinkedBoatUuids),
            UUIDUtil.CODEC.optionalFieldOf("linked_player").forGetter(BoatLinksAttachment::getLinkedPlayerUuid),
            UUIDUtil.CODEC.optionalFieldOf("hook_knot_uuid").forGetter(BoatLinksAttachment::getHookKnotUuid)
    ).apply(inst, BoatLinksAttachment::new));

    public BoatLinksAttachment() {
        this.nextLinkedBoats = new HashSet<>();
        this.previousLinkedBoats = new HashSet<>();
    }

    public BoatLinksAttachment(Set<UUID> nextLinkedBoats, Set<UUID> previousLinkedBoats, Optional<UUID> linkedPlayerUuid, Optional<UUID> hookKnotUuid) {
        this.nextLinkedBoats = nextLinkedBoats;
        this.previousLinkedBoats = previousLinkedBoats;
        this.linkedPlayerUuid = linkedPlayerUuid;
        this.hookKnotUuid = hookKnotUuid;
    }

    public void setFrom(BoatLinksAttachment other) {
        nextLinkedBoats.clear();
        previousLinkedBoats.clear();
        nextLinkedBoats.addAll(other.nextLinkedBoats);
        previousLinkedBoats.addAll(other.previousLinkedBoats);
        linkedPlayerUuid = other.linkedPlayerUuid;
        hookKnotUuid = other.hookKnotUuid;
    }

    public boolean hasData() {
        return !previousLinkedBoats.isEmpty() || !nextLinkedBoats.isEmpty() || hookKnotUuid.isPresent() || linkedPlayerUuid != null;
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

    public Optional<UUID> getLinkedPlayerUuid() {
        return linkedPlayerUuid;
    }

    public void setLinkedPlayerUuid(@Nullable UUID player) {
        this.linkedPlayerUuid = Optional.ofNullable(player);
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
    
    public Optional<UUID> getHookKnotUuid() {
        return hookKnotUuid;
    }
    
    public void setHookKnotUuid(@Nullable UUID hookKnotUuid) {
        this.hookKnotUuid = Optional.ofNullable(hookKnotUuid);
    }

    public Set<Boat> getNextLinkedBoats(Level level) {
        return this.getNextLinkedBoatUuids().stream().map(uuid -> {
            Entity entity = EntityGetUtil.getEntityFromUuid(level, uuid);
            if (entity instanceof Boat boat) {
                return boat;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public Set<Boat> getPreviousLinkedBoats(Level level) {
        return this.getPreviousLinkedBoatUuids().stream().map(uuid -> {
            Entity entity = EntityGetUtil.getEntityFromUuid(level, uuid);
            if (entity instanceof Boat boat) {
                return boat;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public @Nullable Player getLinkedPlayer(Level level) {
        Entity entity = EntityGetUtil.getEntityFromUuid(level, linkedPlayerUuid.orElse(null));
        if (entity instanceof Player player) {
            return player;
        }
        return null;
    }
    
    public @Nullable BoatHookFenceKnotEntity getHookKnot(Level level) {
        Entity entity = EntityGetUtil.getEntityFromUuid(level, hookKnotUuid.orElse(null));
        if (entity instanceof BoatHookFenceKnotEntity knot) {
            return knot;
        }
        return null;
    }

    public static boolean canLinkTo(Boat boat, Boat otherBoat) {
        BoatLinksAttachment boatData = RapscallionsAndRockhoppers.getHelper().getBoatData(boat);
        BoatLinksAttachment otherBoatData = RapscallionsAndRockhoppers.getHelper().getBoatData(otherBoat);
        return !boatData.getPreviousLinkedBoats(boat.level()).contains(otherBoat) && !boatData.getNextLinkedBoats(boat.level()).contains(otherBoat) && !otherBoatData.getPreviousLinkedBoats(otherBoat.level()).contains(boat) && !otherBoatData.getNextLinkedBoats(otherBoat.level()).contains(boat);
    }

    public static InteractionResult handleInteractionWithBoatHook(Boat boat, Player player, InteractionHand interactionHand) {
        BoatLinksAttachment boatData = RapscallionsAndRockhoppers.getHelper().getBoatData(boat);
        PlayerLinksAttachment playerData = RapscallionsAndRockhoppers.getHelper().getPlayerData(player);
        if (boatData.getLinkedPlayer(boat.level()) == player) {
            boatData.setLinkedPlayerUuid(null);
            playerData.removeLinkedBoat(boat.getUUID());
            if (!boatData.hasData())
                RapscallionsAndRockhoppers.getHelper().removeBoatData(boat);
            if (!playerData.getLinkedBoatUUIDs().isEmpty())
                RapscallionsAndRockhoppers.getHelper().removePlayerData(player);
            if (!boat.level().isClientSide) {
                RapscallionsAndRockhoppers.getHelper().syncPlayerData(player);
                RapscallionsAndRockhoppers.getHelper().syncBoatData(boat);
            }
            if (!player.getAbilities().instabuild)
                boat.spawnAtLocation(new ItemStack(RockhoppersItems.BOAT_HOOK), 1.0F);
            return InteractionResult.SUCCESS;
        }
        if (player.getItemInHand(interactionHand).is(RockhoppersItems.BOAT_HOOK) && (playerData.getLinkedBoats(player.level()).isEmpty() && boatData.getPreviousLinkedBoats(boat.level()).isEmpty() && boatData.getNextLinkedBoats(boat.level()).isEmpty() || player.isShiftKeyDown())) {
            boatData.setLinkedPlayerUuid(player.getUUID());
            playerData.addLinkedBoat(boat.getUUID());
            if (!boat.level().isClientSide) {
                RapscallionsAndRockhoppers.getHelper().syncBoatData(boat);
                RapscallionsAndRockhoppers.getHelper().syncPlayerData(player);
            }
            if (!player.getAbilities().instabuild)
                player.getItemInHand(interactionHand).shrink(1);
            return InteractionResult.SUCCESS;
        }
        if (boatData.getLinkedPlayer(boat.level()) == null && !playerData.getLinkedBoats(player.level()).isEmpty()) {
            var otherBoats = playerData.getLinkedBoats(player.level());
            for (var otherBoat : otherBoats) {
                BoatLinksAttachment otherBoatData = RapscallionsAndRockhoppers.getHelper().getBoatData(otherBoat);
                if (!otherBoat.is(boat) && canLinkTo(boat, otherBoat)) {
                    if (otherBoatData.getLinkedPlayer(boat.level()) == player) {
                        otherBoatData.addPreviousLinkedBoat(boat.getUUID());
                        boatData.addNextLinkedBoat(otherBoat.getUUID());
                        otherBoatData.setLinkedPlayerUuid(null);
                        playerData.removeLinkedBoat(otherBoat.getUUID());
                        if (!boat.level().isClientSide) {
                            RapscallionsAndRockhoppers.getHelper().syncBoatData(boat);
                            RapscallionsAndRockhoppers.getHelper().syncBoatData(otherBoat);
                            RapscallionsAndRockhoppers.getHelper().syncPlayerData(player);
                        }
                        boat.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    public static void addBoatMovementCode(Boat boat) {
        if (boat == null || boat.level().isClientSide()) return;
        var data = RapscallionsAndRockhoppers.getHelper().getBoatData(boat);
        if (data.getLinkedPlayer(boat.level()) != null) {
            var distanceBetween = data.getLinkedPlayer(boat.level()).distanceTo(boat);
            if (distanceBetween < 10) {
                moveTowardsNonBoat(boat, data.getLinkedPlayer(boat.level()));
            }
            if (distanceBetween > 10 || !data.getLinkedPlayer(boat.level()).isAlive()) {
                boat.spawnAtLocation(new ItemStack(RockhoppersItems.BOAT_HOOK), 1.0F);
                data.setLinkedPlayerUuid(null);
            }
        }
        if (data.getHookKnot(boat.level()) != null) {
            var knot = data.getHookKnot(boat.level());
            var distanceBetween = knot.distanceTo(boat);
            if (distanceBetween > 10) {
                boat.spawnAtLocation(new ItemStack(RockhoppersItems.BOAT_HOOK), 1.0F);
                data.setHookKnotUuid(null);
                RapscallionsAndRockhoppers.getHelper().syncBoatData(boat);
            }
        }
        if (boat.getPaddleState(0) || boat.getPaddleState(1)) {
            data.lastMovementTime = boat.level().getGameTime();
        }
        moveTowardsBoats(boat, data.getNextLinkedBoatUuids(), data.getPreviousLinkedBoatUuids());
    }

    private static void moveTowardsBoats(Boat boat, Set<UUID> nextUuids, Set<UUID> previousUuids) {
        var data = RapscallionsAndRockhoppers.getHelper().getBoatData(boat);
        if (!nextUuids.isEmpty()) {
            for (Pair<UUID, Boat> next : nextUuids.stream().map(uuid1 -> {
                if (((ServerLevel)boat.level()).getEntity(uuid1) instanceof Boat other) {
                    return Pair.of(uuid1, other);
                }
                return Pair.of(uuid1, (Boat)null);
            }).toList()) {
                if (next.getSecond() == null || next.getSecond().isRemoved() || next.getSecond().distanceTo(boat) > 16) {
                    if (next.getSecond() != null) {
                        BoatLinksAttachment nextBoatData = RapscallionsAndRockhoppers.getHelper().getBoatData(next.getSecond());
                        nextBoatData.removePreviousLinkedBoat(boat.getUUID());
                        if (!nextBoatData.hasData())
                            RapscallionsAndRockhoppers.getHelper().removeBoatData(next.getSecond());
                        RapscallionsAndRockhoppers.getHelper().syncBoatData(next.getSecond());
                    }
                    boat.spawnAtLocation(new ItemStack(RockhoppersItems.BOAT_HOOK), 1.0F);
                    data.removeNextLinkedBoat(next.getFirst());
                    if (!data.hasData())
                        RapscallionsAndRockhoppers.getHelper().removeBoatData(boat);
                    RapscallionsAndRockhoppers.getHelper().syncBoatData(boat);
                    return;
                }
                doBoatLinkedMovementTo(boat, next.getSecond());
            }
        }
        if (!previousUuids.isEmpty()) {
            for (Pair<UUID, Boat> previous : previousUuids.stream().map(uuid1 -> {
                if (((ServerLevel)boat.level()).getEntity(uuid1) instanceof Boat previous) {
                    return Pair.of(uuid1, previous);
                }
                return Pair.of(uuid1, (Boat)null);
            }).toList()) {
                if (previous.getSecond() == null || previous.getSecond().isRemoved() || previous.getSecond().distanceTo(boat) > 16) {
                    if (previous.getSecond() != null) {
                        BoatLinksAttachment nextBoatData = RapscallionsAndRockhoppers.getHelper().getBoatData(previous.getSecond());
                        nextBoatData.removeNextLinkedBoat(boat.getUUID());
                        if (!nextBoatData.hasData())
                            RapscallionsAndRockhoppers.getHelper().removeBoatData(previous.getSecond());
                        RapscallionsAndRockhoppers.getHelper().syncBoatData(previous.getSecond());
                    }
                    boat.spawnAtLocation(new ItemStack(RockhoppersItems.BOAT_HOOK), 1.0F);
                    data.removePreviousLinkedBoat(previous.getFirst());
                    if (!data.hasData())
                        RapscallionsAndRockhoppers.getHelper().removeBoatData(boat);
                    RapscallionsAndRockhoppers.getHelper().syncBoatData(boat);
                    return;
                }
                doBoatLinkedMovementTo(boat, previous.getSecond());
            }
        }

    }

    private static void doBoatLinkedMovementTo(Boat boat, Boat other) {
        // Determines
        if (RapscallionsAndRockhoppers.getHelper().getBoatData(boat).lastMovementTime >= RapscallionsAndRockhoppers.getHelper().getBoatData(other).lastMovementTime || boat.hasControllingPassenger())
            return;

        var thisPos = boat.position();
        var otherPos = other.position();
        var distanceBetween = other.distanceTo(boat);
        if (distanceBetween <= 3 || distanceBetween > 16) return;
        var distanceFactor = (distanceBetween - 3) / 7;

        // This controls the velocity of the boat, making it quicker the further away it is
        var betweenVec = thisPos.vectorTo(otherPos).scale(BoatLinksAttachment.HOOK_DAMPENING_FACTOR);
        var thisDelta = betweenVec.normalize().scale(distanceBetween).scale(distanceFactor);
        // If the delta is forcing this backwards, don't do it
        // if (thisDelta.dot(this.getDeltaMovement()) < 0) return;
        thisDelta.multiply(1f, 0f, 1f);
        thisDelta.add(0f, boat.getDeltaMovement().y(), 0f);
        boat.setDeltaMovement(thisDelta);

        if (boat.getDeltaMovement().horizontalDistance() > 0.05 && (!boat.hasControllingPassenger() || !(boat.getControllingPassenger() instanceof Player))) {
            float cross = (float) (otherPos.subtract(thisPos).cross(boat.getForward()).y()) * 1.4F;
            boat.setYRot(boat.getYRot() + cross);
        }
    }

    public static void moveTowardsNonBoat(Boat boat, Entity other) {
        var thisPos = boat.position();
        var otherPos = other.position();
        if (other.getDeltaMovement().horizontalDistance() > 0.05) {
            float cross = (float) (otherPos.subtract(thisPos).cross(boat.getForward()).y());
            boat.setYRot(boat.getYRot() + cross);
        }

        if (boat.level().isClientSide()) return;
        var distanceBetween = other.position().multiply(1.0, 0.0, 1.0).distanceTo(boat.position().multiply(1.0, 0.0, 1.0));
        if (distanceBetween > 2) {
            var distanceFactor = (distanceBetween - 2) / 7;
            Vec3 delta = boat.position().vectorTo(other.position()).normalize().scale(distanceBetween).scale(distanceFactor).multiply(1.0F, other.getY() - boat.getY() > 1.8F ? 1.0F : 0.0F, 1.0F);
            if (((BoatAccessor)boat).rapscallionsandrockhoppers$getStatus() != null && ((BoatAccessor)boat).rapscallionsandrockhoppers$getStatus().equals(Boat.Status.IN_WATER) && delta.y() < 0.0)
                delta = new Vec3(delta.x(), boat.getDeltaMovement().y(), delta.z());
            boat.setDeltaMovement(delta);
        }

        if (boat.getDeltaMovement().horizontalDistance() > 0.05 && (!boat.hasControllingPassenger() || !(boat.getControllingPassenger() instanceof Player))) {
            float cross = (float) (otherPos.subtract(thisPos).cross(boat.getForward()).y()) * 2.4F;
            boat.setYRot(boat.getYRot() + cross);
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
//        this.clearFollowingPenguins();
//        if (tag.contains("following_penguins", Tag.TAG_LIST)) {
//            ListTag penguins = tag.getList("following_penguins", Tag.TAG_INT_ARRAY);
//            for (Tag penguin : penguins) {
//                this.addFollowingPenguin(NbtUtils.loadUUID(penguin));
//            }
//        }
    }
}
