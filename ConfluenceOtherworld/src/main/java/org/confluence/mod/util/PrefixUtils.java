package org.confluence.mod.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
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
import org.confluence.terra_curio.common.init.TCTags;
import org.confluence.terra_curio.util.TCUtils;
import org.confluence.terra_guns.common.init.TGTags;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.mixed.IPlayer;
import org.jetbrains.annotations.Nullable;

public final class PrefixUtils {
    private static final float MERCY = 2.0F / 3.0F;

    public static boolean canInit(ItemStack itemStack) {
        if (PrefixUtils.getPrefix(itemStack) != null) return false;
        return couldReforge(itemStack);
    }

    public static boolean couldReforge(ItemStack itemStack) {
        return !itemStack.is(ModTags.Items.UNABLE_TO_APPLY_PREFIX) &&
                (itemStack.is(Tags.Items.MELEE_WEAPON_TOOLS) ||
                        itemStack.is(Tags.Items.MINING_TOOL_TOOLS) ||
                        itemStack.is(Tags.Items.RANGED_WEAPON_TOOLS) ||
                        itemStack.is(TCTags.ACCESSORY) ||
                        itemStack.is(ModTags.Items.MANA_WEAPON) ||
                        itemStack.is(TGTags.GUN) ||
                        itemStack.is(ModTags.Items.PREFIX_UNIVERSAL_ONLY));
    }

    public static @Nullable PrefixComponent initPrefix(RandomSource random, ItemStack itemStack) {
        if (random.nextFloat() < 0.75F) {
            PrefixType prefixType = getPrefixType(itemStack);
            if (prefixType != PrefixType.UNKNOWN) {
                return createWithMercy(random, itemStack, prefixType);
            }
        } else {
            unknown(itemStack);
        }
        return null;
    }

    public static @Nullable PrefixComponent best(RandomSource random, ItemStack itemStack) {
        PrefixType prefixType = getPrefixType(itemStack);
        if (prefixType != PrefixType.UNKNOWN) {
            ModPrefix modPrefix = prefixType.bestPrefix(random, itemStack);
            if (modPrefix == null) {
                unknown(itemStack);
            } else {
                return setAndUpdate(itemStack, prefixType, modPrefix);
            }
        }
        return null;
    }

    public static PrefixType getPrefixType(ItemStack itemStack) {
        if (itemStack.is(ModTags.Items.PREFIX_UNIVERSAL_ONLY)) {
            return PrefixType.UNIVERSAL;
        } else if (itemStack.is(Tags.Items.MELEE_WEAPON_TOOLS) || itemStack.is(Tags.Items.MINING_TOOL_TOOLS)) {
            return PrefixType.MELEE;
        } else if (itemStack.is(Tags.Items.RANGED_WEAPON_TOOLS) || itemStack.is(TGTags.GUN)) { // todo 三叉戟会从背包里飞出去，而吃不到加成
            return PrefixType.RANGED;
        } else if (itemStack.is(ModTags.Items.MANA_WEAPON) || itemStack.is(ModTags.Items.SUMMONER_WEAPON)) {
            return PrefixType.MAGIC;
        } else if (itemStack.is(TCTags.ACCESSORY)) {
            return PrefixType.ACCESSORY;
        }
        return PrefixType.UNKNOWN;
    }

    public static @Nullable PrefixComponent createWithMercy(RandomSource random, ItemStack itemStack, PrefixType prefixType) {
        ModPrefix modPrefix = prefixType.randomPrefix(random);
        if (modPrefix.canBeMercy() && random.nextFloat() < MERCY) {
            unknown(itemStack);
        } else {
            return setAndUpdate(itemStack, prefixType, modPrefix);
        }
        return null;
    }

    public static @Nullable PrefixComponent getPrefix(ItemStack itemStack) {
        return itemStack.isEmpty() ? null : itemStack.get(ModDataComponentTypes.PREFIX);
    }

    public static @Nullable PrefixComponent random(RandomSource random, ItemStack itemStack) {
        PrefixType prefixType = getPrefixType(itemStack);
        if (prefixType != PrefixType.UNKNOWN) {
            return random(random, itemStack, prefixType);
        }
        return null;
    }

    public static @Nullable PrefixComponent random(RandomSource random, ItemStack itemStack, PrefixType prefixType) {
        return setAndUpdate(itemStack, prefixType, prefixType.randomPrefix(random));
    }

