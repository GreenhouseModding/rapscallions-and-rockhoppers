package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.apache.logging.log4j.util.TriConsumer;

public class RapscallionsAndRockhoppersSoundEvents {
    public static final SoundEvent PENGUIN_AMBIENT = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.ambient"));
    public static final SoundEvent PENGUIN_DEATH = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.death"));
    public static final SoundEvent PENGUIN_HURT = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.hurt"));
    public static final SoundEvent PENGUIN_JUMP = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.jump"));

    public static void registerSoundEvents(TriConsumer<Registry<SoundEvent>, ResourceLocation, SoundEvent> consumer) {
        consumer.accept(BuiltInRegistries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.ambient"), PENGUIN_AMBIENT);
        consumer.accept(BuiltInRegistries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.death"), PENGUIN_DEATH);
        consumer.accept(BuiltInRegistries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.hurt"), PENGUIN_HURT);
        consumer.accept(BuiltInRegistries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.jump"), PENGUIN_JUMP);
    }

}
