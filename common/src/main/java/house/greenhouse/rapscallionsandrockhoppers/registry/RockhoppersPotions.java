package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;

public class RockhoppersPotions {
    public static final Holder<Potion> FLOATING = register("floating", new Potion("rapscallionsandrockhoppers.floating", new MobEffectInstance(RockhoppersMobEffects.FLOATING, 400)));
    public static final Holder<Potion> LONG_FLOATING = register("long_floating", new Potion("rapscallionsandrockhoppers.floating", new MobEffectInstance(RockhoppersMobEffects.FLOATING, 800)));
    public static final Holder<Potion> STRONG_FLOATING = register("strong_floating", new Potion("rapscallionsandrockhoppers.floating", new MobEffectInstance(RockhoppersMobEffects.FLOATING, 200, 1)));
    public static final Holder<Potion> SINKING = register("sinking", new Potion("rapscallionsandrockhoppers.sinking", new MobEffectInstance(RockhoppersMobEffects.SINKING, 3600)));
    public static final Holder<Potion> LONG_SINKING = register("long_sinking", new Potion("rapscallionsandrockhoppers.sinking", new MobEffectInstance(RockhoppersMobEffects.SINKING, 9600)));
    public static final Holder<Potion> STRONG_SINKING = register("strong_sinking", new Potion("rapscallionsandrockhoppers.sinking", new MobEffectInstance(RockhoppersMobEffects.SINKING, 1800, 1)));

    public static void registerPotions() {
        
    }

    
    public static Holder<Potion> register(String id, Potion effect) {
        return Registry.registerForHolder(BuiltInRegistries.POTION, RapscallionsAndRockhoppers.asResource(id), effect);
    }
    
    public static void createRecipes(PotionBrewing.Builder builder) {
        builder.addMix(
                Potions.AWKWARD,
                RockhoppersItems.FISH_SCALE,
                FLOATING
        );
        builder.addMix(
                FLOATING,
                Items.REDSTONE,
                LONG_FLOATING
        );
        builder.addMix(
                FLOATING,
                Items.GLOWSTONE_DUST,
                STRONG_FLOATING
        );
        builder.addMix(
                FLOATING,
                Items.FERMENTED_SPIDER_EYE,
                SINKING
        );
        builder.addMix(
                SINKING,
                Items.REDSTONE,
                LONG_SINKING
        );
        builder.addMix(
                SINKING,
                Items.GLOWSTONE_DUST,
                STRONG_SINKING
        );
    }
}
