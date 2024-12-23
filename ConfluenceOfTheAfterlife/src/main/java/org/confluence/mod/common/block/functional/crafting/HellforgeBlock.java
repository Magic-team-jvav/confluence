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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
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
import org.confluence.mod.common.block.StateProperties;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.menu.HellforgeMenu;
import org.confluence.mod.common.recipe.ArrayRecipeInput;
import org.confluence.mod.common.recipe.HellforgeRecipe;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

import static org.confluence.mod.common.menu.HellforgeMenu.*;

public class HellforgeBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<HellforgeBlock> CODEC = simpleCodec(HellforgeBlock::new);
    private static final VoxelShape BASE_SHAPE_SOUTH = box(3, 0, 3, 16, 16, 13);
    private static final VoxelShape BASE_SHAPE_WEST = box(3, 0, 3, 13, 16, 16);
    private static final VoxelShape BASE_SHAPE_NORTH = box(0, 0, 3, 13, 16, 13);
    private static final VoxelShape BASE_SHAPE_EAST = box(3, 0, 0, 13, 16, 13);
    private static final VoxelShape RIGHT_SHAPE_SOUTH = box(0, 0, 3, 13, 16, 13);
    private static final VoxelShape RIGHT_SHAPE_WEST = box(3, 0, 0, 13, 16, 13);
    private static final VoxelShape RIGHT_SHAPE_NORTH = box(3, 0, 3, 16, 16, 13);
    private static final VoxelShape RIGHT_SHAPE_EAST = box(3, 0, 3, 13, 16, 16);
    private static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{BASE_SHAPE_SOUTH, BASE_SHAPE_WEST, BASE_SHAPE_NORTH, BASE_SHAPE_EAST};
    private static final VoxelShape[] RIGHT_SHAPES = new VoxelShape[]{RIGHT_SHAPE_SOUTH, RIGHT_SHAPE_WEST, RIGHT_SHAPE_NORTH, RIGHT_SHAPE_EAST};

    public HellforgeBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(StateProperties.HORIZONTAL_TWO_PART, StateProperties.HorizontalTwoPart.BASE)
                .setValue(FACING, Direction.NORTH)
                .setValue(BlockStateProperties.LIT, false));
    }

    @Override
    protected @NotNull MapCodec<HellforgeBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
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
    protected boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(@NotNull BlockState blockState, Level level, @NotNull BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        int index = pState.getValue(FACING).get2DDataValue();
        return pState.getValue(StateProperties.HORIZONTAL_TWO_PART).isBase() ? BASE_SHAPES[index] : RIGHT_SHAPES[index];
    }

    @Override
    public void setPlacedBy(Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @Nullable LivingEntity pPlacer, @NotNull ItemStack pStack) {
        if (!pLevel.isClientSide) {
            BlockPos relativePos = pPos.relative(StateProperties.HorizontalTwoPart.getConnectedDirection(pState));
            pLevel.setBlockAndUpdate(relativePos, defaultBlockState().setValue(StateProperties.HORIZONTAL_TWO_PART, StateProperties.HorizontalTwoPart.RIGHT).setValue(FACING, pState.getValue(FACING)));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockState blockState = defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        BlockPos relativePos = pContext.getClickedPos().relative(StateProperties.HorizontalTwoPart.getConnectedDirection(blockState));
        return level.getBlockState(relativePos).canBeReplaced(pContext) && level.getWorldBorder().isWithinBounds(relativePos) ? blockState : null;
    }

    @Override
    public void onRemove(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
            pLevel.setBlockAndUpdate(pPos.relative(StateProperties.HorizontalTwoPart.getConnectedDirection(pState)), Blocks.AIR.defaultBlockState());
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof Entity entity && pState.getValue(StateProperties.HORIZONTAL_TWO_PART).isBase()) {
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(StateProperties.HORIZONTAL_TWO_PART, FACING, BlockStateProperties.LIT);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide || state.getValue(StateProperties.HORIZONTAL_TWO_PART).isRight() ? null : ModUtils.getTicker(blockEntityType, FunctionalBlocks.HELLFORGE_ENTITY.get(), Entity::serverTick);
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
                return HellforgeMenu.DATA_COUNT;
            }
        };
        private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
        private final RecipeManager.CachedCheck<RecipeInput, HellforgeRecipe> hellforge;
        private final RecipeManager.CachedCheck<SingleRecipeInput, BlastingRecipe> blasting;
        private final ItemStack[] inputs = new ItemStack[4];

        public Entity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.HELLFORGE_ENTITY.get(), pos, blockState);
            this.hellforge = RecipeManager.createCheck(ModRecipes.HELLFORGE_TYPE.get());
            this.blasting = RecipeManager.createCheck(RecipeType.BLASTING);
        }

        public static void serverTick(Level level, BlockPos pos, BlockState state, HellforgeBlock.Entity blockEntity) {
            boolean isLit = blockEntity.isLit();
            boolean update = false;
            if (blockEntity.isLit()) {
                blockEntity.litTime--;
                if (!blockEntity.isLit()) {
                    blockEntity.cookingTotalTime = getTotalCookTime(level, blockEntity);
                    blockEntity.cookingProgress = 0;
                }
            }

            ItemStack[] inputs = blockEntity.getInputs();
            ItemStack fuel = blockEntity.items.get(FUEL_SLOT);
            List<ItemStack> list = Arrays.stream(inputs).filter(itemStack -> !itemStack.isEmpty()).toList();
            boolean hasInput = !list.isEmpty();
            boolean hellforgeMatched = false;

            if (hasInput) {
                RecipeHolder<HellforgeRecipe> recipeholder = blockEntity.hellforge.getRecipeFor(new ArrayRecipeInput(inputs), level).orElse(null);
                if (recipeholder != null) {
                    int maxStackSize = blockEntity.getMaxStackSize();
                    if (!blockEntity.isLit() && canHellforgeBurn(level.registryAccess(), recipeholder, blockEntity.items, maxStackSize, blockEntity)) {
                        update = doUpdateStatus(blockEntity, fuel);
                    }
                    if (canHellforgeBurn(level.registryAccess(), recipeholder, blockEntity.items, maxStackSize, blockEntity)) {
                        if (doUpdateProgress(blockEntity, level, recipeholder, () -> burnHellforge(level.registryAccess(), recipeholder, blockEntity.items, maxStackSize, blockEntity))) {
                            update = true;
                        }
                    }
                    hellforgeMatched = true;
                }
            }

            if (hasInput && !hellforgeMatched) {
                for (ItemStack input : list) {
                    RecipeHolder<BlastingRecipe> recipeholder = blockEntity.blasting.getRecipeFor(new SingleRecipeInput(input), level).orElse(null);
                    if (recipeholder != null) {
                        int maxStackSize = blockEntity.getMaxStackSize();
                        if (!blockEntity.isLit() && canBlastingBurn(level.registryAccess(), recipeholder, blockEntity.items, maxStackSize, input)) {
                            update = doUpdateStatus(blockEntity, fuel);
                        }
                        if (canBlastingBurn(level.registryAccess(), recipeholder, blockEntity.items, maxStackSize, input)) {
                            if (doUpdateProgress(blockEntity, level, recipeholder, () -> burnBlasting(level.registryAccess(), recipeholder, blockEntity.items, maxStackSize, input))) {
                                update = true;
                            }
                        }
                        break;
                    }
                }
            }

            if (isLit != blockEntity.isLit()) {
                update = true;
                state = state.setValue(AbstractFurnaceBlock.LIT, blockEntity.useFuel());
                level.setBlockAndUpdate(pos, state);
            }

            if (update) {
                setChanged(level, pos, state);
            }
        }

        private static boolean doUpdateProgress(Entity blockEntity, Level level, RecipeHolder<?> recipeholder, BooleanSupplier supplier) {
            blockEntity.cookingProgress++;
            if (blockEntity.cookingProgress == blockEntity.cookingTotalTime) {
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
            boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stack);
            getItems().set(index, stack);
            stack.limitSize(getMaxStackSize(stack));
            if (index < 4 && !flag) {
                this.cookingTotalTime = getTotalCookTime(level, this);
                this.cookingProgress = 0;
                setChanged();
            } else if (index == FUEL_SLOT) {
                this.useFuel = stack.isEmpty() ? 0 : 1;
                if (!isLit()) {
                    this.cookingTotalTime = getTotalCookTime(level, this);
                    this.cookingProgress = 0;
                }
                setChanged();
            }
        }

        private static boolean canBlastingBurn(RegistryAccess registryAccess, RecipeHolder<BlastingRecipe> recipe, NonNullList<ItemStack> inventory, int maxStackSize, ItemStack input) {
            if (!input.isEmpty()) {
                ItemStack neoResult = recipe.value().assemble(new SingleRecipeInput(input), registryAccess);
                return canResultInsert(inventory, maxStackSize, neoResult);
            } else {
                return false;
            }
        }

        private static boolean canResultInsert(NonNullList<ItemStack> inventory, int maxStackSize, ItemStack neoResult) {
            if (neoResult.isEmpty()) {
                return false;
            } else {
                ItemStack oldResult = inventory.get(RESULT_SLOT);
                if (oldResult.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItemSameComponents(oldResult, neoResult)) {
                    return false;
                } else {
                    return oldResult.getCount() + neoResult.getCount() <= maxStackSize && oldResult.getCount() + neoResult.getCount() <= oldResult.getMaxStackSize() || oldResult.getCount() + neoResult.getCount() <= neoResult.getMaxStackSize();
                }
            }
        }

        private static boolean burnBlasting(RegistryAccess registryAccess, RecipeHolder<BlastingRecipe> recipe, NonNullList<ItemStack> inventory, int maxStackSize, ItemStack input) {
            if (canBlastingBurn(registryAccess, recipe, inventory, maxStackSize, input)) {
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

        private static boolean canHellforgeBurn(RegistryAccess registryAccess, RecipeHolder<HellforgeRecipe> recipe, NonNullList<ItemStack> inventory, int maxStackSize, HellforgeBlock.Entity furnace) {
            ItemStack[] inputs = furnace.inputs;
            if ((!recipe.value().isRequiresFuel() || furnace.useFuel()) && Arrays.stream(inputs).anyMatch(itemStack -> !itemStack.isEmpty())) {
                ItemStack neoResult = recipe.value().getResultItem(registryAccess);
                return canResultInsert(inventory, maxStackSize, neoResult);
            } else {
                return false;
            }
        }

        private static boolean burnHellforge(RegistryAccess registryAccess, RecipeHolder<HellforgeRecipe> recipe, NonNullList<ItemStack> inventory, int maxStackSize, HellforgeBlock.Entity furnace) {
            if (canHellforgeBurn(registryAccess, recipe, inventory, maxStackSize, furnace)) {
                for (ItemStack input : furnace.inputs) {
                    if (input.is(Blocks.WET_SPONGE.asItem()) && inventory.get(FUEL_SLOT).is(Items.BUCKET)) {
                        inventory.set(FUEL_SLOT, Items.WATER_BUCKET.getDefaultInstance());
                    }
                }
                ItemStack neoResult = recipe.value().assemble(new ArrayRecipeInput(furnace.inputs), registryAccess);
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

        protected ItemStack[] getInputs() {
            this.inputs[0] = items.get(0);
            this.inputs[1] = items.get(1);
            this.inputs[2] = items.get(2);
            this.inputs[3] = items.get(3);
            return inputs;
        }

        private static int getTotalCookTime(Level level, HellforgeBlock.Entity blockEntity) {
            int time = blockEntity.hellforge.getRecipeFor(new ArrayRecipeInput(blockEntity.getInputs()), level).map(holder -> holder.value().getCookingTime()).orElse(100);
            if (!blockEntity.items.get(FUEL_SLOT).isEmpty()) {
                return time;
            }
            return time * 8;
        }

        @Override
        public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
            if (side == Direction.DOWN) {
                return SLOTS_FOR_DOWN;
            } else {
                return side == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
            }
        }

        @Override
        public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStack, @Nullable Direction direction) {
            return canPlaceItem(index, itemStack);
        }

        @Override
        public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
            return direction != Direction.DOWN || index != 1 || stack.is(Items.WATER_BUCKET) || stack.is(Items.BUCKET);
        }

        @Override
        public int getContainerSize() {
            return items.size();
        }

        @Override
        protected @NotNull NonNullList<ItemStack> getItems() {
            if (getBlockState().getValue(StateProperties.HORIZONTAL_TWO_PART).isRight()) {
                return level != null && level.getBlockEntity(getBlockPos().relative(StateProperties.HorizontalTwoPart.getConnectedDirection(getBlockState()))) instanceof Entity entity ? entity.items : items;
            }
            return items;
        }

        @Override
        protected void setItems(@NotNull NonNullList<ItemStack> items) {
            if (getBlockState().getValue(StateProperties.HORIZONTAL_TWO_PART).isRight()) {
                if (level != null && level.getBlockEntity(getBlockPos().relative(StateProperties.HorizontalTwoPart.getConnectedDirection(getBlockState()))) instanceof Entity entity) {
                    entity.items = items;
                    return;
                }
            }
            this.items = items;
        }

        @Override
        protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
            if (getBlockState().getValue(StateProperties.HORIZONTAL_TWO_PART).isBase()) {
                return new HellforgeMenu(containerId, inventory, this, dataAccess);
            } else if (level != null && level.getBlockEntity(getBlockPos().relative(StateProperties.HorizontalTwoPart.getConnectedDirection(getBlockState()))) instanceof Entity entity) {
                return new HellforgeMenu(containerId, inventory, entity, entity.dataAccess);
            }
            return new HellforgeMenu(containerId, inventory, this, dataAccess);
        }

        @Override
        public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
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
                ResourceLocation resourcelocation = recipe.id();
                recipesUsed.addTo(resourcelocation, 1);
            }
        }

        @Override
        public @Nullable RecipeHolder<?> getRecipeUsed() {
            return null;
        }

        @Override
        protected @NotNull Component getDefaultName() {
            return Component.translatable("container.confluence.hellforge");
        }

        @Override
        protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
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
        protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
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
