package org.confluence.mod.common.event.game;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.ShimmerItemTransmutationEvent;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.data.saved.ConfluenceCommand;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.effect.beneficial.HeartReachEffect;
import org.confluence.mod.common.init.ModAttachments;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.item.common.ColoredItem;
import org.confluence.mod.common.item.potion.AbstractPotionItem;
import org.confluence.mod.network.s2c.EchoVisibilityPacketS2C;
import org.confluence.mod.network.s2c.FishingPowerInfoPacketS2C;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.api.event.AfterAccessoryAbilitiesFlushedEvent;
import org.confluence.terra_curio.api.event.RangePickupItemEvent;
import org.confluence.terra_curio.common.item.IFunctionCouldEnable;
import org.confluence.terra_curio.util.TCUtils;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class GameEvents {
    @SubscribeEvent
    public static void itemStackedOnOther(ItemStackedOnOtherEvent event) {
        ItemStack onSlot = event.getCarriedItem();
        ItemStack carried = event.getStackedOnItem(); // 非常奇怪,但事实如此
        Item item = onSlot.getItem();
        if (event.getClickAction() == ClickAction.SECONDARY && carried.isEmpty()) {
            // 需要注意创造模式物品栏是仅客户端的，所以创造模式无法正常使用
            Player player = event.getPlayer();
            if (item instanceof IFunctionCouldEnable couldEnable) {
                if (player instanceof ServerPlayer serverPlayer) {
                    couldEnable.cycleEnable(onSlot);
                    EchoVisibilityPacketS2C.sendToClient(serverPlayer);
                }
                event.setCanceled(true);
            }
        }
        if (ItemStack.isSameItem(onSlot, carried)) {
            if (item instanceof ColoredItem) {
                ColoredItem.setColor(carried, ColoredItem.getColor(onSlot));
            }
        }
    }

    @SubscribeEvent
    public static void shimmerTransmutation$Post(ShimmerItemTransmutationEvent.Post event) {
        if (ConfluenceData.get((ServerLevel) event.getSource().level()).isGraduated()) {
            ItemStack itemStack = event.getSource().getItem();
            Item item = itemStack.getItem();
            if (item == ToolItems.BOTTOMLESS_WATER_BUCKET.get()) {
                event.setShrink(1);
                event.setTargets(Collections.singletonList(new ItemStack(ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get())));
            } else if (item == ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get()) {
                event.setShrink(1);
                event.setTargets(Collections.singletonList(new ItemStack(ToolItems.BOTTOMLESS_WATER_BUCKET.get())));
            }
        }
    }

    @SubscribeEvent
    public static void rangePickupItem$Pre(RangePickupItemEvent.Pre event) {
        LivingEntity living = event.getEntity();
        float mana = TCUtils.getAccessoriesValue(living, AccessoryItems.MANA$PICKUP$RANGE).getA();
        float coin = TCUtils.getAccessoriesValue(living, AccessoryItems.COIN$PICKUP$RANGE).getA();
        float life = HeartReachEffect.getRange(living);
        event.setRange(Math.max(Math.max(Math.max(mana, coin), life), event.getRange()));
    }

    @SubscribeEvent
    public static void rangePickupItem$Post(RangePickupItemEvent.Post event) {
        LivingEntity living = event.getEntity();
        ItemStack itemStack = event.getItemEntity().getItem();
        if (itemStack.is(ModTags.Items.PROVIDE_MANA) && !event.canPickupWithin(TCUtils.getAccessoriesValue(living, AccessoryItems.MANA$PICKUP$RANGE).getA())) {
            event.setCanceled(true);
        }
        if (itemStack.is(ModTags.Items.COIN) && !event.canPickupWithin(TCUtils.getAccessoriesValue(living, AccessoryItems.COIN$PICKUP$RANGE).getA())) {
            event.setCanceled(true);
        }
        if (itemStack.is(ModTags.Items.PROVIDE_LIFE) && !event.canPickupWithin(HeartReachEffect.getRange(living))) {
            event.setCanceled(true);
        }
        if (!event.isCanceled() && !event.canPickupWithin(event.getOriginalRange())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void afterAccessoryAbilitiesFlushed(AfterAccessoryAbilitiesFlushedEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getData(ModAttachments.MANA_STORAGE).flushAbility(serverPlayer);
            FishingPowerInfoPacketS2C.sendToClient(serverPlayer);
            EchoVisibilityPacketS2C.sendToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void command(RegisterCommandsEvent event) {
        ConfluenceCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void curioAttributeModifier(CurioAttributeModifierEvent event) {
        PrefixComponent prefix = PrefixUtils.getPrefix(event.getItemStack());
        if (prefix == null) return;
        String suffix = "_" + event.getSlotContext().index();
        for (Map.Entry<Holder<Attribute>, Collection<AttributeModifier>> entry : prefix.modifiers().get().asMap().entrySet()) {
            Holder<Attribute> attribute = entry.getKey();
            for (AttributeModifier modifier : entry.getValue()) {
                event.addModifier(attribute, new AttributeModifier(modifier.id().withSuffix(suffix), modifier.amount(), modifier.operation()));
            }
        }
    }

    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        AbstractPotionItem.registerRecipes(event.getBuilder()::addRecipe);
    }
}
