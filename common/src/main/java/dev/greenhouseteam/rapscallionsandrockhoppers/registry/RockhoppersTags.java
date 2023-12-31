package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import javax.swing.text.html.parser.Entity;

public class RockhoppersTags {

    public static class ItemTags {
        public static final TagKey<Item> PENGUIN_TEMPT_ITEMS = TagKey.create(Registries.ITEM, RapscallionsAndRockhoppers.asResource("penguin_tempt_items"));
        public static final TagKey<Item> PENGUIN_BREED_ITEMS = TagKey.create(Registries.ITEM, RapscallionsAndRockhoppers.asResource("penguin_breed_items"));
        public static final TagKey<Item> PENGUIN_FOOD_ITEMS = TagKey.create(Registries.ITEM, RapscallionsAndRockhoppers.asResource("penguin_food_items"));
    }

    public static class BlockTags {
        public static final TagKey<Block> ROCKHOPPER_PENGUIN_SPAWN_BLOCKS = TagKey.create(Registries.BLOCK, RapscallionsAndRockhoppers.asResource("rockhopper_penguin_spawn_blocks"));
        public static final TagKey<Block> CHINSTRAP_PENGUIN_SPAWN_BLOCKS = TagKey.create(Registries.BLOCK, RapscallionsAndRockhoppers.asResource("chinstrap_penguin_spawn_blocks"));
    }

    public static class BiomeTags {
        public static final TagKey<Biome> ROCKHOPPER_PENGUIN_SPAWN_BIOMES = TagKey.create(Registries.BIOME, RapscallionsAndRockhoppers.asResource("rockhopper_penguin_spawn_biomes"));
        public static final TagKey<Biome> CHINSTRAP_PENGUIN_SPAWN_BIOMES = TagKey.create(Registries.BIOME, RapscallionsAndRockhoppers.asResource("chinstrap_penguin_spawn_biomes"));
    }

    public static class EntityTypeTags {
        public static final TagKey<EntityType<?>> PENGUIN_ALWAYS_HOSTILES = TagKey.create(Registries.ENTITY_TYPE, RapscallionsAndRockhoppers.asResource("penguin_always_hostiles"));
    }

}
