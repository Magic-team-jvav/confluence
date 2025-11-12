package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BaseTallPlantBlock extends TallGrassBlock {
    public static final MapCodec<BaseTallPlantBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(propertiesCodec(),
            BuiltInRegistries.BLOCK.byNameCodec().listOf().fieldOf("ground").forGetter(block -> block.survive)
    ).apply(instance, BaseTallPlantBlock::new));

    protected final List<Block> survive;
    private transient Set<Block> cache;

    public BaseTallPlantBlock(Block... survive) {
        this(Properties.ofFullCopy(Blocks.TALL_GRASS).replaceable(), Arrays.stream(survive).toList());
    }

    public BaseTallPlantBlock(Properties prop, List<Block> survive) {
        super(prop);
        this.survive = survive;
    }

    @Override
    public boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        if (cache == null) this.cache = new HashSet<>(survive);
        return cache.contains(state.getBlock());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState groundState = level.getBlockState(blockpos);
        return mayPlaceOn(groundState, level, blockpos) && level.isEmptyBlock(pos.above());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (canSurvive(defaultBlockState(), level, pos)) {
            return defaultBlockState();
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    @NotNull
    public BlockState updateShape(BlockState originState, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        BlockState after = super.updateShape(originState, facing, facingState, level, currentPos, facingPos);
        if (facing == Direction.UP && !facingState.isAir()) {
            return Blocks.AIR.defaultBlockState();
        }
        if (facing != Direction.DOWN) return after;
        ISpreadable.Type type = facingState.getBlock() instanceof ISpreadable spreadable ? spreadable.getSpreadType() : ISpreadable.Type.PURE;
        BlockState transformResult = type.getNotNull(originState);
        return transformResult.canSurvive(level, currentPos) ? transformResult : Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            if (aboveState.getBlock() == this) {
                level.setBlock(abovePos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, abovePos, Block.getId(aboveState));
            }
        }
        super.playerWillDestroy(level, pos, state, player);
        return state;
    }
}
