package org.confluence.mod.integration.sodium;

import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.terraentity.integration.sodium_dynamic_light.SDHelper;


public class SodiumDynamicLightHelper {

    public static void registerDynamicLight(){
        if(!SDHelper.isLoaded.get()){
            return;
        }

        DynamicLightHandlers.registerDynamicLightHandler(ModEntities.ARROW_PROJECTILE.get(), entity->{
            return entity.modify.getLuminance();
        });


    }
}
