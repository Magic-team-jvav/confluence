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
import org.confluence.lib.util.LibMathUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.component.SpearProjectileComponent;
import org.confluence.mod.common.entity.projectile.spear.MushroomProjectile;
import org.confluence.mod.common.init.ModEntities;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.loading.math.MathValue;

public class MushroomSpearItem extends AbstractSpearItem {
    /**
     * 刺击前摇结束时刻（tick），矛从蓄力转为前刺的时间点
     */
    private final int windUpEndTick;
    /**
     * 刺击结束时刻（tick），前刺达到最远点的时间点
     */
    private final int strikeEndTick;
    /**
     * 收矛时孢子生成的最小间距（格），使收矛阶段密度与刺出阶段一致
     */
    private static final double RETRACT_SPAWN_SPACING = 0.7;
    /**
     * 上一次生成孢子时矛尖的 z 偏移，用于收矛间距控制
     */
    private double lastSpawnTipZ;

    public MushroomSpearItem() {
        super(new Properties().attributes(attributes(6, 30F)), ModRarity.BLUE, 20, 2, createKeyframes(
                K.of(0, 0, EasingType.LINEAR),
                K.of(0.05, 6, EasingType.EASE_OUT_BACK),
                K.of(0.70, -12, EasingType.EASE_IN_EXPO),
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
        LibEntityUtils.knockBackA2B(owner, victim, 0.3, 0.2);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (!isSelected || !(entity instanceof ServerPlayer owner)) {
            return;
        }

        long gameTime = owner.level().getGameTime();
        long tickCount = gameTime - LibUtils.getItemStackNbtNoCopy(stack).getLong(LAST_ATTACK_TIME_KEY);

        // 刺击阶段：每 tick 在矛尖释放孢子
        if (tickCount > windUpEndTick && tickCount <= strikeEndTick) {
            Vec3 viewVector = owner.getViewVector(1.0F);
            Vec3 position = new Vec3(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
            Vec3 tipPos = position.add(viewVector.scale(getDistance(tickCount, owner)));
            SpearProjectileComponent component = SpearProjectileComponent.MUSHROOM_SPEAR_PROJ.get();
            Vec3 forwardOffset = viewVector.scale(1.0);
            spawnProjectile(owner.serverLevel(), owner, tipPos.add(forwardOffset), component, viewVector);
            lastSpawnTipZ = getDistance(tickCount, owner);
        }
        // 收矛阶段：按间距控制生成密度，与刺出阶段保持一致
        else if (tickCount > strikeEndTick && tickCount <= strikeEndTick + 6) {
            double currentTipZ = getDistance(tickCount, owner);
            if (Math.abs(currentTipZ - lastSpawnTipZ) >= RETRACT_SPAWN_SPACING) {
                Vec3 viewVector = owner.getViewVector(1.0F);
                Vec3 position = new Vec3(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
                Vec3 tipPos = position.add(viewVector.scale(currentTipZ));
                SpearProjectileComponent component = SpearProjectileComponent.MUSHROOM_SPEAR_PROJ.get();
                Vec3 forwardOffset = viewVector.scale(1.0);
                spawnProjectile(owner.serverLevel(), owner, tipPos.add(forwardOffset), component, viewVector);
                lastSpawnTipZ = currentTipZ;
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
