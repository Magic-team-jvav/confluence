package org.confluence.terraentity.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

import static org.confluence.terraentity.TerraEntity.MODID;

@EventBusSubscriber(modid = MODID,bus = EventBusSubscriber.Bus.GAME,value = Dist.CLIENT)
public class RenderEvent {
    @SubscribeEvent
    public static void guiEvent( RenderGuiLayerEvent.Pre event){
        if(event.getName().getPath().equals("boss_overlay")){
//            event.getGuiGraphics().setColor(0,1,0,1);
        }
    }
}
