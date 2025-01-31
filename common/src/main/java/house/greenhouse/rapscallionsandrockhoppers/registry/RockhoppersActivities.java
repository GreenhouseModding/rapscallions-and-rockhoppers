package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.util.RegisterFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.schedule.Activity;

public class RockhoppersActivities {
    public static final Activity FOLLOW_BOAT = new Activity("follow_boat");
    public static final Activity COUGH_UP = new Activity("cough_up");
    public static final Activity WAIT_AROUND_BOBBER = new Activity("wait_around_bobber");

    public static void registerActivities(RegisterFunction<Activity> function) {
        function.register(BuiltInRegistries.ACTIVITY, RapscallionsAndRockhoppers.asResource("follow_boat"), FOLLOW_BOAT);
        function.register(BuiltInRegistries.ACTIVITY, RapscallionsAndRockhoppers.asResource("cough_up"), COUGH_UP);
        function.register(BuiltInRegistries.ACTIVITY, RapscallionsAndRockhoppers.asResource("wait_around_bobber"), WAIT_AROUND_BOBBER);
    }
}
