package org.confluence.mod.common.item.axe;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.data.LucyTheAxeDialogCategory;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.network.s2c.LucyTheAxeDialogPacketS2C;

import java.util.List;

public class LucyTheAxe extends BaseAxeItem {
    public LucyTheAxe() {
        super(Tiers.IRON, 11, 4, ModItems.unbreakable(), ModItems.attributes(0, 0.5), ModRarity.GREEN);
    }

    public static void onDestroyBlock(ServerPlayer serverPlayer, BlockState state) {
        if (state.is(BlockTags.LOGS)) {
            LucyTheAxeDialogPacketS2C.checkAndBroadcast(serverPlayer, LucyTheAxeDialogCategory.CUTTING_DOWN_A_TREE);
        } else if (state.is(NatureBlocks.STONY_LOG)) {
            LucyTheAxeDialogPacketS2C.checkAndBroadcast(serverPlayer, LucyTheAxeDialogCategory.CUTTING_DOWN_A_GEM_TREE);
        } else if (state.is(ModTags.Blocks.CACTUS)) {
            LucyTheAxeDialogPacketS2C.checkAndBroadcast(serverPlayer, LucyTheAxeDialogCategory.CUTTING_DOWN_A_CACTUS);
        } else {
            LucyTheAxeDialogPacketS2C.checkAndBroadcast(serverPlayer, LucyTheAxeDialogCategory.DESTROY_WRONG_BLOCK);
        }
    }

    public static void onDamageLiving(ServerPlayer player, LivingEntity victim) {
        if (victim.isDeadOrDying()) {
            LucyTheAxeDialogPacketS2C.checkAndBroadcast(player, LucyTheAxeDialogCategory.KILL_ENTITY);
        } else {
            LucyTheAxeDialogPacketS2C.checkAndBroadcast(player, LucyTheAxeDialogCategory.ATTACK_ENTITY);
        }
    }

    public static void onToss(ServerPlayer player, ItemStack itemStack) {
        LucyTheAxeDialogPacketS2C.checkAndBroadcast(player, itemStack, LucyTheAxeDialogCategory.THROWN_ON_THE_GROUND);
    }

    public static void onIdle(ServerPlayer player, long gameTime) {
        if (gameTime % 1200 == 0 && player.getRandom().nextInt(5) == 0) {
            NonNullList<ItemStack> items = player.getInventory().items;
            for (int i = 0; i < items.size(); i++) {
                if (Inventory.isHotbarSlot(i) && LucyTheAxeDialogPacketS2C.checkAndBroadcast(player, items.get(i), LucyTheAxeDialogCategory.IDLE)) {
                    break;
                }
            }
        }
    }

    public static void onPickup(ServerPlayer player, ItemStack itemStack) {
        LucyTheAxeDialogPacketS2C.checkAndBroadcast(player, itemStack, LucyTheAxeDialogCategory.PLACED_BACK_INTO_THE_INVENTORY);
    }

    public static void onSwap(ServerPlayer source, Slot slot, boolean invert) {
        if ((slot.container == source.getInventory()) != invert) {
            LucyTheAxeDialogPacketS2C.broadcast(source, LucyTheAxeDialogCategory.PLACED_BACK_INTO_THE_INVENTORY);
        } else {
            LucyTheAxeDialogPacketS2C.broadcast(source, LucyTheAxeDialogCategory.PLACED_IN_OTHER_CONTAINER);
        }
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (player instanceof ServerPlayer source) {
            onSwap(source, slot, false);
        }
        return false;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(NatureBlocks.STONY_LOG) || super.isCorrectToolForDrops(stack, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.lucy_the_axe.0").withStyle(ChatFormatting.GRAY));
    }
}
