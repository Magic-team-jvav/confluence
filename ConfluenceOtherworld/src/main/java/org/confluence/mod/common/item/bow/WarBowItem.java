package org.confluence.mod.common.item.bow;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.item.ModItems;

import java.util.List;

/// 战弓：左键横扫近战、右键拉弓远程都能用。
///
/// 横扫沿用原版剑的判定：只有攻击条蓄满、站在地面且不在疾跑时才会触发（由 `Player#attack` 结算）。
/// Neo 把「能不能横扫」交给 `SWORD_SWEEP` 能力位，弓默认不具备，所以这里显式放开。
///
/// 近战命中时有 50% 概率自动朝最近的敌人补上一箭。
public class WarBowItem extends BaseTerraBowItem {
    /// 自动补箭的搜索半径。
    private static final double AUTO_AIM_RANGE = 24.0;
    /// 触发自动补箭的概率。
    private static final float AUTO_ARROW_CHANCE = 0.5F;
    /// 自动补箭的箭速。
    private static final float AUTO_ARROW_VELOCITY = 2.8F;

    /// 无耐久：不掉耐久，也不需要修复。
    public WarBowItem(float baseDamage) {
        super(baseDamage, new Item.Properties()
                .attributes(ModItems.createAttributes(ModTiers.IRON, 4.0F, -2.4F, ModItems.attributes(0.0, 0.0))));
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return itemAbility == ItemAbilities.SWORD_SWEEP || super.canPerformAction(stack, itemAbility);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.item.confluence.war_bow.0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.item.confluence.war_bow.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.item.confluence.war_bow.2", Math.round(AUTO_ARROW_CHANCE * 100.0F)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (attacker.level().isClientSide || !(attacker instanceof ServerPlayer player)) return result;
        if (player.getRandom().nextFloat() >= AUTO_ARROW_CHANCE) return result;
        shootAtNearest(player, stack);
        return result;
    }

    /// 自动朝最近的敌人发射一支普通箭。
    private void shootAtNearest(ServerPlayer player, ItemStack weapon) {
        Level level = player.level();
        Entity nearest = null;
        double best = AUTO_AIM_RANGE * AUTO_AIM_RANGE;
        for (Entity candidate : LibEntityUtils.queryHitCandidates(level, player, new AABB(player.blockPosition()).inflate(AUTO_AIM_RANGE), e -> LibEntityUtils.effectTarget(e) != null)) {
            if (candidate == player || !candidate.isAlive() || candidate.isSpectator()) continue;
            if (!LibEntityUtils.canHit(player, candidate)) continue;
            double distance = candidate.getBoundingBox().getCenter().distanceToSqr(player.getEyePosition());
            if (distance < best) {
                best = distance;
                nearest = candidate;
            }
        }
        if (nearest == null) return;
        if (!(Items.ARROW instanceof ArrowItem arrowItem)) return;
        AbstractArrow arrow = arrowItem.createArrow(level, Items.ARROW.getDefaultInstance(), player, weapon);
        arrow.setBaseDamage(getFullDrawDamage() * 0.5F);
        arrow.setCritArrow(true);
        Vec3 direction = nearest.getBoundingBox().getCenter().subtract(player.getEyePosition()).normalize();
        arrow.shoot(direction.x, direction.y, direction.z, AUTO_ARROW_VELOCITY, 0.6F);
        level.addFreshEntity(arrow);
    }
}
