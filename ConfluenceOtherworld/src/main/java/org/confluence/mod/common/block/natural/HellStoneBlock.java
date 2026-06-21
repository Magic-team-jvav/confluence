package org.confluence.mod.common.block.natural;

import PortLib.extensions.net.minecraft.world.entity.Entity.PortEntityExtension;
import PortLib.extensions.net.minecraft.world.entity.player.Player.PortPlayerExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nullable;

public class HellStoneBlock extends Block {
    private final boolean lava;

    public HellStoneBlock(boolean lava) {
        this(lava, BlockBehaviour.Properties
                .copy(Blocks.ANCIENT_DEBRIS)
                .mapColor(MapColor.COLOR_RED)
                .lightLevel(value -> 10)
                .strength(12.0F, 1200.0F)
                .requiresCorrectToolForDrops());
    }

    public HellStoneBlock(boolean lava, BlockBehaviour.Properties properties) {
        super(properties);
        this.lava = lava;
    }

    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (entity instanceof LivingEntity) {
            entity.hurt(level.damageSources().hotFloor(), 2.5F);
            PortEntityExtension.igniteForTicks(entity, 60);
        }
        super.stepOn(level, blockPos, blockState, entity);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (lava && !level.isClientSide && !PortPlayerExtension.hasInfiniteMaterials(player)) {
            level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
        }
    }

    public static class BStair extends StairBlock {
        public BStair(BlockState state, Properties properties) {
            super(state, properties);
        }

        @Override
        public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
            if (entity instanceof LivingEntity) {
                entity.hurt(level.damageSources().hotFloor(), 2.5F);
                PortEntityExtension.igniteForTicks(entity, 60);
            }
            super.stepOn(level, pos, state, entity);
        }
    }

    public static class BSlab extends SlabBlock {
        public BSlab(Properties properties) {
            super(properties);
        }

        @Override
        public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
            if (entity instanceof LivingEntity) {
                entity.hurt(level.damageSources().hotFloor(), 2.5F);
                PortEntityExtension.igniteForTicks(entity, 60);
            }
            super.stepOn(level, pos, state, entity);
        }
    }

    public static class BWall extends WallBlock {
        public BWall(Properties properties) {
            super(properties);
        }

        @Override
        public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
            if (entity instanceof LivingEntity) {
                entity.hurt(level.damageSources().hotFloor(), 2.5F);
                PortEntityExtension.igniteForTicks(entity, 60);
            }
            super.stepOn(level, pos, state, entity);
        }
    }
}
