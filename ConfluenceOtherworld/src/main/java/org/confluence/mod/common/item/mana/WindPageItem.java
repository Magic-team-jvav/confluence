package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.WindBulletProjectile;

/// 风页（法书）：32 伤害、5 魔耗，一次释放 3 个风弹。
///
/// 风弹先向上飞一段距离，随后自动追踪最近的敌人。
public class WindPageItem extends ManaStaffItem<WindBulletProjectile> {
    public static final float BASE_DAMAGE = 32.0F;
    public static final int MANA_COST = 5;
    /// 一次释放的风弹数量。
    public static final int SHOTS = 3;
    /// 相邻风弹的横向间距。
    private static final double SPREAD = 0.18;

    public WindPageItem(ModRarity rarity) {
        super(rarity, WindBulletProjectile::new, BASE_DAMAGE, MANA_COST, 6.0F, 6, 0.04);
        withTooltip(Component.translatable("tooltip.item.confluence.wind_page.0").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer && couldShoot(serverPlayer, stack)) {
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            WindBulletProjectile last = null;
            for (int i = 0; i < SHOTS; i++) {
                WindBulletProjectile projectile = factory.create(serverPlayer);
                beforeShoot(serverPlayer, stack, projectile);
                Vec3 motion = projectile.getDeltaMovement();
                Vec3 right = new Vec3(-motion.z, 0.0, motion.x);
                double offset = (i - (SHOTS - 1) / 2.0) * SPREAD;
                if (right.lengthSqr() > 1.0E-6) {
                    projectile.setPos(projectile.position().add(right.normalize().scale(offset)));
                }
                level.addFreshEntity(projectile);
                last = projectile;
            }
            if (last != null) afterShoot(serverPlayer, stack, last);
        }
        return InteractionResultHolder.success(stack);
    }
}
