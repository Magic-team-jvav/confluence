package org.confluence.mod.common.block.functional;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.HorizontalDirectionalWithVerticalFourPartBlock;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BewitchingTableBlock extends HorizontalDirectionalWithVerticalFourPartBlock implements EntityBlock {
    public static final MapCodec<BewitchingTableBlock> CODEC = simpleCodec(BewitchingTableBlock::new);
    private static final VoxelShape BASE_SHAPE_SOUTH = Shapes.or(box(5, 9, 2, 16, 13, 3), box(5, 8, 1.5, 16, 9, 3.5), box(5, 8, 12.5, 16, 9, 14.5), box(5, 9, 13, 16, 13, 14), box(2, 0, 1, 5, 13, 4), box(1, 0, 1, 2, 3, 4), box(2, 0, 12, 5, 13, 15), box(1, 0, 12, 2, 3, 15), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_WEST = Shapes.or(box(13, 9, 5, 14, 13, 16), box(12.5, 8, 5, 14.5, 9, 16), box(1.5, 8, 5, 3.5, 9, 16), box(2, 9, 5, 3, 13, 16), box(12, 0, 2, 15, 13, 5), box(12, 0, 1, 15, 3, 2), box(1, 0, 2, 4, 13, 5), box(1, 0, 1, 4, 3, 2), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_NORTH = Shapes.or(box(0, 9, 2, 11, 13, 3), box(0, 8, 1.5, 11, 9, 3.5), box(0, 8, 12.5, 11, 9, 14.5), box(0, 9, 13, 11, 13, 14), box(11, 0, 1, 14, 13, 4), box(14, 0, 1, 15, 3, 4), box(11, 0, 12, 14, 13, 15), box(14, 0, 12, 15, 3, 15), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_EAST = Shapes.or(box(13, 9, 0, 14, 13, 11), box(12.5, 8, 0, 14.5, 9, 11), box(1.5, 8, 0, 3.5, 9, 11), box(2, 9, 0, 3, 13, 11), box(12, 0, 11, 15, 13, 14), box(12, 0, 14, 15, 3, 15), box(1, 0, 11, 4, 13, 14), box(1, 0, 14, 4, 3, 15), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape RIGHT_SHAPE_SOUTH = Shapes.or(box(0, 9, 2, 11, 13, 3), box(0, 8, 1.5, 11, 9, 3.5), box(0, 8, 12.5, 11, 9, 14.5), box(0, 9, 13, 11, 13, 14), box(11, 0, 1, 14, 13, 4), box(14, 0, 1, 15, 3, 4), box(11, 0, 12, 14, 13, 15), box(14, 0, 12, 15, 3, 15), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape RIGHT_SHAPE_WEST = Shapes.or(box(13, 9, 0, 14, 13, 11), box(12.5, 8, 0, 14.5, 9, 11), box(1.5, 8, 0, 3.5, 9, 11), box(2, 9, 0, 3, 13, 11), box(12, 0, 11, 15, 13, 14), box(12, 0, 14, 15, 3, 15), box(1, 0, 11, 4, 13, 14), box(1, 0, 14, 4, 3, 15), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape RIGHT_SHAPE_NORTH = Shapes.or(box(5, 9, 2, 16, 13, 3), box(5, 8, 1.5, 16, 9, 3.5), box(5, 8, 12.5, 16, 9, 14.5), box(5, 9, 13, 16, 13, 14), box(2, 0, 1, 5, 13, 4), box(1, 0, 1, 2, 3, 4), box(2, 0, 12, 5, 13, 15), box(1, 0, 12, 2, 3, 15), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape RIGHT_SHAPE_EAST = Shapes.or(box(13, 9, 5, 14, 13, 16), box(12.5, 8, 5, 14.5, 9, 16), box(1.5, 8, 5, 3.5, 9, 16), box(2, 9, 5, 3, 13, 16), box(12, 0, 2, 15, 13, 5), box(12, 0, 1, 15, 3, 2), box(1, 0, 2, 4, 13, 5), box(1, 0, 1, 4, 3, 2), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape UP_SHAPE = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape RIGHT_UP_SHAPE = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{BASE_SHAPE_SOUTH, BASE_SHAPE_WEST, BASE_SHAPE_NORTH, BASE_SHAPE_EAST};
    private static final VoxelShape[] RIGHT_SHAPES = new VoxelShape[]{RIGHT_SHAPE_SOUTH, RIGHT_SHAPE_WEST, RIGHT_SHAPE_NORTH, RIGHT_SHAPE_EAST};

    public BewitchingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<BewitchingTableBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            StateProperties.VerticalFourPart part = state.getValue(PART);
            BlockPos basePos = part.isBase() ? pos : StateProperties.VerticalFourPart.getRelatives(part, state.getValue(FACING), pos).get(StateProperties.VerticalFourPart.BASE);
            if (level.getBlockEntity(basePos) instanceof Entity entity && level.getGameTime() - entity.lastClickTime > 110) {
                entity.lastClickTime = level.getGameTime();
                entity.markUpdated();
                player.addEffect(new MobEffectInstance(ModEffects.BEWITCHED, MobEffectInstance.INFINITE_DURATION));
                return InteractionResult.SUCCESS_NO_ITEM_USED;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        int index = pState.getValue(FACING).get2DDataValue();
        return switch (pState.getValue(PART)) {
            case BASE -> BASE_SHAPES[index];
            case RIGHT -> RIGHT_SHAPES[index];
            case UP -> UP_SHAPE;
            case RIGHT_UP -> RIGHT_UP_SHAPE;
        };
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public static class Entity extends BlockEntity implements GeoBlockEntity {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
        public final boolean isBase;
        private long lastClickTime;

        public Entity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.BEWITCHING_TABLE_ENTITY.get(), pos, blockState);
            this.isBase = blockState.getValue(PART).isBase();
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            this.lastClickTime = tag.getLong("LastClickTime");
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            tag.putLong("LastClickTime", lastClickTime);
        }

        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
            CompoundTag tag = new CompoundTag();
            tag.putLong("LastClickTime", lastClickTime);
            return tag;
        }

        @Override
        public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
            this.lastClickTime = tag.getLong("LastClickTime");
        }

        public void markUpdated() {
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), UPDATE_CLIENTS);
            }
        }

        @Override
        public Packet<ClientGamePacketListener> getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            RawAnimation driving = RawAnimation.begin().thenPlay("driving");
            RawAnimation idling = RawAnimation.begin().thenLoop("idling");
            controllers.add(new AnimationController<>(this, state -> {
                if (level != null && level.getGameTime() - lastClickTime < 110) {
                    if (state.isCurrentAnimation(idling)) {
                        return state.setAndContinue(driving);
                    }
                    return PlayState.CONTINUE;
                }
                return state.setAndContinue(idling);
            }));
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return cache;
        }
    }
}
