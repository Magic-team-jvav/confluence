package org.confluence.terraentity.client.gui.renderer.chat.bubble;

import org.confluence.terraentity.api.npc.chat.IBubbleRenderer;

public enum BubbleConfig {
    CLOUD(CloudBubble.INSTANCE),
    RECT(RectBubble.INSTANCE);

    final IBubbleRenderer renderer;

    BubbleConfig(IBubbleRenderer renderer) {
        this.renderer = renderer;
    }

    public IBubbleRenderer getRenderer() {
        return renderer;
    }
}
