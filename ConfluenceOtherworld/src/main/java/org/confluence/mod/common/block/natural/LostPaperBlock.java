package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.block.NatureBlocks;

public class LostPaperBlock extends Block implements EntityBlock {
    protected static final IntegerProperty LAYER = IntegerProperty.create("layer", 0, 3);
    protected static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 1, 15);

    public LostPaperBlock() {
        super(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).noCollission());
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
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(pos.below());
        return belowState.isFaceSturdy(level, belowPos, Direction.UP);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool) {
        if (blockEntity instanceof BEntity entity) {
            int layer = state.getValue(LAYER);
            entity.dropLoot(level, pos, player, layer);
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }


    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) level.scheduleTick(pos, this, 1);
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYER);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    public static class BEntity extends BlockEntity {
        private ResourceLocation lootTable = null;

        public BEntity(BlockPos pos, BlockState state) {
            super(NatureBlocks.LOST_PAPER_ENTITY.get(), pos, state);
        }

        public void setLootTable(ResourceLocation lootTable) {
            this.lootTable = lootTable;
            setChanged();
        }

        public ResourceLocation getLootTable() {
            return lootTable;
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            if (tag.contains("LootTable", Tag.TAG_STRING)) {
                this.lootTable = ResourceLocation.tryParse(tag.getString("LootTable"));
            }
        }

        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            if (lootTable != null) {
                tag.putString("LootTable", lootTable.toString());
            }
        }

        public void dropLoot(Level level, BlockPos pos, Player player, int layer) {
            if (!(level instanceof ServerLevel serverLevel)) return;
            int count = layer + 1;
            if (lootTable != null) {
                ResourceLocation resourcelocation = this.getLootTable();
                LootTable table = serverLevel.getServer().getLootData().getLootTable(resourcelocation);
                if (table != LootTable.EMPTY) {
                    LootParams params = new LootParams.Builder(serverLevel)
                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                            .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
                            .withLuck(player.getLuck())
                            .create(LootContextParamSets.GIFT);
                    for (int i = 0; i < count; i++) {
                        for (ItemStack stack : table.getRandomItems(params)) {
                            popResource(level, pos, stack);
                        }
                    }
                    return;
                }
            }
            ItemStack paperStack = new ItemStack(Items.PAPER, count);
            popResource(level, pos, paperStack);
        }
    }
}
