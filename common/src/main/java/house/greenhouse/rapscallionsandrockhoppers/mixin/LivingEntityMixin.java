package house.greenhouse.rapscallionsandrockhoppers.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> effect);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

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
    
    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isAffectedByFluids()Z"))
    public boolean rapscallionsandrockhoppers$makeNotEffectedByWaterIfSinking(boolean original) {
        if (this.isInWater() && this.hasEffect(RockhoppersMobEffects.SINKING)) {
            return false;
        }
        return original;
    }

    @Unique
    private boolean rapscallionsandrockhoppers$previousWaterState = false;

    @Inject(method = "travel", at = @At("HEAD"))
    private void rapscallionsandrockhoppers$sinkingAttributes(Vec3 travelVector, CallbackInfo ci) {
        if (this.isUnderWater() && this.hasEffect(RockhoppersMobEffects.SINKING)) {
            RockhoppersMobEffects.applySinkingModifiers((LivingEntity) (Object) this);
            rapscallionsandrockhoppers$previousWaterState = true;
        } else if (rapscallionsandrockhoppers$previousWaterState) {
            RockhoppersMobEffects.removeSinkingModifiers((LivingEntity)(Object)this);
            rapscallionsandrockhoppers$previousWaterState = false;
        }
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInWater()Z"))
    public boolean rapscallionsandrockhoppers$jumpMakeNotEffectedByWaterIfSinking(boolean original) {
        if (this.isInWater() && this.hasEffect(RockhoppersMobEffects.SINKING)) {
            return false;
        }
        return original;
    }
}
