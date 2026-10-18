package org.confluence.mod.common.event.game.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.Nullable;

/// 植物纤维绳索宝典：佩戴者空手右键食用草方块、植物与花。
///
/// - 草方块 -> 土块，丛林草方块 -> 泥块
/// - 草、蕨与花可整株吃掉；两格高的植物（高草、大蕨、高花）计为食用两次
/// - 每次食用回复半格饱食度；花额外给予其对应的迷之炖菜效果
public final class PlantFiberCordageEvents {
    /// 每次食用回复的饱食度：半格（1 点）
    private static final int FOOD_PER_EAT = 1;

    /// @return 是否已消耗这次右键
    public static boolean handle(Player player, Level level, InteractionHand hand, ItemStack stack, BlockPos pos) {
        if (level.isClientSide || hand != InteractionHand.MAIN_HAND || !stack.isEmpty()) return false;
        if (player.isSpectator() || player.isShiftKeyDown()) return false;
        if (!TCUtils.hasType(player, AccessoryItems.VINE$ROPE)) return false;
        BlockState state = level.getBlockState(pos);
        Block converted = convertedBlock(state);
        if (converted != null) {
            level.setBlockAndUpdate(pos, converted.defaultBlockState());
            level.levelEvent(2001, pos, Block.getId(state));
            eat(player, 1);
            player.swing(hand, true);
            return true;
        }
        int eats = edibleEats(state);
        if (eats <= 0) return false;
        removePlant(level, pos, state);
        eat(player, eats);
        applyStewEffect(player, state.getBlock(), eats);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, state.getBlock().asItem().getDefaultInstance()),
                    pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, 8, 0.2, 0.2, 0.2, 0.02);
        }
        level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.6F, 1.0F);
        player.swing(hand, true);
        return true;
    }

    /// 可整块转换的方块：草方块变土块，丛林草方块变泥块。
    private static @Nullable Block convertedBlock(BlockState state) {
        if (state.is(Blocks.GRASS_BLOCK)) return Blocks.DIRT;
        if (state.is(NatureBlocks.JUNGLE_GRASS_BLOCK.get())) return Blocks.MUD;
        return null;
    }

    /// 食用次数：两格高的植物一次吃掉两格，因此计为两次。
    private static int edibleEats(BlockState state) {
        Block block = state.getBlock();
        if (isTallPlant(block)) return 2;
        if (isSinglePlant(block)) return 1;
        return 0;
    }

    private static boolean isSinglePlant(Block block) {
        return block == Blocks.SHORT_GRASS || block == Blocks.FERN || block instanceof SuspiciousEffectHolder;
    }

    private static boolean isTallPlant(Block block) {
        return block == Blocks.TALL_GRASS || block == Blocks.LARGE_FERN || block instanceof TallFlowerBlock;
    }

    private static void removePlant(Level level, BlockPos pos, BlockState state) {
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            boolean lower = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
            level.removeBlock(lower ? pos.above() : pos.below(), false);
        }
        level.removeBlock(pos, false);
    }

    private static void eat(Player player, int times) {
        for (int i = 0; i < times; i++) {
            player.getFoodData().eat(FOOD_PER_EAT, 0.0F);
        }
    }

    /// 花提供与其对应的迷之炖菜效果，食用两次的植物效果也应用两次。
    private static void applyStewEffect(Player player, Block block, int times) {
        if (!(block instanceof SuspiciousEffectHolder holder)) return;
        SuspiciousStewEffects effects = holder.getSuspiciousEffects();
        for (int i = 0; i < times; i++) {
            for (SuspiciousStewEffects.Entry entry : effects.effects()) {
                player.addEffect(entry.createEffectInstance());
            }
        }
    }
}
