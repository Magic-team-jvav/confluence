package org.confluence.mod.integration.terra_entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModAchievements;
import org.confluence.mod.common.menu.NPCTradesForgeMenu;
import org.confluence.mod.integration.terra_entity.brain.ConfluenceDemolitionistNPCAi;
import org.confluence.mod.integration.terra_entity.init.ModEffectStrategies;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.terraentity.api.event.LoadResourceEvent;
import org.confluence.terraentity.api.event.NPCEvent;
import org.confluence.terraentity.api.event.SummonEvent;
import org.confluence.terraentity.api.event.WhipRegisterModifyEvent;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.brain.NurseAi;
import org.confluence.terraentity.entity.npc.misc.NPCDialogs;
import org.confluence.terraentity.entity.npc.misc.NPCNames;
import org.confluence.terraentity.entity.npc.mood.NPCMoods;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.init.entity.TENpcEntities;

@EventBusSubscriber(modid = Confluence.MODID, bus = EventBusSubscriber.Bus.MOD)
public class TEEvents {
    @SubscribeEvent
    public static void onRegisterWhips(WhipRegisterModifyEvent event) {
        // 子模块的鞭子数值比本体低
        event.setDamage(event.getDamage() / WhipRegisterModifyEvent.damageFactor);
    }

    @SubscribeEvent
    public static void onInteractNpc(NPCEvent.InteractNPCEvent event) {
        event.setRedirection((npc, player) -> {
//            if(npc.trades != null) { // 不是所有npc都有菜单
            boolean canForge = npc.getType() == TENpcEntities.GOBLIN_TINKERER.get();
            player.openMenu(new SimpleMenuProvider((id, inventory, player1) ->
                    new NPCTradesForgeMenu(id, inventory, npc, canForge), Component.translatable("container.confluence.npc_shop")));
//            }
        });
    }

    @SubscribeEvent
    public static void onInitNpcTrade(NPCEvent.InitNPCTradeEvent event) {
        event.setRedirection(Confluence.asResource(event.getOrigin().getPath()));
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
            // TEST
//            collector.getNPC().getMood().addMoodInfo(MoodInfos.GUILD1.get());
//            collector.getNPC().getMood().addMoodInfo(MoodInfos.GUILD2.get());
        });
        event.register(TENpcEntities.ARMS_DEALER.get(), (collector) -> {
            AbstractTerraNPC npc = collector.getNPC();
            npc.setAttackRange(10);
            npc.setCooldownTicks(20);
            npc.setCanPerformerAttackTest(e -> e.getMainHandItem().getItem() instanceof CrossbowItem);
        });
        event.register(TENpcEntities.GOBLIN_TINKERER.get(), (collector) -> {
            AbstractTerraNPC npc = collector.getNPC();
            // todo 尖钉球
            npc.setCanPerformerAttackTest(e -> e.getMainHandItem().getItem() instanceof BowItem);
        });
        event.register(TENpcEntities.NURSE.get(), (collector) -> {
            // todo 针管
            collector.setReplace(new NurseAi(collector.getNPC()));
        });
    }

    @SubscribeEvent
    public static void onLoadResource(LoadResourceEvent event) {
        if (event.getType() == LoadResourceEvent.Type.NPC_NAMES) {
            event.setReplace(true);
            event.addFile(Confluence.asResource(NPCNames.KEY + "/" + NPCNames.FILE_NAME + ".json"));
        } else if (event.getType() == LoadResourceEvent.Type.NPC_DIALOGS) {
            event.setReplace(true);
            event.addFile(Confluence.asResource(NPCDialogs.KEY + "/" + NPCDialogs.FILE_NAME + ".json"));
        } else if (event.getType() == LoadResourceEvent.Type.NPC_MOODS) {
            event.setReplace(true);
            event.addFile(Confluence.asResource(NPCMoods.KEY + "/" + NPCMoods.FILE_NAME + ".json"));
        }
    }

    @SubscribeEvent
    public static void summon$Pre(SummonEvent.Pre<?> event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer && serverPlayer.getData(TEAttachments.SUMMONER_STORAGE).getIds().size() >= 8) {
            ModAchievements.awardAchievement(serverPlayer, "you_and_what_army");
        }
    }

    public static void init(IEventBus eventBus) {
//        eventBus.addListener(TEEvents::onRegisterWhips);
//        eventBus.addListener(TEEvents::onInteractNpc);
//        eventBus.addListener(TEEvents::onInitNpcTrade);
//        eventBus.addListener(TEEvents::onRegisterBrain);
//        eventBus.addListener(TEEvents::onInitNpcName);

        ModTradeProviders.TYPES.register(eventBus);
        ModEffectStrategies.EFFECT_STRATEGY.register(eventBus);
    }
}
