package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import dev.greenhouseteam.rapscallionsandrockhoppers.attachment.PlayerLinksAttachment;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.PenguinType;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.InvalidateCachedPenguinTypePacket;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.SyncBlockPosLookPacketS2C;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.SyncBoatLinksAttachmentPacket;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.SyncPlayerLinksAttachmentPacket;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.*;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RegisterFunction;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPlayPayloadHandler;
import net.neoforged.neoforge.network.registration.IDirectionAwarePayloadHandlerBuilder;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Optional;
import java.util.function.Consumer;

public class RapscallionsAndRockhoppersEvents {
    @Mod.EventBusSubscriber(modid = RapscallionsAndRockhoppers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {

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
            else if (event.getRegistryKey() == Registries.BLOCK_ENTITY_TYPE)
                register(event, RockhoppersBlockEntityTypes::registerBlockEntityTypes);
        }

        @SubscribeEvent
        public static void createNewDataPackRegistries(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY, PenguinType.CODEC, PenguinType.CODEC);
        }

        private static <T> void register(RegisterEvent event, Consumer<RegisterFunction<T>> consumer) {
            consumer.accept((registry, id, value) -> event.register(registry.key(), id, () -> value));
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
            event.register(RockhoppersEntityTypes.PENGUIN, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Penguin::checkPenguinSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        }
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlerEvent event) {
        event.registrar(RapscallionsAndRockhoppers.MOD_ID)
                .versioned("1.0.0")
                .play(InvalidateCachedPenguinTypePacket.ID, InvalidateCachedPenguinTypePacket::read, createCommonS2CHandler(InvalidateCachedPenguinTypePacket::handle))
                .play(SyncBlockPosLookPacketS2C.ID, SyncBlockPosLookPacketS2C::read, createCommonS2CHandler(SyncBlockPosLookPacketS2C::handle))
                .play(SyncBoatLinksAttachmentPacket.ID, SyncBoatLinksAttachmentPacket::read, createCommonS2CHandler(SyncBoatLinksAttachmentPacket::handle))
                .play(SyncPlayerLinksAttachmentPacket.ID, SyncPlayerLinksAttachmentPacket::read, createCommonS2CHandler(SyncPlayerLinksAttachmentPacket::handle));
    }

    private static <MSG extends CustomPacketPayload> Consumer<IDirectionAwarePayloadHandlerBuilder<MSG, IPlayPayloadHandler<MSG>>> createCommonS2CHandler(Consumer<MSG> handler) {
        return builder -> builder.client((payload, context) -> handler.accept(payload));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Optional<PlayerLinksAttachment> attachment = event.player.getExistingData(RockhoppersAttachments.PLAYER_LINKS);
        if (attachment.isPresent() && event.player.tickCount % 20 == 0) {
            attachment.get().invalidateNonExistentBoats();
        }
    }

    @SubscribeEvent
    public static void createEntityAttributes(EntityAttributeCreationEvent event) {
        RockhoppersEntityTypes.createMobAttributes(event::put);
    }

    @SubscribeEvent
    public static void onCreativeModeTabBuild(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            RockhoppersItems.addAfterIngredientsTab((stack, stack2) -> event.getEntries().putAfter(stack2, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
        } else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            RockhoppersItems.addBeforeToolsAndUtilitiesTab((stack, stack2) -> event.getEntries().putBefore(stack2, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
        } else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            RockhoppersItems.addAfterNaturalBlocksTab((stack, stack2) -> event.getEntries().putAfter(stack2, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
        } else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            RockhoppersItems.addSpawnEggsTab(event::accept);
        }
    }

    @Mod.EventBusSubscriber(modid = RapscallionsAndRockhoppers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusEvents {
        @SubscribeEvent
        public static void onServerStarted(ServerStartedEvent event) {
            RapscallionsAndRockhoppers.removeCachedPenguinTypeRegistry(false);
        }

        @SubscribeEvent
        public static void onServerStopped(ServerStoppingEvent event) {
            RapscallionsAndRockhoppers.removeCachedPenguinTypeRegistry(true);
        }

        @SubscribeEvent
        public static void onDataPackSync(OnDatapackSyncEvent event) {
            if (event.getPlayer() == null) {
                event.getPlayerList().getServer().getAllLevels().forEach(Penguin::invalidateCachedPenguinTypes);
            }
        }

        @SubscribeEvent
        public static void onBoatInteraction(PlayerInteractEvent.EntityInteract event) {
            if (event.getTarget() instanceof Boat boat) {
                BoatLinksAttachment capability = IRockhoppersPlatformHelper.INSTANCE.getBoatData(boat);
                InteractionResult result = capability.handleInteractionWithBoatHook(event.getEntity(), event.getHand());
                if (result != InteractionResult.PASS) {
                    event.setCancellationResult(result);
                    event.setCanceled(true);
                }
            }
        }
    }
}