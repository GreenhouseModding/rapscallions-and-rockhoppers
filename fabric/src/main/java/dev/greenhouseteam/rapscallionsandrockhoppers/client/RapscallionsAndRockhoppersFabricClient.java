package dev.greenhouseteam.rapscallionsandrockhoppers.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class RapscallionsAndRockhoppersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RapscallionsAndRockhoppersClient.createRenderers(EntityRendererRegistry::register);
        RapscallionsAndRockhoppersClient.createEntityLayers((layer, supplier) -> EntityModelLayerRegistry.registerModelLayer(layer, supplier::get));
    }
}
