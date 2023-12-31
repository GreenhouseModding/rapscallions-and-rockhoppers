package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RegisterFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class RockhoppersSoundEvents {
    public static final SoundEvent PENGUIN_AMBIENT = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.ambient"));
    public static final SoundEvent PENGUIN_COUGH = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.cough"));
    public static final SoundEvent PENGUIN_DEATH = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.death"));
    public static final SoundEvent PENGUIN_HURT = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.hurt"));
    public static final SoundEvent PENGUIN_JUMP = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.jump"));

    public static void registerSoundEvents(RegisterFunction<SoundEvent> function) {
        function.register(BuiltInRegistries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.ambient"), PENGUIN_AMBIENT);
        function.register(BuiltInRegistries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.death"), PENGUIN_DEATH);
        function.register(BuiltInRegistries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.hurt"), PENGUIN_HURT);
        function.register(BuiltInRegistries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.jump"), PENGUIN_JUMP);
    }

}
