package org.confluence.mod.integration.jei;

import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import org.confluence.mod.client.gui.container.ExtraInventoryScreen;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ExtraInventoryHandler implements IGlobalGuiHandler {
    @Override
    public Collection<Rect2i> getGuiExtraAreas() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof ExtraInventoryScreen screen) {
            return List.of(
                    new Rect2i(screen.getGuiLeft() - 33, screen.getGuiTop(), 32, 256),
                    new Rect2i(screen.getGuiLeft() + 171, screen.getGuiTop(), 16, 256)
            );
        }
        return Collections.emptyList();
    }
}
