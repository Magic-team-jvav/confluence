package org.confluence.mod.common.item.mana;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.entity.projectile.mana.HomingFallingStarProjectile;
import org.confluence.mod.common.entity.projectile.mana.StarMusicChargeEntity;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;

/// 星乐：长按右键在头顶凝聚坠星，蓄力期间每 0.5 秒多消耗 5 点魔力并多凝聚一颗，
/// 最多 20 颗；蓄满后自动释放，中途松手则立即释放已经凝聚的部分。
///
/// 每颗坠星先从头顶的大坠星处向随机方向散开一段距离，再转入追踪；伤害固定，不随使用次数累加。
public class StarMusicItem extends ManaStaffItem<HomingFallingStarProjectile> {
    /// 单颗坠星的基础魔力消耗。
    public static final int MANA_COST_PER_STAR = 5;
    /// 每凝聚一颗坠星需要的蓄力刻数（0.5 秒 = 10 刻）。
    public static final int TICKS_PER_STAR = 10;
    /// 最多凝聚的坠星数量，蓄满即自动释放。
    public static final int MAX_STARS = 20;
    /// 单颗坠星伤害。
    private static final float STAR_DAMAGE = 14.0F;
    /// 坠星从大坠星处散开的速度（格/刻）。
    private static final double DIVERGE_SPEED = 1.0;
    /// 先直线散开的刻数：约 8 格之后再开始追踪。
    private static final int DIVERGE_TICKS = 8;
    /// 蓄满所需的蓄力刻数。
    private static final int FULL_CHARGE_TICKS = TICKS_PER_STAR * MAX_STARS;

    public StarMusicItem(ModRarity rarity) {
        super(rarity, HomingFallingStarProjectile::new, STAR_DAMAGE, MANA_COST_PER_STAR, 0.0F, 6, 0.04);
    }

    /// 蓄力刻数对应的坠星数量：每满 {@link #TICKS_PER_STAR} 刻多一颗，上限 {@link #MAX_STARS}。
    public static int starsForCharge(int chargeTicks) {
        return Mth.clamp(chargeTicks / TICKS_PER_STAR, 0, MAX_STARS);
    }

    /// 指定坠星数量的魔力消耗。
    public static int manaCostForStars(int stars) {
        return MANA_COST_PER_STAR * stars;
    }

    /// 蓄满所需的蓄力刻数，客户端聚星的大小也按它归一化。
    public static int fullChargeTicks() {
        return FULL_CHARGE_TICKS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            // 起步只检查能否支付一颗坠星，真正的消耗在释放时按凝聚数量结算
            if (!canAffordFirstStar(serverPlayer, stack)) {
                return InteractionResultHolder.fail(stack);
            }
            discardCharge(serverPlayer);
            StarMusicChargeEntity charge = new StarMusicChargeEntity(serverPlayer);
            charge.setOwner(serverPlayer);
            Vec3 offset = StarMusicChargeEntity.followOffset(serverPlayer);
            charge.setPos(serverPlayer.getX() + offset.x, serverPlayer.getY() + offset.y, serverPlayer.getZ() + offset.z);
            level.addFreshEntity(charge);
        }
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    /// 使用时长就是蓄满时长：蓄满后由原版 `completeUsingItem` 自动回调 {@link #releaseUsing} 释放，
    /// 中途松手则按已蓄力刻数释放（`stopUsingItem` 不会触发 `releaseUsing`，所以不能靠它收尾）。
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return FULL_CHARGE_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft) {
        if (living instanceof ServerPlayer player) {
            fireStars(player, stack, getUseDuration(stack, living) - timeLeft);
        }
    }

    /// 蓄满自动释放：原版在 `useItemRemaining` 归零时走的是 `completeUsingItem` → `finishUsingItem`，
    /// 只有「手上物品与 useItem 不一致」才会回调 `releaseUsing`，所以自动释放必须挂在这里。
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        if (living instanceof ServerPlayer player) {
            fireStars(player, stack, FULL_CHARGE_TICKS);
        }
        return stack;
    }

    /// 按已蓄力刻数释放坠星：手动松手与蓄满自动释放共用这一条结算路径。
    private void fireStars(ServerPlayer player, ItemStack stack, int chargeTicks) {
        discardCharge(player);
        int stars = starsForCharge(chargeTicks);
        if (stars <= 0) return;
        // 按凝聚数量结算魔力；魔力不足时自动削减到付得起的那部分
        stars = payMana(player, stack, stars);
        if (stars <= 0) return;
        player.awardStat(Stats.ITEM_USED.get(this));
        Level level = player.level();
        /// 从凝聚的大坠星位置向四周随机方向喷出；每颗先直线散开一段距离，再开始追踪。
        Vec3 origin = player.position().add(StarMusicChargeEntity.followOffset(player));
        for (int i = 0; i < stars; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2.0;
            Vec3 direction = new Vec3(Math.cos(angle), 0.35 + level.random.nextDouble() * 0.35, Math.sin(angle));
            HomingFallingStarProjectile star = HomingFallingStarProjectile.spawnBurst(
                    level, player, origin, direction, DIVERGE_SPEED, DIVERGE_TICKS);
            star.setDamage(STAR_DAMAGE);
            level.addFreshEntity(star);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    /// 起步检查：至少付得起一颗坠星（不实际扣除）。
    private static boolean canAffordFirstStar(ServerPlayer player, ItemStack stack) {
        if (player.isCreative()) return true;
        ManaStorage manaStorage = ManaStorage.of(player);
        return manaStorage.canExtract()
                && manaStorage.getCurrentMana() >= PrefixUtils.calculateManaCost(stack, MANA_COST_PER_STAR);
    }

    /// 从多到少尝试结算，返回真正付得起的坠星数量（付不起就是 0）。
    private static int payMana(ServerPlayer player, ItemStack stack, int stars) {
        for (int count = stars; count > 0; count--) {
            int cost = manaCostForStars(count);
            if (PlayerUtils.extractMana(player, stack, () -> PrefixUtils.calculateManaCost(stack, cost))) {
                return count;
            }
        }
        return 0;
    }

    /// 清掉该玩家头顶残留的聚星，避免连续施法或中断后重复悬浮。
    private static void discardCharge(ServerPlayer player) {
        for (StarMusicChargeEntity charge : player.level().getEntitiesOfClass(StarMusicChargeEntity.class,
                player.getBoundingBox().inflate(6.0), entity -> entity.getOwner() == player)) {
            charge.discard();
        }
    }
}
