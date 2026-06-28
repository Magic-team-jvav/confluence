package org.confluence.mod.common.effect.harmful;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionDefaults;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFlesh;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFleshMouth;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

// TODO: 移植 WallOfFlesh / WallOfFleshMouth 后移除 terraentity 依赖
public class TheTongueEffect extends PortMobEffect {
    private WallOfFleshMouth mouth;

    public TheTongueEffect() {
        super(MobEffectCategory.HARMFUL, 0xAB1122);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        if (mouth != null && mouth.isAlive() && mouth.parentMob != null && mouth.parentMob.isAlive()) {
            WallOfFlesh wall = mouth.parentMob;
            Vec3 mouthPos = mouth.position();

            double distanceToMouth = living.position().distanceTo(mouthPos);
            if (distanceToMouth > 1000.0 && !wall.getOutsideBox().intersects(living.getBoundingBox())) {
                living.kill();
                return;
            }

            Vec3 targetPos = mouthPos.add(wall.getForward().scale(45));
            if (living.level().dimension() == Level.NETHER
                    && living.getY() < DimensionDefaults.NETHER_GENERATION_HEIGHT
                    && targetPos.y >= DimensionDefaults.NETHER_GENERATION_HEIGHT) {
                targetPos = new Vec3(targetPos.x, targetPos.y - 15.0, targetPos.z);
            }

            if (!living.level().isClientSide
                    && living.getBoundingBox().intersects(wall.getOutsideBox())
                    && !living.getBoundingBox().intersects(wall.getInsideBox())) {
                Vec3 toTarget = targetPos.subtract(living.position());
                double distance = toTarget.length();
                Vec3 dragDirection = toTarget.normalize();

                double speed = wall.getMoveSpeed();
                double speedFactor = Mth.clamp(distance / 15.0 + speed + 0.35f,
                        speed + 0.15f, speed + 0.5);
                Vec3 adjustedForce = dragDirection.scale(speedFactor);

                if (distance <= 9.0F || !living.isAlive()) {
                    living.getActiveEffectsMap().remove(ModEffects.THE_TONGUE).getEffect();
                } else {
                    living.setDeltaMovement(living.getDeltaMovement().add(adjustedForce));
                    living.hurtMarked = true;
                    if (living.tickCount % 10 == 0) {
                        living.hurt(living.level().damageSources().mobAttack(mouth.parentMob), 2);
                    }
                }
            } else if (living.isInWall()
                    && living.getY() < DimensionDefaults.NETHER_GENERATION_HEIGHT * 2.0 / 3.0) {
                living.setDeltaMovement(0, 1.25, 0);
            } else if (living.tickCount % 20 == 0) {
                living.getActiveEffectsMap().remove(ModEffects.THE_TONGUE).getEffect();
            }
        }
    }

    @Override
    public void onEffectStarted(LivingEntity living, int amplifier) {
        super.onEffectStarted(living, amplifier);
        if (getWallOfFleshMouth() == null || !getWallOfFleshMouth().isAlive()) {
            living.hurt(living.level().damageSources().mobAttack(living), 4);
        } else {
            living.hurt(living.level().damageSources().mobAttack(mouth.parentMob), 4);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    public WallOfFleshMouth getWallOfFleshMouth() {
        return mouth;
    }

    public void setWallOfFleshMouth(WallOfFleshMouth mouth) {
        this.mouth = mouth;
    }
}
