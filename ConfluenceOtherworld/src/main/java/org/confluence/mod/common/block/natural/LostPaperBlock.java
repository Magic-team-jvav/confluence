package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.Confluence;

public class LostPaperBlock extends Block {
    private static final IntegerProperty LAYER = IntegerProperty.create("layer", 0, 3);
    private static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[]{
            box(3.0, 0.0, 3.0, 13.0, 3.0, 13.0),
            box(3.0, 0.0, 3.0, 13.0, 4.0, 13.0),
            box(3.0, 0.0, 3.0, 13.0, 5.0, 13.0),
            box(3.0, 0.0, 3.0, 13.0, 9.0, 13.0)
    };

    public LostPaperBlock() {
        super(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY));
        registerDefaultState(stateDefinition.any().setValue(LAYER, 0));
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext UseContext) {
        return !UseContext.isSecondaryUseActive() && UseContext.getItemInHand().is(asItem()) && state.getValue(LAYER) < 3 || super.canBeReplaced(state, UseContext);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState clickedBlockState = context.getLevel().getBlockState(context.getClickedPos());
        if (clickedBlockState.is(this)) {
            int currentLayer = clickedBlockState.getValue(LAYER);
            if (currentLayer < 3) {
                return clickedBlockState.setValue(LAYER, currentLayer + 1);
            }
        }
        return this.defaultBlockState();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_LAYER[state.getValue(LAYER)];
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(pos.below());
        return belowState.isFaceSturdy(level, belowPos, Direction.UP);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool) {
        int layer = state.getValue(LAYER);
        int dropTimes = layer + 1;
        if (level instanceof ServerLevel serverLevel) {
            LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, getLootTableId()));
            if (lootTable != LootTable.EMPTY) {
                LootParams params = new LootParams.Builder(serverLevel)
                        .withParameter(LootContextParams.ORIGIN, player.position())
                        .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
                        .withLuck(player.getLuck())
                        .create(LootContextParamSets.GIFT);
                for (int i = 0; i < dropTimes; i++) {
                    for (ItemStack drop : lootTable.getRandomItems(params)) {
                        popResource(level, pos, drop);
                    }
                }
            }
            popResource(level, pos, new ItemStack(Items.PAPER, dropTimes));
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    private ResourceLocation getLootTableId() {
        return Confluence.asResource("blocks/lost_paper");
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) level.scheduleTick(pos, this, 1);
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYER);
    }
}
