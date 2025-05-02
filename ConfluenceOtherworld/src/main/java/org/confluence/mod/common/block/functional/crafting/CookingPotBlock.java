package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.menu.CookingPotMenu;
import org.confluence.mod.common.recipe.CookingPotRecipe;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.function.Consumer;

import static org.confluence.mod.common.menu.HellforgeMenu.RESULT_SLOT;

public class CookingPotBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<CookingPotBlock> CODEC = simpleCodec(CookingPotBlock::new);
    private static final VoxelShape SHAPE = box(1, 0, 1, 15, 14, 15);

    public CookingPotBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.LIT, false));
    }

    @Override
    protected MapCodec<CookingPotBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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
        pBuilder.add(BlockStateProperties.LIT, FACING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : LibUtils.getTicker(blockEntityType, FunctionalBlocks.COOKING_POT_ENTITY.get(), Entity::serverTick);
    }

    public static class Entity extends BaseContainerBlockEntity implements GeoBlockEntity {
        protected NonNullList<ItemStack> items = NonNullList.withSize(CookingPotMenu.SLOT_COUNT, ItemStack.EMPTY);
        int cookingProgress;
        int cookingTotalTime;
        int heatSourceItem = Item.getId(Items.AIR);
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
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

        public Entity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.COOKING_POT_ENTITY.get(), pos, blockState);
            this.cachedCheck = RecipeManager.createCheck(ModRecipes.COOKING_POT_TYPE.get());
        }

        public static void serverTick(Level level, BlockPos pos, BlockState state, Entity blockEntity) {
            BlockInWorld heatSource = new BlockInWorld(level, pos.below(), true);
            if (level.getGameTime() % 20 == 1) { // 每秒获取一次
                blockEntity.heatSourceItem = Item.getId(heatSource.getState().getBlock().asItem());
            }
            ItemStack[] itemStacks = blockEntity.getItemStacks();
            boolean hasItem = false;
            for (ItemStack stack : itemStacks) {
                if (!stack.isEmpty()) {
                    hasItem = true;
                    break;
                }
            }
            if (hasItem) {
                CookingPotRecipe.Input input = new CookingPotRecipe.Input(itemStacks, blockEntity.getItem(CookingPotMenu.CONTAINER_SLOT), heatSource);
                Optional<RecipeHolder<CookingPotRecipe>> recipeFor = blockEntity.cachedCheck.getRecipeFor(input, level);
                if (recipeFor.isPresent()) {
                    CookingPotRecipe recipe = recipeFor.get().value();
                    if (canResultInsert(blockEntity.items, blockEntity.getMaxStackSize(), recipe.getResultItem(null))) {
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

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return CACHE;
        }
    }

    public static class Item extends BlockItem implements GeoItem {
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

        public Item(Block block, Properties properties) {
            super(block, properties);
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return CACHE;
        }

        @Override
        public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
            consumer.accept(new GeoRenderProvider() {
                private GeoItemRenderer<Item> renderer;

                @Override
                public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                    if (renderer == null) {
                        this.renderer = new GeoItemRenderer<>(new DefaultedBlockGeoModel<>(Confluence.asResource("cooking_pot")));
                    }
                    return renderer;
                }
            });
        }
    }
}
