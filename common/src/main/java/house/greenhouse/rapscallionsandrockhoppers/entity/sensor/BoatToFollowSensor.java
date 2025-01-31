package house.greenhouse.rapscallionsandrockhoppers.entity.sensor;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.mixin.BoatAccessor;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersSensorTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE;

public class BoatToFollowSensor extends PredicateSensor<Boat, Penguin> {
    @Nullable
    private SquareRadius radius = null;
    @Nullable
    private SquareRadius updatePenguinRadius = null;

    public BoatToFollowSensor() {
        this.setScanRate(penguin -> 5);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return List.of(RockhoppersMemoryModuleTypes.BOAT_TO_FOLLOW, RockhoppersMemoryModuleTypes.LAST_FOLLOWING_BOAT_CONTROLLER);
    }

    public BoatToFollowSensor setRadius(double radius) {
        return setRadius(radius, radius);
    }

    public BoatToFollowSensor setRadius(double xz, double y) {
        this.radius = new SquareRadius(xz, y);
        return this;
    }

    public BoatToFollowSensor setUpdatePenguinRadius(double xz, double y) {
        this.updatePenguinRadius = new SquareRadius(xz, y);
        return this;
    }

    protected void doTick(ServerLevel level, Penguin penguin) {
        SquareRadius radius = this.radius;

        if (radius == null) {
            double dist = penguin.getAttributeValue(FOLLOW_RANGE);

            radius = new SquareRadius(dist, dist);
        }

        if (penguin.getBoatToFollow() == null && penguin.getTimeAllowedToFollowBoat() < penguin.tickCount && !penguin.isBaby()) {
            Optional<Boat> boat = EntityRetrievalUtil.<Boat>getEntities(level, radius.inflateAABB(penguin.getBoundingBox()), obj -> obj instanceof Boat b && ((BoatAccessor)b).rapscallionsandrockhoppers$getStatus() != null && ((BoatAccessor)b).rapscallionsandrockhoppers$getStatus().equals(Boat.Status.IN_WATER) && b.hasControllingPassenger() && RapscallionsAndRockhoppers.getHelper().getBoatData(b).penguinCount() < 3).stream().min(Comparator.comparingInt(b -> RapscallionsAndRockhoppers.getHelper().getBoatData(b).penguinCount()));
            if (boat.isPresent()) {
                penguin.setBoatToFollow(boat.get().getUUID());
                if (this.updatePenguinRadius != null) {
                    EntityRetrievalUtil.<Penguin>getEntities(level, this.updatePenguinRadius.inflateAABB(penguin.getBoundingBox()), obj -> obj instanceof Penguin && obj.isAlive()).forEach(p -> p.setTimeAllowedToFollowBoat(Optional.of(p.tickCount + 20)));
                }
                RapscallionsAndRockhoppers.getHelper().getBoatData(boat.get()).addFollowingPenguin(penguin.getUUID());
                RapscallionsAndRockhoppers.getHelper().getBoatData(boat.get()).sync();
                penguin.setHungryTime(Optional.of(penguin.tickCount + 4800));
                penguin.setTimeAllowedToEat(Optional.of(penguin.tickCount));
                ((ServerLevel)penguin.level()).sendParticles(ParticleTypes.GLOW, boat.get().getX(), boat.get().getY(), boat.get().getZ(), 8, 0.5, 0.25, 0.5, 0.02);
                penguin.previousBoatPos = penguin.getBoatToFollow().position();
            }
        } else if (BrainUtils.hasMemory(penguin, RockhoppersMemoryModuleTypes.BOAT_TO_FOLLOW)) {
            Boat boat = penguin.getBoatToFollow();
            if (boat != null && boat.getControllingPassenger() instanceof Player) {
                BrainUtils.setMemory(penguin, RockhoppersMemoryModuleTypes.LAST_FOLLOWING_BOAT_CONTROLLER, boat.getControllingPassenger().getUUID());
            }
            if (boat == null || boat.isRemoved()) {
                penguin.setBoatToFollow(null);
                BrainUtils.clearMemory(penguin, RockhoppersMemoryModuleTypes.LAST_FOLLOWING_BOAT_CONTROLLER);
            }
        }
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return RockhoppersSensorTypes.BOAT_TO_FOLLOW;
    }
}
