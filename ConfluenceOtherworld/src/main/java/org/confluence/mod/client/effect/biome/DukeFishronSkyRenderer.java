package org.confluence.mod.client.effect.biome;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.confluence.lib.util.LibRenderUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.DukeFishron;

public class DukeFishronSkyRenderer {
    private static final ResourceLocation SKY = Confluence.asResource("textures/environment/sky/duke_fishron_sky.png");
    private static final double RANGE = 48.0;
    private static final float FADE_STEP = 0.05F;
    private static final float CUBE_HALF_SIZE = 32.0F;
    private static float previousOpacity;
    private static float opacity;

    public static void tick(LocalPlayer player) {
        previousOpacity = opacity;
        boolean active = !player.level().getEntitiesOfClass(DukeFishron.class, player.getBoundingBox().inflate(RANGE),
                duke -> duke.isAlive() && duke.hasDarkSky() && duke.distanceToSqr(player) <= RANGE * RANGE).isEmpty();
        opacity = Mth.approach(opacity, active ? 1.0F : 0.0F, FADE_STEP);
    }

    public static void render(RenderLevelStageEvent event) {
        float alpha = Mth.lerp(event.getPartialTick(), previousOpacity, opacity);
        if (alpha < 0.01F || event.getCamera().getFluidInCamera() != FogType.NONE) return;

        boolean blend = LibRenderUtils.isBlendEnabled();
        boolean cull = LibRenderUtils.isCullEnabled();
        boolean depthMask = LibRenderUtils.getCurrentDepthMask();
        int[] blendFunc = LibRenderUtils.getBlendFunc();
        ShaderInstance shader = RenderSystem.getShader();
        int texture = RenderSystem.getShaderTexture(0);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        try {
            ClientBiomeEffectSystem.drawCubemap(event, SKY, alpha, CUBE_HALF_SIZE);
        } finally {
            RenderSystem.depthMask(depthMask);
            if (cull) RenderSystem.enableCull();
            if (!blend) RenderSystem.disableBlend();
            RenderSystem.blendFuncSeparate(blendFunc[0], blendFunc[1], blendFunc[2], blendFunc[3]);
            RenderSystem.setShaderTexture(0, texture);
            if (shader != null) RenderSystem.setShader(() -> shader);
        }
    }

    public static void clear() {
        previousOpacity = 0.0F;
        opacity = 0.0F;
    }
}
