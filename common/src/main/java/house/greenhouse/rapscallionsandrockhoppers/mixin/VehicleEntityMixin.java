package house.greenhouse.rapscallionsandrockhoppers.mixin;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersItems;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VehicleEntity.class)
public class VehicleEntityMixin {
    @Inject(method = "destroy(Lnet/minecraft/world/item/Item;)V", at = @At("HEAD"))
    private void rapscallionsandrockhoppers$returnHook(Item item, CallbackInfo ci) {
        if ((VehicleEntity)(Object)this instanceof Boat boat && RapscallionsAndRockhoppers.getHelper().getBoatData(boat).getLinkedPlayer() != null) {
            ((VehicleEntity)(Object)this).spawnAtLocation(RockhoppersItems.BOAT_HOOK);
        }
    }
}
