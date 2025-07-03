package org.confluence.mod.integration.xaero;

import xaero.map.element.MapElementRenderProvider;

import java.util.Iterator;

public class PylonWaypointElementRenderProvider extends MapElementRenderProvider<PylonWaypointElement, PylonWaypointElementRenderContext> {
    private final PylonWaypointElementCollector collector;
    private Iterator<PylonWaypointElement> iterator;

    public PylonWaypointElementRenderProvider(PylonWaypointElementCollector collector) {
        this.collector = collector;
    }
    
    @Override
    public void begin(int location, PylonWaypointElementRenderContext context) {
        this.iterator = collector.getElements().iterator();
    }

    @Override
    public boolean hasNext(int location, PylonWaypointElementRenderContext context) {
        return iterator != null && iterator.hasNext();
    }

    @Override
    public PylonWaypointElement getNext(int location, PylonWaypointElementRenderContext context) {
        return iterator.next();
    }

    @Override
    public void end(int location, PylonWaypointElementRenderContext context) {
        this.iterator = null;
    }
}
