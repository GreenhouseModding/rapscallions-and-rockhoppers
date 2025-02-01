package house.greenhouse.rapscallionsandrockhoppers.util;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.entity.PenguinVariant;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;

public class RockhoppersResourceKeys {
    public static final ResourceKey<Registry<PenguinVariant>> PENGUIN_VARIANT = ResourceKey.createRegistryKey(RapscallionsAndRockhoppers.asResource("penguin_variant"));

    public static class PenguinTypeKeys {
        public static final ResourceKey<PenguinVariant> ROCKHOPPER = ResourceKey.create(PENGUIN_VARIANT, RapscallionsAndRockhoppers.asResource("rockhopper"));
        public static final ResourceKey<PenguinVariant> CHINSTRAP = ResourceKey.create(PENGUIN_VARIANT, RapscallionsAndRockhoppers.asResource("chinstrap"));
        public static final ResourceKey<PenguinVariant> GUNTER = ResourceKey.create(PENGUIN_VARIANT, RapscallionsAndRockhoppers.asResource("gunter"));
    }

    public static class SoundEventKeys {
        public static final ResourceKey<SoundEvent> PENGUIN_AMBIENT = ResourceKey.create(Registries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.ambient"));
        public static final ResourceKey<SoundEvent> PENGUIN_DEATH = ResourceKey.create(Registries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.death"));
        public static final ResourceKey<SoundEvent> PENGUIN_HURT = ResourceKey.create(Registries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.hurt"));
        public static final ResourceKey<SoundEvent> PENGUIN_JUMP = ResourceKey.create(Registries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.jump"));
    }
}
