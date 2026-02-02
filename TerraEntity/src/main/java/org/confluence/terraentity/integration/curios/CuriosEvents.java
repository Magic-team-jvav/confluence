package org.confluence.terraentity.integration.curios;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import org.confluence.terraentity.api.entity.IPetMob;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.item.PetItem;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.List;

public class CuriosEvents {

    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();
        if(event.getIdentifier().equals(CuriosHelper.PET_KEY)){

            if(!from.isEmpty()){
                List<Entity> entities = event.getEntity().getData(TEAttachments.SUMMONER_STORAGE).getEntities(event.getEntity().level());
                for(Entity entity : entities){
                    if(entity instanceof IPetMob pet){
                        pet.asEntity().discard();
                    }
                }
            }
            if(!to.isEmpty() && to.getItem() instanceof PetItem<?> item && event.getEntity() instanceof Player player){
                item.summon(player, to);

            }
        }


    }
}
