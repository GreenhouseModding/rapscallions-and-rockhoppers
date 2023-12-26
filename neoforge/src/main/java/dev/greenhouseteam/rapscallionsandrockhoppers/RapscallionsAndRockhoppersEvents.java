package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rapscallionsandrockhoppers.network.RapscallionsAndRockhoppersPacketHandler;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersBlocks;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersEntityTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersItems;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersMemoryModuleTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersSensorTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersSoundEvents;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RegisterFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Consumer;

public class RapscallionsAndRockhoppersEvents {
    @Mod.EventBusSubscriber(modid = RapscallionsAndRockhoppers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            RapscallionsAndRockhoppersPacketHandler.register();
        }

        @SubscribeEvent
        public static void registerContent(RegisterEvent event) {
            if (event.getRegistryKey() == Registries.ENTITY_TYPE)
                register(event, RapscallionsAndRockhoppersEntityTypes::registerEntityTypes);
            else if (event.getRegistryKey() == Registries.SOUND_EVENT)
                register(event, RapscallionsAndRockhoppersSoundEvents::registerSoundEvents);
            else if (event.getRegistryKey() == Registries.ITEM)
                register(event, RapscallionsAndRockhoppersItems::registerItems);
            else if (event.getRegistryKey() == Registries.BLOCK)
                register(event, RapscallionsAndRockhoppersBlocks::registerBlocks);
            else if (event.getRegistryKey() == Registries.MEMORY_MODULE_TYPE)
                register(event, RapscallionsAndRockhoppersMemoryModuleTypes::registerMemoryModuleTypes);
            else if (event.getRegistryKey() == Registries.SENSOR_TYPE)
                register(event, RapscallionsAndRockhoppersSensorTypes::registerSensorTypes);
        }

        private static <T> void register(RegisterEvent event, Consumer<RegisterFunction<T>> consumer) {
            consumer.accept((registry, id, value) -> event.register(registry.key(), id, () -> value));
        }

        @SubscribeEvent
        public static void createEntityAttributes(EntityAttributeCreationEvent event) {
            RapscallionsAndRockhoppersEntityTypes.createMobAttributes(event::put);
        }

        @SubscribeEvent
        public static void onCreativeModeTabBuild(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
                RapscallionsAndRockhoppersItems.addAfterNaturalBlocksTab((stack, itemLike) -> event.getEntries().putAfter(itemLike, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
            } else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                RapscallionsAndRockhoppersItems.addSpawnEggsTab(event::accept);
            }
        }
    }

}
