package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.BoatToFollowSensor;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.NearbyBobbersSensor;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.NearbyEggSensor;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.NearbyShoveableSensor;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.NearbyWaterSensor;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.PenguinHomeSensor;
import house.greenhouse.rapscallionsandrockhoppers.entity.sensor.PlayerToCoughForSensor;
import house.greenhouse.rapscallionsandrockhoppers.util.RegisterFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.sensing.SensorType;

public class RockhoppersSensorTypes {
    public static final SensorType<BoatToFollowSensor> BOAT_TO_FOLLOW = new SensorType<>(BoatToFollowSensor::new);
    public static final SensorType<PlayerToCoughForSensor> PLAYER_TO_COUGH_FOR = new SensorType<>(PlayerToCoughForSensor::new);
    public static final SensorType<NearbyShoveableSensor> NEARBY_SHOVEABLE = new SensorType<>(NearbyShoveableSensor::new);
    public static final SensorType<NearbyWaterSensor> NEARBY_WATER = new SensorType<>(NearbyWaterSensor::new);
    public static final SensorType<PenguinHomeSensor> PENGUIN_HOME = new SensorType<>(PenguinHomeSensor::new);
    public static final SensorType<NearbyEggSensor> NEARBY_EGG = new SensorType<>(NearbyEggSensor::new);
    public static final SensorType<NearbyEggSensor> PENGUIN_ATTACK_TARGET = new SensorType<>(NearbyEggSensor::new);
    public static final SensorType<NearbyBobbersSensor> NEAREST_BOBBERS_SENSOR = new SensorType<>(NearbyBobbersSensor::new);

    public static void registerSensorTypes(RegisterFunction<SensorType<?>> function) {
        function.register(BuiltInRegistries.SENSOR_TYPE, RapscallionsAndRockhoppers.asResource("boat_to_follow"), BOAT_TO_FOLLOW);
        function.register(BuiltInRegistries.SENSOR_TYPE, RapscallionsAndRockhoppers.asResource("player_to_cough_for"), PLAYER_TO_COUGH_FOR);
        function.register(BuiltInRegistries.SENSOR_TYPE, RapscallionsAndRockhoppers.asResource("nearby_shoveable"), NEARBY_SHOVEABLE);
        function.register(BuiltInRegistries.SENSOR_TYPE, RapscallionsAndRockhoppers.asResource("nearby_water"), NEARBY_WATER);
        function.register(BuiltInRegistries.SENSOR_TYPE, RapscallionsAndRockhoppers.asResource("penguin_home"), PENGUIN_HOME);
        function.register(BuiltInRegistries.SENSOR_TYPE, RapscallionsAndRockhoppers.asResource("nearby_egg"), NEARBY_EGG);
        function.register(BuiltInRegistries.SENSOR_TYPE, RapscallionsAndRockhoppers.asResource("penguin_attack_target"), PENGUIN_ATTACK_TARGET);
        function.register(BuiltInRegistries.SENSOR_TYPE, RapscallionsAndRockhoppers.asResource("nearest_bobbers"), NEAREST_BOBBERS_SENSOR);
    }
}
