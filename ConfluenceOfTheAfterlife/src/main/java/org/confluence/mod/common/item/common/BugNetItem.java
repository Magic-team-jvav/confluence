package org.confluence.mod.common.item.common;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.util.TCUtils;

public class BugNetItem extends CustomRarityItem {
    private final double maxSize;

    public BugNetItem(ModRarity rarity, double maxSize) {
        super(new Properties().stacksTo(1), rarity);
        this.maxSize = maxSize;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (usedHand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (player.level().isClientSide) {
            ((LocalPlayer) player).connection.send(ServerboundInteractPacket.createInteractionPacket(interactionTarget, false, InteractionHand.MAIN_HAND));
        } else if (interactionTarget instanceof Animal && interactionTarget.getBoundingBox().getSize() <= maxSize) {
            ItemStack itemStack = ModItems.ENTITY_DISPLAY.get().getDefaultInstance();
            interactionTarget.setYRot(0.0F);
            interactionTarget.setYHeadRot(0.0F);
            interactionTarget.setYBodyRot(0.0F);
            interactionTarget.setXRot(0.0F);
            TCUtils.updateItemStackNbt(itemStack, interactionTarget::save);
            if (interactionTarget.hasCustomName()) {
                itemStack.set(DataComponents.CUSTOM_NAME, interactionTarget.getCustomName());
            }
            player.addItem(itemStack);
            interactionTarget.discard();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
