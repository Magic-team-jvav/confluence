package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipBlockItem;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.lib.util.MobEffectInstanceData;

import java.util.List;

/// 有效果的蜡烛物品
public class EffectiveCandleItem extends TooltipBlockItem {
    public final MobEffectInstanceData[] handheldEffect;

    public EffectiveCandleItem(Block block, ModRarity rarity, List<Component> tooltips, MobEffectInstanceData... handheldEffect) {
        super(block, new Properties(), rarity, tooltips);
        this.handheldEffect = handheldEffect;
    }

    public EffectiveCandleItem(Block block, ModRarity rarity, int tooltipCount, Holder<MobEffect> effect, int duration, int amplifier) {
        this(block, rarity, TooltipItem.getTooltipsFromString(BuiltInRegistries.BLOCK.getKey(block).getPath(), tooltipCount, ChatFormatting.GRAY), new MobEffectInstanceData(effect, duration, amplifier));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (isSelected && entity instanceof LivingEntity living) {
            for (MobEffectInstanceData data : handheldEffect) {
                MobEffectInstance instance = living.getActiveEffectsMap().get(data.effect());
                if (instance == null || instance.duration < 20) {
                    living.addEffect(data.create());
                }
            }
        }
    }
}
