package dev.greenhouseteam.rapscallionsandrockhoppers.entity.goal;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.world.entity.ai.goal.PanicGoal;

public class PenguinPanicGoal extends PanicGoal {
    public PenguinPanicGoal(Penguin penguin, double speedModifier) {
        super(penguin, speedModifier);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && ((Penguin)mob).isShocked();
    }
}
