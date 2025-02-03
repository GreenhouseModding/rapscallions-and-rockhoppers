package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class RockhoppersSoundEvents {
    public static final SoundEvent PENGUIN_AMBIENT = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.ambient"));
    public static final SoundEvent PENGUIN_COUGH = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.cough"));
    public static final SoundEvent PENGUIN_DEATH = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.death"));
    public static final SoundEvent PENGUIN_EAT = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.eat"));
    public static final SoundEvent PENGUIN_HURT = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.hurt"));
    public static final SoundEvent PENGUIN_JUMP = SoundEvent.createVariableRangeEvent(RapscallionsAndRockhoppers.asResource("entity.penguin.jump"));

    public static void registerSoundEvents() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.ambient"), PENGUIN_AMBIENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.cough"), PENGUIN_COUGH);
        Registry.register(BuiltInRegistries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.death"), PENGUIN_DEATH);
        Registry.register(BuiltInRegistries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.eat"), PENGUIN_EAT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.hurt"), PENGUIN_HURT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, RapscallionsAndRockhoppers.asResource("entity.penguin.jump"), PENGUIN_JUMP);
    }

}
