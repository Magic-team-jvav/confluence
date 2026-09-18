package org.confluence.mod.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.handler.ClientBossBarTracker;
import org.confluence.mod.client.handler.ClientBossBarTracker.BossBarData;
import org.confluence.mod.common.init.entity.BossEntities;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.mesdag.portlib.wrapper.common.PortTranslatableEnum;

import java.util.Locale;
import java.util.Map;

public final class CustomBossBarRenderer {
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 46;
    private static final int HEALTH_LEFT = 37;
    private static final int HEALTH_WIDTH = 182;
    private static final int NUMBER_CENTER_X = 61;
    private static final int VERTICAL_INCREMENT = 40;
    private static ShaderInstance flowingFillShader;
    private static final Map<ResourceLocation, BarStyle> STYLES = Map.ofEntries(
            style(BossEntities.KING_SLIME),
            style(BossEntities.EYE_OF_CTHULHU),
            style(BossEntities.EATER_OF_WORLDS),
            style(BossEntities.BRAIN_OF_CTHULHU),
            style(BossEntities.QUEEN_BEE),
            style(BossEntities.SKELETRON),
            style(BossEntities.DEERCLOPS),
            style(BossEntities.WALL_OF_FLESH),
            style(BossEntities.HILL_OF_FLESH)
    );

    private CustomBossBarRenderer() {}

    public static void render(CustomizeGuiOverlayEvent.BossEventProgress event) {
        if (ClientConfigs.bossBarStyle == Style.VANILLA) return;
        BossBarData data = ClientBossBarTracker.get(event.getBossEvent().getId());
        if (data == null) return;
        BarStyle style = STYLES.get(data.entityType());
        if (style == null) return;

        GuiGraphics graphics = event.getGuiGraphics();
        int x = (event.getWindow().getGuiScaledWidth() - TEXTURE_WIDTH) / 2;
        int y = event.getY();
        int filledWidth = Mth.floor(HEALTH_WIDTH * Mth.clamp(event.getBossEvent().getProgress(), 0.0F, 1.0F));
        graphics.blit(style.frame(), x, y, 0.0F, 0.0F, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        if (filledWidth > 0) {
            if (ClientConfigs.bossBarStyle == Style.DYNAMIC && flowingFillShader != null) {
                renderFlowingFill(graphics, style.fill(), x + HEALTH_LEFT, y, filledWidth);
            } else {
                graphics.blit(style.fill(), x + HEALTH_LEFT, y, HEALTH_LEFT, 0.0F, filledWidth, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }
        }

        Minecraft minecraft = Minecraft.getInstance();
        graphics.drawString(minecraft.font, event.getBossEvent().getName(),
                (event.getWindow().getGuiScaledWidth() - minecraft.font.width(event.getBossEvent().getName())) / 2,
                y - 9, 0xFFFFFF);
        if (ClientConfigs.bossBarNumbersVisible && data.maximumHealth() > 0.0F) {
            String value = String.format(Locale.ROOT, "%.0f / %.0f",
                    Math.max(0.0F, data.health()), data.maximumHealth());
            graphics.drawString(minecraft.font, value, x + NUMBER_CENTER_X - minecraft.font.width(value) / 2,
                    y + 14, 0xBFA268);
        }
        event.setIncrement(VERTICAL_INCREMENT);
        event.setCanceled(true);
    }

    private static void renderFlowingFill(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width) {
        ShaderInstance shader = flowingFillShader;
        if (shader == null) return;
        if (shader.getUniform("Time") != null)
            shader.getUniform("Time").set((Util.getMillis() % 100000L) / 1000.0F);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(() -> shader);
        Matrix4f matrix = graphics.pose().last().pose();
        float minU = HEALTH_LEFT / (float) TEXTURE_WIDTH;
        float maxU = (HEALTH_LEFT + width) / (float) TEXTURE_WIDTH;
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(matrix, x, y, 0.0F).uv(minU, 0.0F).endVertex();
        builder.vertex(matrix, x, y + TEXTURE_HEIGHT, 0.0F).uv(minU, 1.0F).endVertex();
        builder.vertex(matrix, x + width, y + TEXTURE_HEIGHT, 0.0F).uv(maxU, 1.0F).endVertex();
        builder.vertex(matrix, x + width, y, 0.0F).uv(maxU, 0.0F).endVertex();
        BufferUploader.drawWithShader(builder.end());
    }

    private static Map.Entry<ResourceLocation, BarStyle> style(RegistryObject<? extends EntityType<?>> entityType) {
        return style(entityType, entityType.getId().getPath());
    }

    private static Map.Entry<ResourceLocation, BarStyle> style(RegistryObject<? extends EntityType<?>> entityType, String textureName) {
        ResourceLocation id = entityType.getId();
        String basePath = "textures/entity/boss_bar/" + textureName + "_bar_";
        return Map.entry(id, new BarStyle(
                ResourceLocation.fromNamespaceAndPath(id.getNamespace(), basePath + "1.png"),
                ResourceLocation.fromNamespaceAndPath(id.getNamespace(), basePath + "2.png")
        ));
    }

    @ApiStatus.Internal
    public static void setShader(ShaderInstance instance) {
        flowingFillShader = instance;
    }

    public enum Style implements PortTranslatableEnum {
        VANILLA,
        STATIC,
        DYNAMIC;

        @Override
        public Component getTranslatedName() {
            return Component.translatable("confluence.configuration.bossBarStyle." + name().toLowerCase(Locale.ROOT));
        }
    }

    private record BarStyle(ResourceLocation frame, ResourceLocation fill) {}
}
