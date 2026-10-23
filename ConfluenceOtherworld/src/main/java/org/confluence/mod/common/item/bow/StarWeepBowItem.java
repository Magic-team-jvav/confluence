package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.mana.HomingFallingStarProjectile;

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
    /// 累积表：施法者 -> 已累积的坠星数。
    private static final Map<UUID, Integer> CHARGES = new ConcurrentHashMap<>();

    public StarWeepBowItem(float baseDamage, int durability) {
        super(baseDamage, new Item.Properties().durability(durability));
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

    /// 命中时把累积的坠星全部释放到目标头上。
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
                    target.getX() + offsetX, target.getY() + 8.0 + i * 1.5, target.getZ() + offsetZ
            );
            star.setDamage(STAR_DAMAGE);
            level.addFreshEntity(star);
        }
    }
}
