package house.greenhouse.rapscallionsandrockhoppers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class BoatHookLeashRenderer {

    public static void renderLeash(Boat thisBoat, float yaw, float tickDelta, PoseStack poseStack, MultiBufferSource bufferSource, Entity linkedTo) {
        poseStack.pushPose();
        Vec3 linkedToPosition;
        if (linkedTo instanceof Boat) {
            linkedToPosition = getEllipseIntersect(linkedTo, thisBoat.getPosition(tickDelta), tickDelta)
                    .add(linkedTo.getPosition(tickDelta).x(), linkedTo.getPosition(tickDelta).y() + linkedTo.getEyeHeight() / 2.0, linkedTo.getPosition(tickDelta).z());
        } else
            linkedToPosition = linkedTo.getRopeHoldPosition(tickDelta);

        Vec3 thisPosition = getEllipseIntersect(thisBoat, linkedToPosition, tickDelta)
                .add(0.0, thisBoat.getEyeHeight() / 2.0, 0.0);
        double xLerped = Mth.lerp(tickDelta, thisBoat.xo, thisBoat.getX()) + thisPosition.x();
        double yLerped = Mth.lerp(tickDelta, thisBoat.yo, thisBoat.getY()) + thisPosition.y();
        double zLerped = Mth.lerp(tickDelta, thisBoat.zo, thisBoat.getZ()) + thisPosition.z();
        poseStack.translate(thisPosition.x(), thisPosition.y, thisPosition.z());
        float xDisplacement = (float)(linkedToPosition.x - xLerped);
        float yDisplacement = (float)(linkedToPosition.y - yLerped);
        float zDisplacement = (float)(linkedToPosition.z - zLerped);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
        Matrix4f matrix = poseStack.last().pose();
        float crossModifier = Mth.invSqrt(xDisplacement * xDisplacement + zDisplacement * zDisplacement) * 0.025F / 2.0F;
        float crossXDirection = zDisplacement * crossModifier;
        float crossZDirection = xDisplacement * crossModifier;
        BlockPos thisPos = BlockPos.containing(thisBoat.getEyePosition(tickDelta));
        BlockPos linkedPos = BlockPos.containing(linkedTo.getEyePosition(tickDelta));
        int thisBoatBlockLight = thisBoat.isOnFire() ? 15 : thisBoat.level().getBrightness(LightLayer.BLOCK, thisPos);
        int otherBoatBlockLight = linkedTo.isOnFire() ? 15 : linkedTo.level().getBrightness(LightLayer.BLOCK, linkedPos);
        int thisBoatSkyLight = thisBoat.level().getBrightness(LightLayer.SKY, thisPos);
        int otherBoatSkyLight = thisBoat.level().getBrightness(LightLayer.SKY, linkedPos);

        for(int pieceIndex = 0; pieceIndex <= 24; ++pieceIndex) {
            addBoatHookVertexPair(vertexConsumer, matrix, xDisplacement, yDisplacement, zDisplacement, thisBoatBlockLight, otherBoatBlockLight, thisBoatSkyLight, otherBoatSkyLight, 0.025F, 0.025F, crossXDirection, crossZDirection, pieceIndex, false);
        }

        for(int pieceIndex = 24; pieceIndex >= 0; --pieceIndex) {
            addBoatHookVertexPair(vertexConsumer, matrix, xDisplacement, yDisplacement, zDisplacement, thisBoatBlockLight, otherBoatBlockLight, thisBoatSkyLight, otherBoatSkyLight, 0.025F, 0.0F, crossXDirection, crossZDirection, pieceIndex, true);
        }

        poseStack.popPose();
    }

    private static Vec3 getEllipseIntersect(Entity thisBoat, Vec3 linkedToPos, float tickDelta) {
        Vec3 rot = thisBoat.calculateViewVector(0.0F, thisBoat.getViewYRot(tickDelta));
        Vec3 pos = linkedToPos.subtract(thisBoat.getPosition(tickDelta)).normalize().multiply(1.0, 0.0, 1.0);
        double width = thisBoat.getBbWidth() / 2.2;
        double length = thisBoat.getBbWidth() / 1.5;

        double inverseWidth = 1 / width;
        double inverseDiff = 1 / length - inverseWidth;

        Vec3 transformedPos = pos.scale(inverseWidth).add(rot.scale(inverseDiff * rot.dot(pos)));;
        double t = 1 / transformedPos.length();

        return pos.scale(t);
    }

    private static void addBoatHookVertexPair(VertexConsumer vertexConsumer, Matrix4f matrix4f, float xDisplacement, float yDisplacement, float zDisplacement, int leashedEntityBlockLight, int holdingEntityBlockLight, int leashedEntitySkyLight, int holdingEntitySkyLight, float leashYOffset, float crossYDirection, float crossXDirection, float crossZDirection, int pieceIndex, boolean swapDarkDirection) {
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
