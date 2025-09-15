package org.confluence.mod.client.gui;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.client.handler.bestiary.ClientBestiaryEntry;
import org.confluence.mod.client.handler.bestiary.FilterEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

public class BestiaryScreen extends Screen {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("bestiary/background");
    private static final ResourceLocation FILTERS = Confluence.asResource("bestiary/filters");

    private final @Nullable Screen parent;
    private int imageWidth;
    private int imageHeight;
    private int topPos;
    private int leftPos;

    private int maxPage;
    private int page;
    private Iterable<ClientBestiaryEntry> renderedEntries = List.of();
    private @Nullable ClientBestiaryEntry showedEntry;

    public BestiaryScreen(@Nullable Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.imageWidth = 220;
        this.imageHeight = 227;
        this.leftPos = (width - imageWidth) / 2;
        this.topPos = (height - imageHeight) / 2;
        Collection<ClientBestiaryEntry> entries = ClientBestiary.getInstance().getSortedEntries();
        for (ClientBestiaryEntry entry : entries) {
            entry.updateUnlockedProgress(getMinecraft().level); // 打开怪物图鉴再刷新
        }
        this.maxPage = entries.size() / 24;
        this.page = 0;
        this.renderedEntries = Iterables.limit(entries, 24);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int lastPage = page;
        Collection<ClientBestiaryEntry> entries = ClientBestiary.getInstance().getSortedEntries();
        if (entries.size() > 24) {
            this.maxPage = entries.size() / 24;
            this.page = Mth.clamp(page + Mth.sign(-scrollY), 0, maxPage);
            if (page == 0) {
                this.renderedEntries = Iterables.limit(entries, 24);
            } else {
                this.renderedEntries = Iterables.limit(Iterables.skip(entries, page * 24), 24);
            }
        } else {
            this.maxPage = 0;
            this.page = 0;
            this.renderedEntries = Iterables.limit(entries, 24);
        }
        return lastPage != page;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = leftPos + 8;
        int y = topPos + 8;
        for (ClientBestiaryEntry entry : renderedEntries) {
            if (mouseX > x && mouseX < x + 36 && mouseY > y && mouseY < y + 36) {
                if (entry.isLocked()) return false;
                this.showedEntry = entry;
                return true;
            }
            x += 36;
            if (x >= leftPos + 8 + 4 * 36) {
                x = leftPos + 8;
                y += 36;
            }
        }
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(BACKGROUND, 256, 256, 0, 0, leftPos, topPos, imageWidth, imageHeight);

        int x1 = leftPos + 8;
        int y1 = topPos + 8;
        for (ClientBestiaryEntry entry : renderedEntries) {
            guiGraphics.blitSprite(BACKGROUND, 256, 256, 220, 0, x1, y1, 35, 35);
            if (entry.isLocked()) {
                FilterEntry locked = FilterEntry.LOCKED;
                guiGraphics.blitSprite(FILTERS, 256, 256, locked.u(), locked.v(), x1 + (36 - locked.w()) / 2, y1 + (36 - locked.h()) / 2, locked.w(), locked.h());
            } else {
                InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, x1, y1, x1 + 35, y1 + 35, 10, 0.3F, mouseX, mouseY, entry.getRenderedEntity(getMinecraft().level));
            }

            x1 += 35;
            if (x1 >= leftPos + 8 + 4 * 35) {
                x1 = leftPos + 8;
                y1 += 35;
            }
        }

        if (maxPage > 0) {
            int i = Mth.lerpInt((float) page / maxPage, 7, 205);
            guiGraphics.blitSprite(BACKGROUND, 256, 256, 221, 37, leftPos + 152, topPos + i, 7, 15);
        }

        if (showedEntry != null) {
            float progress = showedEntry.getUnlockedProgress();
            PoseStack pose = guiGraphics.pose();
            int x2, y2;

            x1 = leftPos + 164;
            y1 = topPos + 8;
            // 背景图
            guiGraphics.blitSprite(showedEntry.getBackground(), 48, 48, 0, 0, x1, y1, 48, 48);
            // 实体
            InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, x1, y1, leftPos + 212, topPos + 56, 20, 0.3F, mouseX, mouseY, showedEntry.getRenderedEntity(getMinecraft().level));
            // 稀有度
            pose.pushPose();
            pose.translate(0, 0, 180);
            guiGraphics.blitSprite(BACKGROUND, 256, 256, 221, 53, x1, y1, 23, 7);
            for (int i = 0; i < showedEntry.getRarity(); i += 5) {
                if (i == 0) {
                    // 大星
                    guiGraphics.blitSprite(BACKGROUND, 256, 256, 222, 61, x1 + 1, y1 + 1, 5, 5);
                } else {
                    // 小星
                    guiGraphics.blitSprite(BACKGROUND, 256, 256, 227, 61, x1 + 2 + i, y1 + 1, 4, 5);
                }
            }
            pose.popPose();
            // 名字
            if (mouseX > x1 && mouseX < x1 + 48 && mouseY > y1 && mouseY < y1 + 48) {
                guiGraphics.renderTooltip(font, showedEntry.getDisplayName(), mouseX, mouseY);
            }
            // 杀死次数
            String text = Integer.toString(showedEntry.killedByCount);
            float lineHeight = font.lineHeight * 0.8F;
            pose.pushPose();
            pose.translate(leftPos + 212 - 1 - font.width(text) * 0.8F, topPos + 70 - lineHeight, 0);
            pose.scale(0.8F, 0.8F, 1);
            guiGraphics.drawString(font, text, 0, 0, 0xFFFFFF);
            pose.popPose();

