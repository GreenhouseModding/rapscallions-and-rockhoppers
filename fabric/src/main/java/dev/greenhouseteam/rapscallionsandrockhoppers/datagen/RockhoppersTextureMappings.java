package dev.greenhouseteam.rapscallionsandrockhoppers.datagen;

import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.world.level.block.Block;

import static net.minecraft.data.models.model.TextureMapping.getBlockTexture;

public class RockhoppersTextureMappings {
    public static final TextureSlot EGG_TEXTURE = TextureSlot.create("egg_texture");

    public static TextureMapping createEggMapping(Block eggBlock) {
        return new TextureMapping().put(EGG_TEXTURE, getBlockTexture(eggBlock));
    }
    public static TextureMapping createEggMapping(Block eggBlock, String suffix) {
        return new TextureMapping().put(EGG_TEXTURE, getBlockTexture(eggBlock, suffix));
    }
}
