package dev.greenhouseteam.rapscallionsandrockhoppers.mixin;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.PenguinType;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(RegistryDataLoader.RegistryData.class)
public abstract class RegistryDataLoaderRegistryDataMixin<T> {
    @Shadow public abstract ResourceKey<? extends Registry<T>> key();

    @Inject(method = { "method_45132", "lambda$create$0" }, at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void rapscallionsandrockhoppers$cachePenguinRegistry(WritableRegistry writableRegistry, Map map, ResourceManager resourceManager, RegistryOps.RegistryInfoLookup registryInfoLookup, CallbackInfo ci) {
        if (this.key().location() == RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY.location()) {
            RapscallionsAndRockhoppers.setBiomePopulationPenguinTypeRegistry((Registry<PenguinType>) writableRegistry);
        }
    }
}
