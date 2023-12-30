package dev.greenhouseteam.rapscallionsandrockhoppers.util;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.PenguinType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;

public class RockhoppersResourceKeys {
    public static final ResourceKey<Registry<PenguinType>> PENGUIN_TYPE_REGISTRY = ResourceKey.createRegistryKey(RapscallionsAndRockhoppers.asResource("penguin_type"));

    public static class PenguinTypeKeys {
        public static final ResourceKey<PenguinType> ROCKHOPPER = ResourceKey.create(PENGUIN_TYPE_REGISTRY, RapscallionsAndRockhoppers.asResource("rockhopper"));
        public static final ResourceKey<PenguinType> CHINSTRAP = ResourceKey.create(PENGUIN_TYPE_REGISTRY, RapscallionsAndRockhoppers.asResource("chinstrap"));
        public static final ResourceKey<PenguinType> GUNTER = ResourceKey.create(PENGUIN_TYPE_REGISTRY, RapscallionsAndRockhoppers.asResource("gunter"));
    }

    public static class SoundEventKeys {
        public static final ResourceKey<SoundEvent> PENGUIN_AMBIENT = ResourceKey.create(Registries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.ambient"));
        public static final ResourceKey<SoundEvent> PENGUIN_DEATH = ResourceKey.create(Registries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.death"));
        public static final ResourceKey<SoundEvent> PENGUIN_HURT = ResourceKey.create(Registries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.hurt"));
        public static final ResourceKey<SoundEvent> PENGUIN_JUMP = ResourceKey.create(Registries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.jump"));
    }
}
