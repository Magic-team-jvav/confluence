package org.confluence.terra_guns.common.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terra_guns.common.init.TGDamageTypes;
import org.confluence.terra_guns.common.item.gun.BaseGun;

@EventBusSubscriber(modid = TerraGuns.MODID)
public class GameEvent {
    @SubscribeEvent
    public static void swapGunAnimator(LivingEquipmentChangeEvent event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer){
            if (event.getSlot() == EquipmentSlot.MAINHAND && event.getTo().getItem() instanceof BaseGun baseGun) {
                // 切枪
                baseGun.pickAnimator(event.getTo(), serverPlayer);
//            TerraGuns.LOGGER.info(event.getTo().toString());
            }
            if (event.getSlot() == EquipmentSlot.MAINHAND && event.getFrom().getItem() instanceof BaseGun) {
                // 收枪
//            TerraGuns.LOGGER.info(event.getFrom().toString());
            }
        }
    }

    @SubscribeEvent
    public static void hurtEvent(LivingIncomingDamageEvent event){
        if (event.getSource().is(TGDamageTypes.BULLET_DAMAGE)) {
            if (TerraGuns.IS_CONFLUENCE_LOADED) return;
            event.setInvulnerabilityTicks(0);
        }
    }
}
