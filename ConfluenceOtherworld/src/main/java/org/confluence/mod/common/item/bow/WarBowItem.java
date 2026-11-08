package org.confluence.mod.common.item.bow;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.item.ISpecialSweepWeapon;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.List;

/// 刃弓：左键横扫近战、右键拉弓远程都能用。
///
/// 横扫沿用原版剑的判定：只有攻击条蓄满、站在地面且不在疾跑时才会触发（由 `Player#attack` 结算）。
/// 原版把「能不能横扫」交给 `SWORD_SWEEP` 能力位，弓默认不具备，所以这里显式放开。
///
/// 同时实现了 [ISpecialSweepWeapon]：蓄力条满时空挥也会打出阔剑式的扩大范围横扫，
/// 横扫结算后照常参与「近战命中 50% 补一箭」的判定。
///
/// 伤害分工：左键（含空挥横扫）走武器自身的近战攻击力，横扫倍率与阔剑的 `.specialSweep(0.8F)` 一致；
/// 射出去的箭走远程伤害，会乘上玩家的远程伤害倍率。
public class WarBowItem extends BaseTerraBowItem implements ISpecialSweepWeapon {
    /// 自动补箭的搜索半径。
    private static final double AUTO_AIM_RANGE = 24.0;
    /// 触发自动补箭的概率。
    private static final float AUTO_ARROW_CHANCE = 0.5F;
    /// 自动补箭的箭速。
    private static final float AUTO_ARROW_VELOCITY = 2.8F;
    /// 横扫伤害倍率，与剑的 `.specialSweep(0.8F)` 等价
    private static final float SWEEP_DAMAGE_RATIO = 0.8F;

    /// 无耐久：不掉耐久，也不需要修复。
    public WarBowItem(float baseDamage) {
        super(baseDamage, new Item.Properties().attributes(attributes()));
    }

    /// 近战属性沿用弓的默认值，额外补上阔剑式的横扫倍率（横扫伤害 = 1 + 0.8 × 近战攻击力）
    private static PortItemAttributeModifiers attributes() {
        return ModItems.createAttributes(ModTiers.IRON, 4.0F, -2.4F, ModItems.attributes(0.0, 0.0).andThen(builder ->
                builder.add(Attributes.SWEEPING_DAMAGE_RATIO,
                        new PortAttributeModifier(Confluence.asResource("blade_bow_sweep"), SWEEP_DAMAGE_RATIO, PortAttributeModifier.Operation.ADD_VALUE),
                        PortEquipmentSlotGroup.MAINHAND)));
    }

    /// 箭矢按远程伤害结算：把武器伤害乘上玩家的远程伤害倍率
    @Override
    public AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
        AbstractArrow result = super.customArrow(arrow, projectileStack, weaponStack);
        if (result.getOwner() instanceof LivingEntity owner) {
            result.setBaseDamage((float) (result.getBaseDamage() * owner.getAttributeValue(LibAttributes.getRangedDamage())));
        }
        return result;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return toolAction == ToolActions.SWORD_SWEEP || super.canPerformAction(stack, toolAction);
    }

    /// 横扫判定改用阔剑的扩大范围
    @Override
    public AABB getSweepHitBox(ItemStack stack, Player player, Entity target) {
        return BaseSwordItem.getSpecialSweepArea(player);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.item.confluence.blade_bow.0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.item.confluence.blade_bow.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.item.confluence.blade_bow.2", Math.round(AUTO_ARROW_CHANCE * 100.0F)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (attacker instanceof ServerPlayer player) {
            tryAutoArrow(player, stack);
        }
        return result;
    }

    /// 空挥的特殊横扫同样会参与补箭判定
    @Override
    public void onSpecialSweep(ServerPlayer player) {
        tryAutoArrow(player, player.getMainHandItem());
    }

    private void tryAutoArrow(ServerPlayer player, ItemStack weapon) {
        if (player.level().isClientSide || weapon.isEmpty()) return;
        if (player.getRandom().nextFloat() >= AUTO_ARROW_CHANCE) return;
        shootAtNearest(player, weapon);
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
        arrow.setBaseDamage((float) (getFullDrawDamage() * player.getAttributeValue(LibAttributes.getRangedDamage()) * 0.5F));
        arrow.setCritArrow(true);
        Vec3 direction = nearest.getBoundingBox().getCenter().subtract(player.getEyePosition()).normalize();
        arrow.shoot(direction.x, direction.y, direction.z, AUTO_ARROW_VELOCITY, 0.6F);
        level.addFreshEntity(arrow);
    }
}
