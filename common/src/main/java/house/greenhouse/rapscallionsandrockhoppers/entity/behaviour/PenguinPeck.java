package house.greenhouse.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.Optional;

public class PenguinPeck extends AnimatableMeleeAttack<Penguin> {
    public PenguinPeck(int delayTicks) {
        super(delayTicks);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(RockhoppersMemoryModuleTypes.HUNGRY_TIME, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT));
    }
    @Override
    protected void start(Penguin penguin) {
        penguin.setPeckTicks(0);
        BehaviorUtils.lookAtEntity(penguin, this.target);
    }

    @Override
    protected void doDelayedAction(Penguin penguin) {
        BrainUtils.setForgettableMemory(penguin, MemoryModuleType.ATTACK_COOLING_DOWN, true, this.attackIntervalSupplier.apply(penguin));
        if (this.target != null) {
            if (penguin.getSensing().hasLineOfSight(this.target) && penguin.isWithinMeleeAttackRange(this.target) && this.target.isAlive()) {
                penguin.doHurtTarget(this.target);
                if (!this.target.isAlive()) {
                    this.target.remove(Entity.RemovalReason.KILLED);
                    penguin.setHungryTime(Optional.of(penguin.tickCount + 4800));
                    penguin.setTimeAllowedToEat(Optional.of(penguin.tickCount + 400));
                    penguin.incrementFishEaten();
                    BrainUtils.clearMemory(penguin, MemoryModuleType.ATTACK_TARGET);
                    BrainUtils.clearMemory(penguin, MemoryModuleType.WALK_TARGET);
                }
            }
        }
    }

    protected void stop(Penguin penguin) {
        this.target = null;
    }

}