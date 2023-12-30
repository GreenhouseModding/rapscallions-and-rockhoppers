package dev.greenhouseteam.rapscallionsandrockhoppers.componability;

import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersAttachments;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.EntityGetUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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
    public @Nullable UUID getNextLinkedBoatUuid() {
        return this.provider.getData(RockhoppersAttachments.BOAT_DATA.get()).getNextLinkedBoatUuid();
    }

    @Override
    public @Nullable UUID getPreviousLinkedBoatUuid() {
        return this.provider.getData(RockhoppersAttachments.BOAT_DATA.get()).getPreviousLinkedBoatUuid();
    }

    @Override
    public @Nullable UUID getLinkedPlayerUuid() {
        return this.provider.getData(RockhoppersAttachments.BOAT_DATA.get()).getLinkedPlayerUuid();
    }

    @Override
    public @Nullable Boat getNextLinkedBoat() {
        Entity entity = EntityGetUtil.getEntityFromUuid(this.provider.level(), this.provider.getData(RockhoppersAttachments.BOAT_DATA.get()).getNextLinkedBoatUuid());
        if (entity instanceof Boat boat) {
            return boat;
        }
        return null;
    }

    @Override
    public @Nullable Boat getPreviousLinkedBoat() {
        Entity entity = EntityGetUtil.getEntityFromUuid(this.provider.level(), this.provider.getData(RockhoppersAttachments.BOAT_DATA.get()).getPreviousLinkedBoatUuid());
        if (entity instanceof Boat boat) {
            return boat;
        }
        return null;
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
    public void setNextLinkedBoat(UUID boat) {
        provider.getData(RockhoppersAttachments.BOAT_DATA.get()).setNextLinkedBoat(boat);
    }

    @Override
    public void setPreviousLinkedBoat(UUID boat) {
        provider.getData(RockhoppersAttachments.BOAT_DATA.get()).setPreviousLinkedBoat(boat);
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