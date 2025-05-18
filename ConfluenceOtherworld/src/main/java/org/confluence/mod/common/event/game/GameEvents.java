package org.confluence.mod.common.event.game;

import com.xiaohunao.equipment_benediction.common.event.AfterEquipmentBenedictionUpdatedEvent;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.heaven_destiny_moment.common.event.MomentEvent;
import com.xiaohunao.heaven_destiny_moment.common.moment.MomentInstance;
import com.xiaohunao.terra_moment.common.init.TMMoments;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.common.item.ColoredItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.ShimmerItemTransmutationEvent;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.component.LootComponent;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.data.ConfluenceCommand;
import org.confluence.mod.common.init.ModAchievements;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.network.s2c.ExtraInventorySyncPacketS2C;
import org.confluence.mod.network.s2c.FishingPowerInfoPacketS2C;
import org.confluence.mod.network.s2c.VisibilityPacketS2C;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.api.event.AfterAccessoryAbilitiesFlushedEvent;
import org.confluence.terra_curio.common.item.IFunctionCouldEnable;
import org.confluence.terra_guns.api.event.GunEvent;
import org.confluence.terraentity.network.s2c.SyncNPCTradesPacketS2C;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class GameEvents {
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
    public static void afterAccessoryAbilitiesFlushed(AfterAccessoryAbilitiesFlushedEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getData(ModAttachmentTypes.MANA_STORAGE).flushAbility(serverPlayer);
            FishingPowerInfoPacketS2C.sendAndGet(serverPlayer);
            VisibilityPacketS2C.sendEcho(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void afterEquipmentBenedictionUpdated(AfterEquipmentBenedictionUpdatedEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getData(ModAttachmentTypes.MANA_STORAGE).flushAbility(serverPlayer);
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
        if (CommonConfigs.BREWING_STAND_RECIPE.get()) {
            ModRecipes.Brewing.registerRecipes(event.getBuilder()::addRecipe);
        }
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        ServerPlayer serverPlayer = event.getPlayer();
        if (serverPlayer == null) {
            for (ServerPlayer player : event.getPlayerList().getPlayers()) {
                ExtraInventorySyncPacketS2C.sendToPlayersTrackingEntityAndSelf(player, player, player.getData(ModAttachmentTypes.EXTRA_INVENTORY));
            }
        } else {
            ExtraInventorySyncPacketS2C.sendToClient(serverPlayer, serverPlayer, serverPlayer.getData(ModAttachmentTypes.EXTRA_INVENTORY));
            SyncNPCTradesPacketS2C.sync(serverPlayer);
        }

    }

    @SubscribeEvent
    public static void moment$End(MomentEvent.End event) {
        MomentInstance momentInstance = event.getMomentInstance();
        if (momentInstance.getLevel() instanceof ServerLevel) {
            if (momentInstance.getMoment() == TMMoments.BLOOD_MOON.get()) {
                for (Player player : momentInstance.getPlayers()) {
                    ModAchievements.awardAchievement((ServerPlayer) player, "bloodbath");
                }
            }
        }
    }

    @SubscribeEvent
    public static void curioChange(CurioChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && PrefixUtils.canInit(event.getTo())) {
            PrefixUtils.initPrefix(player.getRandom(), event.getTo());
        }
    }

    @SubscribeEvent
    public static void shimmerItemTransmutation$Post(ShimmerItemTransmutationEvent.Post event) {
        MinecraftServer currentServer;
        if (event.getTargets() != null && (currentServer = ServerLifecycleHooks.getCurrentServer()) != null) {
            boolean corruption = IMinecraftServer.matchesSecretFlag(currentServer, IWorldOptions.THE_CORRUPTION);
            boolean crimson = IMinecraftServer.matchesSecretFlag(currentServer, IWorldOptions.TR_CRIMSON);
            if (corruption != crimson) {
                List<ItemStack> targets = new ArrayList<>();
                for (ItemStack target : event.getTargets()) {
                    if (corruption && target.is(MaterialItems.TR_CRIMSON_INGOT)) {
                        targets.add(MaterialItems.DEMONITE_INGOT.toStack(target.getCount()));
                    } else if (crimson && target.is(MaterialItems.DEMONITE_INGOT)) {
                        targets.add(MaterialItems.TR_CRIMSON_INGOT.toStack(target.getCount()));
                    } else {
                        targets.add(target);
                    }
                }
                event.setTargets(targets);
            }
        }
    }

    @SubscribeEvent
    public static void gun$ShrinkBullet(GunEvent.ShrinkBulletEvent event) {
        if (event.isCanceled() || event.isInfinity()) return;
        HookMapManager.postHooks(ModHookTypes.SKIP_AMMO_CONSUME.get(), (owner, hook, original) -> {
            if (hook.shouldSkipConsume(owner, original.getPlayer(), original.getBulletStack())) {
                original.setCanceled(true);
            }
            return original;
        }, event.getPlayer(), event);
    }
}
