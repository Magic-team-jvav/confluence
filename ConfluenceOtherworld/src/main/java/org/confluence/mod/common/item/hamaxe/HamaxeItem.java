package org.confluence.mod.common.item.hamaxe;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.component.ToolMode;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.hammer.HammerItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class HamaxeItem extends DiggerItem {
    public HamaxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        super(tier, ModTags.Blocks.MINEABLE_WITH_HAMAXE, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(tier, (rawDamage - tier.getAttackDamageBonus() - 1), rawSpeed - 4))
                .component(ConfluenceMagicLib.TOOL_MODE, new ToolMode(0)));
    }

    public HamaxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, Consumer<ItemAttributeModifiers.Builder> consumer, ModRarity rarity) {
        super(tier, ModTags.Blocks.MINEABLE_WITH_HAMAXE, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS, ModItems.createAttributes(tier, (rawDamage - tier.getAttackDamageBonus() - 1), rawSpeed - 4, consumer))
                .component(ConfluenceMagicLib.TOOL_MODE, new ToolMode(0)));
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        ToolMode toolMode = stack.get(ConfluenceMagicLib.TOOL_MODE.get());
        if (toolMode == null || toolMode.mode() == 0){
            HammerItem.hammerMineBlock(stack, level, state, pos, miningEntity);
        }
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return Items.NETHERITE_AXE.useOn(context);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand){
        if (getPlayerPOVHitResult(level,player, ClipContext.Fluid.NONE).getType() == HitResult.Type.MISS && player.isShiftKeyDown()){
            ItemStack toolItem = player.getItemInHand(usedHand);
            ToolMode toolMode = toolItem.get(ConfluenceMagicLib.TOOL_MODE.get());
            if (toolMode != null && toolMode.mode() == 0){
                toolItem.set(ConfluenceMagicLib.TOOL_MODE.get(), new ToolMode(1));
            }else if (toolMode == null || toolMode.mode() == 1){
                toolItem.set(ConfluenceMagicLib.TOOL_MODE.get(), new ToolMode(0));
            }
            if (player instanceof ServerPlayer) {
                ((ServerPlayer)player).sendSystemMessage(Component.translatable("tooltip.item.confluence.toolmode.mode", String.format("%1$s", getModeName(toolItem))).withStyle(ChatFormatting.WHITE), true);
            }
            level.playSound(player, BlockPos.containing(player.position()), SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.PLAYERS, 0.4F, 0.6F);
        }
        return super.use(level,player,usedHand);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(itemAbility);
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return true;
    }

    public String getModeName(ItemStack stack){
        ToolMode toolMode = stack.get(ConfluenceMagicLib.TOOL_MODE.get());
        if (toolMode != null && toolMode.mode() == 1){
            return Component.translatable("tooltip.item.confluence.hamaxe.mode.1").getString();
        }
        return Component.translatable("tooltip.item.confluence.hamaxe.mode.0").getString();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag){
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        List<Component> list = Lists.newArrayList();
        list.add(Component.translatable("tooltip.item.confluence.toolmode").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("tooltip.item.confluence.toolmode.mode", String.format("%1$s", getModeName(pStack))).withStyle(ChatFormatting.GRAY));
        pTooltipComponents.addAll(list);
    }
}
