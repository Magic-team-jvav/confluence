package org.confluence.mod.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.client.handler.ArrowInBowHandler;
import org.confluence.mod.common.item.bow.TerraBowItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    public abstract void render(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model);

    @Shadow
    public abstract BakedModel getModel(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity, int seed);

    @Inject(method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V", at = @At(value = "TAIL"))
    private void renderStaticMixin(LivingEntity entity, ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, Level level, int combinedLight, int combinedOverlay, int seed, CallbackInfo ci) {
        Player player = minecraft.player;
        if (entity == player && player.isUsingItem()) {
            InteractionHand hand = player.getUsedItemHand();
            ItemStack bow = player.getItemInHand(hand);
            boolean shouldRender = hand == InteractionHand.MAIN_HAND && !leftHand || hand == InteractionHand.OFF_HAND && leftHand;
            if (shouldRender && bow.getItem() instanceof TerraBowItem) {
                float charge = player.getTicksUsingItem() / 20.0f;
                if (charge < 0.1f) return;

                ItemStack arrowItem = player.getProjectile(bow);
                // 移除mixin方便热交换
                ArrowInBowHandler.transform(bow, poseStack, charge, displayContext);
                BakedModel bakedmodel = this.getModel(arrowItem, level, entity, seed);
                this.render(arrowItem, displayContext, leftHand, poseStack, bufferSource, combinedLight, combinedOverlay, bakedmodel);
            }
        }
    }
}
