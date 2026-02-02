package org.confluence.terraentity.integration.sodium_dynamic_light;

import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import net.minecraft.world.item.ItemStack;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.confluence.terraentity.init.entity.TESummonEntities;
import org.confluence.terraentity.init.item.TEBoomerangItems;

public class SDHelper {

    public static void registerDynamicLight(){

        DynamicLightHandlers.registerDynamicLightHandler(TESummonEntities.TERRAPRISMA.get(), o -> 15);
        DynamicLightHandlers.registerDynamicLightHandler(TEProjectileEntities.BOOMERANG_PROJECTILE.get(), entity -> {
            ItemStack stack = entity.weapon;
            if(stack != null){
                return stack.getItem() == TEBoomerangItems.FLAMARANG.get()? 8 : 0;
            }
            return 0;
        });
        DynamicLightHandlers.registerDynamicLightHandler(TEProjectileEntities.FIRE_IMP_PROJ.get(), e -> 10);
        DynamicLightHandlers.registerDynamicLightHandler(TEProjectileEntities.DARK_CASTER_PROJ.get(), e -> 6);
        DynamicLightHandlers.registerDynamicLightHandler(TEProjectileEntities.VILE_SPIT_PROJ.get(), e -> 6);
        DynamicLightHandlers.registerDynamicLightHandler(TEProjectileEntities.LAVA_PILLAR.get(), e -> {
            if(e.isTriggered()){
                return 12;
            }
            return 0;
        });
        DynamicLightHandlers.registerDynamicLightHandler(TEProjectileEntities.THE_DESTROYER_LASER_PROJ.get(), e -> 10);

    }
}
