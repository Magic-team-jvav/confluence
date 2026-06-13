package org.confluence.mod.client.model.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;

public class IceBladeSwordProjectileModel extends EntityModel<SwordProjectile> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("ice_blade_sword_projectile"), "main");
    private final ModelPart bb_main;

    public IceBladeSwordProjectileModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -2.0F, -15.0F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(7, 5).addBox(0.0F, 0.0F, 0.0F, 1.0F, 3.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.5F, 0.0F, -13.0F, -0.7854F, 0.0F, 0.0F));
        bb_main.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 8).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, -15.75F, -0.7854F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(SwordProjectile swordProjectile, float v, float v1, float v2, float v3, float v4) {

    }
}
