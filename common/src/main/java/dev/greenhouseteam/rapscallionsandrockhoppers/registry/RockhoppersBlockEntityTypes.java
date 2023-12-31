package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.block.entity.PenguinEggBlockEntity;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RegisterFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RockhoppersBlockEntityTypes {
    public static final BlockEntityType<PenguinEggBlockEntity> PENGUIN_EGG = BlockEntityType.Builder.of(PenguinEggBlockEntity::new, RockhoppersBlocks.PENGUIN_EGG).build(null);

    public static void registerBlockEntityTypes(RegisterFunction<BlockEntityType<?>> function) {
        function.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, RapscallionsAndRockhoppers.asResource("penguin_egg"), PENGUIN_EGG);
    }
}
