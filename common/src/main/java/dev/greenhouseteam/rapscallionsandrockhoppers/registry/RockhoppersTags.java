package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class RockhoppersTags {

    public static class ItemTags {
        public static final TagKey<Item> PENGUIN_TEMPT_ITEMS = TagKey.create(Registries.ITEM, RapscallionsAndRockhoppers.asResource("penguin_tempt_items"));
        public static final TagKey<Item> PENGUIN_BREED_ITEMS = TagKey.create(Registries.ITEM, RapscallionsAndRockhoppers.asResource("penguin_breed_items"));
    }
}
