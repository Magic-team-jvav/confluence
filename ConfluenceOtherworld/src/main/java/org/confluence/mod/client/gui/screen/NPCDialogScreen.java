package org.confluence.mod.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * NPC 对话界面基类 —— 渲染对话文本 + 按 E 关闭。
 * 子类自行添加按钮（交易/任务/对话等）。
 */
public class NPCDialogScreen extends Screen {
    protected static final int DIALOG_WIDTH = 200;
    protected final int entityId;
    protected Component dialogText = Component.empty();

    public NPCDialogScreen(int entityId) {
        super(Component.empty());
        this.entityId = entityId;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (!dialogText.getString().isEmpty()) {
            List<FormattedCharSequence> lines = font.split(dialogText, DIALOG_WIDTH);
            int y = height / 2 - lines.size() * font.lineHeight / 2 - 30;
            for (FormattedCharSequence line : lines) {
                guiGraphics.drawCenteredString(font, line, width / 2, y, 0xFFFFFF);
                y += font.lineHeight;
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (minecraft != null && minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static void open(int entityId) {
        Minecraft.getInstance().setScreen(new NPCDialogScreen(entityId));
    }
}
