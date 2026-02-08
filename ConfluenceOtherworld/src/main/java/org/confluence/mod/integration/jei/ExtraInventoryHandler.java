package org.confluence.mod.integration.jei;

import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import org.confluence.mod.client.gui.container.ExtraInventoryScreen;
import org.confluence.mod.util.ClientUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ExtraInventoryHandler implements IGlobalGuiHandler {
    @Override
    public Collection<Rect2i> getGuiExtraAreas() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof ExtraInventoryScreen screen) {
            Rect2i left = new Rect2i(screen.getGuiLeft() - 33, screen.getGuiTop(), 32, 256);
            Rect2i right = new Rect2i(screen.getGuiLeft() + 171, screen.getGuiTop(), 16, 256);
            if (ClientUtils.shouldDisplayTeam()) {
                return List.of(left, right);
            } else {
                return List.of(left);
            }
        }
        return Collections.emptyList();
    }
}
