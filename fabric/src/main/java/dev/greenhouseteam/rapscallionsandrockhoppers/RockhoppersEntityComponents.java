package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rapscallionsandrockhoppers.componability.BoatDataComponent;
import dev.greenhouseteam.rapscallionsandrockhoppers.componability.PlayerDataComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.world.entity.vehicle.Boat;

public class RockhoppersEntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<BoatDataComponent> BOAT_DATA_COMPONENT = ComponentRegistry.getOrCreate(BoatDataComponent.ID, BoatDataComponent.class);
    public static final ComponentKey<PlayerDataComponent> PLAYER_DATA_COMPONENT = ComponentRegistry.getOrCreate(PlayerDataComponent.ID, PlayerDataComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Boat.class, BOAT_DATA_COMPONENT, BoatDataComponent::new);
        registry.registerForPlayers(PLAYER_DATA_COMPONENT, PlayerDataComponent::new, RespawnCopyStrategy.NEVER_COPY);
    }
}
