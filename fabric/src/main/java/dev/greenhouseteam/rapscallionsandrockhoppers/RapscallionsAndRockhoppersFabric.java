package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.PenguinType;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.*;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Predicate;

public class RapscallionsAndRockhoppersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RapscallionsAndRockhoppers.init();
        handleRegistration();
        handleBiomeModifications();
        handlePenguinTypeRegistryEvents();

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.isSpectator() || !(entity instanceof Boat boat)) {
                return InteractionResult.PASS;
            }
            return boat.getComponent(RockhoppersEntityComponents.BOAT_DATA_COMPONENT).handleInteractionWithBoatHook(player, hand);
        });
    }

    private static Predicate<BiomeSelectionContext> createPenguinSpawnPredicate() {
        return biomeSelectionContext -> {
            if (RapscallionsAndRockhoppers.getBiomePopulationPenguinTypeRegistry() == null) {
                return false;
            }
            return RapscallionsAndRockhoppers.getBiomePopulationPenguinTypeRegistry().stream().anyMatch(penguinType -> penguinType.spawnBiomes().stream().anyMatch(holderSet -> holderSet.holders().contains(biomeSelectionContext.getBiomeRegistryEntry())));
        };
    }

    public static void handleBiomeModifications() {
        SpawnPlacements.register(RockhoppersEntityTypes.PENGUIN, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Penguin::checkPenguinSpawnRules);
        createBiomeModifications(RapscallionsAndRockhoppers.asResource("penguin"),
                createPenguinSpawnPredicate(), RockhoppersEntityTypes.PENGUIN, 15, 3, 5);
    }

    public static void createBiomeModifications(ResourceLocation location, Predicate<BiomeSelectionContext> predicate, EntityType<?> entityType, int weight, int min, int max) {
        BiomeModifications.create(location).add(ModificationPhase.ADDITIONS, predicate, context -> context.getSpawnSettings().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(entityType, weight, min, max)));
    }

    public static void handleRegistration() {
        DynamicRegistries.registerSynced(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY, PenguinType.CODEC);

        RockhoppersBlocks.registerBlocks(Registry::register);
        RockhoppersItems.registerItems(Registry::register);
        RockhoppersEntityTypes.registerEntityTypes(Registry::register);
        RockhoppersSoundEvents.registerSoundEvents(Registry::register);
        RockhoppersActivities.registerActivities(Registry::register);
        RockhoppersMemoryModuleTypes.registerMemoryModuleTypes(Registry::register);
        RockhoppersSensorTypes.registerSensorTypes(Registry::register);
        RockhoppersBlockEntityTypes.registerBlockEntityTypes(Registry::register);

        RockhoppersEntityTypes.createMobAttributes(FabricDefaultAttributeRegistry::register);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> RockhoppersItems.addAfterIngredientsTab(entries::addAfter));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> RockhoppersItems.addBeforeToolsAndUtilitiesTab(entries::addBefore));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> RockhoppersItems.addAfterNaturalBlocksTab(entries::addAfter));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> RockhoppersItems.addSpawnEggsTab(entries::accept));
    }

    public static void handlePenguinTypeRegistryEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> RapscallionsAndRockhoppers.removeCachedPenguinTypeRegistry(false));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, closeableResourceManager, success) -> {
            if (success) {
                server.getAllLevels().forEach(Penguin::invalidateCachedPenguinTypes);
            }
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> RapscallionsAndRockhoppers.removeCachedPenguinTypeRegistry(true));
    }
}
