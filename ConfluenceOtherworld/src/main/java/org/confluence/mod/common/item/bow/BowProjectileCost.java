package org.confluence.mod.common.item.bow;

import PortLib.extensions.net.minecraft.world.entity.LivingEntity.PortLivingEntityExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.api.projectile.PreparedProjectileCost;
import org.confluence.lib.api.projectile.ProjectileCost;
import org.confluence.lib.api.projectile.ProjectileFireContext;

import java.util.Optional;

/**
 * 一次弓箭动作的弹药与耐久组合成本。
 *
 * <p>弹药引用和武器状态都在 prepare 时冻结，commit 前再次核对；世界生成失败时会精确恢复
 * 弹药数量、武器数量、耐久和 NBT。创造/无限弹药只提交耐久，仍使用标记为不可拾取的视觉箭。</p>
 */
final class BowProjectileCost implements ProjectileCost {
    private final ItemStack selectedAmmo;
    private final boolean consumeAmmo;
    private final int durabilityUse;

    BowProjectileCost(ItemStack selectedAmmo, boolean consumeAmmo, int durabilityUse) {
        if (selectedAmmo == null) {
            throw new IllegalArgumentException("Selected bow ammo must not be null");
        }
        if (durabilityUse < 0) {
            throw new IllegalArgumentException("Bow durability use must not be negative");
        }
        this.selectedAmmo = selectedAmmo;
        this.consumeAmmo = consumeAmmo;
        this.durabilityUse = durabilityUse;
    }

    @Override
    public Optional<PreparedProjectileCost> prepare(ProjectileFireContext context) {
        ItemStack liveWeapon = context.player().getItemInHand(context.hand());
        ItemStack expectedWeapon = liveWeapon.copy();
        ItemStack expectedAmmo = selectedAmmo.copy();
        if (consumeAmmo && (selectedAmmo.isEmpty() || selectedAmmo.getCount() < 1)) {
            return Optional.empty();
        }

        boolean[] ammoConsumed = {false};
        boolean[] weaponDamaged = {false};
        return Optional.of(PreparedProjectileCost.once(() -> {
            if (!context.matchesCurrentWeapon()) {
                throw new IllegalStateException("Prepared bow weapon changed before commit");
            }
            if (consumeAmmo) {
                if (!ItemStack.isSameItemSameTags(selectedAmmo, expectedAmmo)
                        || selectedAmmo.getCount() < 1) {
                    throw new IllegalStateException("Prepared bow ammo changed before commit");
                }
                selectedAmmo.shrink(1);
                ammoConsumed[0] = true;
            }
            if (durabilityUse > 0) {
                liveWeapon.hurtAndBreak(
                        durabilityUse,
                        context.player(),
                        PortLivingEntityExtension.getSlotForHand(context.hand()));
                weaponDamaged[0] = true;
            }
        }, () -> {
            if (weaponDamaged[0]) {
                restoreStackState(liveWeapon, expectedWeapon);
                weaponDamaged[0] = false;
            }
            if (ammoConsumed[0]) {
                selectedAmmo.grow(1);
                ammoConsumed[0] = false;
            }
        }));
    }

    /**
     * 恢复同种物品栈的数量和完整 NBT；成本只会操作当前手中同一件武器。
     */
    private static void restoreStackState(ItemStack target, ItemStack snapshot) {
        target.setCount(snapshot.getCount());
        CompoundTag tag = snapshot.getTag();
        target.setTag(tag == null ? null : tag.copy());
    }
}
