package org.confluence.mod.common.item.common;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.component.ToolMode;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.item.PortItem;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BaseHamaxeItem extends DiggerItem {
    private @Nullable TooltipComponent component;
    private boolean hasImage;

    public BaseHamaxeItem(Tier tier, float rawDamage, float rawSpeed, PortItem.PortProperties properties, ModRarity rarity) {
        super(ModItems.getAttackDamage(tier, rawDamage), ModItems.getAttackSpeed(rawSpeed), tier, ModTags.Blocks.MINEABLE_WITH_HAMAXE, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .component(ConfluenceMagicLib.TOOL_MODE, new ToolMode(0)));
    }

    public BaseHamaxeItem(Tier tier, float rawDamage, float rawSpeed, PortItem.PortProperties properties, Consumer<PortItemAttributeModifiers.PortBuilder> consumer, ModRarity rarity) {
        super(ModItems.getAttackDamage(tier, rawDamage), ModItems.getAttackSpeed(rawSpeed), tier, ModTags.Blocks.MINEABLE_WITH_HAMAXE, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .component(ConfluenceMagicLib.TOOL_MODE, new ToolMode(0)));
        this.defaultModifiers = ModItems.mergeModifiers(defaultModifiers, consumer);
    }

    public BaseHamaxeItem hasImage() {
        this.hasImage = true;
        return this;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (component == null && hasImage) {
            this.component = AltImageComponent.of(stack.getItem());
        }
        return Optional.ofNullable(component);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        ToolMode toolMode = PortItemStackExtension.getData(stack, ConfluenceMagicLib.TOOL_MODE);
        if (toolMode == null || toolMode.mode() == 0) {
            BaseHammerItem.hammerMineBlock(stack, level, state, pos, miningEntity);
        }
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return Items.NETHERITE_AXE.useOn(context);
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
        return ToolActions.DEFAULT_AXE_ACTIONS.contains(itemAbility);
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return true;
    }

    public Component getModeName(ItemStack stack) {
        ToolMode toolMode = PortItemStackExtension.getData(stack, ConfluenceMagicLib.TOOL_MODE.get());
        if (toolMode != null && toolMode.mode() == 1) {
            return Component.translatable("message.confluence.hamaxe.mode.1").withStyle(ChatFormatting.WHITE);
        }
        return Component.translatable("message.confluence.hamaxe.mode.0").withStyle(ChatFormatting.WHITE);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("message.confluence.toolmode.tip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("message.confluence.toolmode.current").withStyle(ChatFormatting.GRAY).append(getModeName(stack)));
    }
}
