package house.greenhouse.rapscallionsandrockhoppers;

import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.entity.PenguinVariant;
import house.greenhouse.rapscallionsandrockhoppers.network.RockhoppersPackets;
import house.greenhouse.rapscallionsandrockhoppers.network.s2c.SyncBoatLinksAttachmentPacketS2C;
import house.greenhouse.rapscallionsandrockhoppers.network.s2c.SyncPlayerLinksAttachmentPacketS2C;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersActivities;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersAttachments;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersBlockEntityTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersBlocks;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersDataComponents;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersEntityTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersItems;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersSensorTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersSoundEvents;
import house.greenhouse.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
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
        RockhoppersAttachments.init();
        handleRegistration();
        handleBiomeModifications();
        handlePenguinTypeRegistryEvents();

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.isSpectator() || !(entity instanceof Boat boat)) {
                return InteractionResult.PASS;
            }
            return RapscallionsAndRockhoppers.getHelper().getBoatData(boat).handleInteractionWithBoatHook(player, hand);
        });

        EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
            if (entity.hasAttached(RockhoppersAttachments.BOAT_LINKS)) {
                var packet = new SyncBoatLinksAttachmentPacketS2C(entity.getId(), entity.getAttached(RockhoppersAttachments.BOAT_LINKS));
                ServerPlayNetworking.send(player, packet);
            }
            if (entity.hasAttached(RockhoppersAttachments.PLAYER_LINKS)) {
                var packet = new SyncPlayerLinksAttachmentPacketS2C(entity.getId(), entity.getAttached(RockhoppersAttachments.PLAYER_LINKS));
                ServerPlayNetworking.send(player, packet);
            }
        });
        RockhoppersPackets.registerPayloadTypes();
    }

    private static Predicate<BiomeSelectionContext> createPenguinSpawnPredicate() {
        return biomeSelectionContext -> {
            if (RapscallionsAndRockhoppers.getBiomePopulationPenguinTypeRegistry() == null) {
                return false;
            }
            return RapscallionsAndRockhoppers.getBiomePopulationPenguinTypeRegistry().stream().anyMatch(penguinType -> penguinType.biomes().unwrap().stream().anyMatch(holderSet -> holderSet.data().contains(biomeSelectionContext.getBiomeRegistryEntry())));
        };
    }

    public static void handleBiomeModifications() {
        SpawnPlacements.register(RockhoppersEntityTypes.PENGUIN, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Penguin::checkPenguinSpawnRules);
        createBiomeModifications(RapscallionsAndRockhoppers.asResource("penguin"),
                createPenguinSpawnPredicate(), RockhoppersEntityTypes.PENGUIN, 15, 3, 5);
    }

    public static void createBiomeModifications(ResourceLocation location, Predicate<BiomeSelectionContext> predicate, EntityType<?> entityType, int weight, int min, int max) {
        BiomeModifications.create(location).add(ModificationPhase.ADDITIONS, predicate, context -> context.getSpawnSettings().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(entityType, weight, min, max)));
    }

    public static void handleRegistration() {
        DynamicRegistries.registerSynced(RockhoppersResourceKeys.PENGUIN_VARIANT, PenguinVariant.DIRECT_CODEC);

        RockhoppersBlocks.registerBlocks(Registry::register);
        RockhoppersItems.registerItems(Registry::register);
        RockhoppersDataComponents.registerDataComponents(Registry::register);
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
        ServerLifecycleEvents.SERVER_STARTED.register(server -> RapscallionsAndRockhoppers.setBiomePopulationPenguinTypeRegistry(null));
    }
}
