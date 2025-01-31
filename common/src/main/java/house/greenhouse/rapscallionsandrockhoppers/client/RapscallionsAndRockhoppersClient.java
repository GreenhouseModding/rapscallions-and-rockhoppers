package house.greenhouse.rapscallionsandrockhoppers.client;

import house.greenhouse.rapscallionsandrockhoppers.client.renderer.PenguinRenderer;
import house.greenhouse.rapscallionsandrockhoppers.client.renderer.model.PenguinModel;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersEntityTypes;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class RapscallionsAndRockhoppersClient {
    public static void createEntityLayers(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {
        consumer.accept(PenguinModel.LAYER_LOCATION, PenguinModel::createLayer);
    }

    public interface EntityRendererCallback {
        <T extends Entity> void accept(EntityType<? extends T> entityType,
                                       EntityRendererProvider<T> factory);
    }

    public static void createRenderers(EntityRendererCallback consumer) {
        consumer.accept(RockhoppersEntityTypes.PENGUIN, PenguinRenderer::new);
    }
}
