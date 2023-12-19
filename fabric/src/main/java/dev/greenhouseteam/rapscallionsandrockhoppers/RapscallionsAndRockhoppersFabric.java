package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersEntityTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersSoundEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;

public class RapscallionsAndRockhoppersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RapscallionsAndRockhoppers.init();
        handleRegistration();
    }

    public static void handleRegistration() {
        RapscallionsAndRockhoppersEntityTypes.registerEntityTypes(Registry::register);
        RapscallionsAndRockhoppersSoundEvents.registerSoundEvents(Registry::register);

        RapscallionsAndRockhoppersEntityTypes.createMobAttributes(FabricDefaultAttributeRegistry::register);
    }
}