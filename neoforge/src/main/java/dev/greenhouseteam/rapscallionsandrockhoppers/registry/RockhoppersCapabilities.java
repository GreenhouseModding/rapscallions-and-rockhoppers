package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.componability.BoatDataCapability;
import dev.greenhouseteam.rapscallionsandrockhoppers.componability.PlayerDataCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;

public class RockhoppersCapabilities {

    public static final EntityCapability<BoatDataCapability, Void> BOAT_DATA = EntityCapability.createVoid(
            BoatDataCapability.ID, BoatDataCapability.class);
    public static final EntityCapability<PlayerDataCapability, Void> PLAYER_DATA = EntityCapability.createVoid(
            PlayerDataCapability.ID, PlayerDataCapability.class);

}
