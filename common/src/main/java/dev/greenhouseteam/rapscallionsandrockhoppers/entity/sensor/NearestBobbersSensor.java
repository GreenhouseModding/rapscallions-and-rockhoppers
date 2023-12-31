package dev.greenhouseteam.rapscallionsandrockhoppers.entity.sensor;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersSensorTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class NearestBobbersSensor extends PredicateSensor<FishingHook, Penguin> {

    @Nullable
    protected SquareRadius radius;
    public NearestBobbersSensor() {
        setScanRate((penguin -> 20)); // Once a second
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return List.of(RockhoppersMemoryModuleTypes.NEAREST_BOBBERS);
    }

    public NearestBobbersSensor setRadius(double radius) {
        return setRadius(radius, radius);
    }

    public NearestBobbersSensor setRadius(double xz, double y) {
        this.radius = new SquareRadius(xz, y);
        return this;
    }

    @Override
    protected void doTick(ServerLevel level, Penguin penguin) {
        if (radius == null) {
            double dist = penguin.getAttributeValue(Attributes.FOLLOW_RANGE);
            radius = new SquareRadius(dist, dist);
        }

        List<FishingHook> bobbers = EntityRetrievalUtil.getEntities(level, penguin.getBoundingBox().inflate(radius.xzRadius(), radius.yRadius(), radius.xzRadius()), entity -> entity instanceof FishingHook fishingHook && predicate().test(fishingHook, penguin));
        bobbers.sort(Comparator.comparingDouble(penguin::distanceToSqr));

        penguin.getBrain().setMemory(RockhoppersMemoryModuleTypes.NEAREST_BOBBERS, bobbers);


    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return RockhoppersSensorTypes.NEAREST_BOBBERS_SENSOR;
    }

}
