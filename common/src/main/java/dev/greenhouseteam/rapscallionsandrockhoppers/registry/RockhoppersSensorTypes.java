package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.sensor.*;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RegisterFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.sensing.SensorType;

public class RockhoppersSensorTypes {
    public static final SensorType<BoatToFollowSensor> BOAT_TO_FOLLOW = new SensorType<>(BoatToFollowSensor::new);
    public static final SensorType<NearbyPufferfishSensor> NEARBY_PUFFERFISH = new SensorType<>(NearbyPufferfishSensor::new);
    public static final SensorType<NearbyShoveableSensor> NEARBY_SHOVEABLE = new SensorType<>(NearbyShoveableSensor::new);
    public static final SensorType<NearbyWaterSensor> NEARBY_WATER = new SensorType<>(NearbyWaterSensor::new);
    public static final SensorType<PenguinHomeSensor> PENGUIN_HOME = new SensorType<>(PenguinHomeSensor::new);
    public static final SensorType<NearbyEggSensor> NEARBY_EGG = new SensorType<>(NearbyEggSensor::new);

    public static void registerSensorTypes(RegisterFunction<SensorType<?>> function) {
        function.register(BuiltInRegistries.SENSOR_TYPE, RapscallionsAndRockhoppers.asResource("boat_to_follow"), BOAT_TO_FOLLOW);
        function.register(BuiltInRegistries.SENSOR_TYPE, RapscallionsAndRockhoppers.asResource("nearby_pufferfish"), NEARBY_PUFFERFISH);
        function.register(BuiltInRegistries.SENSOR_TYPE, RapscallionsAndRockhoppers.asResource("nearby_shoveable"), NEARBY_SHOVEABLE);
        function.register(BuiltInRegistries.SENSOR_TYPE, RapscallionsAndRockhoppers.asResource("nearby_water"), NEARBY_WATER);
        function.register(BuiltInRegistries.SENSOR_TYPE, RapscallionsAndRockhoppers.asResource("penguin_home"), PENGUIN_HOME);
        function.register(BuiltInRegistries.SENSOR_TYPE, RapscallionsAndRockhoppers.asResource("nearby_egg"), NEARBY_EGG);
    }
}
