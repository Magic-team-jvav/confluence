package org.confluence.mod.api.client.animation;

/**
 * 枪械手持动画支持的标准动作。
 */
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
