package org.confluence.mod.mixin.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SnowballItem;
import org.confluence.lib.util.LibUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SnowballItem.class)
public abstract class SnowballItemMixin {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;<init>(Lnet/minecraft/world/item/Item$Properties;)V"))
    private static Item.Properties maxStackSize(Item.Properties properties) {
        int original = properties.maxStackSize;
        if (original == 16) {
            properties.stacksTo(LibUtils.MAX_STACK_SIZE);
        }
        return properties;
    }
}
