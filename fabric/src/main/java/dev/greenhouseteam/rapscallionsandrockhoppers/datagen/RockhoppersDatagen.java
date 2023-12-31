package dev.greenhouseteam.rapscallionsandrockhoppers.datagen;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.PenguinType;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersBlocks;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersItems;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersLootTables;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersTags;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.WeightedHolderSet;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class RockhoppersDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(RockhoppersBiomeTagProvider::new);
        pack.addProvider(RockhoppersItemTagProvider::new);
        pack.addProvider(RockhoppersEntityTagProvider::new);
        pack.addProvider(RockhoppersDynamicRegistryProvider::new);
        pack.addProvider(RockhoppersModelProvider::new);
        pack.addProvider(RockhoppersBlockLootProvider::new);
        pack.addProvider(RockhoppersRecipeProvider::new);
        pack.addProvider(RockhoppersLootTableProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY, v -> {});
    }

    public static class RockhoppersDynamicRegistryProvider extends FabricDynamicRegistryProvider {

        public RockhoppersDynamicRegistryProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(HolderLookup.Provider registries, Entries entries) {
            Optional<Holder<SoundEvent>> idleSound = Optional.of(registries.lookup(Registries.SOUND_EVENT).orElseThrow().getOrThrow(RockhoppersResourceKeys.SoundEventKeys.PENGUIN_AMBIENT));
            Optional<Holder<SoundEvent>> hurtSound = Optional.of(registries.lookup(Registries.SOUND_EVENT).orElseThrow().getOrThrow(RockhoppersResourceKeys.SoundEventKeys.PENGUIN_HURT));
            Optional<Holder<SoundEvent>> deathSound = Optional.of(registries.lookup(Registries.SOUND_EVENT).orElseThrow().getOrThrow(RockhoppersResourceKeys.SoundEventKeys.PENGUIN_DEATH));
            Optional<Holder<SoundEvent>> waterJumpSound = Optional.of(registries.lookup(Registries.SOUND_EVENT).orElseThrow().getOrThrow(RockhoppersResourceKeys.SoundEventKeys.PENGUIN_JUMP));

            entries.add(RockhoppersResourceKeys.PenguinTypeKeys.ROCKHOPPER, new PenguinType(
                    Optional.empty(), Optional.empty(), List.of(new WeightedHolderSet<>(new DummyHolderSet<>(RockhoppersTags.BiomeTags.ROCKHOPPER_PENGUIN_SPAWN_BIOMES), 1)),
                    new PenguinType.PenguinSounds(idleSound, hurtSound, deathSound, waterJumpSound), Optional.empty()));
            entries.add(RockhoppersResourceKeys.PenguinTypeKeys.CHINSTRAP, new PenguinType(
                    Optional.empty(), Optional.empty(), List.of(new WeightedHolderSet<>(new DummyHolderSet<>(RockhoppersTags.BiomeTags.CHINSTRAP_PENGUIN_SPAWN_BIOMES), 1)),
                    new PenguinType.PenguinSounds(idleSound, hurtSound, deathSound, waterJumpSound), Optional.empty()));
            entries.add(RockhoppersResourceKeys.PenguinTypeKeys.GUNTER, new PenguinType(
                    Optional.empty(), Optional.of(RapscallionsAndRockhoppers.asResource("penguin/gunter_penguin")), List.of(),
                    new PenguinType.PenguinSounds(idleSound, hurtSound, deathSound, waterJumpSound), Optional.of("Gunter")));
        }

        @Override
        public String getName() {
            return "Rapscallions and Rockhoppers Dynamic Registries";
        }
    }

    public static class RockhoppersModelProvider extends FabricModelProvider {

        public RockhoppersModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
            createEgg(RockhoppersBlocks.PENGUIN_EGG, blockStateModelGenerator);
        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerator) {
            itemModelGenerator.generateFlatItem(RockhoppersItems.BOAT_HOOK, ModelTemplates.FLAT_ITEM);
            itemModelGenerator.generateFlatItem(RockhoppersItems.FISH_BONES, ModelTemplates.FLAT_ITEM);
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
    public static class RockhoppersBlockLootProvider extends FabricBlockLootTableProvider {
        protected RockhoppersBlockLootProvider(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            dropWhenSilkTouch(RockhoppersBlocks.PENGUIN_EGG);
        }
    }

    public static class RockhoppersRecipeProvider extends FabricRecipeProvider {
        public RockhoppersRecipeProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void buildRecipes(RecipeOutput exporter) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL, 3).group("bonemeal").requires(RockhoppersItems.FISH_BONES)
                    .unlockedBy("has_bone_block", FabricRecipeProvider.has(RockhoppersItems.FISH_BONES))
                    .save(exporter, "bone_meal_from_fish_bones");
            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RockhoppersItems.BOAT_HOOK).pattern("F~ ").pattern("~O ").pattern("  ~")
                    .define('F', RockhoppersItems.FISH_BONES).define('~', Items.STRING).define('O', Items.SLIME_BALL)
                    .unlockedBy("has_fish_bones", FabricRecipeProvider.has(RockhoppersItems.FISH_BONES))
                    .save(exporter, "boat_hook");
        }

    }

    public static class RockhoppersLootTableProvider extends SimpleFabricLootTableProvider {

        public RockhoppersLootTableProvider(FabricDataOutput output) {
            super(output, LootContextParamSets.GIFT);
        }

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
            biConsumer.accept(RockhoppersLootTables.PENGUIN_COUGH_UP_INK_SAC, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(3.0F))
                            .with(LootItem.lootTableItem(Items.INK_SAC).setWeight(3).build())
                            .with(LootItem.lootTableItem(Items.GLOW_INK_SAC).setWeight(1).build())));

            biConsumer.accept(RockhoppersLootTables.PENGUIN_COUGH_UP_ROCKS, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(2.0F))
                            .with(LootItem.lootTableItem(Items.FLINT).setWeight(2).build())
                            .with(LootItem.lootTableItem(Items.PRISMARINE_SHARD).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F))).setWeight(1).build())
                            .with(LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F))).setWeight(1).build())));

            biConsumer.accept(RockhoppersLootTables.PENGUIN_COUGH_UP, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .with(LootItem.lootTableItem(RockhoppersItems.FISH_BONES).build()))
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .with(LootTableReference.lootTableReference(BuiltInLootTables.FISHING_JUNK).setWeight(20).build())
                            .with(LootTableReference.lootTableReference(RockhoppersLootTables.PENGUIN_COUGH_UP_INK_SAC).setWeight(15).build())
                            .with(LootTableReference.lootTableReference(RockhoppersLootTables.PENGUIN_COUGH_UP_ROCKS).setWeight(10).build())
                            .with(LootItem.lootTableItem(Items.BOOK).setWeight(2).apply(EnchantRandomlyFunction.randomEnchantment().withEnchantment(Enchantments.DEPTH_STRIDER)).build())
                            .with(LootItem.lootTableItem(Items.NAUTILUS_SHELL).setWeight(2).build())
                            .with(LootItem.lootTableItem(Items.NAME_TAG).setWeight(1).build())));
        }
    }

    public static class RockhoppersItemTagProvider extends FabricTagProvider.ItemTagProvider {

        public RockhoppersItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            this.tag(RockhoppersTags.ItemTags.PENGUIN_BREED_ITEMS)
                    .add(this.reverseLookup(Items.INK_SAC), this.reverseLookup(Items.GLOW_INK_SAC));
            this.tag(RockhoppersTags.ItemTags.PENGUIN_TEMPT_ITEMS)
                    .add(this.reverseLookup(Items.COD), this.reverseLookup(Items.SALMON), this.reverseLookup(Items.TROPICAL_FISH))
                    .addTag(RockhoppersTags.ItemTags.PENGUIN_BREED_ITEMS);
        }
    }

    public static class RockhoppersBiomeTagProvider extends FabricTagProvider<Biome> {

        public RockhoppersBiomeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, Registries.BIOME, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            this.tag(RockhoppersTags.BiomeTags.ROCKHOPPER_PENGUIN_SPAWN_BIOMES).add(Biomes.STONY_SHORE);
            this.tag(RockhoppersTags.BiomeTags.CHINSTRAP_PENGUIN_SPAWN_BIOMES).add(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
        }
    }

    public static class RockhoppersEntityTagProvider extends FabricTagProvider<EntityType<?>> {

        public RockhoppersEntityTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, Registries.ENTITY_TYPE, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            this.tag(RockhoppersTags.EntityTypeTags.PENGUIN_ALWAYS_HOSTILES).add(this.reverseLookup(EntityType.COD), this.reverseLookup(EntityType.SALMON), this.reverseLookup(EntityType.TROPICAL_FISH), this.reverseLookup(EntityType.SQUID), this.reverseLookup(EntityType.GLOW_SQUID));
        }
    }
}
