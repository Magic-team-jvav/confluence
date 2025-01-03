package org.confluence.mod.common.item.paint;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.jetbrains.annotations.NotNull;

public class PaintScraperItem extends Item {
    public PaintScraperItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel() instanceof ServerLevel serverLevel) {
            BrushingColorPacketS2C.remove(serverLevel, pContext.getClickedPos());
        }
        return InteractionResult.SUCCESS;
    }
}
