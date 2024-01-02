package dev.greenhouseteam.rapscallionsandrockhoppers.entity.sensor;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersSensorTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.UUID;

public class PlayerToCoughForSensor extends PredicateSensor<Boat, Penguin> {

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return List.of(RockhoppersMemoryModuleTypes.FISH_EATEN, RockhoppersMemoryModuleTypes.LAST_FOLLOWING_BOAT_CONTROLLER, RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR);
    }

    protected void doTick(ServerLevel level, Penguin penguin) {
        if (BrainUtils.hasMemory(penguin, RockhoppersMemoryModuleTypes.FISH_EATEN) && !BrainUtils.hasMemory(penguin, RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR)) {
            UUID uuid = BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.LAST_FOLLOWING_BOAT_CONTROLLER);
            if (uuid == null) return;
            Entity entity = level.getEntity(uuid);
            if (entity != null && entity.onGround() && !entity.isInWaterOrBubble() && entity.level().getBlockState(entity.getOnPos()).isCollisionShapeFullBlock(entity.level(), entity.getOnPos()) && penguin.distanceTo(entity) < 16.0F) {
                BrainUtils.setMemory(penguin, RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR, uuid);
            }
        } else {
            UUID uuid = BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR);
            if (uuid == null) return;
            Entity entity = level.getEntity(uuid);
            if (entity == null || !entity.level().getBlockState(entity.getOnPos()).isCollisionShapeFullBlock(entity.level(), entity.getOnPos())  || penguin.distanceTo(entity) > 16.0F) {
                BrainUtils.clearMemory(penguin, RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR);
            }
        }
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return RockhoppersSensorTypes.BOAT_TO_FOLLOW;
    }
}
