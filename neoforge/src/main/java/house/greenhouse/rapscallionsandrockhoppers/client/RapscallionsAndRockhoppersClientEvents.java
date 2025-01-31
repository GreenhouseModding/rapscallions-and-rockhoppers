package house.greenhouse.rapscallionsandrockhoppers.client;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class RapscallionsAndRockhoppersClientEvents {

    @EventBusSubscriber(modid = RapscallionsAndRockhoppers.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            RapscallionsAndRockhoppersClient.createRenderers(event::registerEntityRenderer);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            RapscallionsAndRockhoppersClient.createEntityLayers(event::registerLayerDefinition);
        }
    }

}
