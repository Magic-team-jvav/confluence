package org.confluence.mod.client.effect.biome;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.fml.ModLoader;
import org.confluence.lib.util.LibRenderUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.BiomeSkyEffectRegisterEvent;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.util.OverworldUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.mesdag.portlib.event.client.PortRenderLevelStageEvent;

import java.util.Map;

public final class ClientBiomeEffectSystem {
    static final Map<ResourceLocation, BiomeSkyEffect> EFFECTS = new Object2ObjectOpenHashMap<>();
    private static final float BLEND_SPEED = 0.025F;

    private static @Nullable BiomeSkyEffect current;
    private static @Nullable BiomeSkyEffect target;
    private static float blend;

    public static void registerEffects() {
        EFFECTS.put(Confluence.asResource("the_corruption"), new BiomeSkyEffect(
                holder -> holder.is(ModTags.Biomes.THE_CORRUPTION),
                Confluence.asResource("textures/environment/corruption_sky.png"),
                null
        ));
        EFFECTS.put(Confluence.asResource("the_crimson"), new BiomeSkyEffect(
                holder -> holder.is(ModTags.Biomes.THE_CRIMSON),
                Confluence.asResource("textures/environment/crimson_sky.png"),
                null
        ));
        EFFECTS.put(Confluence.asResource("the_hallow"), new BiomeSkyEffect(
                holder -> holder.is(ModTags.Biomes.THE_HALLOW),
                null,
                TheHallowSkyRender::render
        ));
        EFFECTS.put(Confluence.asResource("moonlit_dry_sea"), new BiomeSkyEffect(
                holder -> holder.is(ModTags.Biomes.THE_END_SEA),
                null,
                MoonlitDrySeaSkyRender::render
        ));
        ModLoader.postEvent(new BiomeSkyEffectRegisterEvent(EFFECTS));
    }

    public static void tick(LocalPlayer player) {
        ResourceKey<Level> dimension = player.level().dimension();
        if (dimension != OverworldUtils.dimension()
                && dimension != Level.END) {
            current = null;
            target = null;
            blend = 0;
            return;
        }

        updateBlend();

        if (player.level().getGameTime() % 20 == 0) {
            applyDesired(resolve(player.level().getBiome(player.blockPosition())));
        }
    }

    private static void updateBlend() {
        if (blend <= 0 || blend >= 1) return;

        if (target == null) {
            blend = Math.min(1, blend + BLEND_SPEED);
            if (blend >= 1) {
                current = null;
                blend = 0;
            }
        } else {
            blend = Math.min(1, blend + BLEND_SPEED);
            if (blend >= 1) {
                current = target;
                target = null;
                blend = 0;
            }
        }
    }

    private static void applyDesired(@Nullable BiomeSkyEffect desired) {
        if (current == null && desired != null) {
            current = desired;
            return;
        }
        if (current == null) return;

        if (desired == null) {
            if (target == null && blend == 0) {
                blend = 0.001F;
            }
            return;
        }

        if (desired == target) return;

        if (desired == current && blend > 0) {
            BiomeSkyEffect tmp = target;
            target = current;
            current = tmp;
            blend = 1 - blend;
            return;
        }

        if (desired == current && blend == 0) return;

        current = desired;
        target = null;
        blend = 0;
    }

    @Nullable
    public static BiomeSkyEffect resolve(Holder<Biome> holder) {
        for (BiomeSkyEffect effect : EFFECTS.values()) {
            if (effect.matches(holder)) return effect;
        }
        return null;
    }

    // ---- rendering ----

    public static void renderSky(LocalPlayer player, PortRenderLevelStageEvent event) {
        if (current == null && target == null) return;

        boolean blendEnabled = LibRenderUtils.isBlendEnabled();
        int[] blendFunc = LibRenderUtils.getBlendFunc();
        boolean depthMask = LibRenderUtils.getCurrentDepthMask();
        @Nullable ShaderInstance oldShader = RenderSystem.getShader();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        if (target != null) {
            if (current != null) {
                boolean curTex = current.skyTexture() != null;
                boolean tgtTex = target.skyTexture() != null;
                if (curTex && tgtTex) {
                    renderCubemapFade(event, current, target, blend);
                } else if (curTex) {
                    renderCubemap(event, current.skyTexture(), 1 - blend);
                } else if (tgtTex) {
                    renderCubemap(event, target.skyTexture(), blend);
                }
                renderEffectsCrossFade(player, event, 1 - blend, blend);
            } else {
                if (target.skyTexture() != null) {
                    renderCubemap(event, target.skyTexture(), blend);
                }
                renderEffects(player, event, target, blend);
            }
        } else if (blend > 0) {
            float a = 1 - blend;
            if (current.skyTexture() != null) renderCubemap(event, current.skyTexture(), a);
            renderEffects(player, event, current, a);
        } else {
            if (current.skyTexture() != null) renderCubemap(event, current.skyTexture(), 1.0F);
            renderEffects(player, event, current, 1.0F);
        }

        RenderSystem.depthMask(depthMask);
        if (!blendEnabled) {
            RenderSystem.disableBlend();
        }
        RenderSystem.blendFuncSeparate(blendFunc[0], blendFunc[1], blendFunc[2], blendFunc[3]);
        if (oldShader != null) {
            RenderSystem.setShader(() -> oldShader);
        }
    }

