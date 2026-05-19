package org.confluence.mod.common.entity.projectile.spear;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.entitiy.IAxisZRotate;
import org.confluence.mod.Confluence;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.network.EmitterCreationPacketS2C;

/**
 * <h1>风暴长矛弹射物</h1>
 * <p>
 * 直线加速弹射物，单次命中后销毁。
 * 移除时播放闪电粒子特效。
 */
public class StormSpearProjectile extends SpearProjectile {
    /** 风暴长矛额外伤害倍率 */
    private static final float STORM_DAMAGE_MULTIPLIER = 1.6f;

    /** 模型层定义位置 */
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Confluence.asResource("storm_spear_shot_projectile"), "main");

    /** 模型网格定义 */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create()
                .texOffs(12, 10).addBox(-1.0F, -1.0F, -8.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.01F))
                .texOffs(0, 16).addBox(-1.0F, -1.0F, 4.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.ZERO);
        bone.addOrReplaceChild("cube_ml_r1", CubeListBuilder.create()
                .texOffs(0, 10).addBox(-1.0F, 2.0659F, -0.5303F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(12, 16).addBox(-1.0F, -4.1213F, -3.5355F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        bone.addOrReplaceChild("cube_mm_r1", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.0F, -0.5858F, -4.0F, 2.0F, 2.0F, 8.0F, new CubeDeformation(-0.01F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public StormSpearProjectile(EntityType<? extends StormSpearProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void updateMotion() {
        if (projComponent != null) {
            velocity = velocity.scale(projComponent.acceleration());
        }
    }

    @Override
    protected Vec3 initVelocity(LivingEntity owner, Vec3 direction, float speed) {
        return direction.scale(1.92f);
    }

    /**
     * 风暴长矛额外 1.6 倍伤害，叠加上基类的组件伤害系数。
     */
    @Override
    protected float getDamage() {
        return super.getDamage() * STORM_DAMAGE_MULTIPLIER;
    }

    @Override
    public void tick() {
        super.tick();
    }

    // ===== 渲染元数据 =====

    @Override
    public net.minecraft.resources.ResourceLocation getProjTexture() {
        return Confluence.asResource("textures/entity/storm_spear_shot_projectile.png");
    }

    @Override
    public net.minecraft.client.model.geom.ModelLayerLocation getModelLayer() {
        return LAYER_LOCATION;
    }


    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        if (!level().isClientSide) {
            EmitterCreationPacketS2C.sendToAll(
                    Confluence.asResource("thunder_zapper_expiration"),
                    position().toVector3f(),
                    MolangExp.EMPTY,
                    null);
        }
    }
}
