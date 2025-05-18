package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.jetbrains.annotations.NotNull;

public class MushroomBlock extends BasePlantBlock implements ISpreadable {
    private static final VoxelShape SHAPE=Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
    public final Type type;
    public MushroomBlock(Type type, Block... surviveBlock){
        super(surviveBlock);
        this.type = type;
    }

    @Override
    @NotNull
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext){
        Vec3 vec3 = pState.getOffset(pLevel, pPos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public Type getType() {
        return type;
    }
}
