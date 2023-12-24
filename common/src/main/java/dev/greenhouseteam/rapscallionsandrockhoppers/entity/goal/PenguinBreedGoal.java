package dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RapscallionsAndRockhoppersBlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;

public class PenguinBreedGoal extends BreedGoal {
    public PenguinBreedGoal(Penguin $$0, double $$1) {
        super($$0, $$1);
    }

    @Override
    protected void breed() {
        ServerPlayer player = this.animal.getLoveCause();
        if (player == null && this.partner.getLoveCause() != null) {
            player = this.partner.getLoveCause();
        }

        if (player != null) {
            player.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(player, this.animal, this.partner, null);
        }


        BlockPos blockPos = this.animal.blockPosition();
        BlockState blockstate = this.level.getBlockState(blockPos);

        if (blockstate.isAir()) {
            this.level.setBlockAndUpdate(blockPos, RapscallionsAndRockhoppersBlocks.PENGUIN_EGG.defaultBlockState());
        }
        this.animal.setAge(6000);
        this.animal.resetLove();
        this.partner.setAge(6000);
        this.partner.resetLove();
        RandomSource penguinRandom = this.animal.getRandom();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), penguinRandom.nextInt(7) + 1));
        }

    }
}
