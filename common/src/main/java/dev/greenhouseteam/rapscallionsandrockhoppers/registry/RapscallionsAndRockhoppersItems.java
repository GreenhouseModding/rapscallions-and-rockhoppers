package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.apache.logging.log4j.util.TriConsumer;

public class RapscallionsAndRockhoppersItems {
    public static final Item PENGUIN_EGG = new BlockItem(RapscallionsAndRockhoppersBlocks.PENGUIN_EGG, new Item.Properties());
    public static final Item PENGUIN_SPAWN_EGG = new SpawnEggItem(RapscallionsAndRockhoppersEntityTypes.PENGUIN, 0x232232, 0xEBD149, new Item.Properties());
    public static void registerItems(TriConsumer<Registry<Item>, ResourceLocation, Item> consumer) {
        consumer.accept(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("penguin_egg"), PENGUIN_EGG);
        consumer.accept(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("penguin_spawn_egg"), PENGUIN_SPAWN_EGG);
    }
}
