package dev.greenhouseteam.rapscallionsandrockhoppers.client.renderer;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.client.renderer.model.PenguinModel;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PenguinRenderer extends MobRenderer<Penguin, PenguinModel> {
    private static final ResourceLocation PENGUIN_TEXTURE = RapscallionsAndRockhoppers.asResource("textures/entity/penguin/penguin.png");
    private static final ResourceLocation SURPRISED_PENGUIN_TEXTURE = RapscallionsAndRockhoppers.asResource("textures/entity/penguin/penguin_surprised.png");

    public PenguinRenderer(EntityRendererProvider.Context context) {
        super(context, new PenguinModel(context.bakeLayer(PenguinModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(Penguin penguin) {
        return penguin.isShocked() || penguin.isStumbling() ? SURPRISED_PENGUIN_TEXTURE : PENGUIN_TEXTURE;
    }
}
