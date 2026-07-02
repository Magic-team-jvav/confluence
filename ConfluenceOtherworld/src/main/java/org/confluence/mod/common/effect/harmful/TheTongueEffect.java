package org.confluence.mod.common.effect.harmful;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.boss.WallOfFlesh;
import org.confluence.mod.common.init.ModEffects;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

public class TheTongueEffect extends PortMobEffect {
    private static final int NETHER_CEILING = 128;
    private WallOfFlesh wall;

    public TheTongueEffect() {
        super(MobEffectCategory.HARMFUL, 0xAB1122);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        if (wall == null || !wall.isAlive()) return;

        Vec3 wallPos = wall.position();
        double distanceToWall = living.position().distanceTo(wallPos);

        // If too far, kill
        if (distanceToWall > 1000.0) {
            living.kill();
            return;
        }

        // Pull target: 45 blocks ahead of the wall (toward east = away from wall's movement)
        Vec3 targetPos = wallPos.add(new Vec3(45, 0, 0));
        if (living.level().dimension() == Level.NETHER
                && living.getY() < NETHER_CEILING
                && targetPos.y >= NETHER_CEILING) {
            targetPos = new Vec3(targetPos.x, targetPos.y - 15.0, targetPos.z);
        }

        if (!living.level().isClientSide) {
            Vec3 toTarget = targetPos.subtract(living.position());
            double distance = toTarget.length();
            if (distance < 0.01) return;
            Vec3 dragDir = toTarget.normalize();

            double speed = Math.abs(wall.getDeltaMovement().x) + 0.1;
            double factor = Mth.clamp(distance / 15.0 + speed + 0.35, speed + 0.15, speed + 0.5);
            Vec3 adjustedForce = dragDir.scale(factor);

            if (distance <= 9.0) {
                living.removeEffect(ModEffects.THE_TONGUE.get());
            } else {
                living.setDeltaMovement(living.getDeltaMovement().add(adjustedForce));
                living.hurtMarked = true;
                if (living.tickCount % 10 == 0) {
                    living.hurt(living.level().damageSources().mobAttack(wall), 2);
                }
            }
        }
    }

    @Override
    public void onEffectStarted(LivingEntity living, int amplifier) {
        super.onEffectStarted(living, amplifier);
        if (wall == null || !wall.isAlive()) {
            living.hurt(living.level().damageSources().magic(), 4);
        } else {
            living.hurt(living.level().damageSources().mobAttack(wall), 4);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    public WallOfFlesh getWall() {
        return wall;
    }

    public void setWall(WallOfFlesh wall) {
        this.wall = wall;
    }
}
