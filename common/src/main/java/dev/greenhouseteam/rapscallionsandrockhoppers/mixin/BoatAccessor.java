package dev.greenhouseteam.rapscallionsandrockhoppers.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Boat.class)
public interface BoatAccessor {
    @Invoker("clampRotation")
    void rapscallionsandrockhoppers$invokeClampRotation(Entity entity);
}
