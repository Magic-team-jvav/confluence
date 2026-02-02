package org.confluence.terraentity.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.ModRenderTypes;
import org.confluence.terraentity.client.util.ShaderUtil;
import org.confluence.terraentity.mixed.IBossEvent;
import org.confluence.terraentity.mixed.IShaderInstance;

import java.util.HashMap;
import java.util.Map;

import static org.confluence.terraentity.config.ClientConfig.*;


public class CustomizeBossHealthBar {
    public ResourceLocation tex;

    public CustomizeBossHealthBar(ResourceLocation tex) {
        this.tex = tex;
    }

    public void render(CustomizeGuiOverlayEvent.BossEventProgress event) {
        GuiGraphics g = event.getGuiGraphics();
        int w = 256;
        int h = 64;
        float segment = 0.6f;
        int x = (int) (Minecraft.getInstance().getWindow().getWidth() * 0.5f / Minecraft.getInstance().getWindow().getGuiScale() - w * 0.5f);
        int y = event.getY();

        float progress = event.getBossEvent().getProgress();
        int from = 37;
        int to = w - from;
        int pos = (int) (from + (to - from) * progress);

        if(bossBarStyle == 1){
            g.blit(tex, x, y, 0, 0, w, (int) (h * segment), w, h);
            g.blit(tex, x, (int) (y + 0.1f * h), 0, (int) (h * (segment - 0.015f)), pos, (int) (h * (1 - segment)), w, h);
        } else if(bossBarStyle == 2) {
            // 流动速度
            float speed = 0.01f;
            ((IShaderInstance) ModRenderTypes.Shaders.floatBarShader).getTerra_entity$Time().set(System.currentTimeMillis() % 100000 * speed);
            // 噪声强度
            ((IShaderInstance) ModRenderTypes.Shaders.floatBarShader).getTerra_entity$Radius().set(0.9f);
            g.blit(tex, x, y, 0, 0, w, (int) (h * segment), w, h);

            RenderSystem.setShaderTexture(0, tex);
            RenderSystem.setShaderTexture(1, TerraEntity.space("textures/gui/noise.png"));
            RenderSystem.setShader(() -> ModRenderTypes.Shaders.floatBarShader);

            ShaderUtil.shaderBlit(g.pose().last().pose(),
                    x, (int) (y + 0.1f * h),
                    0, (int) (h * (segment - 0.015f)),
                    pos, (int) (h * (1 - segment)),
                    w, h
            );

            float hp = ((IBossEvent)event.getBossEvent()).terra_enity$getBossHealth();
            float maxHealth = ((IBossEvent)event.getBossEvent()).terra_enity$getBossMaxHealth();
            String str = String.format("%.0f/%.0f", hp, maxHealth);
            int len = Minecraft.getInstance().font.width(str);
            g.pose().pushPose();
            float scale = 1.2f;
            g.pose().scale(scale,scale,scale);
            g.drawString(Minecraft.getInstance().font, String.format("%.0f / %.0f", hp, maxHealth), (int) ((x - len / 2 + 60 + BossBarNumberOffsetX.get())/scale), (int) ((y + BossBarNumberOffsetY.get())/scale), 0xbfa268);
//            g.drawString(Minecraft.getInstance().font, String.format("%.0f / %.0f", hp, maxHealth), (int) ((x - len / 2 - 20)/scale), (int) ((y + 15)/scale), 0xbfa268);

            g.pose().popPose();
        }

        event.setIncrement(40);
        event.setCanceled(true);
    }


    private static final Map<String, CustomizeBossHealthBar> bossHealthBars = new HashMap<>();

    // 用于重写render且自定义路径
    public static void registerBossHealthBars(String entityName, CustomizeBossHealthBar bossHealthBar) {
        bossHealthBars.put(entityName, bossHealthBar);
    }

    // 自定义路径
    public static void registerBossHealthBar(String eventName, ResourceLocation tex) {
        if (Minecraft.getInstance().getResourceManager().getResource(tex).isPresent() && !bossHealthBars.containsKey(eventName))
            bossHealthBars.put(eventName, new CustomizeBossHealthBar(tex));
    }

    //默认路径 textures/gui/{eventName}_bar.png
    public static void registerBossHealthBar(String eventName, EntityType<?> entityType) {
        registerBossHealthBar(eventName, defaultResource(entityType));
    }

    public static CustomizeBossHealthBar getBossHealthBars(String eventName) {
        return bossHealthBars.getOrDefault(eventName, null);
    }

    public static ResourceLocation defaultResource(EntityType<?> entityType) {
        ResourceLocation loc = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        return ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), "textures/gui/" + loc.getPath() + "_bar.png");
    }
}
