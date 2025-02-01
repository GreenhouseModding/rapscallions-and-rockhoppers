package house.greenhouse.rapscallionsandrockhoppers.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;

public class RockhoppersCodecs {
    public static <T> Codec<SimpleWeightedRandomList<T>> weightedEntryCodec(Codec<T> codec, String fieldName) {
        Codec<WeightedEntry.Wrapper<T>> direct = RecordCodecBuilder.create(inst -> inst.group(
                codec.fieldOf(fieldName).forGetter(WeightedEntry.Wrapper::data),
                Weight.CODEC.optionalFieldOf("weight", Weight.of(1)).forGetter(WeightedEntry.Wrapper::weight)
        ).apply(inst, WeightedEntry.Wrapper::new));

        return Codec.withAlternative(Codec.either(codec, direct).listOf().xmap(list -> {
            var builder = SimpleWeightedRandomList.<T>builder();
            list.forEach(value ->
                    value.ifLeft(builder::add).ifRight(wrapper -> builder.add(wrapper.data(), wrapper.weight().asInt())));
            return builder.build();
        }, list -> list.unwrap().stream().map(Either::<T, WeightedEntry.Wrapper<T>>right).toList()), codec, t -> {
            var builder = SimpleWeightedRandomList.<T>builder();
            builder.add(t);
            return builder.build();
        });
    }
}
