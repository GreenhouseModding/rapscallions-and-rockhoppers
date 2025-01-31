package dev.greenhouseteam.rapscallionsandrockhoppers.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BoatRenderer.class)
public abstract class BoatRendererMixin extends EntityRenderer<Boat> {

    protected BoatRendererMixin(EntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    public void addBoatHookRenderering(Boat boat, float yaw, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, CallbackInfo ci) {
        BoatLinksAttachment boatData = RapscallionsAndRockhoppers.getHelper().getBoatData(boat);
        boatData.getPreviousLinkedBoats().forEach(previous -> rapscallionsandrockhoppers$renderLeash(boat, yaw, tickDelta, poseStack, multiBufferSource, previous));
        if (boatData.getLinkedPlayer() != null) {
            rapscallionsandrockhoppers$renderLeash(boat, yaw, tickDelta, poseStack, multiBufferSource, boatData.getLinkedPlayer());
        }
    }

    @Unique
    private void rapscallionsandrockhoppers$renderLeash(Boat thisBoat, float yaw, float tickDelta, PoseStack poseStack, MultiBufferSource bufferSource, Entity linkedTo) {
        poseStack.pushPose();
        Vec3 linkedToPosition;
        if (linkedTo instanceof Boat) {
            linkedToPosition = rapscallionsandrockhoppers$approximateClosestHitPoint(linkedTo, thisBoat).add(0.0, linkedTo.position().y() + (linkedTo.getEyeHeight() / 2.0), 0.0);
        } else
            linkedToPosition = linkedTo.getRopeHoldPosition(tickDelta);
        double $$6 = (double)(Mth.lerp(tickDelta, thisBoat.getYRot(), thisBoat.lerpTargetYRot()) * 0.017453292F) + 1.5707963267948966;

        Vec3 $$7 = rapscallionsandrockhoppers$approximateClosestHitPoint(thisBoat, linkedTo).subtract(Mth.lerp(tickDelta, thisBoat.xo, thisBoat.getX()), 0.0, Mth.lerp(tickDelta, thisBoat.zo, thisBoat.getZ())).add(0.0, thisBoat.getEyeHeight() / 2.0, 0.0);
        double $$8 = Math.cos($$6) * $$7.z + Math.sin($$6) * $$7.x;
        double $$9 = Math.sin($$6) * $$7.z - Math.cos($$6) * $$7.x;
        double xLerped = Mth.lerp(tickDelta, thisBoat.xo, thisBoat.getX()) + $$8;
        double yLerped = Mth.lerp(tickDelta, thisBoat.yo, thisBoat.getY()) + $$7.y;
        double zLerped = Mth.lerp(tickDelta, thisBoat.zo, thisBoat.getZ()) + $$9;
        poseStack.translate($$8, $$7.y, $$9);
        float xDisplacement = (float)(linkedToPosition.x - xLerped);
        float yDisplacement = (float)(linkedToPosition.y - yLerped);
        float zDisplacement = (float)(linkedToPosition.z - zLerped);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
        Matrix4f matrix = poseStack.last().pose();
        float $$19 = Mth.invSqrt(xDisplacement * xDisplacement + zDisplacement * zDisplacement) * 0.025F / 2.0F;
        float crossXDirection = zDisplacement * $$19;
        float crossZDirection = xDisplacement * $$19;
        BlockPos $$22 = BlockPos.containing(thisBoat.getEyePosition(tickDelta));
        BlockPos $$23 = BlockPos.containing(linkedTo.getEyePosition(tickDelta));
        int thisBoatBlockLight = this.getBlockLightLevel(thisBoat, $$22);
        int otherBoatBlockLight = linkedTo.isOnFire() ? 15 : linkedTo.level().getBrightness(LightLayer.BLOCK, $$23);
        int thisBoatSkyLight = thisBoat.level().getBrightness(LightLayer.SKY, $$22);
        int otherBoatSkyLight = thisBoat.level().getBrightness(LightLayer.SKY, $$23);

        for(int pieceIndex = 0; pieceIndex <= 24; ++pieceIndex) {
            rapscallionsandrockhoppers$addBoatHookVertexPair(vertexConsumer, matrix, xDisplacement, yDisplacement, zDisplacement, thisBoatBlockLight, otherBoatBlockLight, thisBoatSkyLight, otherBoatSkyLight, 0.025F, 0.025F, crossXDirection, crossZDirection, pieceIndex, false);
        }

        for(int pieceIndex = 24; pieceIndex >= 0; --pieceIndex) {
            rapscallionsandrockhoppers$addBoatHookVertexPair(vertexConsumer, matrix, xDisplacement, yDisplacement, zDisplacement, thisBoatBlockLight, otherBoatBlockLight, thisBoatSkyLight, otherBoatSkyLight, 0.025F, 0.0F, crossXDirection, crossZDirection, pieceIndex, true);
        }

        poseStack.popPose();
    }

    @Unique
    Vec3 rapscallionsandrockhoppers$approximateClosestHitPoint(Entity thisBoat, Entity linkedTo) {
        Vec3 hitboxVec = new Vec3(thisBoat.getBoundingBox().getXsize() / 2, 0, thisBoat.getBoundingBox().getZsize() / 2);
        Vec3 otherHitboxVec = new Vec3(linkedTo.getBoundingBox().getXsize() / 2, 0, linkedTo.getBoundingBox().getZsize() / 2);
        Vec3 closestPoint1 = rapscallionsandrockhoppers$getClosestHitPoint(thisBoat.position(), linkedTo.position(), hitboxVec);
        Vec3 closestPoint2 = rapscallionsandrockhoppers$getClosestHitPoint(linkedTo.position(), thisBoat.position(),  otherHitboxVec);
        Vec3 closestPoint3 = rapscallionsandrockhoppers$getClosestHitPoint(thisBoat.position(), closestPoint2, hitboxVec);
        return closestPoint1.add(closestPoint3).multiply(0.5, 0.0, 0.5);
    }

    @Unique
    Vec3 rapscallionsandrockhoppers$getClosestHitPoint(Vec3 selfPos, Vec3 targetPos, Vec3 hitBox) {
        Vec3 relative = targetPos.subtract(selfPos);
        Vec3 clampedPos = new Vec3(
                Math.min(Math.max(relative.x, -hitBox.x), hitBox.x),
                relative.y(),
                Math.min(Math.max(relative.z, -hitBox.z), hitBox.z)
        );
        return clampedPos.add(selfPos);
    }

    @Unique
    private static void rapscallionsandrockhoppers$addBoatHookVertexPair(VertexConsumer vertexConsumer, Matrix4f matrix4f, float xDisplacement, float yDisplacement, float zDisplacement, int leashedEntityBlockLight, int holdingEntityBlockLight, int leashedEntitySkyLight, int holdingEntitySkyLight, float leashYOffset, float crossYDirection, float crossXDirection, float crossZDirection, int pieceIndex, boolean swapDarkDirection) {
        float f = (float)pieceIndex / 24.0F;
        int blockLight = (int)Mth.lerp(f, (float)leashedEntityBlockLight, (float)holdingEntityBlockLight);
        int skyLight = (int)Mth.lerp(f, (float)leashedEntitySkyLight, (float)holdingEntitySkyLight);
        int packedLight = LightTexture.pack(blockLight, skyLight);
        float darkOrLight = pieceIndex % 2 == (swapDarkDirection ? 1 : 0) ? 0.7F : 1.0F;
        float r = 0.5F * darkOrLight;
        float g = 0.4F * darkOrLight;
        float b = 0.3F * darkOrLight;
        float n = xDisplacement * f;
        float o = yDisplacement > 0.0F ? yDisplacement * f * f : yDisplacement - yDisplacement * (1.0F - f) * (1.0F - f);
        float p = zDisplacement * f;
        vertexConsumer.addVertex(matrix4f, n - crossXDirection, o + crossYDirection, p + crossZDirection).setColor(r, g, b, 1.0F).setLight(packedLight);
        vertexConsumer.addVertex(matrix4f, n + crossXDirection, o + leashYOffset - crossYDirection, p - crossZDirection).setColor(r, g, b, 1.0F).setLight(packedLight);
    }

}
