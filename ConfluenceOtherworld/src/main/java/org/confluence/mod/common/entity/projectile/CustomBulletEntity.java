package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.entity.ModEntities;
import org.mesdag.portlib.wrapper.common.extensions.IPortEntityExtension;

/**
 * 带重力的枪械子弹变体。
 *
 * <p>与 1.21 TerraGuns 保持一致：真实弹药始终保存在基础类的 {@code BULLET} 槽里，
 * 本类只额外保存重力。这样命中行为、拖尾颜色、客户端渲染和保存恢复都会读取同一个弹药来源，
 * 不会出现“看起来是一种子弹、实际执行另一种子弹行为”的错位。</p>
 */
public class CustomBulletEntity extends BaseBulletEntity implements ItemSupplier, IPortEntityExtension {
    protected float gravity = 0.0F;

    public CustomBulletEntity(EntityType<? extends BaseBulletEntity> type, Level level) {
        super(type, level);
    }

    public CustomBulletEntity(LivingEntity owner, float gravity, ItemStack bullet) {
        this(ModEntities.GRAVITY_BULLET_ENTITY.get(), owner, gravity, bullet);
    }

    public CustomBulletEntity(
            EntityType<? extends BaseBulletEntity> type,
            LivingEntity owner,
            float gravity,
            ItemStack bullet
    ) {
        super(type, owner, bullet);
        this.gravity = gravity;
    }

    /**
     * 创建派生弹丸时保留原弹丸的真实弹药与重力。
     */
    public CustomBulletEntity(
            EntityType<? extends BaseBulletEntity> type,
            Level level,
            double x,
            double y,
            double z,
            ItemStack bullet,
            float gravity
    ) {
        super(type, level, x, y, z, bullet);
        this.gravity = gravity;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (getProjectileCombatSnapshot() == null) {
            resetCustomRuntimeFields();
            return;
        }
        try {
            BulletRuntimeState.CustomState runtimeState = BulletRuntimeState.readCustom(compound);
            this.gravity = runtimeState.gravity();
        } catch (RuntimeException exception) {
            resetCustomRuntimeFields();
            invalidateRuntimeState(BulletRuntimeState.englishReason(
                    exception, "Malformed custom bullet runtime state"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        BulletRuntimeState.writeCustom(compound, this.gravity, this.getBulletStack());
    }

    @Override
    protected void applyForces() {
        this.applyGravity();
    }

    public float getBulletGravity() {
        return gravity;
    }

    @Override
    public double getDefaultGravity() {
        return gravity;
    }

    @Override
    public ItemStack getItem() {
        return this.getBulletStack();
    }

    private void resetCustomRuntimeFields() {
        this.gravity = 0.0F;
    }
}
