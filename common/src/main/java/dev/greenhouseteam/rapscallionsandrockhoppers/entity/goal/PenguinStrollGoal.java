package dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PenguinStrollGoal extends RandomStrollGoal {
    public PenguinStrollGoal(Penguin mob) {
        super(mob, 1.0F, 30);
    }

    @Override @Nullable
    public Vec3 getPosition() {
        Vec3 position = this.mob.isInWaterOrBubble() ? BehaviorUtils.getRandomSwimmablePos(this.mob, 10, 7) : LandRandomPos.getPos(this.mob, 10, 7);
        if (((Penguin)this.mob).getPointOfInterest() != null && GoalUtils.mobRestricted(this.mob, 0)) {
            return null;
        }
        return position;
    }
}
