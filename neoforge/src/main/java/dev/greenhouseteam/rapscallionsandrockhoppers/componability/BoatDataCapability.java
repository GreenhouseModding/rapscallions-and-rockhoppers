package dev.greenhouseteam.rapscallionsandrockhoppers.componability;

import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersAttachments;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.EntityGetUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BoatDataCapability implements IBoatData {
    private final Boat provider;

    public BoatDataCapability(Boat entity) {
        this.provider = entity;
    }

    @Override
    public @Nullable Boat getProvider() {
        return this.provider;
    }

    @Override
    public List<UUID> getNextLinkedBoatUuids() {
        return this.provider.getData(RockhoppersAttachments.BOAT_DATA.get()).getNextLinkedBoatUuids();
    }

    @Override
    public void clearNextLinkedBoatUuids() {
        this.provider.getData(RockhoppersAttachments.BOAT_DATA.get()).clearNextLinkedBoatUuids();
    }

    @Override
    public List<UUID> getPreviousLinkedBoatUuids() {
        return this.provider.getData(RockhoppersAttachments.BOAT_DATA.get()).getPreviousLinkedBoatUuids();
    }

    @Override
    public void clearPreviousLinkedBoatUuids() {
        this.provider.getData(RockhoppersAttachments.BOAT_DATA.get()).clearPreviousLinkedBoatUuids();
    }

    @Override
    public @Nullable UUID getLinkedPlayerUuid() {
        return this.provider.getData(RockhoppersAttachments.BOAT_DATA.get()).getLinkedPlayerUuid();
    }

    @Override
    public List<Boat> getNextLinkedBoats() {
        return this.getNextLinkedBoatUuids().stream().map(uuid -> {
            Entity entity = EntityGetUtil.getEntityFromUuid(this.provider.level(), uuid);
            if (entity instanceof Boat boat) {
                return boat;
            }
            return null;
        }).filter(Objects::nonNull).toList();
    }

    @Override
    public List<Boat> getPreviousLinkedBoats() {
        return this.getPreviousLinkedBoatUuids().stream().map(uuid -> {
            Entity entity = EntityGetUtil.getEntityFromUuid(this.provider.level(), uuid);
            if (entity instanceof Boat boat) {
                return boat;
            }
            return null;
        }).filter(Objects::nonNull).toList();
    }

    @Override
    public @Nullable Player getLinkedPlayer() {
        Entity entity = EntityGetUtil.getEntityFromUuid(this.provider.level(), this.provider.getData(RockhoppersAttachments.BOAT_DATA.get()).getLinkedPlayerUuid());
        if (entity instanceof Player player) {
            return player;
        }
        return null;
    }

    @Override
    public void setLinkedPlayer(@Nullable UUID player) {
        provider.getData(RockhoppersAttachments.BOAT_DATA.get()).setLinkedPlayer(player);
    }

    @Override
    public void addNextLinkedBoat(@Nullable UUID boat) {
        provider.getData(RockhoppersAttachments.BOAT_DATA.get()).addNextLinkedBoat(boat);
    }

    @Override
    public void removeNextLinkedBoat(@Nullable UUID boat) {
        provider.getData(RockhoppersAttachments.BOAT_DATA.get()).removeNextLinkedBoat(boat);
    }

    @Override
    public void addPreviousLinkedBoat(@Nullable UUID boat) {
        provider.getData(RockhoppersAttachments.BOAT_DATA.get()).addPreviousLinkedBoat(boat);
    }

    @Override
    public void removePreviousLinkedBoat(@Nullable UUID boat) {
        provider.getData(RockhoppersAttachments.BOAT_DATA.get()).removePreviousLinkedBoat(boat);
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