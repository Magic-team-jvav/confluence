package org.confluence.mod.mixin.client.world.item;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.client.handler.PlacementSpeedHandler;
import org.confluence.mod.mixin.client.accessor.MinecraftAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 只在客户端确认成功放置方块后应用放置速度，不影响容器和普通物品的右键行为。
 */
@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Unique
    private static final PlacementSpeedHandler CONFLUENCE$PLACEMENT_SPEED = new PlacementSpeedHandler();

    @Inject(method = "place", at = @At("RETURN"))
    private void applyPlacementSpeed(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!context.getLevel().isClientSide || !cir.getReturnValue().consumesAction()) return;
        Player player = context.getPlayer();
        if (player == null) return;

        MinecraftAccessor minecraft = (MinecraftAccessor) Minecraft.getInstance();
        int delay = minecraft.confluence$getRightClickDelay();
        minecraft.confluence$setRightClickDelay(CONFLUENCE$PLACEMENT_SPEED.apply(
                delay, player.getAttributeValue(ConfluenceMagicLib.PLACEMENT_SPEED.get())));
    }
}
