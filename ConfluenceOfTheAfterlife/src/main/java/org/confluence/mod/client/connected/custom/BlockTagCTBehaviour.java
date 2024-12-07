package org.confluence.mod.client.connected.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.client.connected.CTSpriteShiftEntry;
import org.confluence.mod.client.connected.behaviour.SimpleCTBehaviour;

public class BlockTagCTBehaviour extends SimpleCTBehaviour {
    private final TagKey<Block> tag;

    public BlockTagCTBehaviour(CTSpriteShiftEntry shift, TagKey<Block> tag) {
        super(shift);
        this.tag = tag;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        return !isBeingBlocked(state, reader, pos, otherPos, face) && other.is(tag);
    }
}
