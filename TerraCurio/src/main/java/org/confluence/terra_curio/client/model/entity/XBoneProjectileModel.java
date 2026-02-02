package org.confluence.terra_curio.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.entity.XBoneProjectile;

public class XBoneProjectileModel extends EntityModel<XBoneProjectile> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TerraCurio.asResource("x_bone"), "main");
    private final ModelPart root;

    public XBoneProjectileModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone2 = partdefinition.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -5.5F, -1.0F, 1.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(6, 5).addBox(-0.5F, 5.5F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(6, 0).addBox(-0.5F, -6.5F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.016F, 8.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r1 = bone2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(14, 3).addBox(-0.995F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(0, 13).addBox(-0.7071F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(0, 13).addBox(-0.4192F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F)), PartPose.offsetAndRotation(0.2071F, -4.5858F, 0.0858F, 0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r2 = bone2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(12, 10).addBox(-0.005F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(12, 10).addBox(0.2829F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(6, 10).addBox(-0.2929F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F)), PartPose.offsetAndRotation(-0.495F, -4.5858F, -0.0858F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r3 = bone2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(18, 0).addBox(-0.005F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(0, 18).addBox(-0.2929F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(18, 0).addBox(0.2829F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F)), PartPose.offsetAndRotation(-0.495F, 4.5858F, 0.0858F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r4 = bone2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(6, 15).addBox(-0.005F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(12, 15).addBox(-0.2929F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(6, 15).addBox(0.2829F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F)), PartPose.offsetAndRotation(-0.495F, 4.5858F, -0.0858F, 0.7854F, 0.0F, 0.0F));

        PartDefinition bone3 = partdefinition.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -5.5F, -1.0F, 1.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(6, 5).addBox(-0.5F, 5.5F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(6, 0).addBox(-0.5F, -6.5F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.016F, 8.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r5 = bone3.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(14, 3).addBox(-0.995F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(0, 13).addBox(-0.7071F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(0, 13).addBox(-0.4192F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F)), PartPose.offsetAndRotation(0.2071F, -4.5858F, 0.0858F, 0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r6 = bone3.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(12, 10).addBox(-0.005F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(12, 10).addBox(0.2829F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(6, 10).addBox(-0.2929F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F)), PartPose.offsetAndRotation(-0.495F, -4.5858F, -0.0858F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r7 = bone3.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(18, 0).addBox(-0.005F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(0, 18).addBox(-0.2929F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(18, 0).addBox(0.2829F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F)), PartPose.offsetAndRotation(-0.495F, 4.5858F, 0.0858F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r8 = bone3.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(6, 15).addBox(-0.005F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(12, 15).addBox(-0.2929F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F))
                .texOffs(6, 15).addBox(0.2829F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.2929F)), PartPose.offsetAndRotation(-0.495F, 4.5858F, -0.0858F, 0.7854F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(XBoneProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}