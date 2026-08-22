package org.confluence.mod.common.entity.projectile.spear;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;

/// <h1>蘑菇弹射物</h1>
/// 自旋悬浮弹射物，可穿墙，单次命中后销毁，无击退。
public class MushroomProjectile extends SpearProjectile {

    public MushroomProjectile(EntityType<? extends MushroomProjectile> entityType, Level level) {
        super(entityType, level);
        this.knockBack = 0.0f;
        this.baseKnockBack = 0.0f;
    }

    @Override
    protected void updateMotion() {
        // 仅依赖组件加速度衰减产生悬浮减速效果
    }

    //蘑菇孢子是x轴旋转
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

    /// 可穿墙 — 不销毁
    @Override
    protected void onHitBlock(BlockHitResult result) {
    }

    /// 无视所有伤害
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    // ===== 渲染元数据 =====

    @Override
    public net.minecraft.resources.ResourceLocation getProjTexture() {
        return Confluence.asResource("textures/entity/mushroom_projectile.png");
    }

}
