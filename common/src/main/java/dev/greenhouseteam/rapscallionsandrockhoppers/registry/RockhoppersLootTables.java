package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class RockhoppersLootTables {
    public static final ResourceKey<LootTable> PENGUIN_COUGH_UP = register("gameplay/penguin_cough_up");
    public static final ResourceKey<LootTable> PENGUIN_COUGH_UP_INK_SAC = register("gameplay/penguin_cough_up_ink_sac");
    public static final ResourceKey<LootTable> PENGUIN_COUGH_UP_ROCKS = register("gameplay/penguin_cough_up_rocks");

    private static ResourceKey<LootTable> register(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, RapscallionsAndRockhoppers.asResource(name));
    }
}
