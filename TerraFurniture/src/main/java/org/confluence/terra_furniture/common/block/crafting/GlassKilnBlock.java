package org.confluence.terra_furniture.common.block.crafting;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.common.init.TFRegistries;
import org.confluence.terra_furniture.common.menu.GlassKilnMenu;
import org.confluence.terra_furniture.common.recipe.GlassKilnRecipe;
import org.jetbrains.annotations.Nullable;

import static org.confluence.terra_furniture.common.menu.GlassKilnMenu.*;

public class GlassKilnBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<GlassKilnBlock> CODEC = simpleCodec(GlassKilnBlock::new);

    public GlassKilnBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BlockStateProperties.LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected MapCodec<GlassKilnBlock> codec() {
        return CODEC;
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
                if (pLevel instanceof ServerLevel serverLevel) {
                    Containers.dropContents(pLevel, pPos, entity);
                    entity.getRecipesToAwardAndPopExperience(serverLevel, Vec3.atCenterOf(pPos));
                }
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new Entity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : LibUtils.getTicker(blockEntityType, TFBlocks.GLASS_KILN_ENTITY.get(), Entity::serverTick);
    }

    public static class Entity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder {
        private static final Component TITLE = Component.translatable("container.terra_furniture.glass_kiln");
        private static final int[] SLOTS_FOR_UP = Util.make(new int[16], array -> {
            for (int i = INPUT_START; i <= INPUT_END; i++) array[i] = i;
        });
        private static final int[] SLOTS_FOR_DOWN = new int[]{RESULT_SLOT, FUEL_SLOT};
        private static final int[] SLOTS_FOR_SIDES = new int[]{FUEL_SLOT};
        protected NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        int litTime;
        int litDuration;
        int cookingProgress;
        int cookingTotalTime;
        protected final ContainerData dataAccess = new ContainerData() {
            @Override
            public int get(int data) {
                return switch (data) {
                    case 0 -> {
                        if (litDuration > Short.MAX_VALUE) {
                            yield Mth.floor(((double) litTime / litDuration) * Short.MAX_VALUE);
                        }
                        yield litTime;
                    }
                    case 1 -> Math.min(litDuration, Short.MAX_VALUE);
                    case 2 -> cookingProgress;
                    case 3 -> cookingTotalTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int data, int value) {
                switch (data) {
                    case 0:
                        litTime = value;
                        break;
                    case 1:
                        litDuration = value;
                        break;
                    case 2:
                        cookingProgress = value;
                        break;
                    case 3:
                        cookingTotalTime = value;
                }
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
        private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
        private final RecipeManager.CachedCheck<CraftingInput, GlassKilnRecipe> glassKiln;
        private final RecipeManager.CachedCheck<SingleRecipeInput, SmeltingRecipe> smelting;
        private final NonNullList<ItemStack> cacheList = NonNullList.withSize(16, ItemStack.EMPTY);
        private CraftingInput cachedInput = CraftingInput.EMPTY;

        public Entity(BlockPos pos, BlockState blockState) {
            super(TFBlocks.GLASS_KILN_ENTITY.get(), pos, blockState);
            this.glassKiln = RecipeManager.createCheck(TFRegistries.GLASS_KILN_RECIPE_TYPE.get());
            this.smelting = RecipeManager.createCheck(RecipeType.SMELTING);
        }

        public static void serverTick(Level level, BlockPos pos, BlockState blockState, Entity entity) {
            boolean isLit = entity.isLit();
            boolean shouldUpdate = false;
            if (isLit) {
                entity.litTime--;
            }
            NonNullList<ItemStack> itemStacks = entity.items;
            if (entity.isLit() || !itemStacks.get(FUEL_SLOT).isEmpty()) {
                if (entity.cachedInput == CraftingInput.EMPTY) {
                    entity.updateCache();
                }
                RecipeHolder<GlassKilnRecipe> recipeHolder = entity.glassKiln.getRecipeFor(entity.cachedInput, level).orElse(null);
                if (recipeHolder != null && entity.canResultInsert(recipeHolder.value().getResultItem(null))) {
                    if (entity.isLit()) {
                        entity.cookingProgress++;
                        if (entity.cookingProgress >= entity.cookingTotalTime) {
                            entity.cookingProgress = 0;
                            entity.cookingTotalTime = entity.getTotalCookTime(level);
                            if (itemStacks.get(FUEL_SLOT).is(Items.BUCKET)) {
                                for (int i = INPUT_START; i <= INPUT_END; i++) {
                                    if (itemStacks.get(i).is(Items.WET_SPONGE)) {
                                        itemStacks.set(FUEL_SLOT, Items.WATER_BUCKET.getDefaultInstance());
                                    }
                                }
                            }
                            shouldUpdate |= assemble(recipeHolder.value().assembleAndExtract(new CraftingInput(4, 4, entity.cacheList), null), itemStacks, entity, recipeHolder);
                        }
                    } else {
                        shouldUpdate |= useFuel(entity, itemStacks);
                    }
                } else {
                    boolean matches = false;
                    for (ItemStack stack : entity.cacheList) {
                        if (stack.isEmpty()) continue;
                        SingleRecipeInput recipeInput = new SingleRecipeInput(stack);
                        RecipeHolder<SmeltingRecipe> recipeHolder1 = entity.smelting.getRecipeFor(recipeInput, level).orElse(null);
                        if (recipeHolder1 != null && entity.canResultInsert(recipeHolder1.value().getResultItem(level.registryAccess()))) {
                            matches = true;
                            if (entity.isLit()) {
                                entity.cookingProgress++;
                                if (entity.cookingProgress >= entity.cookingTotalTime) {
                                    entity.cookingProgress = 0;
                                    entity.cookingTotalTime = entity.getTotalCookTime(level);
                                    if (stack.is(Items.WET_SPONGE) && itemStacks.get(FUEL_SLOT).is(Items.BUCKET)) {
                                        itemStacks.set(FUEL_SLOT, Items.WATER_BUCKET.getDefaultInstance());
                                    }
                                    stack.shrink(1);
                                    shouldUpdate |= assemble(recipeHolder1.value().assemble(recipeInput, level.registryAccess()), itemStacks, entity, recipeHolder1);
                                }
                            } else {
                                shouldUpdate |= useFuel(entity, itemStacks);
                            }
                            break;
                        }
                    }
                    if (!matches) entity.cookingProgress = 0;
                }
            } else if (!entity.isLit() && entity.cookingProgress > 0) {
                entity.cookingProgress = Mth.clamp(entity.cookingProgress - 2, 0, entity.cookingTotalTime);
            }

            if (isLit != entity.isLit()) {
                shouldUpdate = true;
                blockState = blockState.setValue(AbstractFurnaceBlock.LIT, entity.isLit());
                level.setBlock(pos, blockState, Block.UPDATE_ALL);
            }

            if (shouldUpdate) {
                setChanged(level, pos, blockState);
                entity.updateCache();
            }
        }

        private static boolean assemble(ItemStack neoResult, NonNullList<ItemStack> itemStacks, Entity entity, @Nullable RecipeHolder<?> recipeHolder) {
            ItemStack oldResult = itemStacks.get(RESULT_SLOT);
            if (oldResult.isEmpty()) {
                itemStacks.set(RESULT_SLOT, neoResult.copy());
            } else if (ItemStack.isSameItemSameComponents(oldResult, neoResult)) {
                oldResult.grow(neoResult.getCount());
            }
            entity.setRecipeUsed(recipeHolder);
            return true;
        }

        private static boolean useFuel(Entity entity, NonNullList<ItemStack> itemStacks) {
            ItemStack itemStack = itemStacks.get(FUEL_SLOT);
            entity.litDuration = entity.getBurnDuration(itemStack);
            entity.litTime = entity.litDuration;
            if (entity.isLit()) {
                if (itemStack.hasCraftingRemainingItem()) {
                    itemStacks.set(FUEL_SLOT, itemStack.getCraftingRemainingItem());
                } else if (!itemStack.isEmpty()) {
                    itemStack.shrink(1);
                    if (itemStacks.isEmpty()) {
                        itemStacks.set(FUEL_SLOT, itemStack.getCraftingRemainingItem());
                    }
                }
                return true;
            }
            return false;
        }

        private void updateCache() {
            for (int i = INPUT_START; i <= INPUT_END; i++) {
                cacheList.set(i, items.get(i));
            }
            this.cachedInput = CraftingInput.of(4, 4, cacheList);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            ItemStack itemstack = items.get(slot);
            boolean neoStackOrStackOn = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stack);
            items.set(slot, stack);
            stack.limitSize(LibUtils.MAX_STACK_SIZE);
            if (slot < FUEL_SLOT && !neoStackOrStackOn && level != null) {
                this.cookingTotalTime = getTotalCookTime(level);
                this.cookingProgress = 0;
                setChanged();
            }
            updateCache();
        }

        private int getTotalCookTime(Level level) {
            return glassKiln.getRecipeFor(cachedInput, level)
                    .map(holder -> holder.value().getCookingTime())
                    .orElseGet(() -> {
                        SingleRecipeInput recipeInput = null;
                        for (int i = INPUT_START; i <= INPUT_END; i++) {
                            ItemStack itemStack = items.get(i);
                            if (!itemStack.isEmpty()) {
                                recipeInput = new SingleRecipeInput(itemStack);
                            }
                        }
                        if (recipeInput == null) return 200;
                        return smelting.getRecipeFor(recipeInput, level)
                                .map(holder -> holder.value().getCookingTime())
                                .orElse(200);
                    });
        }

        @Override
        protected Component getDefaultName() {
            return TITLE;
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
        protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
            return new GlassKilnMenu(i, inventory, this, dataAccess);
        }

        private boolean canResultInsert(ItemStack neoResult) {
            if (neoResult.isEmpty()) {
                return false;
            } else {
                ItemStack oldResult = items.get(RESULT_SLOT);
                if (oldResult.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItemSameComponents(oldResult, neoResult)) {
                    return false;
                } else {
                    return oldResult.getCount() + neoResult.getCount() <= LibUtils.MAX_STACK_SIZE && oldResult.getCount() + neoResult.getCount() <= oldResult.getMaxStackSize() || oldResult.getCount() + neoResult.getCount() <= neoResult.getMaxStackSize();
                }
            }
        }

        protected boolean isLit() {
            return litTime > 0;
        }

        @Override
        public int[] getSlotsForFace(Direction side) {
            if (side == Direction.DOWN) {
                return SLOTS_FOR_DOWN;
            } else {
                return side == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
            }
        }

        @Override
        public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
            return canPlaceItem(index, itemStack);
        }

        @Override
        public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
            return direction != Direction.DOWN || index != FUEL_SLOT || stack.is(Items.WATER_BUCKET) || stack.is(Items.BUCKET);
        }

        @Override
        public int getContainerSize() {
            return items.size();
        }

        @Override
        public void setRecipeUsed(@Nullable RecipeHolder<?> recipeHolder) {
            if (recipeHolder != null) {
                recipesUsed.addTo(recipeHolder.id(), 1);
            }
        }

        @Override
        public @Nullable RecipeHolder<?> getRecipeUsed() {
            return null;
        }

        @Override
        public boolean canPlaceItem(int index, ItemStack stack) {
            if (index == RESULT_SLOT) {
                return false;
            } else if (index < FUEL_SLOT) {
                ItemStack itemStack = getItem(index);
                return itemStack.isEmpty() || (itemStack.is(stack.getItem()) && itemStack.getCount() + stack.getCount() <= stack.getMaxStackSize());
            } else {
                ItemStack itemstack = getItem(FUEL_SLOT);
                return stack.getBurnTime(TFRegistries.GLASS_KILN_RECIPE_TYPE.get()) > 0 || stack.is(Items.BUCKET) && !itemstack.is(Items.BUCKET);
            }
        }

        public void getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 popVec) {
            for (Object2IntMap.Entry<ResourceLocation> entry : recipesUsed.object2IntEntrySet()) {
                level.getRecipeManager().byKey(entry.getKey()).ifPresent(recipeHolder -> {
                    if (recipeHolder.value() instanceof GlassKilnRecipe glassKilnRecipe) {
                        createExperience(level, popVec, entry.getIntValue(), glassKilnRecipe.getExperience());
                    } else if (recipeHolder.value() instanceof SmeltingRecipe smeltingRecipe) {
                        createExperience(level, popVec, entry.getIntValue(), smeltingRecipe.getExperience());
                    }
                });
            }
        }

        private static void createExperience(ServerLevel level, Vec3 popVec, int recipeIndex, float experience) {
            int i = Mth.floor((float) recipeIndex * experience);
            float f = Mth.frac((float) recipeIndex * experience);
            if (f != 0F && Math.random() < (double) f) {
                i++;
            }

            ExperienceOrb.award(level, popVec, i);
        }

        protected int getBurnDuration(ItemStack fuel) {
            if (fuel.isEmpty()) {
                return 0;
            } else {
                return fuel.getBurnTime(TFRegistries.GLASS_KILN_RECIPE_TYPE.get());
            }
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, items, registries);
            updateCache();
            this.litTime = tag.getInt("BurnTime");
            this.cookingProgress = tag.getInt("CookTime");
            this.cookingTotalTime = tag.getInt("CookTimeTotal");
            this.litDuration = getBurnDuration(items.get(FUEL_SLOT));
            CompoundTag compoundtag = tag.getCompound("RecipesUsed");

            for (String s : compoundtag.getAllKeys()) {
                recipesUsed.put(ResourceLocation.parse(s), compoundtag.getInt(s));
            }
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            tag.putInt("BurnTime", this.litTime);
            tag.putInt("CookTime", this.cookingProgress);
            tag.putInt("CookTimeTotal", this.cookingTotalTime);
            ContainerHelper.saveAllItems(tag, this.items, registries);
            CompoundTag compoundtag = new CompoundTag();
            recipesUsed.forEach((id, amount) -> compoundtag.putInt(id.toString(), amount));
            tag.put("RecipesUsed", compoundtag);
        }
    }
}
