package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.FoodItems;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ShimmerDroopingVinesPlantBlock extends CaveVinesPlantBlock {
    public static final MapCodec<CaveVinesPlantBlock> CODEC = simpleCodec(CaveVinesPlantBlock::new);

    public MapCodec<CaveVinesPlantBlock> codec() {
        return CODEC;
    }

    public ShimmerDroopingVinesPlantBlock(BlockBehaviour.Properties p_153000_) {
        super(p_153000_);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(BERRIES, false));
    }
    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) NatureBlocks.SHIMMER_DROOPING_VINE.get();
    }
    protected BlockState updateHeadAfterConvertedFromBody(BlockState p_153028_, BlockState p_153029_) {
        return (BlockState)p_153029_.setValue(BERRIES, (Boolean)p_153028_.getValue(BERRIES));
    }
    public ItemStack getCloneItemStack(LevelReader p_304444_, BlockPos p_153008_, BlockState p_153009_) {
        return new ItemStack(FoodItems.SHIMMER_BERRIES.get());
    }

    protected InteractionResult useWithoutItem(BlockState p_153021_, Level p_153022_, BlockPos p_153023_, Player p_153024_, BlockHitResult p_153026_) {
        return CaveVines.use(p_153024_, p_153021_, p_153022_, p_153023_);
    }

}