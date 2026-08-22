package org.confluence.mod.mixin.client.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.item.gun.BaseGun;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// 收枪动画结束后再替换原版手持物品缓存，避免较长的动画被提前截断。
@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private ItemStack mainHandItem;

    @Shadow
    private float mainHandHeight;

    @Unique
    private boolean confluence$gunPutAwayWasObserved;

    @Inject(method = "tick", at = @At("HEAD"))
    private void confluence$finishGunPutAway(CallbackInfo callbackInfo) {
        if (minecraft.player == null || mainHandItem.isEmpty() || ItemStack.matches(mainHandItem, minecraft.player.getMainHandItem()) || !(mainHandItem.getItem() instanceof BaseGun gun)) {
            confluence$gunPutAwayWasObserved = false;
            return;
        }

        if (gun.isPutAwayAnimationPlaying(mainHandItem)) {
            confluence$gunPutAwayWasObserved = true;
            // 收枪动画播放时维持旧物品缓存，结束后恢复原版更新。
            mainHandHeight = Math.max(mainHandHeight, 0.6F);
        } else if (confluence$gunPutAwayWasObserved) {
            // 收枪动画结束后在本刻替换缓存，避免旧枪回到待机姿态后多渲染一帧。
            mainHandHeight = 0.0F;
            confluence$gunPutAwayWasObserved = false;
        }
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void confluence$skipCompletedPutAwayFrame(AbstractClientPlayer player, float partialTick, float pitch,
                                                      InteractionHand hand, float swingProgress, ItemStack itemStack,
                                                      float equippedProgress, PoseStack poseStack, MultiBufferSource bufferSource,
                                                      int packedLight, CallbackInfo callbackInfo) {
        if (hand != InteractionHand.MAIN_HAND || !confluence$gunPutAwayWasObserved || mainHandItem.isEmpty() || ItemStack.matches(mainHandItem, player.getMainHandItem()) || !(mainHandItem.getItem() instanceof BaseGun gun)) {
            return;
        }

        if (!gun.isPutAwayAnimationPlaying(mainHandItem)) {
            // 旧栈即将由 tick 替换，跳过控制器恢复待机姿态的这一帧。
            mainHandHeight = 0.0F;
            callbackInfo.cancel();
        }
    }
}
