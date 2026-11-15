package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.init.item.HammerItems;

/// 圣骑士投掷的旋转重锤。
///
/// 重锤具有真实飞行时间并能穿过实心方块。当前使用已有神锤物品作为三维视觉，
/// 等专用圣骑士锤资源补齐时只需替换 {@link #getItem()}，服务端碰撞与伤害行为无需改动。
public final class PaladinHammerProjectile extends StraightMonsterProjectile implements ItemSupplier {

    public PaladinHammerProjectile(EntityType<? extends PaladinHammerProjectile> type, Level level) {
        super(type, level);
    }

    public void configure(Mob owner, LivingEntity target, float damage) {
        super.configure(owner, target, damage, 0.65F, 1.5F, 80);
    }

    @Override
    public ItemStack getItem() {
        return HammerItems.PWNHAMMER.toStack();
    }

    /// 圣骑士锤在泰拉瑞亚中不会被墙体截停；实体命中仍由公共弹幕逻辑结算并销毁。
    @Override
    protected void onHitBlock(BlockHitResult result) {}
}
