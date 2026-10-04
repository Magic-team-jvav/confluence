package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.client.gui.container.SoulOverviewScreen;

public final class SoulSkillHandler {
    public static void handle(Minecraft minecraft) {
        SoulSkillClientHandler.INSTANCE.handle();
        boolean isSoulOverviewScreen = false;
        while (ModKeyBindings.SOUL_OVERVIEW.get().consumeClick()) {
            if (!isSoulOverviewScreen) {
                isSoulOverviewScreen = true;
            }
        }
        if (isSoulOverviewScreen) {
            minecraft.setScreen(new SoulOverviewScreen());
        }
    }
}
