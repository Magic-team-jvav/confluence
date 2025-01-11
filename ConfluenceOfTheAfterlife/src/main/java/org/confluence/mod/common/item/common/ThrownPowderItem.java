package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;

public class ThrownPowderItem extends Item {
    private final ISpreadable.Type type;

    public ThrownPowderItem(ISpreadable.Type type) {
        super(new Properties());
        this.type = type;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        Block clickedBlock = level.getBlockState(pos).getBlock();
        Block target = type.getBlockMap().get(clickedBlock);
        if (!level.isClientSide && target != null) {
            // todo
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}