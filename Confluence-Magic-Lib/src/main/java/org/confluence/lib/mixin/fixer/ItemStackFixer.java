package org.confluence.lib.mixin.fixer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.data.IdFixer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackFixer {
    @ModifyExpressionValue(method = "<clinit>", at= @At(value = "INVOKE", target = "Lnet/minecraft/core/DefaultedRegistry;holderByNameCodec()Lcom/mojang/serialization/Codec;"))
    private static Codec<Holder<Item>> fixItemName(Codec<Holder<Item>> original) {
        return IdFixer.fixItemName(original);
    }
}
