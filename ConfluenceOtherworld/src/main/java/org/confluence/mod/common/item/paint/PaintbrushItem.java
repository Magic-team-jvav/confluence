package org.confluence.mod.common.item.paint;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;


public class PaintbrushItem extends CustomRarityItem {
    public PaintbrushItem(Properties properties, ModRarity rarity) {
        super(properties.stacksTo(1), rarity);
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
