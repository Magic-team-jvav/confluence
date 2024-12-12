package org.confluence.mod.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.confluence.terra_curio.common.init.TCTags;
import org.jetbrains.annotations.Nullable;

public final class PrefixUtils {
    private static final float MERCY = 2.0F / 3.0F;

    public static boolean canInit(ItemStack itemStack) {
        if (PrefixUtils.getPrefix(itemStack) != null) return false;
        return itemStack.is(Tags.Items.MELEE_WEAPON_TOOLS) ||
                itemStack.is(Tags.Items.MINING_TOOL_TOOLS) ||
                itemStack.is(Tags.Items.RANGED_WEAPON_TOOLS) ||
                itemStack.is(TCTags.ACCESSORY) ||
                itemStack.is(ModTags.Items.MANA_WEAPON) ||
                itemStack.is(ModTags.Items.PREFIX_UNIVERSAL_ONLY);
    }

    public static void initPrefix(RandomSource random, ItemStack itemStack) {
        if (random.nextFloat() < 0.75F) {
            if (itemStack.is(ModTags.Items.PREFIX_UNIVERSAL_ONLY)) {
                createWithMercy(random, itemStack, PrefixType.UNIVERSAL);
            } else if (itemStack.is(Tags.Items.MELEE_WEAPON_TOOLS) || itemStack.is(Tags.Items.MINING_TOOL_TOOLS)) {
                createWithMercy(random, itemStack, PrefixType.MELEE);
            } else if (itemStack.is(Tags.Items.RANGED_WEAPON_TOOLS)) { // todo 三叉戟会从背包里飞出去，而吃不到加成
                createWithMercy(random, itemStack, PrefixType.RANGED);
            } else if (itemStack.is(ModTags.Items.MANA_WEAPON)) {
                createWithMercy(random, itemStack, PrefixType.MAGIC);
            } else if (itemStack.is(TCTags.ACCESSORY)) {
                createWithMercy(random, itemStack, PrefixType.ACCESSORY);
            }
        } else {
            unknown(itemStack);
        }
    }

    public static void createWithMercy(RandomSource random, ItemStack itemStack, PrefixType prefixType) {
        ModPrefix modPrefix = prefixType.randomPrefix(random);
        if (modPrefix.canBeMercy() && random.nextFloat() < MERCY) {
            unknown(itemStack);
        } else {
            itemStack.set(ModDataComponentTypes.PREFIX, modPrefix.createComponent(prefixType));
        }
    }

    public static @Nullable PrefixComponent getPrefix(ItemStack itemStack) {
        return itemStack.get(ModDataComponentTypes.PREFIX);
    }

    public static void random(RandomSource random, ItemStack itemStack, PrefixType prefixType) {
        itemStack.set(ModDataComponentTypes.PREFIX, prefixType.randomPrefix(random).createComponent(prefixType));
    }

    public static void unknown(ItemStack itemStack) {
        itemStack.set(ModDataComponentTypes.PREFIX, new PrefixComponent(PrefixType.UNKNOWN, "unknown", AttributeModifiersValue.EMPTY, 0.0F, 0, 0, 0.0F));
    }

    public static int calculateManaCost(ItemStack itemStack, int amount) {
        PrefixComponent prefix = itemStack.get(ModDataComponentTypes.PREFIX);
        if (prefix != null) {
            return (int) (amount * (1.0F + prefix.manaCost()));
        }
        return amount;
    }
}
