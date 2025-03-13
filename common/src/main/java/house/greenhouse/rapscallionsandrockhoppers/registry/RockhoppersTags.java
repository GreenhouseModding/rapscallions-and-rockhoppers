package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class RockhoppersTags {

    public static class ItemTags {
        public static final TagKey<Item> PENGUIN_TEMPT_ITEMS = TagKey.create(Registries.ITEM, RapscallionsAndRockhoppers.asResource("penguin_tempt_items"));
        public static final TagKey<Item> PENGUIN_BREED_ITEMS = TagKey.create(Registries.ITEM, RapscallionsAndRockhoppers.asResource("penguin_breed_items"));
        public static final TagKey<Item> PENGUIN_FOOD = TagKey.create(Registries.ITEM, RapscallionsAndRockhoppers.asResource("penguin_food"));
        @Deprecated
        public static final TagKey<Item> PENGUIN_FOOD_ITEMS = PENGUIN_FOOD;

        public static final TagKey<Item> SEAHORSE_FISH_SCALE_BLOCK_DYES = TagKey.create(Registries.ITEM, RapscallionsAndRockhoppers.asResource("fish_scale_block_dyes/seahorse"));
        public static final TagKey<Item> EEL_FISH_SCALE_BLOCK_DYES = TagKey.create(Registries.ITEM, RapscallionsAndRockhoppers.asResource("fish_scale_block_dyes/eel"));
        public static final TagKey<Item> JELLYFISH_FISH_SCALE_BLOCK_DYES = TagKey.create(Registries.ITEM, RapscallionsAndRockhoppers.asResource("fish_scale_block_dyes/jellyfish"));
        public static final TagKey<Item> SHARK_FISH_SCALE_BLOCK_DYES = TagKey.create(Registries.ITEM, RapscallionsAndRockhoppers.asResource("fish_scale_block_dyes/shark"));
    }

    public static class BiomeTags {
        public static final TagKey<Biome> SPAWNS_ROCKHOPPER_PENGUINS = TagKey.create(Registries.BIOME, RapscallionsAndRockhoppers.asResource("spawns_rockhopper_penguins"));
        public static final TagKey<Biome> SPAWNS_CHINSTRAP_PENGUINS = TagKey.create(Registries.BIOME, RapscallionsAndRockhoppers.asResource("spawns_chinstrap_penguins"));
        public static final TagKey<Biome> SPAWNS_LITTLE_PENGUINS = TagKey.create(Registries.BIOME, RapscallionsAndRockhoppers.asResource("spawns_little_penguins"));

    }

    public static class EntityTypeTags {
        public static final TagKey<EntityType<?>> PENGUIN_ALWAYS_HOSTILES = TagKey.create(Registries.ENTITY_TYPE, RapscallionsAndRockhoppers.asResource("penguin_always_hostiles"));
    }

}
