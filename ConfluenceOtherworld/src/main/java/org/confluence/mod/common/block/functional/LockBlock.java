package org.confluence.mod.common.block.functional;

import PortLib.extensions.net.minecraft.advancements.critereon.ItemPredicate.PortItemPredicateExtension;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

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
        return new BEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof BEntity entity) {
            ItemStack stack = player.getItemInHand(hand);
            if (entity.matchTool.isEmpty() || entity.matchTool.get().matches(stack)) {
                if (level.destroyBlock(pos, false, player)) {
                    BlockPos relative = pos.relative(state.getValue(BlockStateProperties.FACING));
                    if (level.getBlockState(relative).getDestroySpeed(level, relative) != -1) {
                        level.destroyBlock(relative, false, player);
                        if (entity.consumeTool) {
                            stack.shrink(1);
                            return InteractionResult.CONSUME;
                        }
                        return InteractionResult.CONSUME_PARTIAL;
                    }
                }
            } else {
                TagKey<Item> tag = entity.matchTool.get().tag;
                if (tag != null) {
                    HolderSet<Item> holders = BuiltInRegistries.ITEM.getOrCreateTag(tag);
                    MutableComponent itemsNeed = Component.translatable("message.confluence.lock.need");
                    Component or = Component.translatable("message.confluence.lock.or");
                    Iterator<Holder<Item>> iterator = holders.iterator();
                    while (iterator.hasNext()) {
                        itemsNeed.append(iterator.next().value().getDescription());
                        if (iterator.hasNext()) itemsNeed.append(or);
                    }
                    player.displayClientMessage(itemsNeed, true);
                } else {
                    Set<Item> items = entity.matchTool.get().items;
                    if (items != null) {
                        MutableComponent itemsNeed = Component.translatable("message.confluence.lock.need");
                        Component or = Component.translatable("message.confluence.lock.or");
                        Iterator<Item> iterator = items.iterator();
                        while (iterator.hasNext()) {
                            itemsNeed.append(iterator.next().getDescription());
                            if (iterator.hasNext()) itemsNeed.append(or);
                        }
                        player.displayClientMessage(itemsNeed, true);
                    }
                }
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    public static class BEntity extends BlockEntity {
        private Optional<ItemPredicate> matchTool = Optional.empty();
        private boolean consumeTool = false;

        public BEntity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.LOCK_BLOCK_ENTITY.get(), pos, blockState);
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            this.matchTool = PortItemPredicateExtension.codec().parse(NbtOps.INSTANCE, tag.get("MatchTool")).result();
            this.consumeTool = tag.getBoolean("ConsumeTool");
        }

        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            matchTool.flatMap(predicate -> PortItemPredicateExtension.codec().encodeStart(NbtOps.INSTANCE, predicate).result()).ifPresent(nbt -> tag.put("MatchTool", nbt));
            tag.putBoolean("ConsumeTool", consumeTool);
        }
    }
}
