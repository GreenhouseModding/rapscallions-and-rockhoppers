package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.effect.FloatingMobEffect;
import house.greenhouse.rapscallionsandrockhoppers.effect.RockhoppersMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class RockhoppersMobEffects {
    public static final Holder<MobEffect> FLOATING = register("floating", new FloatingMobEffect());
    public static final Holder<MobEffect> SINKING = register("sinking", new RockhoppersMobEffect(MobEffectCategory.HARMFUL, 0x00095E));

    public static void registerEffects() {
    }

    public static Holder<MobEffect> register(String id, MobEffect effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, RapscallionsAndRockhoppers.asResource(id), effect);
    }
}
