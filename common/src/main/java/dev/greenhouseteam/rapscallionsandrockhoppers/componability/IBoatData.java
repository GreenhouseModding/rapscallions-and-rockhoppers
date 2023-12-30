package dev.greenhouseteam.rapscallionsandrockhoppers.componability;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
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

    @Nullable Boat getNextLinkedBoat();
    @Nullable Boat getPreviousLinkedBoat();
    @Nullable Player getLinkedPlayer();
    void setLinkedPlayer(@Nullable Player player);

    void setNextLinkedBoat(Boat boat);
    void setPreviousLinkedBoat(Boat boat);
    default boolean canLinkTo(Boat otherBoat) {
        // This means the back of the current boat is free to be linked from.
        if (getPreviousLinkedBoat() != null) return true;
        // This means the front of the other boat is free to be linked to.
        return ((IBoatData)otherBoat).getNextLinkedBoat() == null;
    }

    List<UUID> getFollowingPenguins();
    int penguinCount();
    void addFollowingPenguin(UUID penguinUUID);
    void removeFollowingPenguin(UUID penguinUUID);
    void clearFollowingPenguins();

    default void serialize(CompoundTag tag) {
        if (this.penguinCount() > 0) {
            ListTag penguins = new ListTag();
            for (UUID uuid : this.getFollowingPenguins()) {
                penguins.add(NbtUtils.createUUID(uuid));
            }
            tag.put("following_penguins", penguins);
        }
    }

    default void deserialize(CompoundTag tag) {
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
