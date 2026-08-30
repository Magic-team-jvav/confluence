package org.confluence.mod.common.item.common;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.map.BugNetEntityToItem;
import org.confluence.mod.common.init.item.ModItems;

import java.util.List;
import java.util.function.Predicate;

public class BugNetItem extends TooltipItem {
    private final double maxSize;
    private final Predicate<LivingEntity> predicate;

    public BugNetItem(ModRarity rarity, List<Component> tooltips, double maxSize, Predicate<LivingEntity> predicate) {
        super(new Properties().stacksTo(1), rarity, tooltips);
        this.maxSize = maxSize;
        this.predicate = predicate;
    }

//    @Override
//    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
//        if (usedHand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
//        if (player.isLocalPlayer()) {
//            ((LocalPlayer) player).connection.send(ServerboundInteractPacket.createInteractionPacket(interactionTarget, false, InteractionHand.MAIN_HAND));
//        } else if (predicate.test(interactionTarget) && interactionTarget.getBoundingBox().getSize() <= maxSize) {
//            ItemStack itemStack = BugNetEntityToItem.getItem((ServerPlayer) player, interactionTarget);
//            if (itemStack == null) {
//                itemStack = ModItems.ENTITY_DISPLAY.get().getDefaultInstance();
//                interactionTarget.setYRot(0.0F);
//                interactionTarget.setYHeadRot(0.0F);
//                interactionTarget.setYBodyRot(0.0F);
//                interactionTarget.setXRot(0.0F);
//                interactionTarget.stopRiding();
//                LibUtils.updateItemStackNbt(itemStack, interactionTarget::save);
//                if (interactionTarget.hasCustomName()) {
//                    itemStack.set(DataComponents.CUSTOM_NAME, interactionTarget.getCustomName());
//                }
//                itemStack.remove(ConfluenceMagicLib.MOD_RARITY);
//            }
//            if (!player.addItem(itemStack)) {
//                player.drop(itemStack, false);
//            }
//            interactionTarget.discard();
//            return InteractionResult.SUCCESS_NO_ITEM_USED;
//        }
//        return InteractionResult.SUCCESS;
//    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (usedHand != InteractionHand.MAIN_HAND) return InteractionResultHolder.pass(stack);

        double reach = 15;
        double squared = Mth.square(reach);
        Vec3 from = player.getEyePosition(1.0F);
        HitResult hitResult = player.pick(reach, 1.0F, false);
        double sqr = hitResult.getLocation().distanceToSqr(from);
        if (hitResult.getType() != HitResult.Type.MISS) {
            squared = sqr;
            reach = Math.sqrt(sqr);
        }
        Vec3 viewVector = player.getViewVector(1.0F);
        Vec3 to = from.add(viewVector.x * reach, viewVector.y * reach, viewVector.z * reach);
        AABB aabb = player.getBoundingBox().expandTowards(viewVector.scale(reach)).inflate(1.0);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                player, from, to, aabb, entity -> !entity.isSpectator() && entity.isPickable(), squared
        );
        if (entityHitResult != null && entityHitResult.getLocation().distanceToSqr(from) < sqr) l:{
            if (!(LibUtils.tryFindBeImpacted(entityHitResult.getEntity()) instanceof LivingEntity interactionTarget)) {
                break l;
            }
            if (player.isLocalPlayer()) {
                ((LocalPlayer) player).connection.send(ServerboundInteractPacket.createInteractionPacket(interactionTarget, false, InteractionHand.MAIN_HAND));
            } else if (predicate.test(interactionTarget) && interactionTarget.getBoundingBox().getSize() <= maxSize) {
                ItemStack itemStack = BugNetEntityToItem.getItem((ServerPlayer) player, interactionTarget);
                if (itemStack == null) {
                    itemStack = ModItems.ENTITY_DISPLAY.get().getDefaultInstance();
                    interactionTarget.setYRot(0.0F);
                    interactionTarget.setYHeadRot(0.0F);
                    interactionTarget.setYBodyRot(0.0F);
                    interactionTarget.setXRot(0.0F);
                    interactionTarget.stopRiding();
                    LibUtils.updateItemStackNbt(itemStack, interactionTarget::save);
                    if (interactionTarget.hasCustomName()) {
                        itemStack.set(DataComponents.CUSTOM_NAME, interactionTarget.getCustomName());
                    }
                    itemStack.remove(ConfluenceMagicLib.MOD_RARITY);
                }
                if (!player.addItem(itemStack)) {
                    player.drop(itemStack, false);
                }
                interactionTarget.discard();
            }
        }

        return InteractionResultHolder.success(stack);
    }
}
