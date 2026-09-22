package org.confluence.mod.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.item.summon.SummonerWeaponItem;
import org.mesdag.portlib.event.client.PortAddAttributeTooltipsEvent;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

// todo 合并四职业属性的tooltip
public final class ModAttributeUtils {
    public static void addPrefixTooltips(PortAddAttributeTooltipsEvent event) {
        PrefixComponent prefix = PrefixUtils.getPrefix(event.getStack());
        if (prefix == null) return;
        if (prefix.type() == PrefixType.MAGIC) {
            if (prefix.manaCost() != 0.0) {
                boolean positive = prefix.manaCost() > 0.0;
                String format = ATTRIBUTE_MODIFIER_FORMAT.format(prefix.manaCost() * (positive ? 100 : -100));
                MutableComponent component = Component.translatable("prefix.confluence.tooltip.mana_cost");
                if (event.getContext().flag().isAdvanced()/* && ForgeConfig.COMMON.attributeAdvancedTooltipDebugInfo.get()*/) {
                    String valueStr = ATTRIBUTE_MODIFIER_FORMAT.format(1 + prefix.manaCost());
                    component.append(Component.literal(" [x" + valueStr + "]").withStyle(ChatFormatting.GRAY));
                }
                event.addTooltipLines(Component.translatable("prefix.confluence.tooltip." + (positive ? "plus" : "take"), format, component)
                        .withStyle(positive ? ChatFormatting.RED : ChatFormatting.BLUE));
            }
        } else if (prefix.type() == PrefixType.ACCESSORY) {
            if (prefix.additionalMana() > 0) {
                MutableComponent component = Component.translatable("prefix.confluence.tooltip.additional_mana");
                if (event.getContext().flag().isAdvanced()/* && ForgeConfig.COMMON.attributeAdvancedTooltipDebugInfo.get()*/) {
                    component.append(Component.literal(" [+" + prefix.additionalMana() + "]").withStyle(ChatFormatting.GRAY));
                }
                event.addTooltipLines(Component.translatable("prefix.confluence.tooltip.add", prefix.additionalMana(), component)
                        .withStyle(ChatFormatting.BLUE));
            }
        }
        if (event.getStack().getItem() instanceof SummonerWeaponItem<?>) {
            ModPrefix modPrefix = prefix.type() == PrefixType.SUMMON ? ModPrefix.Summon.VALUES.get(prefix.name()) : ModPrefix.Universal.VALUES.get(prefix.name());
            // 召唤武器的伤害与击退，两类词缀都作用于召唤物
            if (modPrefix instanceof ModPrefix.Summon summon) {
                if (summon.armorPenetration() != 0)
                    event.addTooltipLines(Component.translatable("prefix.confluence.tooltip.add", ATTRIBUTE_MODIFIER_FORMAT.format(summon.armorPenetration()), Component.translatable("prefix.confluence.tooltip.armor_penetration")).withStyle(ChatFormatting.BLUE));
                if (summon.attackDamage() != 0)
                    event.addTooltipLines(Component.translatable("prefix.confluence.tooltip." + (summon.attackDamage() > 0 ? "plus" : "take"), ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(summon.attackDamage()) * 100), Component.translatable("item.confluence.tooltip.damage")).withStyle(summon.attackDamage() > 0 ? ChatFormatting.BLUE : ChatFormatting.RED));
                if (summon.knockBack() != 0)
                    event.addTooltipLines(Component.translatable("prefix.confluence.tooltip." + (summon.knockBack() > 0 ? "plus" : "take"), ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(summon.knockBack()) * 100), Component.translatable("item.confluence.tooltip.knockback")).withStyle(summon.knockBack() > 0 ? ChatFormatting.BLUE : ChatFormatting.RED));
            } else if (modPrefix instanceof ModPrefix.Universal universal) {
                if (universal.attackDamage() != 0)
                    event.addTooltipLines(Component.translatable("prefix.confluence.tooltip." + (universal.attackDamage() > 0 ? "plus" : "take"), ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(universal.attackDamage()) * 100), Component.translatable("item.confluence.tooltip.damage")).withStyle(universal.attackDamage() > 0 ? ChatFormatting.BLUE : ChatFormatting.RED));
                if (universal.knockBack() != 0)
                    event.addTooltipLines(Component.translatable("prefix.confluence.tooltip." + (universal.knockBack() > 0 ? "plus" : "take"), ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(universal.knockBack()) * 100), Component.translatable("item.confluence.tooltip.knockback")).withStyle(universal.knockBack() > 0 ? ChatFormatting.BLUE : ChatFormatting.RED));
            }
        }
    }
}
