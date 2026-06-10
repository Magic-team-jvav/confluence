package org.confluence.mod.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationProcessor;

public class ModGeckoLibUtils {
    public static void stopAndPlayAnim(GeoItem geoItem, ItemStack itemStack, ServerPlayer serverPlayer, String controllerName, @Nullable String animName) {
        if (controllerName == null || animName == null) return;
        long orAssignId = GeoItem.getOrAssignId(itemStack, serverPlayer.serverLevel());
        AnimatableManager<GeoAnimatable> animatableManager = geoItem.getAnimatableInstanceCache().getManagerForId(orAssignId);
        AnimationController<GeoAnimatable> gunController = animatableManager.getAnimationControllers().get(controllerName);
        AnimationProcessor.QueuedAnimation currentAnimation = gunController.getCurrentAnimation();

        if (currentAnimation != null) {
            geoItem.stopTriggeredAnim(serverPlayer, orAssignId, controllerName, animName);
        }
        geoItem.triggerAnim(serverPlayer, orAssignId, controllerName, animName);
    }
}
