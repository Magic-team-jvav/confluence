package org.confluence.mod.client.entity.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.Confluence;

/// 普通史莱姆的原版形状模型。
///
/// <p>泰拉瑞亚史莱姆纹理沿用 Minecraft 史莱姆的内外层布局，因此普通史莱姆应当使用
/// 方形内核、眼睛和半透明外壳。尖刺史莱姆拥有独立骨骼模型，不应复用到这里。</p>
public final class BaseSlimeModel<T extends LivingEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation INNER_LAYER = new ModelLayerLocation(
            Confluence.asResource("base_slime"), "inner");
    public static final ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(
            Confluence.asResource("base_slime"), "outer");
    private final ModelPart root;

    public BaseSlimeModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createInnerBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("cube",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-3.0F, 17.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("right_eye",
                CubeListBuilder.create().texOffs(32, 0)
                        .addBox(-3.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("left_eye",
                CubeListBuilder.create().texOffs(32, 4)
                        .addBox(1.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("mouth",
                CubeListBuilder.create().texOffs(32, 8)
                        .addBox(0.0F, 21.0F, -3.5F, 1.0F, 1.0F, 1.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition createOuterBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("cube",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        // 史莱姆的挤压由渲染器统一施加，模型骨骼本身无需逐帧旋转。
    }
}
