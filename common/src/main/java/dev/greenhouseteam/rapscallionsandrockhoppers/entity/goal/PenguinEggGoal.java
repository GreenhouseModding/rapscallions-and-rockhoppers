package dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal;

import dev.greenhouseteam.rapscallionsandrockhoppers.block.PenguinEggBlock;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

// TODO: Implement this as a behaviour
public class PenguinEggGoal extends MoveToBlockGoal {
    public static final int EGG_CRACK_TIME = 6000;
    private final Penguin penguin;
    private int eggCrackTime = EGG_CRACK_TIME;
    public PenguinEggGoal(Penguin penguin, double speedModifier, int searchDistance) {
        super(penguin, speedModifier, searchDistance);
        this.penguin = penguin;
    }

    @Override
    public void start() {
        super.start();
        eggCrackTime = EGG_CRACK_TIME;
    }

    @Override
    public boolean canUse() {
        return !penguin.isBaby()&& super.canUse();
    }

    @Override
    public void tick() {
        if (penguin.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ()) > 1.2) {
            return;
        }
        BlockState blockState = penguin.level().getBlockState(blockPos);
        if (blockState.is(RockhoppersBlocks.PENGUIN_EGG) && penguin.level() instanceof ServerLevel serverLevel) {
            eggCrackTime -= 1;
            if (eggCrackTime <= 0) {
                PenguinEggBlock.crackEgg(blockState, serverLevel, blockPos);
                eggCrackTime = EGG_CRACK_TIME;
            }
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        if (!(levelReader instanceof ServerLevel serverLevel)) return false;
        if (levelReader.isEmptyBlock(blockPos.above())) {
            BlockState blockState = levelReader.getBlockState(blockPos);
            if (blockState.is(RockhoppersBlocks.PENGUIN_EGG)) {
                var penguins = serverLevel.getEntitiesOfClass(Penguin.class, new AABB(blockPos.above()).inflate(1.0), (otherPenguin) -> !otherPenguin.is(this.penguin));
                return penguins.isEmpty();
            }
        }
        return false;
    }
}
