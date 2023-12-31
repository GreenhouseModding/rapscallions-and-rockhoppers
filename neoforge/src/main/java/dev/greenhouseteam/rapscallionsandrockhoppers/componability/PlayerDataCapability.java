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

public class PlayerDataCapability implements IPlayerData {
    private final Player provider;

    public PlayerDataCapability(Player entity) {
        this.provider = entity;
    }

    @Override
    public List<UUID> getLinkedBoatUUIDs() {
        return this.provider.getData(RockhoppersAttachments.PLAYER_DATA.get()).getLinkedBoatUUIDs();
    }

    @Override
    public void addLinkedBoat(UUID boat) {
        this.provider.getData(RockhoppersAttachments.PLAYER_DATA.get()).addLinkedBoat(boat);
    }

    @Override
    public void removeLinkedBoat(UUID boat) {
        this.provider.getData(RockhoppersAttachments.PLAYER_DATA.get()).removeLinkedBoat(boat);
    }

    @Override
    public void clearLinkedBoats() {
        this.provider.getData(RockhoppersAttachments.PLAYER_DATA.get()).clearLinkedBoats();
    }

    @Override
    public @Nullable List<Boat> getLinkedBoats() {
        return this.getLinkedBoatUUIDs().stream().map(uuid -> {
            Entity entity = EntityGetUtil.getEntityFromUuid(this.provider.level(), uuid);
            if (entity instanceof Boat boat) {
                return boat;
            }
            return null;
        }).filter(Objects::nonNull).toList();
    }
}