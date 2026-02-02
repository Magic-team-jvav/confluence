package nowebsite.makertechno.the_trackers.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public interface IRenderElement {
    void render(@NotNull GuiGraphics graphics, float partialTick);
    IRenderElement flush();
    int width();
    int height();

    default float rescale() {
        return 1F;
    }
}
