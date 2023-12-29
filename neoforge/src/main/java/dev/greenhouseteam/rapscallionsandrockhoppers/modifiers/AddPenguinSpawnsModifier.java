package dev.greenhouseteam.rapscallionsandrockhoppers.modifiers;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersBiomeModifiers;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.MobSpawnSettingsBuilder;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

import java.util.List;

public record AddPenguinSpawnsModifier(List<MobSpawnSettings.SpawnerData> spawners) implements BiomeModifier {
    @Override
    public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (RapscallionsAndRockhoppers.getCachedPenguinTypeRegistry().stream().anyMatch(penguinType -> penguinType.spawnBiomes().stream().anyMatch(holderSet -> holderSet.holders().contains(biome)))) {
            if (phase == Phase.ADD) {
                MobSpawnSettingsBuilder spawns = builder.getMobSpawnSettings();
                for (MobSpawnSettings.SpawnerData spawner : this.spawners) {
                    spawns.addSpawn(MobCategory.CREATURE, spawner);
                }
            }
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return RockhoppersBiomeModifiers.ADD_PENGUIN_SPAWNS_MODIFIER.get();
    }
}
