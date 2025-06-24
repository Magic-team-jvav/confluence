package org.confluence.mod.integration.xaero;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureManager;
import org.confluence.mod.client.handler.CompatibilityHandler;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import xaero.map.element.MapElementRenderer;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;

public class PylonWaypointElementRenderer extends MapElementRenderer<PylonWaypointElement, PylonWaypointElementRenderContext, PylonWaypointElementRenderer> {
    public PylonWaypointElementRenderer() {
        super(new PylonWaypointElementRenderContext(), new PylonWaypointElementRenderProvider((PylonWaypointElementCollector) XaeroHelper.getCollector()), new PylonWaypointElementRenderReader());
    }

    @Override
    public void beforeRender(int location, Minecraft minecraft, GuiGraphics guiGraphics, double cameraX, double cameraZ, double mouseX, double mouseZ, float brightness, double scale, double guiBasedScale, TextureManager textureManager, Font font, MultiBufferSource.BufferSource bufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRendererProvider, boolean pre) {

    }

    @Override
    public void afterRender(int location, Minecraft minecraft, GuiGraphics guiGraphics, double cameraX, double cameraZ, double mouseX, double mouseZ, float brightness, double scale, double guiBasedScale, TextureManager textureManager, Font font, MultiBufferSource.BufferSource bufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRendererProvider, boolean pre) {

    }

    @Override
    public void renderElementPre(int location, PylonWaypointElement pylonWaypointElement, boolean hovered, Minecraft minecraft, GuiGraphics guiGraphics, double cameraX, double cameraZ, double mouseX, double mouseZ, float brightness, double scale, double guiBasedScale, TextureManager textureManager, Font font, MultiBufferSource.BufferSource bufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRendererProvider, float optionalScale, double partialX, double partialY, boolean cave, float partialTicks) {

    }

    @Override
    public boolean renderElement(int location, PylonWaypointElement pylonWaypointElement, boolean hovered, Minecraft minecraft, GuiGraphics guiGraphics, double cameraX, double cameraZ, double mouseX, double mouseZ, float brightness, double scale, double guiBasedScale, TextureManager textureManager, Font font, MultiBufferSource.BufferSource bufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRendererProvider, int elementIndex, double optionalDepth, float optionalScale, double partialX, double partialY, boolean cave, float partialTicks) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.scale(3, 3, 1);
        poseStack.translate(partialX - 7, partialY - 9.5, 0);
        guiGraphics.blit(WaystonesHelper.FOREST_PYLON_TEXTURE, 0, 0, 0, 0, 14, 19, 14, 19);
        poseStack.popPose();
        return true;
    }

    @Override
    public boolean shouldRender(int location, boolean pre) {
        return WaystonesHelper.IS_LOADED && CompatibilityHandler.isXaerosMapPylonWaypoint();
    }

    @Override
    public boolean shouldBeDimScaled() {
        return false;
    }
}
