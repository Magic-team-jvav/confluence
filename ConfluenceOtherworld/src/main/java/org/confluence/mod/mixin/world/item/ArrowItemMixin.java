package org.confluence.mod.mixin.world.item;

import net.minecraft.core.component.DataComponents;
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
        int value = LibUtils.MAX_STACK_SIZE;
        if (properties.components != null && !properties.components.map.containsKey(DataComponents.DAMAGE)) {
            int size = (int) properties.components.map.getOrDefault(DataComponents.MAX_STACK_SIZE, 1);
            if (size > value) value = size;
        }
        return properties.stacksTo(value);
    }
}
