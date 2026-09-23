package org.confluence.mod.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.OldManNPC;
import org.confluence.mod.common.entity.npc.dialog.NPCDialogLoader;
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
        if (!(minecraft.level.getEntity(entityId) instanceof BaseNPC npc) || !npc.isAlive()
                || minecraft.player.distanceToSqr(npc) > 64.0D) {
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
        } else if (canTrade) {
            addTradeButtons(npc, buttonY);
        } else {
            addRenderableWidget(Button.builder(Component.translatable("gui.confluence.dialog"), button -> selectDialog(npc)).width(80).pos(width / 2 - 40, buttonY).build());
        }
    }

    protected void selectDialog(BaseNPC npc) {
        String key = NPCDialogLoader.getInstance().getRandomDialogKey(npc.getRandom1211(), npc.getType());
        if (key != null) dialogText = Component.translatable(key);
    }

    protected void addTradeButtons(BaseNPC npc, int buttonY) {
        addRenderableWidget(Button.builder(Component.translatable("gui.confluence.dialog"), button -> selectDialog(npc)).width(80).pos(width / 2 - 85, buttonY).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.confluence.shop"), button -> OpenNPCServicePacketC2S.sendToServer(entityId, OpenNPCServicePacketC2S.TRADE)).width(80).pos(width / 2 + 5, buttonY).build());
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
