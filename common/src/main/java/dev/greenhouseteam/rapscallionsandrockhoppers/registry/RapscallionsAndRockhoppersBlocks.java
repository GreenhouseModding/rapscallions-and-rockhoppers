package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.block.PenguinEggBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.apache.logging.log4j.util.TriConsumer;

public class RapscallionsAndRockhoppersBlocks {
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
    public static void registerBlocks(TriConsumer<Registry<Block>, ResourceLocation, Block> consumer) {
        consumer.accept(BuiltInRegistries.BLOCK, RapscallionsAndRockhoppers.asResource("penguin_egg"), PENGUIN_EGG);
    }
}
