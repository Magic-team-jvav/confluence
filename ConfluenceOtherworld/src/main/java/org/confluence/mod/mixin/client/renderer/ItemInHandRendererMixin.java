package org.confluence.mod.mixin.client.renderer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.entity.projectile.whip.WhipAttackEntity;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.common.item.whip.BaseWhipItem;
import org.confluence.mod.common.item.yoyo.YoyoItem;
import org.confluence.terra_curio.common.item.MagicMirror;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Shadow
    protected abstract void renderPlayerArm(PoseStack poseStack, MultiBufferSource buffer, int packedLight, float equippedProgress, float swingProgress, HumanoidArm side);

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 0))
    private void check(
            CallbackInfo ci,
            @Local(argsOnly = true) AbstractClientPlayer player,
            @Local(argsOnly = true, ordinal = 2) float swingProgress,
            @Local(argsOnly = true) ItemStack stack,
            @Local(argsOnly = true, ordinal = 3) float equippedProgress,
            @Local(argsOnly = true) PoseStack poseStack,
            @Local(argsOnly = true) MultiBufferSource buffer,
            @Local(argsOnly = true) int combinedLight,
            @Local HumanoidArm humanoidarm
    ) {
        if (stack.is(SwordItems.ZOMBIE_ARM)) {
            if (!player.isInvisible()) {
                renderPlayerArm(poseStack, buffer, combinedLight, equippedProgress, swingProgress, humanoidarm);
            }
        } else if (stack.getItem() instanceof BaseWhipItem && !player.isInvisible()
                && !player.level().getEntitiesOfClass(WhipAttackEntity.class, player.getBoundingBox().inflate(8.0), attack -> attack.representsHeldWeapon(player, stack, humanoidarm)).isEmpty()) {
            // 挥鞭时只保留伸出的手臂，物品模型由 ItemRendererMixin 隐藏。
            renderPlayerArm(poseStack, buffer, combinedLight, equippedProgress, 0.0F, humanoidarm);
        } else if (stack.getItem() instanceof YoyoItem && !player.isInvisible() && confluence$hasMatchingDeployedYoyo(player, stack)) {
            // The deployed yoyo model is suppressed separately, so retain the first-person arm.
            // This mirrors 1.21's explicit arm rendering without coupling every yoyo item to a
            // shared client-side weapon flag.
            renderPlayerArm(poseStack, buffer, combinedLight, equippedProgress, swingProgress, humanoidarm);
        }
    }

    @Unique
    private static boolean confluence$hasMatchingDeployedYoyo(AbstractClientPlayer player, ItemStack stack) {
        return !player.level().getEntitiesOfClass(YoyoEntity.class,
                AABB.ofSize(player.position(), 128.0D, 128.0D, 128.0D),
                yoyo -> !yoyo.isDetached() && !yoyo.isCounterweight() && yoyo.belongsTo(player) && yoyo.represents(stack)).isEmpty();
    }

    @WrapOperation(method = {"evaluateWhichHandsToRender", "selectionUsingItemWhileHoldingBowLike"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private static boolean isBow(ItemStack instance, Item item, Operation<Boolean> original) {
        boolean is = original.call(instance, item);
        if (!is) {
            if (item == Items.BOW && instance.getItem() instanceof BaseTerraBowItem) {
                return true;
            }
            if (item == Items.CROSSBOW && instance.getItem() instanceof BaseTerraRepeaterItem) {
                return true;
            }
        }
        return is;
    }

    /// 魔镜保留长按使用时间，但第一人称不套用望远镜的贴脸模型变换。
    @WrapOperation(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;"))
    private UseAnim useNormalFirstPersonTransformForMagicMirror(ItemStack stack, Operation<UseAnim> original) {
        return stack.getItem() instanceof MagicMirror ? UseAnim.NONE : original.call(stack);
    }
}
