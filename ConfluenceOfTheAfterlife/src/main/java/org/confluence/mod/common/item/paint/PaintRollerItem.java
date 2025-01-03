package org.confluence.mod.common.item.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.jetbrains.annotations.NotNull;

public class PaintRollerItem extends Item {
    public PaintRollerItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getPlayer() instanceof ServerPlayer serverPlayer) {
            ServerLevel serverLevel = serverPlayer.serverLevel();
            BlockPos clickedPos = pContext.getClickedPos();
            Direction clickedFace = pContext.getClickedFace();
            if (serverLevel.getBlockState(clickedPos).isFaceSturdy(serverLevel, clickedPos, clickedFace)) {
                int color = PaintItem.getColor(pContext.getPlayer());
                if (color != -1) {
                    BrushingColorPacketS2C.sendToPlayersTrackingChunk(
                            serverLevel, clickedPos,
                            BrushData.Facing.fromDirection(clickedFace),
                            color, true
                    );
                }
            } else {
                serverPlayer.sendSystemMessage(Component.translatable("item.confluence.paint_roller.failed"));
            }
        }
        return InteractionResult.SUCCESS;
    }
}
