package house.greenhouse.rapscallionsandrockhoppers.effect;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class FloatingMobEffect extends MobEffect {
    public FloatingMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x6CEBEB);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.isInWater() && livingEntity.getFluidHeight(FluidTags.WATER) >= 0.5) {
            Vec3 movement = livingEntity.getDeltaMovement();
            var maxSpeed = (0.5f + 0.1 * amplifier);
            var buildUp = (0.15f + (0.02 * (amplifier + 1))) * (livingEntity.getFluidHeight(FluidTags.WATER) / 3.0);
            if (livingEntity.isSwimming()) {
                buildUp *= 2;
            }
            livingEntity.setDeltaMovement(
                    movement.x,
                    Math.min(maxSpeed, movement.y + buildUp),
                    movement.z
            );
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
