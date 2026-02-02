package org.confluence.terra_furniture.common.block.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.common.block.HorizontalDirectionalWithVerticalTwoPartBlock;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.terra_furniture.api.client.model.CacheItemRefBlockModel;
import org.confluence.terra_furniture.api.client.renderer.block.IRenderFunctionHook;
import org.confluence.terra_furniture.api.client.renderer.item.BaseGeoItemRendererProvider;
import org.confluence.terra_furniture.common.block.func.be.BaseSwayingBE;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.network.s2c.PlayerCrossDeltaS2C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;
import java.util.function.Consumer;

public class HangingPotBlock extends HorizontalDirectionalWithVerticalTwoPartBlock implements EntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public HangingPotBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PART, StateProperties.VerticalTwoPart.UP).setValue(LIT, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return blockState.getValue(PART).isBase() ? new BEntity(blockPos, blockState) : null;
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        BlockPos basePos = toBase(state, pos);
        if (level.isClientSide && level.getBlockEntity(basePos) instanceof BEntity blockEntity) {
            blockEntity.applyMovingAffectedDelta(entity.getDeltaMovement());
        } else if (!level.isClientSide && entity instanceof Player) {
            PacketDistributor.sendToPlayersTrackingEntity(entity, new PlayerCrossDeltaS2C(entity.getPosition(1).subtract(entity.getPosition(0)), basePos));
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState resultState = super.getStateForPlacement(context);
        if (resultState != null)  return resultState.setValue(LIT, Boolean.FALSE);
        return null;
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() && state.getValue(PART).isBase() ?  (level1, pos, blockState, t) -> {
            if (t instanceof BEntity blockEntity) {
                blockEntity.tickAtClient();
            }
        } : null;
    }

    public void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos, boolean lit) {
        if (state.getValue(LIT) == lit) return;
        level.playSound(
            null, pos, !state.getValue(LIT) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS
        );
        level.setBlock(pos, level.getBlockState(pos).setValue(LIT, lit), 19);
        BlockPos another = state.getValue(PART).isBase() ? pos.above() : pos.below();
        level.setBlock(another, level.getBlockState(another).setValue(LIT, lit), 19);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockPos basePos = toBase(state, pos);
        if (!level.isClientSide && level.getBlockEntity(basePos) instanceof BEntity bEntity) {
            checkAndFlip(state, (ServerLevel) level, pos, false);
            if (bEntity.getFeatureStack().isEmpty()) return InteractionResult.FAIL;
            else {
                bEntity.drops();
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos basePos = toBase(state, pos);
        if (!level.isClientSide && level.getBlockEntity(basePos) instanceof BEntity bEntity) {
            if (bEntity.placeFeature(player, player.getItemInHand(hand))) {
                if (bEntity.getFeatureStack().getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TorchBlock) checkAndFlip(state, (ServerLevel) level, pos, true);
                return ItemInteractionResult.SUCCESS;
            }
        }
        if (player.getItemInHand(hand).isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP && !this.canSurvive(state, level, pos)) return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(PART).isUpper()) {
            BlockPos suspendingPos = pos.above();
            return level.getBlockState(suspendingPos).isFaceSturdy(level, suspendingPos, Direction.DOWN);
        }
        else return super.canSurvive(state, level, pos);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return true;
    }

    public BlockPos toBase(BlockState state, BlockPos pos) {
        return state.getValue(PART).isBase() ? pos : pos.below();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.is(newState.getBlock())) return;
        BlockPos relative = toBase(state, pos);
        level.destroyBlock(relative, false);
        if (state.hasBlockEntity()) {
            if (level.getBlockEntity(pos) instanceof BEntity bEntity) bEntity.drops();
            level.removeBlockEntity(pos);
        }
    }

    public static final class BEntity extends BaseSwayingBE implements GeoBlockEntity {
        private static final Vec3 ANCHOR = new Vec3(0.5, 2, 0.5);
        private final ItemStackHandler itemStackHandler = new ItemStackHandler(1){
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
            }
            @Override
            public void setSize(int size) {
                super.setSize(1);
            }
        };
        public BEntity(BlockPos pos, BlockState blockState) {
            super(TFBlocks.HANGING_POT_ENTITY.get(), pos, blockState);
            controller.setDAMPING(0.96f);
            movingFactor = 0.15f;
        }

        private Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemStackHandler);
        public static final String INVENTORY = "inventory";
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

        /**You can't change this output */
        public ItemStack getFeatureStack() {
            return itemStackHandler.getStackInSlot(0).copy();
        }

        public void changeFeature(ItemStack stack) {
            itemStackHandler.setStackInSlot(0, stack);
            this.markUpdated();
        }

        public boolean placeFeature(Entity entity, ItemStack stack) {
            if (!getFeatureStack().isEmpty() || !canBeHold(stack)) return false;
            changeFeature(stack.split(1));
            if (this.level != null) {
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(entity, this.getBlockState()));
            }
            return true;
        }

        public boolean canBeHold(ItemStack stack) {
            if (!(stack.getItem() instanceof BlockItem blockItem)) return false;
            Block block = blockItem.getBlock();

            FlowerPotBlock potBlock = (FlowerPotBlock) Blocks.FLOWER_POT;
            BlockState state = potBlock.getEmptyPot().getFullPotsView().getOrDefault(BuiltInRegistries.BLOCK.getKey(block), () -> Blocks.AIR).get().defaultBlockState();
            if (!state.isAir()) return true;
            if (block instanceof BushBlock) return true;
            return block.equals(Blocks.TORCH) || block.equals(Blocks.SOUL_TORCH);
        }

        private void markUpdated() {
            this.setChanged();
            Objects.requireNonNull(this.getLevel()).sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }

        public void drops(){
            SimpleContainer inventory = new SimpleContainer(1);
            inventory.setItem(0, getFeatureStack());
            Containers.dropContents(Objects.requireNonNull(this.getLevel()), this.worldPosition, inventory);
            changeFeature(ItemStack.EMPTY);
            markUpdated();
        }

        public Lazy<IItemHandler> getLazyItemHandler() {
            return lazyItemHandler;
        }

        @Override
        public void onLoad() {
            super.onLoad();
            lazyItemHandler = Lazy.of(() -> itemStackHandler);
        }
        @Override
        protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
            tag.put(INVENTORY, itemStackHandler.serializeNBT(registries));
            super.saveAdditional(tag, registries);
        }
        @Override
        protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
            itemStackHandler.deserializeNBT(registries, tag.getCompound(INVENTORY));
            super.loadAdditional(tag, registries);
        }
        @Override
        public Packet<ClientGamePacketListener> getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }
        @Override
        public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, HolderLookup.@NotNull Provider lookupProvider) {
            handleUpdateTag(pkt.getTag(), lookupProvider);
        }
        @Override
        public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
            return saveWithoutMetadata(registries);
        }
        @Override
        public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
            super.handleUpdateTag(tag, lookupProvider);
        }
        @Override
        public Vec3 getAnchorPoint() {
            return ANCHOR;
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return cache;
        }
    }
    public static class BItem extends BlockItem implements GeoItem {
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
        public BItem(HangingPotBlock block, Properties properties) {
            super(block, properties);
        }

        @Override
        public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
            consumer.accept(new BaseGeoItemRendererProvider<BItem>(new CacheItemRefBlockModel<>(), false));
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return CACHE;
        }
    }
    public static final class AddedRenderer<T extends HangingPotBlock.BEntity> implements IRenderFunctionHook<T> {
        @Override
        public void processBefore(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {}
        @Override
        public void processAfter(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
            if (animatable.getFeatureStack().isEmpty()) return;
            BlockState state = ((BlockItem)animatable.getFeatureStack().getItem()).getBlock().defaultBlockState();
            if (state.getBlock() instanceof TorchBlock torchBlock) {
                if (torchBlock.equals(Blocks.TORCH)) state = Blocks.FIRE.defaultBlockState();
                else if (torchBlock.equals(Blocks.SOUL_TORCH)) state = Blocks.SOUL_FIRE.defaultBlockState();
            }
            if (state != Blocks.AIR.defaultBlockState()) {
                poseStack.pushPose();
                poseStack.translate(2.0/16,7.001/16, 2.0/16);
                poseStack.pushPose();
                poseStack.scale((float) 12.0 /16, (float) 5.699/16, (float) 12.0 /16);
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                    Blocks.DIRT.defaultBlockState(),
                    poseStack,
                    bufferSource,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    ModelData.EMPTY,
                    RenderType.CUTOUT
                );
                poseStack.popPose();
                poseStack.translate(1.5/16, 5.699/16, 1.5/16);
                poseStack.scale((float) 9.0/16,(float) 9.0 /16,(float) 9.0 /16);
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                    state,
                    poseStack,
                    bufferSource,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    ModelData.EMPTY,
                    RenderType.CUTOUT
                );
                poseStack.popPose();
            }
        }
    }
}
