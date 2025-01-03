package org.confluence.mod.common.item.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.jetbrains.annotations.NotNull;

public class PaintbrushItem extends Item {
    public PaintbrushItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel() instanceof ServerLevel serverLevel) {
            int color = 0xFFFFFF;
            BlockPos clickedPos = pContext.getClickedPos();
            BrushingColorPacketS2C.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(clickedPos), clickedPos, BrushData.Facing.ALL, color, true);
        }
        return InteractionResult.SUCCESS;
    }
}
