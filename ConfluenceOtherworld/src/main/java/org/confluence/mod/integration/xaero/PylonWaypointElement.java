package org.confluence.mod.integration.xaero;

import net.blay09.mods.waystones.api.Waystone;

public class PylonWaypointElement {
    private final Waystone waystone;
    private boolean renderedOnRadar;
    private final double x;
    private final double z;

    public PylonWaypointElement(Waystone waystone) {
        this.waystone = waystone;
        this.x = waystone.getPos().getX() + 0.5;
        this.z = waystone.getPos().getZ() + 0.5;
    }

    public Waystone getWaystone() {
        return waystone;
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }

    public void setRenderedOnRadar(boolean renderedOnRadar) {
        this.renderedOnRadar = renderedOnRadar;
    }

    public boolean wasRenderedOnRadar() {
        return this.renderedOnRadar;
    }
}
