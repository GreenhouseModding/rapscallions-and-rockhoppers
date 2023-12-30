package dev.greenhouseteam.rapscallionsandrockhoppers.componability;

import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersAttachments;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.List;
import java.util.UUID;

public class BoatDataCapability implements IBoatData {
    private final Boat provider;

    public BoatDataCapability(Boat entity) {
        this.provider = entity;
    }

    @Override
    public List<UUID> getFollowingPenguins() {
        return provider.getData(RockhoppersAttachments.BOAT_DATA.get()).getFollowingPenguins();
    }

    @Override
    public int penguinCount() {
        return provider.getData(RockhoppersAttachments.BOAT_DATA.get()).penguinCount();
    }

    @Override
    public void addFollowingPenguin(UUID penguinUUID) {
        provider.getData(RockhoppersAttachments.BOAT_DATA.get()).addFollowingPenguin(penguinUUID);
    }

    @Override
    public void removeFollowingPenguin(UUID penguinUUID) {
        provider.getData(RockhoppersAttachments.BOAT_DATA.get()).removeFollowingPenguin(penguinUUID);
    }

    @Override
    public void clearFollowingPenguins() {
        provider.getData(RockhoppersAttachments.BOAT_DATA.get()).clearFollowingPenguins();
    }
}