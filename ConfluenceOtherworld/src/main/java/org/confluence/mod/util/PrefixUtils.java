package org.confluence.mod.util;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.item.soulcerer.SoulWeaponItem;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.Nullable;

public final class PrefixUtils {
    private static final float MERCY = 2.0F / 3.0F;

    public static boolean canInit(ItemStack itemStack) {
        if (getPrefix(itemStack) != null) return false;
        return couldReforge(itemStack);
    }

    public static boolean couldReforge(ItemStack stack) {
        return getPrefixType(stack) != PrefixType.UNKNOWN;
    }

    public static @Nullable PrefixComponent initPrefix(RandomSource random, ItemStack itemStack) {
        if (!(random.nextFloat() < 0.75F)) {
            unknown(itemStack);
            return null;
        }
        PrefixType prefixType = getPrefixType(itemStack);
        if (prefixType != PrefixType.UNKNOWN) {
            return createWithMercy(random, itemStack, prefixType);
        }
        return null;
    }

    public static @Nullable PrefixComponent best(RandomSource random, ItemStack itemStack) {
        PrefixType prefixType = getPrefixType(itemStack);
        if (prefixType == PrefixType.UNKNOWN) {
            return null;
        }
        ModPrefix modPrefix = prefixType.bestPrefix(random, itemStack);
        if (modPrefix != null) {
            return setAndUpdate(itemStack, prefixType, modPrefix);
        }
        unknown(itemStack);
        return null;
    }

    public static PrefixType getPrefixType(ItemStack itemStack) {
        if (itemStack.isEmpty() || itemStack.is(ModTags.Items.UNABLE_TO_APPLY_PREFIX))
            return PrefixType.UNKNOWN;
        if (itemStack.is(ModTags.Items.PREFIX_SOUL_ONLY) && itemStack.getItem() instanceof SoulWeaponItem) {
            return PrefixType.SOUL;
        }
        if (itemStack.is(ModTags.Items.PREFIX_UNIVERSAL_ONLY)) {
            return PrefixType.UNIVERSAL;
        }
        if (itemStack.is(ModTags.Items.PREFIX_MELEE_ONLY)) {
            return PrefixType.MELEE;
        }
        if (itemStack.is(ModTags.Items.PREFIX_RANGED_ONLY)) { // todo 三叉戟会从背包里飞出去，而吃不到加成
            return PrefixType.RANGED;
        }
        if (itemStack.is(ModTags.Items.PREFIX_MAGIC_ONLY)) {
            return PrefixType.MAGIC;
        }
        if (itemStack.is(ModTags.Items.PREFIX_SUMMON_ONLY)) {
            return PrefixType.SUMMON;
        }
        if (itemStack.is(ModTags.Items.PREFIX_ACCESSORY_ONLY)) {
            return PrefixType.ACCESSORY;
        }
        return PrefixType.UNKNOWN;
    }

    public static int calculateUseTime(Player player, int baseTicks) {
        if (baseTicks <= 0) return 0;
        double baseSpeed = player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
        double attackSpeed = player.getAttributeValue(Attributes.ATTACK_SPEED);
        if (baseSpeed <= 0.0 || attackSpeed <= 0.0) {
            return baseTicks;
        }
        return Math.max(1, (int) Math.ceil(baseTicks * baseSpeed / attackSpeed));
    }

    public static @Nullable PrefixComponent createWithMercy(RandomSource random, ItemStack stack, PrefixType type) {
        ModPrefix modPrefix = type.randomPrefix(random, stack);
        if (modPrefix == null || modPrefix.canBeMercy() && random.nextFloat() < MERCY) {
            unknown(stack);
            return null;
        }
        return setAndUpdate(stack, type, modPrefix);
    }

    public static @Nullable PrefixComponent getPrefix(ItemStack stack) {
        return stack.isEmpty() ? null : stack.get(ModDataComponentTypes.PREFIX);
    }

    /// 取实体在指定属性上的加成，并剔除手持物品自身对该属性的贡献。
    public static double attributeWithoutHeldItem(LivingEntity entity, Holder<Attribute> attribute, ItemStack heldItem) {
        double heldValue = heldItemContribution(heldItem, 1, attribute);
        return heldValue > 0 ? entity.getAttributeValue(attribute) / heldValue : entity.getAttributeValue(attribute);
    }

    /// 以基准值反推手持物品在该属性上的贡献，等价于原版对物品修饰符的结算。
    public static double heldItemContribution(ItemStack heldItem, double baseValue, Holder<Attribute> attribute) {
        double value = baseValue;
        PrefixComponent component = getPrefix(heldItem);
        if (component == null) {
            return value;
        }
        for (AttributeModifier modifier : component.modifiers().get().get(attribute)) {
            value += switch (modifier.operation()) {
                case ADD_VALUE -> modifier.amount();
                case ADD_MULTIPLIED_BASE -> modifier.amount() * baseValue;
                case ADD_MULTIPLIED_TOTAL -> modifier.amount() * value;
            };
        }
        return value;
    }

    public static @Nullable PrefixComponent random(RandomSource random, ItemStack stack) {
        PrefixType type = getPrefixType(stack);
        if (type != PrefixType.UNKNOWN) {
            ModPrefix prefix = type.randomPrefix(random, stack);
            if (prefix != null) return setAndUpdate(stack, type, prefix);
        }
        return null;
    }

    public static PrefixComponent setAndUpdate(ItemStack stack, PrefixType type, ModPrefix prefix) {
        PrefixComponent component = prefix.createComponent(type, stack);
        stack.set(ModDataComponentTypes.PREFIX, component);

        ModRarity base = ModRarity.getModRarity(stack, true);
        if (base != null) {
            Integer tier = ModRarity.TIER.inverse().get(base);
            // 专家、大师等独立稀有度没有数字等级，重铸后仍使用物品原色。
            ModRarity rarity = tier == null ? base : ModRarity.TIER.get(Math.max(-1, Math.min(11, tier + prefix.tier())));
            stack.set(ConfluenceMagicLib.MOD_RARITY, rarity);
        }
        int value = ValueComponent.getValue(stack, 50, true);
        float finalValue = value + value * prefix.value();
        stack.set(ModDataComponentTypes.VALUE, new ValueComponent((int) finalValue));
        return component;
    }

    public static void unknown(ItemStack itemStack) {
        itemStack.set(ModDataComponentTypes.PREFIX, new PrefixComponent(PrefixType.UNKNOWN, "unknown", AttributeModifiersValue.EMPTY, 0.0F));
    }

    public static float calculateManaCost(ItemStack itemStack, float amount) {
        PrefixComponent prefix = itemStack.get(ModDataComponentTypes.PREFIX);
        if (prefix != null) return amount * (1.0F + prefix.manaCost());
        return amount;
    }

    public static int getReforgeCost(Player player, ItemStack itemStack) {
        int price = ValueComponent.getValue(itemStack, 5000);
        if (TCUtils.getValue(player, AccessoryItems.SPECIAL$PRICE) > 0) {
            price = (int) ((double) price * 0.8);
        }
        // todo 心情
        return price / 3;
    }
}
