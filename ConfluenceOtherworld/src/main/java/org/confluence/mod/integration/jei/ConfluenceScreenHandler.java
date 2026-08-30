package org.confluence.mod.integration.jei;

import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import org.confluence.mod.client.gui.container.ExtraInventoryScreen;
import org.confluence.mod.client.gui.container.PiggyBankScreen;
import org.confluence.mod.util.ClientUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public enum ConfluenceScreenHandler implements IGlobalGuiHandler {
    INSTANCE;

    @Override
    public Collection<Rect2i> getGuiExtraAreas() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof ExtraInventoryScreen screen) {
            Rect2i left = new Rect2i(screen.getGuiLeft() - 33, screen.getGuiTop(), 32, 256);
            Rect2i right = new Rect2i(screen.getGuiLeft() + 171, screen.getGuiTop(), 16, 256);
            if (ClientUtils.shouldDisplayTeam()) {
                return List.of(left, right);
            }
            return List.of(left);
        } else if (minecraft.screen instanceof PiggyBankScreen screen) {
            return List.of(new Rect2i(screen.getGuiLeft() - 33, screen.getGuiTop(), 32, 100));
        }
        return Collections.emptyList();
    }
}
