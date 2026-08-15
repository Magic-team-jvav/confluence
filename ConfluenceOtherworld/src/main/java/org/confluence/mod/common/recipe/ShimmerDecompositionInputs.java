package org.confluence.mod.common.recipe;

import net.minecraft.Util;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModTags;
import org.jetbrains.annotations.Nullable;

public final class ShimmerDecompositionInputs {
    private ShimmerDecompositionInputs() {}

    /**
     * 从配方原料候选中选择微光分解的返还物。
     *
     * <p>默认逻辑保持旧行为：从候选中随机抽取，并在肉后未开启时回退到第一个非肉后物品。
     * 开启“首个标签物品”配置后，则直接按候选顺序选择第一个当前世界阶段允许的物品，
     * 用于还原泰拉中“同一标签统一分解成默认材料”的表现。</p>
     */
    public static @Nullable ItemStack choose(ItemStack[] itemStacks, boolean hardmode, RandomSource random, boolean preferFirst) {
        if (itemStacks.length == 0) return null;
        if (preferFirst) {
            return firstAllowed(itemStacks, hardmode);
        }
        ItemStack input = Util.getRandom(itemStacks, random);
        if (!hardmode && input.is(ModTags.Items.HARDMODE)) {
            return firstAllowed(itemStacks, false);
        }
        return input;
    }

    /**
     * 按候选顺序寻找当前阶段可用的第一个物品。
     */
    private static @Nullable ItemStack firstAllowed(ItemStack[] itemStacks, boolean hardmode) {
        for (ItemStack itemStack : itemStacks) {
            if (hardmode || !itemStack.is(ModTags.Items.HARDMODE)) {
                return itemStack;
            }
        }
        return null;
    }
}
