package nowebsite.makertechno.the_trackers.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class BaseComponent implements IRenderElement{
    private IRenderElement element;
    public BaseComponent(IRenderElement element) {
        this.element = element;
    }
    public void render(@NotNull GuiGraphics graphics, float partialTick) {
        element.render(graphics, partialTick);
    }
    public IRenderElement getElement() {
        return element;
    }
    public IRenderElement flush() {
        element = element.flush();
        return element;
    }

    @Override
    public int width() {
        return element.width();
    }

    @Override
    public int height() {
        return element.height();
    }

    @Override
    public float rescale() {
        return element.rescale();
    }
}
