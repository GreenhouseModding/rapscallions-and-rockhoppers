package house.greenhouse.rapscallionsandrockhoppers.datagen;

import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersBlocks;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;

public class RockhoppersBlockFamilies {
    public static final BlockFamily SEAHORSE_FISH_SCALE = BlockFamilies.familyBuilder(RockhoppersBlocks.SEAHORSE_FISH_SCALE_BLOCK)
            .slab(RockhoppersBlocks.SEAHORSE_FISH_SCALE_SLAB)
            .stairs(RockhoppersBlocks.SEAHORSE_FISH_SCALE_STAIRS)
            .wall(RockhoppersBlocks.SEAHORSE_FISH_SCALE_WALL)
            .getFamily();

    public static final BlockFamily EEL_FISH_SCALE = BlockFamilies.familyBuilder(RockhoppersBlocks.EEL_FISH_SCALE_BLOCK)
            .slab(RockhoppersBlocks.EEL_FISH_SCALE_SLAB)
            .stairs(RockhoppersBlocks.EEL_FISH_SCALE_STAIRS)
            .wall(RockhoppersBlocks.EEL_FISH_SCALE_WALL)
            .getFamily();

    public static final BlockFamily JELLYFISH_FISH_SCALE = BlockFamilies.familyBuilder(RockhoppersBlocks.JELLYFISH_FISH_SCALE_BLOCK)
            .slab(RockhoppersBlocks.JELLYFISH_FISH_SCALE_SLAB)
            .stairs(RockhoppersBlocks.JELLYFISH_FISH_SCALE_STAIRS)
            .wall(RockhoppersBlocks.JELLYFISH_FISH_SCALE_WALL)
            .getFamily();

    public static final BlockFamily SHARK_FISH_SCALE = BlockFamilies.familyBuilder(RockhoppersBlocks.SHARK_FISH_SCALE_BLOCK)
            .slab(RockhoppersBlocks.SHARK_FISH_SCALE_SLAB)
            .stairs(RockhoppersBlocks.SHARK_FISH_SCALE_STAIRS)
            .wall(RockhoppersBlocks.SHARK_FISH_SCALE_WALL)
            .getFamily();
}
