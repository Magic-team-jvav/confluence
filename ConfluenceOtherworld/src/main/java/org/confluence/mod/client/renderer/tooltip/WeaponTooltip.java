package org.confluence.mod.client.renderer.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.api.item.IWeaponTooltip;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.item.boomerang.BoomerangItem;
import org.confluence.mod.common.item.summon.SummonerWeaponItem;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// 汇流武器共用原版属性提示；不替换名称、附魔、物品价值或其他模组的提示。
public final class WeaponTooltip {
    private WeaponTooltip() {}

    /// 从真实属性修饰符计算悬停武器的数值，不临时装备物品或修改玩家属性。
    public static double attributeValue(ItemStack stack, @Nullable Player player, Holder<Attribute> attribute, boolean baseStats) {
        AttributeInstance preview = new AttributeInstance(attribute, ignored -> {});
        double defaultBase = attribute == Attributes.ATTACK_DAMAGE ? 1.0 : attribute.value().getDefaultValue();
        AttributeInstance current = player == null ? null : player.getAttribute(attribute);
        preview.setBaseValue(current != null && !baseStats ? current.getBaseValue() : defaultBase);
        if (player != null && !baseStats) {
            Set<ResourceLocation> heldModifiers = new HashSet<>();
            player.getMainHandItem().forEachModifier(EquipmentSlot.MAINHAND, (type, modifier) -> {
                if (type.equals(attribute)) heldModifiers.add(modifier.id());
            });
            if (current != null) {
                for (AttributeModifier modifier : current.getModifiers()) {
                    if (!heldModifiers.contains(modifier.id()))
                        preview.addTransientModifier(modifier);
                }
            }
        }
        stack.forEachModifier(EquipmentSlot.MAINHAND, (type, modifier) -> {
            if (type.equals(attribute)) {
                preview.removeModifier(modifier.id());
                preview.addTransientModifier(modifier);
            }
        });
        return preview.getValue();
    }

