package org.confluence.lib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @Unique
    private static final ResourceLocation confluence$wip = ConfluenceMagicLib.asResource("textures/gui/wip.png");

    @Shadow
    public abstract void blit(ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight);

    @Shadow
    @Final
    private PoseStack pose;

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"))
    private void renderItemDecorations(Font font, ItemStack stack, int x, int y, @Nullable String text, CallbackInfo ci) {
        if (stack.is(LibTags.Items.WIP)) {
            pose.pushPose();
            pose.translate(0.0F, 0.0F, 200.0F);
            blit(confluence$wip, x, y, 0.0F, 0.0F, 16, 16, 16, 16);
            pose.popPose();
        }
    }
}
