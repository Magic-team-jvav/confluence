package org.confluence.mod.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.confluence.mod.client.handler.ClientNPCDialogs;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.NurseNPC;
import org.confluence.mod.common.entity.npc.OldManNPC;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.network.c2s.NPCDialogSessionPacketC2S;
import org.confluence.mod.network.c2s.OpenNPCServicePacketC2S;
import org.confluence.mod.network.c2s.SummonSkeletronPacketC2S;
import org.jetbrains.annotations.NotNull;

import java.util.List;


/// NPC 对话界面基类 —— 渲染对话文本、商店入口与对话刷新按钮，按 E 关闭。
public class NPCDialogScreen extends Screen {
    protected static final int DIALOG_WIDTH = 200;
    protected final int entityId;
    protected final boolean canTrade;
    protected Component dialogText = Component.empty();
    private int sessionTicks;

    @Override
    public void tick() {
        super.tick();
        if (minecraft == null || minecraft.level == null || minecraft.player == null) return;
        if (!(minecraft.level.getEntity(entityId) instanceof BaseNPC npc) || !npc.isAlive() || npc.hurtTime > 0 || minecraft.player.distanceToSqr(npc) > 64.0D) {
            onClose();
            return;
        }
        if (sessionTicks++ % 20 == 0) NPCDialogSessionPacketC2S.send(entityId, true);
    }

    @Override
    public void removed() {
        super.removed();
        if (minecraft != null && minecraft.getConnection() != null)
            NPCDialogSessionPacketC2S.send(entityId, false);
    }

    public NPCDialogScreen(int entityId) {
        this(entityId, false);
    }

    public NPCDialogScreen(int entityId, boolean canTrade) {
        super(Component.empty());
        this.entityId = entityId;
        this.canTrade = canTrade;
    }

    @Override
    protected void init() {
        super.init();
        if (minecraft == null || minecraft.level == null) return;
        if (!(minecraft.level.getEntity(entityId) instanceof BaseNPC npc)) return;
        selectDialog(npc);
        int buttonY = height / 2 + 20;
        if (npc instanceof OldManNPC oldMan && oldMan.canSummonSkeletron()) {
            addRenderableWidget(Button.builder(Component.translatable("dialogs.confluence.old_man.curse"), button -> {
                SummonSkeletronPacketC2S.sendToServer(entityId);
                onClose();
            }).width(80).pos(width / 2 - 85, buttonY).build());
            addRenderableWidget(Button.builder(Component.translatable("gui.confluence.dialog"), button -> selectDialog(npc)).width(80).pos(width / 2 + 5, buttonY).build());
        } else if (npc instanceof NurseNPC) {
            addRenderableWidget(Button.builder(Component.translatable("gui.confluence.dialog"), button -> selectDialog(npc)).width(80).pos(width / 2 - 85, buttonY).build());
            addRenderableWidget(Button.builder(Component.translatable("gui.confluence.heal"), button -> OpenNPCServicePacketC2S.sendToServer(entityId, OpenNPCServicePacketC2S.HEAL)).width(80).pos(width / 2 + 5, buttonY).build());
        } else if (canTrade) {
            addTradeButtons(npc, buttonY);
        } else {
            addRenderableWidget(Button.builder(Component.translatable("gui.confluence.dialog"), button -> selectDialog(npc)).width(80).pos(width / 2 - 40, buttonY).build());
        }
        if (terraria()) toTerrariaButtons();
    }

    // ---------- 泰拉式对话框：屏幕上方的半透明面板，NPC 的话在上面，下面一排文字按钮（悬停变黄），世界不变暗 ----------
    private static final int PANEL_MAX_WIDTH = 320;
    private final List<org.confluence.mod.client.gui.container.TerraChestScreen.TextButton> terraButtons = new java.util.ArrayList<>();

    protected static boolean terraria() {
        return org.confluence.mod.client.ClientConfigs.terrariaInterface;
    }

