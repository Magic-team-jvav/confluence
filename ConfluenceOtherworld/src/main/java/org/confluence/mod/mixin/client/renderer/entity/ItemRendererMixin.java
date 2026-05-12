package org.confluence.mod.mixin.client.renderer.entity;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.client.renderer.item.SpecialItemRenderingUtil;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements SelfGetter<ItemRenderer> {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    public abstract void render(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model);

    @Shadow
    public abstract BakedModel getModel(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity, int seed);

    @Inject(method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V", shift = At.Shift.AFTER))
    private void renderArrowInBow(
            CallbackInfo ci,
            @Local(argsOnly = true) @Nullable LivingEntity entity,
            @Local(argsOnly = true) ItemDisplayContext displayContext,
            @Local(argsOnly = true) boolean leftHand,
            @Local(argsOnly = true) PoseStack poseStack,
            @Local(argsOnly = true) MultiBufferSource bufferSource,
            @Local(argsOnly = true) @Nullable Level level,
            @Local(argsOnly = true, ordinal = 0) int combinedLight,
            @Local(argsOnly = true, ordinal = 1) int combinedOverlay,
            @Local(argsOnly = true, ordinal = 2) int seed
    ) {
        if (entity == null) return;
        Player player = minecraft.player;
        if (entity != player) {
            return;
        }

        InteractionHand hand = player.getUsedItemHand();
        ItemStack stack;
        boolean shouldRender = hand == InteractionHand.MAIN_HAND && !leftHand || hand == InteractionHand.OFF_HAND && leftHand;
        if (!shouldRender) {
            return;
        }
        stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        if (item instanceof BaseTerraRepeaterItem) {
            SpecialItemRenderingUtil.repeaterArrowRenderer(confluence$self(), entity, displayContext, leftHand, poseStack, bufferSource, level, combinedLight, combinedOverlay, seed, player, stack);
            return;
        }

        if (!player.isUsingItem()) {
            return;
        }

        if (item instanceof BaseTerraBowItem) {
            SpecialItemRenderingUtil.bowArrowRenderer(confluence$self(), entity, displayContext, leftHand, poseStack, bufferSource, level, combinedLight, combinedOverlay, seed, player, stack);
        }
    }
}
