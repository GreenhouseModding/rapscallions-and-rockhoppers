package house.greenhouse.rapscallionsandrockhoppers.mixin;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import net.minecraft.resources.RegistryDataLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderRegistryDataMixin {

    // TODO: Look into migrating this into an event maybe?
    @Inject(method = { "method_45128", "lambda$load$6" }, at = @At("RETURN"))
    private static void rapscallionsandrockhoppers$cachePenguinRegistry(Map map, RegistryDataLoader.Loader loader, CallbackInfo ci) {
        if (loader.registry().key().location() == RockhoppersResourceKeys.PENGUIN_VARIANT.location()) {
            RapscallionsAndRockhoppers.setBiomePopulationPenguinTypeRegistry(loader.registry());
        }
    }
}
