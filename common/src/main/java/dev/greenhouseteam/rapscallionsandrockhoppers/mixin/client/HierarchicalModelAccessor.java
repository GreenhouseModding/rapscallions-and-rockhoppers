package dev.greenhouseteam.rapscallionsandrockhoppers.mixin.client;

import net.minecraft.client.model.HierarchicalModel;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HierarchicalModel.class)
public interface HierarchicalModelAccessor {
    @Accessor("ANIMATION_VECTOR_CACHE") @Final
    static Vector3f rapscallionsandrockhoppers$ANIMATION_VECTOR_CACHE() {
        throw new RuntimeException("");
    }
}
