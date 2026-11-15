package org.confluence.mod.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.client.event.AddAttributeTooltipsEvent;
import net.neoforged.neoforge.common.NeoForgeConfig;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.confluence.mod.common.init.ModTags;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

/// 追加不属于伤害、暴击率和攻速的词缀属性提示。
public class ModAttributeUtils {
    public static void addPrefixTooltips(AddAttributeTooltipsEvent event) {
        PrefixComponent prefix = PrefixUtils.getPrefix(event.getStack());
        if (prefix == null) return;
        if (prefix.type() == PrefixType.SOUL) {
            soul(event, prefix);
        } else if (prefix.type() == PrefixType.MAGIC) {
            magic(event, prefix);
        } else if (prefix.type() == PrefixType.ACCESSORY) {
            accessory(event, prefix);
        }
        if (event.getStack().is(ModTags.Items.SUMMONER_WEAPON)) {
            summoner(event, prefix);
        }
    }

    private static void summoner(AddAttributeTooltipsEvent event, PrefixComponent prefix) {
        ModPrefix modPrefix = prefix.type() == PrefixType.SUMMON ? ModPrefix.Summon.VALUES.get(prefix.name()) : ModPrefix.Universal.VALUES.get(prefix.name());
        // 召唤武器的伤害与击退，两类词缀都作用于召唤物
        if (modPrefix instanceof ModPrefix.Summon summon) {
            if (summon.knockBack() != 0)
                event.addTooltipLines(Component.translatable("prefix.confluence.tooltip." + (summon.knockBack() > 0 ? "plus" : "take"), ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(summon.knockBack()) * 100), Component.translatable("item.confluence.tooltip.knockback")).withStyle(summon.knockBack() > 0 ? ChatFormatting.BLUE : ChatFormatting.RED));
            if (summon.tagDamage() != 0)
                event.addTooltipLines(Component.translatable("prefix.confluence.tooltip.add", ATTRIBUTE_MODIFIER_FORMAT.format(summon.tagDamage()), Component.translatable("prefix.confluence.tooltip.summon_tag_damage")).withStyle(ChatFormatting.BLUE));
            if (summon.armorPenetration() != 0)
                event.addTooltipLines(Component.translatable("prefix.confluence.tooltip.add", ATTRIBUTE_MODIFIER_FORMAT.format(summon.armorPenetration()), Component.translatable("prefix.confluence.tooltip.armor_penetration")).withStyle(ChatFormatting.BLUE));
            return;
        }
        if (modPrefix instanceof ModPrefix.Universal universal) {
            if (universal.knockBack() != 0)
                event.addTooltipLines(Component.translatable("prefix.confluence.tooltip." + (universal.knockBack() > 0 ? "plus" : "take"), ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(universal.knockBack()) * 100), Component.translatable("item.confluence.tooltip.knockback")).withStyle(universal.knockBack() > 0 ? ChatFormatting.BLUE : ChatFormatting.RED));
        }
    }

    private static void accessory(AddAttributeTooltipsEvent event, PrefixComponent prefix) {
        if (prefix.name().equals(ModPrefix.Accessory.HAUNTING.name())) {
            event.addTooltipLines(Component.translatable("prefix.confluence.tooltip.plus",
                    ATTRIBUTE_MODIFIER_FORMAT.format(SoulSkillParameters.HAUNTING_SOUL_CHANCE * 100),
                    Component.translatable("prefix.confluence.tooltip.soul_chance")).withStyle(ChatFormatting.BLUE));
        }
        if (prefix.additionalMana() > 0) {
            MutableComponent component = Component.translatable("prefix.confluence.tooltip.additional_mana");
            if (event.getContext().flag().isAdvanced() && NeoForgeConfig.COMMON.attributeAdvancedTooltipDebugInfo.get()) {
                component.append(Component.literal(" [+" + prefix.additionalMana() + "]").withStyle(ChatFormatting.GRAY));
            }
            event.addTooltipLines(Component.translatable("prefix.confluence.tooltip.add", prefix.additionalMana(), component)
                    .withStyle(ChatFormatting.BLUE));
        }
    }

    private static void magic(AddAttributeTooltipsEvent event, PrefixComponent prefix) {
        if (prefix.manaCost() == 0.0) {
            return;
        }
        boolean positive = prefix.manaCost() > 0.0;
        String format = ATTRIBUTE_MODIFIER_FORMAT.format(prefix.manaCost() * (positive ? 100 : -100));
        MutableComponent component = Component.translatable("prefix.confluence.tooltip.mana_cost");
        if (event.getContext().flag().isAdvanced() && NeoForgeConfig.COMMON.attributeAdvancedTooltipDebugInfo.get()) {
            String valueStr = ATTRIBUTE_MODIFIER_FORMAT.format(1 + prefix.manaCost());
            component.append(Component.literal(" [x" + valueStr + "]").withStyle(ChatFormatting.GRAY));
        }
        event.addTooltipLines(Component.translatable("prefix.confluence.tooltip." + (positive ? "plus" : "take"), format, component)
                .withStyle(positive ? ChatFormatting.RED : ChatFormatting.BLUE));
    }

    private static void soul(AddAttributeTooltipsEvent event, PrefixComponent prefix) {
        ModPrefix.Soul soul = ModPrefix.Soul.VALUES.get(prefix.name());
        if (soul == null || soul.soulCost() == 0) {
            return;
        }
        event.addTooltipLines(Component.translatable("prefix.confluence.tooltip.soul_cost",
                (soul.soulCost() > 0 ? "+" : "") + soul.soulCost())
                .withStyle(soul.soulCost() > 0 ? ChatFormatting.RED : ChatFormatting.BLUE));
    }
}
