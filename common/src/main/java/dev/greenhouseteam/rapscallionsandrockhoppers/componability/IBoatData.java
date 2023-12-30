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

    @Nullable UUID getNextLinkedBoatUuid();
    @Nullable UUID getPreviousLinkedBoatUuid();
    @Nullable UUID getLinkedPlayerUuid();
    default @Nullable Boat getNextLinkedBoat() {
        return null;
    }
    default @Nullable Boat getPreviousLinkedBoat() {
        return null;
    }
    default @Nullable Player getLinkedPlayer() {
        return null;
    }
    void setLinkedPlayer(@Nullable UUID player);
    void setNextLinkedBoat(@Nullable UUID boat);
    void setPreviousLinkedBoat(@Nullable UUID boat);
    default boolean canLinkTo(Boat otherBoat) {
        IBoatData otherBoatData = IRockhoppersPlatformHelper.INSTANCE.getBoatData(otherBoat);
        if (this.getPreviousLinkedBoat() == otherBoat || this.getNextLinkedBoat() == otherBoat || otherBoatData.getPreviousLinkedBoat() == this.getProvider() || otherBoatData.getNextLinkedBoat() == this.getProvider()) return false;
        // This means the back of the current boat is free to be linked from.
        if (getPreviousLinkedBoat() != null) return true;
        // This means the front of the other boat is free to be linked to.
        return otherBoatData.getNextLinkedBoat() == null;
    }

    List<UUID> getFollowingPenguins();
    int penguinCount();
    void addFollowingPenguin(UUID penguinUUID);
    void removeFollowingPenguin(UUID penguinUUID);
    void clearFollowingPenguins();

    default void serialize(CompoundTag tag) {
        if (this.getNextLinkedBoatUuid() != null) {
            tag.putUUID("next_linked_boat", this.getNextLinkedBoatUuid());
        }
        if (this.getPreviousLinkedBoatUuid() != null) {
            tag.putUUID("previous_linked_boat", this.getPreviousLinkedBoatUuid());
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
        if (tag.contains("next_linked_boat", Tag.TAG_INT_ARRAY)) {
            this.setNextLinkedBoat(NbtUtils.loadUUID(tag.get("next_linked_boat")));
        }
        if (tag.contains("previous_linked_boat", Tag.TAG_INT_ARRAY)) {
            this.setPreviousLinkedBoat(NbtUtils.loadUUID(tag.get("previous_linked_boat")));
        }
        if (tag.contains("linked_player", Tag.TAG_INT_ARRAY)) {
            this.setLinkedPlayer(NbtUtils.loadUUID(tag.get("linked_player")));
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
