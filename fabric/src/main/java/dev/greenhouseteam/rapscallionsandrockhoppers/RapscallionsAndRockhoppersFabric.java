package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersBlocks;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersEntityTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersItems;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersSensorTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersSoundEvents;
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
        RockhoppersBlocks.registerBlocks(Registry::register);
        RockhoppersItems.registerItems(Registry::register);
        RockhoppersEntityTypes.registerEntityTypes(Registry::register);
        RockhoppersSoundEvents.registerSoundEvents(Registry::register);
        RockhoppersMemoryModuleTypes.registerMemoryModuleTypes(Registry::register);
        RockhoppersSensorTypes.registerSensorTypes(Registry::register);

        RockhoppersEntityTypes.createMobAttributes(FabricDefaultAttributeRegistry::register);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> RockhoppersItems.addAfterNaturalBlocksTab(entries::addAfter));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> RockhoppersItems.addSpawnEggsTab(entries::accept));
    }
}
