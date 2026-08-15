package org.confluence.mod.common.item.crossbow;

import PortLib.extensions.net.minecraft.world.entity.LivingEntity.PortLivingEntityExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.api.projectile.PreparedProjectileCost;
import org.confluence.lib.api.projectile.ProjectileCost;
import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.mod.common.component.RepeaterContents;
import org.confluence.mod.common.init.ModDataComponentTypes;

import java.util.Optional;

/**
 * 连弩弹仓与耐久的原子 prepared cost。
 *
 * <p>每个实际生成的 burst 批次各自预留一枚弹药和对应耐久；生成失败会恢复完整弹仓、数量、
 * 耐久和 NBT。尚未执行的延迟批次没有成本，因此取消连发不会吞掉剩余弹药。</p>
 */
final class RepeaterContentsProjectileCost implements ProjectileCost {
    private final RepeaterContents expectedContents;
    private final RepeaterContents remainingContents;
    private final int durabilityUse;

    RepeaterContentsProjectileCost(
            RepeaterContents expectedContents,
            RepeaterContents remainingContents,
            int durabilityUse
    ) {
        if (expectedContents == null || remainingContents == null) {
            throw new IllegalArgumentException("Repeater contents cost must not contain null values");
        }
        if (durabilityUse < 0) {
            throw new IllegalArgumentException("Repeater durability use must not be negative");
        }
        this.expectedContents = expectedContents;
        this.remainingContents = remainingContents;
        this.durabilityUse = durabilityUse;
    }

    @Override
    public Optional<PreparedProjectileCost> prepare(ProjectileFireContext context) {
        ItemStack weapon = context.player().getItemInHand(context.hand());
        RepeaterContents liveContents = weapon.getOrDefault(
                ModDataComponentTypes.REPEATER_CONTENTS.get(), RepeaterContents.EMPTY);
        if (!liveContents.equals(expectedContents)
                || liveContents.getItemsTotalCount() != expectedContents.getItemsTotalCount()) {
            return Optional.empty();
        }
        ItemStack weaponSnapshot = weapon.copy();
        boolean[] committed = {false};
        return Optional.of(PreparedProjectileCost.once(() -> {
            if (!context.matchesCurrentWeapon()) {
                throw new IllegalStateException("Prepared repeater weapon changed before commit");
            }
            RepeaterContents current = weapon.getOrDefault(
                    ModDataComponentTypes.REPEATER_CONTENTS.get(), RepeaterContents.EMPTY);
            if (!current.equals(expectedContents)
                    || current.getItemsTotalCount() != expectedContents.getItemsTotalCount()) {
                throw new IllegalStateException("Prepared repeater contents changed before commit");
            }
            weapon.set(ModDataComponentTypes.REPEATER_CONTENTS.get(), remainingContents);
            if (durabilityUse > 0) {
                weapon.hurtAndBreak(
                        durabilityUse,
                        context.player(),
                        PortLivingEntityExtension.getSlotForHand(context.hand()));
            }
            committed[0] = true;
        }, () -> {
            if (committed[0]) {
                restoreStackState(weapon, weaponSnapshot);
                committed[0] = false;
            }
        }));
    }

    private static void restoreStackState(ItemStack target, ItemStack snapshot) {
        target.setCount(snapshot.getCount());
        CompoundTag tag = snapshot.getTag();
        target.setTag(tag == null ? null : tag.copy());
    }
}
