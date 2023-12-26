package dev.greenhouseteam.rapscallionsandrockhoppers.entity.sensor;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersMemoryModuleTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersSensorTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.Optional;

public class NearbyWaterSensor extends ExtendedSensor<Penguin> {
    private int xzRadius = 8;
    private int yRadius = 4;

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return List.of(RapscallionsAndRockhoppersMemoryModuleTypes.NEAREST_WATER);
    }

    public NearbyWaterSensor setXZRadius(int xzRadius) {
        this.xzRadius = xzRadius;
        return this;
    }

    public NearbyWaterSensor setYRadius(int yRadius) {
        this.yRadius = yRadius;
        return this;
    }

    @Override
    protected void doTick(ServerLevel level, Penguin entity) {
        Optional<BlockPos> waterPos = BlockPos.findClosestMatch(entity.blockPosition(), xzRadius, yRadius, (pos) -> level.getFluidState(pos).is(FluidTags.WATER));
        BrainUtils.setMemory(entity, RapscallionsAndRockhoppersMemoryModuleTypes.NEAREST_WATER, waterPos.orElse(null));
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return RapscallionsAndRockhoppersSensorTypes.NEARBY_WATER;
    }
}
