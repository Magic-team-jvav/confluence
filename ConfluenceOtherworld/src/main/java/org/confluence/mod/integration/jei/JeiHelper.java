package org.confluence.mod.integration.jei;

import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.neoforged.fml.ModList;
import org.confluence.mod.client.gui.AchievementToast;

import java.util.ArrayList;
import java.util.List;

public class JeiHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("jei");

    public static List<ToastComponent.ToastInstance<?>> filterAchievements(List<ToastComponent.ToastInstance<?>> original) {
        List<ToastComponent.ToastInstance<?>> list = new ArrayList<>(original);
        list.removeIf(toastInstance -> toastInstance.getToast() instanceof AchievementToast);
        return list;
    }
}
