package dev.greenhouseteam.rapscallionsandrockhoppers.componability;

import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersAttachments;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.EntityGetUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerDataCapability implements IPlayerData {
    private final Player provider;

    public PlayerDataCapability(Player entity) {
        this.provider = entity;
    }

    @Override
    public @Nullable UUID getLinkedBoatUUID() {
        return this.provider.getData(RockhoppersAttachments.PLAYER_DATA.get()).getLinkedBoatUUID();
    }

    @Override
    public @Nullable Boat getLinkedBoat() {
        Entity entity = EntityGetUtil.getEntityFromUuid(this.provider.level(), this.getLinkedBoatUUID());
        if (entity instanceof Boat boat) {
            return boat;
        }
        return null;
    }

    @Override
    public void setLinkedBoat(UUID boat) {
        this.provider.getData(RockhoppersAttachments.PLAYER_DATA.get()).setLinkedBoat(boat);
    }
}