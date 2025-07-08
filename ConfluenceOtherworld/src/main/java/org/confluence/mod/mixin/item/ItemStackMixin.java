package org.confluence.mod.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.mixed.Immunity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemStack.class, priority = 1100)
public abstract class ItemStackMixin implements Immunity, SelfGetter<ItemStack> {
    @Shadow
    public abstract Item getItem();

    @Override
    public Type confluence$getImmunityType() {
        return Type.LOCAL;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        if (getItem() instanceof Immunity immunity) {
            return immunity.confluence$getImmunityDuration(damageSource);
        }
        return Immunity.super.confluence$getImmunityDuration(damageSource);
    }

    @WrapOperation(method = "lambda$static$3", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ExtraCodecs;intRange(II)Lcom/mojang/serialization/Codec;"))
    private static Codec<Integer> modify(int min, int max, Operation<Codec<Integer>> original) {
        return original.call(min, LibUtils.getMaxStackSize(max));
    }

    @ModifyExpressionValue(method = "canBeHurtBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z"))
    private boolean fireResistant(boolean original) {
        if (original) return true;
        ModRarity rarity = confluence$self().get(ConfluenceMagicLib.MOD_RARITY);
        return rarity != null && rarity != ModRarity.WHITE && rarity != ModRarity.GRAY;
    }
}
