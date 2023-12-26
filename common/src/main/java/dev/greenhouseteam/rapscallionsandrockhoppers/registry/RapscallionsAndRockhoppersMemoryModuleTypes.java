package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RegisterFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Pufferfish;

import java.util.Optional;

public class RapscallionsAndRockhoppersMemoryModuleTypes {
    public static final MemoryModuleType<Pufferfish> NEAREST_VISIBLE_PUFFERFISH = createEmpty();
    public static final MemoryModuleType<Penguin> NEAREST_VISIBLE_SHOVEABLE = createEmpty();
    public static final MemoryModuleType<BlockPos> NEAREST_WATER = createEmpty();
    public static final MemoryModuleType<Integer> WATER_JUMP_COOLDOWN_TICKS = createSerializable(Codec.INT);

    public static void registerMemoryModuleTypes(RegisterFunction<MemoryModuleType<?>> function) {
        function.register(BuiltInRegistries.MEMORY_MODULE_TYPE, RapscallionsAndRockhoppers.asResource("water_jump_cooling_down"), WATER_JUMP_COOLDOWN_TICKS);
        function.register(BuiltInRegistries.MEMORY_MODULE_TYPE, RapscallionsAndRockhoppers.asResource("nearest_visible_shoveable"), NEAREST_VISIBLE_SHOVEABLE);
        function.register(BuiltInRegistries.MEMORY_MODULE_TYPE, RapscallionsAndRockhoppers.asResource("nearest_water"), NEAREST_WATER);
        function.register(BuiltInRegistries.MEMORY_MODULE_TYPE, RapscallionsAndRockhoppers.asResource("nearest_visible_pufferfish"), NEAREST_VISIBLE_PUFFERFISH);
    }

    private static <T> MemoryModuleType<T> createEmpty() {
        return new MemoryModuleType<>(Optional.empty());
    }

    private static <T> MemoryModuleType<T> createSerializable(Codec<T> codec) {
        return new MemoryModuleType<>(Optional.of(codec));
    }
}
