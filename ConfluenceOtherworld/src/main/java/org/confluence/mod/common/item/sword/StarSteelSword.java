package org.confluence.mod.common.item.sword;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.util.DateUtils;

public class StarSteelSword extends BaseSwordItem {
    /// 暴击增益持续时间（tick）
    public static final long CRIT_BUFF_DURATION = 20;
    /// 暴击倍率（正常为1.5，星钢剑增益为2.5）
    public static final float CRIT_MULTIPLIER = 2.5F;
    /// 掉落魔力星概率
    public static final float DROP_STAR_CHANCE = 0.75F;

    public StarSteelSword() {
        super(ModTiers.UNBREAKABLE, ModRarity.BLUE, 9, 2.2F, new BaseSwordItem.ModifierBuilder().addTooltip(p -> p.withColor(0xc0e8ff)).addTooltip(p -> p.withColor(0xc0e8ff)));
    }

    public static void onManaStarPickup(Player player) {
        LibEntityUtils.getOrCreatePersistedData(player).putLong("confluence:mana_pickup_timestamp", player.level().getGameTime());
    }

    public static void processCriticalDamage(ServerPlayer player, boolean critical, FloatConsumer multiplier) {
        if (player.getMainHandItem().is(SwordItems.STAR_STEEL_SWORD)) {
            CompoundTag tag = LibEntityUtils.getOrCreatePersistedData(player);
            tag.putBoolean("confluence:last_attack_crit", critical);
            if (critical) {
                long lastPickup = tag.getLong("confluence:mana_pickup_timestamp");
                if (lastPickup != 0 && player.level().getGameTime() - lastPickup <= CRIT_BUFF_DURATION) {
                    multiplier.accept(CRIT_MULTIPLIER);
                }
            }
        }
    }

    /// 尝试掉落魔力星（暴击时不掉落）
    public static void tryDropManaStar(LivingEntity victim, ServerPlayer attacker) {
        if (attacker.getMainHandItem().is(SwordItems.STAR_STEEL_SWORD)) {
            CompoundTag tag = LibEntityUtils.getOrCreatePersistedData(attacker);
            boolean lastAttackCrit = tag.getBoolean("confluence:last_attack_crit");
            tag.remove("confluence:last_attack_crit");
            if (lastAttackCrit) return;
            if (LibMathUtils.checkChance(DROP_STAR_CHANCE, victim.getRandom())) {
                Vec3 delta = attacker.getLookAngle().add(0, 0.2, 0).scale(0.3);
                ItemEntity itemEntity = new ItemEntity(
                        attacker.level(),
                        victim.getX(),
                        victim.getY() + victim.getBbHeight() * 0.5,
                        victim.getZ(),
                        DateUtils.getStarItem().getDefaultInstance(),
                        delta.x,
                        delta.y,
                        delta.z
                );
                itemEntity.setPickUpDelay(10);
                attacker.level().addFreshEntity(itemEntity);
            }
        }
    }
}
