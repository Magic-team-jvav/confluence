package org.confluence.mod.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.Confluence;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class TombstoneEditScreen extends AbstractSignEditScreen {
    private static final Vector3f TEXT_SCALE = new Vector3f(1.5F, 1.5F, 1.5F);
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/tombstone.png");
    private static final ResourceLocation GOLD_BACKGROUND = Confluence.asResource("textures/gui/gold_tombstone.png");
    // 墓碑背景中羊皮纸区域（未缩放坐标，原点为文字中心）
    private static final float PAPER_WIDTH = 156.0F;
    private static final float PAPER_HEIGHT = 96.0F;
    private static final float PAPER_CENTER_Y = 9.0F;
    private static final float MIN_TEXT_SCALE = 0.5F;
    private final boolean isGold;

    public TombstoneEditScreen(SignBlockEntity sign, boolean isFrontText, boolean isFiltered, boolean isGold) {
        super(sign, isFrontText, isFiltered, Component.empty());
        this.isGold = isGold;
    }

    @Override
    protected void renderSignBackground(GuiGraphics guiGraphics, BlockState state) {
        guiGraphics.pose().translate(0, 31, 0);
        guiGraphics.blit(isGold ? GOLD_BACKGROUND : BACKGROUND, -111, -100, 0, 0, 223, 166);
    }

    @Override
    protected Vector3f getSignTextScale() {
        return TEXT_SCALE;
    }

    /// 墓碑上的死亡信息往往比一行长得多（原版编辑界面不换行，文字会超出背景）。
    /// 当任意一行超出最大宽度时，把所有行自动换行并缩放到羊皮纸区域内。
    /// 玩家手动输入的行不会超宽（输入时已限制），此时返回 false，走原版渲染（带光标）。
    @Override
    protected void renderSignText(GuiGraphics guiGraphics) {
        int color = text.hasGlowingText() ? text.getColor().getTextColor() : SignRenderer.getDarkColor(text);
        boolean overflow = false;
        for (String message : messages) {
            if (message != null && font.width(message) > sign.getMaxTextLineWidth()) {
                overflow = true;
                break;
            }
        }
        if (!overflow) {
            super.renderSignText(guiGraphics);
            return;
        }

        int lineHeight = sign.getTextLineHeight();
        float scale = TEXT_SCALE.x();
        List<FormattedCharSequence> lines = wrap(font, messages, PAPER_WIDTH / scale);
        while (lines.size() * lineHeight * scale > PAPER_HEIGHT && scale > MIN_TEXT_SCALE) {
            scale = Math.max(MIN_TEXT_SCALE, scale - 0.05F);
            lines = wrap(font, messages, PAPER_WIDTH / scale);
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, PAPER_CENTER_Y - lines.size() * lineHeight * scale / 2.0F, 4.0F);
        guiGraphics.pose().scale(scale, scale, scale);
        for (int i = 0; i < lines.size(); i++) {
            FormattedCharSequence line = lines.get(i);
            guiGraphics.drawString(font, line, -font.width(line) / 2, i * lineHeight, color, false);
        }
        guiGraphics.pose().popPose();
    }

    private static List<FormattedCharSequence> wrap(Font font, String[] messages, float width) {
        List<FormattedCharSequence> lines = new ArrayList<>();
        int last = messages.length - 1;
        while (last >= 0 && (messages[last] == null || messages[last].isEmpty())) last--;
        for (int i = 0; i <= last; i++) {
            String message = messages[i];
            if (message == null || message.isEmpty()) {
                lines.add(FormattedCharSequence.EMPTY);
            } else {
                lines.addAll(font.split(Component.literal(message), (int) width));
            }
        }
        return lines;
    }
}
