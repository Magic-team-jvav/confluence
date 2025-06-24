package org.confluence.mod.integration.xaero;

import net.minecraft.client.Minecraft;
import xaero.map.element.MapElementReader;

public class PylonWaypointElementRenderReader extends MapElementReader<PylonWaypointElement, PylonWaypointContext, PylonWaypointElementRenderer> {
    @Override
    public boolean isHidden(PylonWaypointElement pylonWaypointElement, PylonWaypointContext pylonWaypointContext) {
        return false;
    }

    @Override
    public double getRenderX(PylonWaypointElement pylonWaypointElement, PylonWaypointContext pylonWaypointContext, float v) {
        return 0;
    }

    @Override
    public double getRenderZ(PylonWaypointElement pylonWaypointElement, PylonWaypointContext pylonWaypointContext, float v) {
        return 0;
    }

    @Override
    public int getInteractionBoxLeft(PylonWaypointElement pylonWaypointElement, PylonWaypointContext pylonWaypointContext, float v) {
        return 0;
    }

    @Override
    public int getInteractionBoxRight(PylonWaypointElement pylonWaypointElement, PylonWaypointContext pylonWaypointContext, float v) {
        return 0;
    }

    @Override
    public int getInteractionBoxTop(PylonWaypointElement pylonWaypointElement, PylonWaypointContext pylonWaypointContext, float v) {
        return 0;
    }

    @Override
    public int getInteractionBoxBottom(PylonWaypointElement pylonWaypointElement, PylonWaypointContext pylonWaypointContext, float v) {
        return 0;
    }

    @Override
    public int getRenderBoxLeft(PylonWaypointElement pylonWaypointElement, PylonWaypointContext pylonWaypointContext, float v) {
        return 0;
    }

    @Override
    public int getRenderBoxRight(PylonWaypointElement pylonWaypointElement, PylonWaypointContext pylonWaypointContext, float v) {
        return 0;
    }

    @Override
    public int getRenderBoxTop(PylonWaypointElement pylonWaypointElement, PylonWaypointContext pylonWaypointContext, float v) {
        return 0;
    }

    @Override
    public int getRenderBoxBottom(PylonWaypointElement pylonWaypointElement, PylonWaypointContext pylonWaypointContext, float v) {
        return 0;
    }

    @Override
    public int getLeftSideLength(PylonWaypointElement pylonWaypointElement, Minecraft minecraft) {
        return 0;
    }

    @Override
    public String getMenuName(PylonWaypointElement pylonWaypointElement) {
        return "";
    }

    @Override
    public String getFilterName(PylonWaypointElement pylonWaypointElement) {
        return "";
    }

    @Override
    public int getMenuTextFillLeftPadding(PylonWaypointElement pylonWaypointElement) {
        return 0;
    }

    @Override
    public int getRightClickTitleBackgroundColor(PylonWaypointElement pylonWaypointElement) {
        return 0;
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return false;
    }
}
