package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.block.PenguinEggBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class RockhoppersBlocks {
    public static final Block PENGUIN_EGG = new PenguinEggBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .forceSolidOn()
                    .strength(0.5F)
                    .sound(SoundType.METAL)
                    .randomTicks()
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
    );
    
    public static final Block SEAHORSE_FISH_SCALE_BLOCK = createFishScaleBlock(MapColor.COLOR_RED);
    public static final Block SEAHORSE_FISH_SCALE_SLAB = new SlabBlock(BlockBehaviour.Properties.ofLegacyCopy(SEAHORSE_FISH_SCALE_BLOCK));
    public static final Block SEAHORSE_FISH_SCALE_STAIRS = new StairBlock(SEAHORSE_FISH_SCALE_BLOCK.defaultBlockState(), BlockBehaviour.Properties.ofLegacyCopy(SEAHORSE_FISH_SCALE_BLOCK));
    public static final Block SEAHORSE_FISH_SCALE_WALL = new WallBlock(BlockBehaviour.Properties.ofLegacyCopy(SEAHORSE_FISH_SCALE_BLOCK));
    public static final Block EEL_FISH_SCALE_BLOCK = createFishScaleBlock(MapColor.COLOR_LIGHT_GREEN);
    public static final Block EEL_FISH_SCALE_SLAB = new SlabBlock(BlockBehaviour.Properties.ofLegacyCopy(EEL_FISH_SCALE_BLOCK));
    public static final Block EEL_FISH_SCALE_STAIRS = new StairBlock(EEL_FISH_SCALE_BLOCK.defaultBlockState(), BlockBehaviour.Properties.ofLegacyCopy(EEL_FISH_SCALE_BLOCK));
    public static final Block EEL_FISH_SCALE_WALL = new WallBlock(BlockBehaviour.Properties.ofLegacyCopy(EEL_FISH_SCALE_BLOCK));
    public static final Block JELLYFISH_FISH_SCALE_BLOCK = createFishScaleBlock(MapColor.TERRACOTTA_PURPLE);
    public static final Block JELLYFISH_FISH_SCALE_SLAB = new SlabBlock(BlockBehaviour.Properties.ofLegacyCopy(JELLYFISH_FISH_SCALE_BLOCK));
    public static final Block JELLYFISH_FISH_SCALE_STAIRS = new StairBlock(JELLYFISH_FISH_SCALE_BLOCK.defaultBlockState(), BlockBehaviour.Properties.ofLegacyCopy(JELLYFISH_FISH_SCALE_BLOCK));
    public static final Block JELLYFISH_FISH_SCALE_WALL = new WallBlock(BlockBehaviour.Properties.ofLegacyCopy(JELLYFISH_FISH_SCALE_BLOCK));
    public static final Block SHARK_FISH_SCALE_BLOCK = createFishScaleBlock(MapColor.COLOR_GRAY);
    public static final Block SHARK_FISH_SCALE_SLAB = new SlabBlock(BlockBehaviour.Properties.ofLegacyCopy(SHARK_FISH_SCALE_BLOCK));
    public static final Block SHARK_FISH_SCALE_STAIRS = new StairBlock(SHARK_FISH_SCALE_BLOCK.defaultBlockState(), BlockBehaviour.Properties.ofLegacyCopy(SHARK_FISH_SCALE_BLOCK));
    public static final Block SHARK_FISH_SCALE_WALL = new WallBlock(BlockBehaviour.Properties.ofLegacyCopy(SHARK_FISH_SCALE_BLOCK));
    
    public static void registerBlocks() {
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("penguin_egg"), PENGUIN_EGG);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("seahorse_fish_scale_block"), SEAHORSE_FISH_SCALE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("seahorse_fish_scale_slab"), SEAHORSE_FISH_SCALE_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("seahorse_fish_scale_stairs"), SEAHORSE_FISH_SCALE_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("seahorse_fish_scale_wall"), SEAHORSE_FISH_SCALE_WALL);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("eel_fish_scale_block"), EEL_FISH_SCALE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("eel_fish_scale_slab"), EEL_FISH_SCALE_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("eel_fish_scale_stairs"), EEL_FISH_SCALE_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("eel_fish_scale_wall"), EEL_FISH_SCALE_WALL);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("jellyfish_fish_scale_block"), JELLYFISH_FISH_SCALE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("jellyfish_fish_scale_slab"), JELLYFISH_FISH_SCALE_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("jellyfish_fish_scale_stairs"), JELLYFISH_FISH_SCALE_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("jellyfish_fish_scale_wall"), JELLYFISH_FISH_SCALE_WALL);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("shark_fish_scale_block"), SHARK_FISH_SCALE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("shark_fish_scale_slab"), SHARK_FISH_SCALE_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("shark_fish_scale_stairs"), SHARK_FISH_SCALE_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("shark_fish_scale_wall"), SHARK_FISH_SCALE_WALL);
    }
    
    public static Block createFishScaleBlock(MapColor color) {
        return new Block(BlockBehaviour.Properties.of()
                .mapColor(color)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 4.0F));
    }
}
