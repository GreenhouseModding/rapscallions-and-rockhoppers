package dev.greenhouseteam.rapscallionsandrockhoppers.datagen;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class RockhoppersModelTemplates {
    public static final ModelTemplate EGG = new ModelTemplate(
            Optional.of(RapscallionsAndRockhoppers.asResource("block/penguin_egg_template")),
            Optional.empty(),
            RockhoppersTextureMappings.EGG_TEXTURE
    );
    public static final ModelTemplate SPAWN_EGG = new ModelTemplate(
            Optional.of(ResourceLocation.fromNamespaceAndPath("minecraft", "item/template_spawn_egg")),
            Optional.empty()
    );
}
