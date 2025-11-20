package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;

public class StarInABottleBlock extends LanternBlock implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(
            box(4, 0.03125, 4, 12, 10.03125, 12),
            box(5, 10, 5, 11, 12, 11)
    );
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    public static class BEntity extends BlockEntity {
        public BEntity(BlockPos pos, BlockState state) {
            super(FunctionalBlocks.STAR_IN_A_BOTTLE_ENTITY.get(), pos, state);
        }
    }
    public StarInABottleBlock(Properties properties) {
        super(properties);
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return null;
        }

        if (blockEntityType != FunctionalBlocks.STAR_IN_A_BOTTLE_ENTITY.get()) {
            return null;
        }

        return (level1, pos, state1, blockEntity) -> {
            if (level1 instanceof ServerLevel serverLevel) {
                if (serverLevel.getGameTime() % 200 == 0) {
                    Vec3 center = pos.getCenter();
                    for (Player player : serverLevel.players()) {
                        if (player.distanceToSqr(center) < 32 * 32) {
                            player.addEffect(new MobEffectInstance(ModEffects.STAR_IN_A_BOTTLE, 420));
                        }
                    }
                }
            }
        };
    }

    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = getConnectedDirection(state).getOpposite();
        BlockPos checkPos = pos.relative(direction);
        return (Block.canSupportCenter(level, checkPos, direction.getOpposite()) || ((direction == Direction.UP) && level.getBlockState(checkPos).is(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ropes")))));
    }
}
