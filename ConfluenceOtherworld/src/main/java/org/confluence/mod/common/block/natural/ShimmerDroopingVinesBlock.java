package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ShimmerDroopingVinesBlock extends CaveVinesBlock {
    public static final MapCodec<CaveVinesBlock> CODEC = simpleCodec(CaveVinesBlock::new);
    private static final float CHANCE_OF_BERRIES_ON_GROWTH = 0.11F;

    public MapCodec<CaveVinesBlock> codec() {
        return CODEC;
    }
    protected Block getBodyBlock() {
        return NatureBlocks.SHIMMER_DROOPING_VINE_PLANT.get();
    }
    public ShimmerDroopingVinesBlock(BlockBehaviour.Properties p_152959_) {
        super(p_152959_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0)).setValue(BERRIES, false));
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState p_152980_, Level p_152981_, BlockPos p_152982_, Player p_152983_, BlockHitResult p_152985_) {
        return CaveVines.use(p_152983_, p_152980_, p_152981_, p_152982_);
    }
}