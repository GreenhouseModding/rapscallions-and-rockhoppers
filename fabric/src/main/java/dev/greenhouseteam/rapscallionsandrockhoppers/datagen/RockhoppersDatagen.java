package dev.greenhouseteam.rapscallionsandrockhoppers.datagen;

import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersBlocks;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class RockhoppersDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(RapscallionsAndRockhoppersModelProvider::new);
        pack.addProvider(RapscallionsAndRockhoppersBlockLootProvider::new);

    }
    public static class RapscallionsAndRockhoppersModelProvider extends FabricModelProvider {

        public RapscallionsAndRockhoppersModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
            createEgg(RockhoppersBlocks.PENGUIN_EGG, blockStateModelGenerator);
        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerator) {
            itemModelGenerator.generateFlatItem(RockhoppersItems.PENGUIN_EGG, ModelTemplates.FLAT_ITEM);
            itemModelGenerator.generateFlatItem(RockhoppersItems.PENGUIN_SPAWN_EGG, RockhoppersModelTemplates.SPAWN_EGG);

        }
        public void createEgg(Block egg, BlockModelGenerators blockModelGenerators) {
            TextureMapping textureMapping = RockhoppersTextureMappings.createEggMapping(egg);
            TextureMapping slightlyCrackedTextureMapping = RockhoppersTextureMappings.createEggMapping(egg, "_slightly_cracked");
            TextureMapping veryCrackedTextureMapping = RockhoppersTextureMappings.createEggMapping(egg, "_very_cracked");
            var eggModel = RockhoppersModelTemplates.EGG.create(egg, textureMapping, blockModelGenerators.modelOutput);
            var slightlyCrackedEggModel = RockhoppersModelTemplates.EGG.createWithSuffix(egg, "_slightly_cracked", slightlyCrackedTextureMapping, blockModelGenerators.modelOutput);
            var veryCrackedEggModel = RockhoppersModelTemplates.EGG.createWithSuffix(egg, "_very_cracked", veryCrackedTextureMapping, blockModelGenerators.modelOutput);
            var multiVariant = MultiVariantGenerator.multiVariant(egg)
                            .with(PropertyDispatch.property(BlockStateProperties.HATCH)
                                            .select(0, Variant.variant().with(VariantProperties.MODEL, eggModel))
                                            .select(1, Variant.variant().with(VariantProperties.MODEL, slightlyCrackedEggModel))
                                            .select(2, Variant.variant().with(VariantProperties.MODEL, veryCrackedEggModel)));
            blockModelGenerators.blockStateOutput.accept(multiVariant);
        }
    }
    public static class RapscallionsAndRockhoppersBlockLootProvider extends FabricBlockLootTableProvider {
        protected RapscallionsAndRockhoppersBlockLootProvider(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            dropWhenSilkTouch(RockhoppersBlocks.PENGUIN_EGG);
        }
    }
}
