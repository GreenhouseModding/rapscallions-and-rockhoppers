package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.item.BoatHookItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import org.apache.logging.log4j.util.BiConsumer;

import java.util.function.Consumer;

public class RockhoppersItems {
    public static final Item PENGUIN_EGG = new BlockItem(RockhoppersBlocks.PENGUIN_EGG, new Item.Properties());
    public static final Item PENGUIN_SPAWN_EGG = new SpawnEggItem(RockhoppersEntityTypes.PENGUIN, 0x232232, 0xEBD149, new Item.Properties());
    public static final Item BOAT_HOOK = new BoatHookItem(new Item.Properties());
    public static final Item FISH_BONES = new Item(new Item.Properties());

    public static void registerItems() {
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("penguin_egg"), PENGUIN_EGG);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("penguin_spawn_egg"), PENGUIN_SPAWN_EGG);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("boat_hook"), BOAT_HOOK);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("fish_bones"), FISH_BONES);
    }

    public static void addBeforeToolsAndUtilitiesTab(BiConsumer<ItemStack, ItemStack> consumer) {
        consumer.accept(new ItemStack(Items.RAIL), new ItemStack(BOAT_HOOK));
    }

    public static void addAfterIngredientsTab(BiConsumer<ItemStack, ItemStack> consumer) {
        consumer.accept(new ItemStack(Items.BONE_MEAL), new ItemStack(FISH_BONES));
    }

    public static void addAfterNaturalBlocksTab(BiConsumer<ItemStack, ItemStack> consumer) {
        consumer.accept(new ItemStack(Items.TURTLE_EGG), new ItemStack(PENGUIN_EGG));
    }

    public static void addSpawnEggsTab(Consumer<ItemStack> consumer) {
        consumer.accept(new ItemStack(PENGUIN_SPAWN_EGG));
    }
}
