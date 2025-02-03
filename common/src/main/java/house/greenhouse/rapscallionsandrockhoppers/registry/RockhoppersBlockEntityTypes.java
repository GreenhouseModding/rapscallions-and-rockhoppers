package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.block.entity.PenguinEggBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RockhoppersBlockEntityTypes {
    public static final BlockEntityType<PenguinEggBlockEntity> PENGUIN_EGG = BlockEntityType.Builder.of(PenguinEggBlockEntity::new, RockhoppersBlocks.PENGUIN_EGG).build(null);

    public static void registerBlockEntityTypes() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, RapscallionsAndRockhoppers.asResource("penguin_egg"), PENGUIN_EGG);
    }
}
