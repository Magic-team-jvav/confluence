package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BasePlantBlock extends BushBlock {
    public static final MapCodec<BasePlantBlock> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(propertiesCodec(),
            BuiltInRegistries.BLOCK.byNameCodec().listOf().fieldOf("ground").forGetter(basePlantBlock ->
                Arrays.asList(basePlantBlock.survive)))
            .apply(builder, (prop, ground) -> new BasePlantBlock(prop, ground.toArray(new Block[0])))
    );

    private final Block[] survive;

    public BasePlantBlock(Block... survive){
        super(Properties.ofFullCopy(Blocks.DANDELION).replaceable());
        this.survive = survive;
    }

    public BasePlantBlock(Properties prop, Block... survive){
        super(prop);
        this.survive = survive;
    }

    @Override
    @NotNull
    protected MapCodec<? extends BushBlock> codec(){
        return CODEC;
    }

    @Override
    @NotNull
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context){
        Vec3 offset = state.getOffset(world, pos);
        return box(2, 0, 2, 14, 13, 14).move(offset.x, offset.y, offset.z);
    }

    @Override
    public boolean mayPlaceOn(BlockState groundState, BlockGetter worldIn, BlockPos pos){
        return Arrays.stream(survive).anyMatch(state -> state == groundState.getBlock());
    }

    @Override
    public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos){
        BlockPos blockpos = pos.below();
        BlockState groundState = worldIn.getBlockState(blockpos);
        return mayPlaceOn(groundState, worldIn, blockpos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!canSurvive(this.defaultBlockState(), level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return this.defaultBlockState();
    }


    /**
     * 邪恶草和蘑菇下方的方块被转化时转化自身
     */
    @Override
    @NotNull
    public BlockState updateShape(BlockState originState, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos){
        BlockState after = super.updateShape(originState, facing, facingState, level, currentPos, facingPos);
        if(facing != Direction.DOWN) return after;
        ISpreadable.Type type = facingState.getBlock() instanceof ISpreadable sp ? sp.getSpreadType() : ISpreadable.Type.PURE;
        BlockState transformResult = type.getNotNull(originState);  // 默认不转化，如果结果是摧毁则是写到表里面
        return transformResult.canSurvive(level, currentPos) ? transformResult : Blocks.AIR.defaultBlockState();
    }
}
