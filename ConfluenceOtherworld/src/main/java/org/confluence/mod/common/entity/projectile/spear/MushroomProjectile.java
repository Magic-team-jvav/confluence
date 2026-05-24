package org.confluence.mod.common.entity.projectile.spear;

import org.confluence.mod.common.component.SpearProjectileComponent;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

/**
 * <h1>蘑菇弹射物</h1>
 * 自旋悬浮弹射物，可穿墙，单次命中后销毁，无击退。
 * 生命周期 30 tick（约 1.5 秒）。
 */
public class MushroomProjectile extends SpearProjectile {
    /** 蘑菇弹射物生命周期（tick） */
    private static final int MUSHROOM_LIFETIME = 20;

    /** 模型层定义 */
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Confluence.asResource("mushroom_projectile"), "main");

    /** 蘑菇弹射物网格：扁盘形菌盖 + 细短菌柄 */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("cap", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 1.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("stem", CubeListBuilder.create()
                .texOffs(0, 8).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 2.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public MushroomProjectile(EntityType<? extends MushroomProjectile> entityType, Level level) {
        super(entityType, level);
        this.lifetime = MUSHROOM_LIFETIME;
        this.knockBack = 0.0f;
        this.baseKnockBack = 0.0f;
    }

    @Override
    public void setProjComponent(SpearProjectileComponent projComponent, LivingEntity owner) {
        super.setProjComponent(projComponent, owner);
        this.lifetime = MUSHROOM_LIFETIME;
        this.knockBack = 0.0f;
        this.baseKnockBack = 0.0f;
    }

    @Override
    protected void updateMotion() {
        // 仅依赖组件加速度衰减产生悬浮减速效果
    }

    @Override
    public com.mojang.math.Axis getSpinAxis() {
        return com.mojang.math.Axis.XP;
    }

    @Override
    protected Vec3 initVelocity(LivingEntity owner, Vec3 direction, float speed) {
        return direction.scale(speed);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            rotate.neo += 2;
            if (rotate.neo > Mth.TWO_PI) {
                rotate.neo -= Mth.TWO_PI;
            }
        }
    }

    /** 可穿墙 — 不销毁 */
    @Override
    protected void onHitBlock(BlockHitResult result) {
    }

    /** 无视所有伤害 */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    // ===== 渲染元数据 =====

    @Override
    public net.minecraft.resources.ResourceLocation getProjTexture() {
        return Confluence.asResource("textures/entity/mushroom_projectile.png");
    }

    @Override
    public ModelLayerLocation getModelLayer() {
        return LAYER_LOCATION;
    }

    @Override
    @Nullable
    protected net.minecraft.core.particles.ParticleOptions getTrailParticle() {
        return null;
    }
}
