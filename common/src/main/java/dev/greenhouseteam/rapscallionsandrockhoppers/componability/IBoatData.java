package dev.greenhouseteam.rapscallionsandrockhoppers.componability;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface IBoatData {
    ResourceLocation ID = RapscallionsAndRockhoppers.asResource("boat_data");

    double HOOK_DAMPENING_FACTOR = 0.2d;

    default @Nullable Boat getProvider() {
        return null;
    }

    List<UUID> getNextLinkedBoatUuids();
    void clearNextLinkedBoatUuids();
    List<UUID> getPreviousLinkedBoatUuids();
    void clearPreviousLinkedBoatUuids();
    @Nullable UUID getLinkedPlayerUuid();
    default List<Boat> getNextLinkedBoats() {
        return null;
    }
    default List<Boat> getPreviousLinkedBoats() {
        return null;
    }
    default @Nullable Player getLinkedPlayer() {
        return null;
    }
    void setLinkedPlayer(@Nullable UUID player);
    void addNextLinkedBoat(@Nullable UUID boat);
    void removeNextLinkedBoat(@Nullable UUID boat);
    void addPreviousLinkedBoat(@Nullable UUID boat);
    void removePreviousLinkedBoat(@Nullable UUID boat);
    default boolean canLinkTo(Boat otherBoat) {
        IBoatData otherBoatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData(otherBoat);
        return !this.getPreviousLinkedBoats().contains(otherBoat) && !this.getNextLinkedBoats().contains(otherBoat) && !otherBoatData.getPreviousLinkedBoats().contains(this.getProvider()) && !otherBoatData.getNextLinkedBoats().contains(this.getProvider());
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
