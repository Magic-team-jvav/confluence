package org.confluence.mod.common.entity.projectile.spear;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
    /**
     * 风暴长矛额外伤害倍率
     */
    private static final float STORM_DAMAGE_MULTIPLIER = 1.6f;

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
        // 新事务的基础伤害已经包含武器声明倍率；旧生成路径仍保留风暴长矛的 1.6 倍特性。
        return getProjectileCombatSnapshot() == null
                ? super.getDamage() * STORM_DAMAGE_MULTIPLIER
                : super.getDamage();
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
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (!level().isClientSide) {
            EmitterCreationPacketS2C.sendToAll(
                    Confluence.asResource("thunder_zapper_expiration"),
                    position().toVector3f(),
                    MolangExp.EMPTY,
                    null);
        }
    }
}
