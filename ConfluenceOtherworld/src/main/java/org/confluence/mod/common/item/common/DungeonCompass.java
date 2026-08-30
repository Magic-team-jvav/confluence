package org.confluence.mod.common.item.common;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.item.ToolItems;

import java.util.List;

public class DungeonCompass extends TooltipItem {
    public static final int[][] CRYSTALS = {
            new int[]{3, 0},
            new int[]{2, 2},
            new int[]{0, 3},
            new int[]{-2, 2},
            new int[]{-3, 0},
            new int[]{-2, -2},
            new int[]{0, -3},
            new int[]{2, -2}
    };
    private static final BlockPattern PATTERN = BlockPatternBuilder.start()
            .aisle("   A   ", " A   A ", "       ", "A     A", "       ", " A   A ", "   A   ")
            .aisle("   T   ", " T   T ", "   O   ", "T O O T", "   O   ", " T   T ", "   T   ")
            .where('A', BlockInWorld.hasState(state -> state.is(Blocks.AMETHYST_BLOCK)))
            .where('T', BlockInWorld.hasState(state -> state.is(Blocks.CHISELED_TUFF)))
            .where('O', BlockInWorld.hasState(state -> state.is(Blocks.CRYING_OBSIDIAN)))
            .build();

    public DungeonCompass() {
        super(new Properties().fireResistant().stacksTo(1), ModRarity.GREEN, Component.translatable("tooltip.item.confluence.dungeon_compass.0").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(CommonComponents.EMPTY);
        tooltipComponents.add(Component.translatable("item.modifiers.head").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public static void matches(Player player, InteractionHand hand, Level level, ItemStack itemStack, BlockState blockState, BlockPos blockPos) {
        if (itemStack.is(ToolItems.METEOR_COMPASS) && blockState.is(Blocks.CHISELED_TUFF)) {
            BlockPattern.BlockPatternMatch matches = PATTERN.matches(level, blockPos.offset(3, 1, 3), Direction.DOWN, Direction.SOUTH);
            if (matches == null) return;
            if (level.isClientSide) {
                RandomSource random = level.random;
                for (int[] crystal : CRYSTALS) {
                    for (int i = 0; i < 16; i++) {
                        level.addParticle(ParticleTypes.ENCHANT, blockPos.getX() + random.nextFloat(), blockPos.getY() + random.nextFloat() + 1.5, blockPos.getZ() + random.nextFloat(), crystal[0] - 0.5, 0, crystal[1] - 0.5);
                    }
                }
            } else {
                ServerLevel serverlevel = (ServerLevel) level;
                HolderSet<Structure> dungeon = HolderSet.direct(serverlevel.registryAccess().holderOrThrow(ModStructures.Keys.DUNGEON));
                Pair<BlockPos, Holder<Structure>> pair = serverlevel.getChunkSource().getGenerator().findNearestMapStructure(serverlevel, dungeon, blockPos, 100, false);
                if (pair == null) {
                    player.displayClientMessage(Component.translatable("message.confluence.dungeon_not_found").withStyle(ChatFormatting.RED), true);
                } else {
                    player.setItemInHand(hand, ItemStack.EMPTY);
                    ItemStack stack = ToolItems.DUNGEON_COMPASS.toStack();
                    LibUtils.updateItemStackNbt(stack, tag -> {
                        BlockPos pos = pair.getFirst();
                        tag.putIntArray("pos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                    });
                    LibUtils.createItemEntity(stack, blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5, level, 0);
                }
            }
            player.swing(hand);
        }
    }
}
