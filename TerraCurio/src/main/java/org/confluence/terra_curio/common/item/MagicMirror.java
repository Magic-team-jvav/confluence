package org.confluence.terra_curio.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCSoundEvents;

import java.util.List;

public class MagicMirror extends Item {
    public MagicMirror(ModRarity rarity) {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, rarity).fireResistant().stacksTo(1));
    }

    public MagicMirror(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.SPYGLASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 30;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity living) {
        if (level.isClientSide) {
            Minecraft.getInstance().gameRenderer.displayItemActivation(itemStack);
        } else if (living instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.getVehicle() != null) {
                serverPlayer.removeVehicle();
            }
            serverPlayer.getCooldowns().addCooldown(this, 10);
            serverPlayer.changeDimension(serverPlayer.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.DO_NOTHING));
        }
        living.playSound(TCSoundEvents.TRANSMISSION.get());
        return itemStack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.item.terra_curio.magic_mirror.0").withStyle(ChatFormatting.GRAY));
    }
}