    private static void renderEffects(LocalPlayer player, PortRenderLevelStageEvent event, BiomeSkyEffect type, float alphaMul) {
        if (type.renderer() == null) return;
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        type.renderer().render(player, event, alphaMul);
    }

    private static void renderEffectsCrossFade(LocalPlayer player, PortRenderLevelStageEvent event, float fromAlpha, float toAlpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        assert current != null && target != null;
        if (current.renderer() != null) current.renderer().render(player, event, fromAlpha);
        if (target.renderer() != null) target.renderer().render(player, event, toAlpha);
    }

    // ---- 6-face cubemap skybox ----

    /// Cubemap texture is a compact 3×2 layout:
    ///
    /// \[0,0]=TOP \[1,0]=BOTTOM \[2,0]=BACK
    ///
    /// \[0,1]=LEFT \[1,1]=FRONT \[2,1]=RIGHT
    static void renderCubemap(PortRenderLevelStageEvent event, ResourceLocation texture, float alphaMul) {
        if (alphaMul < 0.01F) return;

        Minecraft minecraft = Minecraft.getInstance();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        float rainLevel = minecraft.level == null ? 0.0F : minecraft.level.getRainLevel(partialTick);
        float rainAlpha = 1.0F - rainLevel;
        if (rainAlpha < 0.05F) return;

        float dayTime = minecraft.level == null ? 6000 : minecraft.level.getTimeOfDay(partialTick);
        float nightFactor = 1.0F - Mth.clamp((Mth.abs(dayTime - 12000.0F) - 4000.0F) / 4000.0F, 0.0F, 1.0F);
        float baseAlpha = nightFactor * 0.35F * rainAlpha * alphaMul;
        int a = (int) (baseAlpha * 255);

        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(event.getModelViewMatrix());

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, texture);

        float d = 100.0F;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        Matrix4f matrix4f = poseStack.last().pose();

        // top (+y)    cell [0,0]: (0,0),(1,0),(1,1),(0,1)
        addFace(builder, matrix4f, -d, d, -d, d, d, -d, d, d, d, -d, d, d, 0, 0, 1, 0, 1, 1, 0, 1, a);
        // bottom (-y) cell [1,0]: (1,0),(2,0),(2,1),(1,1)
        addFace(builder, matrix4f, -d, -d, d, d, -d, d, d, -d, -d, -d, -d, -d, 1, 0, 2, 0, 2, 1, 1, 1, a);
        // back (-z)   cell [2,0]: (2,0),(3,0),(3,1),(2,1)
        addFace(builder, matrix4f, d, -d, -d, -d, -d, -d, -d, d, -d, d, d, -d, 2, 0, 3, 0, 3, 1, 2, 1, a);
        // left (-x)   cell [0,1]: (0,1),(1,1),(1,2),(0,2)
        addFace(builder, matrix4f, -d, -d, -d, -d, -d, d, -d, d, d, -d, d, -d, 0, 1, 1, 1, 1, 2, 0, 2, a);
        // front (+z)  cell [1,1]: (1,1),(2,1),(2,2),(1,2)
        addFace(builder, matrix4f, -d, -d, d, d, -d, d, d, d, d, -d, d, d, 1, 1, 2, 1, 2, 2, 1, 2, a);
        // right (+x)  cell [2,1]: (2,1),(3,1),(3,2),(2,2)
        addFace(builder, matrix4f, d, -d, d, d, -d, -d, d, d, -d, d, d, d, 2, 1, 3, 1, 3, 2, 2, 2, a);

        MeshData data = builder.build();
        if (data != null) {
            BufferUploader.drawWithShader(data);
        }
    }

    private static void renderCubemapFade(PortRenderLevelStageEvent event, BiomeSkyEffect cur, BiomeSkyEffect tgt, float blend) {
        renderCubemap(event, cur.skyTexture(), 1 - blend);
        renderCubemap(event, tgt.skyTexture(), blend);
    }

    /// Adds a quad face with UV mapped to a 3×2 cubemap grid.
    /// uv values are in [0,3]×[0,2] cell coordinates, converted to [0,1] UV range.
    private static void addFace(BufferBuilder builder, Matrix4f matrix4f,
                                float x0, float y0, float z0, float x1, float y1, float z1,
                                float x2, float y2, float z2, float x3, float y3, float z3,
                                int u0c, int v0c, int u1c, int v1c,
                                int u2c, int v2c, int u3c, int v3c, int alpha) {
        builder.vertex(matrix4f, x0, y0, z0).uv(u0c / 3.0F, v0c / 2.0F).color(255, 255, 255, alpha);
        builder.vertex(matrix4f, x1, y1, z1).uv(u1c / 3.0F, v1c / 2.0F).color(255, 255, 255, alpha);
        builder.vertex(matrix4f, x2, y2, z2).uv(u2c / 3.0F, v2c / 2.0F).color(255, 255, 255, alpha);
        builder.vertex(matrix4f, x3, y3, z3).uv(u3c / 3.0F, v3c / 2.0F).color(255, 255, 255, alpha);
    }
}
