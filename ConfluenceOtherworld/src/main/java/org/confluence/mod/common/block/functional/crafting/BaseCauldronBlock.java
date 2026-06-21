package org.confluence.mod.common.block.functional.crafting;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.menu.CookingPotMenu;
import org.confluence.mod.common.recipe.CookingPotRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static org.confluence.mod.common.menu.CookingPotMenu.RESULT_SLOT;

public class BaseCauldronBlock extends HorizontalDirectionalBlock implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(box(1, 0.016, 1, 15, 8.016, 15), box(1, 10.016, 1, 15, 12.016, 15), box(2, 8.016, 2, 14, 10.016, 14));

    public BaseCauldronBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.LIT, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            if (level.getBlockEntity(pos) instanceof BEntity entity) {
                player.openMenu(entity);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof Container container) {
                if (!level.isClientSide) {
                    Containers.dropContents(level, pos, container);
                }
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.LIT, FACING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : LibUtils.getTicker(blockEntityType, FunctionalBlocks.CAULDRON_ENTITY.get(), CookingPotBlock.BEntity::serverTick);
    }

    public static class BEntity extends BaseContainerBlockEntity {
        protected NonNullList<ItemStack> items = NonNullList.withSize(CookingPotMenu.SLOT_COUNT, ItemStack.EMPTY);
        int cookingProgress;
        int cookingTotalTime;
        int heatSourceItem = CookingPotBlock.BItem.getId(Items.AIR);
        ItemStack[] itemStacks = new ItemStack[4];
        protected final ContainerData dataAccess = new ContainerData() {
            @Override
            public int get(int data) {
                return switch (data) {
                    case 0 -> cookingProgress;
                    case 1 -> cookingTotalTime;
                    case 2 -> heatSourceItem;
                    default -> 0;
                };
            }

            @Override
            public void set(int data, int value) {
                switch (data) {
                    case 0:
                        cookingProgress = value;
                        break;
                    case 1:
                        cookingTotalTime = value;
                        break;
                    case 2:
                        heatSourceItem = value;
                }
            }

            @Override
            public int getCount() {
                return CookingPotMenu.DATA_COUNT;
            }
        };
        private final RecipeManager.CachedCheck<CookingPotRecipe.Input, CookingPotRecipe> cachedCheck;

        public BEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
            super(blockEntityType, pos, blockState);
            this.cachedCheck = RecipeManager.createCheck(ModRecipes.COOKING_POT_TYPE.get());
        }

        public BEntity(BlockPos pos, BlockState blockState) {
            this(FunctionalBlocks.CAULDRON_ENTITY.get(), pos, blockState);
        }

//        @Override
//        public void onLoad() {
//            super.onLoad();
//            invalidateCapabilities();
//        }
//
//        @Override
//        public void onChunkUnloaded() {
//            invalidateCapabilities();
//        }

        public static void serverTick(Level level, BlockPos pos, BlockState state, BEntity blockEntity) {
            BlockInWorld heatSource = new BlockInWorld(level, pos.below(), true);
            if (level.getGameTime() % 20 == 1) { // 每秒获取一次
                blockEntity.heatSourceItem = Item.getId(heatSource.getState().getBlock().asItem());
            }
            ItemStack[] itemStacks = blockEntity.getItemStacks();
            boolean hasItem = !itemStacks[0].isEmpty() || !itemStacks[1].isEmpty() || !itemStacks[2].isEmpty() || !itemStacks[3].isEmpty();
            if (hasItem) {
                CookingPotRecipe.Input input = new CookingPotRecipe.Input(itemStacks, blockEntity.getItem(CookingPotMenu.CONTAINER_SLOT), heatSource);
                Optional<CookingPotRecipe> recipeFor = blockEntity.cachedCheck.getRecipeFor(input, level);
                if (recipeFor.isPresent()) {
                    CookingPotRecipe recipe = recipeFor.get();
                    if (canResultInsert(blockEntity.items, blockEntity.getMaxStackSize(), recipe.getResultItem(null))) {
                        blockEntity.cookingTotalTime = recipe.getCookingTime();
                        if (++blockEntity.cookingProgress >= blockEntity.cookingTotalTime) {
                            blockEntity.items.get(CookingPotMenu.CONTAINER_SLOT).shrink(1);
                            ItemStack neoResult = recipe.assembleAndExtract(input, level.registryAccess());
                            ItemStack oldResult = blockEntity.items.get(RESULT_SLOT);
                            if (oldResult.isEmpty()) {
                                blockEntity.items.set(RESULT_SLOT, neoResult.copy());
                            } else if (PortItemStackExtension.isSameItemSameComponents(oldResult, neoResult)) {
                                oldResult.grow(neoResult.getCount());
                            }
                        } else {
                            return;
                        }
                    }
                }
            }
            blockEntity.cookingProgress = 0;
        }

        private ItemStack[] getItemStacks() {
            itemStacks[0] = items.get(0);
            itemStacks[1] = items.get(1);
            itemStacks[2] = items.get(2);
            itemStacks[3] = items.get(3);
            return itemStacks;
        }

        private static boolean canResultInsert(NonNullList<ItemStack> inventory, int maxStackSize, ItemStack neoResult) {
            if (neoResult.isEmpty()) {
                return false;
            } else {
                ItemStack oldResult = inventory.get(CookingPotMenu.RESULT_SLOT);
                if (oldResult.isEmpty()) {
                    return true;
                } else if (!PortItemStackExtension.isSameItemSameComponents(oldResult, neoResult)) {
                    return false;
                } else {
                    return oldResult.getCount() + neoResult.getCount() <= maxStackSize && oldResult.getCount() + neoResult.getCount() <= oldResult.getMaxStackSize() || oldResult.getCount() + neoResult.getCount() <= neoResult.getMaxStackSize();
                }
            }
        }

        @Override
        protected Component getDefaultName() {
            return Component.translatable("container.confluence.cauldron");
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void clearContent() {

        }

        @Override
        public ItemStack getItem(int i) {
            return items.get(i);
        }

        @Override
        public ItemStack removeItem(int i, int i1) {
            return items.remove(i);
        }

        @Override
        public ItemStack removeItemNoUpdate(int i) {
            return items.remove(i);
        }

        @Override
        public void setItem(int i, ItemStack itemStack) {
            items.set(i, itemStack);
        }

        @Override
        public boolean stillValid(Player player) {
            return false;
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, items);
            this.cookingProgress = tag.getInt("CookTime");
            this.cookingTotalTime = tag.getInt("CookTimeTotal");
        }

        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            tag.putInt("CookTime", this.cookingProgress);
            tag.putInt("CookTimeTotal", this.cookingTotalTime);
            ContainerHelper.saveAllItems(tag, this.items);
        }

        @Override
        protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
            return new CookingPotMenu(containerId, inventory, this, dataAccess);
        }

        @Override
        public int getContainerSize() {
            return items.size();
        }

    }
}
