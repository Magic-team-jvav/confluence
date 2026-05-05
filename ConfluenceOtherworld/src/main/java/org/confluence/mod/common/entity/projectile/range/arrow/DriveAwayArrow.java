package org.confluence.mod.common.entity.projectile.range.arrow;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;
import org.confluence.terraentity.entity.monster.Harpy;
import org.confluence.terraentity.init.TEEffects;
import org.confluence.terraentity.utils.DriveAwaySystem.DriveAwayArrowIntegration;
import org.jetbrains.annotations.Nullable;

/**
 * 稻草人弓专用箭矢，在飞行过程中驱离鸟妖
 * 对飞行单位造成1.5倍伤害
 * 使用 DriveAwayArrowIntegration 处理飞行途中和命中驱离逻辑
 */
public class DriveAwayArrow extends BaseArrowEntity {

    private boolean hittingFlyingTarget = false;

    public DriveAwayArrow(EntityType<? extends DriveAwayArrow> entityType, Level level) {
        super(entityType, level);
    }

    public DriveAwayArrow(EntityType<? extends DriveAwayArrow> entityType, LivingEntity owner,
                          ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon,
                          @Nullable BaseTerraArrowItem arrow,
                          BaseTerraArrowItem.ModifyArrowBuilder modifyConsumer) {
        super(entityType, owner, pickupItemStack, firedFromWeapon, arrow, modifyConsumer);
    }

    @Override
    public void tick() {
        // 调用父类 tick（包括丢弃检测、粒子效果等）
        super.tick();
        
        // 仅在服务端执行驱离逻辑
        if (!level().isClientSide()) {
            DriveAwayArrowIntegration.onArrowTick(this);
        }
    }
    
    @Override
    public double getBaseDamage() {
        double damage = super.getBaseDamage();
        if (this.hittingFlyingTarget) {
            damage *= 1.5;
        }
        return damage;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        // 在命中前计算伤害倍数
        if (result.getEntity() instanceof LivingEntity living) {
            // 对飞行单位造成1.5倍伤害（一箭杀鸟）
            if (living instanceof FlyingAnimal || living instanceof FlyingMob || living instanceof Harpy) {
                // 在 super.onHitEntity 之前设置，确保 getBaseDamage() 被调用时倍率已生效
                this.hittingFlyingTarget = true;
            }
        }
        super.onHitEntity(result);
        // super 之后立即重置
        this.hittingFlyingTarget = false;

        // 命中时触发大范围驱离
        if (!level().isClientSide()) {
            Vec3 hitPos = result.getLocation();
            // 调用 DriveAwayArrowIntegration 处理命中驱离和药水效果
            DriveAwayArrowIntegration.onArrowHit(this, hitPos);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        // 命中方块时也触发驱离
        if (!level().isClientSide()) {
            Vec3 hitPos = result.getLocation();
            DriveAwayArrowIntegration.onArrowHit(this, hitPos);
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
        // 清理追踪数据
        DriveAwayArrowIntegration.clearArrowTrackingData(this);
    }

    /**
     * 工厂方法，用于 BaseTerraArrowItem.EntityTransform
     * 匹配 ArrowFactory 接口：(type, shooter, pickupItemStack, firedFromWeapon, arrow, modifyConsumer)
     */
    public static BaseArrowEntity create(EntityType<? extends AbstractArrow> type, LivingEntity shooter,
                                            ItemStack pickupItemStack, ItemStack firedFromWeapon,
                                            @Nullable BaseTerraArrowItem arrow,
                                            BaseTerraArrowItem.ModifyArrowBuilder modifyConsumer) {
        return new DriveAwayArrow((EntityType<? extends DriveAwayArrow>) type, shooter, pickupItemStack, firedFromWeapon, arrow, modifyConsumer);
    }
}
