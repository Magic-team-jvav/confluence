package org.confluence.mod.integration.terra_entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.mod.integration.terra_entity.init.ModEffectStrategies;
import org.confluence.mod.common.menu.NPCTradesMenu;
import org.confluence.terraentity.api.event.NPCEvent;
import org.confluence.terraentity.api.event.WhipRegisterModifyEvent;

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
                player.openMenu(new SimpleMenuProvider((id, inventory, player1) ->
                        new NPCTradesMenu(id, inventory, npc.trades, false), Component.translatable("container.confluence.npc_shop")));

            }});
    }

    @SubscribeEvent
    public static void onInitNpcTrade(NPCEvent.InitNPCTradeEvent event){
        event.setRedirection(Confluence.asResource(event.getOrigin().getPath()));
    }

    @SubscribeEvent
    public static void onRegisterBrain(NPCEvent.NPCBrainRegisterEvent event){
//        if(event.getNPC().getType() == TENpcEntities.DEMOLITIONIST.get()){
//            event.setReplace(new DemolitionistNPCAi(event.getNPC()));
//        }

    }


    public static void init(IEventBus eventBus){
        eventBus.addListener(TEEvents::onRegisterWhips);
        eventBus.addListener(TEEvents::onInteractNpc);
        eventBus.addListener(TEEvents::onInitNpcTrade);
        eventBus.addListener(TEEvents::onRegisterBrain);

        ModTradeProviders.TYPES.register(eventBus);
        ModEffectStrategies.EFFECT_STRATEGY.register(eventBus);

    }
}
