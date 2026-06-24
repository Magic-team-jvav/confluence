package org.confluence.mod.common.item.common;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import com.google.common.collect.Sets;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.item.ModItems;
import org.mesdag.portlib.wrapper.common.PortItemAbilities;
import org.mesdag.portlib.wrapper.common.PortItemAbility;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GardenShearsItem extends ShearsItem {
    private static final Set<ToolAction> ACTIONS = Stream.of(
            PortItemAbilities.SHEARS_DIG,
            PortItemAbilities.SHEARS_HARVEST,
            PortItemAbilities.SHEARS_REMOVE_ARMOR,
            PortItemAbilities.SHEARS_CARVE,
            PortItemAbilities.SHEARS_DISARM,
            PortItemAbilities.SHEARS_TRIM,
            PortItemAbilities.AXE_STRIP
    ).map(PortItemAbility::unwrap).collect(Collectors.toCollection(Sets::newIdentityHashSet));

    public GardenShearsItem(Properties properties, ModRarity rarity) {
        super(properties
                .stacksTo(1)
//                .component(DataComponents.TOOL, ShearsItem.createToolProperties())
                .component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .attributes(PortItemAttributeModifiers.builder().add(
                        PortAttributesExtension.blockInteractionRange(), new PortAttributeModifier(
                                ModItems.BASE_BLOCK_INTERACTION_RANGE_ID, 2.5, PortAttributeModifier.PortOperation.ADD_VALUE),
                        PortEquipmentSlotGroup.MAINHAND).build()));
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction itemAbility) {
        return ACTIONS.contains(itemAbility);
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
        Optional<BlockState> optional = Optional.ofNullable(state.getToolModifiedState(useOnContext, ToolActions.AXE_STRIP, false));
        if (optional.isPresent()) {
            level.playSound(player, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 0.5F);
            return optional;
        } else {
            return Optional.empty();
        }
    }

}
