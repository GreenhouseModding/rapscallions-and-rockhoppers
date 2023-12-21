package dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.phys.Vec3;

public class PenguinStrollGoal extends RandomStrollGoal {
    public PenguinStrollGoal(PathfinderMob mob) {
        super(mob, 1.0F, 60);
    }

    @Override
    public Vec3 getPosition() {
        return this.mob.isInWaterOrBubble() ? BehaviorUtils.getRandomSwimmablePos(this.mob, 10, 7) : super.getPosition();
    }
}
