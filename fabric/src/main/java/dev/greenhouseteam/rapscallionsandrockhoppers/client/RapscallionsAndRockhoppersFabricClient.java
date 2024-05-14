package dev.greenhouseteam.rapscallionsandrockhoppers.client;

import dev.greenhouseteam.rapscallionsandrockhoppers.network.RockhoppersPackets;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersAttachments;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;

public class RapscallionsAndRockhoppersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RapscallionsAndRockhoppersClient.createRenderers(EntityRendererRegistry::register);
        RapscallionsAndRockhoppersClient.createEntityLayers((layer, supplier) -> EntityModelLayerRegistry.registerModelLayer(layer, supplier::get));
        RockhoppersPackets.registerS2C();
    }
}
