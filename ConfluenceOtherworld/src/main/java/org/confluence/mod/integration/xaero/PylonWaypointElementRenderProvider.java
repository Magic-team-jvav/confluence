package org.confluence.mod.integration.xaero;

import xaero.map.element.MapElementRenderProvider;

public class PylonWaypointElementRenderProvider extends MapElementRenderProvider<PylonWaypointElement, PylonWaypointContext> {
    @Override
    public void begin(int i, PylonWaypointContext pylonWaypointContext) {

    }

    @Override
    public boolean hasNext(int i, PylonWaypointContext pylonWaypointContext) {
        return false;
    }

    @Override
    public PylonWaypointElement getNext(int i, PylonWaypointContext pylonWaypointContext) {
        return null;
    }

    @Override
    public void end(int i, PylonWaypointContext pylonWaypointContext) {

    }
}
