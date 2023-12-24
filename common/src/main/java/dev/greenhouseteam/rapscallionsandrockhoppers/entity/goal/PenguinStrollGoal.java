package dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.PathfindUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PenguinStrollGoal extends RandomStrollGoal {
    private boolean wasInWater = false;

    public PenguinStrollGoal(Penguin mob) {
        super(mob, 1.0F);
    }

    @Override
    public boolean canUse() {
        if (!wasInWater && this.mob.isInWaterOrBubble()) {
            this.setInterval(60);
            this.wasInWater = true;
        } else if (wasInWater && !this.mob.isInWaterOrBubble()) {
            this.setInterval(120);
            this.wasInWater = false;
        }
        return super.canUse();
    }

    @Override @Nullable
    public Vec3 getPosition() {
        Vec3 position = this.mob.isInWaterOrBubble() ? DefaultRandomPos.getPos(this.mob, 10, 7) : PathfindUtil.getRandomLandPos(this.mob, 6, 5);
        if (position != null && ((((Penguin)this.mob).getPointOfInterest() != null && GoalUtils.mobRestricted(this.mob, 0)) || GoalUtils.hasMalus(this.mob, BlockPos.containing(position)))) {
            return null;
        }
        return position;
    }
}
