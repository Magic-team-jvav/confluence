package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.common.init.ModEffects;

/// 服装商用于自卫的暗影焰骷髅。
public final class NPCShadowflameSkullProjectile extends SkullProjectile {
    /// 创建使用骷髅模型和追踪逻辑的服装商专用弹体。
    public NPCShadowflameSkullProjectile(EntityType<? extends NPCShadowflameSkullProjectile> type, Level level) {
        super(type, level);
    }

    /// 命中成功后施加暗影焰，并保留骷髅弹体原有的三次穿透上限。
    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        if (doPenetrateCheck(target)) {
            if (doHurtAndKnockback(target, 0.35, 0.1) && target instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(ModEffects.SHADOWFLAME.get(), 120));
            }
            doDiscardInMaxPenetrate(3);
        }
    }
}
