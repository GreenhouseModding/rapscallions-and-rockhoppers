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
    public static final Item FISH_SCALE = new Item(new Item.Properties());

    public static final Item SEAHORSE_FISH_SCALE_BLOCK = new BlockItem(RockhoppersBlocks.SEAHORSE_FISH_SCALE_BLOCK, new Item.Properties());
    public static final Item SEAHORSE_FISH_SCALE_SLAB = new BlockItem(RockhoppersBlocks.SEAHORSE_FISH_SCALE_SLAB, new Item.Properties());
    public static final Item SEAHORSE_FISH_SCALE_STAIRS = new BlockItem(RockhoppersBlocks.SEAHORSE_FISH_SCALE_STAIRS, new Item.Properties());
    public static final Item SEAHORSE_FISH_SCALE_WALL = new BlockItem(RockhoppersBlocks.SEAHORSE_FISH_SCALE_WALL, new Item.Properties());

    public static final Item EEL_FISH_SCALE_BLOCK = new BlockItem(RockhoppersBlocks.EEL_FISH_SCALE_BLOCK, new Item.Properties());
    public static final Item EEL_FISH_SCALE_SLAB = new BlockItem(RockhoppersBlocks.EEL_FISH_SCALE_SLAB, new Item.Properties());
    public static final Item EEL_FISH_SCALE_STAIRS = new BlockItem(RockhoppersBlocks.EEL_FISH_SCALE_STAIRS, new Item.Properties());
    public static final Item EEL_FISH_SCALE_WALL = new BlockItem(RockhoppersBlocks.EEL_FISH_SCALE_WALL, new Item.Properties());

    public static final Item JELLYFISH_FISH_SCALE_BLOCK = new BlockItem(RockhoppersBlocks.JELLYFISH_FISH_SCALE_BLOCK, new Item.Properties());
    public static final Item JELLYFISH_FISH_SCALE_SLAB = new BlockItem(RockhoppersBlocks.JELLYFISH_FISH_SCALE_SLAB, new Item.Properties());
    public static final Item JELLYFISH_FISH_SCALE_STAIRS = new BlockItem(RockhoppersBlocks.JELLYFISH_FISH_SCALE_STAIRS, new Item.Properties());
    public static final Item JELLYFISH_FISH_SCALE_WALL = new BlockItem(RockhoppersBlocks.JELLYFISH_FISH_SCALE_WALL, new Item.Properties());

    public static final Item SHARK_FISH_SCALE_BLOCK = new BlockItem(RockhoppersBlocks.SHARK_FISH_SCALE_BLOCK, new Item.Properties());
    public static final Item SHARK_FISH_SCALE_SLAB = new BlockItem(RockhoppersBlocks.SHARK_FISH_SCALE_SLAB, new Item.Properties());
    public static final Item SHARK_FISH_SCALE_STAIRS = new BlockItem(RockhoppersBlocks.SHARK_FISH_SCALE_STAIRS, new Item.Properties());
    public static final Item SHARK_FISH_SCALE_WALL = new BlockItem(RockhoppersBlocks.SHARK_FISH_SCALE_WALL, new Item.Properties());

    public static void registerItems() {
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("penguin_egg"), PENGUIN_EGG);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("penguin_spawn_egg"), PENGUIN_SPAWN_EGG);

        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("boat_hook"), BOAT_HOOK);

        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("fish_bones"), FISH_BONES);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("fish_scale"), FISH_SCALE);

        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("seahorse_fish_scale_block"), SEAHORSE_FISH_SCALE_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("seahorse_fish_scale_slab"), SEAHORSE_FISH_SCALE_SLAB);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("seahorse_fish_scale_stairs"), SEAHORSE_FISH_SCALE_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("seahorse_fish_scale_wall"), SEAHORSE_FISH_SCALE_WALL);

        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("eel_fish_scale_block"), EEL_FISH_SCALE_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("eel_fish_scale_slab"), EEL_FISH_SCALE_SLAB);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("eel_fish_scale_stairs"), EEL_FISH_SCALE_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("eel_fish_scale_wall"), EEL_FISH_SCALE_WALL);

        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("jellyfish_fish_scale_block"), JELLYFISH_FISH_SCALE_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("jellyfish_fish_scale_slab"), JELLYFISH_FISH_SCALE_SLAB);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("jellyfish_fish_scale_stairs"), JELLYFISH_FISH_SCALE_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("jellyfish_fish_scale_wall"), JELLYFISH_FISH_SCALE_WALL);

        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("shark_fish_scale_block"), SHARK_FISH_SCALE_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("shark_fish_scale_slab"), SHARK_FISH_SCALE_SLAB);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("shark_fish_scale_stairs"), SHARK_FISH_SCALE_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, RapscallionsAndRockhoppers.asResource("shark_fish_scale_wall"), SHARK_FISH_SCALE_WALL);
    }

    public static void addBeforeToolsAndUtilitiesTab(BiConsumer<ItemStack, ItemStack> consumer) {
        consumer.accept(new ItemStack(Items.RAIL), new ItemStack(BOAT_HOOK));
    }

    public static void addAfterIngredientsTab(BiConsumer<ItemStack, ItemStack> consumer) {
        consumer.accept(new ItemStack(Items.BONE_MEAL), new ItemStack(FISH_BONES));
        consumer.accept(new ItemStack(FISH_BONES), new ItemStack(FISH_SCALE));
    }

    public static void addAfterNaturalBlocksTab(BiConsumer<ItemStack, ItemStack> consumer) {
        consumer.accept(new ItemStack(Items.TURTLE_EGG), new ItemStack(PENGUIN_EGG));
    }

    public static void addAfterBuildingBlocksTab(BiConsumer<ItemStack, ItemStack> consumer) {
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE), new ItemStack(SHARK_FISH_SCALE_WALL));
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(SHARK_FISH_SCALE_SLAB));
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(SHARK_FISH_SCALE_STAIRS));
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(SHARK_FISH_SCALE_BLOCK));
        
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(JELLYFISH_FISH_SCALE_WALL));
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(JELLYFISH_FISH_SCALE_SLAB));
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(JELLYFISH_FISH_SCALE_STAIRS));
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(JELLYFISH_FISH_SCALE_BLOCK));
        
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(EEL_FISH_SCALE_WALL));
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(EEL_FISH_SCALE_SLAB));
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(EEL_FISH_SCALE_STAIRS));
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(EEL_FISH_SCALE_BLOCK));
        
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(SEAHORSE_FISH_SCALE_WALL));
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(SEAHORSE_FISH_SCALE_SLAB));
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(SEAHORSE_FISH_SCALE_STAIRS));
        consumer.accept(new ItemStack(Items.DARK_PRISMARINE_SLAB), new ItemStack(SEAHORSE_FISH_SCALE_BLOCK));
    }

    public static void addSpawnEggsTab(Consumer<ItemStack> consumer) {
        consumer.accept(new ItemStack(PENGUIN_SPAWN_EGG));
    }
}
