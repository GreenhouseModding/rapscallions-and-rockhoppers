package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.componability.BoatDataCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;

public class RockhoppersCapabilities {

    public static final EntityCapability<BoatDataCapability, Void> BOAT_DATA = EntityCapability.createVoid(
            BoatDataCapability.ID, BoatDataCapability.class);

}
