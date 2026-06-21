package org.confluence.mod.common.item.common;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.component.ToolMode;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.item.PortItem;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.List;
import java.util.function.Consumer;

public class HoeShovelItem extends DiggerItem {
    public HoeShovelItem(Tier tier, float rawDamage, float rawSpeed, PortItem.PortProperties properties, Consumer<PortItemAttributeModifiers.PortBuilder> consumer, ModRarity rarity) {
        super(ModItems.getAttackDamage(tier, rawDamage), ModItems.getAttackSpeed(rawSpeed), tier, ModTags.Blocks.MINEABLE_WITH_HOE_SHOVEL, properties
                .component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .component(ConfluenceMagicLib.TOOL_MODE, new ToolMode(0)));
        this.defaultModifiers = ModItems.mergeModifiers(defaultModifiers, consumer);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ToolMode toolMode = context.getItemInHand().getData(ConfluenceMagicLib.TOOL_MODE);
        if (toolMode == null || toolMode.mode() == 0) {
            return Items.NETHERITE_SHOVEL.useOn(context);
        } else {
            return Items.NETHERITE_HOE.useOn(context);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player instanceof ServerPlayer serverPlayer && getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE).getType() == HitResult.Type.MISS && player.isCrouching()) {
            ItemStack toolItem = player.getItemInHand(usedHand);
            ToolMode toolMode = PortItemStackExtension.getData(toolItem, ConfluenceMagicLib.TOOL_MODE);
            if (toolMode != null && toolMode.mode() == 0) {
                PortItemStackExtension.setData(toolItem, ConfluenceMagicLib.TOOL_MODE, new ToolMode(1));
            } else if (toolMode == null || toolMode.mode() == 1) {
                PortItemStackExtension.setData(toolItem, ConfluenceMagicLib.TOOL_MODE, new ToolMode(0));
            }
            serverPlayer.sendSystemMessage(Component.translatable("message.confluence.toolmode.current").withStyle(ChatFormatting.GRAY).append(getModeName(toolItem)), true);
            level.playSound(null, player.blockPosition(), SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.PLAYERS, 0.4F, 0.6F);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction itemAbility) {
        ToolMode toolMode = stack.getData(ConfluenceMagicLib.TOOL_MODE.get());
        if (toolMode == null || toolMode.mode() == 0) {
            return ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(itemAbility);
        }
        return ToolActions.DEFAULT_HOE_ACTIONS.contains(itemAbility);
    }

    public Component getModeName(ItemStack stack) {
        ToolMode toolMode = stack.get(ConfluenceMagicLib.TOOL_MODE.get());
        if (toolMode != null && toolMode.mode() == 1) {
            return Component.translatable("message.confluence.hoe_shovel.mode.1").withStyle(ChatFormatting.WHITE);
        }
        return Component.translatable("message.confluence.hoe_shovel.mode.0").withStyle(ChatFormatting.WHITE);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("message.confluence.toolmode.tip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("message.confluence.toolmode.current").withStyle(ChatFormatting.GRAY).append(getModeName(stack)));
    }
}
