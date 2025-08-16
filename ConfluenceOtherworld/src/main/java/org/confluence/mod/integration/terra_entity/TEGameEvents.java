package org.confluence.mod.integration.terra_entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.confluence.lib.color.GlobalColors;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.menu.NPCTradesForgeMenu;
import org.confluence.mod.integration.terra_entity.brain.ConfluenceDemolitionistNPCAi;
import org.confluence.mod.integration.terra_entity.npc_trade.SellTrade;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terraentity.api.event.NPCEvent;
import org.confluence.terraentity.api.event.SummonEvent;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.AnglerNPC;
import org.confluence.terraentity.entity.npc.brain.NurseAi;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.registries.npc_trade_task.variant.DynamicAnglerTradeTask;

@EventBusSubscriber(modid = Confluence.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class TEGameEvents {
    @SubscribeEvent
    public static void onTravelMerchantGenerator(NPCEvent.TravelingMerchantGenerateTradeEvent event) {
        if (event.getNPC().getType() == TENpcEntities.TRAVELING_MERCHANT.get()) {
            int addition = NPCSpawner.INSTANCE.isPeddlersSatchelUsed() ? 1 : 0;
            int count = event.getNPC().getRandom().nextInt(4, 10);
            event.addTrade(SellTrade.INSTANCE); // 通过事件额外添加售卖交易
            event.setGenerateCount(count + addition);
        }
    }

    @SubscribeEvent
    public static void onInitNpcTrade(NPCEvent.InitNPCTradeEvent event) {
        if (event.getNPC().getType() == TENpcEntities.FEMALE_ANGLER.get()) { // 渔女直接使用渔夫的任务表
            event.setRedirection(Confluence.asResource(TENpcEntities.ANGLER.getId().getPath()));
        } else {
            event.setRedirection(Confluence.asResource(event.getOrigin().getPath()));
        }
    }

    @SubscribeEvent
    public static void onInteractNpc(NPCEvent.InteractNPCEvent event) {
        event.setRedirection((npc, player) -> {
            if (npc.getTradeManager() != null) {
                npc.getTradeManager().reCheckAvailableTrades(player);
            }
            EntityType<?> type = npc.getType();
            player.openMenu(new SimpleMenuProvider((id, inventory, player1) -> new NPCTradesForgeMenu(id, inventory, npc, type == TENpcEntities.GOBLIN_TINKERER.get()), Component.translatable("container.confluence.npc_shop")));

            IAbstractTerraNPC terraNPC = IAbstractTerraNPC.of(npc);
            if (terraNPC.confluence$shouldInteract()) {
                terraNPC.confluence$setShouldInteract(false);
                if (type == TENpcEntities.MECHANIC.get()) { // 仅设置NPC的区域，不标记alive（机械师不在区域内时会自动移除）
                    NPCSpawner.Region region = new NPCSpawner.Region(NPCSpawner.getNpcSpawnPos(player));
                    terraNPC.confluence$setRegion(region);
                    NPCSpawner.INSTANCE.applyBenedictions(npc);
                    NPCSpawner.INSTANCE.addSpawned(type);
                    NPCSpawner.broadcastMessageToRegion(player.level(), npc, Component.translatable("event.confluence.npc.arrived", type.getDescription(), npc.getName()).withColor(GlobalColors.NPC_ARRIVED.get()));
                }
            }

            if (npc instanceof AnglerNPC angler) {
                DynamicAnglerTradeTask task = angler.getFirstTask();
                if (task != null) {
                    PlayerSpecialData.of(player).setCurrentQuestedFish(task.getCurrentCost(), task.getCurrentLock());
                }
            }

            event.setResult(InteractionResult.CONSUME_PARTIAL);
        });
    }

    @SubscribeEvent
    public static void onCollectBrains(NPCEvent.NPCBrainCollectionEvent event) {
        event.register(TENpcEntities.DEMOLITIONIST.get(), (collector) -> {
            AbstractTerraNPC npc = collector.getNPC();
            collector.setReplace(new ConfluenceDemolitionistNPCAi(npc));
            npc.setAttackRange(5);
            npc.setCanPerformerAttackTest(e -> true);
        });
        event.register(TENpcEntities.GUIDE.get(), (collector) -> {
            AbstractTerraNPC npc = collector.getNPC();
            npc.setCanPerformerAttackTest(e -> e.getMainHandItem().getItem() instanceof BowItem);
        });
        event.register(TENpcEntities.ARMS_DEALER.get(), (collector) -> {
            AbstractTerraNPC npc = collector.getNPC();
            npc.setAttackRange(10);
            npc.setCooldownTicks(20);
            npc.setCanPerformerAttackTest(e -> e.getMainHandItem().getItem() instanceof CrossbowItem);
        });
        event.register(TENpcEntities.GOBLIN_TINKERER.get(), (collector) -> {
            AbstractTerraNPC npc = collector.getNPC();
            // todo 尖钉球已经有了
            npc.setCanPerformerAttackTest(e -> e.getMainHandItem().getItem() instanceof BowItem);
        });
        event.register(TENpcEntities.NURSE.get(), (collector) -> {
            // todo 针管
            collector.setReplace(new NurseAi(collector.getNPC()));
        });
    }

    @SubscribeEvent
    public static void summon$Pre(SummonEvent.Pre<?> event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && serverPlayer.getData(TEAttachments.SUMMONER_STORAGE).getIds().size() >= 8) {
            AchievementUtils.awardAchievement(serverPlayer, "you_and_what_army");
        }
    }
}
