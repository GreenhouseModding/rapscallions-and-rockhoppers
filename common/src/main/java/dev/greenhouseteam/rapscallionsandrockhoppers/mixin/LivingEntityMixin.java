package dev.greenhouseteam.rapscallionsandrockhoppers.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyReturnValue(method = "getFrictionInfluencedSpeed", at = @At("RETURN"))
    private float rapscallionsandrockhoppers$modifyPenguinFrictionWhenStumble(float original) {
        if (((LivingEntity)(Object)this instanceof Penguin penguin)) {
            if (penguin.isStumbling()) {
                return original * 1.8F;
            } else if (penguin.getBlockStateOn().is(BlockTags.ICE)) {
                return original * 1.2F;
            }
        }
        return original;
    }

    @Inject(method = "dropFromLootTable", at = @At("HEAD"), cancellable = true)
    private void rapscallionsandrockhoppers$dontDropLootIfKilledByPenguin(DamageSource source, boolean useLastDamagePlayer, CallbackInfo ci) {
        if (source.getEntity() instanceof Penguin) {
            ci.cancel();
        }
    }
}
