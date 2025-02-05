package house.greenhouse.rapscallionsandrockhoppers;

import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import house.greenhouse.rapscallionsandrockhoppers.attachment.PlayerLinksAttachment;
import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.entity.PenguinVariant;
import house.greenhouse.rapscallionsandrockhoppers.network.s2c.*;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Optional;

public class RapscallionsAndRockhoppersEvents {
    @EventBusSubscriber(modid = RapscallionsAndRockhoppers.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void registerContent(RegisterEvent event) {
            if (event.getRegistryKey() == Registries.ENTITY_TYPE)
                RockhoppersEntityTypes.registerEntityTypes();
            if (event.getRegistryKey() == Registries.SOUND_EVENT)
                RockhoppersSoundEvents.registerSoundEvents();
            if (event.getRegistryKey() == Registries.ITEM)
                RockhoppersItems.registerItems();
            if (event.getRegistryKey() == Registries.BLOCK)
                RockhoppersBlocks.registerBlocks();
            if (event.getRegistryKey() == Registries.ACTIVITY)
                RockhoppersActivities.registerActivities();
            if (event.getRegistryKey() == Registries.MEMORY_MODULE_TYPE)
                RockhoppersMemoryModuleTypes.registerMemoryModuleTypes();
            if (event.getRegistryKey() == Registries.SENSOR_TYPE)
                RockhoppersSensorTypes.registerSensorTypes();
            if (event.getRegistryKey() == Registries.BLOCK_ENTITY_TYPE)
                RockhoppersBlockEntityTypes.registerBlockEntityTypes();
            if (event.getRegistryKey() == Registries.DATA_COMPONENT_TYPE)
                RockhoppersDataComponents.registerDataComponents();
        }

        @SubscribeEvent
        public static void createNewDataPackRegistries(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(RockhoppersResourceKeys.PENGUIN_VARIANT, PenguinVariant.DIRECT_CODEC, PenguinVariant.DIRECT_CODEC);
        }
        
      

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
            event.register(RockhoppersEntityTypes.PENGUIN, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Penguin::checkPenguinSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        }

        @SubscribeEvent
        public static void register(RegisterPayloadHandlersEvent event) {
            event.registrar(RapscallionsAndRockhoppers.MOD_ID)
                    .versioned("1.0.0")
                    .playToClient(InvalidateCachedPenguinTypePacketS2C.TYPE, InvalidateCachedPenguinTypePacketS2C.STREAM_CODEC, (payload, context) -> payload.handle())
                    .playToClient(SyncBlockPosLookPacketS2C.TYPE, SyncBlockPosLookPacketS2C.STREAM_CODEC, (payload, context) -> payload.handle())
                    .playToClient(SyncBoatLinksAttachmentPacketS2C.TYPE, SyncBoatLinksAttachmentPacketS2C.STREAM_CODEC, (payload, context) -> payload.handle())
                    .playToClient(SyncPlayerLinksAttachmentPacketS2C.TYPE, SyncPlayerLinksAttachmentPacketS2C.STREAM_CODEC, (payload, context) -> payload.handle())
                    .playToClient(SyncBoatPenguinsAttachmentPacketS2C.TYPE, SyncBoatPenguinsAttachmentPacketS2C.STREAM_CODEC, (payload, context) -> payload.handle());
        }



        @SubscribeEvent
        public static void createEntityAttributes(EntityAttributeCreationEvent event) {
            RockhoppersEntityTypes.createMobAttributes(event::put);
        }

        @SubscribeEvent
        public static void onCreativeModeTabBuild(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
                RockhoppersItems.addAfterIngredientsTab((stack, stack2) -> event.insertAfter(stack, stack2, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
            } else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                RockhoppersItems.addBeforeToolsAndUtilitiesTab((stack, stack2) -> event.insertAfter(stack, stack2, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
            } else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
                RockhoppersItems.addAfterNaturalBlocksTab((stack, stack2) -> event.insertAfter(stack, stack2, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
            } else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                RockhoppersItems.addSpawnEggsTab(event::accept);
            }

        }

    }



    @EventBusSubscriber(modid = RapscallionsAndRockhoppers.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class ForgeBusEvents {
        @SubscribeEvent
        public static void onServerStarted(ServerStartedEvent event) {
            RapscallionsAndRockhoppers.setBiomePopulationPenguinTypeRegistry(null);
        }

        @SubscribeEvent
        public static void onBoatInteraction(PlayerInteractEvent.EntityInteract event) {
            if (event.getTarget() instanceof Boat boat) {
                BoatLinksAttachment capability = RapscallionsAndRockhoppers.getHelper().getBoatData(boat);
                InteractionResult result = capability.handleInteractionWithBoatHook(event.getEntity(), event.getHand());
                if (result != InteractionResult.PASS) {
                    event.setCancellationResult(result);
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(PlayerTickEvent.Post event) {
            Optional<PlayerLinksAttachment> attachment = event.getEntity().getExistingData(RockhoppersAttachments.PLAYER_LINKS);
            if (attachment.isPresent() && event.getEntity().tickCount % 20 == 0) {
                attachment.get().invalidateNonExistentBoats();
            }
        }

        @SubscribeEvent
        public static void onStartTracking(PlayerEvent.StartTracking event) {
            event.getTarget().getExistingData(RockhoppersAttachments.BOAT_LINKS).ifPresent(attachment -> PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new SyncBoatLinksAttachmentPacketS2C(attachment.getProvider().getId(), attachment)));
            event.getTarget().getExistingData(RockhoppersAttachments.PLAYER_LINKS).ifPresent(attachment -> PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new SyncPlayerLinksAttachmentPacketS2C(attachment.getProvider().getId(), attachment)));
        }
    }
}