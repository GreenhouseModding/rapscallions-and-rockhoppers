package dev.greenhouseteam.rapscallionsandrockhoppers.datagen;

import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersBlocks;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersItems;
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

public class RapscallionsAndRockhoppersDatagen implements DataGeneratorEntrypoint {
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
            createEgg(RapscallionsAndRockhoppersBlocks.PENGUIN_EGG, blockStateModelGenerator);
        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerator) {
            itemModelGenerator.generateFlatItem(RapscallionsAndRockhoppersItems.PENGUIN_EGG, ModelTemplates.FLAT_ITEM);
            itemModelGenerator.generateFlatItem(RapscallionsAndRockhoppersItems.PENGUIN_SPAWN_EGG, RapscallionsAndRockhoppersModelTemplates.SPAWN_EGG);

        }
        public void createEgg(Block egg, BlockModelGenerators blockModelGenerators) {
            TextureMapping textureMapping = RapscallionsAndRockhoppersTextureMappings.createEggMapping(egg);
            TextureMapping slightlyCrackedTextureMapping = RapscallionsAndRockhoppersTextureMappings.createEggMapping(egg, "_slightly_cracked");
            TextureMapping veryCrackedTextureMapping = RapscallionsAndRockhoppersTextureMappings.createEggMapping(egg, "_very_cracked");
            var eggModel = RapscallionsAndRockhoppersModelTemplates.EGG.create(egg, textureMapping, blockModelGenerators.modelOutput);
            var slightlyCrackedEggModel = RapscallionsAndRockhoppersModelTemplates.EGG.createWithSuffix(egg, "_slightly_cracked", slightlyCrackedTextureMapping, blockModelGenerators.modelOutput);
            var veryCrackedEggModel = RapscallionsAndRockhoppersModelTemplates.EGG.createWithSuffix(egg, "_very_cracked", veryCrackedTextureMapping, blockModelGenerators.modelOutput);
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
            dropWhenSilkTouch(RapscallionsAndRockhoppersBlocks.PENGUIN_EGG);
        }
    }
}
