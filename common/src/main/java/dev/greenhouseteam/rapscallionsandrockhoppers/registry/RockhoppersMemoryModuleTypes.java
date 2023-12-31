package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RegisterFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Pufferfish;

import java.util.Optional;
import java.util.UUID;

public class RockhoppersMemoryModuleTypes {
    public static final MemoryModuleType<UUID> BOAT_TO_FOLLOW = createEmpty();
    public static final MemoryModuleType<Integer> HUNGRY_TIME = createEmpty();
    public static final MemoryModuleType<Penguin> NEAREST_VISIBLE_SHOVEABLE = createEmpty();
    public static final MemoryModuleType<BlockPos> NEAREST_WATER = createEmpty();
    public static final MemoryModuleType<Unit> IS_JUMPING = createEmpty();
    public static final MemoryModuleType<Integer> TIME_ALLOWED_TO_EAT = createEmpty();
    public static final MemoryModuleType<Integer> TIME_ALLOWED_TO_FOLLOW_BOAT = createEmpty();
    public static final MemoryModuleType<Integer> TIME_ALLOWED_TO_WATER_JUMP = createEmpty();
    public static final MemoryModuleType<BlockPos> EGG_POS = createEmpty();

    public static void registerMemoryModuleTypes(RegisterFunction<MemoryModuleType<?>> function) {
        function.register(BuiltInRegistries.MEMORY_MODULE_TYPE, RapscallionsAndRockhoppers.asResource("boat_to_follow"), BOAT_TO_FOLLOW);
        function.register(BuiltInRegistries.MEMORY_MODULE_TYPE, RapscallionsAndRockhoppers.asResource("hungry_time"), HUNGRY_TIME);
        function.register(BuiltInRegistries.MEMORY_MODULE_TYPE, RapscallionsAndRockhoppers.asResource("nearest_visible_shoveable"), NEAREST_VISIBLE_SHOVEABLE);
        function.register(BuiltInRegistries.MEMORY_MODULE_TYPE, RapscallionsAndRockhoppers.asResource("nearest_water"), NEAREST_WATER);
        function.register(BuiltInRegistries.MEMORY_MODULE_TYPE, RapscallionsAndRockhoppers.asResource("time_allowed_to_eat"), TIME_ALLOWED_TO_EAT);
        function.register(BuiltInRegistries.MEMORY_MODULE_TYPE, RapscallionsAndRockhoppers.asResource("time_allowed_to_follow_boat"), TIME_ALLOWED_TO_FOLLOW_BOAT);
        function.register(BuiltInRegistries.MEMORY_MODULE_TYPE, RapscallionsAndRockhoppers.asResource("time_allowed_to_water_jump"), TIME_ALLOWED_TO_WATER_JUMP);
        function.register(BuiltInRegistries.MEMORY_MODULE_TYPE, RapscallionsAndRockhoppers.asResource("is_jumping"), IS_JUMPING);
    }

    private static <T> MemoryModuleType<T> createEmpty() {
        return new MemoryModuleType<>(Optional.empty());
    }
}
