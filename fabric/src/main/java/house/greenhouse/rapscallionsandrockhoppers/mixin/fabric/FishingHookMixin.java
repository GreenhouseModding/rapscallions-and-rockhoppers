package house.greenhouse.rapscallionsandrockhoppers.mixin.fabric;

import com.llamalad7.mixinextras.sugar.Local;
import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersActivities;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersTags;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.Optional;

@Mixin(FishingHook.class)
public class FishingHookMixin {
    @Unique
    private boolean rapscallionsandrockhoppers$hasAttractedPenguin = false;

    @Inject(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z"))
    private void rapscallionsandrockhopper$setPenguinUpToCatchFish(ItemStack $$0, CallbackInfoReturnable<Integer> cir, @Local(ordinal = 1) ItemStack stack) {
        if (!stack.is(RockhoppersTags.ItemTags.PENGUIN_FOOD) || this.rapscallionsandrockhoppers$hasAttractedPenguin) return;
        Optional<Penguin> penguin = ((FishingHook) (Object) this).level().getEntitiesOfClass(Penguin.class, ((FishingHook) (Object) this).getBoundingBox().inflate(24.0F), penguin1 -> penguin1.getBrain().getActiveNonCoreActivity().map(activity -> activity == RockhoppersActivities.WAIT_AROUND_BOBBER).orElse(false)).stream().min(Comparator.comparing(penguin1 -> ((FishingHook) (Object) this).distanceTo(penguin1)));
        penguin.ifPresent(value -> BrainUtils.setMemory(value, RockhoppersMemoryModuleTypes.CAUGHT_BOBBER, ((FishingHook) (Object) this)));
        this.rapscallionsandrockhoppers$hasAttractedPenguin = true;
    }

    @Inject(method = "retrieve", at = @At("TAIL"))
    private void rapscallionsandrockhopper$clearAttractedPenguin(ItemStack $$0, CallbackInfoReturnable<Integer> cir) {
        this.rapscallionsandrockhoppers$hasAttractedPenguin = false;
    }
}
