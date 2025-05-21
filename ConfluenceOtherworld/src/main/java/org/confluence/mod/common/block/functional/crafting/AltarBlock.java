package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
import org.confluence.mod.client.model.block.AltarBlockModel;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.HammerItems;
import org.confluence.mod.common.recipe.AltarRecipe;
import org.confluence.mod.mixed.IMinecraftServer;
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

public class AltarBlock extends BaseEntityBlock {
    public static final MapCodec<AltarBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            propertiesCodec(),
            Variant.CODEC.fieldOf("variant").orElse(Variant.DEMON).forGetter(block -> block.variant)
    ).apply(instance, AltarBlock::new));
    public static final VoxelShape SHAPE = Shapes.box(-0.125, 0.0, -0.125, 1.125, 0.8, 1.125);
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
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public float getDestroyProgress(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
        return pPlayer.getMainHandItem().is(ModTags.Items.HAMMERS) ? super.getDestroyProgress(pState, pPlayer, pLevel, pPos) : 0.0F;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pBlockEntity, ItemStack pTool) {
        super.playerDestroy(pLevel, pPlayer, pPos, pState, pBlockEntity, pTool);
        if (pPlayer instanceof ServerPlayer serverPlayer && IMinecraftServer.isHardmode(serverPlayer.server)) {
            ServerLevel serverLevel = serverPlayer.serverLevel();
            ConfluenceData data = ConfluenceData.get(serverLevel);
            if (data.increaseRevealStep(serverLevel)) {
                serverLevel.getServer().getPlayerList().broadcastSystemMessage(Component.translatable(
                        "event.confluence.reveal_step" + data.getRevealStep()
                ).withColor(GlobalColors.MESSAGE.get()), false);
            }
            if (serverPlayer.getMainHandItem().is(HammerItems.PWNHAMMER)) {
                AdvancementHolder holder = serverLevel.getServer().getAdvancements().get(AchievementOffsetLoader.asAchievement("begone_evil"));
                if (holder != null) {
                    serverPlayer.getAdvancements().award(holder, "never");
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pLevel.getBlockEntity(pPos) instanceof Entity entity) {
            Containers.dropContents(pLevel, pPos, entity.itemHandler.getItems());
            pLevel.removeBlockEntity(pPos);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof Entity entity) { // 放/取物品
            if (player.isCrouching()) { // 取物品
                player.addItem(entity.takeItem(-1));
            } else { // 存物品
                player.setItemInHand(hand, entity.addItem(player.getItemInHand(hand)));
                if (CommonConfigs.ALTAR_TIPS.get()) {
                    player.sendSystemMessage(Component.translatable("message.confluence.altar_tips.0"));
                    player.sendSystemMessage(Component.translatable("message.confluence.altar_tips.1"));
                }
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    public static void onLeftClick(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) { // 合成
        if (pLevel instanceof ServerLevel serverLevel && pState.getBlock() instanceof AltarBlock && pLevel.getBlockEntity(pPos) instanceof Entity entity) {
            RecipeManager recipeManager = serverLevel.getServer().getRecipeManager();
            if (pPlayer.isCrouching()) { // 全部合成
                List<RecipeHolder<AltarRecipe>> recipes;
                boolean crafted = false;
                while (!(recipes = recipeManager.getRecipesFor(ModRecipes.ALTAR_TYPE.get(), entity.itemHandler, pLevel)).isEmpty()) {
                    crafted = true;
                    AltarRecipe recipe = recipes.getFirst().value(); // 先只取第一个合成表
                    ItemStack result = recipe.assembleAndExtract(entity.itemHandler, pLevel.registryAccess());
                    LibUtils.createItemEntity(result, pPos.getX() + 0.5, pPos.getY() + 0.75, pPos.getZ() + 0.5, pLevel, 0);
                }
                if (crafted) entity.playAnimation(serverLevel, pPos);
            } else { // 合成一个
                List<RecipeHolder<AltarRecipe>> recipes = recipeManager.getRecipesFor(ModRecipes.ALTAR_TYPE.get(), entity.itemHandler, pLevel); // todo 多态合成
                if (recipes.isEmpty()) return;
                AltarRecipe recipe = recipes.getFirst().value(); // 先只取第一个合成表
                ItemStack result = recipe.assembleAndExtract(entity.itemHandler, pLevel.registryAccess());
                LibUtils.createItemEntity(result, pPos.getX() + 0.5, pPos.getY() + 0.75, pPos.getZ() + 0.5, pLevel, 0);
                entity.playAnimation(serverLevel, pPos);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new Entity(pPos, pState).setVariant(variant);
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

    public static class Entity extends BaseContainerBlockEntity implements GeoBlockEntity {
        public static final int CONTAINER_SIZE = 6;
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
        private final ItemStackHandlerRecipeInput itemHandler; // 5 Inputs and 1 Output.
        private Variant variant;

        public Entity(BlockPos pPos, BlockState pBlockState) {
            super(FunctionalBlocks.ALTAR_BLOCK_ENTITY.get(), pPos, pBlockState);
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
                    return result;
                }
            }
            if (firstEmptySlot != -1) {
                itemHandler.setStackInSlot(firstEmptySlot, toAdd);
                setChanged();
                return ItemStack.EMPTY;
            }
            return ItemStack.EMPTY;
        }

        public ItemStack takeItem(int slot) {
            if (slot == -1) {
                for (int i = 0; i < 5; i++) {
                    if (!itemHandler.getStackInSlot(i).isEmpty()) {
                        ItemStack stack = itemHandler.extractItem(i, 64, false);
                        setChanged();
                        return stack;
                    }
                }
                return ItemStack.EMPTY;
            } else {
                ItemStack stack = itemHandler.getStackInSlot(slot).copy();
                itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
                setChanged();
                return stack;
            }
        }

        Entity setVariant(Variant variant) {
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
            variant = Variant.byId(nbt.getInt("variant"));
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

    public static class Item extends BlockItem implements GeoItem {
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

        public Item(AltarBlock pBlock) {
            super(pBlock, new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.PURPLE));
        }

        @Override
        public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
            consumer.accept(new GeoRenderProvider() {
                private GeoItemRenderer<Item> renderer;

                @Override
                public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                    if (renderer == null) {
                        this.renderer = new GeoItemRenderer<>(new GeoModel<>() {
                            @Override
                            public ResourceLocation getModelResource(AltarBlock.Item animatable) {
                                return AltarBlockModel.MODELS[animatable.getVariant().getId()];
                            }

                            @Override
                            public ResourceLocation getTextureResource(AltarBlock.Item animatable) {
                                return AltarBlockModel.TEXTURES[animatable.getVariant().getId()];
                            }

                            @Override
                            public ResourceLocation getAnimationResource(AltarBlock.Item animatable) {
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
