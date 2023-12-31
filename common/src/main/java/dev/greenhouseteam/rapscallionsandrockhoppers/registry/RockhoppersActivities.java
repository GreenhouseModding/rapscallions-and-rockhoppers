package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RegisterFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.schedule.Activity;

public class RockhoppersActivities {
    public static final Activity FOLLOW_BOAT = new Activity("follow_boat");
    public static final Activity HANG_AROUND_BOBBER = new Activity("hang_around_bobber");

    public static void registerActivities(RegisterFunction<Activity> function) {
        function.register(BuiltInRegistries.ACTIVITY, RapscallionsAndRockhoppers.asResource("follow_boat"), FOLLOW_BOAT);
        function.register(BuiltInRegistries.ACTIVITY, RapscallionsAndRockhoppers.asResource("hang_around_bobber"), HANG_AROUND_BOBBER);
    }
}
