package nowebsite.makertechno.the_trackers.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import nowebsite.makertechno.the_trackers.client.gui.provider.TextureCache;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public record Icon(String regName, @Nullable ResourceLocation location, int width, int height) implements IRenderElement {
    public static final Icon NONE = new Icon("none", null, 16, 16);
    public static final String DYNAMIC = "dynamic";

    @SuppressWarnings("all")
    @Override
    public void render(@NotNull GuiGraphics graphics, float partialTick) {
        if (this.equals(Icon.NONE)) return;
        graphics.blit(location(),
                0,0,
                0,0,
                width(), height(),
                width(), height()
        );
    }

    @Override
    public IRenderElement flush() {
        return regName.equals(DYNAMIC) ? this : TextureCache.getIcon(regName());
    }
}
