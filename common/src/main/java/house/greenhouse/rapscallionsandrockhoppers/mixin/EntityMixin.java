package house.greenhouse.rapscallionsandrockhoppers.mixin;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import house.greenhouse.rapscallionsandrockhoppers.attachment.PlayerLinksAttachment;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract void setSwimming(boolean swimming);

    @Shadow public abstract boolean isSwimming();

    // TODO: Move to custom DFU.
    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void bovinesandbuttercups$loadFromLegacyAttachments(CompoundTag tag, CallbackInfo ci) {
        if ((Entity) (Object) this instanceof Boat boat) {
            BoatLinksAttachment attachment = RapscallionsAndRockhoppers.getHelper().getBoatData(boat);
            CompoundTag legacyTag = RapscallionsAndRockhoppers.getHelper().getLegacyTagStart(tag).getCompound("rapscallionsandrockhoppers:boat_data");
            if (attachment != null && !legacyTag.isEmpty()) {
                attachment.deserializeLegacyData(legacyTag);
            }
        }
        if ((Entity) (Object) this instanceof Player player) {
            PlayerLinksAttachment attachment = RapscallionsAndRockhoppers.getHelper().getPlayerData(player);
            CompoundTag legacyTag = RapscallionsAndRockhoppers.getHelper().getLegacyTagStart(tag).getCompound("rapscallionsandrockhoppers:boat_data");
            if (attachment != null && !legacyTag.isEmpty()) {
                attachment.deserializeLegacyData(legacyTag);
            }
        }
    }
    
    @Inject(method = "updateSwimming", at = @At("HEAD"), cancellable = true)
    private void rapscallionsandrockhoppers$shouldUnsetSwimmingIfSinking(CallbackInfo ci) {
        if ((Entity)(Object)this instanceof LivingEntity living && living.hasEffect(RockhoppersMobEffects.SINKING)) {
            if (isSwimming())
                setSwimming(false);
            ci.cancel();
        }
    }
}
