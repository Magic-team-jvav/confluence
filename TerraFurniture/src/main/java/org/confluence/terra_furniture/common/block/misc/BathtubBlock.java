package org.confluence.terra_furniture.common.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.confluence.lib.common.block.HorizontalDirectionalWithForwardTwoPartBlock;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.terra_furniture.client.generators.SingleMulStateBDG;
import org.confluence.terra_furniture.common.block.func.BlockSetGetter;
import org.confluence.terra_furniture.common.block.func.MulStateGetter;
import org.confluence.terra_furniture.common.block.func.TFBlockSetType;
import org.confluence.terra_furniture.common.datagen.empowered.AutoGenBlockData;
import org.confluence.terra_furniture.common.datagen.empowered.BlockDataGenerator;
import org.confluence.terra_furniture.common.init.TFTags;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public class BathtubBlock extends HorizontalDirectionalWithForwardTwoPartBlock implements AutoGenBlockData<BathtubBlock>, BlockSetGetter<BathtubBlock>, MulStateGetter<StateProperties.ForwardTwoPart> {
    private static final VoxelShape SOUTH_SHAPE = Shapes.join(box(0, 0, 0, 16, 12, 16), box(1, 1, 0, 15, 12, 15), BooleanOp.ONLY_FIRST);
    private static final VoxelShape WEST_SHAPE = Shapes.join(box(0, 0, 0, 16, 12, 16), box(1, 1, 1, 16, 12, 15), BooleanOp.ONLY_FIRST);
    private static final VoxelShape NORTH_SHAPE = Shapes.join(box(0, 0, 0, 16, 12, 16), box(1, 1, 1, 15, 12, 16), BooleanOp.ONLY_FIRST);
    private static final VoxelShape EAST_SHAPE = Shapes.join(box(0, 0, 0, 16, 12, 16), box(0, 1, 1, 15, 12, 15), BooleanOp.ONLY_FIRST);
    private static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{SOUTH_SHAPE, WEST_SHAPE, NORTH_SHAPE, EAST_SHAPE};
    private static final VoxelShape[] FORWARD_SHAPES = new VoxelShape[]{NORTH_SHAPE, EAST_SHAPE, SOUTH_SHAPE, WEST_SHAPE};

    private final TFBlockSetType type;
    public BathtubBlock(TFBlockSetType type, Properties properties) {
        super(properties);
        this.type = type;
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.OCCUPIED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(BlockStateProperties.OCCUPIED));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = state.getValue(FACING).get2DDataValue();
        return switch (state.getValue(PART)) {
            case BASE -> BASE_SHAPES[index];
            case FORWARD -> FORWARD_SHAPES[index];
        };
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter level, BlockPos pos, LivingEntity sleeper) {
        return true;
    }

    @Override
    public Direction getBedDirection(BlockState state, LevelReader level, BlockPos pos) {
        return state.getValue(FACING).getOpposite();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moveByPiston) {
        if (!state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, moveByPiston);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(BlockStateProperties.OCCUPIED)) {
            if (!kickVillagerOutOfBed(level, pos)) {
                player.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
            }
        } else {
            if (state.getValue(PART).isBase()) {
                pos = pos.relative(StateProperties.ForwardTwoPart.getConnectedDirection(state));
            }
            player.startSleepInBed(pos).ifLeft(problem -> {
                if (problem.getMessage() != null) {
                    player.displayClientMessage(problem.getMessage(), true);
                }
            });
        }
        return InteractionResult.SUCCESS;
    }

    private boolean kickVillagerOutOfBed(Level level, BlockPos pos) {
        List<Villager> list = level.getEntitiesOfClass(Villager.class, new AABB(pos), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        } else {
            list.getFirst().stopSleeping();
            return true;
        }
    }

    @Override
    public @Nullable BlockDataGenerator<? super BathtubBlock> getGenerator() {
        return new SingleMulStateBDG<>() {
            @Override
            public String getTemplateType(BathtubBlock block) {
                return "bathtub";
            }

            @Override
            public void addBlockTags(BathtubBlock block, BlockTagsProvider provider, HashSet<TagKey<Block>> keys) {
                super.addBlockTags(block, provider, keys);
                keys.add(TFTags.BATHTUBS);
            }
        };
    }

    @Override
    public TFBlockSetType getType() {
        return type;
    }

    @Override
    public boolean hasParticle(BathtubBlock block) {
        return true;
    }

    @Override
    public EnumProperty<StateProperties.ForwardTwoPart> getContainer() {
        return PART;
    }
}
