package org.confluence.mod.common.item.mana;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.lib.api.projectile.ProjectileFireTrigger;
import org.confluence.lib.api.projectile.ServerProjectileFireService;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.GoldenShowerProjectile;
import org.confluence.mod.common.init.ModSoundEvents;

/// 保留原有脉冲节奏、但让每一发都独立提交魔力事务的黄金雨法杖。
public class GoldenShowerItem extends ManaStaffItem<GoldenShowerProjectile> {
    public GoldenShowerItem() {
        super(ModRarity.LIGHT_RED, GoldenShowerProjectile::new, 20, 7, 30, 0, 0.04);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72_000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.awardStat(Stats.ITEM_USED.get(this));
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }

    @Override
    protected boolean supportsTrigger(ProjectileFireTrigger trigger) {
        return trigger == ProjectileFireTrigger.CONTINUOUS_USE_TICK;
    }

    /// 持续脉冲只接受服务端确认仍在使用当前权杖的请求。
    @Override
    protected boolean validateAction(ProjectileFireContext context) {
        return isActivelyUsingCurrentWeapon(context);
    }

    /// 每枚黄金雨只承担原始七点魔力成本的三分之一。
    @Override
    protected float resolveManaCost(ProjectileFireContext context) {
        return manaCost / 3.0F;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if ((remainingUseDuration & 2) == 0 && livingEntity instanceof ServerPlayer player) {
            ServerProjectileFireService.fire(
                    player,
                    player.getUsedItemHand(),
                    ProjectileFireTrigger.CONTINUOUS_USE_TICK);
        }
    }

    /// 保留原有每六 tick 一次的声音节奏。
    @Override
    protected void playSuccessfulShot(ProjectileFireContext context, GoldenShowerProjectile projectile) {
        if (context.player().getUseItemRemainingTicks() % 6 == 0) {
            context.level().playSound(
                    null,
                    context.player().getX(),
                    context.player().getY(),
                    context.player().getZ(),
                    ModSoundEvents.REGULAR_STAFF_SHOOT_3.get(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F);
        }
    }

    @Override
    protected boolean shouldAwardUsageStat(ProjectileFireContext context) {
        return false;
    }
}
