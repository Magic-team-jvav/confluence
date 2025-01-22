package org.confluence.mod.common.event.game;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.ShimmerItemTransmutationEvent;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.data.saved.ConfluenceCommand;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.effect.beneficial.HeartReachEffect;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.AxeItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.item.axe.BaseAxeItem;
import org.confluence.mod.common.item.common.ColoredItem;
import org.confluence.mod.network.s2c.EchoVisibilityPacketS2C;
import org.confluence.mod.network.s2c.ExtraInventorySyncPacketS2C;
import org.confluence.mod.network.s2c.FishingPowerInfoPacketS2C;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.api.event.AfterAccessoryAbilitiesFlushedEvent;
import org.confluence.terra_curio.api.event.RangePickupItemEvent;
import org.confluence.terra_curio.common.item.IFunctionCouldEnable;
import org.confluence.terra_curio.util.TCUtils;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.*;

import static net.minecraft.world.level.block.Block.dropResources;

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
        if (itemStack.is(ModTags.Items.COINS) && !event.canPickupWithin(TCUtils.getAccessoriesValue(living, AccessoryItems.COIN$PICKUP$RANGE).getA())) {
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
            serverPlayer.getData(ModAttachmentTypes.MANA_STORAGE).flushAbility(serverPlayer);
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
        ModRecipes.Brewing.registerRecipes(event.getBuilder()::addRecipe);
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
        }
    }

    @SubscribeEvent
    public static void dropBlock(BlockDropsEvent event) {
        BlockState state = event.getState();
        Entity breaker = event.getBreaker();
        ItemStack tool = event.getTool();

        // 再生法杖/再生之斧 时运
        if(tool.is(ModTags.Items.CROP_FORTUNE) && breaker != null && (state.is(BlockTags.CROPS) || state.getBlock() instanceof CropBlock)){
            BaseAxeItem.increaseDropsOnBlockBreak(breaker, tool, event.getDrops());
        }
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getEntity().level();
        ItemStack itemStack = event.getItemStack();

        // 再生之斧/再生法杖 右键自动收获
        if(!level.isClientSide && itemStack.is(ModTags.Items.CROP_FORTUNE)){
            BaseAxeItem.dropAndPlaceOnRightClick(event.getEntity(), event.getItemStack(), event.getPos());
        }

    }
}
