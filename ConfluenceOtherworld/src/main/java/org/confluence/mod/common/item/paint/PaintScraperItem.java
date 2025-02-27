package org.confluence.mod.common.item.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;

import java.util.List;

public class PaintScraperItem extends Item {
    public PaintScraperItem() {
        super(new Properties().stacksTo(1));
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.paint_scraper"));
    }
}
