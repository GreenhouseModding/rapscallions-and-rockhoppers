package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.effect.FloatingMobEffect;
import house.greenhouse.rapscallionsandrockhoppers.effect.RockhoppersMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class RockhoppersMobEffects {
    public static final Holder<MobEffect> FLOATING = register("floating", new FloatingMobEffect());
    public static final Holder<MobEffect> SINKING = register("sinking", new RockhoppersMobEffect(MobEffectCategory.HARMFUL, 0x00095E));

    public static final ResourceLocation SINKING_MOVEMENT_SPEED_MODIFIER_ID = RapscallionsAndRockhoppers.asResource("mob_effect.sinking.movement_speed");
    public static final AttributeModifier SINKING_GRAVITY_MODIFIER = new AttributeModifier(RapscallionsAndRockhoppers.asResource("mob_effect.sinking.gravity"), -0.8F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    public static void registerEffects() {
    }

    public static Holder<MobEffect> register(String id, MobEffect effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, RapscallionsAndRockhoppers.asResource(id), effect);
    }

    public static void applySinkingModifiers(LivingEntity entity) {
        AttributeModifier movementModifier = new AttributeModifier(SINKING_MOVEMENT_SPEED_MODIFIER_ID, -0.04F + entity.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY) * 0.03, AttributeModifier.Operation.ADD_VALUE);

        entity.getAttribute(Attributes.MOVEMENT_SPEED).addOrUpdateTransientModifier(movementModifier);;

        if (!entity.getAttribute(Attributes.GRAVITY).hasModifier(RockhoppersMobEffects.SINKING_GRAVITY_MODIFIER.id()))
            entity.getAttribute(Attributes.GRAVITY).addTransientModifier(RockhoppersMobEffects.SINKING_GRAVITY_MODIFIER);
    }

    public static void removeSinkingModifiers(LivingEntity entity) {
        entity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(RockhoppersMobEffects.SINKING_MOVEMENT_SPEED_MODIFIER_ID);
        entity.getAttribute(Attributes.GRAVITY).removeModifier(RockhoppersMobEffects.SINKING_GRAVITY_MODIFIER);
    }
}
