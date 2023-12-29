package dev.greenhouseteam.rapscallionsandrockhoppers.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.WeightedHolderSet;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Optional;

public record PenguinType(Optional<ResourceLocation> textureLocation, Optional<ResourceLocation> surprisedTextureLocation,
                          List<WeightedHolderSet<Biome>> spawnBiomes, PenguinSounds sounds, Optional<String> whenNamed) {
    public static final PenguinType MISSING = new PenguinType(Optional.empty(), Optional.empty(), List.of(), PenguinSounds.NO_SOUNDS, Optional.empty());

    public static final Codec<PenguinType> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.strictOptionalField(ResourceLocation.CODEC, "texture_location").forGetter(PenguinType::textureLocation),
            ExtraCodecs.strictOptionalField(ResourceLocation.CODEC, "surprised_texture_location").forGetter(PenguinType::surprisedTextureLocation),
            WeightedHolderSet.listCodec(Biome.LIST_CODEC, "biomes").optionalFieldOf("spawn_biomes", List.of()).forGetter(PenguinType::spawnBiomes),
            ExtraCodecs.strictOptionalField(PenguinSounds.CODEC, "sounds", PenguinSounds.NO_SOUNDS).forGetter(PenguinType::sounds),
            ExtraCodecs.strictOptionalField(Codec.STRING, "when_named").forGetter(PenguinType::whenNamed)
    ).apply(inst, PenguinType::new));

    public record PenguinSounds(Optional<Holder<SoundEvent>> ambientSound, Optional<Holder<SoundEvent>> hurtSound,
                                Optional<Holder<SoundEvent>> deathSound, Optional<Holder<SoundEvent>> waterJumpSound) {
        public static final Codec<PenguinSounds> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ExtraCodecs.strictOptionalField(SoundEvent.CODEC, "ambient").forGetter(PenguinSounds::ambientSound),
                ExtraCodecs.strictOptionalField(SoundEvent.CODEC, "hurt").forGetter(PenguinSounds::hurtSound),
                ExtraCodecs.strictOptionalField(SoundEvent.CODEC, "death").forGetter(PenguinSounds::deathSound),
                ExtraCodecs.strictOptionalField(SoundEvent.CODEC, "water_jump").forGetter(PenguinSounds::waterJumpSound)
        ).apply(inst, PenguinSounds::new));

        public static final PenguinSounds NO_SOUNDS = new PenguinSounds(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }
}
