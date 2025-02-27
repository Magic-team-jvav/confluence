package org.confluence.mod.common.item.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.item.PaintItems;

public class EyedropperItem extends Item {
    public EyedropperItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
            BlockPos clickedPos = context.getClickedPos();
            BrushData brushData = serverPlayer.level().getData(ModAttachmentTypes.CHUNK_BRUSH_DATA).getDataMap().get(new ChunkPos(clickedPos));
            if (brushData != null) {
                int color = brushData.get(clickedPos, context.getClickedFace());
                if (color != BrushData.EMPTY_COLOR) {
                    ItemStack itemStack = PaintItems.PAINT.get().getDefaultInstance();
                    itemStack.set(DataComponents.DYED_COLOR, new DyedItemColor(color, true));
                    serverPlayer.setItemInHand(InteractionHand.OFF_HAND, itemStack);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
