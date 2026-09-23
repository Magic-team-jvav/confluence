package org.confluence.mod.util;

import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
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
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.Nullable;

public final class PrefixUtils {
    public static boolean canInit(ItemStack stack) {
        if (PrefixUtils.getPrefix(stack) != null) return false;
        return couldReforge(stack);
    }

    public static boolean couldReforge(ItemStack stack) {
        return !stack.is(ModTags.Items.UNABLE_TO_APPLY_PREFIX) &&
                (stack.is(ModTags.Items.SUMMONER_WEAPON) || stack.is(ModTags.Items.PREFIX_UNIVERSAL_ONLY) ||
                        stack.is(ModTags.Items.PREFIX_MELEE_ONLY) ||
                        stack.is(ModTags.Items.PREFIX_RANGED_ONLY) ||
                        stack.is(ModTags.Items.PREFIX_MAGIC_ONLY) ||
                        stack.is(ModTags.Items.PREFIX_SUMMON_ONLY) ||
                        stack.is(ModTags.Items.PREFIX_ACCESSORY_ONLY));
    }

    public static @Nullable PrefixComponent initPrefix(RandomSource random, ItemStack stack) {
        if (random.nextInt(4) == 0) {
            unknown(stack);
        } else {
            PrefixType type = getPrefixType(stack);
            if (type != PrefixType.UNKNOWN) {
                return createWithMercy(random, stack, type);
            }
        }
        return null;
    }

    public static @Nullable PrefixComponent best(RandomSource random, ItemStack stack) {
        PrefixType type = getPrefixType(stack);
        if (type != PrefixType.UNKNOWN) {
            ModPrefix prefix = type.bestPrefix(random, stack);
            if (prefix == null) {
                unknown(stack);
            } else {
                return setAndUpdate(stack, type, prefix);
            }
        }
        return null;
    }

    public static PrefixType getPrefixType(ItemStack stack) {
        if (stack.is(ModTags.Items.PREFIX_UNIVERSAL_ONLY)) {
            return PrefixType.UNIVERSAL;
        } else if (stack.is(ModTags.Items.PREFIX_MELEE_ONLY)) {
            return PrefixType.MELEE;
        } else if (stack.is(ModTags.Items.PREFIX_RANGED_ONLY)) { // todo 三叉戟会从背包里飞出去，而吃不到加成
            return PrefixType.RANGED;
        } else if (stack.is(ModTags.Items.PREFIX_MAGIC_ONLY)) {
            return PrefixType.MAGIC;
        } else if (stack.is(ModTags.Items.PREFIX_SUMMON_ONLY)) {
            return PrefixType.SUMMON;
        } else if (stack.is(ModTags.Items.PREFIX_ACCESSORY_ONLY)) {
            return PrefixType.ACCESSORY;
        }
        return PrefixType.UNKNOWN;
    }

    public static @Nullable PrefixComponent createWithMercy(RandomSource random, ItemStack stack, PrefixType type) {
        ModPrefix prefix = type.randomPrefix(random, stack);
        if (prefix.canBeMercy() && random.nextInt(3) != 0) {
            unknown(stack);
            return null;
        }
        return setAndUpdate(stack, type, prefix);
    }

    public static @Nullable PrefixComponent getPrefix(ItemStack stack) {
        return stack.isEmpty() ? null : stack.get(ModDataComponentTypes.PREFIX);
    }

    /// 取实体在指定属性上的加成，并剔除手持物品自身对该属性的贡献。
    public static double attributeWithoutHeldItem(LivingEntity entity, Holder<Attribute> attribute, ItemStack heldItem) {
        double heldValue = PrefixUtils.heldItemContribution(heldItem, 1, attribute.value());
        return heldValue > 0 ? entity.getAttributeValue(attribute) / heldValue : entity.getAttributeValue(attribute);
    }

    /// 以基准值反推手持物品在该属性上的贡献，等价于原版对物品修饰符的结算。
    public static double heldItemContribution(ItemStack heldItem, double baseValue, Attribute attribute) {
        double value = baseValue;
        PrefixComponent component = getPrefix(heldItem);
        if (component != null) {
            for (AttributeModifier modifier : component.modifiers().get().get(attribute)) {
                value += switch (modifier.getOperation()) {
                    case ADDITION -> modifier.getAmount();
                    case MULTIPLY_BASE -> modifier.getAmount() * baseValue;
                    case MULTIPLY_TOTAL -> modifier.getAmount() * value;
                };
            }
        }
        return value;
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

    public static @Nullable PrefixComponent random(RandomSource random, ItemStack stack) {
        PrefixType type = getPrefixType(stack);
        if (type != PrefixType.UNKNOWN) {
            return setAndUpdate(stack, type, type.randomPrefix(random, stack));
        }
        return null;
    }

    public static PrefixComponent setAndUpdate(ItemStack stack, PrefixType type, ModPrefix prefix) {
        PrefixComponent component = prefix.createComponent(type, stack);
        stack.set(ModDataComponentTypes.PREFIX, component);

        int tier = ModRarity.TIER.inverse().getOrDefault(ModRarity.getRarity(stack, true), -2);
        if (tier > -2) {
            tier += prefix.tier();
            if (tier < -1) tier = -1;
            else if (tier > 11) tier = 11;
        }
        stack.set(ConfluenceMagicLib.MOD_RARITY, ModRarity.TIER.get(tier));
        stack.set(ModDataComponentTypes.VALUE, new ValueComponent((int) (ValueComponent.getValue(stack, 50, true) * Mth.square(prefix.value()))));
        return component;
    }

    public static void unknown(ItemStack stack) {
        stack.set(ModDataComponentTypes.PREFIX, new PrefixComponent(PrefixType.UNKNOWN, "unknown", AttributeModifiersValue.EMPTY, 0, 0));
    }

    public static float calculateManaCost(ItemStack stack, float amount) {
        PrefixComponent prefix = stack.get(ModDataComponentTypes.PREFIX);
        if (prefix != null) return amount * (1.0F + prefix.manaCost());
        return amount;
    }

    public static int getReforgeCost(Player player, ItemStack stack) {
        int price = ValueComponent.getValue(stack, 5000);
        if (TCUtils.getValue(player, AccessoryItems.SPECIAL$PRICE) > 0) {
            price = (int) ((double) price * 0.8);
        }
// todo trade       ITradeHolder holder = ((IPlayer) player).confluence$getTradeHolder();
//        float priceAdjustment = 1.0F;
//        if (holder != null && holder.getMood() != null) {
//            priceAdjustment = 100.0F / holder.getMood().getValue();
//        }
//        return (int) (price * priceAdjustment / 3);
        return price;
    }
}
