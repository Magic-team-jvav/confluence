package org.confluence.mod.client.effect;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModParticleTypes;
import org.confluence.mod.mixed.IChunkSection;
import org.confluence.mod.util.DynamicBiomeUtils;
import software.bernie.geckolib.animation.EasingType;

@EventBusSubscriber(modid = Confluence.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class EctoMistHelper {
    public static boolean isGraveyard = false;
    private static float ectoMistStep = 1.0F;
    private static float originalNearFog = 0.0F;
    private static float originalFarFog = 0.0F;

    public static void tick(Minecraft minecraft, LocalPlayer player) {
        if (player.level().getGameTime() % 40 == 2) {
            IChunkSection iSection = DynamicBiomeUtils.getISection(player.level(), player.blockPosition());
            isGraveyard = iSection != null && iSection.confluence$isGraveyard();
        }
        if (isGraveyard && !minecraft.isPaused() && player.getRandom().nextInt(10) == 0) {
            player.level().addParticle(ModParticleTypes.ECTO_MIST.get(),
                    player.getX() + (player.getRandom().nextDouble() - 0.5) * 16,
                    player.getY() + player.getRandom().nextDouble() * 4,
                    player.getZ() + (player.getRandom().nextDouble() - 0.5) * 16,
                    0, 0, 0);
        }
    }

    @SubscribeEvent
    public static void fogColor(ViewportEvent.ComputeFogColor event) {
        if (isGraveyard) {
            if (ectoMistStep > 0.0F) {
                ectoMistStep -= 0.5F / Minecraft.getInstance().getFps();
            }
            float exp = Mth.clamp(ectoMistStep, 0.5F, 1.0F);
            event.setRed(event.getRed() * exp);
            event.setGreen(event.getGreen() * exp);
            event.setBlue(event.getBlue() * exp);
        } else if (ectoMistStep < 1.0F) {
            ectoMistStep += 0.5F / Minecraft.getInstance().getFps();
            float exp = Mth.clamp(ectoMistStep, 0.5F, 1.0F);
            event.setRed(event.getRed() * exp);
            event.setGreen(event.getGreen() * exp);
            event.setBlue(event.getBlue() * exp);
        } else {
            ectoMistStep = 1.0F;
        }
    }

    @SubscribeEvent
    public static void renderFog(ViewportEvent.RenderFog event) {
        if (isGraveyard) {
            float exp = (float) EasingType.exp(ectoMistStep);
            event.setNearPlaneDistance(Mth.lerp(exp, 4, originalNearFog));
            event.setFarPlaneDistance(Mth.lerp(exp, 8, originalFarFog));
            event.setFogShape(FogShape.SPHERE);
            event.setCanceled(true);
        } else if (ectoMistStep < 1.0F) {
            float exp = (float) EasingType.exp(ectoMistStep);
            event.setNearPlaneDistance(Mth.lerp(exp, 4, originalNearFog * 0.5F));
            event.setFarPlaneDistance(Mth.lerp(exp, 8, originalFarFog * 0.5F));
            event.setFogShape(FogShape.SPHERE);
            event.setCanceled(true);
        } else {
            originalNearFog = event.getNearPlaneDistance();
            originalFarFog = event.getFarPlaneDistance();
        }
    }
}
