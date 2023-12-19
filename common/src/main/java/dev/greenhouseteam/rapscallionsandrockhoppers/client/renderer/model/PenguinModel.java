package dev.greenhouseteam.rapscallionsandrockhoppers.client.renderer.model;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
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

public class PenguinModel extends AgeableHierarchicalModel<Penguin> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "penguin"), "main");

	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart brows;

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
		if (penguin.isShocked()) {
			this.brows.setPos(0.0F, -0.75F, 0.0F);
		} else {
			this.brows.setPos(0.0F, 0.0F, 0.0F);
		}

		this.animate(penguin.idleAnimationState, PenguinAnimation.IDLE, delta, 0.5F);
		this.animateWaddle(penguin, delta, limbSwing, limbSwingAmount, penguin.isShocked() ? 1.25F : 1.0F);
		this.animate(penguin.shockArmAnimationState, PenguinAnimation.SHOCK_ARMS, delta, 1.25F);
	}

	protected void animateWaddle(Penguin penguin, float delta, float limbSwing, float limbSwingAmount, float animationSpeed) {
		this.animate(penguin.waddleAnimationState, PenguinAnimation.WADDLE_BODY, delta, animationSpeed);
		this.animate(penguin.waddleExpandAnimationState, PenguinAnimation.WADDLE_ARMS_EXTEND, delta, animationSpeed);
		this.animate(penguin.waddleRetractAnimationState, PenguinAnimation.WADDLE_ARMS_RETRACT, delta, animationSpeed);
		this.animateWalk(PenguinAnimation.WADDLE_FEET, limbSwing, limbSwingAmount, 4.5F, 40.0F);
	}
}