package org.confluence.terraentity.client.entity.model;
// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import org.confluence.terraentity.TerraEntity;

public class JungleSpikedProjectlieModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TerraEntity.space("jungle_spiked_projectlie"), "main");
	private final ModelPart all;
	private final ModelPart bone2;

	public JungleSpikedProjectlieModel(ModelPart root) {
		this.all = root.getChild("all");
		this.bone2 = this.all.getChild("bone2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5651F, 0.3913F, -0.032F));

		PartDefinition bone2 = all.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 0).addBox(-2.75F, -1.5013F, -1.0F, 7.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8855F, -1.2756F, 0.0F, 0.0F, 0.0F, -1.1781F));

		PartDefinition cube_r1 = bone2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.125F, -1.0F, -1.0F, 2.125F, 2.125F, 2.0F, new CubeDeformation(0.002F)), PartPose.offsetAndRotation(4.25F, -0.0871F, 0.0F, 0.0F, 0.0F, 0.7854F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		all.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}
