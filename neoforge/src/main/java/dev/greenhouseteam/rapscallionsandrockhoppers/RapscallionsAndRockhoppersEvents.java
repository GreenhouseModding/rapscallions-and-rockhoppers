package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rapscallionsandrockhoppers.componability.BoatDataCapability;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.PenguinType;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.RockhoppersPacketHandler;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersActivities;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersBlocks;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersCapabilities;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersEntityTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersItems;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersSensorTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersSoundEvents;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RegisterFunction;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import dev.greenhouseteam.rdpr.api.ReloadableRegistryEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class RapscallionsAndRockhoppersEvents {
    @Mod.EventBusSubscriber(modid = RapscallionsAndRockhoppers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            RockhoppersPacketHandler.register();
        }

        @SubscribeEvent
        public static void registerContent(RegisterEvent event) {
            if (event.getRegistryKey() == Registries.ENTITY_TYPE)
                register(event, RockhoppersEntityTypes::registerEntityTypes);
            else if (event.getRegistryKey() == Registries.SOUND_EVENT)
                register(event, RockhoppersSoundEvents::registerSoundEvents);
            else if (event.getRegistryKey() == Registries.ITEM)
                register(event, RockhoppersItems::registerItems);
            else if (event.getRegistryKey() == Registries.BLOCK)
                register(event, RockhoppersBlocks::registerBlocks);
            else if (event.getRegistryKey() == Registries.ACTIVITY)
                register(event, RockhoppersActivities::registerActivities);
            else if (event.getRegistryKey() == Registries.MEMORY_MODULE_TYPE)
                register(event, RockhoppersMemoryModuleTypes::registerMemoryModuleTypes);
            else if (event.getRegistryKey() == Registries.SENSOR_TYPE)
                register(event, RockhoppersSensorTypes::registerSensorTypes);
        }

        @SubscribeEvent
        public static void createNewDataPackRegistries(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY, PenguinType.CODEC, PenguinType.CODEC);
        }

        @SubscribeEvent
        public static void makeDataPackRegistriesReloadable(ReloadableRegistryEvent event) {
            RapscallionsAndRockhoppers.createRDPRContents(event);
        }

        private static <T> void register(RegisterEvent event, Consumer<RegisterFunction<T>> consumer) {
            consumer.accept((registry, id, value) -> event.register(registry.key(), id, () -> value));
        }

        private static final Map<Boat, BoatDataCapability> BOAT_DATA_CAPABILITY_CACHE = new WeakHashMap<>(512);


        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
            event.register(RockhoppersEntityTypes.PENGUIN, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Penguin::checkPenguinSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        }

        @SubscribeEvent
        public static void attachCapabilities(RegisterCapabilitiesEvent event) {
            for (Map.Entry<ResourceKey<EntityType<?>>, EntityType<?>> entityType : BuiltInRegistries.ENTITY_TYPE.entrySet()) {
                if (entityType.getValue().getBaseClass().isAssignableFrom(Boat.class)) {
                    event.registerEntity(RockhoppersCapabilities.BOAT_DATA, entityType.getValue(), (entity, ctx) -> {
                        if (entity instanceof Boat boat) {
                            return BOAT_DATA_CAPABILITY_CACHE.computeIfAbsent(boat, BoatDataCapability::new);
                        }
                        return null;
                    });
                }
            }
        }

        @SubscribeEvent
        public static void createEntityAttributes(EntityAttributeCreationEvent event) {
            RockhoppersEntityTypes.createMobAttributes(event::put);
        }

        @SubscribeEvent
        public static void onCreativeModeTabBuild(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
                RockhoppersItems.addAfterNaturalBlocksTab((stack, itemLike) -> event.getEntries().putAfter(itemLike, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
            } else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                RockhoppersItems.addSpawnEggsTab(event::accept);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = RapscallionsAndRockhoppers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusEvents {
        @SubscribeEvent
        public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
            RapscallionsAndRockhoppers.onUnload(event.getEntity(), event.getLevel());
        }

        @SubscribeEvent
        public static void onServerStopped(ServerStoppingEvent event) {
            RapscallionsAndRockhoppers.removeCachedPenguinTypeRegistry();
        }

        @SubscribeEvent
        public static void onDataPackSync(OnDatapackSyncEvent event) {
            if (event.getPlayer() == null) {
                RapscallionsAndRockhoppers.setCachedPenguinTypeRegistry(event.getPlayerList().getServer().registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY));
                event.getPlayerList().getServer().getAllLevels().forEach(Penguin::invalidateCachedPenguinTypes);
            }
        }
    }

}
