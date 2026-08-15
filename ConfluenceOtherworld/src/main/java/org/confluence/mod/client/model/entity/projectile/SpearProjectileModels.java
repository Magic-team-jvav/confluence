package org.confluence.mod.client.model.entity.projectile;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.confluence.mod.Confluence;

/**
 * 长矛衍生弹幕的纯客户端模型声明。
 *
 * <p>模型层和网格类型不得出现在公共实体类的字段或方法签名中，否则 Forge 专用服务端的
 * DistCleaner 会在加载实体类时拒绝客户端类型。这里集中保存渲染元数据，公共实体只保留运动、
 * 战斗状态和通用纹理标识。</p>
 */
public final class SpearProjectileModels {
    public static final ModelLayerLocation STORM = new ModelLayerLocation(
            Confluence.asResource("storm_spear_shot_projectile"), "main");
    public static final ModelLayerLocation NORTH_POLE = new ModelLayerLocation(
            Confluence.asResource("north_pole_projectile"), "main");
    public static final ModelLayerLocation MUSHROOM = new ModelLayerLocation(
            Confluence.asResource("mushroom_projectile"), "main");
    public static final ModelLayerLocation GHASTLY = new ModelLayerLocation(
            Confluence.asResource("ghastly_projectile"), "main");

    private SpearProjectileModels() {}

    /**
     * 风暴长矛与北极矛共用的十字矛头网格。
     */
    public static LayerDefinition createSpearLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition bone = root.addOrReplaceChild("bone", CubeListBuilder.create()
                .texOffs(12, 10).addBox(
                        -1.0F, -1.0F, -8.0F, 2.0F, 2.0F, 4.0F,
                        new CubeDeformation(-0.01F))
                .texOffs(0, 16).addBox(
                        -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, 4.0F,
                        new CubeDeformation(-0.01F)), PartPose.ZERO);
        bone.addOrReplaceChild("cube_ml_r1", CubeListBuilder.create()
                        .texOffs(0, 10).addBox(
                                -1.0F, 2.0659F, -0.5303F,
                                2.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                        .texOffs(12, 16).addBox(
                                -1.0F, -4.1213F, -3.5355F,
                                2.0F, 2.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        bone.addOrReplaceChild("cube_mm_r1", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(
                                -1.0F, -0.5858F, -4.0F,
                                2.0F, 2.0F, 8.0F, new CubeDeformation(-0.01F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    /**
     * 蘑菇弹幕的扁盘菌盖与短菌柄网格。
     */
    public static LayerDefinition createMushroomLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("cap", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 1.0F), PartPose.ZERO);
        root.addOrReplaceChild("stem", CubeListBuilder.create()
                .texOffs(0, 8).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 2.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 32, 32);
    }

    /**
     * 恶魂弹幕的半透明方块体网格。
     */
    public static LayerDefinition createGhastlyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 32, 32);
    }
}
