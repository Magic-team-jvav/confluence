package org.confluence.mod.common.item.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;

import java.util.List;

public class PaintRollerItem extends TooltipItem {
    public PaintRollerItem(Properties properties, ModRarity rarity, List<Component> tooltips) {
        super(properties.stacksTo(1), rarity, tooltips);
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
