package org.confluence.mod.mixin.world.item;

import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import org.confluence.lib.util.LibUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ArrowItem.class, priority = 1100)
public abstract class ArrowItemMixin {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;<init>(Lnet/minecraft/world/item/Item$Properties;)V"))
    private static Item.Properties maxStack(Item.Properties properties) {
        if (properties.maxStackSize != 1) {
            return properties.stacksTo(Math.max(LibUtils.MAX_STACK_SIZE, properties.maxStackSize));
        }
        return properties;
    }
}
