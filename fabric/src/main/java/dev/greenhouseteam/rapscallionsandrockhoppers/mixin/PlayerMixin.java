package dev.greenhouseteam.rapscallionsandrockhoppers.mixin;

import dev.greenhouseteam.rapscallionsandrockhoppers.RockhoppersEntityComponents;
import dev.greenhouseteam.rapscallionsandrockhoppers.componability.PlayerDataComponent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void rapscallionsandrockhoppers$removeInvalidLinkedBoats(CallbackInfo ci) {
        PlayerDataComponent capability = ((Player)(Object)this).getComponent(RockhoppersEntityComponents.PLAYER_DATA_COMPONENT);
        if (((Player)(Object)this).tickCount % 20 == 0) {
            capability.invalidateNonExistentBoats();
        }
    }
}
