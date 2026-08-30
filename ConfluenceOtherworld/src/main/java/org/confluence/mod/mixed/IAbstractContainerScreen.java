package org.confluence.mod.mixed;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.util.TriState;
import org.confluence.mod.network.c2s.SwitchEffectEnabledPackedC2S;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.ApiStatus;

public interface IAbstractContainerScreen {
    @ApiStatus.OverrideOnly
    default TriState confluence$onMouseClicked(double mouseX, double mouseY, int button) {
        return TriState.DEFAULT;
    }

    static IAbstractContainerScreen of(AbstractContainerScreen<?> screen) {
        return (IAbstractContainerScreen) screen;
    }

    static void switchEnabled(MobEffectInstance instance) {
        if (ModUtils.isSwitchableEffect(instance)) {
            IMobEffectInstance i = IMobEffectInstance.of(instance);
            i.confluence$setEnabled(!i.confluence$isEnabled());
            SwitchEffectEnabledPackedC2S.sendToServer(instance.getEffect(), i.confluence$isEnabled());
        }
    }

    static void makeTranslucent(GuiGraphics guiGraphics, MobEffectInstance instance, Runnable call) {
        if (IMobEffectInstance.of(instance).confluence$isEnabled()) {
            call.run();
        } else {
            RenderSystem.enableBlend();
            guiGraphics.setColor(1, 1, 1, 0.5F);
            call.run();
            guiGraphics.setColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
    }
}
