package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.block.NatureBlocks;

public class OpalOreBlock extends BrushableBlock {
    public OpalOreBlock() {
        super(NatureBlocks.DIATOMACEOUS.get(), SoundEvents.BRUSH_SAND, SoundEvents.BRUSH_SAND_COMPLETED, BlockBehaviour.Properties.of().strength(3.0F, 3.0F).pushReaction(PushReaction.BLOCK));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        BrushableBlockEntity blockEntity = new BrushableBlockEntity(pos, state);
        blockEntity.setLootTable(ModLootTables.OPAL_BLOCK, pos.asLong());
        return blockEntity;
    }
}
