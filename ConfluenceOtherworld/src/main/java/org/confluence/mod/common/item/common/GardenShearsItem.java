package org.confluence.mod.common.item.common;

import com.google.common.collect.Sets;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.item.ModItems;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GardenShearsItem extends ShearsItem {
    public GardenShearsItem(Properties properties, ModRarity rarity) {
        super(properties
                .component(DataComponents.MAX_STACK_SIZE, 1)
                .component(DataComponents.TOOL, ShearsItem.createToolProperties())
                .component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .attributes(ItemAttributeModifiers.builder().add(
                        Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(
                                ModItems.BASE_BLOCK_INTERACTION_RANGE_ID, 2.5, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND).build()));
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return Stream.of(net.minecraftforge.common.ItemAbilities.SHEARS_DIG,
                        net.minecraftforge.common.ItemAbilities.SHEARS_HARVEST,
                        net.minecraftforge.common.ItemAbilities.SHEARS_REMOVE_ARMOR,
                        net.minecraftforge.common.ItemAbilities.SHEARS_CARVE,
                        net.minecraftforge.common.ItemAbilities.SHEARS_DISARM,
                        net.minecraftforge.common.ItemAbilities.SHEARS_TRIM,
                        net.minecraftforge.common.ItemAbilities.AXE_STRIP)
                .collect(Collectors.toCollection(Sets::newIdentityHashSet))
                .contains(itemAbility);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Player player = context.getPlayer();
        Optional<BlockState> optional = this.evaluateNewBlockState(level, blockpos, player, level.getBlockState(blockpos), context);
        if (optional.isEmpty()) {
            return super.useOn(context);
        } else {
            ItemStack itemstack = context.getItemInHand();
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, blockpos, itemstack);
            }

            level.setBlock(blockpos, optional.get(), 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, optional.get()));

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    private Optional<BlockState> evaluateNewBlockState(Level level, BlockPos pos, @Nullable Player player, BlockState state, UseOnContext useOnContext) {
        Optional<BlockState> optional = Optional.ofNullable(state.getToolModifiedState(useOnContext, net.minecraftforge.common.ItemAbilities.AXE_STRIP, false));
        if (optional.isPresent()) {
            level.playSound(player, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 0.5F);
            return optional;
        } else {
            return Optional.empty();
        }
    }

}
