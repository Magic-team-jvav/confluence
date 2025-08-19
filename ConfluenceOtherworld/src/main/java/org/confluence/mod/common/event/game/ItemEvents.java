package org.confluence.mod.common.event.game;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import org.confluence.lib.common.item.ColoredItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.component.LootComponent;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.entity.TreasureBagItemEntity;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.common.GuideVooDooDollItem;
import org.confluence.mod.common.item.gun.ManaGunItem;
import org.confluence.mod.network.s2c.VisibilityPacketS2C;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terra_curio.common.item.IFunctionCouldEnable;
import org.confluence.terra_guns.api.event.GunEvent;

import java.util.Collection;
import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class ItemEvents {
    @SubscribeEvent
    public static void itemStackedOnOther(ItemStackedOnOtherEvent event) {
        ItemStack onSlot = event.getStackedOnItem();
        ItemStack carried = event.getCarriedItem();
        Item slotItem = onSlot.getItem();
        Player player = event.getPlayer();
        if (event.getClickAction() == ClickAction.SECONDARY) {
            if (carried.isEmpty()) {
                // 需要注意创造模式物品栏是仅客户端的，所以创造模式无法正常使用
                if (slotItem instanceof IFunctionCouldEnable couldEnable) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        couldEnable.cycleEnable(onSlot);
                        VisibilityPacketS2C.sendEcho(serverPlayer);
                    }
                    event.setCanceled(true);
                }
            }

            boolean isGoldenKey = carried.is(ToolItems.GOLDEN_DUNGEON_KEY);
            if ((isGoldenKey && onSlot.is(ConsumableItems.GOLDEN_LOCK_BOX) || (carried.is(ToolItems.SHADOW_KEY) && onSlot.is(ConsumableItems.OBSIDIAN_LOCK_BOX)))) {
                if (player instanceof ServerPlayer serverPlayer && LootComponent.open(serverPlayer, onSlot)) {
                    if (!serverPlayer.hasInfiniteMaterials()) {
                        if (isGoldenKey) {
                            carried.shrink(1);
                        }
                        onSlot.shrink(1);
                    }
                }
                event.setCanceled(true);
            }
        }
        if (slotItem instanceof ColoredItem && ItemStack.isSameItem(onSlot, carried)) {
            ColoredItem.setColor(carried, ColoredItem.getColor(onSlot));
        }
    }

    @SubscribeEvent
    public static void attributeModifier(ItemAttributeModifierEvent event) {
        ItemStack itemStack = event.getItemStack();
        PrefixComponent prefix = PrefixUtils.getPrefix(itemStack);
        if (prefix == null || prefix.type() == PrefixType.ACCESSORY || prefix.modifiers().isEmpty() || !PrefixUtils.couldReforge(itemStack)) return;
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
            TreasureBagItemEntity entity = new TreasureBagItemEntity(itemEntity.level(), itemEntity.position(), itemStack, null);
            entity.setPickUpDelay(40);
            entity.setDeltaMovement(itemEntity.getDeltaMovement());
            itemEntity.level().addFreshEntity(entity);
            itemEntity.discard();
            event.setCanceled(true);
        } else if (itemStack.is(AccessoryItems.GUIDE_VOODOO_DOLL)) {
            GuideVooDooDollItem.setDirection(itemStack, event.getPlayer());
        } else if (itemStack.is(ModTags.Items.COINS)) {
            itemEntity.playSound(ModSoundEvents.COINS_SMALL.get());
        }
        ModUtils.makeItemAntigravity(itemEntity);
    }

    @SubscribeEvent
    public static void gunFire(GunEvent.GunFireEvent event) {
        if (event.getGun() instanceof ManaGunItem manaGunItem) {
            float currentMana;
            if (event.getPlayer().isLocalPlayer()) {
                currentMana = ClientPacketHandler.getCurrentMana();
            } else {
                currentMana = ManaStorage.of(event.getPlayer()).getCurrentMana();
            }
            event.setFire(currentMana >= manaGunItem.getManaCost());
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
        float damageModify = (float) player.getAttributeValue(TCAttributes.getRangedDamage());
        float knockbackModify = (float) player.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        float criticalModify = (float) player.getAttributeValue(TCAttributes.getCriticalChance());

        if (event.getGun() instanceof ManaGunItem manaGunItem) {
            event.setDamage(manaGunItem.getDamage());
            event.setInaccuracy(manaGunItem.getInaccuracy());
            event.setVelocity(manaGunItem.getVelocity());
            event.setPenetrate(manaGunItem.getPenetrate());
            event.setKnockback(manaGunItem.getKnockback());
            event.setCritical(manaGunItem.getCritical());
        }

        event.setVelocity(event.getVelocity() * velocityModify);
        event.setDamage(event.getDamage() * damageModify);
        event.setKnockback(event.getKnockback() * knockbackModify);
        event.setCritical(event.getCritical() * criticalModify);
    }

    @SubscribeEvent
    public static void ammoSelection(GunEvent.AmmoSelectionEvent event) {
        if (GunItems.STAR_CANNON.toStack().is(event.getGun())) {
            event.setSelected(event.getAmmo().is(MaterialItems.FALLING_STAR.get()));
        }
    }

    @SubscribeEvent
    public static void extraInventory(GunEvent.InventoryExtraEvent event) {
        Player player = event.getPlayer();
        event.addAmmoFirst(ExtraInventory.of(player).getAllAmmo());
    }
}
