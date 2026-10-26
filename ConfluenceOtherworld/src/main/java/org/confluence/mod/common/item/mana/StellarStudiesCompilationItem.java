package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.StellarStudiesProjectile;
import org.confluence.mod.common.entity.projectile.mana.StellarStudiesProjectile.Variant;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.item.MaterialItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// 星体研究汇编。
///
/// 空壳状态下无法攻击，需要在物品栏里用左键把一张张 {@code 蕴魔书页} 叠进去（最多 {@link #MAX_PAGES} 张）：
/// - 第 1 张解锁攻击，并让攻击有 {@link #EFFECT_CHANCE} 的概率额外射出一发漂浮弹幕；
/// - 第 2~5 张各追加一种 5% 概率的附加效果弹幕（中毒 / 着火 / 凋零 / 减速）。
public class StellarStudiesCompilationItem extends ManaStaffItem<StellarStudiesProjectile> {
    /// 最多能填充的书页数量。
    public static final int MAX_PAGES = 5;
    /// 每种附加效果弹幕的触发概率。
    public static final float EFFECT_CHANCE = 0.15F;
    public static final float BASE_DAMAGE = 14.0F;
    public static final int MANA_COST = 6;
    /// 除基础弹幕之外的所有附加效果弹幕，顺序与书页顺序一致。
    private static final Variant[] EFFECTS = {
            Variant.LEVITATION, Variant.POISON, Variant.FIRE, Variant.WITHER, Variant.SLOW
    };

    public StellarStudiesCompilationItem(ModRarity rarity) {
        super(rarity, player -> new StellarStudiesProjectile(player, Variant.BASE), BASE_DAMAGE, MANA_COST, 9.0F, 8, 0.04);
    }

    /// 当前装订好的书页数量（0~{@link #MAX_PAGES}）。
    public static int getPages(ItemStack stack) {
        return stack.getOrDefault(ModDataComponentTypes.STELLAR_STUDIES_PAGES, 0);
    }

    public static void setPages(ItemStack stack, int pages) {
        stack.set(ModDataComponentTypes.STELLAR_STUDIES_PAGES, Mth.clamp(pages, 0, MAX_PAGES));
    }

    /// 一张书页都没装的时候攻击是锁死的。
    public static boolean canAttack(ItemStack stack) {
        return getPages(stack) > 0;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!canAttack(stack)) {
            return InteractionResultHolder.success(stack);
        }
        if (!(player instanceof ServerPlayer serverPlayer) || !couldShoot(serverPlayer, stack)) {
            return InteractionResultHolder.success(stack);
        }
        serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        StellarStudiesProjectile last = shoot(serverPlayer, stack, Variant.BASE);
        // 基础弹幕飞得不算慢，补一段射线，避免贴脸时在 1 tick 内直接穿过目标。
        rayTrace(serverPlayer, stack, last);
        int pages = getPages(stack);
        for (Variant effect : EFFECTS) {
            if (pages >= effect.requiredPages && serverPlayer.getRandom().nextFloat() < EFFECT_CHANCE) {
                last = shoot(serverPlayer, stack, effect);
            }
        }
        afterShoot(serverPlayer, stack, last);
        return InteractionResultHolder.success(stack);
    }

    private StellarStudiesProjectile shoot(ServerPlayer player, ItemStack stack, Variant variant) {
        StellarStudiesProjectile projectile = factory.create(player);
        projectile.setVariant(variant);
        beforeShoot(player, stack, projectile);
        player.level().addFreshEntity(projectile);
        return projectile;
    }

    /// 手持蕴魔书页左键点击本物品即可装订一页。
    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action != ClickAction.PRIMARY || other.isEmpty()) return false;
        if (!other.is(MaterialItems.LOST_ENCHANTED_PAGE.get())) return false;
        int pages = getPages(stack);
        if (pages >= MAX_PAGES) return false;
        setPages(stack, pages + 1);
        other.shrink(1);
        // 双端都会跑 doClick，用 Entity#playSound 而不是 Level#playSound，避免服务端广播叠加出两次音效。
        player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F);
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
        int pages = getPages(stack);
        String key = "tooltip.item.confluence.stellar_studies_compilation.";
        if (pages <= 0) {
            tooltipComponents.add(Component.translatable(key + "missing_page").withStyle(ChatFormatting.DARK_RED));
            return;
        }
        if (pages < MAX_PAGES) {
            tooltipComponents.add(Component.translatable(key + "remaining", MAX_PAGES - pages).withStyle(ChatFormatting.GOLD));
        } else {
            tooltipComponents.add(Component.translatable(key + "complete").withStyle(ChatFormatting.GREEN));
        }
        for (Variant effect : EFFECTS) {
            if (pages >= effect.requiredPages) {
                tooltipComponents.add(Component.translatable(key + effect.getSerializedName()).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
