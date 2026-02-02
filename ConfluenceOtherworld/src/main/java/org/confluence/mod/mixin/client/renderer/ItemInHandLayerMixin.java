package org.confluence.mod.mixin.client.renderer;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.client.renderer.item.ShortSwordInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin implements SelfGetter<ItemInHandLayer<?, ?>> {
    @WrapWithCondition(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private boolean shortSword(
            PoseStack instance, float x, float y, float z,
            @Local(argsOnly = true) ItemStack itemStack,
            @Local(argsOnly = true) ItemDisplayContext displayContext,
            @Local(argsOnly = true) HumanoidArm arm
    ) {
        return ShortSwordInHandRenderer.renderArmWithItem(itemStack, displayContext, arm, instance, x, z, confluence$self().getParentModel());
    }
}
