package org.confluence.mod.common.item.bow;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModEffects;

/// 极昼：射出的箭矢会对途径的敌人造成范围伤害，被箭矢击中的敌人则会持续受到更高的伤害。
///
/// 途经范围伤害在箭矢命中方块（落空）与命中敌怪时都会结算一次；
/// 命中敌怪还会附加「极昼灼烧」，在持续时间里不断掉血。
public class PolarDayBowItem extends BaseTerraBowItem {
    /// 途经范围伤害半径。
    private static final double SPLASH_RADIUS = 2.5;
    /// 途经范围伤害相对满蓄力伤害的倍率。
    private static final float SPLASH_FACTOR = 0.5F;
    /// 命中附加的极昼灼烧时长（tick）。
    private static final int MARK_DURATION = 120;

    /// 无耐久：不掉耐久，也不需要修复。
    public PolarDayBowItem(float baseDamage) {
        super(baseDamage, new Item.Properties());
    }

    @Override
    public void modifyArrowEntity(BaseArrowEntity entity) {
        entity.addWeaponHitEffect((owner, target, fullPull) -> {
            target.addEffect(new MobEffectInstance(ModEffects.POLAR_MARK.get(), MARK_DURATION, 0), owner);
            splash(entity, owner, target.position());
        });
        entity.addMissCallback(() -> splash(entity, entity.getOwner() instanceof LivingEntity living ? living : null, entity.position()));
    }

    /// 对指定位置周围结算一次范围伤害。
    private void splash(BaseArrowEntity arrow, LivingEntity owner, Vec3 center) {
        Level level = arrow.level();
        if (level.isClientSide) return;
        float damage = getFullDrawDamage() * SPLASH_FACTOR;
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(SPLASH_RADIUS))) {
            if (victim == owner || !victim.isAlive() || victim.isSpectator()) continue;
            if (owner != null && !LibEntityUtils.canHit(owner, victim)) continue;
            victim.invulnerableTime = 0;
            victim.hurt(level.damageSources().indirectMagic(arrow, owner), damage);
        }
    }
}
