package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.mana.AbstractManaProjectile;
import org.confluence.mod.common.init.ModEffects;

/// 舌锋剑命中敌怪后射出的灵液流。
///
/// 外观复用黄金雨的粒子风暴配置，但伤害走近战（剑）弹幕，不吃魔法加成。
public class IchorStreamProjectile extends AbstractManaProjectile {
    /// 基础伤害比例：武器基础伤害的 50%。
    public static final float DAMAGE_FACTOR = 0.5F;
    /// 击退为常规剑气击退的 70%。
    public static final double KNOCKBACK = 2.45;
    /// 最多穿透 4 个敌怪。
    public static final int MAX_PENETRATE = 4;
    /// 物块上最多反弹 4 次，第 5 次击中物块时消失。
    public static final int MAX_BOUNCE = 4;
    /// 约 2 秒后消失。
    public static final int MAX_AGE = 40;

    public IchorStreamProjectile(EntityType<IchorStreamProjectile> entityType, Level level) {
        super(entityType, level);
        withParticle(Confluence.asResource("golden_shower"));
    }

    @Override
    public DamageSource getDamageSource() {
        return LibDamageTypes.of(level(), LibDamageTypes.SWORD_PROJECTILE, this, getOwner());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        doAgeCheck(MAX_AGE);
        doBouncyMove(false, this::onBounce, motion -> motion.scale(0.92));
    }

    private void onBounce() {
        doCollisionCheck(MAX_BOUNCE);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (!doPenetrateCheck(entity)) return;
        doHurtAndKnockback(entity, KNOCKBACK, 0.2);
        LivingEntity living = LibEntityUtils.effectTarget(entity);
        if (living != null) {
            /// 已存在灵液时再次赋予需要重置时长，因此先移除再添加。
            living.removeEffect(ModEffects.ICHOR.get());
            living.addEffect(new MobEffectInstance(ModEffects.ICHOR.get(), 60 + living.getRandom().nextInt(41)));
        }
        doDiscardInMaxPenetrate(MAX_PENETRATE);
    }
}
