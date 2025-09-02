package org.confluence.mod.common.item.paint;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;

public class PaintbrushItem extends Item {
    public PaintbrushItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getPlayer() instanceof ServerPlayer serverPlayer) {
            int color = PaintItem.useAndGetRGB(pContext.getPlayer());
            if (color != -1) {
                BrushingColorPacketS2C.sendToPlayersTrackingChunk(serverPlayer.serverLevel(), pContext.getClickedPos(), null, color, true);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
