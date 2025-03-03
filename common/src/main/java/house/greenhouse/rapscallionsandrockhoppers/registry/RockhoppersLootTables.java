package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class RockhoppersLootTables {
    public static final ResourceKey<LootTable> PENGUIN_COUGH_UP_FEED = register("gameplay/penguin_cough_up/feed");
    public static final ResourceKey<LootTable> PENGUIN_COUGH_UP_TRAVEL = register("gameplay/penguin_cough_up/travel");
    public static final ResourceKey<LootTable> PENGUIN_COUGH_UP_INK_SAC = register("gameplay/penguin_cough_up/ink_sac");
    public static final ResourceKey<LootTable> PENGUIN_COUGH_UP_ROCKS = register("gameplay/penguin_cough_up/rocks");

    private static ResourceKey<LootTable> register(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, RapscallionsAndRockhoppers.asResource(name));
    }
}
