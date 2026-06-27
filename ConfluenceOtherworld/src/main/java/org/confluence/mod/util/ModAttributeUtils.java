package org.confluence.mod.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
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
    }
}
