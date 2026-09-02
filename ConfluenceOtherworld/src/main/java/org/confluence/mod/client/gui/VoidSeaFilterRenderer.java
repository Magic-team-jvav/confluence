package org.confluence.mod.client.gui;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.confluence.mod.client.renderer.ModRenderer;
import org.confluence.mod.common.util.VoidSeaHelper;

import static org.confluence.mod.client.util.ClientVoidSeaConstants.*;

public final class VoidSeaFilterRenderer {
    private static TextureTarget refractionSource;
    private static float filterFade;

    private VoidSeaFilterRenderer() {}

    public static void renderFilter(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || !VoidSeaHelper.isEnd(level)) {
            filterFade = 0.0F;
            return;
        }

        LocalPlayer player = minecraft.player;
        if (player == null) {
            filterFade = 0.0F;
            return;
        }

        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        Camera camera = minecraft.gameRenderer.getMainCamera();
        Vec3 cameraPosition = camera.getPosition();
        boolean shouldShowFilter = !player.isSpectator()
                && VoidSeaHelper.isDimensionalOverlapEffect(player)
                && cameraPosition.y < VoidSeaHelper.getHeight(partialTick);
        filterFade = Mth.lerp(FILTER_FADE_SPEED, filterFade, shouldShowFilter ? 1.0F : 0.0F);
        if (filterFade <= 0.001F) {
            return;
        }

        guiGraphics.flush();
        renderRefraction(minecraft, cameraPosition, partialTick, filterFade);
    }

    private static void renderRefraction(Minecraft minecraft, Vec3 position, float partialTick, float fade) {
        ShaderInstance shader = ModRenderer.getVoidSeaRefractionShader();
        ClientLevel level = minecraft.level;
        if (shader == null
                || level == null
                || !VoidSeaHelper.isEnd(level)) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        if (refractionSource == null) {
            refractionSource = new TextureTarget(mainTarget.width, mainTarget.height, false, Minecraft.ON_OSX);
        } else if (refractionSource.width != mainTarget.width
                || refractionSource.height != mainTarget.height) {
            refractionSource.resize(mainTarget.width, mainTarget.height, Minecraft.ON_OSX);
        }

        refractionSource.bindWrite(true);
        mainTarget.blitToScreen(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight(), false);
        mainTarget.bindWrite(true);
        RenderSystem.disableBlend();


        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        float filterProgress = getDamageProximity(player, position.y);
        shader.safeGetUniform("FilterProgress").set(filterProgress);
        shader.safeGetUniform("FilterFade").set(fade);
        shader.safeGetUniform("BlackFilterRadius").set(BLACK_FILTER_RADIUS);
        shader.safeGetUniform("BlackFilterMaxRadius").set(BLACK_FILTER_MAX_RADIUS);
        shader.safeGetUniform("PurpleFilterRadius").set(PURPLE_FILTER_RADIUS);
        shader.safeGetUniform("PurpleFilterMaxRadius").set(PURPLE_FILTER_MAX_RADIUS);
        shader.safeGetUniform("FilterTransitionRatio").set(FILTER_TRANSITION_RATIO);
        shader.safeGetUniform("FilterTransitionStrength").set(FILTER_TRANSITION_STRENGTH);
        shader.safeGetUniform("FullScreenBlackFilterAlpha").set(FULL_SCREEN_BLACK_FILTER_ALPHA);
        shader.safeGetUniform("BlackFilterColor").set(BLACK_FILTER_COLOR.x, BLACK_FILTER_COLOR.y, BLACK_FILTER_COLOR.z);
        shader.safeGetUniform("BlackFilterAlpha").set(BLACK_FILTER_ALPHA);
        shader.safeGetUniform("BlackFilterMinAlpha").set(BLACK_FILTER_MIN_ALPHA);
        shader.safeGetUniform("PurpleFilterColor").set(PURPLE_FILTER_COLOR.x, PURPLE_FILTER_COLOR.y, PURPLE_FILTER_COLOR.z);
        shader.safeGetUniform("PurpleFilterAlpha").set(PURPLE_FILTER_ALPHA);
        shader.safeGetUniform("Time").set((level.getGameTime() + partialTick) / 20.0F * REFRACTION_SPEED);
        shader.setSampler("Sampler0", refractionSource);
        shader.safeGetUniform("Zoom").set(REFRACTION_ZOOM);
        shader.safeGetUniform("Distortion").set(REFRACTION_DISTORTION);
        shader.safeGetUniform("CenterDistortion").set(REFRACTION_CENTER_DISTORTION);
        renderRefractionPass(shader);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private static void renderRefractionPass(ShaderInstance shader) {
        shader.apply();
        BufferBuilder buffer = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
        buffer.addVertex(0.0F, 0.0F, 0.0F);
        buffer.addVertex(1.0F, 0.0F, 0.0F);
        buffer.addVertex(1.0F, 1.0F, 0.0F);
        buffer.addVertex(0.0F, 1.0F, 0.0F);
        BufferUploader.draw(buffer.buildOrThrow());
        shader.clear();
    }

    public static void computeFogColor(ViewportEvent.ComputeFogColor event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null
                || level.dimension() != Level.END) {
            return;
        }

        LocalPlayer player = minecraft.player;
        if (player == null
                || !VoidSeaHelper.isDimensionalOverlapEffect(player)) {
            return;
        }

        float seaHeight = VoidSeaHelper.getHeight((float) event.getPartialTick());
        if (event.getCamera().getPosition().y >= seaHeight) {
            return;
        }

        event.setRed(VOID_SEA_FOG_COLOR.x * VOID_SEA_FOG_BRIGHTNESS);
        event.setGreen(VOID_SEA_FOG_COLOR.y * VOID_SEA_FOG_BRIGHTNESS);
        event.setBlue(VOID_SEA_FOG_COLOR.z * VOID_SEA_FOG_BRIGHTNESS);
    }

    public static void renderFog(ViewportEvent.RenderFog event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null
                || level.dimension() != Level.END) {
            return;
        }

        LocalPlayer player = minecraft.player;
        if (player == null
                || !VoidSeaHelper.isDimensionalOverlapEffect(player)) {
            return;
        }

        float partialTick = (float) event.getPartialTick();
        Vec3 cameraPosition = event.getCamera().getPosition();
        float seaHeight = VoidSeaHelper.getHeight(partialTick);
        if (cameraPosition.y >= seaHeight) {
            return;
        }

        float damageProximity = getDamageProximity(player, player.getY());
        float fogReduction = Mth.lerp(damageProximity, NORMAL_FOG_REDUCTION, INTERFERENCE_FOG_REDUCTION);
        float farPlaneDistance = MINIMUM_FOG_DISTANCE * Math.max(0.25F, player.getWaterVision());
        Holder<Biome> biome = level.getBiome(player.blockPosition());
        if (biome.is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
            farPlaneDistance *= 0.85F;
        }

        event.setNearPlaneDistance(-8.0F);
        //noinspection MathClampMigration
        event.setFarPlaneDistance(Math.min(event.getFarPlaneDistance(),
                Math.max(farPlaneDistance, event.getFarPlaneDistance() * (1.0F - fogReduction))));
        event.setCanceled(true);
    }

    /// 获取由伤害高度决定的雾进度。
    public static float getDamageProximity(LocalPlayer player, double cameraY) {
        float damageHeight = VoidSeaHelper.getVoidErosionDeltaDamageHeight(player);
        return Mth.clamp(1.0F - ((float) cameraY - damageHeight) / DAMAGE_EFFECT_RANGE, 0.0F, 1.0F);
    }
}
