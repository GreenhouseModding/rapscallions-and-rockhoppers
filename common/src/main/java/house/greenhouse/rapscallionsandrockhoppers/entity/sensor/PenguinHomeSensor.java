package house.greenhouse.rapscallionsandrockhoppers.entity.sensor;

import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersSensorTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PenguinHomeSensor extends ExtendedSensor<Penguin> {

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return List.of(MemoryModuleType.HOME);
    }

    protected void doTick(ServerLevel level, Penguin penguin) {
        if (BrainUtils.hasMemory(penguin, RockhoppersMemoryModuleTypes.BOAT_TO_FOLLOW) && penguin.tickCount % 20 != 0)
            return;

        Optional<GlobalPos> homePos = Optional.ofNullable(getHomePos(penguin));
        if (homePos.isPresent() && (!BrainUtils.hasMemory(penguin, MemoryModuleType.HOME) || homePos.get().pos().distToCenterSqr(penguin.position()) > 24 * 24)) {
            BrainUtils.setMemory(penguin, MemoryModuleType.HOME, homePos.orElse(null));
        }
    }

    @Nullable
    protected GlobalPos getHomePos(Penguin penguin) {
        ResourceKey<Level> levelResourceKey = penguin.level().dimension();
        if (levelResourceKey != Level.OVERWORLD)
            return null;
        if (penguin.isInWaterOrBubble() && !BrainUtils.hasMemory(penguin, RockhoppersMemoryModuleTypes.IS_JUMPING)) {
            BlockPos.MutableBlockPos mutableBlockPos = penguin.blockPosition().mutable();
            while (penguin.level().getFluidState(mutableBlockPos).is(FluidTags.WATER)) {
                mutableBlockPos.move(Direction.UP);
            }
            BlockPos immutableBlockPos = mutableBlockPos.immutable();
            return GlobalPos.of(levelResourceKey, immutableBlockPos);
        }
        return null;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return RockhoppersSensorTypes.PENGUIN_HOME;
    }
}
