package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.MaterialItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 必须在NatureBlocks.JUNGLE_GRASS_BLOCK之后注册
 */
public class JungleSporeBlock extends BasePlantBlock {
    protected static final VoxelShape SHAPE = box(4.0D, 0.0D, 4.0D, 12.0D, 10.0D, 12.0D);

    public JungleSporeBlock() {
        super(Properties.ofFullCopy(Blocks.DANDELION).lightLevel(value -> 4), List.of(NatureBlocks.JUNGLE_GRASS_BLOCK.get(), Blocks.STONE, Blocks.DEEPSLATE, Blocks.MOSS_BLOCK, Blocks.CLAY));
    }

    @Override
    @NotNull
    public VoxelShape getShape(BlockState pState, BlockGetter level, BlockPos pPos, CollisionContext pContext) {
        Vec3 vec3 = pState.getOffset(level, pPos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return new ItemStack(MaterialItems.JUNGLE_SPORE.get());
    }
}
