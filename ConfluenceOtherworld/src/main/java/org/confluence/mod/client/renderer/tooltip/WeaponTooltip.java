package org.confluence.mod.client.renderer.tooltip;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.api.item.IWeaponTooltip;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.boomerang.BoomerangItem;
import org.confluence.mod.common.item.summon.SummonerWeaponItem;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.*;

/// 汇流武器共用原版属性提示；不替换名称、附魔、物品价值或其他模组的提示。
public final class WeaponTooltip {
    private WeaponTooltip() {}

    /// 从真实属性修饰符计算悬停武器的数值，不临时装备物品或修改玩家属性。
    public static double attributeValue(ItemStack stack, @Nullable Player player, Attribute attribute, boolean baseStats) {
        AttributeInstance preview = new AttributeInstance(attribute, ignored -> {});
        double defaultBase = attribute == Attributes.ATTACK_DAMAGE ? 1.0 : attribute.getDefaultValue();
        AttributeInstance current = player == null ? null : player.getAttribute(attribute);
        preview.setBaseValue(current != null && !baseStats ? current.getBaseValue() : defaultBase);
        if (player != null && !baseStats) {
            Set<UUID> heldModifiers = new HashSet<>();
            player.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND).get(attribute)
                    .forEach(modifier -> heldModifiers.add(modifier.getId()));
            if (current != null) {
                for (AttributeModifier modifier : current.getModifiers()) {
                    if (!heldModifiers.contains(modifier.getId()))
                        preview.addTransientModifier(modifier);
                }
            }
        }
        for (AttributeModifier modifier : stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(attribute)) {
            preview.removeModifier(modifier.getId());
            preview.addTransientModifier(modifier);
        }
        return preview.getValue();
    }

    /// 只向原版 tooltip 的局部计算提供显示修饰符，绝不改变物品或玩家的实际属性。
    public static Multimap<Attribute, AttributeModifier> tooltipModifiers(ItemStack stack, @Nullable Player player, Multimap<Attribute, AttributeModifier> original) {
        if (!(stack.getItem() instanceof IWeaponTooltip weapon)) return original;
        boolean base = ClientConfigs.weaponTooltipBaseStats;
        Attribute damageAttribute = weapon.getTooltipDamageAttribute();
        Attribute criticalAttribute = LibAttributes.getCriticalChance().value();
        double damage = weapon.getTooltipDamage(stack, attributeValue(stack, player, damageAttribute, base));
        /// 这些伤害源同时属于射弹：实际受伤流程还会应用远程倍率，提示不能漏掉这一层。
        if (damageAttribute == LibAttributes.getMagicDamage().value() || stack.getItem() instanceof BoomerangItem) {
            damage *= attributeValue(stack, player, LibAttributes.getRangedDamage().value(), base);
        }
        double critical = weapon.getTooltipCriticalChance(stack, attributeValue(stack, player, criticalAttribute, base));
        double speed = weapon.getTooltipAttackSpeed(stack, attributeValue(stack, player, Attributes.ATTACK_SPEED, false), player);
        Multimap<Attribute, AttributeModifier> result = LinkedHashMultimap.create();
        /// 原版通过两个固定 UUID 补上玩家基础属性和附魔；这里减去同样的值，避免重复相加。
        double nativeDamage = player == null ? 0 : player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) + EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
        double nativeSpeed = player == null ? 0 : player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
        result.put(damageAttribute, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Weapon tooltip damage", damage - nativeDamage, AttributeModifier.Operation.ADDITION));
        if (critical != 0)
            result.put(criticalAttribute, new AttributeModifier(PortAttributeModifier.rl2uuid(ModItems.BASE_CRITICAL_CHANCE_ID), "Weapon tooltip critical chance", Math.max(0, Math.min(1, critical)), AttributeModifier.Operation.ADDITION));
        result.put(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "Weapon tooltip speed", speed - nativeSpeed, AttributeModifier.Operation.ADDITION));
        for (Map.Entry<Attribute, AttributeModifier> entry : original.entries()) {
            if (entry.getKey() != damageAttribute && entry.getKey() != criticalAttribute && entry.getKey() != Attributes.ATTACK_SPEED)
                result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /// 移动原版已经生成的主手属性段，不另建属性行；暴击率为零时遵从原版省略。
    public static void update(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof SummonerWeaponItem<?> weapon) {
            appendSummonStats(event, weapon);
        } else if (stack.getItem() instanceof IWeaponTooltip) {
            moveMainHandAttributes(event.getToolTip());
        }
    }

    /// 召唤物伤害采用拥有者当前加成和召唤杖词缀，不伪装成装备在玩家主手上的属性。
    private static void appendSummonStats(ItemTooltipEvent event, SummonerWeaponItem<?> weapon) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        double damage = weapon.getSummonDamage(ClientConfigs.weaponTooltipBaseStats ? null : player, stack);
        double critical = attributeValue(stack, player, LibAttributes.getCriticalChance().value(), ClientConfigs.weaponTooltipBaseStats);
        if (damage <= 0) return;
        List<Component> lines = event.getToolTip();
        if (lines.isEmpty()) return;
        List<Component> stats = new ArrayList<>();
        stats.add(Component.empty());
        stats.add(Component.translatable("item.confluence.tooltip.summon_attributes").withStyle(ChatFormatting.GRAY));
        stats.add(Component.translatable("attribute.modifier.equals.0",
                ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(damage), Component.translatable("item.confluence.tooltip.damage")).withStyle(ChatFormatting.DARK_GREEN));
        if (critical > 0) {
            stats.add(Component.translatable("attribute.modifier.plus.1",
                    ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(Math.min(1, critical) * 100),
                    Component.translatable(LibAttributes.getCriticalChance().value().getDescriptionId())).withStyle(ChatFormatting.BLUE));
        }
        if (lines.size() > 1 && !lines.get(1).equals(Component.empty()))
            stats.add(Component.empty());
        lines.addAll(1, stats);
    }

    /// 原版属性行可能包在空格组件的子节点中，整段移动时必须连同包装一起保留。
    static void moveMainHandAttributes(List<Component> lines) {
        if (lines.isEmpty()) return;
        int start = -1;
        for (int i = 1; i < lines.size(); i++) {
            if (hasKey(lines.get(i), "item.modifiers.mainhand")) {
                start = i;
                break;
            }
        }
        if (start < 0) return;
        int end = start + 1;
        while (end < lines.size() && isAttributeLine(lines.get(end))) {
            lines.set(end, alignAttributeLine(lines.get(end)));
            end++;
        }
        if (start > 1 && lines.get(start - 1).equals(Component.empty())) start--;
        List<Component> attributes = new ArrayList<>(lines.subList(start, end));
        lines.subList(start, end).clear();
        if (lines.size() > 1 && !lines.get(1).equals(Component.empty()))
            attributes.add(Component.empty());
        lines.addAll(1, attributes);
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
                && (contents.getKey().startsWith("attribute.modifier.") || contents.getKey().startsWith("portlib.modifier.") || contents.getKey().startsWith("prefix.confluence.tooltip.")))
            return true;
        for (Component sibling : component.getSiblings()) if (isAttributeLine(sibling)) return true;
        return false;
    }

    /// 描述的参数和子组件也统一灰色；不触碰属性段、稀有度名称和价值。
    public static Component grayDescription(Component component) {
        MutableComponent result;
        if (component.getContents() instanceof TranslatableContents contents) {
            Object[] args = contents.getArgs().clone();
            for (int i = 0; i < args.length; i++)
                if (args[i] instanceof Component child) args[i] = grayDescription(child);
            result = Component.translatableWithFallback(contents.getKey(), contents.getFallback(), args);
        } else result = component.plainCopy();
        result.setStyle(component.getStyle().withColor(ChatFormatting.GRAY));
        for (Component sibling : component.getSiblings()) result.append(grayDescription(sibling));
        return result;
    }
}
