package house.greenhouse.rapscallionsandrockhoppers.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import house.greenhouse.rapscallionsandrockhoppers.util.WeightedHolderSet;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.Optional;

// NOTE / TODO: Maybe the textures (and sounds) shouldn't be optional*. The sounds could be defaulted to SoundEvents.EMPTY, and textures could just be outright required.
public record PenguinType(Optional<ResourceLocation> textureLocation, Optional<ResourceLocation> surprisedTextureLocation,
                          List<WeightedHolderSet<Biome>> spawnBiomes, PenguinSounds sounds, Optional<String> whenNamed) {
    public static final PenguinType MISSING = new PenguinType(Optional.empty(), Optional.empty(), List.of(), PenguinSounds.NO_SOUNDS, Optional.empty());

    public static final Codec<PenguinType> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.optionalFieldOf("texture_location").forGetter(PenguinType::textureLocation),
            ResourceLocation.CODEC.optionalFieldOf("surprised_texture_location").forGetter(PenguinType::surprisedTextureLocation),
            WeightedHolderSet.listCodec(Biome.LIST_CODEC, "biomes").optionalFieldOf("spawn_biomes", List.of()).forGetter(PenguinType::spawnBiomes),
            PenguinSounds.CODEC.optionalFieldOf("sounds", PenguinSounds.NO_SOUNDS).forGetter(PenguinType::sounds),
            Codec.STRING.optionalFieldOf("when_named").forGetter(PenguinType::whenNamed)
    ).apply(inst, PenguinType::new));

    public record PenguinSounds(Optional<Holder<SoundEvent>> ambientSound, Optional<Holder<SoundEvent>> hurtSound,
                                Optional<Holder<SoundEvent>> deathSound, Optional<Holder<SoundEvent>> waterJumpSound) {
        public static final Codec<PenguinSounds> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                SoundEvent.CODEC.optionalFieldOf("ambient").forGetter(PenguinSounds::ambientSound),
                SoundEvent.CODEC.optionalFieldOf("hurt").forGetter(PenguinSounds::hurtSound),
                SoundEvent.CODEC.optionalFieldOf("death").forGetter(PenguinSounds::deathSound),
                SoundEvent.CODEC.optionalFieldOf("water_jump").forGetter(PenguinSounds::waterJumpSound)
        ).apply(inst, PenguinSounds::new));

        public static final PenguinSounds NO_SOUNDS = new PenguinSounds(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }
}