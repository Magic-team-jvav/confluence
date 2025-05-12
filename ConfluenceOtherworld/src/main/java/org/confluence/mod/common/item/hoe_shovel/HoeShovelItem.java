package org.confluence.mod.common.item.hoe_shovel;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.component.ToolMode;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HoeShovelItem extends DiggerItem {
    public HoeShovelItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, Consumer<ItemAttributeModifiers.Builder> consumer, ModRarity rarity) {
        super(tier, ModTags.Blocks.MINEABLE_WITH_HOE_SHOVEL, properties
                .component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS, ModItems.createAttributes(tier, (rawDamage - tier.getAttackDamageBonus() - 1), rawSpeed - 4, consumer))
                .component(ConfluenceMagicLib.TOOL_MODE, new ToolMode(0)));
    }
    @Override
    public InteractionResult useOn(UseOnContext context) {
        ToolMode toolMode = context.getItemInHand().get(ConfluenceMagicLib.TOOL_MODE.get());
        if (toolMode == null || toolMode.mode() == 0) {
            return Items.NETHERITE_SHOVEL.useOn(context);
        }else{
            return Items.NETHERITE_HOE.useOn(context);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player instanceof ServerPlayer serverPlayer && getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE).getType() == HitResult.Type.MISS && player.isCrouching()) {
            ItemStack toolItem = player.getItemInHand(usedHand);
            ToolMode toolMode = toolItem.get(ConfluenceMagicLib.TOOL_MODE.get());
            if (toolMode != null && toolMode.mode() == 0) {
                toolItem.set(ConfluenceMagicLib.TOOL_MODE, new ToolMode(1));
            } else if (toolMode == null || toolMode.mode() == 1) {
                toolItem.set(ConfluenceMagicLib.TOOL_MODE, new ToolMode(0));
            }
            serverPlayer.sendSystemMessage(Component.translatable("message.confluence.toolmode.current").withStyle(ChatFormatting.GRAY).append(getModeName(toolItem)), true);
            level.playSound(null, player.blockPosition(), SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.PLAYERS, 0.4F, 0.6F);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        ToolMode toolMode = stack.get(ConfluenceMagicLib.TOOL_MODE.get());
        if (toolMode == null || toolMode.mode() == 0) {
            return ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(itemAbility);
        }else{
            return ItemAbilities.DEFAULT_HOE_ACTIONS.contains(itemAbility);
        }
    }

    public Component getModeName(ItemStack stack) {
        ToolMode toolMode = stack.get(ConfluenceMagicLib.TOOL_MODE.get());
        if (toolMode != null && toolMode.mode() == 1) {
            return Component.translatable("message.confluence.hoe_shovel.mode.1").withStyle(ChatFormatting.WHITE);
        }
        return Component.translatable("message.confluence.hoe_shovel.mode.0").withStyle(ChatFormatting.WHITE);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("message.confluence.toolmode.tip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("message.confluence.toolmode.current").withStyle(ChatFormatting.GRAY).append(getModeName(stack)));
    }
}
