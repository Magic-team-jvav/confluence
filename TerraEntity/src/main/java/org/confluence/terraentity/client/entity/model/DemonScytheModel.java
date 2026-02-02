package org.confluence.terraentity.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.proj.DemonScytheProj;
import org.jetbrains.annotations.NotNull;

public class DemonScytheModel extends EntityModel<DemonScytheProj> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TerraEntity.space("model"), "main");
	private final ModelPart root;

	public DemonScytheModel(ModelPart root) {
		this.root = root;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(48, 0).addBox(-1.0F, -12.0F, -1.0F, 2.0F, 24.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(0, -24).addBox(0.0F, -12.0F, -12.0F, 0.0F, 24.0F, 24.0F, CubeDeformation.NONE), PartPose.ZERO);

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NotNull DemonScytheProj entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}
