package dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Optional;

public class PenguinSwapBetweenWaterAndLandGoal extends RandomStrollGoal {
    private static final int SWAP_CHANCE = 40;

    public PenguinSwapBetweenWaterAndLandGoal(Penguin penguin) {
        super(penguin, 1.0F);
    }

    @Override
    public boolean canUse() {
        if ((this.mob.getRandom().nextInt(SWAP_CHANCE) == 0)) {
            Vec3 vec3 = this.getPosition();
            if (vec3 == null || !this.mob.level().hasChunkAt(BlockPos.containing(vec3))) {
                return false;
            } else {
                this.wantedX = vec3.x();
                this.wantedY = vec3.y();
                this.wantedZ = vec3.z();
                this.forceTrigger = false;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    protected Vec3 getPosition() {
        BlockPos pointOfInterest = ((Penguin) this.mob).getPointOfInterest() == null ? this.mob.blockPosition() : ((Penguin) this.mob).getPointOfInterest();

        Optional<BlockPos> generatedPos = BlockPos.betweenClosedStream(new BoundingBox(-12, -12, -12, 12, 12, 12).moved(pointOfInterest.getX(), pointOfInterest.getY(), pointOfInterest.getZ())).filter(pos -> (((Penguin) this.mob).getPointOfInterest() == null || !GoalUtils.mobRestricted(this.mob, 8)) && !GoalUtils.isSolid(this.mob, pos) && (this.mob.isInWaterOrBubble() ^ GoalUtils.isWater(this.mob, pos))).map(BlockPos::immutable).min(Comparator.comparing(pos -> pos.distManhattan(this.mob.blockPosition())));
        return generatedPos.map(BlockPos::getCenter).orElse(null);
    }

}
