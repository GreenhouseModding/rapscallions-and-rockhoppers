package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rapscallionsandrockhoppers.network.RapscallionsAndRockhoppersPacketHandler;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersBlocks;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersEntityTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersSoundEvents;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

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
                RapscallionsAndRockhoppersEntityTypes.registerEntityTypes((registry, resource, entityType) -> event.register(registry.key(), resource, () -> entityType));
            else if (event.getRegistryKey() == Registries.SOUND_EVENT)
                RapscallionsAndRockhoppersSoundEvents.registerSoundEvents((registry, resource, soundEvent) -> event.register(registry.key(), resource, () -> soundEvent));
            else if (event.getRegistryKey() == Registries.BLOCK)
                RapscallionsAndRockhoppersBlocks.registerBlocks((registry, resource, block) -> event.register(registry.key(), resource, () -> block));
        }

        @SubscribeEvent
        public static void createEntityAttributes(EntityAttributeCreationEvent event) {
            RapscallionsAndRockhoppersEntityTypes.createMobAttributes(event::put);
        }
    }

}
