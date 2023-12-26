package dev.greenhouseteam.rapscallionsandrockhoppers.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.mixin.client.HierarchicalModelAccessor;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;

public class PenguinModel extends AgeableHierarchicalModel<Penguin> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "penguin"), "main");

	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart brows;

	private long previousStumbleTime = Long.MIN_VALUE;

	public PenguinModel(ModelPart root) {
		super(0.5F, 24.0F);
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.head = this.body.getChild("head");
		this.brows = this.head.getChild("brows");
	}

	public static LayerDefinition createLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 17.5F, 0.5F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 14).addBox(-4.5F, -5.5F, -3.5F, 9.0F, 11.0F, 7.0F, new CubeDeformation(-0.001F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.0F, -3.5F, 7.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-0.5F, 0.0F, -5.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.5F, 0.0F));

		PartDefinition brows = head.addOrReplaceChild("brows", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		brows.addOrReplaceChild("leftBrow", CubeListBuilder.create().texOffs(28, 0).addBox(-1.5F, -1.5F, -2.5F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -2.5F, -1.25F));
		brows.addOrReplaceChild("rightBrow", CubeListBuilder.create().texOffs(28, 0).mirror().addBox(-1.5F, -1.5F, -2.5F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.0F, -2.5F, -1.25F));

		body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(25, 15).addBox(-2.5F, -0.75F, -0.75F, 5.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.5F, 3.5F, -0.7854F, 0.0F, 0.0F));

		body.addOrReplaceChild("leftFlipper", CubeListBuilder.create().texOffs(32, 20).addBox(0.0F, 0.0F, -1.5F, 1.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, -5.5F, 0.0F));
		body.addOrReplaceChild("rightFlipper", CubeListBuilder.create().texOffs(32, 20).addBox(-1.0F, 0.0F, -1.5F, 1.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, -5.5F, 0.0F));

		root.addOrReplaceChild("leftFoot", CubeListBuilder.create().texOffs(28, 10).addBox(-1.5F, 0.0F, -3.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(32, 8).addBox(-1.5F, 2.0F, -5.0F, 3.0F, 0.001F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 4.5F, 1.5F));
		root.addOrReplaceChild("rightFoot", CubeListBuilder.create().texOffs(28, 10).mirror().addBox(-1.5F, 0.0F, -3.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(32, 8).addBox(-1.5F, 2.0F, -5.0F, 3.0F, 0.001F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 4.5F, 1.5F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(Penguin penguin, float limbSwing, float limbSwingAmount, float delta, float yRot, float xRot) {
		this.root().getAllParts().forEach(ModelPart::resetPose);

		if (this.young) {
			this.head.xScale *= 1.3F;
			this.head.yScale *= 1.3F;
			this.head.zScale *= 1.3F;
		}

		if (penguin.isShocked()) {
			this.brows.setPos(0.0F, -0.75F, 0.0F);
		} else {
			this.brows.setPos(0.0F, 0.0F, 0.0F);
		}

		this.animateWaddle(penguin, delta, limbSwing, limbSwingAmount, penguin.isShocked() ? 1.4F : 1.0F);
		this.animate(penguin.shockArmAnimationState, PenguinAnimation.SHOCK_ARMS, delta, 1.25F);

		this.animateStumble(penguin, delta);
		this.animate(penguin.shoveAnimationState, PenguinAnimation.SHOVE, delta, 1.0F);


		this.moveBodyInWater(penguin, yRot, xRot);
		this.animateSwim(penguin, delta);

		this.moveHead(penguin, yRot, xRot);
	}

	private void animateWaddle(Penguin penguin, float delta, float limbSwing, float limbSwingAmount, float animationSpeed) {
		this.animate(penguin.idleAnimationState, PenguinAnimation.IDLE, delta, animationSpeed * 0.5F);
		this.animate(penguin.waddleAnimationState, PenguinAnimation.WADDLE_BODY, delta, animationSpeed);
		this.animate(penguin.waddleArmEaseInAnimationState, PenguinAnimation.WADDLE_ARMS_EASE_IN, delta, animationSpeed);
		this.animate(penguin.waddleArmEaseOutAnimationState, PenguinAnimation.WADDLE_ARMS_EASE_OUT, delta, animationSpeed);
		this.animateWalk(PenguinAnimation.WADDLE_FEET, limbSwing, limbSwingAmount, 5.0F, 45.0F);
	}

	private void animateStumble(Penguin penguin, float delta) {
		this.animate(penguin.stumbleAnimationState, PenguinAnimation.STUMBLE, delta, 1.0F);
		if (penguin.stumbleFallingAnimationState.isStarted()) {
			if (this.previousStumbleTime == Long.MIN_VALUE) {
				this.previousStumbleTime = penguin.stumbleGroundAnimationState.getAccumulatedTime();
			}
			this.animateAtSpecificFrame(penguin.stumbleGroundAnimationState, PenguinAnimation.STUMBLE_LAND, this.previousStumbleTime, delta, 1.0F);
		} else {
			if (this.previousStumbleTime != Long.MIN_VALUE) {
				this.previousStumbleTime = Long.MIN_VALUE;
			}
			this.animate(penguin.stumbleGroundAnimationState, PenguinAnimation.STUMBLE_LAND, delta, 1.0F);
		}
		this.animate(penguin.stumbleFallingAnimationState, PenguinAnimation.STUMBLE_FALLING, delta, 1.0F);
		this.animate(penguin.stumbleGetUpAnimationState, PenguinAnimation.GET_UP, delta, 1.0F);
	}

	private void scaleBabyHead(Penguin penguin) {
		if (!penguin.isBaby()) return;
		this.head.xScale *= 0.6F;
		this.head.yScale *= 0.6F;
		this.head.zScale *= 0.6F;
	}

	private void animateSwim(Penguin penguin, float delta) {
		this.animate(penguin.swimIdleAnimationState, PenguinAnimation.SWIM_IDLE, delta, 1.0F);
		this.animate(penguin.swimAnimationState, PenguinAnimation.SWIM, delta, 0.25F);
		this.animate(penguin.swimEaseInAnimationState, PenguinAnimation.SWIM_EASE_IN, delta, 1.0F);
		this.animate(penguin.swimEaseOutAnimationState, PenguinAnimation.SWIM_EASE_OUT, delta, 1.0F);
	}

	private void moveHead(Penguin penguin, float yRot, float xRot) {
		if (!canAnimateHead(penguin)) return;
		this.head.xRot = xRot * (float) (Mth.PI / 180.0);
		this.head.yRot = yRot * (float) (Mth.PI / 180.0);
	}

	private void moveBodyInWater(Penguin penguin, float yRot, float xRot) {
		if (!this.canRotateBodyInWater(penguin)) return;
		this.root.xRot = (float) (xRot * Math.PI / 180.0);
		this.root.yRot = (float) (yRot * Math.PI / 180.0);
	}

	private boolean canAnimateHead(Penguin penguin) {
		return !penguin.isStumbling() && !this.canRotateBodyInWater(penguin);
	}
	private boolean canRotateBodyInWater(Penguin penguin) {
		return penguin.swimAnimationState.isStarted();
	}

	private void animateAtSpecificFrame(AnimationState state, AnimationDefinition definition, long time, float delta, float animationSpeed) {
		state.updateTime(delta, animationSpeed);
		state.ifStarted(st -> KeyframeAnimations.animate(this, definition, time, 1.0F, HierarchicalModelAccessor.rapscallionsandrockhoppers$ANIMATION_VECTOR_CACHE()));
	}
}