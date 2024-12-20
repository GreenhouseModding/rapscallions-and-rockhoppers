package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RegisterFunction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class RockhoppersDataComponents {

    // TODO: Should probably have this actually have the actual type instead of just a resource location
    public static final DataComponentType<ResourceLocation> PENGUIN_TYPE = DataComponentType.<ResourceLocation>builder()
            .persistent(ResourceLocation.CODEC)
            .networkSynchronized(ResourceLocation.STREAM_CODEC)
            .build();

    public static void registerDataComponents(RegisterFunction<DataComponentType<?>> function) {
        function.register(BuiltInRegistries.DATA_COMPONENT_TYPE, RapscallionsAndRockhoppers.asResource("penguin_type"), PENGUIN_TYPE);
    }
}
