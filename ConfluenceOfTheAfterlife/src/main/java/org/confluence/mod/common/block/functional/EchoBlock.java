package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.block.StateProperties;
import org.jetbrains.annotations.NotNull;

public class EchoBlock extends HalfTransparentBlock {
    public EchoBlock() {
        super(Properties.of().isSuffocating((blockState, blockGetter, blockPos) -> false).noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(StateProperties.VISIBLE, false));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext context) {
        if (context instanceof EntityCollisionContext context1 && context1.getEntity() instanceof Player player) {
            if (player.isLocalPlayer()) return ClientPacketHandler.hasEchoVisible() ? Shapes.block() : Shapes.empty();
            return player.getPersistentData().getBoolean("confluence:hasEchoVisibility") ? Shapes.block() : Shapes.empty();
        }
        return Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public @NotNull VoxelShape getVisualShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    public boolean propagatesSkylightDown(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StateProperties.VISIBLE);
    }

    public float getShadeBrightness(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos) {
        return 1.0F;
    }
}
