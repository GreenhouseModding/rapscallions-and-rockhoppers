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
    private boolean hasBeenSetUp;
    private boolean wasInWater;

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return List.of(MemoryModuleType.HOME);
    }

    protected void doTick(ServerLevel level, Penguin penguin) {
        if (!this.hasBeenSetUp) {
            this.wasInWater = penguin.isInWaterOrBubble();
            this.hasBeenSetUp = true;
        }

        if (!BrainUtils.hasMemory(penguin, MemoryModuleType.HOME) && !BrainUtils.hasMemory(penguin, RockhoppersMemoryModuleTypes.BOAT_TO_FOLLOW)) {
            BrainUtils.setMemory(penguin, MemoryModuleType.HOME, getInitialHomePos(penguin));
            return;
        }

        Optional<GlobalPos> homePos = Optional.ofNullable(getHomePos(penguin));
        if (homePos.isPresent() && (!BrainUtils.hasMemory(penguin, MemoryModuleType.HOME) || homePos.get().pos().distSqr(BrainUtils.getMemory(penguin, MemoryModuleType.HOME).pos()) > 24 * 24)) {
            BrainUtils.setMemory(penguin, MemoryModuleType.HOME, homePos.orElse(null));
        }
    }

    @Nullable
    protected GlobalPos getHomePos(Penguin penguin) {
        ResourceKey<Level> levelResourceKey = penguin.level().dimension();
        if (penguin.isInWaterOrBubble() && !BrainUtils.hasMemory(penguin, RockhoppersMemoryModuleTypes.IS_JUMPING) && !this.wasInWater) {
            this.wasInWater = true;
            BlockPos.MutableBlockPos mutableBlockPos = penguin.blockPosition().mutable();
            while (!penguin.level().getFluidState(mutableBlockPos).is(FluidTags.WATER)) {
                mutableBlockPos.move(Direction.UP);
            }
            BlockPos immutableBlockPos = mutableBlockPos.immutable();
            if (penguin.level().getBlockState(immutableBlockPos).isPathfindable(PathComputationType.LAND)) {
                return GlobalPos.of(levelResourceKey, immutableBlockPos);
            }
        } else if (!penguin.isInWaterOrBubble() && penguin.onGround() && this.wasInWater) {
            this.wasInWater = false;
            Optional<BlockPos> pos = BlockPos.findClosestMatch(penguin.blockPosition(), 16, 6, p -> penguin.level().getFluidState(p).is(FluidTags.WATER) && penguin.level().getBlockState(p.above()).isPathfindable(PathComputationType.LAND));
            return pos.map(blockPos -> GlobalPos.of(levelResourceKey, blockPos)).orElse(null);
        }
        return null;
    }

    protected GlobalPos getInitialHomePos(Penguin penguin) {
        @Nullable GlobalPos pos = getHomePos(penguin);
        if (pos != null) {
            return pos;
        }

        Optional<Penguin> optionalPenguin = penguin.level().getEntitiesOfClass(Penguin.class, penguin.getBoundingBox().inflate(12, 6, 12), penguin1 -> !penguin1.is(penguin) && BrainUtils.hasMemory(penguin1, MemoryModuleType.HOME)).stream().findAny();
        if (optionalPenguin.isPresent()) {
            return BrainUtils.getMemory(optionalPenguin.get(), MemoryModuleType.HOME);
        }
        Optional<BlockPos> waterPos = BlockPos.findClosestMatch(penguin.blockPosition(), 24, 18, p -> penguin.level().getFluidState(p).is(FluidTags.WATER));
        return waterPos.map(blockPos -> GlobalPos.of(penguin.level().dimension(), blockPos)).orElse(null);
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return RockhoppersSensorTypes.PENGUIN_HOME;
    }
}
