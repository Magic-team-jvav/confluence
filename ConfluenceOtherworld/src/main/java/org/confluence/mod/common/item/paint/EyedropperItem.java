package org.confluence.mod.common.item.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import org.confluence.mod.common.attachment.ChunkBrushData;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.common.init.item.PaintItems;

public class EyedropperItem extends Item {
    public EyedropperItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() instanceof ServerPlayer player) {
            BlockPos clickedPos = context.getClickedPos();
            BrushData brushData = ChunkBrushData.of(player.serverLevel()).getDataMap().get(new ChunkPos(clickedPos));
            if (brushData != null) {
                int rgb = brushData.get(clickedPos, context.getClickedFace());
                if (rgb != BrushData.EMPTY_COLOR) {
                    ItemStack stack = PaintItems.PAINT.toStack();
                    PaintItem.setRGB(stack, rgb);
                    player.setItemInHand(InteractionHand.OFF_HAND, stack);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
