package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// 黄蜂发射的毒刺弹幕。
///
/// 毒刺复用敌对生物直线弹幕的碰撞与阵营判断，并在合法命中时附加五秒中毒。
/// 效果只由服务端命中结算产生，客户端不自行预测状态效果。
public final class HornetStingerProjectile extends StraightMonsterProjectile {
    private static final int POISON_DURATION = 100;
    private int poisonAmplifier;

    public HornetStingerProjectile(EntityType<? extends HornetStingerProjectile> type, Level level) {
        super(type, level);
    }

    public void configure(Mob owner, LivingEntity target, float damage) {
        configure(owner, target, damage, 0);
    }

    /// 配置毒刺的命中伤害与中毒等级。
    ///
    /// 弹道、阵营和生命周期仍由直线怪物弹幕基类统一处理；中毒等级由发射者在生成时明确
    /// 传入，便于蜂王愤怒状态和普通黄蜂复用同一种弹幕实体。
    public void configure(Mob owner, LivingEntity target, float damage, int poisonAmplifier) {
        configure(owner, owner.position(), target.getEyePosition().subtract(owner.position()), damage, 5.0F, poisonAmplifier);
    }

    /// 使用调用方明确提供的出生点和瞄准向量生成毒刺。
    /// 蜂王瞄准目标眼睛，普通黄蜂瞄准目标身体中部；两种 1.21 行为不能被同一个
    /// 通用“从发射者眼睛射击”入口合并，否则出生高度和弹道都会发生偏移。
    public void configure(Mob owner, Vec3 origin, Vec3 aim, float damage, float inaccuracy, int poisonAmplifier) {
        configureAimed(owner, origin, aim, damage, 1.0F, inaccuracy, 100);
        this.poisonAmplifier = Math.max(0, poisonAmplifier);
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION, poisonAmplifier), owner);
    }
}
