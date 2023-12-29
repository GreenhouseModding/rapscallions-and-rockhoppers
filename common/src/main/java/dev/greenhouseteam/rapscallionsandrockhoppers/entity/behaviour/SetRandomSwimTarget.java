package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class SetRandomSwimTarget extends SetRandomWalkTarget<Penguin> {
    protected Predicate<Penguin> avoidLandPredicate = entity -> true;

    public SetRandomSwimTarget dontAvoidLand() {
        return avoidLandWhen(entity -> false);
    }

    public SetRandomSwimTarget avoidLandWhen(Predicate<Penguin> predicate) {
        this.avoidWaterPredicate = predicate;
        return this;
    }

    @Override
    protected @Nullable Vec3 getTargetPos(Penguin penguin) {
        return this.avoidLandPredicate.test(penguin) ? BehaviorUtils.getRandomSwimmablePos(penguin, (int) this.radius.xzRadius(), (int) this.radius.yRadius()) : DefaultRandomPos.getPos(penguin, (int) this.radius.xzRadius(), (int) this.radius.yRadius());
    }
}
