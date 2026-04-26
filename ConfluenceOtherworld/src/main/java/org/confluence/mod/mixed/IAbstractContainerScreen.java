package org.confluence.mod.mixed;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriState;
import org.confluence.mod.common.item.GroupItem;
import org.confluence.mod.network.c2s.SwitchEffectEnabledPackedC2S;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.ApiStatus;

public interface IAbstractContainerScreen {
    @ApiStatus.OverrideOnly
    default TriState confluence$onMouseClicked(double mouseX, double mouseY, int button) {
        return TriState.DEFAULT;
    }

    default void confluence$setShouldRenderGroupBackground(boolean should) {}

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

    static void renderGroupBackground(GuiGraphics instance, ItemStack stack, int x, int y, Slot slot) {
        int id = GroupItem.getGroupId(stack);
        if (id == -1) return;
        int index = slot.getContainerSlot();
        if (hasNeighbour(index + 10, slot.container, id)) { // 右下
            instance.fill(x, y, x + 18, y + 18, 0x7F111111);
        } else { //                          右
            instance.fill(x, y, x + (hasNeighbour(index + 1, slot.container, id) ? 18 : 16), y + 16, 0x7F111111);
            if (hasNeighbour(index + 9, slot.container, id)) { // 下
                instance.fill(x, y + 16, x + 16, y + 18, 0x7F111111);
            }
        }
    }

    private static boolean hasNeighbour(int index, Container container, int id) {
        return index >= 0 && index < container.getContainerSize() && GroupItem.getGroupId(container.getItem(index)) == id;
    }
}
