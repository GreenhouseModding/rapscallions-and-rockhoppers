package dev.greenhouseteam.rapscallionsandrockhoppers.client;

import dev.greenhouseteam.rapscallionsandrockhoppers.network.RockhoppersPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class RapscallionsAndRockhoppersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RapscallionsAndRockhoppersClient.createRenderers(EntityRendererRegistry::register);
        RapscallionsAndRockhoppersClient.createEntityLayers((layer, supplier) -> EntityModelLayerRegistry.registerModelLayer(layer, supplier::get));
        RockhoppersPackets.registerS2C();
    }
}
