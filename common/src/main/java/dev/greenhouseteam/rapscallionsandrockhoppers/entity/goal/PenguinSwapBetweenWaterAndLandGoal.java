package dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class PenguinSwapBetweenWaterAndLandGoal extends RandomStrollGoal {
    private static final int SWAP_WATER_OR_LAND_CHANCE = 1200;

    public PenguinSwapBetweenWaterAndLandGoal(Penguin penguin) {
        super(penguin, 1.0F);
    }

    @Override
    public boolean canUse() {
        return !this.mob.isInWaterOrBubble() && ((Penguin)this.mob).getPointOfInterest() != null && this.mob.getRandom().nextInt(SWAP_WATER_OR_LAND_CHANCE) == 0 && super.canUse();
    }

    @Override
    protected Vec3 getPosition() {
        BlockPos pointOfInterest = ((Penguin) this.mob).getPointOfInterest();

        Vec3 returnPos = null;

        while (returnPos == null) {
            Vec3 generatedPos = RandomPos.generateRandomPos(() -> pointOfInterest, value -> 0.0D);
            if (generatedPos != null && this.mob.isWithinRestriction(BlockPos.containing(generatedPos))) {
                while (GoalUtils.isSolid(this.mob, BlockPos.containing(generatedPos))) {
                    generatedPos = generatedPos.add(0.0, 1.0, 0.0);
                }

                if (this.mob.isInWaterOrBubble() ^ GoalUtils.isWater(this.mob, BlockPos.containing(generatedPos))) {
                    returnPos = generatedPos;
                }
            }
        }

        return returnPos;
    }

}
