package org.confluence.terraentity.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.attachment.WeaponStorage;
import org.confluence.terraentity.client.init.model.AdditionalItemRegister;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.init.item.TESummonItems;
import org.confluence.terraentity.item.BaseWhipItem;
import org.confluence.terraentity.item.YoyosItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Shadow
    @Final
    private ItemModelShaper itemModelShaper;

    @WrapOperation(method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"))
    private void renderStatic(ItemRenderer instance, ItemStack itemStack, ItemDisplayContext context, boolean posestack$pose, PoseStack poseStack, MultiBufferSource vertexconsumer, int light, int combinedOverlay, BakedModel model, Operation<Void> original, @Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true) boolean leftHand) {
        Item item = itemStack.getItem();
        if (entity instanceof Player player && !leftHand) {
            // 右手使用鞭子时取消渲染
            if(item instanceof BaseWhipItem && player.getCooldowns().isOnCooldown(item)){
                return;
            }
            if (item instanceof YoyosItem && WeaponStorage.of(player).yoyosEntity != null) {
                return;
            }


        }
        original.call(instance, itemStack, context, posestack$pose, poseStack, vertexconsumer, light, combinedOverlay, model);
    }

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    public void getModel(ItemStack stack, Level level, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (entity instanceof Player player) {
            if (!player.getData(TEAttachments.SUMMONER_STORAGE).canSummon(1)) {
                if (stack.getItem() == TESummonItems.FINCH_STAFF.get()) {
                    cir.setReturnValue(itemModelShaper.getModelManager().getModel(AdditionalItemRegister.FINCH_STAFF_MODEL));
                }
            }
        }
    }
}
