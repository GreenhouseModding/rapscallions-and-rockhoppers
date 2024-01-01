package dev.greenhouseteam.rapscallionsandrockhoppers.entity.sensor;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersSensorTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class NearbyBobbersSensor extends PredicateSensor<FishingHook, Penguin> {

    private boolean hasSetUpPreviousBoatPos;
    private Vec3 previousBoatPos;

    @Nullable
    protected SquareRadius radius;
    public NearbyBobbersSensor() {
        setScanRate((penguin -> 20)); // Once a second
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return List.of(RockhoppersMemoryModuleTypes.NEAREST_BOBBERS);
    }

    public NearbyBobbersSensor setRadius(double radius) {
        return setRadius(radius, radius);
    }

    public NearbyBobbersSensor setRadius(double xz, double y) {
        this.radius = new SquareRadius(xz, y);
        return this;
    }

    @Override
    protected void doTick(ServerLevel level, Penguin penguin) {
        if (BrainUtils.hasMemory(penguin, RockhoppersMemoryModuleTypes.BOAT_TO_FOLLOW) && penguin.getBoatToFollow() != null) {
            if (!this.hasSetUpPreviousBoatPos) {
                previousBoatPos = penguin.getBoatToFollow().position();
                this.hasSetUpPreviousBoatPos = true;
            }
            boolean bl = previousBoatPos.subtract(penguin.getBoatToFollow().position()).horizontalDistance() > 0.075;
            previousBoatPos = penguin.getBoatToFollow().position();
            if (bl)
                BrainUtils.clearMemory(penguin, RockhoppersMemoryModuleTypes.NEAREST_BOBBERS);
        } else if (this.hasSetUpPreviousBoatPos) {
            this.hasSetUpPreviousBoatPos = false;
        }

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
