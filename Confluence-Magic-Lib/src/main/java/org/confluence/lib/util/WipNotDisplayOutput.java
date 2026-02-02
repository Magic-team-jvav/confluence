package org.confluence.lib.util;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.LibTags;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class WipNotDisplayOutput implements CreativeModeTab.Output {
    private static boolean forceAllow = LibUtils.isDev();
    private final CreativeModeTab.Output delegate;

    public WipNotDisplayOutput(CreativeModeTab.Output delegate) {
        this.delegate = delegate;
    }

    @Override
    public void accept(ItemStack stack, CreativeModeTab.TabVisibility tabVisibility) {
        if (displayable(stack)) {
            delegate.accept(stack, tabVisibility);
        }
    }

    public static boolean displayable(ItemStack stack) {
        return forceAllow || !stack.is(LibTags.Items.WIP);
    }

    public static void forceAllow() {
        forceAllow = true;
    }
}
