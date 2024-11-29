package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.util.ModUtils;
import org.confluence.terra_curio.client.handler.InformationHandler;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public final class WeatherHandler {
    private static final ParticleOptions WIND = new DustColorTransitionOptions(new Vector3f(0.8F), new Vector3f(1.0F), 1.0F);

    public static void handleWind(Minecraft minecraft, LocalPlayer player) {
        if (ClientConfigs.showWindParticles) {
            float windSpeedX = InformationHandler.getWindSpeedX();
            float windSpeedZ = InformationHandler.getWindSpeedZ();
            RandomSource random = player.getRandom();
            double x = player.getX() + ModUtils.nextFloat(random, -10, 10) - windSpeedX * 10;
            double y = player.getY() + ModUtils.nextFloat(random, -5, 5);
            double z = player.getZ() + ModUtils.nextFloat(random, -10, 10) - windSpeedZ * 10;
            Particle particle = minecraft.particleEngine.createParticle(WIND, x, y, z, 0.0, 0.0, 0.0);
            if (particle != null) particle.setParticleSpeed(windSpeedX, 0.0, windSpeedZ);
        }
    }
}
