package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import org.confluence.mod.common.init.item.FoodItems;

import javax.annotation.Nullable;
import java.util.function.ToIntFunction;

public class ShimmerDroopingVines implements CaveVines {
    public static InteractionResult useShimmerBerries(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
        if (state.hasProperty(BERRIES) && state.getValue(BERRIES)) {
            // 掉落微光浆果而非原版发光浆果
            Block.popResource(level, pos, new ItemStack(FoodItems.SHIMMER_BERRIES.get(), 1));
            float f = Mth.randomBetween(level.random, 0.8F, 1.2F);
            level.playSound((Player)null, pos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, f);
            BlockState newState = state.setValue(BERRIES, false);
            level.setBlock(pos, newState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, newState));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    // 发光等级方法（复用原版逻辑）
    public static ToIntFunction<BlockState> shimmerEmission(int lightLevel) {
        return (state) -> state.getValue(BERRIES) ? lightLevel : 0;
    }
}