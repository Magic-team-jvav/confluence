package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import org.confluence.mod.common.init.item.SwordItems;

public class LashRendererHandler {


    public static void render(RenderPlayerEvent.Post event){
        Player player = event.getEntity();
        ItemStack stack = player.getItemBySlot(EquipmentSlot.MAINHAND);

        if(stack.is(SwordItems.VOLCANO)){
//            float partialTicks = event.getPartialTick();
//            FishingHook hook = new FishingHook(player, player.level(), 0, 0);
////            hook.setPos(player.getEyePosition().add(player.getForward().scale(3)));
//
//            Minecraft.getInstance().getEntityRenderDispatcher().render(hook, 0,2,0,player.yHeadRot,partialTicks,event.getPoseStack(),event.getMultiBufferSource(),event.getPackedLight());
        }
    }
}
