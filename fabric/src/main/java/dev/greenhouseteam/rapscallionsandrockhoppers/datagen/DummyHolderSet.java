package dev.greenhouseteam.rapscallionsandrockhoppers.datagen;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DummyHolderSet<T> implements HolderSet<T> {
    private TagKey<T> tagKey;

    public DummyHolderSet(TagKey<T> tagKey) {
        this.tagKey = tagKey;
    }

    @Override
    public Stream<Holder<T>> stream() {
        return Stream.of();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Either<TagKey<T>, List<Holder<T>>> unwrap() {
        return Either.left(this.tagKey);
    }

    @Override
    public Optional<Holder<T>> getRandomElement(RandomSource randomSource) {
        return Optional.empty();
    }

    @Override
    public Holder<T> get(int i) {
        return null;
    }

    @Override
    public boolean contains(Holder<T> holder) {
        return false;
    }

    @Override
    public boolean canSerializeIn(HolderOwner<T> holderOwner) {
        return true;
    }

    @Override
    public Optional<TagKey<T>> unwrapKey() {
        return Optional.of(this.tagKey);
    }

    @NotNull
    @Override
    public Iterator<Holder<T>> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Holder<T> next() {
                return null;
            }
        };
    }
}
