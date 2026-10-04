package org.confluence.mod.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.container.npc_screen.NPCTradeItemOutline;
import org.confluence.mod.client.gui.hud.CustomBossBarRenderer;

import java.io.IOException;
import java.util.function.Consumer;

public class ModRenderer {
    public static final ResourceLocation SEA_SHADER_ID = Confluence.asResource("void_sea");
    public static final ResourceLocation SUBMERGED_SURFACE_SHADER_ID = Confluence.asResource("void_sea_submerged_surface");
    public static final ResourceLocation REFRACTION_SHADER_ID = Confluence.asResource("void_sea_refraction");
    public static final ResourceLocation TRADE_ITEM_OUTLINE_SHADER_ID = Confluence.asResource("trade_item_outline");
    public static final ResourceLocation BOSS_BAR_FLOW_SHADER_ID = Confluence.asResource("boss_bar_flow");
    /// 虚空海海面
    private static ShaderInstance voidSeaShader;
    private static ShaderInstance voidSeaSubmergedSurfaceShader;
    private static ShaderInstance voidSeaRefractionShader;
    /// 商品图标轮廓
    private static ShaderInstance tradeItemOutlineShader;
    public static void register(ResourceProvider resourceProvider, RegisterConsumer consumer) throws IOException {
        consumer.register(new ShaderInstance(resourceProvider, SEA_SHADER_ID, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
                shader -> voidSeaShader = shader);
        consumer.register(new ShaderInstance(resourceProvider, SUBMERGED_SURFACE_SHADER_ID, DefaultVertexFormat.BLIT_SCREEN),
                shader -> voidSeaSubmergedSurfaceShader = shader);
        consumer.register(new ShaderInstance(resourceProvider, REFRACTION_SHADER_ID, DefaultVertexFormat.BLIT_SCREEN),
                shader -> voidSeaRefractionShader = shader);
        consumer.register(new ShaderInstance(resourceProvider, TRADE_ITEM_OUTLINE_SHADER_ID, DefaultVertexFormat.POSITION_TEX_COLOR),
                shader -> {
                    tradeItemOutlineShader = shader;
                    NPCTradeItemOutline.setShader(shader);
                });
        consumer.register(new ShaderInstance(resourceProvider, BOSS_BAR_FLOW_SHADER_ID, DefaultVertexFormat.POSITION_TEX),
                CustomBossBarRenderer::setShader);
    }

    public static ShaderInstance getVoidSeaShader() {
        return voidSeaShader;
    }

    public static ShaderInstance getVoidSeaSubmergedSurfaceShader() {
        return voidSeaSubmergedSurfaceShader;
    }

    public static ShaderInstance getVoidSeaRefractionShader() {
        return voidSeaRefractionShader;
    }

    @FunctionalInterface
    public interface RegisterConsumer {
        void register(ShaderInstance shaderInstance, Consumer<ShaderInstance> onLoaded);
    }
}
