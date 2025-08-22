package org.confluence.mod.mixed;

import net.neoforged.neoforge.common.util.TriState;

public interface IAbstractContainerScreen {
    default TriState confluence$onMouseClicked(double mouseX, double mouseY, int button) {
        return TriState.DEFAULT;
    }

    default TriState confluence$onMouseReleased(double mouseX, double mouseY, int button) {
        return TriState.DEFAULT;
    }
}
