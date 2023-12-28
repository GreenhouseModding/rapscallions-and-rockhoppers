package dev.greenhouseteam.rapscallionsandrockhoppers.client.renderer;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import dev.greenhouseteam.rapscallionsandrockhoppers.client.renderer.model.PenguinModel;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PenguinRenderer extends MobRenderer<Penguin, PenguinModel> {

    private static Map<ResourceLocation, Boolean> PENGUIN_TEXTURE_CACHE = new HashMap<>();

    public PenguinRenderer(EntityRendererProvider.Context context) {
        super(context, new PenguinModel(context.bakeLayer(PenguinModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(Penguin penguin) {
        if (penguin.isShocked() || penguin.isStumbling() && !penguin.isGettingUp()) {
            ResourceLocation textureLocation = penguin.getPenguinType().surprisedTextureLocation().map(resourceLocation -> new ResourceLocation(resourceLocation.getNamespace(), "textures/entity/" + resourceLocation.getPath() + ".png")).orElse(new ResourceLocation(penguin.getPenguinTypeKey().getNamespace(), "textures/entity/penguin/" + penguin.getPenguinTypeKey().getPath() + "_penguin_surprised.png"));
            addToTextureCache(textureLocation);
            if (PENGUIN_TEXTURE_CACHE.getOrDefault(textureLocation, false)) {
                return textureLocation;
            }
        } else {
            ResourceLocation textureLocation = penguin.getPenguinType().textureLocation().map(resourceLocation -> new ResourceLocation(resourceLocation.getNamespace(), "textures/entity/" + resourceLocation.getPath() + ".png")).orElse(new ResourceLocation(penguin.getPenguinTypeKey().getNamespace(), "textures/entity/penguin/" + penguin.getPenguinTypeKey().getPath() + "_penguin.png"));
            addToTextureCache(textureLocation);
            if (PENGUIN_TEXTURE_CACHE.getOrDefault(textureLocation, false)) {
                return textureLocation;
            }
        }
        return penguin.getPenguinType().surprisedTextureLocation().orElse(new ResourceLocation(penguin.getPenguinTypeKey().getNamespace(), "textures/entity/rapscallionsandrockhoppers/penguin/missing_penguin.png"));
    }

    private void addToTextureCache(ResourceLocation textureLocation) {
        PENGUIN_TEXTURE_CACHE.computeIfAbsent(textureLocation, tl -> Minecraft.getInstance().getResourceManager().getResource(tl).isPresent());
    }
}
