package house.greenhouse.rapscallionsandrockhoppers.registry;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.modifiers.AddPenguinSpawnsModifier;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class RockhoppersBiomeModifiers {
    private static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, RapscallionsAndRockhoppers.MOD_ID);

    public static final Supplier<MapCodec<AddPenguinSpawnsModifier>> ADD_PENGUIN_SPAWNS_MODIFIER = BIOME_MODIFIER_SERIALIZERS.register("add_penguin_spawns", () ->
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.either(MobSpawnSettings.SpawnerData.CODEC.listOf(), MobSpawnSettings.SpawnerData.CODEC).xmap(
                            either -> either.map(Function.identity(), List::of),
                            list -> list.size() == 1 ? Either.right(list.getFirst()) : Either.left(list)
                    ).fieldOf("spawners").forGetter(AddPenguinSpawnsModifier::spawners)
            ).apply(instance, AddPenguinSpawnsModifier::new))
    );



    public static void register(IEventBus eventBus) {
        BIOME_MODIFIER_SERIALIZERS.register(eventBus);
    }
}