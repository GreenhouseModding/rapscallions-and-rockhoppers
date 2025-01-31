package house.greenhouse.rapscallionsandrockhoppers.entity.sensor;

import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersSensorTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.EntityFilteringSensor;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public class PenguinAttackTargetSensor<E extends LivingEntity> extends EntityFilteringSensor<LivingEntity, E> {

    protected MemoryModuleType<LivingEntity> getMemory() {
        return MemoryModuleType.NEAREST_ATTACKABLE;
    }

    @Override
    protected BiPredicate<LivingEntity, E> predicate() {
        return (entity, e) -> entity.getType().is(RockhoppersTags.EntityTypeTags.PENGUIN_ALWAYS_HOSTILES);
    }

    protected @Nullable LivingEntity findMatches(E entity, NearestVisibleLivingEntities matcher) {
        return matcher.findClosest((target) -> this.predicate().test(target, entity)).orElse(null);
    }

    public SensorType<? extends ExtendedSensor<?>> type() {
        return RockhoppersSensorTypes.PENGUIN_ATTACK_TARGET;
    }
}
