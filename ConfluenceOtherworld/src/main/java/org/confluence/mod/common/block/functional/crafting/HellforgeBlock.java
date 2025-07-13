package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.*;
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
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.HorizontalDirectionalWithHorizontalTwoPartBlock;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.lib.common.recipe.ArrayRecipeInput;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.menu.HellforgeMenu;
import org.confluence.mod.common.recipe.HellforgeRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import static org.confluence.mod.common.menu.HellforgeMenu.*;

public class HellforgeBlock extends HorizontalDirectionalWithHorizontalTwoPartBlock implements EntityBlock {
    public static final MapCodec<HellforgeBlock> CODEC = simpleCodec(HellforgeBlock::new);
    private static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{
            box(3, 0, 3, 16, 16, 13),
            box(3, 0, 3, 13, 16, 16),
            box(0, 0, 3, 13, 16, 13),
            box(3, 0, 0, 13, 16, 13)
    };
    private static final VoxelShape[] RIGHT_SHAPES = new VoxelShape[]{
            box(0, 0, 3, 13, 16, 13),
            box(3, 0, 0, 13, 16, 13),
            box(3, 0, 3, 16, 16, 13),
            box(3, 0, 3, 13, 16, 16)
    };

    public HellforgeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.LIT, false));
    }

    @Override
    protected MapCodec<HellforgeBlock> codec() {
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
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = state.getValue(FACING).get2DDataValue();
        return state.getValue(StateProperties.HORIZONTAL_TWO_PART).isBase() ? BASE_SHAPES[index] : RIGHT_SHAPES[index];
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moveByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof Entity entity && state.getValue(StateProperties.HORIZONTAL_TWO_PART).isBase()) {
                if (level instanceof ServerLevel serverLevel) {
                    Containers.dropContents(level, pos, entity);
                    entity.getRecipesToAwardAndPopExperience(serverLevel, Vec3.atCenterOf(pos));
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, moveByPiston);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(BlockStateProperties.LIT));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide || state.getValue(StateProperties.HORIZONTAL_TWO_PART).isRight() ? null : LibUtils.getTicker(blockEntityType, FunctionalBlocks.HELLFORGE_ENTITY.get(), Entity::serverTick);
    }

    public static class Entity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder {
        private static final int[] SLOTS_FOR_UP = new int[]{INPUT_SLOT_1, INPUT_SLOT_2, INPUT_SLOT_3, INPUT_SLOT_4};
        private static final int[] SLOTS_FOR_DOWN = new int[]{RESULT_SLOT, FUEL_SLOT};
        private static final int[] SLOTS_FOR_SIDES = new int[]{FUEL_SLOT};
        protected NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        int litTime;
        int litDuration;
        int cookingProgress;
        int cookingTotalTime;
        int useFuel;
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
        private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
        private final RecipeManager.CachedCheck<RecipeInput, HellforgeRecipe> hellforge;
        private final RecipeManager.CachedCheck<SingleRecipeInput, BlastingRecipe> blasting;
        private final ItemStack[] itemStacks = new ItemStack[4];
        private int lastCheckSlot = 0;

        public Entity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.HELLFORGE_ENTITY.get(), pos, blockState);
            this.hellforge = new RecipeManager.CachedCheck<>() {
                @Nullable
                private RecipeHolder<HellforgeRecipe> lastRecipe;
                private int lastIngredientCount = 0;

                @Override
                public Optional<RecipeHolder<HellforgeRecipe>> getRecipeFor(RecipeInput recipeInput, Level level) {
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

                    Optional<RecipeHolder<HellforgeRecipe>> recipe = level.getRecipeManager()
                            .byType(ModRecipes.HELLFORGE_TYPE.get()).stream()
                            .filter(holder -> holder.value().matches(recipeInput, level))
                            .max(Comparator.comparingInt(holder -> holder.value().ingredients.size()));
                    if (recipe.isPresent()) {
                        this.lastRecipe = recipe.get();
                        return recipe;
                    }
                    return Optional.empty();
                }
            };
            this.blasting = RecipeManager.createCheck(RecipeType.BLASTING);
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

        public static void serverTick(Level level, BlockPos pos, BlockState state, Entity blockEntity) {
            boolean isLit = blockEntity.isLit();
            boolean[] data = new boolean[2];
            if (isLit) {
                blockEntity.litTime--;
                resetCookTime(blockEntity);
            }

            ItemStack[] itemStacks = blockEntity.getItemStacks();
            ItemStack fuel = blockEntity.items.get(FUEL_SLOT);
            boolean hasItem = !itemStacks[0].isEmpty() || !itemStacks[1].isEmpty() || !itemStacks[2].isEmpty() || !itemStacks[3].isEmpty();
            ;
            boolean hellforgeMatched = false;

            if (hasItem) {
                RecipeHolder<HellforgeRecipe> recipeholder = blockEntity.hellforge.getRecipeFor(new ArrayRecipeInput(itemStacks), level).orElse(null);
                if (recipeholder != null) {
                    if (!blockEntity.isLit() && canHellforgeBurn(level.registryAccess(), recipeholder, blockEntity.items, blockEntity)) {
                        data[0] = doUpdateStatus(blockEntity, fuel);
                    }
                    if (canHellforgeBurn(level.registryAccess(), recipeholder, blockEntity.items, blockEntity)) {
                        if (doUpdateProgress(blockEntity, level, recipeholder, () -> burnHellforge(level.registryAccess(), recipeholder, blockEntity.items, blockEntity))) {
                            data[0] = true;
                        }
                    }
                    hellforgeMatched = true;
                }
            }

            if (hasItem && !hellforgeMatched) {
                ItemStack lastInput = itemStacks[blockEntity.lastCheckSlot];
                RecipeHolder<BlastingRecipe> recipeholder;
                SingleRecipeInput recipeInput;
                if (!lastInput.isEmpty() &&
                        (recipeholder = blockEntity.blasting.getRecipeFor(recipeInput = new SingleRecipeInput(lastInput), level).orElse(null)) != null &&
                        recipeholder.value().matches(recipeInput, level)
                ) {
                    doBlasting(level, blockEntity, recipeholder, lastInput, data, fuel);
                } else {
                    for (int i = 0; i < itemStacks.length; i++) {
                        ItemStack input = itemStacks[i];
                        if (input.isEmpty()) continue;
                        recipeholder = blockEntity.blasting.getRecipeFor(new SingleRecipeInput(input), level).orElse(null);
                        if (recipeholder != null) {
                            doBlasting(level, blockEntity, recipeholder, input, data, fuel);
                            blockEntity.lastCheckSlot = i;
                            break;
                        }
                    }
                }
            }

            if (!data[1] && !hellforgeMatched) {
                blockEntity.cookingProgress = 0;
            }

            if (isLit != blockEntity.isLit()) {
                data[0] = true;
                state = state.setValue(AbstractFurnaceBlock.LIT, blockEntity.useFuel());
                level.setBlockAndUpdate(pos, state);
            }

            if (data[0]) {
                setChanged(level, pos, state);
            }
        }

        private static void doBlasting(Level level, Entity blockEntity, RecipeHolder<BlastingRecipe> recipeholder, ItemStack lastInput, boolean[] data, ItemStack fuel) {
            if (!blockEntity.isLit() && canBlastingBurn(level.registryAccess(), recipeholder, blockEntity.items, lastInput)) {
                data[0] = doUpdateStatus(blockEntity, fuel);
            }
            if (canBlastingBurn(level.registryAccess(), recipeholder, blockEntity.items, lastInput)) {
                if (doUpdateProgress(blockEntity, level, recipeholder, () -> burnBlasting(level.registryAccess(), recipeholder, blockEntity.items, lastInput))) {
                    data[0] = true;
                }
            }
            data[1] = true;
        }

        private static boolean doUpdateProgress(Entity blockEntity, Level level, RecipeHolder<?> recipeholder, BooleanSupplier supplier) {
            if (++blockEntity.cookingProgress >= blockEntity.cookingTotalTime) {
                blockEntity.cookingProgress = 0;
                if (!blockEntity.isLit()) {
                    blockEntity.cookingTotalTime = getTotalCookTime(level, blockEntity);
                }
                if (supplier.getAsBoolean()) {
                    blockEntity.setRecipeUsed(recipeholder);
                }
                return true;
            }
            return false;
        }

        private static boolean doUpdateStatus(Entity blockEntity, ItemStack fuel) {
            blockEntity.litTime = blockEntity.getBurnDuration(fuel);
            blockEntity.litDuration = blockEntity.litTime;
            if (fuel.hasCraftingRemainingItem()) {
                blockEntity.items.set(FUEL_SLOT, fuel.getCraftingRemainingItem());
            } else if (fuel.isEmpty()) {
                blockEntity.useFuel = 0;
            } else {
                fuel.shrink(1);
                if (fuel.isEmpty()) {
                    blockEntity.items.set(FUEL_SLOT, fuel.getCraftingRemainingItem());
                }
                blockEntity.useFuel = 1;
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
                resetCookTime(this);
                setChanged();
            } else if (index == FUEL_SLOT) {
                this.useFuel = stack.isEmpty() ? 0 : 1;
                resetCookTime(this);
                setChanged();
            }
        }

        private static void resetCookTime(Entity entity) {
            if (!entity.isLit()) {
                entity.cookingTotalTime = getTotalCookTime(entity.level, entity);
                if (entity.items.get(FUEL_SLOT).isEmpty()) {
                    entity.cookingProgress = 0;
                }
            }
        }

        private static boolean canBlastingBurn(RegistryAccess registryAccess, RecipeHolder<BlastingRecipe> recipe, NonNullList<ItemStack> inventory, ItemStack input) {
            if (!input.isEmpty()) {
                ItemStack neoResult = recipe.value().assemble(new SingleRecipeInput(input), registryAccess);
                return canResultInsert(inventory, neoResult);
            } else {
                return false;
            }
        }

        private static boolean canResultInsert(NonNullList<ItemStack> inventory, ItemStack neoResult) {
            if (neoResult.isEmpty()) {
                return false;
            } else {
                ItemStack oldResult = inventory.get(RESULT_SLOT);
                if (oldResult.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItemSameComponents(oldResult, neoResult)) {
                    return false;
                } else {
                    return oldResult.getCount() + neoResult.getCount() <= LibUtils.MAX_STACK_SIZE && oldResult.getCount() + neoResult.getCount() <= oldResult.getMaxStackSize() || oldResult.getCount() + neoResult.getCount() <= neoResult.getMaxStackSize();
                }
            }
        }

        private static boolean burnBlasting(RegistryAccess registryAccess, RecipeHolder<BlastingRecipe> recipe, NonNullList<ItemStack> inventory, ItemStack input) {
            if (canBlastingBurn(registryAccess, recipe, inventory, input)) {
                ItemStack neoResult = recipe.value().assemble(new SingleRecipeInput(input), registryAccess);
                ItemStack oldResult = inventory.get(RESULT_SLOT);
                if (oldResult.isEmpty()) {
                    inventory.set(RESULT_SLOT, neoResult.copy());
                } else if (ItemStack.isSameItemSameComponents(oldResult, neoResult)) {
                    oldResult.grow(neoResult.getCount());
                }

                if (input.is(Blocks.WET_SPONGE.asItem()) && inventory.get(FUEL_SLOT).is(Items.BUCKET)) {
                    inventory.set(FUEL_SLOT, Items.WATER_BUCKET.getDefaultInstance());
                }

                input.shrink(1);
                return true;
            } else {
                return false;
            }
        }

        private static boolean canHellforgeBurn(RegistryAccess registryAccess, RecipeHolder<HellforgeRecipe> recipe, NonNullList<ItemStack> inventory, HellforgeBlock.Entity furnace) {
            ItemStack[] inputs = furnace.itemStacks;
            if ((!recipe.value().isRequiresFuel() || (furnace.useFuel() || furnace.isLit() || !furnace.getItem(FUEL_SLOT).isEmpty())) && Arrays.stream(inputs).anyMatch(itemStack -> !itemStack.isEmpty())) {
                ItemStack neoResult = recipe.value().getResultItem(registryAccess);
                return canResultInsert(inventory, neoResult);
            } else {
                return false;
            }
        }

        private static boolean burnHellforge(RegistryAccess registryAccess, RecipeHolder<HellforgeRecipe> recipe, NonNullList<ItemStack> inventory, HellforgeBlock.Entity furnace) {
            if (canHellforgeBurn(registryAccess, recipe, inventory, furnace)) {
                if (inventory.get(FUEL_SLOT).is(Items.BUCKET)) {
                    for (ItemStack input : furnace.itemStacks) {
                        if (input.is(Items.WET_SPONGE)) {
                            inventory.set(FUEL_SLOT, Items.WATER_BUCKET.getDefaultInstance());
                        }
                    }
                }
                ItemStack neoResult = recipe.value().assembleAndExtract(new ArrayRecipeInput(furnace.itemStacks), registryAccess);
                ItemStack oldResult = inventory.get(RESULT_SLOT);
                if (oldResult.isEmpty()) {
                    inventory.set(RESULT_SLOT, neoResult.copy());
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
                return fuel.getBurnTime(ModRecipes.HELLFORGE_TYPE.get()) / 2;
            }
        }

        protected boolean useFuel() {
            return useFuel == 1;
        }

        protected boolean isLit() {
            return litTime > 0;
        }

        protected ItemStack[] getItemStacks() {
            this.itemStacks[0] = items.get(0);
            this.itemStacks[1] = items.get(1);
            this.itemStacks[2] = items.get(2);
            this.itemStacks[3] = items.get(3);
            return itemStacks;
        }

        private static int getTotalCookTime(Level level, Entity blockEntity) {
            int time = blockEntity.hellforge.getRecipeFor(new ArrayRecipeInput(blockEntity.getItemStacks()), level).map(holder -> holder.value().getCookingTime()).orElse(100);
            if (!blockEntity.items.get(FUEL_SLOT).isEmpty()) {
                return time;
            }
            return time * 8;
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
        protected NonNullList<ItemStack> getItems() {
            if (getBlockState().getValue(StateProperties.HORIZONTAL_TWO_PART).isRight()) {
                return level != null && level.getBlockEntity(getBlockPos().relative(StateProperties.HorizontalTwoPart.getConnectedDirection(getBlockState()))) instanceof Entity entity ? entity.items : items;
            }
            return items;
        }

        @Override
        protected void setItems(NonNullList<ItemStack> items) {
            if (getBlockState().getValue(StateProperties.HORIZONTAL_TWO_PART).isRight()) {
                if (level != null && level.getBlockEntity(getBlockPos().relative(StateProperties.HorizontalTwoPart.getConnectedDirection(getBlockState()))) instanceof Entity entity) {
                    entity.items = items;
                    return;
                }
            }
            this.items = items;
        }

        @Override
        protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
            if (getBlockState().getValue(StateProperties.HORIZONTAL_TWO_PART).isBase()) {
                return new HellforgeMenu(containerId, inventory, this, dataAccess);
            } else if (level != null && level.getBlockEntity(getBlockPos().relative(StateProperties.HorizontalTwoPart.getConnectedDirection(getBlockState()))) instanceof Entity entity) {
                return new HellforgeMenu(containerId, inventory, entity, entity.dataAccess);
            }
            return new HellforgeMenu(containerId, inventory, this, dataAccess);
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
                return stack.getBurnTime(ModRecipes.HELLFORGE_TYPE.get()) > 0 || stack.is(Items.BUCKET) && !itemstack.is(Items.BUCKET);
            }
        }

        public void getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 popVec) {
            for (Object2IntMap.Entry<ResourceLocation> entry : recipesUsed.object2IntEntrySet()) {
                level.getRecipeManager().byKey(entry.getKey()).ifPresent(recipeHolder -> {
                    if (recipeHolder.value() instanceof HellforgeRecipe hellforgeRecipe) {
                        createExperience(level, popVec, entry.getIntValue(), hellforgeRecipe.getExperience());
                    } else if (recipeHolder.value() instanceof BlastingRecipe blastingRecipe) {
                        createExperience(level, popVec, entry.getIntValue(), blastingRecipe.getExperience());
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
        protected Component getDefaultName() {
            return Component.translatable("container.confluence.hellforge");
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
                this.litDuration = getBurnDuration(items.get(FUEL_SLOT));
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
