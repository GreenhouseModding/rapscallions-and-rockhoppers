package house.greenhouse.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class LeaveBoat extends ExtendedBehaviour<Penguin> {
    private Vec3 leavingBoatPos = null;

    public LeaveBoat() {
        this.runFor(penguin -> 200);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED), Pair.of(RockhoppersMemoryModuleTypes.BOAT_TO_FOLLOW, MemoryStatus.VALUE_PRESENT), Pair.of(RockhoppersMemoryModuleTypes.HUNGRY_TIME, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.NEAREST_ATTACKABLE, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    public boolean checkExtraStartConditions(ServerLevel level, Penguin penguin) {
        if (penguin.tickCount < BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.HUNGRY_TIME) && penguin.getBoatToFollow() != null && penguin.getBoatToFollow().distanceTo(penguin) < 32.0) {
            return false;
        }

        this.leavingBoatPos = penguin.getBoatToFollow() == null ? penguin.position() : penguin.getBoatToFollow().position();
        return true;
    }

    @Override
    public boolean shouldKeepRunning(Penguin penguin) {
        return penguin.distanceToSqr(this.leavingBoatPos) < 32.0 * 32.0;
    }

    @Override
    protected void start(Penguin penguin) {
        BrainUtils.setMemory(penguin, RockhoppersMemoryModuleTypes.BOAT_TO_FOLLOW, null);
        Vec3 posAway = DefaultRandomPos.getPosAway(penguin, 32, 8, leavingBoatPos);
        if (posAway != null) {
            BrainUtils.setMemory(penguin, MemoryModuleType.WALK_TARGET, new WalkTarget(posAway, 1.0F, 0));
        }
    }

    @Override
    protected void stop(Penguin penguin) {
        if (penguin.position().distanceTo(this.leavingBoatPos) >= 64.0) {
            penguin.returnToHome();
        }
        this.leavingBoatPos = null;
    }
}
