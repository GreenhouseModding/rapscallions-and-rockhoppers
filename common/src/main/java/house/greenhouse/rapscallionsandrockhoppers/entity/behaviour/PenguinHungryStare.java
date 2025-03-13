package house.greenhouse.rapscallionsandrockhoppers.entity.behaviour;

import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;

public class PenguinHungryStare extends LookAtTarget<Penguin> {
    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Penguin penguin) {
        return super.checkExtraStartConditions(level, penguin) && 
                penguin.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_PLAYER) && 
                penguin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).get().distanceTo(penguin) < 5;
    }

    @Override
    protected void start(Penguin entity) {
        super.start(entity);
        entity.setIsStaringAtPlayer(true);
    }

    @Override
    protected void stop(Penguin entity) {
        super.stop(entity);
        entity.setIsStaringAtPlayer(false);
    }
}