    /// 把上面建好的按钮（各 NPC 不同：商店、治疗、重铸、诅咒……）换成泰拉的文字按钮，再加一个“关闭”
    private void toTerrariaButtons() {
        List<Button> buttons = new java.util.ArrayList<>();
        for (var child : children()) if (child instanceof Button button) buttons.add(button);
        clearWidgets();
        terraButtons.clear();
        for (Button button : buttons) {
            terraButtons.add(addRenderableWidget(new org.confluence.mod.client.gui.container.TerraChestScreen.TextButton(0, 0,
                    font.width(button.getMessage()), button.getMessage(), button::onPress)));
        }
        Component close = Component.translatable("gui.confluence.dialog.close");
        terraButtons.add(addRenderableWidget(new org.confluence.mod.client.gui.container.TerraChestScreen.TextButton(0, 0, font.width(close), close, this::onClose)));
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (terraria()) return; // 不变暗、不模糊
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderTerraria(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int panelWidth = Math.min(width - 20, PANEL_MAX_WIDTH);
        int left = (width - panelWidth) / 2;
        int top = 6;
        List<FormattedCharSequence> lines = font.split(dialogText, panelWidth - 16);
        int buttonsY = top + 8 + Math.max(1, lines.size()) * (font.lineHeight + 1) + 6;
        int bottom = buttonsY + font.lineHeight + 8;
        guiGraphics.fill(left, top, left + panelWidth, bottom, 0xC8182850);
        guiGraphics.renderOutline(left, top, panelWidth, bottom - top, 0xFF5070B8);
        int y = top + 8;
        for (FormattedCharSequence line : lines) {
            guiGraphics.drawString(font, line, left + 8, y, 0xFFFFFF, true);
            y += font.lineHeight + 1;
        }
        int x = left + 8;
        for (var button : terraButtons) {
            button.setX(x);
            button.setY(buttonsY);
            x += button.getWidth() + 14;
        }
        for (var renderable : renderables) renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        // 心情（泰拉的“幸福感”）：右下角，悬停看原因
        if (minecraft != null && minecraft.level != null && minecraft.level.getEntity(entityId) instanceof BaseNPC npc) {
            Component moodText = Component.translatable("gui.confluence.mood.value", npc.getMood().getValue());
            int moodX = left + panelWidth - 8 - font.width(moodText);
            guiGraphics.drawString(font, moodText, moodX, buttonsY + 1, 0xC0C0C0, true);
            if (mouseX >= moodX && mouseX < moodX + font.width(moodText) && mouseY >= buttonsY && mouseY < buttonsY + font.lineHeight + 1
                    && !npc.getMood().getReasons().isEmpty()) {
                guiGraphics.renderComponentTooltip(font, npc.getMood().getReasons(), mouseX, mouseY);
            }
        }
    }

    protected void selectDialog(BaseNPC npc) {
        String key = ClientNPCDialogs.getRandomDialogKey(npc.getRandom(), npc.getType());
        if (key != null) dialogText = Component.translatable(key);
    }

    protected void addTradeButtons(BaseNPC npc, int buttonY) {
        addRenderableWidget(Button.builder(Component.translatable("gui.confluence.dialog"), button -> selectDialog(npc)).width(80).pos(width / 2 - 85, buttonY).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.confluence.shop"), button -> OpenNPCServicePacketC2S.sendToServer(entityId, OpenNPCServicePacketC2S.TRADE)).width(80).pos(width / 2 + 5, buttonY).build());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (terraria() && !terraButtons.isEmpty()) {
            renderTerraria(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (!dialogText.getString().isEmpty()) {
            List<FormattedCharSequence> lines = font.split(dialogText, DIALOG_WIDTH);
            int y = height / 2 - lines.size() * font.lineHeight / 2 - 30;
            for (FormattedCharSequence line : lines) {
                guiGraphics.drawCenteredString(font, line, width / 2, y, 0xFFFFFF);
                y += font.lineHeight;
            }
        }
        if (minecraft != null && minecraft.level != null && minecraft.level.getEntity(entityId) instanceof BaseNPC npc) {
            Component moodText = Component.translatable("gui.confluence.mood.value", npc.getMood().getValue());
            int moodY = height / 2 + 55;
            guiGraphics.drawCenteredString(font, moodText, width / 2, moodY, 0xFFFFFF);
            if (mouseY >= moodY && mouseY < moodY + font.lineHeight
                    && Math.abs(mouseX - width / 2) <= font.width(moodText) / 2
                    && !npc.getMood().getReasons().isEmpty()) {
                guiGraphics.renderComponentTooltip(font, npc.getMood().getReasons(), mouseX, mouseY);
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
        open(entityId, false);
    }

    public static void open(int entityId, boolean canTrade) {
        Minecraft minecraft = Minecraft.getInstance();
        if (canTrade && minecraft.level != null && minecraft.level.getEntity(entityId) instanceof BaseNPC npc
                && npc.getType() == NpcEntities.GOBLIN_TINKERER.get()) {
            minecraft.setScreen(new GoblinTinkererDialogScreen(entityId));
        } else {
            minecraft.setScreen(new NPCDialogScreen(entityId, canTrade));
        }
    }
}
