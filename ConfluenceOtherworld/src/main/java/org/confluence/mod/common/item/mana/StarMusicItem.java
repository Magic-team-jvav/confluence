package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.HomingFallingStarProjectile;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/// 星乐：消耗 10 点魔力，长按右键在头顶逐渐聚起越来越大的坠星。
///
/// 松开右键时坠星爆开，向下喷射追踪坠星，数量与蓄力时间正相关；
/// 累计使用 20 次后达到临界值，下一次释放会额外爆开一轮更大的星雨。
public class StarMusicItem extends ManaStaffItem<HomingFallingStarProjectile> {
    public static final int MANA_COST = 10;
    /// 每多蓄力这么多 tick 多一颗坠星。
    private static final int TICKS_PER_STAR = 20;
    /// 单次最多喷射的坠星数量。
    private static final int MAX_STARS = 6;
    /// 临界值：使用这么多次后必定爆开。
    private static final int CRITICAL_USES = 20;
    /// 单颗坠星伤害。
    private static final float STAR_DAMAGE = 14.0F;
    /// 达到临界值后额外增加的坠星数量。
    private static final int CRITICAL_EXTRA_STARS = 4;
    /// 最短蓄力时间，过短不触发。
    private static final int MIN_CHARGE_TICKS = 6;

    /// 使用次数计数：施法者 -> 已使用次数。
    private static final Map<UUID, Integer> USE_COUNTER = new ConcurrentHashMap<>();

    public StarMusicItem(ModRarity rarity) {
        super(rarity, HomingFallingStarProjectile::new, STAR_DAMAGE, MANA_COST, 0.0F, 6, 0.04);
        withTooltip(Component.translatable("tooltip.item.confluence.star_music.0").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer && !couldShoot(serverPlayer, stack)) {
            return InteractionResultHolder.fail(stack);
        }
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft) {
        if (!(living instanceof ServerPlayer player)) return;
        int used = getUseDuration(stack, living) - timeLeft;
        if (used < MIN_CHARGE_TICKS) return;
        int stars = Mth.clamp(1 + used / TICKS_PER_STAR, 1, MAX_STARS);
        int uses = USE_COUNTER.merge(player.getUUID(), 1, Integer::sum);
        boolean critical = uses >= CRITICAL_USES;
        if (critical) {
            USE_COUNTER.put(player.getUUID(), 0);
            stars += CRITICAL_EXTRA_STARS;
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        for (int i = 0; i < stars; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2.0;
            double radius = 0.5 + level.random.nextDouble() * 1.5;
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;
            double y = player.getY() + 6.0 + level.random.nextDouble() * 2.0;
            HomingFallingStarProjectile star = HomingFallingStarProjectile.spawnAbove(level, player, x, y, z);
            star.setDamage(STAR_DAMAGE * (critical ? 1.5F : 1.0F));
            level.addFreshEntity(star);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
