package house.greenhouse.rapscallionsandrockhoppers.mixin.neoforge;

import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersActivities;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(FishingHook.class)
public class FishingHookMixin {
    @Unique
    private boolean rapscallionsandrockhoppers$hasAttractedPenguin = false;

    @Inject(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void rapscallionsandrockhopper$setPenguinUpToCatchFish(ItemStack p_37157_, CallbackInfoReturnable<Integer> cir, Player player, int i, ItemFishedEvent event, LootParams lootparams, LootTable loottable, List list, Iterator iterator, ItemStack stack) {
        if (!stack.is(RockhoppersTags.ItemTags.PENGUIN_FOOD_ITEMS) || this.rapscallionsandrockhoppers$hasAttractedPenguin) return;
        Optional<Penguin> penguin = ((FishingHook) (Object) this).level().getEntitiesOfClass(Penguin.class, ((FishingHook) (Object) this).getBoundingBox().inflate(24.0F), penguin1 -> penguin1.getBrain().getActiveNonCoreActivity().map(activity -> activity == RockhoppersActivities.WAIT_AROUND_BOBBER).orElse(false)).stream().min(Comparator.comparing(penguin1 -> ((FishingHook) (Object) this).distanceTo(penguin1)));
        penguin.ifPresent(value -> BrainUtils.setMemory(value, RockhoppersMemoryModuleTypes.CAUGHT_BOBBER, ((FishingHook) (Object) this)));
        this.rapscallionsandrockhoppers$hasAttractedPenguin = true;
    }

    @Inject(method = "retrieve", at = @At("TAIL"))
    private void rapscallionsandrockhopper$clearAttractedPenguin(ItemStack $$0, CallbackInfoReturnable<Integer> cir) {
        this.rapscallionsandrockhoppers$hasAttractedPenguin = false;
    }
}
