package org.confluence.mod.integration.xaero;

import net.minecraft.client.Minecraft;
import xaero.map.element.MapElementReader;

public class PylonWaypointElementRenderReader extends MapElementReader<PylonWaypointElement, PylonWaypointElementRenderContext, PylonWaypointElementRenderer> {
    @Override
    public boolean isHidden(PylonWaypointElement element, PylonWaypointElementRenderContext context) {
        return context.mapDimId != element.getWaystone().getDimension();
    }

    @Override
    public double getRenderX(PylonWaypointElement element, PylonWaypointElementRenderContext context, float partialTicks) {
        return element.getX();
    }

    @Override
    public double getRenderZ(PylonWaypointElement element, PylonWaypointElementRenderContext context, float partialTicks) {
        return element.getZ();
    }

    @Override
    public int getInteractionBoxLeft(PylonWaypointElement element, PylonWaypointElementRenderContext context, float partialTicks) {
        return -28;
    }

    @Override
    public int getInteractionBoxRight(PylonWaypointElement element, PylonWaypointElementRenderContext context, float partialTicks) {
        return 28;
    }

    @Override
    public int getInteractionBoxTop(PylonWaypointElement element, PylonWaypointElementRenderContext context, float partialTicks) {
        return -21;
    }

    @Override
    public int getInteractionBoxBottom(PylonWaypointElement element, PylonWaypointElementRenderContext context, float partialTicks) {
        return 21;
    }

    @Override
    public int getRenderBoxLeft(PylonWaypointElement element, PylonWaypointElementRenderContext context, float partialTicks) {
        return -30;
    }

    @Override
    public int getRenderBoxRight(PylonWaypointElement element, PylonWaypointElementRenderContext context, float partialTicks) {
        return 30;
    }

    @Override
    public int getRenderBoxTop(PylonWaypointElement element, PylonWaypointElementRenderContext context, float partialTicks) {
        return -23;
    }

    @Override
    public int getRenderBoxBottom(PylonWaypointElement element, PylonWaypointElementRenderContext context, float partialTicks) {
        return 23;
    }

    @Override
    public int getLeftSideLength(PylonWaypointElement element, Minecraft minecraft) {
        return 0;
    }

    @Override
    public String getMenuName(PylonWaypointElement element) {
        return "";
    }

    @Override
    public String getFilterName(PylonWaypointElement element) {
        return "";
    }

    @Override
    public int getMenuTextFillLeftPadding(PylonWaypointElement element) {
        return 0;
    }

    @Override
    public int getRightClickTitleBackgroundColor(PylonWaypointElement element) {
        return 0;
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return true;
    }

    @Override
    public boolean isInteractable(int location, PylonWaypointElement element) {
        return true;
    }
}
