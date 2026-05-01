package org.confluence.mod.common.item.sword;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.util.DateUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StarSteelSword extends BaseSwordItem {
    /// 玩家UUID → 上次拾取魔力星的时间戳（毫秒）
    private static final Map<UUID, Long> MANA_PICKUP_TIMESTAMPS = new ConcurrentHashMap<>();
    /// 玩家UUID → 最近一次攻击是否为暴击
    private static final Map<UUID, Boolean> LAST_ATTACK_CRIT = new ConcurrentHashMap<>();
    /// 暴击增益持续时间（毫秒）
    private static final long CRIT_BUFF_DURATION_MS = 1000;
    /// 暴击倍率（正常为1.5，星钢剑增益为2.5）
    public static final float CRIT_MULTIPLIER = 2.5F;
    /// 正常暴击倍率
    private static final float NORMAL_CRIT_MULTIPLIER = 1.5F;
    /// 掉落魔力星概率
    private static final float DROP_STAR_CHANCE = 0.75F;

    public StarSteelSword(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, ModifierBuilder modifierBuilder) {
        super(tier, rarity, rawDamage, rawSpeed, modifierBuilder);
    }

    public static void onManaStarPickup(Player player) {
        MANA_PICKUP_TIMESTAMPS.put(player.getUUID(), System.currentTimeMillis());
    }

    public static boolean hasCritBuff(Player player) {
        Long lastPickup = MANA_PICKUP_TIMESTAMPS.get(player.getUUID());
        if (lastPickup == null) return false;
        if (System.currentTimeMillis() - lastPickup > CRIT_BUFF_DURATION_MS) {
            MANA_PICKUP_TIMESTAMPS.remove(player.getUUID());
            return false;
        }
        return true;
    }

    /**
     * 记录玩家最近一次攻击是否为暴击，在LivingDamageEvent.Post中调用
     */
    public static void recordCritResult(Player player, boolean isCrit) {
        LAST_ATTACK_CRIT.put(player.getUUID(), isCrit);
    }

    /**
     * 消费并返回最近一次攻击是否为暴击
     */
    public static boolean consumeCritResult(Player player) {
        Boolean result = LAST_ATTACK_CRIT.remove(player.getUUID());
        return result != null && result;
    }

    /**
     * 获取当前暴击倍率
     * 如果玩家在增益窗口内返回2.5，否则返回1.5
     */
    public static float getCritMultiplier(Player player) {
        return hasCritBuff(player) ? CRIT_MULTIPLIER : NORMAL_CRIT_MULTIPLIER;
    }

    /**
     * 尝试掉落魔力星（暴击时不掉落）
     */
    public static void tryDropManaStar(LivingEntity target, LivingEntity attacker) {
        if (target.level() instanceof ServerLevel serverLevel
                && !(attacker instanceof Player player && consumeCritResult(player))
                && target.getRandom().nextFloat() < DROP_STAR_CHANCE) {
            ItemStack star = DateUtils.getStarItem().getDefaultInstance();
            ItemEntity itemEntity = new ItemEntity(serverLevel,
                    target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(), star);
            itemEntity.setPickUpDelay(10);
            if (attacker != null) {
                itemEntity.setDeltaMovement(attacker.getLookAngle().add(0, 0.2, 0).scale(0.3));
            }
            serverLevel.addFreshEntity(itemEntity);
        }
    }
}
