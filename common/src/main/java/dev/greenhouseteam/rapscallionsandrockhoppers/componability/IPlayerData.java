package dev.greenhouseteam.rapscallionsandrockhoppers.componability;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface IPlayerData {
    ResourceLocation ID = RapscallionsAndRockhoppers.asResource("player_data");

    List<UUID> getLinkedBoatUUIDs();

    default @Nullable List<Boat> getLinkedBoats() {
        return null;
    }

    void addLinkedBoat(UUID boat);
    void removeLinkedBoat(UUID boat);
    void clearLinkedBoats();

    default void serialize(CompoundTag tag) {
        ListTag linkedBoats = new ListTag();
        this.getLinkedBoatUUIDs().forEach(uuid -> linkedBoats.add(NbtUtils.createUUID(uuid)));
        tag.put("linked_boat", linkedBoats);
    }

    default void deserialize(CompoundTag tag) {
        this.clearLinkedBoats();
        if (tag.contains("linked_boat", Tag.TAG_LIST)) {
            ListTag linkedBoats = tag.getList("linked_boat", Tag.TAG_INT_ARRAY);
            for (Tag linkedBoat : linkedBoats) {
                this.addLinkedBoat(NbtUtils.loadUUID(linkedBoat));
            }
        }
    }

    default void sync() {

    }
}
