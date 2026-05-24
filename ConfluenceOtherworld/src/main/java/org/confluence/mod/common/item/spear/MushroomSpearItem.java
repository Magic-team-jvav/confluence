package org.confluence.mod.common.item.spear;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.component.SpearProjectileComponent;
import org.confluence.mod.common.entity.projectile.spear.MushroomProjectile;
import org.confluence.mod.common.init.ModEntities;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.loading.math.MathValue;

public class MushroomSpearItem extends AbstractSpearItem {
    /** 刺击前摇结束时刻（tick），矛从蓄力转为前刺的时间点 */
    private final int windUpEndTick;
    /** 刺击结束时刻（tick），前刺达到最远点的时间点 */
    private final int strikeEndTick;

    public MushroomSpearItem() {
        super(new Properties().attributes(attributes(3, 15F)), ModRarity.BLUE, 40, 5, createKeyframes(
                K.of(0, 0, EasingType.LINEAR),
                K.of(0.2, 6, EasingType.EASE_OUT_BACK),
                K.of(0.8, -12, EasingType.EASE_IN_EXPO),
                K.of(1.0, 0, EasingType.LINEAR)
        ));
        this.windUpEndTick = computeWindUpEndTick();
        this.strikeEndTick = computeStrikeEndTick();
    }

    /**
     * 从关键帧中计算刺击前摇结束时刻：z 值达到最大（蓄力最远）的累积 tick。
     */
    private int computeWindUpEndTick() {
        double maxZ = Double.NEGATIVE_INFINITY;
        int maxZTime = 0;
        double cumulativeTime = 0;
        for (Keyframe<MathValue> frame : keyframes) {
            cumulativeTime += frame.length();
            double endZ = frame.endValue().get();
            if (endZ > maxZ) {
                maxZ = endZ;
                maxZTime = (int) cumulativeTime;
            }
        }
        return maxZTime;
    }

    /**
     * 从关键帧中计算刺击结束时刻：z 值达到最小（前刺最远）的累积 tick。
     */
    private int computeStrikeEndTick() {
        double minZ = Double.POSITIVE_INFINITY;
        int minZTime = 0;
        double cumulativeTime = 0;
        for (Keyframe<MathValue> frame : keyframes) {
            cumulativeTime += frame.length();
            double endZ = frame.endValue().get();
            if (endZ < minZ) {
                minZ = endZ;
                minZTime = (int) cumulativeTime;
            }
        }
        return minZTime;
    }

    @Override
    protected void onHitEntity(DamageSource damageSource, LivingEntity owner, Entity victim) {
        hurtVictim(damageSource, owner, victim);
        VectorUtils.knockBackA2B(owner, victim, 0.3, 0.2);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (!isSelected || !(entity instanceof ServerPlayer owner)) {
            return;
        }

        long gameTime = owner.level().getGameTime();
        long tickCount = gameTime - LibUtils.getItemStackNbtNoCopy(stack).getLong(LAST_ATTACK_TIME_KEY);

        // 释放区间 = [刺击前摇结束时间，刺击结束时间]，在此区间内每tick在矛尖释放一个蘑菇孢子
        if (tickCount > windUpEndTick && tickCount <= strikeEndTick) {
            Vec3 viewVector = owner.getViewVector(1.0F);
            Vec3 position = new Vec3(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
            Vec3 tipPos = position.add(viewVector.scale(getDistance(tickCount, owner)));
            SpearProjectileComponent component = SpearProjectileComponent.MUSHROOM_SPEAR_PROJ.get();
            // 位置前移1格
            Vec3 forwardOffset = viewVector.scale(1.0);
            spawnProjectile(owner.serverLevel(), owner, tipPos.add(forwardOffset), component, viewVector);

            // 刺击结束时，在最远端每隔一格生成一个蘑菇孢子
            if (tickCount == strikeEndTick) {
                Vec3 lookAngle = owner.getLookAngle();
                for (float i = 1.0F; i <= 3.0F; i+=1.2F) {
                    Vec3 spawnPos = tipPos.add(lookAngle.scale(i));
                    spawnProjectile(owner.serverLevel(), owner, spawnPos.add(forwardOffset), component, lookAngle);
                }
            }
        }
    }

    private void spawnProjectile(ServerLevel level, LivingEntity owner, Vec3 pos,
                                  SpearProjectileComponent component, Vec3 direction) {
        MushroomProjectile projectile = new MushroomProjectile(
                ModEntities.MUSHROOM_PROJECTILE.get(), level);
        projectile.setOwner(owner);
        projectile.setWeapon(owner.getMainHandItem());
        projectile.setProjComponent(component, owner);
        projectile.setPos(pos.x, pos.y, pos.z);
        projectile.fire(direction, 0.0f, 0.0f);
        level.addFreshEntity(projectile);
    }
}
