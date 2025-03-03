package house.greenhouse.rapscallionsandrockhoppers.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import house.greenhouse.rapscallionsandrockhoppers.client.renderer.BoatHookLeashRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    @Inject(method = "render", at = @At("HEAD"))
    public void addBoatHookRenderering(T entity, float yaw, float tickDelta, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (entity instanceof Boat boat) {
            BoatLinksAttachment boatData = RapscallionsAndRockhoppers.getHelper().getBoatData(boat);
            boatData.getPreviousLinkedBoats(entity.level()).forEach(previous -> BoatHookLeashRenderer.renderLeash(boat, yaw, tickDelta, poseStack, bufferSource, previous));
            if (boatData.getLinkedPlayer(entity.level()) != null) {
                BoatHookLeashRenderer.renderLeash(boat, yaw, tickDelta, poseStack, bufferSource, boatData.getLinkedPlayer(entity.level()));
            } else if (boatData.getHookKnot(entity.level()) != null) {
                BoatHookLeashRenderer.renderLeash(boat, yaw, tickDelta, poseStack, bufferSource, boatData.getHookKnot(entity.level()));
            }
        }
    }
    
    @Inject(method = "shouldRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;getLeashHolder()Lnet/minecraft/world/entity/Entity;"), cancellable = true)
    public void shouldRenderBoatHook(T livingEntity, Frustum camera, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity instanceof Boat boat && RapscallionsAndRockhoppers.getHelper().getBoatData(boat).getHookKnotUuid().isPresent()) {
            var boatKnot = RapscallionsAndRockhoppers.getHelper().getBoatData(boat).getHookKnot(boat.level());
            if (boatKnot != null) {
                cir.setReturnValue(camera.isVisible(boatKnot.getBoundingBoxForCulling()));
            }
        }
    }
}
