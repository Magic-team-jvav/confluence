package org.confluence.mod.mixed;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriState;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.GroupItem;
import org.confluence.mod.network.c2s.SwitchEffectEnabledPackedC2S;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.ApiStatus;

public interface IAbstractContainerScreen {
    @ApiStatus.OverrideOnly
    default TriState confluence$onMouseClicked(double mouseX, double mouseY, int button) {
        return TriState.DEFAULT;
    }

    @ApiStatus.OverrideOnly
    default TriState confluence$onMouseReleased(double mouseX, double mouseY, int button) {
        return TriState.DEFAULT;
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
        int id;
        if (stack.is(GroupItem.getInstance())) {
            id = stack.getOrDefault(ModDataComponentTypes.GROUP_STACKS, GroupItem.Stacks.EMPTY).id;
        } else {
            id = IClientItemStack.of(stack).confluence$getGroupId();
        }
        if (id != -1) {
            int maxX = x + 16;
            int maxY = y + 16;
            int index = slot.getContainerSlot();
            int color = 0x7F111111;
            if (hasNeighbour(index + 10, slot.container, id)) {
                instance.fill(x, y, maxX + 2, maxY + 2, color);
            } else {
                if (hasNeighbour(index + 1, slot.container, id)) {
                    instance.fill(maxX, y, maxX + 2, maxY, color);
                }
                if (hasNeighbour(index + 9, slot.container, id)) {
                    instance.fill(x, maxY, maxX, maxY + 2, color);
                }
                instance.fill(x, y, maxX, maxY, color);
            }
        }
    }

    private static boolean hasNeighbour(int index, Container container, int id) {
        return index >= 0 && index < container.getContainerSize() && IClientItemStack.of(container.getItem(index)).confluence$getGroupId() == id;
    }
}
