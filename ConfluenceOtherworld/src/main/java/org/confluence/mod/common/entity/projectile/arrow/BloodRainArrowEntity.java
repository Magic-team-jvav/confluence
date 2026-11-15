package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/// 血雨：血雨弓把任意箭矢转化成的落雨弹幕。
///
/// 走箭矢实体（{@link BaseArrowEntity}）的结算，所以满蓄力、附魔、暴击与普通箭一致；
/// 只是它不能被拾取，落地即消失。
public class BloodRainArrowEntity extends BaseArrowEntity {
    public BloodRainArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        this.pickup = Pickup.DISALLOWED;
    }

    public BloodRainArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
        this.pickup = Pickup.DISALLOWED;
    }

    /// 落雨不用停留太久：没砸到东西也会在半空散掉。
    @Override
    protected int getAutoDiscardTick() {
        return 200;
    }
}
