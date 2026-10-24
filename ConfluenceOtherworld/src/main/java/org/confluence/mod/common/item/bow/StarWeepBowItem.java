package org.confluence.mod.common.item.bow;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.mana.HomingFallingStarProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/// 星泣：未命中的箭矢会转化为一次坠星协同，累积到下一次命中敌怪时释放，最多累积三次。
///
/// 累积值按施法者记录，属于会话级状态；命中后一次性清空并按数量落下坠星。
public class StarWeepBowItem extends BaseTerraBowItem {
    /// 累积上限。
    public static final int MAX_STARS = 3;
    /// 每颗坠星的伤害。
    private static final float STAR_DAMAGE = 12.0F;
    /// 多颗坠星依次落下的间隔：0.5 秒。
    private static final int STAR_INTERVAL_TICKS = 10;
    /// 累积表：施法者 -> 已累积的坠星数。
    private static final Map<UUID, Integer> CHARGES = new ConcurrentHashMap<>();

    public StarWeepBowItem(float baseDamage, int durability) {
        super(baseDamage, new Item.Properties().durability(durability));
    }

    /// 普通箭（原版箭 / 药箭等）必须换成泰拉箭实体，才能挂上落空与命中回调。
    ///
    /// 泰拉箭矢物品的 `createArrow` 本身就会返回 `BaseArrowEntity`，这里返回 null 让它自己创建，
    /// 避免把风矢、星箭等特殊箭替换成普通泰拉箭。
    @Override
    public @Nullable BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        if (ammo.getItem() instanceof BaseTerraArrowItem) {
            return null;
        }
        BaseArrowEntity arrow = new BaseArrowEntity(ModEntities.BASE_ARROW.get(), shooter, ammo.copyWithCount(1), weapon);
        arrow.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        arrow.setOwner(shooter);
        return arrow;
    }

    /// 读取当前累积数，供 HUD/提示使用。
    public static int getCharges(LivingEntity owner) {
        return CHARGES.getOrDefault(owner.getUUID(), 0);
    }

    @Override
    public void modifyArrowEntity(BaseArrowEntity entity) {
        entity.addMissCallback(() -> {
            if (entity.getOwner() instanceof LivingEntity owner) {
                CHARGES.merge(owner.getUUID(), 1, (current, added) -> Math.min(MAX_STARS, current + added));
            }
        });
        entity.addWeaponHitEffect((owner, target, fullPull) -> release(owner, target));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.item.confluence.star_weep.0").withStyle(ChatFormatting.GRAY));
    }

    /// 命中时把累积的坠星按 0.5 秒间隔依次释放到目标头上；星泣的坠星不追踪。
    private static void release(LivingEntity owner, LivingEntity target) {
        Integer stored = CHARGES.remove(owner.getUUID());
        if (stored == null || stored <= 0) return;
        Level level = target.level();
        if (level.isClientSide) return;
        for (int i = 0; i < stored; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 2.0;
            double offsetZ = (level.random.nextDouble() - 0.5) * 2.0;
            HomingFallingStarProjectile star = HomingFallingStarProjectile.spawnAbove(
                    level, owner,
                    target.getX() + offsetX, target.getY(), target.getZ() + offsetZ
            );
            star.setHoming(false);
            /// 等待期间跟着目标走、落下时再对准一次，避免目标走位/被击退后整颗砸空。
            star.setFallTarget(target, offsetX, offsetZ);
            /// 箭矢命中留下的无敌帧会吞掉紧跟着落下的第一颗，这里按颗结算伤害。
            star.setIgnoreInvulnerability(true);
            star.setSpawnDelay(i * STAR_INTERVAL_TICKS);
            star.setDamage(STAR_DAMAGE);
            level.addFreshEntity(star);
        }
    }
}
