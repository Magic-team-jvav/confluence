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
import org.confluence.mod.client.renderer.item.ArrowInBowRenderer;
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

    @Inject(method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V", shift = At.Shift.AFTER))
    private void renderArrowInBow(@Nullable LivingEntity entity, ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, @Nullable Level level, int combinedLight, int combinedOverlay, int seed, CallbackInfo ci) {
        if (entity == null) return;
        Player player = minecraft.player;
        if (entity == player && player.isUsingItem()) {
            InteractionHand hand = player.getUsedItemHand();
            ItemStack bow;
            boolean shouldRender = hand == InteractionHand.MAIN_HAND && !leftHand || hand == InteractionHand.OFF_HAND && leftHand;
            if (shouldRender && (bow = player.getItemInHand(hand)).getItem() instanceof TerraBowItem) {
                float charge = player.getTicksUsingItem() / 20.0f;
                if (charge < 0.1f) return;

                ItemStack arrowItem = player.getProjectile(bow);
                // 移除mixin方便热交换
                ArrowInBowRenderer.transform(bow, poseStack, charge, displayContext);
                BakedModel bakedmodel = getModel(arrowItem, level, entity, seed);
                render(arrowItem, displayContext, leftHand, poseStack, bufferSource, combinedLight, combinedOverlay, bakedmodel);
            }
        }
    }
}
