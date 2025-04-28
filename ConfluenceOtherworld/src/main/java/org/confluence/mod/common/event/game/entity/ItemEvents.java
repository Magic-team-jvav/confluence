package org.confluence.mod.common.event.game.entity;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.entity.TreasureBagItemEntity;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.item.gun.ManaGunItem;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_guns.api.event.GunEvent;
import org.confluence.terra_guns.common.init.TGItems;

import java.util.Collection;
import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class ItemEvents {
    @SubscribeEvent
    public static void attributeModifier(ItemAttributeModifierEvent event) {
        ItemStack itemStack = event.getItemStack();
        PrefixComponent prefix = PrefixUtils.getPrefix(itemStack);
        if (prefix == null) return;
        if (itemStack.is(Tags.Items.MELEE_WEAPON_TOOLS) ||
                itemStack.is(Tags.Items.MINING_TOOL_TOOLS) ||
                itemStack.is(Tags.Items.RANGED_WEAPON_TOOLS) ||
                itemStack.is(ModTags.Items.MANA_WEAPON) ||
                itemStack.is(ModTags.Items.PREFIX_UNIVERSAL_ONLY)
        ) {
            for (Map.Entry<Holder<Attribute>, Collection<AttributeModifier>> entry : prefix.modifiers().get().asMap().entrySet()) {
                Holder<Attribute> attribute = entry.getKey();
                for (AttributeModifier modifier : entry.getValue()) {
                    event.addModifier(attribute, modifier, EquipmentSlotGroup.MAINHAND);
                }
            }
        }
    }

    @SubscribeEvent
    public static void toss(ItemTossEvent event) {
        if (event.isCanceled()) return;
        ItemEntity itemEntity = event.getEntity();
        if (itemEntity.getItem().is(ModTags.Items.TREASURE_BAG)) {
            TreasureBagItemEntity entity = new TreasureBagItemEntity(itemEntity.level(), itemEntity.position(), itemEntity.getItem(), null);
            entity.setPickUpDelay(40);
            entity.setDeltaMovement(itemEntity.getDeltaMovement());
            entity.setGlowingTag(true);
            itemEntity.level().addFreshEntity(entity);
            itemEntity.discard();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void gunFire(GunEvent.GunFireEvent event){
        if (event.getGun() instanceof ManaGunItem manaGunItem) {
            int currentMana = ClientPacketHandler.getCurrentMana();
            event.setAlwaysFire(currentMana >= manaGunItem.getManaCost());
        }
    }

    @SubscribeEvent
    public static void shirkAmmo(GunEvent.ShrinkBulletEvent event){
        if (event.getGun() instanceof ManaGunItem) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void ammoData(GunEvent.AmmoDataEvent event){
        if (event.getGun() instanceof ManaGunItem manaGunItem){
            event.setDamage(manaGunItem.getDamage());
            event.setInaccuracy(manaGunItem.getInaccuracy());
            event.setVelocity(manaGunItem.getVelocity());
            event.setPenetrate(manaGunItem.getPenetrate());
            event.setKnockback(manaGunItem.getKnockback());
        }
    }
}
