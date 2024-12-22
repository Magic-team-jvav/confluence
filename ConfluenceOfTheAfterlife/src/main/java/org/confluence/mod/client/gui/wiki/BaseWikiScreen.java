package org.confluence.mod.client.gui.wiki;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.GuiUtils;

import java.util.Random;

public class BaseWikiScreen extends Screen {
    protected BaseWikiScreen(Component title) {
        super(title);
        Random rd = new Random();
        String s = rd.nextDouble() <= 0.5D ? "c_" : "h_";
        PANORAMA_RESOURCES = new CubeMap(
                ResourceLocation.fromNamespaceAndPath(Confluence.MODID,
                        "textures/gui/title/background/" + s + "panorama"));
        PANORAMA = new PanoramaRenderer(PANORAMA_RESOURCES);
    }

    public final CubeMap PANORAMA_RESOURCES;

    public static final ResourceLocation PANORAMA_OVERLAY_TEXTURES =
            ResourceLocation.withDefaultNamespace(
                    "textures/gui/title/background/panorama_overlay.png");

    public final PanoramaRenderer PANORAMA;

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        PANORAMA.render(guiGraphics, width, height, 1.0F, partialTick);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        guiGraphics.blit(PANORAMA_OVERLAY_TEXTURES, 0, 0, this.width,
                this.height, 0.0F, 0.0F, 16, 128,
                16, 128);

        Button exit = GuiUtils.buildIconButton(guiGraphics, getExitIconLocation(),
                31, height - 51, 20, 20, 16, 16,
                (e) -> this.minecraft.setScreen(getParentScreen()),
                Component.translatable("wiki.confluence.back"));

        this.addRenderableWidget(exit);
    }

    public Screen getParentScreen(){
        return new TitleScreen();
    }

    public boolean useExitIcon(){
        return false;
    }

    public ResourceLocation getExitIconLocation(){
        return useExitIcon() ? ResourceLocation.fromNamespaceAndPath(Confluence.MODID,
                "textures/gui/icon/exit.png") :
                ResourceLocation.fromNamespaceAndPath(Confluence.MODID,
                        "textures/gui/icon/back.png");
    }
}
