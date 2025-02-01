package house.greenhouse.rapscallionsandrockhoppers.client.renderer;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.client.renderer.model.PenguinModel;
import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.entity.PenguinVariant;
import house.greenhouse.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class PenguinRenderer extends MobRenderer<Penguin, PenguinModel> {

    private static final Map<ResourceLocation, Boolean> PENGUIN_TEXTURE_CACHE = new HashMap<>();

    public PenguinRenderer(EntityRendererProvider.Context context) {
        super(context, new PenguinModel(context.bakeLayer(PenguinModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(Penguin penguin) {
        if (penguin.isShocked() || penguin.isStumbling() && !penguin.isGettingUp()) {
            ResourceLocation textureLocation = penguin.getVariant().value().surprisedTexture().withPath(path -> "textures/" + path + ".png");
            addToTextureCache(textureLocation);
            if (PENGUIN_TEXTURE_CACHE.getOrDefault(textureLocation, false))
                return textureLocation;
        } else {
            ResourceLocation textureLocation = penguin.getVariant().value().texture().withPath(path -> "textures/" + path + ".png");
            addToTextureCache(textureLocation);
            if (PENGUIN_TEXTURE_CACHE.getOrDefault(textureLocation, false))
                return textureLocation;
        }
        return penguin.level().registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_VARIANT).getOrThrow(RockhoppersResourceKeys.PenguinTypeKeys.ROCKHOPPER).texture();
    }

    private void addToTextureCache(ResourceLocation textureLocation) {
        PENGUIN_TEXTURE_CACHE.computeIfAbsent(textureLocation, tl -> Minecraft.getInstance().getResourceManager().getResource(tl).isPresent());
    }
}
