package org.confluence.mod.client.model.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.TitaniumShardsProjectile;

public class TitaniumShardsProjectileModel extends EntityModel<TitaniumShardsProjectile> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("titanium_shards_projectile"), "main");
    private final ModelPart bb_main;

    public TitaniumShardsProjectileModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(22, 20).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 16.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(0.0F, -12.0F, -5.5F, 0.0F, 24.0F, 11.0F, CubeDeformation.NONE), PartPose.ZERO);

        bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(22, 0).addBox(-0.5F, -5.0F, -5.0F, 1.0F, 10.0F, 10.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(TitaniumShardsProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bb_main.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
