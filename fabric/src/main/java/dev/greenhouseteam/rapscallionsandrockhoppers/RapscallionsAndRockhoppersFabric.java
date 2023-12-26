package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersBlocks;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersEntityTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersItems;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersMemoryModuleTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersSensorTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersSoundEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTabs;

public class RapscallionsAndRockhoppersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RapscallionsAndRockhoppers.init();
        handleRegistration();
    }

    public static void handleRegistration() {
        RapscallionsAndRockhoppersBlocks.registerBlocks(Registry::register);
        RapscallionsAndRockhoppersItems.registerItems(Registry::register);
        RapscallionsAndRockhoppersEntityTypes.registerEntityTypes(Registry::register);
        RapscallionsAndRockhoppersSoundEvents.registerSoundEvents(Registry::register);
        RapscallionsAndRockhoppersMemoryModuleTypes.registerMemoryModuleTypes(Registry::register);
        RapscallionsAndRockhoppersSensorTypes.registerSensorTypes(Registry::register);

        RapscallionsAndRockhoppersEntityTypes.createMobAttributes(FabricDefaultAttributeRegistry::register);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> RapscallionsAndRockhoppersItems.addAfterNaturalBlocksTab(entries::addAfter));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> RapscallionsAndRockhoppersItems.addSpawnEggsTab(entries::accept));
    }
}
