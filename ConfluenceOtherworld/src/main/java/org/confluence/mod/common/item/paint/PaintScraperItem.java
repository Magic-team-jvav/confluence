package org.confluence.mod.common.item.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;

import java.util.List;

public class PaintScraperItem extends TooltipItem {
    public PaintScraperItem(Properties properties, ModRarity rarity, List<Component> tooltips) {
        super(properties.stacksTo(1), rarity, tooltips);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getPlayer() instanceof ServerPlayer serverPlayer) {
            ServerLevel serverLevel = serverPlayer.serverLevel();
            BlockPos clickedPos = pContext.getClickedPos();
            if (serverPlayer.isCrouching()) {
                BrushingColorPacketS2C.remove(serverLevel, clickedPos, pContext.getClickedFace());
            } else {
                BrushingColorPacketS2C.remove(serverLevel, clickedPos);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
