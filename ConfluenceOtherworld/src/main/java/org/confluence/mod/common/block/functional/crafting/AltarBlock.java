package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.recipe.ItemStackHandlerRecipeInput;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.block.AltarBlockModel;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.HammerItems;
import org.confluence.mod.common.recipe.AltarRecipe;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

public class AltarBlock extends BaseEntityBlock {
    public static final MapCodec<AltarBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            propertiesCodec(),
            Variant.CODEC.fieldOf("variant").orElse(Variant.DEMON).forGetter(block -> block.variant)
    ).apply(instance, AltarBlock::new));
    public static final VoxelShape SHAPE = Shapes.box(-0.125, 0.0, -0.125, 1.125, 0.8, 1.125);
    private static final Component TIPS;

    static {
        ResourceLocation fontId = Confluence.asResource("button");
        UnaryOperator<Style> button = style -> style.withFont(fontId);
        TIPS = Component.empty()
                .append(Component.literal("2").withStyle(button))
                .append(Component.translatable("message.confluence.altar_tips.0"))
                .append(Component.literal("23").withStyle(button))
                .append(Component.translatable("message.confluence.altar_tips.1"))
                .append(Component.literal("1").withStyle(button))
                .append(Component.translatable("message.confluence.altar_tips.2"))
                .append(Component.literal("13").withStyle(button))
                .append(Component.translatable("message.confluence.altar_tips.3"));
    }

    private final Variant variant;

    public AltarBlock(Properties properties, Variant variant) {
        super(properties);
        this.variant = variant;
    }

    @Override
    protected MapCodec<AltarBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return player.getMainHandItem().is(ModTags.Items.TOOLS_HAMMER) ? super.getDestroyProgress(state, player, level, pos) : 0.0F;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (player instanceof ServerPlayer serverPlayer && IMinecraftServer.isHardmode(serverPlayer.server)) {
            ServerLevel serverLevel = serverPlayer.serverLevel();
            ConfluenceData data = ConfluenceData.get(serverLevel);
            if (data.increaseRevealStep()) {
                serverLevel.getServer().getPlayerList().broadcastSystemMessage(Component.translatable(
                        "event.confluence.reveal_step" + data.getRevealStep()
                ).withColor(GlobalColors.MESSAGE.get()), false);
            }
            if (tool.is(HammerItems.PWNHAMMER)) {
                AchievementUtils.awardAchievement(serverPlayer, "begone_evil");
            }
            RandomSource random = player.getRandom();
            int wraithAmount = random.nextInt(2) + 1;
            for (int i = 0; i < wraithAmount; i++) {
                TEMonsterEntities.WRAITH.get().spawn(serverLevel, pos.offset(
                        Mth.randomBetweenInclusive(random, -15, 15),
                        Mth.randomBetweenInclusive(random, -15, 15),
                        Mth.randomBetweenInclusive(random, -15, 15)
                ), MobSpawnType.MOB_SUMMONED);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof BEntity entity) {
            Containers.dropContents(level, pos, entity.itemHandler.getItems());
            level.removeBlockEntity(pos);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof BEntity entity) {
            if (player.isCrouching()) {
                player.addItem(entity.takeItem(-1));
            } else {
                player.setItemInHand(hand, entity.addItem(player.getItemInHand(hand)));
                if (CommonConfigs.ALTAR_TIPS.get()) {
                    player.displayClientMessage(TIPS, true);
                }
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    public static void onLeftClick(BlockState state, Level level, BlockPos pos, Player player) {
        if (level instanceof ServerLevel serverLevel && state.getBlock() instanceof AltarBlock && level.getBlockEntity(pos) instanceof BEntity entity) {
            RecipeManager recipeManager = serverLevel.getServer().getRecipeManager();
            if (player.isCrouching()) {
                List<RecipeHolder<AltarRecipe>> recipes;
                boolean crafted = false;
                while (!(recipes = recipeManager.getRecipesFor(ModRecipes.ALTAR_TYPE.get(), entity.itemHandler, level)).isEmpty()) {
                    crafted = true;
                    AltarRecipe recipe = recipes.getFirst().value();
                    ItemStack result = recipe.assembleAndExtract(entity.itemHandler, level.registryAccess());
                    LibUtils.createItemEntity(result, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, level, 0);
                }
                if (crafted) {
                    entity.playAnimation(serverLevel, pos);
                    entity.markUpdated();
                }
            } else {
                List<RecipeHolder<AltarRecipe>> recipes = recipeManager.getRecipesFor(ModRecipes.ALTAR_TYPE.get(), entity.itemHandler, level);
                if (recipes.isEmpty()) return;
                AltarRecipe recipe = recipes.getFirst().value();
                ItemStack result = recipe.assembleAndExtract(entity.itemHandler, level.registryAccess());
                LibUtils.createItemEntity(result, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, level, 0);
                entity.playAnimation(serverLevel, pos);
                entity.markUpdated();
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state).setVariant(variant);
    }

    public static boolean hurtPlayerIfBrokenNotAllowed(Player player, BlockState blockState) {
        if (!player.hasInfiniteMaterials() &&
                blockState.getBlock() instanceof AltarBlock &&
                !player.getMainHandItem().is(ModTags.Items.ABLE_TO_DESTROY_ALTAR)
        ) {
            player.hurt(player.damageSources().fellOutOfWorld(), player.getMaxHealth() / 2);
            return true;
        }
        return false;
    }

    public static class BEntity extends BaseContainerBlockEntity implements GeoBlockEntity {
        public static final int CONTAINER_SIZE = 6;
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
        private final ItemStackHandlerRecipeInput itemHandler; // 5 Inputs and 1 Output.
        private Variant variant;

        public BEntity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.ALTAR_BLOCK_ENTITY.get(), pos, blockState);
            this.itemHandler = new ItemStackHandlerRecipeInput(this, CONTAINER_SIZE);
            SingletonGeoAnimatable.registerSyncedAnimatable(this);
        }

        public ItemStack addItem(ItemStack toAdd) {
            int firstEmptySlot = -1;
            for (int i = 0; i < 5; i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (firstEmptySlot == -1 && stack.isEmpty()) {
                    firstEmptySlot = i;
                }
                if (ItemStack.isSameItemSameComponents(stack, toAdd)) {
                    ItemStack result = itemHandler.insertItem(i, toAdd, false);
                    setChanged();
                    markUpdated();
                    return result;
                }
            }
            if (firstEmptySlot != -1) {
                itemHandler.setStackInSlot(firstEmptySlot, toAdd);
                setChanged();
                markUpdated();
                return ItemStack.EMPTY;
            }
            return toAdd;
        }

        public ItemStack takeItem(int slot) {
            if (slot == -1) {
                for (int i = 0; i < 5; i++) {
                    ItemStack stack = itemHandler.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        stack = itemHandler.extractItem(i, stack.getMaxStackSize(), false);
                        setChanged();
                        markUpdated();
                        return stack;
                    }
                }
                return ItemStack.EMPTY;
            } else {
                ItemStack stack = itemHandler.getStackInSlot(slot).copy();
                itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
                setChanged();
                markUpdated();
                return stack;
            }
        }

        BEntity setVariant(Variant variant) {
            this.variant = variant;
            markUpdated();
            return this;
        }

        public Variant getVariant() {
            return variant;
        }

        @Override
        public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
            super.loadAdditional(nbt, registries);
            this.variant = Variant.byId(nbt.getInt("variant"));
            itemHandler.setItems(NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY));
            ContainerHelper.loadAllItems(nbt, itemHandler.getItems(), registries);
        }

        @Override
        protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
            super.saveAdditional(nbt, registries);
            nbt.putInt("variant", variant.id);
            ContainerHelper.saveAllItems(nbt, itemHandler.getItems(), registries);
        }

        @Override
        protected Component getDefaultName() {
            return Component.empty();
        }

        @Override
        protected NonNullList<ItemStack> getItems() {
            return itemHandler.getItems();
        }

        @Override
        protected void setItems(NonNullList<ItemStack> items) {
            itemHandler.setItems(items);
        }

        @Override
        public boolean canOpen(Player player) {
            return false;
        }

        @Override
        protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ClientboundBlockEntityDataPacket getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("variant", variant.id);
            ContainerHelper.saveAllItems(nbt, itemHandler.getItems(), registries);
            return nbt;
        }

        @Override
        public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
            this.variant = Variant.byId(tag.getInt("variant"));
            itemHandler.setItems(NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY));
            ContainerHelper.loadAllItems(tag, itemHandler.getItems(), lookupProvider);
        }

        public void markUpdated() {
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), UPDATE_CLIENTS);
            }
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            controllers.add(new AnimationController<>(this, "controller", state ->
                    state.setAndContinue(RawAnimation.begin().thenLoop("default")))
                    .triggerableAnim("crafting", RawAnimation.begin().thenPlay("crafting"))
            );
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return CACHE;
        }

        private void playAnimation(ServerLevel level, BlockPos pos) {
            triggerAnim("controller", "crafting");
            switch (variant) {
                case DEMON -> level.sendParticles(ParticleTypes.SOUL,
                        pos.getX() + 0.5F,
                        pos.getY() + 0.75F,
                        pos.getZ() + 0.5F,
                        20, 0.0F, 0.0F, 0.0F, 0.02F);
                case CRIMSON -> level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.defaultBlockState()),
                        pos.getX() + 0.5F,
                        pos.getY() + 0.75F,
                        pos.getZ() + 0.5F,
                        500, 0F, 0.0625F, 0F, 0.25F);
            }
        }

        @Override
        public int getContainerSize() {
            return itemHandler.size();
        }
    }

    public static class BItem extends BlockItem implements GeoItem {
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

        public BItem(AltarBlock block) {
            super(block, new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.PURPLE));
        }

        @Override
        public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
            consumer.accept(new GeoRenderProvider() {
                private GeoItemRenderer<BItem> renderer;

                @Override
                public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                    if (renderer == null) {
                        this.renderer = new GeoItemRenderer<>(new GeoModel<>() {
                            @Override
                            public ResourceLocation getModelResource(BItem animatable) {
                                return AltarBlockModel.MODELS[animatable.getVariant().getId()];
                            }

                            @Override
                            public ResourceLocation getTextureResource(BItem animatable) {
                                return AltarBlockModel.TEXTURES[animatable.getVariant().getId()];
                            }

                            @Override
                            public ResourceLocation getAnimationResource(BItem animatable) {
                                return AltarBlockModel.ANIMATIONS[animatable.getVariant().getId()];
                            }
                        });
                    }
                    return renderer;
                }
            });
        }

        public Variant getVariant() {
            return ((AltarBlock) getBlock()).variant;
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return CACHE;
        }
    }

    public enum Variant implements StringRepresentable {
        DEMON(0, "demon"),
        CRIMSON(1, "crimson");

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
        final int id;
        private final String name;

        Variant(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public static Variant byId(int pId) {
            return BY_ID.apply(pId);
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
