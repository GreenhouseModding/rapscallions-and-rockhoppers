package dev.greenhouseteam.rapscallionsandrockhoppers.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record WeightedHolderSet<T>(HolderSet<T> holders, int weight) {

    public static <T> Codec<WeightedHolderSet<T>> codec(Codec<HolderSet<T>> codec, String fieldName) {
        return Codec.either(codec, RecordCodecBuilder.<WeightedHolderSet<T>>create(inst -> inst.group(
                        codec.fieldOf(fieldName).forGetter(WeightedHolderSet::holders),
                        Codec.INT.fieldOf("weight").forGetter(WeightedHolderSet::weight)
                ).apply(inst, WeightedHolderSet::new)))
                .xmap(either -> either.map(holders -> new WeightedHolderSet<>(holders, 1), weightedHolderSet -> weightedHolderSet), Either::right);
    }

    public static <T> Codec<List<WeightedHolderSet<T>>> listCodec(Codec<HolderSet<T>> codec, String fieldName) {
        return Codec.either(codec(codec, fieldName), codec(codec, fieldName).listOf())
                .xmap(either -> either.map(List::of, holderSets -> holderSets), Either::right);
    }
}
