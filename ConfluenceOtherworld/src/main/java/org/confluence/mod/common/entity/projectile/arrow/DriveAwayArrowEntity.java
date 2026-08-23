package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.effect.harmful.DriveAwayController;
import org.confluence.mod.common.entity.monster.Harpy;
import org.confluence.mod.common.init.ModEffects;
import org.jetbrains.annotations.Nullable;

/// 惊弓专用箭矢，会沿飞行路径驱散附近的飞行生物。
///
/// 驱离完全由服务端计算。箭矢飞行时提供较弱的持续推力，命中实体或方块时产生一次
/// 范围更大的冲击；客户端只接收实体速度和效果同步，不能指定受影响目标。直接命中的
/// 飞行生物仍然承受 1.5 倍箭矢伤害。
public class DriveAwayArrowEntity extends BaseArrowEntity {
    private static final double TRAIL_RADIUS = 3.0;
    private static final double TRAIL_SPEED = 1.3;
    private static final int TRAIL_DURATION = 160;
    private static final double IMPACT_RADIUS = 6.0;
    private static final double IMPACT_SPEED = 1.3;
    private static final int IMPACT_DURATION = 260;

    private boolean hittingFlyingTarget;

    public DriveAwayArrowEntity(EntityType<? extends DriveAwayArrowEntity> entityType, Level level) {
        super(entityType, level);
    }

    public DriveAwayArrowEntity(EntityType<? extends DriveAwayArrowEntity> entityType, LivingEntity owner,
                                ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && !isRemoved()) {
            driveAwayNearby(position(), TRAIL_RADIUS, TRAIL_SPEED, TRAIL_DURATION);
        }
    }

    @Override
    public double getBaseDamage() {
        double damage = super.getBaseDamage();
        return hittingFlyingTarget ? damage * 1.5 : damage;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        hittingFlyingTarget = result.getEntity() instanceof LivingEntity living && isDriveAwayTarget(living);
        try {
            super.onHitEntity(result);
        } finally {
            hittingFlyingTarget = false;
        }
        if (!level().isClientSide) {
            driveAwayNearby(result.getLocation(), IMPACT_RADIUS, IMPACT_SPEED, IMPACT_DURATION);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            driveAwayNearby(result.getLocation(), IMPACT_RADIUS, IMPACT_SPEED, IMPACT_DURATION);
        }
    }

    /// 从给定中心驱散范围内的飞行生物，供命中与飞行路径共用同一套规则。
    void driveAwayNearby(Vec3 center, double radius, double speed, int duration) {
        if (level().isClientSide || radius <= 0.0 || speed <= 0.0 || duration <= 0) {
            return;
        }
        AABB area = AABB.ofSize(center, radius * 2.0, radius * 2.0, radius * 2.0);
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity != getOwner() && entity.isAlive() && isDriveAwayTarget(entity) && !entity.hasEffect(ModEffects.SCARED.get()))) {
            target.addEffect(new MobEffectInstance(ModEffects.SCARED.get(), duration, 0), getOwner());
            if (target instanceof Mob mob) DriveAwayController.start(mob, center, speed, duration);
        }
    }

    static boolean isDriveAwayTarget(LivingEntity entity) {
        return entity instanceof FlyingAnimal
                || entity instanceof FlyingMob
                || entity instanceof Harpy;
    }

    public static BaseArrowEntity create(EntityType<? extends AbstractArrow> type, LivingEntity shooter,
                                         ItemStack pickupItemStack, ItemStack firedFromWeapon) {
        return new DriveAwayArrowEntity((EntityType<? extends DriveAwayArrowEntity>) type, shooter, pickupItemStack, firedFromWeapon);
    }
}
