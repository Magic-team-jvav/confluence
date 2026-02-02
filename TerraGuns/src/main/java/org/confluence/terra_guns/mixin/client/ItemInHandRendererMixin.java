package org.confluence.terra_guns.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.confluence.terra_guns.common.init.TGTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Shadow private ItemStack mainHandItem;

    @Inject(method = "applyItemArmTransform", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"), cancellable = true)
    public void cancelAnim(PoseStack poseStack, HumanoidArm hand, float equippedProg, CallbackInfo ci, @Local int i){
        if (this.mainHandItem.is(TGTags.GUN)){
            poseStack.translate((float)i * 0.56F, -0.52F + 0, -0.72F);
            ci.cancel();
        }
    }
}
