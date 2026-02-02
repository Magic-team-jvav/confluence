package org.confluence.terraentity.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.event.NPCEvent;
import org.confluence.terraentity.config.TEAttributeModifierConfig;
import org.confluence.terraentity.data.saved_data.HouseStoreSaver;
import org.confluence.terraentity.entity.animation.HillOfFleshModelAnimationTable;
import org.confluence.terraentity.entity.npc.brain.ArmDealerNPCAi;
import org.confluence.terraentity.entity.npc.brain.DemolitionistNPCAi;
import org.confluence.terraentity.entity.npc.brain.NurseAi;
import org.confluence.terraentity.entity.npc.brain.OldManAi;
import org.confluence.terraentity.entity.npc.chat.ChatManager;
import org.confluence.terraentity.entity.npc.misc.NPCDialogs;
import org.confluence.terraentity.entity.npc.misc.NPCNames;
import org.confluence.terraentity.entity.npc.mood.NPCMood;
import org.confluence.terraentity.entity.npc.trade.NPCTradeManager;
import org.confluence.terraentity.entity.npc.trade.TradeModifiers;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.integration.ModChecker;
import org.confluence.terraentity.network.s2c.SyncDataS2C;
import org.confluence.terraentity.network.s2c.SyncNPCTradesPacketS2C;
import org.confluence.terraentity.registries.mappeddata.MappedDataLoader;
import org.confluence.terraentity.utils.AdapterUtils;

@EventBusSubscriber(modid = TerraEntity.MODID)
public class GameEvent {
    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        ServerPlayer serverPlayer = event.getPlayer();
        if (serverPlayer != null) {
            SyncNPCTradesPacketS2C.sync(serverPlayer);
            SyncDataS2C.syncAll(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void serverStartBefore(ServerAboutToStartEvent event) {
        AdapterUtils.postGameEvent(new NPCEvent.NPCBrainCollectionEvent());
        TEAttributeModifierConfig.getInstance().loadConfig();
    }

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event) {
        HouseStoreSaver.get(event.getServer().overworld());
    }

    @SubscribeEvent
    public static void serverStopped(ServerStoppedEvent event) {
        HouseStoreSaver.get(event.getServer().overworld());
    }

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(NPCNames.Loader.getInstance());
        event.addListener(NPCMood.Loader.getInstance());
        event.addListener(NPCDialogs.Loader.getInstance());
        event.addListener(NPCTradeManager.Loader.getInstance());
        event.addListener(TradeModifiers.getInstance());
        event.addListener(ChatManager.Loader.getInstance());
        event.addListener(HillOfFleshModelAnimationTable.getInstance());
        event.addListener(new MappedDataLoader());

    }

    @SubscribeEvent
    public static void onCollectBrains(NPCEvent.NPCBrainCollectionEvent event) {
        if (!ModChecker.confluence.isLoaded()) {
            event.register(TENpcEntities.DEMOLITIONIST.get(), (collector) -> {
                collector.setReplace(new DemolitionistNPCAi(collector.getNPC()));
            });
            event.register(TENpcEntities.GUIDE.get(), (collector) -> {
                collector.getNPC().setCanPerformerAttackTest(e -> e.getMainHandItem().getItem() instanceof BowItem);
//                collector.getNPC().getMood().addMoodInfo(MoodInfos.GUILD1.get());
//                collector.getNPC().getMood().addMoodInfo(MoodInfos.GUILD2.get());

            });
            event.register(TENpcEntities.ARMS_DEALER.get(), (collector) -> {
                collector.setReplace(new ArmDealerNPCAi(collector.getNPC()));
                collector.getNPC().setCanPerformerAttackTest(e -> e.getMainHandItem().getItem() instanceof CrossbowItem);
            });
            event.register(TENpcEntities.NURSE.get(), (collector) -> {
                collector.setReplace(new NurseAi(collector.getNPC()));
            });
            event.register(TENpcEntities.GOBLIN_TINKERER.get(), (collector) -> {
                collector.getNPC().setCanPerformerAttackTest(e -> e.getMainHandItem().getItem() instanceof BowItem);
            });
        }

        event.register(TENpcEntities.OLD_MAN.get(), collector -> collector.setReplace(new OldManAi(collector.getNPC())));
    }

//    @SubscribeEvent
//    static void onDataMapsUpdated(DataMapsUpdatedEvent event) {
//        event.ifRegistry(Registries.ITEM, (registry)->{
//            registry.getDataMap(TEDataMaps.WHIP_DATA_MAP).forEach((item, data)->{
//                if(BuiltInRegistries.ITEM.get(item) instanceof BaseWhipItem whip){
//                    List<ItemAttributeModifiers.Entry> modifiers = new ArrayList<>(whip.components().get(DataComponents.ATTRIBUTE_MODIFIERS).modifiers());
//                    int index = 0;
//                    for(var modifier : modifiers){
//                        if(modifier.matches(TEAttributes.SUMMON_DAMAGE, TerraEntity.space("whip_damage_modifier"))){
//                            if(data.damage().isPresent()){
//                                modifiers.set(index, new ItemAttributeModifiers.Entry(
//                                        TEAttributes.SUMMON_DAMAGE,
//                                        new AttributeModifier(TerraEntity.space("whip_damage_modifier"), data.damage().get(), modifier.modifier().operation()),
//                                        modifier.slot()));
//                            }
//                        }
//                        index++;
//                    }
//                }
//            });
//        });
//    }
}
