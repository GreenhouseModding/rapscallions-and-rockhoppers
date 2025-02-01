package house.greenhouse.rapscallionsandrockhoppers.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public record PenguinVariant(ResourceLocation texture, ResourceLocation surprisedTexture,
                             SimpleWeightedRandomList<HolderSet<Biome>> biomes, PenguinSounds sounds, Optional<String> whenNamed) {
    public static final PenguinVariant MISSING = new PenguinVariant(RapscallionsAndRockhoppers.asResource("entity/penguin/missing_penguin"), RapscallionsAndRockhoppers.asResource("entity/penguin/missing_penguin_surprised"), SimpleWeightedRandomList.empty(), PenguinSounds.NO_SOUNDS, Optional.empty());

    public static final Codec<PenguinVariant> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(PenguinVariant::texture),
            ResourceLocation.CODEC.fieldOf("surprised_texture").forGetter(PenguinVariant::surprisedTexture),
            SimpleWeightedRandomList.wrappedCodec(Biome.LIST_CODEC).optionalFieldOf("biomes", SimpleWeightedRandomList.empty()).forGetter(PenguinVariant::biomes),
            PenguinSounds.CODEC.optionalFieldOf("sounds", PenguinSounds.NO_SOUNDS).forGetter(PenguinVariant::sounds),
            Codec.STRING.optionalFieldOf("when_named").forGetter(PenguinVariant::whenNamed)
    ).apply(inst, PenguinVariant::new));
    public static final Codec<Holder<PenguinVariant>> CODEC = RegistryFixedCodec.create(RockhoppersResourceKeys.PENGUIN_VARIANT);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<PenguinVariant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(RockhoppersResourceKeys.PENGUIN_VARIANT);

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