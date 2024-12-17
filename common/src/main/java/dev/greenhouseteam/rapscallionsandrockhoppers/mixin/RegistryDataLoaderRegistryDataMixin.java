package dev.greenhouseteam.rapscallionsandrockhoppers.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.PenguinType;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderRegistryDataMixin<T> {

    // TODO: Look into migrating this into an event maybe?
    @Inject(method = { "method_45128", "lambda$load$6" }, at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void rapscallionsandrockhoppers$cachePenguinRegistry(Map map, RegistryDataLoader.Loader p_344258_, CallbackInfo ci) {
        if (p_344258_.registry().key().location() == RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY.location()) {
            RapscallionsAndRockhoppers.setBiomePopulationPenguinTypeRegistry(p_344258_.registry());
        }
    }
}
