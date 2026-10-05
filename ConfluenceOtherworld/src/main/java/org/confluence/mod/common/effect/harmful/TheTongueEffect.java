package org.confluence.mod.common.effect.harmful;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.boss.WallOfFlesh;
import org.confluence.mod.common.entity.boss.WallOfFleshMouth;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.util.OverworldUtils;

public class TheTongueEffect extends MobEffect {
    private static final double EXECUTION_DISTANCE = 1000.0;
    private static final double RELEASE_DISTANCE = 9.0;
    private static final double NETHER_GENERATION_HEIGHT = 128.0;

    public TheTongueEffect() {
        super(MobEffectCategory.HARMFUL, 0xAB1122);
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        if (living.level().isClientSide) {
            return true;
        }
        WallOfFlesh wall = HorrifiedEffect.resolve(living);
        if (wall == null) {
            living.removeEffect(ModEffects.THE_TONGUE);
            return true;
        }
        WallOfFleshMouth mouth = wall.findTongueMouth(living);
        if (mouth == null) {
            living.removeEffect(ModEffects.THE_TONGUE);
            return true;
        }

        Vec3 targetPosition = mouth.position().add(wall.getForwardVector().scale(45.0));
        if (living.level().dimension() == OverworldUtils.underworld() && living.getY() < NETHER_GENERATION_HEIGHT && targetPosition.y >= NETHER_GENERATION_HEIGHT) {
            targetPosition = targetPosition.add(0.0, -15.0, 0.0);
        }
        if (living.position().distanceTo(mouth.position()) > EXECUTION_DISTANCE) {
            living.kill();
            return true;
        }
        Vec3 toTarget = targetPosition.subtract(living.position());
        double distance = toTarget.length();
        if (distance <= RELEASE_DISTANCE) {
            living.removeEffect(ModEffects.THE_TONGUE);
            return true;
        }

        double wallSpeed = wall.getAttributeValue(Attributes.MOVEMENT_SPEED);
        double strength = Mth.clamp(distance / 15.0 + wallSpeed + 0.35, wallSpeed + 0.15, wallSpeed + 0.5);
        Vec3 next = living.position().add(toTarget.normalize().scale(Math.min(distance, strength)));
        /// 狂卷之舌本就需要无视方块；普通速度会在墙面碰撞后把玩家卡在墙后。
        if (living instanceof ServerPlayer player) {
            player.connection.teleport(next.x, next.y, next.z, player.getYRot(), player.getXRot());
        } else {
            living.setPos(next);
        }
        living.setDeltaMovement(Vec3.ZERO);
        living.fallDistance = 0.0F;
        living.hurtMarked = true;
        if (living.tickCount % 10 == 0) {
            living.hurt(living.damageSources().mobAttack(wall), 2.0F);
        }
        return true;
    }

    @Override
    public void onEffectStarted(LivingEntity living, int amplifier) {
        super.onEffectStarted(living, amplifier);
        WallOfFlesh wall = HorrifiedEffect.resolve(living);
        if (wall == null) {
            living.hurt(living.damageSources().magic(), 4.0F);
        } else {
            living.hurt(living.damageSources().mobAttack(wall), 4.0F);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
