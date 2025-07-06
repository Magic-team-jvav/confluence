package org.confluence.mod.common.block.functional;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.init.block.FunctionalBlocks;

import java.util.Optional;

public class LockBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public LockBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.WEST));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof Entity entity && (entity.matchTool.isEmpty() || entity.matchTool.get().test(stack)) && level.destroyBlock(pos, false, player)) {
            BlockPos relative = pos.relative(state.getValue(FACING));
            BlockState blockState = level.getBlockState(relative);
            if (blockState.getDestroySpeed(level, relative) != -1) {
                level.destroyBlock(relative, false, player);
                if (entity.consumeTool) {
                    stack.shrink(1);
                    return ItemInteractionResult.CONSUME;
                }
                return ItemInteractionResult.CONSUME_PARTIAL;
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    public static class Entity extends BlockEntity {
        private Optional<ItemPredicate> matchTool = Optional.empty();
        private boolean consumeTool = false;

        public Entity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.LOCK_BLOCK_ENTITY.get(), pos, blockState);
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            this.matchTool = ItemPredicate.CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, registries), tag.get("MatchTool")).result();
            this.consumeTool = tag.getBoolean("ConsumeTool");
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            matchTool.flatMap(predicate -> ItemPredicate.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registries), predicate).result()).ifPresent(nbt -> tag.put("MatchTool", nbt));
            tag.putBoolean("ConsumeTool", consumeTool);
        }
    }
}
