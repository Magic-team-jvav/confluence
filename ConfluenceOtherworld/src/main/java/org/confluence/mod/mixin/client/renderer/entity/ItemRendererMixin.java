package org.confluence.mod.mixin.client.renderer.entity;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.client.renderer.item.SpecialItemRenderingUtil;
import org.confluence.mod.client.summon.ClientSummonManager;
import org.confluence.mod.common.entity.projectile.sword.PhasebladeProjectile;
import org.confluence.mod.common.entity.projectile.whip.WhipAttackEntity;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.item.SummonItems;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.common.item.sword.BasePhasebladeItem;
import org.confluence.mod.common.item.whip.BaseWhipItem;
import org.confluence.mod.common.item.yoyo.YoyoItem;
import org.confluence.mod.common.summoner.SummonerHelper;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements SelfGetter<ItemRenderer> {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    public abstract void render(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model);

    @Shadow
    public abstract BakedModel getModel(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity, int seed);

    @WrapWithCondition(method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"))
    private boolean hideTransientWeaponModel(ItemRenderer instance, ItemStack stack, ItemDisplayContext displayContext,
                                         boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource,
                                         int combinedLight, int combinedOverlay, BakedModel model,
                                         @Local(argsOnly = true) @Nullable LivingEntity entity) {
        if (!(entity instanceof Player player)) return true;
        if (stack.getItem() instanceof BaseWhipItem && (displayContext.firstPerson() || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)) {
            HumanoidArm arm = leftHand ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
            return player.level().getEntitiesOfClass(WhipAttackEntity.class, player.getBoundingBox().inflate(8.0), attack -> attack.representsHeldWeapon(player, stack, arm)).isEmpty();
        }
        if (stack.getItem() instanceof YoyoItem) {
            return player.level().getEntitiesOfClass(YoyoEntity.class,
                    AABB.ofSize(player.position(), 128.0D, 128.0D, 128.0D),
                    yoyo -> yoyo.belongsTo(player) && yoyo.represents(stack)).isEmpty();
        }
        if (!(stack.getItem() instanceof BasePhasebladeItem)
                || !(displayContext.firstPerson() || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)) return true;
        return player.level().getEntitiesOfClass(PhasebladeProjectile.class,
                AABB.ofSize(player.position(), 256.0D, 256.0D, 256.0D),
                projectile -> projectile.belongsTo(player) && projectile.represents(stack)).isEmpty();
    }

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void useEmptyFinchStaffModel(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> callback) {
        if (entity instanceof Player player && stack.is(SummonItems.FINCH_STAFF)) {
            if (!SummonerHelper.get(player).getEntityData().get(SummonerAttachmentEntityTypes.FINCH.get()).isEmpty()) {
                callback.setReturnValue(ClientSummonManager.finchStaffEmptyModel());
            }
        }
    }

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
