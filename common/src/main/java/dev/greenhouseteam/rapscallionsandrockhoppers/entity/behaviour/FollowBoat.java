package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class FollowBoat extends ExtendedBehaviour<Penguin> {

    private Vec3 previousBoatPos = null;
    private boolean isSameTickAsStart = false;
    private int timeToRecalcPath;
    private float untilDistance;

    public FollowBoat untilDistance(float untilDistance) {
        this.untilDistance = untilDistance;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED), Pair.of(RockhoppersMemoryModuleTypes.BOAT_TO_FOLLOW, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    public boolean checkExtraStartConditions(ServerLevel level, Penguin penguin) {
        if (penguin.getBoatToFollow() == null) {
            return false;
        }

        if (this.previousBoatPos == null) {
            this.previousBoatPos = new Vec3(penguin.getBoatToFollow().position().x(), 0, penguin.getBoatToFollow().position().z());
        }
        boolean boatPosValue = Mth.abs((float) (this.previousBoatPos.x() - penguin.getBoatToFollow().position().x())) > 0.05 || Mth.abs((float) (this.previousBoatPos.z() - penguin.getBoatToFollow().position().z())) > 0.05;
        this.previousBoatPos = new Vec3(penguin.getBoatToFollow().position().x(), 0, penguin.getBoatToFollow().position().z());
        this.isSameTickAsStart = true;
        return boatPosValue;
    }

    @Override
    public boolean shouldKeepRunning(Penguin penguin) {
        if (penguin.getBoatToFollow() == null) {
            return false;
        }

        if (this.isSameTickAsStart) {
            this.isSameTickAsStart = false;
            return true;
        }

        boolean boatPosValue = Mth.abs((float) (this.previousBoatPos.x() - penguin.getBoatToFollow().position().x())) > 0.05 || Mth.abs((float) (this.previousBoatPos.z() - penguin.getBoatToFollow().position().z())) > 0.05;
        this.previousBoatPos = new Vec3(penguin.getBoatToFollow().position().x(), 0, penguin.getBoatToFollow().position().z());
        return boatPosValue;
    }

    @Override
    protected void start(Penguin penguin) {
        BrainUtils.clearMemory(penguin, MemoryModuleType.ATTACK_TARGET);
    }

    @Override
    protected void tick(Penguin penguin) {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            Direction direction = penguin.getBoatToFollow().getDirection().getOpposite();
            Vec3 directionVec = new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ()).multiply(this.untilDistance, 1.0F, this.untilDistance);
            BlockPos boatPos = penguin.getBoatToFollow().blockPosition().offset((int) directionVec.x(), (int) directionVec.y(), (int) directionVec.z());
            boatPos = boatPos.offset(0, -1, 0);
            BrainUtils.setMemory(penguin, MemoryModuleType.WALK_TARGET, new WalkTarget(boatPos, penguin.isInWater() ? 2.0F : 1.5F, 1));
        }
    }

    @Override
    protected void stop(Penguin penguin) {
        this.previousBoatPos = null;
    }
}
