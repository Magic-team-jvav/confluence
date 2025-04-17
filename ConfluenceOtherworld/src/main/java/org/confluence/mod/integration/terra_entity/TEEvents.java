package org.confluence.mod.integration.terra_entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.brain.ConfluenceArmDealerNPCAi;
import org.confluence.mod.integration.terra_entity.brain.ConfluenceDemolitionistNPCAi;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.mod.integration.terra_entity.init.ModEffectStrategies;
import org.confluence.mod.common.menu.NPCTradesMenu;
import org.confluence.terraentity.api.event.LoadResourceEvent;
import org.confluence.terraentity.api.event.NPCEvent;
import org.confluence.terraentity.api.event.WhipRegisterModifyEvent;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.NPCDialogs;
import org.confluence.terraentity.entity.npc.NPCNames;
import org.confluence.terraentity.init.entity.TENpcEntities;

@EventBusSubscriber(modid = Confluence.MODID, bus = EventBusSubscriber.Bus.MOD)
public class TEEvents {

    @SubscribeEvent
    public static void onRegisterWhips(WhipRegisterModifyEvent event){
        // 子模块的鞭子数值比本体低
        event.setDamage(event.getDamage() / WhipRegisterModifyEvent.damageFactor);
    }

    @SubscribeEvent
    public static void onInteractNpc(NPCEvent.InteractNPCEvent event){
        event.setRedirection((npc, player)->{
            if(npc.trades != null) { // 不是所有npc都有菜单
                boolean canForge = npc.getType() == TENpcEntities.GOBLIN_TINKERER.get();
                player.openMenu(new SimpleMenuProvider((id, inventory, player1) ->
                        new NPCTradesMenu(id, inventory, npc.trades, canForge), Component.translatable("container.confluence.npc_shop")));

            }
        });
    }

    @SubscribeEvent
    public static void onInitNpcTrade(NPCEvent.InitNPCTradeEvent event){
        event.setRedirection(Confluence.asResource(event.getOrigin().getPath()));
    }

    @SubscribeEvent
    public static void onRegisterBrain(NPCEvent.NPCBrainRegisterEvent event){
        AbstractTerraNPC npc = event.getNPC();

        if (npc.getType() == TENpcEntities.DEMOLITIONIST.get()) {
            event.setReplace(new ConfluenceDemolitionistNPCAi(npc));
            npc.setAttackRange(5);
            npc.setCanPerformerAttackTest(e->true);
        } else if(npc.getType() == TENpcEntities.GUIDE.get()){
            npc.setCanPerformerAttackTest(e->e.getMainHandItem().getItem() instanceof BowItem);

        }else if(npc.getType() == TENpcEntities.ARMS_DEALER.get()){
            event.setReplace(new ConfluenceArmDealerNPCAi(npc));
            npc.setAttackRange(10);
            npc.setCooldownTicks(20);
            npc.setCanPerformerAttackTest(e->e.getMainHandItem().getItem() instanceof CrossbowItem);
        }else if(npc.getType() == TENpcEntities.GOBLIN_TINKERER.get()){
            // todo 尖顶球
            npc.setCanPerformerAttackTest(e->e.getMainHandItem().getItem() instanceof BowItem);
        }

    }

    @SubscribeEvent
    public static void onInitNpcName(LoadResourceEvent event){
        if(event.getType() == LoadResourceEvent.Type.NPC_NAMES) {
            event.addFile(Confluence.asResource(NPCNames.KEY + "/" + NPCNames.FILE_NAME + ".json"));
        }else if(event.getType() == LoadResourceEvent.Type.NPC_DIALOGS){
            event.addFile(Confluence.asResource(NPCDialogs.KEY + "/" + NPCDialogs.FILE_NAME + ".json"));

        }
    }



    public static void init(IEventBus eventBus){
//        eventBus.addListener(TEEvents::onRegisterWhips);
//        eventBus.addListener(TEEvents::onInteractNpc);
//        eventBus.addListener(TEEvents::onInitNpcTrade);
//        eventBus.addListener(TEEvents::onRegisterBrain);
//        eventBus.addListener(TEEvents::onInitNpcName);

        ModTradeProviders.TYPES.register(eventBus);
        ModEffectStrategies.EFFECT_STRATEGY.register(eventBus);

    }
}
