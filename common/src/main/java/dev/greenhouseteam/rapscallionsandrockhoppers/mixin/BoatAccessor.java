package dev.greenhouseteam.rapscallionsandrockhoppers.mixin;

import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Boat.class)
public interface BoatAccessor {

    @Accessor("status")
    Boat.Status rapscallionsandrockhoppers$getStatus();
}
