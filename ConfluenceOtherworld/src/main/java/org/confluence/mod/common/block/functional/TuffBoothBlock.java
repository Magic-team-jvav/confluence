package org.confluence.mod.common.block.functional;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TuffBoothBlock extends BaseEntityBlock {
    public static final EnumProperty<BoothColor> COLOR = EnumProperty.create("color", BoothColor.class);
    public static final BooleanProperty SHOW_NAME = BooleanProperty.create("show_name");
    public static final MapCodec<TuffBoothBlock> CODEC = simpleCodec(TuffBoothBlock::new);

    private static final Map<Item, BoothColor> ITEM_TO_COLOR = new HashMap<>();
    private static final Map<BoothColor, Item> COLOR_TO_ITEM = new HashMap<>();
    private static final VoxelShape SHAPE = Shapes.or(
            box(1, 0, 1, 15, 3, 15),
            box(3, 3, 3, 13, 13, 13),
            box(0, 13, 0, 16, 16, 16),
            box(1, 3, 12, 4, 5, 15),
            box(12, 3, 12, 15, 5, 15),
            box(12, 3, 1, 15, 5, 4),
            box(1, 3, 1, 4, 5, 4)
    );

    static {
        registerCarpet(Items.WHITE_CARPET, BoothColor.WHITE);
        registerCarpet(Items.LIGHT_GRAY_CARPET, BoothColor.LIGHT_GRAY);
        registerCarpet(Items.GRAY_CARPET, BoothColor.GRAY);
        registerCarpet(Items.BLACK_CARPET, BoothColor.BLACK);
        registerCarpet(Items.BROWN_CARPET, BoothColor.BROWN);
        registerCarpet(Items.RED_CARPET, BoothColor.RED);
        registerCarpet(Items.ORANGE_CARPET, BoothColor.ORANGE);
        registerCarpet(Items.YELLOW_CARPET, BoothColor.YELLOW);
        registerCarpet(Items.LIME_CARPET, BoothColor.LIME);
        registerCarpet(Items.GREEN_CARPET, BoothColor.GREEN);
        registerCarpet(Items.CYAN_CARPET, BoothColor.CYAN);
        registerCarpet(Items.LIGHT_BLUE_CARPET, BoothColor.LIGHT_BLUE);
        registerCarpet(Items.BLUE_CARPET, BoothColor.BLUE);
        registerCarpet(Items.PURPLE_CARPET, BoothColor.PURPLE);
        registerCarpet(Items.MAGENTA_CARPET, BoothColor.MAGENTA);
        registerCarpet(Items.PINK_CARPET, BoothColor.PINK);
    }

    public TuffBoothBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, BoothColor.NONE).setValue(SHOW_NAME, false));
    }

    private static void registerCarpet(Item item, BoothColor color) {
        ITEM_TO_COLOR.put(item, color);
        COLOR_TO_ITEM.put(color, item);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {return CODEC;}

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {return SHAPE;}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {builder.add(COLOR, SHOW_NAME);}

    @Override
    public RenderShape getRenderShape(BlockState state) {return RenderShape.MODEL;}

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        if (target instanceof BlockHitResult hit) {
            Vec3 location = hit.getLocation();
            if (location.y - pos.getY() > 0.5) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof TuffBoothBlockEntity booth) {
                    ItemStack displayStack = booth.getItemHandler().getStackInSlot(0);
                    if (!displayStack.isEmpty()) {
                        return displayStack.copy();
                    }
                }
            }
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof TuffBoothBlockEntity booth))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        double hitY = hit.getLocation().y - pos.getY();
        if (hitY > 0.5) {
            return handleItemInteraction(booth, player, hand, stack);
        } else {
            return handleDecorationInteraction(level, pos, state, player, stack);
        }
    }

    private ItemInteractionResult handleItemInteraction(TuffBoothBlockEntity booth, Player player, InteractionHand hand, ItemStack stack) {
        IItemHandler handler = booth.getItemHandler();
        ItemStack current = handler.getStackInSlot(0);

        if (!stack.isEmpty()) {
            if (!current.isEmpty()) popItem(player, current.copy());
            handler.extractItem(0, 1, false);
            handler.insertItem(0, stack.copyWithCount(1), false);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            return ItemInteractionResult.SUCCESS;
        } else if (!current.isEmpty()) {
            popItem(player, handler.extractItem(0, 1, false));
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.CONSUME;
    }

    private ItemInteractionResult handleDecorationInteraction(Level level, BlockPos pos, BlockState state, Player player, ItemStack stack) {
        boolean isCreative = player.getAbilities().instabuild;

        if (stack.isEmpty()) {
            if (state.getValue(SHOW_NAME)) {
                popItem(player, new ItemStack(Items.NAME_TAG));
                level.setBlock(pos, state.setValue(SHOW_NAME, false), 3);
                return ItemInteractionResult.SUCCESS;
            }
            BoothColor color = state.getValue(COLOR);
            if (color != BoothColor.NONE) {
                Item carpet = COLOR_TO_ITEM.get(color);
                if (carpet != null) popItem(player, new ItemStack(carpet));
                level.setBlock(pos, state.setValue(COLOR, BoothColor.NONE), 3);
                return ItemInteractionResult.SUCCESS;
            }
        } else {
            BoothColor newColor = ITEM_TO_COLOR.get(stack.getItem());
            if (newColor != null) {
                if (state.getValue(COLOR) != newColor) {
                    BoothColor oldColor = state.getValue(COLOR);
                    if (oldColor != BoothColor.NONE) {
                        Item oldItem = COLOR_TO_ITEM.get(oldColor);
                        if (oldItem != null) popItem(player, new ItemStack(oldItem));
                    }
                    if (!isCreative) stack.shrink(1);
                    level.setBlock(pos, state.setValue(COLOR, newColor), 3);
                    level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return ItemInteractionResult.SUCCESS;
                }
            } else if (stack.is(Items.NAME_TAG) && !state.getValue(SHOW_NAME)) {
                if (!isCreative) stack.shrink(1);
                level.setBlock(pos, state.setValue(SHOW_NAME, true), 3);
                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5F, 1.0F);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private void popItem(Player player, ItemStack stack) {
        if (stack.isEmpty()) return;
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof TuffBoothBlockEntity booth) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), booth.getItemHandler().getStackInSlot(0));
                BoothColor color = state.getValue(COLOR);
                if (color != BoothColor.NONE) {
                    Item carpet = COLOR_TO_ITEM.get(color);
                    if (carpet != null)
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(carpet));
                }
                if (state.getValue(SHOW_NAME)) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.NAME_TAG));
                }
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TuffBoothBlockEntity(pos, state);
    }

    public enum BoothColor implements StringRepresentable {
        NONE("none"), WHITE("white"), LIGHT_GRAY("light_gray"), GRAY("gray"), BLACK("black"),
        BROWN("brown"), RED("red"), ORANGE("orange"), YELLOW("yellow"), LIME("lime"),
        GREEN("green"), CYAN("cyan"), LIGHT_BLUE("light_blue"), BLUE("blue"),
        PURPLE("purple"), MAGENTA("magenta"), PINK("pink");

        private final String name;

        BoothColor(String name) {this.name = name;}

        @Override
        public String getSerializedName() {return this.name;}
    }

    public static class TuffBoothBlockEntity extends BlockEntity {
        private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (level != null && !level.isClientSide) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
        };

        public TuffBoothBlockEntity(BlockPos pos, BlockState state) {
            super(FunctionalBlocks.TUFF_BOOTH_ENTITY.get(), pos, state);
        }

        public IItemHandler getItemHandler() {return itemHandler;}

        @Override
        public ClientboundBlockEntityDataPacket getUpdatePacket() {return ClientboundBlockEntityDataPacket.create(this);}

        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
            CompoundTag tag = new CompoundTag();
            saveAdditional(tag, registries);
            return tag;
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            tag.put("inv", itemHandler.serializeNBT(registries));
        }

        @Override
        public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            if (tag.contains("inv")) {
                itemHandler.deserializeNBT(registries, tag.getCompound("inv"));
            }
        }
    }
}
