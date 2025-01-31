package house.greenhouse.rapscallionsandrockhoppers.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface RegisterFunction<T> {
    void register(Registry<T> registry, ResourceLocation id, T value);
}
