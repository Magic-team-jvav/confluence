package org.confluence.mod.client.renderer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class NoopTooltip implements ClientTooltipComponent {
    public static final NoopTooltip INSTANCE = new NoopTooltip();

    private NoopTooltip() {}

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getWidth(Font font) {
        return 0;
    }
}
