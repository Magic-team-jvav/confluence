package org.confluence.mod.common.block.functional;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.PlayerContainer;
import org.confluence.lib.common.block.HorizontalDirectionalWaterloggedBlock;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.attachment.PlayerSafeContainer;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SafeBlock extends HorizontalDirectionalWaterloggedBlock implements EntityBlock {
    public static final MapCodec<SafeBlock> CODEC = simpleCodec(SafeBlock::new);
    private static final VoxelShape SHAPE = box(1, 0, 1, 15, 15, 15);

    public SafeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(BlockStateProperties.OPEN));
    }

    @Override
    protected MapCodec<SafeBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof BEntity entity) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            level.setBlock(pos, state.setValue(BlockStateProperties.OPEN, true), Block.UPDATE_ALL);
            PlayerSafeContainer container = player.getData(ModAttachmentTypes.SAFE);
            container.setActiveContainer(entity);
            player.openMenu(new SimpleMenuProvider((id, inventory, player1) -> new ChestMenu(MenuType.GENERIC_9x6, id, inventory, container, 6), Component.translatable("container.confluence.safe")));
            PiglinAi.angerNearbyPiglins(player, true);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : LibUtils.getTicker(blockEntityType, FunctionalBlocks.SAFE_ENTITY.get(), BEntity::serverTick);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    public static class BEntity extends BlockEntity implements PlayerContainer.ValidEntity, GeoBlockEntity {
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
        public final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
            @Override
            protected void onOpen(Level level, BlockPos pos, BlockState state) {
                BEntity.this.triggerAnim("status", "open");
            }

            @Override
            protected void onClose(Level level, BlockPos pos, BlockState state) {
                BEntity.this.triggerAnim("status", "close");
            }

            @Override
            protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {}

            @Override
            protected boolean isOwnContainer(Player player) {
                if (player.containerMenu instanceof ChestMenu chestMenu) {
                    return chestMenu.getContainer() == player.getData(ModAttachmentTypes.SAFE);
                }
                return false;
            }
        };

        public BEntity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.SAFE_ENTITY.get(), pos, blockState);
            SingletonGeoAnimatable.registerSyncedAnimatable(this);
        }

        @Override
        public BlockEntity self() {
            return this;
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            controllers.add(new AnimationController<>(this, "status", 1, state -> PlayState.STOP)
                    .triggerableAnim("open", RawAnimation.begin().thenPlayAndHold("open"))
                    .triggerableAnim("close", RawAnimation.begin().thenPlayAndHold("close")));
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return CACHE;
        }

        public static void serverTick(Level level, BlockPos pos, BlockState state, BEntity entity) {
            if (!entity.remove) {
                entity.openersCounter.recheckOpeners(level, pos, state);
            }
        }
    }
}