    /// 只修改最终提示列表，不接管物品或玩家的真实属性。
    public static void update(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).showInTooltip())
            return;
        if (stack.getItem() instanceof SummonerWeaponItem<?> weapon) {
            appendSummonStats(event, weapon);
        } else if (stack.getItem() instanceof IWeaponTooltip weapon) {
            replaceMainHandAttributes(event.getToolTip(), stack, event.getEntity(), weapon, event.getFlags());
        }
    }

    /// 保留原生主手段中的其他属性，只统一伤害、暴击率和攻速。
    private static void replaceMainHandAttributes(List<Component> lines, ItemStack stack, @Nullable Player player, IWeaponTooltip weapon, TooltipFlag flag) {
        Holder<Attribute> damageAttribute = weapon.getTooltipDamageAttribute();
        Holder<Attribute> criticalAttribute = LibAttributes.getCriticalChance();
        boolean base = ClientConfigs.weaponTooltipBaseStats;
        double damage = weapon.getTooltipDamage(stack, attributeValue(stack, player, damageAttribute, base));
        if (damageAttribute == LibAttributes.getMagicDamage() || stack.getItem() instanceof BoomerangItem) {
            damage *= attributeValue(stack, player, LibAttributes.getRangedDamage(), base);
        }
        double critical = weapon.getTooltipCriticalChance(stack, attributeValue(stack, player, criticalAttribute, base));
        double speed = weapon.getTooltipAttackSpeed(stack, attributeValue(stack, player, Attributes.ATTACK_SPEED, false), player);
        List<Component> stats = new ArrayList<>();
        stats.add(Component.empty());
        stats.add(Component.translatable("item.modifiers.mainhand").withStyle(ChatFormatting.GRAY));
        double damageBase = player == null ? 0 : player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        stats.add(damageAttribute.value().toBaseComponent(damage, damageBase, false, flag).withStyle(ChatFormatting.DARK_GREEN));
        if (critical > 0) {
            stats.add(Component.translatable("attribute.modifier.plus.1", ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(Math.min(1, critical) * 100),
                    Component.translatable(criticalAttribute.value().getDescriptionId())).withStyle(ChatFormatting.BLUE));
        }
        double speedBase = player == null ? 0 : player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
        stats.add(Attributes.ATTACK_SPEED.value().toBaseComponent(speed, speedBase, false, flag).withStyle(ChatFormatting.DARK_GREEN));

        List<AttributeModifier> modifiers = new ArrayList<>();
        stack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> modifiers.add(modifier));
        boolean hasModifiers = !modifiers.isEmpty();
        setMainHandAttributes(lines, stats, Set.of(
                damageAttribute.value().getDescriptionId(),
                criticalAttribute.value().getDescriptionId(),
                Attributes.ATTACK_SPEED.value().getDescriptionId()), hasModifiers);
    }

    /// 按翻译键替换属性行，其他主手属性、其他装备槽、描述和价值保持原样。
    private static void setMainHandAttributes(List<Component> lines, List<Component> stats, Set<String> replacedAttributes, boolean hasModifiers) {
        if (lines.isEmpty()) return;
        int start = -1;
        for (int i = 1; i < lines.size(); i++) {
            if (hasKey(lines.get(i), "item.modifiers.mainhand")) {
                start = i;
                break;
            }
        }
        List<Component> remaining = new ArrayList<>();
        if (start >= 0) {
            int end = start + 1;
            while (end < lines.size() && isAttributeLine(lines.get(end))) {
                Component line = lines.get(end++);
                if (replacedAttributes.stream().noneMatch(key -> hasKey(line, key))) {
                    remaining.add(alignAttributeLine(line));
                }
            }
            if (start > 1 && lines.get(start - 1).equals(Component.empty())) start--;
            lines.subList(start, end).clear();
        } else if (hasModifiers) {
            /// 有主手修饰符却没有原生属性段时，不恢复已被其他事件隐藏的属性段。
            return;
        }
        List<Component> attributes = new ArrayList<>(stats);
        attributes.addAll(remaining);
        if (lines.size() > 1 && !lines.get(1).equals(Component.empty()))
            attributes.add(Component.empty());
        lines.addAll(1, attributes);
    }

    /// 召唤物伤害采用拥有者当前加成和召唤杖词缀，不伪装成装备在玩家主手上的属性。
    private static void appendSummonStats(ItemTooltipEvent event, SummonerWeaponItem<?> weapon) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        double damage = weapon.getSummonDamage(ClientConfigs.weaponTooltipBaseStats ? null : player, stack);
        double critical = attributeValue(stack, player, LibAttributes.getCriticalChance(), ClientConfigs.weaponTooltipBaseStats);
        if (damage <= 0) return;
        List<Component> lines = event.getToolTip();
        if (lines.isEmpty()) return;
        List<Component> stats = new ArrayList<>();
        stats.add(Component.empty());
        stats.add(Component.translatable("item.confluence.tooltip.summon_attributes").withStyle(ChatFormatting.GRAY));
        stats.add(Component.translatable("attribute.modifier.equals.0",
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(damage), Component.translatable("item.confluence.tooltip.damage")).withStyle(ChatFormatting.DARK_GREEN));
        if (critical > 0) {
            stats.add(Component.translatable("attribute.modifier.plus.1",
                    ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(Math.min(1, critical) * 100),
                    Component.translatable(LibAttributes.getCriticalChance().value().getDescriptionId())).withStyle(ChatFormatting.BLUE));
        }
        if (lines.size() > 1 && !lines.get(1).equals(Component.empty()))
            stats.add(Component.empty());
        lines.addAll(1, stats);
    }

    /// 只移除原版属性包装的前导空格，保留翻译、颜色和所有子组件，不改变其他提示段。
    private static Component alignAttributeLine(Component line) {
        if (!(line.getContents() instanceof LiteralContents literal)) return line;
        String text = literal.text().stripLeading();
        if (text.equals(literal.text())) return line;
        MutableComponent aligned = Component.literal(text).setStyle(line.getStyle());
        for (Component sibling : line.getSiblings()) aligned.append(sibling);
        return aligned;
    }

    /// 检查翻译组件结构而非中文/英文显示文本，不依赖玩家语言。
    private static boolean hasKey(Component component, String key) {
        if (component.getContents() instanceof TranslatableContents contents) {
            if (contents.getKey().equals(key)) return true;
            for (Object arg : contents.getArgs())
                if (arg instanceof Component child && hasKey(child, key)) return true;
        }
        for (Component sibling : component.getSiblings()) if (hasKey(sibling, key)) return true;
        return false;
    }

    private static boolean isAttributeLine(Component component) {
        if (component.getContents() instanceof TranslatableContents contents
                && (contents.getKey().startsWith("attribute.modifier.") || contents.getKey().startsWith("neoforge.modifier.") || contents.getKey().startsWith("prefix.confluence.tooltip.")))
            return true;
        for (Component sibling : component.getSiblings()) if (isAttributeLine(sibling)) return true;
        return false;
    }
}
