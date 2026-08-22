package org.confluence.mod.api.client.animation;

public enum HandAnimationAction {
    IDLE("idle"),
    DRAW("draw"),
    PUT_AWAY("put_away"),
    SHOOT("shoot"),
    RELOAD("reload"),
    INSPECT("inspect");

    private final String id;

    HandAnimationAction(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }
}