    public static @Nullable PrefixComponent setAndUpdate(ItemStack itemStack, PrefixType prefixType, ModPrefix modPrefix) {
        if (prefixType == null) return null;
        PrefixComponent prefix = modPrefix.createComponent(prefixType, itemStack);
        itemStack.set(ModDataComponentTypes.PREFIX, prefix);
        int num1 = ModPrefix.ID_MAP.inverse().getOrDefault(modPrefix, 0);
        float num2 = 1;
        float num3 = 1;
        float num4 = 1;
        float num5 = 1;
        float num6 = 1;
        float num7 = 1;
        float num8 = 0;

        switch (num1) {
            case 1:
                num5 = 1.12f;
                break;
            case 2:
                num5 = 1.18f;
                break;
            case 3:
                num2 = 1.05f;
                num8 = 2;
                num5 = 1.05f;
                break;
            case 4:
                num2 = 1.1f;
                num5 = 1.1f;
                num3 = 1.1f;
                break;
            case 5:
                num2 = 1.15f;
                break;
            case 6, 53:
                num2 = 1.1f;
                break;
            case 7:
                num5 = 0.82f;
                break;
            case 8:
                num3 = 0.85f;
                num2 = 0.85f;
                num5 = 0.87f;
                break;
            case 9:
                num5 = 0.9f;
                break;
            case 10, 40:
                num2 = 0.85f;
                break;
            case 11:
                num4 = 1.1f;
                num3 = 0.9f;
                num5 = 0.9f;
                break;
            case 12:
                num3 = 1.1f;
                num2 = 1.05f;
                num5 = 1.1f;
                num4 = 1.15f;
                break;
            case 13:
                num3 = 0.8f;
                num2 = 0.9f;
                num5 = 1.1f;
                break;
            case 14:
                num3 = 1.15f;
                num4 = 1.1f;
                break;
            case 15:
                num3 = 0.9f;
                num4 = 0.85f;
                break;
            case 16:
                num2 = 1.1f;
                num8 = 3;
                break;
            case 17:
                num4 = 0.85f;
                num6 = 1.1f;
                break;
            case 18:
                num4 = 0.9f;
                num6 = 1.15f;
                break;
            case 19:
                num3 = 1.15f;
                num6 = 1.05f;
                break;
            case 20:
                num3 = 1.05f;
                num6 = 1.05f;
                num2 = 1.1f;
                num4 = 0.95f;
                num8 = 2;
                break;
            case 21:
                num3 = 1.15f;
                num2 = 1.1f;
                break;
            case 22:
                num3 = 0.9f;
                num6 = 0.9f;
                num2 = 0.85f;
                break;
            case 23:
                num4 = 1.15f;
                num6 = 0.9f;
                break;
            case 24:
                num4 = 1.1f;
                num3 = 0.8f;
                break;
            case 25:
                num4 = 1.1f;
                num2 = 1.15f;
                num8 = 1;
                break;
            case 26:
                num7 = 0.85f;
                num2 = 1.1f;
                break;
            case 27:
                num7 = 0.85f;
                break;
            case 28:
                num7 = 0.85f;
                num2 = 1.15f;
                num3 = 1.05f;
                break;
            case 29:
                num7 = 1.1f;
                break;
            case 30:
                num7 = 1.2f;
                num2 = 0.9f;
                break;
            case 31:
                num3 = 0.9f;
                num2 = 0.9f;
                break;
            case 32:
                num7 = 1.15f;
                num2 = 1.1f;
                break;
            case 33:
                num7 = 1.1f;
                num3 = 1.1f;
                num4 = 0.9f;
                break;
            case 34:
                num7 = 0.9f;
                num3 = 1.1f;
                num4 = 1.1f;
                num2 = 1.1f;
                break;
            case 35:
                num7 = 1.2f;
                num2 = 1.15f;
                num3 = 1.15f;
                break;
            case 36:
                num8 = 3;
                break;
            case 37:
                num2 = 1.1f;
                num8 = 3;
                num3 = 1.1f;
                break;
            case 38, 54:
                num3 = 1.15f;
                break;
            case 39:
                num2 = 0.7f;
                num3 = 0.8f;
                break;
            case 41:
                num3 = 0.85f;
                num2 = 0.9f;
                break;
            case 42:
                num4 = 0.9f;
                break;
            case 43:
                num2 = 1.1f;
                num4 = 0.9f;
                break;
            case 44:
                num4 = 0.9f;
                num8 = 3;
                break;
            case 45:
                num4 = 0.95f;
                break;
            case 46:
                num8 = 3;
                num4 = 0.94f;
                num2 = 1.07f;
                break;
            case 47:
                num4 = 1.15f;
                break;
            case 48:
                num4 = 1.2f;
                break;
            case 49:
                num4 = 1.08f;
                break;
            case 50:
                num2 = 0.8f;
                num4 = 1.15f;
                break;
            case 51:
                num3 = 0.9f;
                num4 = 0.9f;
                num2 = 1.05f;
                num8 = 2;
                break;
            case 52:
                num7 = 0.9f;
                num2 = 0.9f;
                num4 = 0.9f;
                break;
            case 55:
                num3 = 1.15f;
                num2 = 1.05f;
                break;
            case 56:
                num3 = 0.8f;
                break;
            case 57:
                num3 = 0.9f;
                num2 = 1.18f;
                break;
            case 58:
                num4 = 0.85f;
                num2 = 0.85f;
                break;
            case 59:
                num3 = 1.15f;
                num2 = 1.15f;
                num8 = 5;
                break;
            case 60:
                num2 = 1.15f;
                num8 = 5;
                break;
            case 61:
                num8 = 5;
                break;
            case 81:
                num3 = 1.15f;
                num2 = 1.15f;
                num8 = 5;
                num4 = 0.9f;
                num5 = 1.1f;
                break;
            case 82:
                num3 = 1.15f;
                num2 = 1.15f;
                num8 = 5;
                num4 = 0.9f;
                num6 = 1.1f;
                break;
            case 83:
                num3 = 1.15f;
                num2 = 1.15f;
                num8 = 5;
                num4 = 0.9f;
                num7 = 0.9f;
                break;
            case 84:
                num3 = 1.17f;
                num2 = 1.17f;
                num8 = 8;
                break;
        }

        float num14 = (float) (1.0 * (double) num2 * (2.0 - (double) num4) * (2.0 - (double) num7) * (double) num5 * (double) num3 * (double) num6 * (1.0 + (double) num8 * 0.0199999995529652));
        if (num1 == 62 || num1 == 69 || num1 == 73 || num1 == 77) num14 *= 1.05f;
        if (num1 == 63 || num1 == 70 || num1 == 74 || num1 == 78 || num1 == 67) num14 *= 1.1f;
        if (num1 == 64 || num1 == 71 || num1 == 75 || num1 == 79 || num1 == 66) num14 *= 1.15f;
        if (num1 == 65 || num1 == 72 || num1 == 76 || num1 == 80 || num1 == 68) num14 *= 1.2f;

        int rarity = ModRarity.ID_MAP.inverse().getOrDefault(ModRarity.getRarity(itemStack, true), 0);
        if (rarity > -11) {
            if (rarity == -10) rarity = 0;
            else if (rarity == -9) rarity = 2;
            else if (rarity == -8) rarity = 4;
            else if (rarity == -7) rarity = 6;

            if ((double) num14 >= 1.2) rarity += 2;
            else if ((double) num14 >= 1.05) ++rarity;
            else if ((double) num14 <= 0.8) rarity -= 2;
            else if ((double) num14 <= 0.95) --rarity;
            if (rarity < -1) rarity = -1;
            else if (rarity > 11) rarity = 11;
        }
        itemStack.set(ConfluenceMagicLib.MOD_RARITY, ModRarity.ID_MAP.getOrDefault(rarity, ModRarity.WHITE));
        itemStack.set(ModDataComponentTypes.VALUE, new ValueComponent((int) (ValueComponent.getValue(itemStack, 50, true) * num14 * num14)));
        return prefix;
    }

    public static void unknown(ItemStack itemStack) {
        itemStack.set(ModDataComponentTypes.PREFIX, new PrefixComponent(PrefixType.UNKNOWN, "unknown", AttributeModifiersValue.EMPTY, 0.0F, 0, 0, 0.0F));
    }

    public static float calculateManaCost(ItemStack itemStack, float amount) {
        PrefixComponent prefix = itemStack.get(ModDataComponentTypes.PREFIX);
        if (prefix != null) return amount * (1.0F + prefix.manaCost());
        return amount;
    }

    public static int getReforgeCost(Player player, ItemStack itemStack) {
        int price = ValueComponent.getValue(itemStack, 5000);
        if (TCUtils.getAccessoriesValue(player, AccessoryItems.SPECIAL$PRICE) > 0) {
            price = (int) ((double) price * 0.8);
        }
        ITradeHolder holder = ((IPlayer) player).terra_entity$getTradeHolder();
        float priceAdjustment = 1.0F;
        if (holder != null && holder.getMood() != null) {
            priceAdjustment = 100.0F / holder.getMood().getValue();
        }
        return (int) (price * priceAdjustment / 3);
    }
}
