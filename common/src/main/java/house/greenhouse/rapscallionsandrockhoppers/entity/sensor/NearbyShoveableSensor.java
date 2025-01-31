package house.greenhouse.rapscallionsandrockhoppers.entity.sensor;

import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersEntityTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersSensorTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.EntityFilteringSensor;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public class NearbyShoveableSensor extends EntityFilteringSensor<Penguin, Penguin> {
    @Override
    protected MemoryModuleType<Penguin> getMemory() {
        return RockhoppersMemoryModuleTypes.NEAREST_VISIBLE_SHOVEABLE;
    }

    @Override
    protected BiPredicate<LivingEntity, Penguin> predicate() {
        return (entity, penguin) -> entity.getType() == RockhoppersEntityTypes.PENGUIN && entity.isAlive() && !entity.isBaby() && !entity.is(penguin) && !((Penguin)entity).isStumbling() && !BrainUtils.hasMemory(entity, MemoryModuleType.IS_IN_WATER) && !BrainUtils.hasMemory(entity, MemoryModuleType.TEMPTING_PLAYER);
    }

    @Override
    protected @Nullable Penguin findMatches(Penguin penguin, NearestVisibleLivingEntities matcher) {
        return (Penguin) matcher.findClosest((target) -> this.predicate().test(target, penguin)).orElse(null);
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return RockhoppersSensorTypes.NEARBY_SHOVEABLE;
    }
}
