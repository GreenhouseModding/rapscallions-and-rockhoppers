package dev.greenhouseteam.rapscallionsandrockhoppers.entity.sensor;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersBlocks;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersSensorTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.phys.AABB;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class NearbyEggSensor extends ExtendedSensor<Penguin> {
    private int radius = 8;

    public NearbyEggSensor() {

    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return RockhoppersSensorTypes.NEARBY_EGG;
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return List.of(RockhoppersMemoryModuleTypes.EGG_POS);
    }

    @Override
    protected void doTick(ServerLevel level, Penguin penguin) {
        if (!BrainUtils.hasMemory(penguin, RockhoppersMemoryModuleTypes.EGG_POS)) {
            AABB boundingBox = new AABB(penguin.blockPosition()).inflate(radius);
            var penguinsInArea = level.getEntitiesOfClass(Penguin.class, boundingBox);
            penguinsInArea.removeIf(otherPenguin -> otherPenguin == penguin);
            BlockPos firstPos = penguin.blockPosition().offset(-radius, -radius, -radius);
            BlockPos lastPos = penguin.blockPosition().offset(radius, radius, radius);
            for (BlockPos eggBlockPos : BlockPos.betweenClosed(firstPos, lastPos)) {
                if (level.getBlockState(eggBlockPos).is(RockhoppersBlocks.PENGUIN_EGG)) {
                    if (penguinsInArea.stream().noneMatch(otherPenguin -> otherPenguin.blockPosition().distSqr(eggBlockPos) < 1)) {
                        penguin.getBrain().setMemory(RockhoppersMemoryModuleTypes.EGG_POS, eggBlockPos);
                        return;
                    }
                }
            }
        }
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
