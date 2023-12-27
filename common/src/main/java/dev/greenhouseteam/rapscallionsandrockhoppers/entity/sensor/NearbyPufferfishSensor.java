package dev.greenhouseteam.rapscallionsandrockhoppers.entity.sensor;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersSensorTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Pufferfish;
import net.tslat.smartbrainlib.api.core.sensor.EntityFilteringSensor;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public class NearbyPufferfishSensor extends EntityFilteringSensor<Pufferfish, Penguin> {
    @Override
    protected MemoryModuleType<Pufferfish> getMemory() {
        return RockhoppersMemoryModuleTypes.NEAREST_VISIBLE_PUFFERFISH;
    }

    @Override
    protected BiPredicate<LivingEntity, Penguin> predicate() {
        return (entity, penguin) -> entity.getType() == EntityType.PUFFERFISH;
    }

    @Override
    protected @Nullable Pufferfish findMatches(Penguin penguin, NearestVisibleLivingEntities matcher) {
        return (Pufferfish) matcher.findClosest((target) -> this.predicate().test(target, penguin)).orElse(null);
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return RockhoppersSensorTypes.NEARBY_PUFFERFISH;
    }
}
