package dev.greenhouseteam.rapscallionsandrockhoppers.mixin;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyVariable(method = "collide", at = @At(value = "LOAD"), ordinal = 3)
    private boolean rapscallionsandrockhoppers$handlePenguinStepHeightWhilstSwimming(boolean value) {
        if (((Entity)(Object)this) instanceof Penguin penguin && penguin.isVisuallySwimming()) {
            return true;
        }
        return value;
    }
}
