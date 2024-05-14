package dev.greenhouseteam.rapscallionsandrockhoppers.mixin;

import dev.greenhouseteam.rapscallionsandrockhoppers.attachment.PlayerLinksAttachment;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersAttachments;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void rapscallionsandrockhoppers$removeInvalidLinkedBoats(CallbackInfo ci) {
        PlayerLinksAttachment attachment = ((Player)(Object)this).getAttached(RockhoppersAttachments.PLAYER_LINKS);
        if (attachment != null && ((Player)(Object)this).tickCount % 20 == 0) {
            attachment.invalidateNonExistentBoats();
        }
    }
}
