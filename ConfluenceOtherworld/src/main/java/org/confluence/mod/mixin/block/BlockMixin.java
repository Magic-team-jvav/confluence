package org.confluence.mod.mixin.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.item.paint.PaintItem;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.confluence.terra_curio.util.TCUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(method = "setPlacedBy", at = @At("HEAD"))
    private void withPaint(CallbackInfo ci, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) LivingEntity placer) {
        if (!level.isClientSide && placer instanceof ServerPlayer serverPlayer && TCUtils.hasType(serverPlayer, AccessoryItems.PAINT$SPRAYER)) {
            int color = PaintItem.useAndGetRGB(serverPlayer);
            if (color != -1) {
                BrushingColorPacketS2C.sendToPlayersTrackingChunk(serverPlayer.serverLevel(), pos, null, color, true);
            }
        }
    }
}
