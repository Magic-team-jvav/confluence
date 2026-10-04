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
import net.neoforged.fml.loading.FMLEnvironment;
import org.confluence.lib.common.block.ILibSimulatorBlock;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.client.handler.ClientPacketHandler;

public class EchoBlock extends HalfTransparentBlock implements ILibSimulatorBlock {
    public EchoBlock() {
        super(Properties.of().isSuffocating((blockState, blockGetter, blockPos) -> false).noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(StateProperties.VISIBLE, false));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext context) {
        if (context instanceof EntityCollisionContext context1 && context1.getEntity() instanceof Player player) {
            if (player.isLocalPlayer()) return ClientPacketHandler.hasEchoVisible() ? Shapes.block() : Shapes.empty();
            return LibEntityUtils.getOrCreatePersistedData(player).getBoolean("confluence:has_echo_visibility") ? Shapes.block() : Shapes.empty();
        }
        return Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public VoxelShape getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext context) {
        return Shapes.empty();
    }

    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StateProperties.VISIBLE);
    }

    public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 1.0F;
    }

    @Override
    public BlockState getSimulatedBlock(boolean isClient) {
        if (isClient && FMLEnvironment.dist.isClient()) {
            return defaultBlockState().setValue(StateProperties.VISIBLE, ClientPacketHandler.hasEchoVisible());
        }
        return defaultBlockState();
    }
}
