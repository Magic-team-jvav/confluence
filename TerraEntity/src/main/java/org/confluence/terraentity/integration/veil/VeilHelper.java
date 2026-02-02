package org.confluence.terraentity.integration.veil;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.PointLightData;
import net.minecraft.world.level.Level;

public class VeilHelper {

    public static Object initLight(Level level){
        if(level.isClientSide) {
            return new PointLightData();
        }
        return null;
    }


    public static void addLight(Level level, Object light){
        if(level.isClientSide) {
            VeilRenderSystem.renderer().getLightRenderer().addLight((PointLightData) light);
        }
    }

    public static void removeLight(Level level, Object light){
        if(level.isClientSide) {
            PointLightData lightData = (PointLightData) light;
            VeilRenderSystem.renderer().getLightRenderer().getLights(lightData.getType()).removeIf(l -> l.getLightData().equals(lightData));
        }
    }

    public static void setLight(Level level, Object light, double x, double y, double z, float brightness, float range, int color){
        if(level.isClientSide) {
            ((PointLightData)light).setPosition(x,y,z)
                    .setBrightness(brightness).setRadius(range)
                    .setColor(color >> 16 & 255, color >> 8 & 255, color & 255);
        }
    }
}
