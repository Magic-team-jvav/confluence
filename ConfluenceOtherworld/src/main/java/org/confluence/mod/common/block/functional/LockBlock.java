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
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.Optional;
import java.util.Vector;

public class LockBlock extends Block implements EntityBlock {
    public LockBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Vector3i offset = new Vector3i(0, 0, 0);
        offset = switch (state.getValue(BlockStateProperties.FACING)) {
            case Direction.UP -> offset.add(0, 1, 0);
            case Direction.DOWN -> offset.add(0, -1, 0);
            case Direction.NORTH -> offset.add(0, 0, -1);
            case Direction.SOUTH -> offset.add(0, 0, 1);
            case Direction.EAST -> offset.add(1, 0, 0);
            default -> offset.add(-1, 0, 0);
        };
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof Entity entity && (entity.matchTool.isEmpty() || entity.matchTool.get().test(stack)) && (level.destroyBlock(pos, false, player) && level.destroyBlock(pos.offset(offset.x, offset.y, offset.z), false, player))) {
            if (entity.consumeTool) {
                stack.shrink(1);
                return ItemInteractionResult.CONSUME;
            }
            return ItemInteractionResult.CONSUME_PARTIAL;
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        return state.setValue(BlockStateProperties.FACING, context.getClickedFace().getOpposite());
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
