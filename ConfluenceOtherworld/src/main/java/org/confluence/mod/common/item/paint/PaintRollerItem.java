package org.confluence.mod.common.item.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;

public class PaintRollerItem extends Item {
    public PaintRollerItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getPlayer() instanceof ServerPlayer serverPlayer) {
            ServerLevel serverLevel = serverPlayer.serverLevel();
            BlockPos clickedPos = pContext.getClickedPos();
            Direction clickedFace = pContext.getClickedFace();
            int color = PaintItem.useAndGetRGB(pContext.getPlayer());
            if (color != BrushData.EMPTY_COLOR) {
                BrushingColorPacketS2C.sendToPlayersTrackingChunk(serverLevel, clickedPos, clickedFace, color, true);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
