package org.confluence.mod.common.block.functional;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.confluence.mod.common.init.block.FunctionalBlocks;

import java.util.Objects;

public class TuffBoothBlock extends BaseEntityBlock {
    public static final IntegerProperty COLOR = IntegerProperty.create("color", 0, 16); // 名字为 "color"，范围 0-15
    public static final BooleanProperty SHOW_NAME = BooleanProperty.create("show_name");
    private static final VoxelShape SHAPE = Shapes.or(
            box(1, 0, 1, 15, 3, 15),
            box(3, 3, 3, 13, 13, 13),
            box(0, 13, 0, 16, 16, 16),
            box(1, 3, 12, 4, 5, 15),
            box(12, 3, 12, 15, 5, 15),
            box(12, 3, 1, 15, 5, 4),
            box(1, 3, 1, 4, 5, 4)
    );
    private static final BiMap<Item, Integer> CARPET_TO_COLOR = Util.make(HashBiMap.create(), map -> {
        map.put(Items.WHITE_CARPET, 1);
        map.put(Items.LIGHT_GRAY_CARPET, 2);
        map.put(Items.GRAY_CARPET, 3);
        map.put(Items.BLACK_CARPET, 4);
        map.put(Items.BROWN_CARPET, 5);
        map.put(Items.RED_CARPET, 6);
        map.put(Items.ORANGE_CARPET, 7);
        map.put(Items.YELLOW_CARPET, 8);
        map.put(Items.LIME_CARPET, 9);
        map.put(Items.GREEN_CARPET, 10);
        map.put(Items.CYAN_CARPET, 11);
        map.put(Items.LIGHT_BLUE_CARPET, 12);
        map.put(Items.BLUE_CARPET, 13);
        map.put(Items.PURPLE_CARPET, 14);
        map.put(Items.MAGENTA_CARPET, 15);
        map.put(Items.PINK_CARPET, 16);
    });

    public TuffBoothBlock(Properties p_309186_) {
        super(p_309186_);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(COLOR, 0)
                .setValue(SHOW_NAME, false)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOR, SHOW_NAME);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TuffBoothBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(TuffBoothBlock::new);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {

        if (!newState.isAir()) {
            super.onRemove(state, level, pos, newState, movedByPiston);
            return;
        }

        if (level.getBlockEntity(pos) instanceof TuffBoothBlockEntity boothEntity) {
            ItemStack stackToDrop = boothEntity.getItemHandler().getStackInSlot(0);

            if (!stackToDrop.isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stackToDrop);
            }
            if (state.getValue(SHOW_NAME))
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), Items.NAME_TAG.getDefaultInstance());
            if (state.getValue(COLOR) != 0)
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), Objects.requireNonNull(CARPET_TO_COLOR.inverse().get(state.getValue(COLOR))).getDefaultInstance());
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return ItemInteractionResult.sidedSuccess(true);
        }

        if (!(level.getBlockEntity(pos) instanceof TuffBoothBlockEntity boothEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        ItemStackHandler itemHandler = boothEntity.getItemHandler();
        ItemStack boothItem = itemHandler.getStackInSlot(0);

        if (hit.getLocation().y - pos.getY() > 0.5) {
            if (!stack.isEmpty()) {
                if (!boothItem.isEmpty()) {
                    if (!player.getInventory().add(boothItem)) {
                        player.drop(boothItem, false);
                    }
                }
                ItemStack oneItem = stack.split(1);
                itemHandler.setStackInSlot(0, oneItem);
            } else {
                if (!boothItem.isEmpty()) {
                    player.setItemInHand(hand, boothItem.copy());
                    itemHandler.setStackInSlot(0, ItemStack.EMPTY);
                }
            }
            TuffBoothBlockEntity.setChanged(level, pos, boothEntity.getBlockState());
        } else {
            if (!stack.isEmpty()) {
                if (CARPET_TO_COLOR.containsKey(stack.getItem())) {
                    int currentColor = state.getValue(COLOR);
                    int newColor = Objects.requireNonNull(CARPET_TO_COLOR.get(stack.getItem()));

                    if (currentColor != newColor) {
                        stack.shrink(1);

                        if (currentColor != 0) {
                            ItemStack oldCarpet = Objects.requireNonNull(CARPET_TO_COLOR.inverse().get(currentColor)).getDefaultInstance();
                            if (!player.getInventory().add(oldCarpet)) {
                                player.drop(oldCarpet, false);
                            }
                        }

                        level.setBlock(pos, state.setValue(COLOR, newColor), UPDATE_ALL);
                    }
                } else if (stack.is(Items.NAME_TAG)) {
                    if (!state.getValue(SHOW_NAME)) {
                        stack.shrink(1);
                        level.setBlock(pos, state.setValue(SHOW_NAME, true), UPDATE_ALL);
                    }
                }
            } else {
                boolean stateChanged = false;
                BlockState newState = state;

                int currentColor = state.getValue(COLOR);
                if (currentColor != 0) {
                    ItemStack carpetStack = Objects.requireNonNull(CARPET_TO_COLOR.inverse().get(currentColor)).getDefaultInstance();
                    if (!player.getInventory().add(carpetStack)) {
                        player.drop(carpetStack, false);
                    }
                    newState = newState.setValue(COLOR, 0);
                    stateChanged = true;
                }

                if (newState.getValue(SHOW_NAME)) {
                    ItemStack nameTagStack = Items.NAME_TAG.getDefaultInstance();
                    if (!player.getInventory().add(nameTagStack)) {
                        player.drop(nameTagStack, false);
                    }
                    newState = newState.setValue(SHOW_NAME, false);
                    stateChanged = true;
                }

                if (stateChanged) {
                    level.setBlock(pos, newState, UPDATE_ALL);
                }
            }
        }

        boothEntity.setChanged();
        return ItemInteractionResult.sidedSuccess(false);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    public static class TuffBoothBlockEntity extends BlockEntity {
        private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                setChanged();
                if (level != null) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }
        };

        public TuffBoothBlockEntity(BlockPos pos, BlockState state) {
            super(FunctionalBlocks.TUFF_BOOTH_ENTITY.get(), pos, state);
        }

        @Override
        public ClientboundBlockEntityDataPacket getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        public ItemStackHandler getItemHandler() {
            return itemHandler;
        }

        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
            return saveWithoutMetadata(registries);
        }

        @Override
        public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
            loadAdditional(tag, registries);
        }

        @Override
        public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            if (tag.contains("inv")) {
                itemHandler.deserializeNBT(registries, tag.getCompound("inv"));
            }
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            tag.put("inv", itemHandler.serializeNBT(registries));
        }
    }
}
