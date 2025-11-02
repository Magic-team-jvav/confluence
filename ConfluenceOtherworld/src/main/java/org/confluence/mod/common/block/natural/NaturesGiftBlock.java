package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.AccessoryItems;

public class NaturesGiftBlock extends BasePlantBlock {
    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 10.0D, 12.0D);
    private final BlockItemType type;

    public NaturesGiftBlock(BlockItemType type) {
        super(Blocks.GRASS_BLOCK, Blocks.MOSS_BLOCK, Blocks.CLAY, NatureBlocks.JUNGLE_GRASS_BLOCK.get());
        this.type = type;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return type == BlockItemType.JUNGLE_ROSE ? new ItemStack(NatureBlocks.JUNGLE_ROSE.get()) : new ItemStack(AccessoryItems.NATURES_GIFT.get());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Vec3 vec3 = pState.getOffset(pLevel, pPos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    private static ResourceKey<Block> createKey(String id) {
        return ResourceKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace(id));
    }

    public enum BlockItemType {
        JUNGLE_ROSE,
        NATURES_GIFT
    }
}

