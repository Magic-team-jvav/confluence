package org.confluence.mod.common.block.functional.crafting;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.block.HorizontalDirectionalWithHorizontalTwoPartBlock;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.lib.common.recipe.ArrayRecipeInput;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.recipe.EnhancedForgeRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class EnhancedForgeBlock extends HorizontalDirectionalWithHorizontalTwoPartBlock implements EntityBlock {
    public EnhancedForgeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.LIT, false));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            if (level.getBlockEntity(pos) instanceof EnhancedForgeBlock.BEntity<?> entity) {
                player.openMenu(entity.getBasePart());
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
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moveByPiston) {
        if (state.is(newState.getBlock())) return;
        if (state.getValue(PART).isBase() && level.getBlockEntity(pos) instanceof EnhancedForgeBlock.BEntity<?> entity) {
            if (level instanceof ServerLevel serverLevel) {
                Containers.dropContents(level, pos, entity);
                entity.getRecipesToAwardAndPopExperience(serverLevel, Vec3.atCenterOf(pos));
            }
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, newState, moveByPiston);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(BlockStateProperties.LIT));
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide || state.getValue(StateProperties.HORIZONTAL_TWO_PART).isRight() ? null : LibUtils.getTicker(blockEntityType, getBlockEntityType(), this::serverTick);
    }

    protected <T extends EnhancedForgeRecipe> void serverTick(Level level, BlockPos pos, BlockState state, EnhancedForgeBlock.BEntity<T> entity) {
        boolean isLit = entity.isLit();
        boolean[] data = new boolean[2];
        if (isLit) {
            entity.litTime--;
            entity.resetCookTime();
        }

        ItemStack[] itemStacks = entity.getItemStacks();
        boolean hasItem = !itemStacks[0].isEmpty() || !itemStacks[1].isEmpty() || !itemStacks[2].isEmpty() || !itemStacks[3].isEmpty();

        boolean forgeMatched = hasItem && isForgeMatched(level, entity, itemStacks, data);

        if (hasItem && !forgeMatched) {
            ItemStack lastInput = itemStacks[entity.lastCheckSlot];
            RecipeHolder<BlastingRecipe> recipeholder;
            SingleRecipeInput recipeInput;
            if (!lastInput.isEmpty() &&
                    (recipeholder = entity.blasting.getRecipeFor(recipeInput = new SingleRecipeInput(lastInput), level).orElse(null)) != null &&
                    recipeholder.value().matches(recipeInput, level)
            ) {
                entity.doBlasting(recipeholder, lastInput, data);
            } else {
                for (int i = 0; i < itemStacks.length; i++) {
                    ItemStack input = itemStacks[i];
                    if (input.isEmpty()) continue;
                    recipeholder = entity.blasting.getRecipeFor(new SingleRecipeInput(input), level).orElse(null);
                    if (recipeholder != null) {
                        entity.doBlasting(recipeholder, input, data);
                        entity.lastCheckSlot = i;
                        break;
                    }
                }
            }
        }

        if (!data[1] && !forgeMatched) {
            entity.cookingProgress = 0;
        }

        if (isLit != entity.isLit()) {
            data[0] = true;
            state = state.setValue(AbstractFurnaceBlock.LIT, entity.isLit());
            level.setBlockAndUpdate(pos, state);
        }

        if (data[0]) {
            BlockEntity.setChanged(level, pos, state);
        }
    }

    protected <T extends EnhancedForgeRecipe> boolean isForgeMatched(Level level, BEntity<T> entity, ItemStack[] itemStacks, boolean[] data) {
        RecipeHolder<T> recipeholder = entity.forge.getRecipeFor(new ArrayRecipeInput(itemStacks), level).orElse(null);
        if (recipeholder != null) {
            if (!entity.isLit() && entity.canForgeBurn(recipeholder)) {
                data[0] = entity.doUpdateStatus();
            }
            if (entity.canForgeBurn(recipeholder)) {
                if (entity.doUpdateProgress(recipeholder, entity::burnForge)) {
                    data[0] = true;
                }
            }
            return true;
        }
        return false;
    }

    protected abstract BlockEntityType<? extends BEntity<?>> getBlockEntityType();

    public static abstract class BEntity<T extends EnhancedForgeRecipe> extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder {
        public static final int INPUT_SLOT_1 = 0;
        public static final int INPUT_SLOT_2 = 1;
        public static final int INPUT_SLOT_3 = 2;
        public static final int INPUT_SLOT_4 = 3;
        public static final int FUEL_SLOT = 4;
        public static final int RESULT_SLOT = 5;
        public static final int DATA_COUNT = 5;
        public static final int SLOT_COUNT = 6;
        protected static final int[] SLOTS_FOR_UP = new int[]{INPUT_SLOT_1, INPUT_SLOT_2, INPUT_SLOT_3, INPUT_SLOT_4};
        protected static final int[] SLOTS_FOR_DOWN = new int[]{RESULT_SLOT, FUEL_SLOT};
        protected static final int[] SLOTS_FOR_SIDES = new int[]{FUEL_SLOT};
        protected NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        protected int litTime;
        protected int litDuration;
        protected int cookingProgress;
        protected int cookingTotalTime;
        protected int useFuel;
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
                    case 4 -> useFuel;
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
                        break;
                    case 4:
                        useFuel = value;
                }
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
        protected final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
        protected final RecipeManager.CachedCheck<RecipeInput, T> forge;
        protected final RecipeManager.CachedCheck<SingleRecipeInput, BlastingRecipe> blasting;
        protected final ItemStack[] itemStacks = new ItemStack[4];
        protected int lastCheckSlot = 0;
        protected BEntity<?> basePart = null;

        public BEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
            super(type, pos, blockState);
            this.forge = createCachedCheck(getRecipeType());
            this.blasting = RecipeManager.createCheck(RecipeType.BLASTING);
        }

        protected abstract RecipeType<T> getRecipeType();

        protected static <R extends EnhancedForgeRecipe> RecipeManager.CachedCheck<RecipeInput, R> createCachedCheck(RecipeType<R> recipeType) {
            return new RecipeManager.CachedCheck<>() {
                @Nullable
                private RecipeHolder<R> lastRecipe;
                private int lastIngredientCount = 0;

                @Override
                public Optional<RecipeHolder<R>> getRecipeFor(RecipeInput recipeInput, Level level) {
                    int count = 0;
                    for (int i = 0; i < recipeInput.size(); i++) {
                        if (!recipeInput.getItem(i).isEmpty()) count++;
                    }
                    if (count == 0) return Optional.empty();

                    if (lastRecipe != null && lastRecipe.value().matches(recipeInput, level)) {
                        if (count == lastIngredientCount) {
                            return Optional.of(lastRecipe);
                        }
                        this.lastIngredientCount = count;
                    }

                    Optional<RecipeHolder<R>> recipe = level.getRecipeManager().byType(recipeType).stream()
                            .filter(holder -> holder.value().matches(recipeInput, level))
                            .max(Comparator.comparingInt(holder -> holder.value().ingredients.size()));
                    if (recipe.isPresent()) {
                        this.lastRecipe = recipe.get();
                        return recipe;
                    }
                    return Optional.empty();
                }
            };
        }

        protected void doBlasting(RecipeHolder<BlastingRecipe> recipeholder, ItemStack lastInput, boolean[] data) {
            if (!isLit() && canBlastingBurn(recipeholder, lastInput)) {
                data[0] = doUpdateStatus();
            }
            if (canBlastingBurn(recipeholder, lastInput)) {
                if (doUpdateProgress(recipeholder, recipeHolder -> burnBlasting(recipeHolder, lastInput))) {
                    data[0] = true;
                }
            }
            data[1] = true;
        }

        protected <R extends Recipe<?>> boolean doUpdateProgress(RecipeHolder<R> recipeholder, Predicate<RecipeHolder<R>> predicate) {
            if (++getBasePart().cookingProgress >= cookingTotalTime) {
                getBasePart().cookingProgress = 0;
                if (!isLit()) {
                    getBasePart().cookingTotalTime = getTotalCookTime();
                }
                if (predicate.test(recipeholder)) {
                    setRecipeUsed(recipeholder);
                }
                return true;
            }
            return false;
        }

        protected boolean doUpdateStatus() {
            ItemStack fuel = getItem(FUEL_SLOT);
            getBasePart().litTime = getBurnDuration(fuel);
            getBasePart().litDuration = litTime;
            if (fuel.hasCraftingRemainingItem()) {
                getItems().set(FUEL_SLOT, fuel.getCraftingRemainingItem());
            } else if (fuel.isEmpty()) {
                getBasePart().useFuel = 0;
            } else {
                fuel.shrink(1);
                if (fuel.isEmpty()) {
                    getItems().set(FUEL_SLOT, fuel.getCraftingRemainingItem());
                }
                getBasePart().useFuel = 1;
            }
            return true;
        }

        @Override
        public void setItem(int index, ItemStack stack) {
            ItemStack itemstack = getItem(index);
            boolean flag = stack.isEmpty() || !ItemStack.isSameItemSameComponents(itemstack, stack);
            getItems().set(index, stack);
            stack.limitSize(getMaxStackSize(stack));
            if (index < 4 && flag) {
                resetCookTime();
                setChanged();
            } else if (index == FUEL_SLOT) {
                getBasePart().useFuel = stack.isEmpty() ? 0 : 1;
                resetCookTime();
                setChanged();
            }
        }

        protected void resetCookTime() {
            if (!isLit()) {
                getBasePart().cookingTotalTime = getTotalCookTime();
                if (getItems().get(FUEL_SLOT).isEmpty()) {
                    getBasePart().cookingProgress = 0;
                }
            }
        }

        protected boolean canBlastingBurn(RecipeHolder<BlastingRecipe> recipe, ItemStack input) {
            if (!input.isEmpty()) {
                ItemStack neoResult = recipe.value().assemble(new SingleRecipeInput(input), level.registryAccess());
                return canResultInsert(neoResult);
            } else {
                return false;
            }
        }

        protected boolean canResultInsert(ItemStack neoResult) {
            if (neoResult.isEmpty()) {
                return false;
            } else {
                ItemStack oldResult = getItems().get(RESULT_SLOT);
                if (oldResult.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItemSameComponents(oldResult, neoResult)) {
                    return false;
                } else {
                    return oldResult.getCount() + neoResult.getCount() <= LibUtils.MAX_STACK_SIZE && oldResult.getCount() + neoResult.getCount() <= oldResult.getMaxStackSize() || oldResult.getCount() + neoResult.getCount() <= neoResult.getMaxStackSize();
                }
            }
        }

        protected boolean burnBlasting(RecipeHolder<BlastingRecipe> recipe, ItemStack input) {
            if (canBlastingBurn(recipe, input)) {
                ItemStack neoResult = recipe.value().assemble(new SingleRecipeInput(input), level.registryAccess());
                ItemStack oldResult = getItems().get(RESULT_SLOT);
                if (oldResult.isEmpty()) {
                    getItems().set(RESULT_SLOT, neoResult.copy());
                } else if (ItemStack.isSameItemSameComponents(oldResult, neoResult)) {
                    oldResult.grow(neoResult.getCount());
                }

                if (input.is(Blocks.WET_SPONGE.asItem()) && getItems().get(FUEL_SLOT).is(Items.BUCKET)) {
                    getItems().set(FUEL_SLOT, Items.WATER_BUCKET.getDefaultInstance());
                }

                input.shrink(1);
                return true;
            } else {
                return false;
            }
        }

        protected boolean canForgeBurn(RecipeHolder<T> recipe) {
            if ((!recipe.value().isRequiresFuel() || (useFuel() || isLit() || !getItem(FUEL_SLOT).isEmpty())) && Arrays.stream(itemStacks).anyMatch(itemStack -> !itemStack.isEmpty())) {
                ItemStack neoResult = recipe.value().getResultItem(level.registryAccess());
                return canResultInsert(neoResult);
            } else {
                return false;
            }
        }

        protected boolean burnForge(RecipeHolder<T> recipe) {
            if (canForgeBurn(recipe)) {
                if (getItems().get(FUEL_SLOT).is(Items.BUCKET)) {
                    for (ItemStack input : itemStacks) {
                        if (input.is(Items.WET_SPONGE)) {
                            getItems().set(FUEL_SLOT, Items.WATER_BUCKET.getDefaultInstance());
                        }
                    }
                }
                ItemStack neoResult = recipe.value().assembleAndExtract(new ArrayRecipeInput(itemStacks), level.registryAccess());
                ItemStack oldResult = getItems().get(RESULT_SLOT);
                if (oldResult.isEmpty()) {
                    getItems().set(RESULT_SLOT, neoResult.copy());
                } else if (ItemStack.isSameItemSameComponents(oldResult, neoResult)) {
                    oldResult.grow(neoResult.getCount());
                }
                return true;
            } else {
                return false;
            }
        }

        protected int getBurnDuration(ItemStack fuel) {
            if (fuel.isEmpty()) {
                return 0;
            } else {
                return fuel.getBurnTime(getRecipeType()) / 2;
            }
        }

        protected boolean useFuel() {
            return useFuel == 1;
        }

        protected boolean isLit() {
            return litTime > 0;
        }

        protected ItemStack[] getItemStacks() {
            NonNullList<ItemStack> stacks = getItems();
            this.itemStacks[0] = stacks.get(0);
            this.itemStacks[1] = stacks.get(1);
            this.itemStacks[2] = stacks.get(2);
            this.itemStacks[3] = stacks.get(3);
            return itemStacks;
        }

        protected int getTotalCookTime() {
            if (level == null) return 100;
            int time = forge.getRecipeFor(new ArrayRecipeInput(getItemStacks()), level)
                    .map(holder -> holder.value().getCookingTime()).orElse(100);
            return getItems().get(FUEL_SLOT).isEmpty() ? time * 8 : time;
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
            return getItems().size();
        }

        @Override
        protected NonNullList<ItemStack> getItems() {
            return getBasePart().items;
        }

        protected BEntity<?> getBasePart() {
            if (basePart == null) {
                if (getBlockState().getValue(StateProperties.HORIZONTAL_TWO_PART).isBase()) {
                    return this.basePart = this;
                }
                if (level == null) return this;
                BlockPos relative = getBlockPos().relative(StateProperties.HorizontalTwoPart.getConnectedDirection(getBlockState()));
                if (level.isLoaded(relative) && level.getBlockEntity(relative) instanceof BEntity<?> entity) {
                    return this.basePart = entity;
                }
                return this;
            }
            return basePart;
        }

        @Override
        protected void setItems(NonNullList<ItemStack> items) {
            getBasePart().items = items;
        }

        @Override
        protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
            return newMenu(containerId, inventory, getBasePart(), getBasePart().dataAccess);
        }

        protected abstract AbstractContainerMenu newMenu(int containerId, Inventory inventory, Container forgeContainer, ContainerData forgeData);

        @Override
        public boolean canPlaceItem(int index, ItemStack stack) {
            if (index == RESULT_SLOT) {
                return false;
            } else if (index < FUEL_SLOT) {
                ItemStack itemStack = getItem(index);
                return itemStack.isEmpty() || (itemStack.is(stack.getItem()) && itemStack.getCount() + stack.getCount() <= stack.getMaxStackSize());
            } else {
                ItemStack itemstack = getItem(FUEL_SLOT);
                return stack.getBurnTime(getRecipeType()) > 0 || stack.is(Items.BUCKET) && !itemstack.is(Items.BUCKET);
            }
        }

        public void getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 popVec) {
            for (Object2IntMap.Entry<ResourceLocation> entry : recipesUsed.object2IntEntrySet()) {
                level.getRecipeManager().byKey(entry.getKey()).ifPresent(recipeHolder -> {
                    if (recipeHolder.value() instanceof EnhancedForgeRecipe recipe) {
                        createExperience(level, popVec, entry.getIntValue(), recipe.getExperience());
                    } else if (recipeHolder.value() instanceof BlastingRecipe recipe) {
                        createExperience(level, popVec, entry.getIntValue(), recipe.getExperience());
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

        @Override
        public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
            if (recipe != null) {
                recipesUsed.addTo(recipe.id(), 1);
            }
        }

        @Override
        public @Nullable RecipeHolder<?> getRecipeUsed() {
            return null;
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            if (getBlockState().getValue(StateProperties.HORIZONTAL_TWO_PART).isBase()) {
                this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
                ContainerHelper.loadAllItems(tag, items, registries);
                this.litTime = tag.getInt("BurnTime");
                this.cookingProgress = tag.getInt("CookTime");
                this.cookingTotalTime = tag.getInt("CookTimeTotal");
                this.litDuration = getBurnDuration(getItems().get(FUEL_SLOT));
                CompoundTag compoundtag = tag.getCompound("RecipesUsed");

                for (String s : compoundtag.getAllKeys()) {
                    recipesUsed.put(ResourceLocation.parse(s), compoundtag.getInt(s));
                }
            }
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            if (getBlockState().getValue(StateProperties.HORIZONTAL_TWO_PART).isBase()) {
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
}
