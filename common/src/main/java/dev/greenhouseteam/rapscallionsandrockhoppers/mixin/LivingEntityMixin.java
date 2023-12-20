package dev.greenhouseteam.rapscallionsandrockhoppers.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyReturnValue(method = "getFrictionInfluencedSpeed", at = @At("RETURN"))
    private float rapscallionsandrockhoppers$modifyPenguinFrictionWhenStumble(float original) {
        if (((LivingEntity)(Object)this instanceof Penguin penguin) && penguin.isStumbling()) {
            return original * 1.8F;
        }

        return original;
    }
}
