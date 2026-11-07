package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.projectile.mana.AbstractLittleCelestialEntity;
import org.confluence.mod.common.entity.projectile.mana.LittleMoonEntity;
import org.confluence.mod.common.entity.projectile.mana.LittleSunEntity;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;

import java.util.Optional;

/// 昏晓：法师武器。
///
/// 20 魔耗、30 基础伤害；交替发射「日光弹幕」与「月光弹幕」。
/// 日光弹幕命中后在敌怪头顶挂一个小日光：持续 10 秒，每秒对 5x5 范围内的敌怪造成 10% 伤害，
/// 宿主死亡时再补发一次不带效果的日光弹幕。月光弹幕则挂一个小月光：持续 7 秒，
/// 每 2 秒朝宿主之外的附近敌怪射出一道 80% 伤害的月光射线，宿主死亡时把自身再射出去。
/// 一个敌怪头顶最多挂一个，且日光与月光互斥；再次命中只刷新已有附着（异种则直接替换），
/// 弹幕本身不结算伤害。
public class DuskDawnItem extends ManaStaffItem<AbstractLittleCelestialEntity> {
    public static final float BASE_DAMAGE = 30.0F;
    public static final int MANA_COST = 20;
    /// 下次是否发射月光弹幕（跟随玩家存档，服务端使用）
    private static final String NEXT_MOON_TAG = "ConfluenceDuskDawnNextMoon";
    private TooltipComponent tooltipImage;

    public DuskDawnItem(ModRarity rarity) {
        super(rarity, DuskDawnItem::createProjectile, BASE_DAMAGE, MANA_COST, 6.0F, 16, 0.04);
        withTooltip(
                Component.translatable("tooltip.item.confluence.dusk_dawn.0").withStyle(ChatFormatting.GRAY),
                Component.translatable("tooltip.item.confluence.dusk_dawn.1").withStyle(ChatFormatting.GRAY),
                Component.translatable("tooltip.item.confluence.dusk_dawn.2").withStyle(ChatFormatting.GRAY)
        );
    }

    /// 与裂天剑一致，在 tooltip 里额外画出大图预览（使用 HEAD 视角模型里的 24x 贴图）
    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (tooltipImage == null) {
            this.tooltipImage = AltImageComponent.of(stack.getItem());
        }
        return Optional.of(tooltipImage);
    }

    /// 昏晓只在附着期间结算伤害；开火瞬间不再用射线补一次伤害，弹幕命中也不结算伤害
    @Override
    protected void rayTrace(ServerPlayer player, ItemStack stack, AbstractLittleCelestialEntity projectile) {}

    /// 日光 → 月光 → 日光…… 交替发射
    private static AbstractLittleCelestialEntity createProjectile(ServerPlayer player) {
        CompoundTag data = LibEntityUtils.getOrCreatePersistedData(player);
        boolean moon = !data.getBoolean(NEXT_MOON_TAG);
        data.putBoolean(NEXT_MOON_TAG, moon);
        return moon ? new LittleMoonEntity(player, BASE_DAMAGE) : new LittleSunEntity(player, BASE_DAMAGE);
    }
}
