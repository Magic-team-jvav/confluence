package org.confluence.mod.common.event.game;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import org.confluence.lib.common.item.ColoredItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.entity.TreasureBagItemEntity;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.GunItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.item.accessory.GuideVooDooDollItem;
import org.confluence.mod.common.item.gun.ManaGunItem;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terra_guns.api.event.GunEvent;

import java.util.Collection;
import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class ItemEvents {
    @SubscribeEvent(receiveCanceled = true)
    public static void itemStackedOnOther(ItemStackedOnOtherEvent event) {
        ItemStack carried = event.getCarriedItem();
        ItemStack onSlot = event.getStackedOnItem();
        if (event.getClickAction() == ClickAction.SECONDARY && ModUtils.useKey(carried, onSlot, event.getPlayer())) {
            event.setCanceled(true);
        }
        if (carried.is(MaterialItems.GEL)) {
            ColoredItem.merge(carried, onSlot);
        }
    }

    @SubscribeEvent
    public static void attributeModifier(ItemAttributeModifierEvent event) {
        ItemStack itemStack = event.getItemStack();
        PrefixComponent prefix = PrefixUtils.getPrefix(itemStack);
        if (prefix == null ||
                prefix.type() == PrefixType.UNKNOWN ||
                prefix.type() == PrefixType.ACCESSORY || // 通过curios的事件添加
                prefix.modifiers().isEmpty()
        ) return;
        for (Map.Entry<Holder<Attribute>, Collection<AttributeModifier>> entry : prefix.modifiers().get().asMap().entrySet()) {
            Holder<Attribute> attribute = entry.getKey();
            for (AttributeModifier modifier : entry.getValue()) {
                event.addModifier(attribute, modifier, EquipmentSlotGroup.MAINHAND);
            }
        }
    }

    @SubscribeEvent
    public static void toss(ItemTossEvent event) {
        ItemEntity itemEntity = event.getEntity();
        ItemStack itemStack = itemEntity.getItem();
        if (itemStack.is(ModTags.Items.TREASURE_BAG)) {
            TreasureBagItemEntity.convert(itemEntity);
            event.setCanceled(true);
        } else if (itemStack.is(AccessoryItems.GUIDE_VOODOO_DOLL)) {
            GuideVooDooDollItem.setForward(event.getPlayer(), itemStack);
        } else if (itemStack.is(ModTags.Items.COINS)) {
            itemEntity.playSound(ModSoundEvents.COINS_SMALL.get());
        }
        ModUtils.makeItemAntigravity(itemEntity);
    }

    @SubscribeEvent
    public static void gunFire(GunEvent.GunFireEvent event) {
        if (event.getGun() instanceof ManaGunItem) {
            event.setFire(true);
        }
    }

    @SubscribeEvent
    public static void shirkAmmo(GunEvent.ShrinkBulletEvent event) {
        if (event.getGun() instanceof ManaGunItem) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void ammoData(GunEvent.AmmoDataEvent event) {
        Player player = event.getPlayer();
        float velocityModify = (float) player.getAttributeValue(TCAttributes.getRangedVelocity());
        float knockbackModify = (float) player.getAttributeValue(Attributes.ATTACK_KNOCKBACK);

        if (event.getGun() instanceof ManaGunItem manaGunItem) {
            event.setDamage(manaGunItem.getDamage());
            event.setInaccuracy(manaGunItem.getInaccuracy());
            event.setVelocity(manaGunItem.getVelocity());
            event.setPenetrate(manaGunItem.getPenetrate());
            event.setKnockback(manaGunItem.getKnockback());
            event.setCritical(manaGunItem.getCritical());
        }

        event.setVelocity(event.getVelocity() * velocityModify);
        event.setKnockback(event.getKnockback() * knockbackModify);
    }

    @SubscribeEvent
    public static void ammoSelection(GunEvent.AmmoSelectionEvent event) {
        if (GunItems.STAR_CANNON.toStack().is(event.getGun())) {
            event.setSelected(event.getAmmo().is(MaterialItems.FALLING_STAR.get()));
        }
    }

    @SubscribeEvent
    public static void extraInventory(GunEvent.InventoryExtraEvent event) {
        event.addAmmoFirst(ExtraInventory.of(event.getPlayer()).getAllAmmo());
    }
}
