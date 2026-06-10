package org.confluence.mod.common.event.game.entity;

import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.api.event.RegisterEvilMaterialReplacesEvent;
import org.confluence.mod.api.event.ShimmerItemTransmutationEvent;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.entity.TreasureBagItemEntity;
import org.confluence.mod.common.gameevent.SlimeRainGameEvent;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.GunItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.item.accessory.GuideVooDooDollItem;
import org.confluence.mod.common.item.axe.LucyTheAxe;
import org.confluence.mod.common.item.gun.ManaGunItem;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;

import java.util.Collection;
import java.util.Map;

@EventBusSubscriber(modid = Confluence.MODID)
public final class ItemEvents {
    @SubscribeEvent(receiveCanceled = true)
    public static void itemStackedOnOther(ItemStackedOnOtherEvent event) {
        ItemStack carried = event.getCarriedItem();
        ItemStack stackedOn = event.getStackedOnItem();
        Player player = event.getPlayer();
        if (event.getClickAction() == ClickAction.SECONDARY && ModUtils.useKey(carried, stackedOn, player)) {
            event.setCanceled(true);
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
        if (event.getPlayer() instanceof ServerPlayer player) {
            LucyTheAxe.onToss(player, itemStack);
        }
    }

    @SubscribeEvent
    public static void gunFire(GunEvent.GunFireEvent event) {
        if (event.getGun() instanceof ManaGunItem) {
            event.setFire(true);
        }
    }

    @SubscribeEvent
    public static void gun$ShrinkBullet(GunEvent.ShrinkBulletEvent event) {
        if (event.getGun() instanceof ManaGunItem) {
            event.setCanceled(true);
        } else if (!event.isInfinity() && PlayerUtils.shouldSkipConsumeAmmo(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void gun$AmmoData(GunEvent.AmmoDataEvent event) {
        Player player = event.getPlayer();
        float velocityModify = (float) player.getAttributeValue(LibAttributes.getRangedVelocity());
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
    public static void gun$AmmoSelection(GunEvent.AmmoSelectionEvent event) {
        if (GunItems.STAR_CANNON.toStack().is(event.getGun())) {
            event.setSelected(event.getAmmo().is(MaterialItems.FALLING_STAR.get()));
        }
    }

    @SubscribeEvent
    public static void gun$InventoryExtra(GunEvent.InventoryExtraEvent event) {
        event.addAmmoFirst(ExtraInventory.of(event.getPlayer()).getAllAmmo());
    }

    @SubscribeEvent
    public static void shimmerItemTransmutation$Pre(ShimmerItemTransmutationEvent.Pre event) {
        ItemEntity source = event.getSource();
        if (source.getItem().is(ConsumableItems.SLIME_CROWN) && SlimeRainGameEvent.INSTANCE.forceStart()) {
            source.level().playSound(null, source.getX(), source.getY(), source.getZ(), ModSoundEvents.SHIMMER_EVOLUTION.get(), SoundSource.AMBIENT, 0.5F, 1.0F);
            source.discard();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void shimmerItemTransmutation$Post(ShimmerItemTransmutationEvent.Post event) {
        if (event.getTargets() == null) return;
        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        if (currentServer == null) return;

        boolean corrupt = IMinecraftServer.matchesSecretFlag(currentServer, IWorldOptions.THE_CORRUPTION);
        boolean crimson = IMinecraftServer.matchesSecretFlag(currentServer, IWorldOptions.THE_CRIMSON);
        if (corrupt == crimson) return;

        event.setTargets(RegisterEvilMaterialReplacesEvent.replaceTargets(event.getTargets(), corrupt, crimson));
    }
}
