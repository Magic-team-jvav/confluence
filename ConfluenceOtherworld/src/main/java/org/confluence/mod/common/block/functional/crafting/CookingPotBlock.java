package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.menu.CookingPotMenu;
import org.confluence.mod.common.recipe.CookingPotRecipe;
import org.confluence.mod.common.recipe.ListRecipeInput;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static org.confluence.mod.common.menu.HellforgeMenu.RESULT_SLOT;

public class CookingPotBlock extends BaseEntityBlock {
    public static final MapCodec<CookingPotBlock> CODEC = simpleCodec(CookingPotBlock::new);

    public CookingPotBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.LIT, false));
    }

    @Override
    protected MapCodec<CookingPotBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            if (level.getBlockEntity(pos) instanceof Entity entity) {
                player.openMenu(entity);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
            if (pLevel.getBlockEntity(pPos) instanceof Entity entity) {
                if (!pLevel.isClientSide) {
                    Containers.dropContents(pLevel, pPos, entity);
                }
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(BlockStateProperties.LIT);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : ModUtils.getTicker(blockEntityType, FunctionalBlocks.COOKING_POT_ENTITY.get(), Entity::serverTick);
    }

    public static class Entity extends BaseContainerBlockEntity {
        protected NonNullList<ItemStack> items = NonNullList.withSize(CookingPotMenu.SLOT_COUNT, ItemStack.EMPTY);
        int cookingProgress;
        int cookingTotalTime;
        int heatSourceItem = Item.getId(Items.AIR);
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
        private final RecipeManager.CachedCheck<RecipeInput, CookingPotRecipe> cachedCheck;

        public Entity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.COOKING_POT_ENTITY.get(), pos, blockState);
            this.cachedCheck = RecipeManager.createCheck(ModRecipes.COOKING_POT_TYPE.get());
        }

        public static void serverTick(Level level, BlockPos pos, BlockState state, Entity blockEntity) {
            BlockState blockState = level.getBlockState(pos.below());
            blockEntity.heatSourceItem = Item.getId(blockState.getBlock().asItem());
            boolean hasInput = blockEntity.items.stream().anyMatch(itemStack -> !itemStack.isEmpty());
            if (hasInput) {
                ListRecipeInput input = new ListRecipeInput(blockEntity.items);
                Optional<RecipeHolder<CookingPotRecipe>> recipeFor = blockEntity.cachedCheck.getRecipeFor(input, level);
                if (recipeFor.isPresent()) {
                    CookingPotRecipe recipe = recipeFor.get().value();
                    if (blockState.is(recipe.getHeatSource()) &&
                            recipe.getContainer().test(blockEntity.items.get(CookingPotMenu.CONTAINER_SLOT)) &&
                            canResultInsert(blockEntity.items, blockEntity.getMaxStackSize(), recipe.getResultItem(null))
                    ) {
                        blockEntity.cookingTotalTime = recipe.getCookingTime();
                        if (++blockEntity.cookingProgress >= blockEntity.cookingTotalTime) {
                            blockEntity.items.get(CookingPotMenu.CONTAINER_SLOT).shrink(1);
                            ItemStack neoResult = recipe.assembleAndExtract(input, level.registryAccess());
                            ItemStack oldResult = blockEntity.items.get(RESULT_SLOT);
                            if (oldResult.isEmpty()) {
                                blockEntity.items.set(RESULT_SLOT, neoResult.copy());
                            } else if (ItemStack.isSameItemSameComponents(oldResult, neoResult)) {
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

        private static boolean canResultInsert(NonNullList<ItemStack> inventory, int maxStackSize, ItemStack neoResult) {
            if (neoResult.isEmpty()) {
                return false;
            } else {
                ItemStack oldResult = inventory.get(CookingPotMenu.RESULT_SLOT);
                if (oldResult.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItemSameComponents(oldResult, neoResult)) {
                    return false;
                } else {
                    return oldResult.getCount() + neoResult.getCount() <= maxStackSize && oldResult.getCount() + neoResult.getCount() <= oldResult.getMaxStackSize() || oldResult.getCount() + neoResult.getCount() <= neoResult.getMaxStackSize();
                }
            }
        }

        @Override
        protected Component getDefaultName() {
            return Component.translatable("container.confluence.cooking_pot");
        }

        @Override
        protected NonNullList<ItemStack> getItems() {
            return items;
        }

        @Override
        protected void setItems(NonNullList<ItemStack> items) {
            this.items = items;
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, items, registries);
            this.cookingProgress = tag.getInt("CookTime");
            this.cookingTotalTime = tag.getInt("CookTimeTotal");
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            tag.putInt("CookTime", this.cookingProgress);
            tag.putInt("CookTimeTotal", this.cookingTotalTime);
            ContainerHelper.saveAllItems(tag, this.items, registries);
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
