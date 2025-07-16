package org.confluence.mod.common.item.common;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.item.ModItems;

import java.util.function.Predicate;

public class BugNetItem extends CustomRarityItem {
    private final double maxSize;
    private final Predicate<LivingEntity> predicate;

    public BugNetItem(ModRarity rarity, double maxSize, Predicate<LivingEntity> predicate) {
        super(new Properties().stacksTo(1), rarity);
        this.maxSize = maxSize;
        this.predicate = predicate;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (usedHand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (player.isLocalPlayer()) {
            ((LocalPlayer) player).connection.send(ServerboundInteractPacket.createInteractionPacket(interactionTarget, false, InteractionHand.MAIN_HAND));
        } else if (predicate.test(interactionTarget) && interactionTarget.getBoundingBox().getSize() <= maxSize) {
            ItemStack itemStack = ModItems.ENTITY_DISPLAY.get().getDefaultInstance();
            interactionTarget.setYRot(0.0F);
            interactionTarget.setYHeadRot(0.0F);
            interactionTarget.setYBodyRot(0.0F);
            interactionTarget.setXRot(0.0F);
            LibUtils.updateItemStackNbt(itemStack, interactionTarget::save);
            if (interactionTarget.hasCustomName()) {
                itemStack.set(DataComponents.CUSTOM_NAME, interactionTarget.getCustomName());
            }
            itemStack.remove(ConfluenceMagicLib.MOD_RARITY);
            if (!player.addItem(itemStack)) {
                player.drop(itemStack, false);
            }
            interactionTarget.discard();
            return InteractionResult.SUCCESS_NO_ITEM_USED;
        }
        return InteractionResult.SUCCESS;
    }
}