            if (progress >= 0.2F) {
                x1 = leftPos + 187 - 1;
                y1 = topPos + 81 - 1;
                x2 = leftPos + 212 - 1;
                y2 = topPos + 92 - 1;
                // 生命值
                pose.pushPose();
                text = ATTRIBUTE_MODIFIER_FORMAT.format(showedEntry.maxHealth);
                pose.translate(x1 - font.width(text) * 0.8F, y1 - lineHeight, 0);
                pose.scale(0.8F, 0.8F, 1);
                guiGraphics.drawString(font, text, 0, 0, 0xFFFFFF);
                pose.popPose();
                // 击退抗性
                pose.pushPose();
                text = ATTRIBUTE_MODIFIER_FORMAT.format(showedEntry.knockbackResistance);
                pose.translate(x2 - font.width(text) * 0.8F, y1 - lineHeight, 0);
                pose.scale(0.8F, 0.8F, 1);
                guiGraphics.drawString(font, text, 0, 0, 0xFFFFFF);
                pose.popPose();
                // 攻击力
                pose.pushPose();
                text = ATTRIBUTE_MODIFIER_FORMAT.format(showedEntry.attackDamage);
                pose.translate(x1 - font.width(text) * 0.8F, y2 - lineHeight, 0);
                pose.scale(0.8F, 0.8F, 1);
                guiGraphics.drawString(font, text, 0, 0, 0xFFFFFF);
                pose.popPose();
                // 防御力
                pose.pushPose();
                text = ATTRIBUTE_MODIFIER_FORMAT.format(showedEntry.armor);
                pose.translate(x2 - font.width(text) * 0.8F, y2 - lineHeight, 0);
                pose.scale(0.8F, 0.8F, 1);
                guiGraphics.drawString(font, text, 0, 0, 0xFFFFFF);
                pose.popPose();
                // 钱币
                x1 = leftPos + 174 - 1;
                y1 = topPos + 105;
                for (Integer count : showedEntry.getCoins().platinum2Copper()) {
                    pose.pushPose();
                    text = count.toString();
                    pose.translate(x1 - font.width(text) * 0.8F, y1 - lineHeight, 0);
                    pose.scale(0.8F, 0.8F, 1);
                    guiGraphics.drawString(font, text, 0, 0, 0xFFFFFF);
                    pose.popPose();
                    x1 += 13;
                }
            }
            // 群系
            x1 = leftPos + 164;
            y1 = topPos + 109;
            x2 = x1 + 48;
            int t = x1;
            for (FilterEntry filter : showedEntry.getFilters()) {
                guiGraphics.blitSprite(FILTERS, 256, 256, filter.u(), filter.v(), x1 + (16 - filter.w()) / 2, y1 + (16 - filter.h()) / 2, filter.w(), filter.h());
                if (mouseX > x1 && mouseX < x1 + 16 && mouseY > y1 && mouseY < y1 + 16) {
                    guiGraphics.renderTooltip(font, filter.tooltip(), Optional.empty(), mouseX, mouseY);
                }
                x1 += 16;
                if (x1 >= x2) {
                    x1 = t;
                    y1 += 16;
                }
            }
            // 描述
            if (progress >= 0.2F) {
                x1 = leftPos + 164;
                if (!showedEntry.getFilters().isEmpty()) y1 += 16;
                Component description = showedEntry.getDescription();
                if (font.width(description) > 48) {
                    for (FormattedCharSequence sequence : font.split(description, 48)) {
                        guiGraphics.drawString(font, sequence, x1, y1, 0xFFFFFF);
                        y1 += font.lineHeight;
                    }
                } else {
                    guiGraphics.drawString(font, description, x1, y1, 0xFFFFFF);
                }
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        if (parent != null) {
            getMinecraft().setScreen(parent);
        }
    }
}
