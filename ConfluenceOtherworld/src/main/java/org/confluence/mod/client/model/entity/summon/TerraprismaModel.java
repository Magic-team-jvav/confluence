package org.confluence.mod.client.model.entity.summon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.confluence.mod.Confluence;

/// 泰拉棱镜的长剑模型。
/// 模型原点放在护手附近，待命姿态和攻击轨迹都以这个锚点计算，避免剑刃长度变化后重新调整跟随算法。
public final class TerraprismaModel {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Confluence.asResource("terraprisma"), "main");

    private final ModelPart main;

    public TerraprismaModel(ModelPart root) {
        this.main = root.getChild("main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition main = root.addOrReplaceChild("main",
                CubeListBuilder.create()
                        .texOffs(0, 7)
                        .addBox(-1.0F, -3.0F, 1.0F,
                                2.0F, 6.0F, 2.0F,
                                new CubeDeformation(0.0F))
                        .texOffs(0, 0)
                        .addBox(-0.5F, -2.0F, -15.0F,
                                1.0F, 4.0F, 16.0F,
                                new CubeDeformation(0.0F))
                        .texOffs(0, 0)
                        .addBox(-1.0F, -1.0F, 3.0F,
                                2.0F, 2.0F, 5.0F,
                                new CubeDeformation(0.0F)),
                PartPose.ZERO);

        main.addOrReplaceChild("tip",
                CubeListBuilder.create()
                        .texOffs(8, 7)
                        .addBox(-0.5F, -2.0F, -1.0F,
                                1.0F, 3.0F, 3.0F,
                                new CubeDeformation(0.01F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, -15.75F,
                        -0.7854F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer,
                               int packedLight, int packedOverlay,
                               float red, float green, float blue,
                               float alpha) {
        main.render(poseStack, consumer, packedLight, packedOverlay,
                red, green, blue, alpha);
    }
}
