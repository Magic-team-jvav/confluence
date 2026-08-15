package org.confluence.mod.common.effect.harmful;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.boss.WallOfFlesh;
import org.confluence.mod.common.init.ModEffects;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

/**
 * 将逃离追逐区域的参战者拉回血肉墙前方。
 *
 * <p>效果每次都从受影响实体保存的 UUID 解析血肉墙，不缓存可被其他战斗覆盖的全局引用。
 * 拉回点使用墙体的实际朝向计算，因此四个水平推进方向具有完全一致的行为。</p>
 */
public class TheTongueEffect extends PortMobEffect {
    private static final double EXECUTION_DISTANCE = 1000.0;
    private static final double RELEASE_DISTANCE = 9.0;

    public TheTongueEffect() {
        super(MobEffectCategory.HARMFUL, 0xAB1122);
    }

    @Override
    public void applyEffectTick(
            LivingEntity living, int amplifier) {
        if (living.level().isClientSide) {
            return;
        }
        WallOfFlesh wall = HorrifiedEffect.resolve(living);
        if (wall == null) {
            living.removeEffect(ModEffects.THE_TONGUE.get());
            return;
        }

        Vec3 targetPosition = wall.position()
                .add(wall.getForwardVector().scale(45.0))
                .add(0.0, wall.getBbHeight() * 0.5, 0.0);
        double distanceToWall =
                living.position().distanceTo(wall.position());
        if (distanceToWall > EXECUTION_DISTANCE) {
            living.kill();
            return;
        }
        Vec3 toTarget =
                targetPosition.subtract(living.position());
        double distance = toTarget.length();
        if (distance <= RELEASE_DISTANCE) {
            living.removeEffect(ModEffects.THE_TONGUE.get());
            return;
        }

        double wallSpeed =
                wall.getDeltaMovement().horizontalDistance();
        double strength = Mth.clamp(
                distance / 15.0 + wallSpeed + 0.35,
                wallSpeed + 0.15,
                wallSpeed + 0.5);
        living.setDeltaMovement(
                living.getDeltaMovement()
                        .add(toTarget.normalize().scale(strength)));
        living.hurtMarked = true;
        if (living.tickCount % 10 == 0) {
            living.hurt(
                    living.damageSources().mobAttack(wall), 2.0F);
        }
    }

    @Override
    public void onEffectStarted(
            LivingEntity living, int amplifier) {
        super.onEffectStarted(living, amplifier);
        WallOfFlesh wall = HorrifiedEffect.resolve(living);
        if (wall == null) {
            living.hurt(living.damageSources().magic(), 4.0F);
        } else {
            living.hurt(
                    living.damageSources().mobAttack(wall), 4.0F);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(
            int duration, int amplifier) {
        return true;
    }
}
