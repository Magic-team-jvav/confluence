package org.confluence.mod.integration.xaero;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureManager;
import xaero.map.element.MapElementRenderer;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;

public class PylonWaypointElementRenderer extends MapElementRenderer<PylonWaypointElement, PylonWaypointContext, PylonWaypointElementRenderer> {
    public PylonWaypointElementRenderer() {
        super(new PylonWaypointContext(), new PylonWaypointElementRenderProvider(), new PylonWaypointElementRenderReader());
    }

    @Override
    public void beforeRender(int i, Minecraft minecraft, GuiGraphics guiGraphics, double v, double v1, double v2, double v3, float v4, double v5, double v6, TextureManager textureManager, Font font, MultiBufferSource.BufferSource bufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRendererProvider, boolean b) {

    }

    @Override
    public void afterRender(int i, Minecraft minecraft, GuiGraphics guiGraphics, double v, double v1, double v2, double v3, float v4, double v5, double v6, TextureManager textureManager, Font font, MultiBufferSource.BufferSource bufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRendererProvider, boolean b) {

    }

    @Override
    public void renderElementPre(int i, PylonWaypointElement pylonWaypointElement, boolean b, Minecraft minecraft, GuiGraphics guiGraphics, double v, double v1, double v2, double v3, float v4, double v5, double v6, TextureManager textureManager, Font font, MultiBufferSource.BufferSource bufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRendererProvider, float v7, double v8, double v9, boolean b1, float v10) {

    }

    @Override
    public boolean renderElement(int i, PylonWaypointElement pylonWaypointElement, boolean b, Minecraft minecraft, GuiGraphics guiGraphics, double v, double v1, double v2, double v3, float v4, double v5, double v6, TextureManager textureManager, Font font, MultiBufferSource.BufferSource bufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRendererProvider, int i1, double v7, float v8, double v9, double v10, boolean b1, float v11) {
        return false;
    }

    @Override
    public boolean shouldRender(int i, boolean b) {
        return false;
    }
}
