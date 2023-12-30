package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rapscallionsandrockhoppers.componability.BoatDataComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.world.entity.vehicle.Boat;

public class RockhoppersEntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<BoatDataComponent> BOAT_ATTACHMENTS_COMPONENT = ComponentRegistry.getOrCreate(BoatDataComponent.ID, BoatDataComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Boat.class, BOAT_ATTACHMENTS_COMPONENT, BoatDataComponent::new);
    }
}
