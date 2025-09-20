package org.confluence.mod.client.gui;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.client.handler.bestiary.ClientBestiaryEntry;
import org.confluence.mod.client.handler.bestiary.FilterEntry;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

public class BestiaryScreen extends Screen {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("bestiary/background");
    private static final ResourceLocation FILTERS = Confluence.asResource("bestiary/filters");

    private static final int textureW = 512;
    private static final int textureH = 256;
    private static final int imageWidth = 220;
    private static final int imageHeight = 230;

    private static final int renderedEntryX = 8;
    private static final int renderedEntryY = 10;

    private static final int showedEntryX = 164;
    private static final int showedEntryY = renderedEntryY;
    private static final int showedEntryWH = 48;
    private static final float textScale = 0.8F;

    private final ClientBestiary bestiary;
    private int topPos;
    private int leftPos;

    private int maxPage;
    private int page;
    private Iterable<ClientBestiaryEntry> renderedEntries = List.of();
    private Collection<ClientBestiaryEntry> entries = List.of();
    private @Nullable ClientBestiaryEntry showedEntry;

    private GuiSprite slider;

    private boolean searchBoxOpened = false;
    private GuiSprite closedSearchBox;
    private GuiSprite openedSearchBox;
    private ClickableArea editBoxArea;
    private ClickableArea searchButtonArea;
    private ClickableArea closeSearchBoxArea;
    private EditBox editBox;

    private boolean filterOpened = false;
    private int filterMaxPage;
    private int filterPage;
    private Iterable<FilterEntry> renderedFilters = List.of();
    private GuiSprite closedFilter;
    private GuiSprite openedFilter;
    private ClickableArea closeFilterArea;

    private boolean sortOpened = false;
    private GuiSprite closedSort;
    private GuiSprite openedSortTop;
    private ClickableArea closeSortArea;
    private ClickableArea reverseSortArea;
    private GuiSprite incSort;
    private GuiSprite decSort;
    private EnumMap<ClientBestiary.SortType, GuiSprite> sortSelections;
    private GuiSprite selectedSort;
    private GuiSprite openedSortBottom;

    public BestiaryScreen() {
        super(Component.empty());
        this.bestiary = ClientBestiary.getInstance();
    }

    @Override
    protected void init() {
        this.leftPos = (width - imageWidth) / 2;
        this.topPos = (height - imageHeight) / 2;
        this.entries = bestiary.getSortedEntries();
        for (ClientBestiaryEntry entry : entries) {
            entry.updateUnlockedProgress(getMinecraft().level); // 打开怪物图鉴再刷新
        }
        updateRenderedEntries(0);

        this.slider = new GuiSprite(BACKGROUND, textureW, textureH, 221, 37, 7, 15).setX(leftPos + 153);

        int searchBoxX = leftPos + imageWidth;
        int searchBoxY = topPos;
        this.closedSearchBox = new GuiSprite(BACKGROUND, textureW, textureH, 257, 0, 13, 16).setPos(searchBoxX, searchBoxY)
                .setHovered(new GuiSprite(BACKGROUND, textureW, textureH, 257, 34, 13, 16).setPos(searchBoxX, searchBoxY));
        this.openedSearchBox = new GuiSprite(BACKGROUND, textureW, textureH, 285, 0, 72, 16).setPos(searchBoxX, searchBoxY);
        this.editBoxArea = new ClickableArea(49, 16).setPos(searchBoxX, searchBoxY);
        this.searchButtonArea = new ClickableArea(13, 16).setPos(editBoxArea.getEndX(), searchBoxY)
                .setHovered(new GuiSprite(BACKGROUND, textureW, textureH, 358, 0, 13, 16).setPos(editBoxArea.getEndX(), searchBoxY));
        this.closeSearchBoxArea = new ClickableArea(9, 16).setPos(searchButtonArea.getEndX(), searchBoxY)
                .setHovered(new GuiSprite(BACKGROUND, textureW, textureH, 372, 0, 10, 16).setPos(searchButtonArea.getEndX(), searchBoxY));
        addRenderableWidget(this.editBox = new EditBox(font, searchBoxX + 1, searchBoxY + 2, 48, 9, Component.empty()));
        editBox.setBordered(false);

        int filterX = leftPos + imageWidth;
        int filterY = topPos + 17;
        this.filterMaxPage = bestiary.getFilterEntries().size() / 28;
        this.filterPage = 0;
        this.renderedFilters = Iterables.limit(bestiary.getFilterEntries(), 28);
        this.closedFilter = new GuiSprite(BACKGROUND, textureW, textureH, 257, 17, 13, 16).setPos(filterX, filterY);
        closedFilter.setHovered(new GuiSprite(BACKGROUND, textureW, textureH, 257, 51, 13, 16).setPos(closedFilter.getX(), closedFilter.getY()));
        this.openedFilter = new GuiSprite(BACKGROUND, textureW, textureH, 285, 17, 72, 136).setPos(filterX, filterY);
        this.closeFilterArea = new ClickableArea(10, 13);
        closeFilterArea.setPos(openedFilter.getEndX() - closeFilterArea.getW(), openedFilter.getY());
        closeFilterArea.setHovered(new GuiSprite(BACKGROUND, textureW, textureH, 358, 17, 10, 14).setPos(closeFilterArea.getX(), closeFilterArea.getY()));

        this.closedSort = new GuiSprite(BACKGROUND, textureW, textureH, 271, 0, 13, 16);
        closedSort.setPos(leftPos - closedSort.getW(), topPos).setHovered(new GuiSprite(BACKGROUND, textureW, textureH, 271, 34, 13, 16).setPos(closedSort.getX(), closedSort.getY()));
        int sortWidth = 72;
        this.openedSortTop = new GuiSprite(BACKGROUND, textureW, textureH, 285, 202, sortWidth, 12).setPos(leftPos - sortWidth, topPos);
        this.closeSortArea = new ClickableArea(10, 12).setPos(openedSortTop.getX(), openedSortTop.getY());
        closeSortArea.setHovered(new GuiSprite(BACKGROUND, textureW, textureH, 248, 202, 10, 12).setPos(closeSortArea.getX(), closeSortArea.getY()));
        this.reverseSortArea = new ClickableArea(12, 12).setPos(closeSortArea.getEndX(), openedSortTop.getY());
        this.incSort = new GuiSprite(BACKGROUND, textureW, textureH, 272, 202, 12, 12).setPos(reverseSortArea.getX(), reverseSortArea.getY());
        incSort.setHovered(new GuiSprite(BACKGROUND, textureW, textureH, 259, 202, 12, 12).setPos(incSort.getX(), incSort.getY()));
        this.decSort = new GuiSprite(BACKGROUND, textureW, textureH, 272, 215, 12, 12).setPos(reverseSortArea.getX(), reverseSortArea.getY());
        decSort.setHovered(new GuiSprite(BACKGROUND, textureW, textureH, 259, 215, 12, 12).setPos(decSort.getX(), decSort.getY()));
        this.sortSelections = new EnumMap<>(ClientBestiary.SortType.class);
        int selectionY = openedSortTop.getEndY();
        for (ClientBestiary.SortType sortType : ClientBestiary.SortType.values()) {
            GuiSprite sprite = new GuiSprite(BACKGROUND, textureW, textureH, 285, 215, sortWidth, 13).setPos(openedSortTop.getX(), selectionY);
            sortSelections.put(sortType, sprite);
            selectionY += sprite.getH();
        }
        this.selectedSort = new GuiSprite(BACKGROUND, textureW, textureH, 285, 239, sortWidth, 13);
        this.openedSortBottom = new GuiSprite(BACKGROUND, textureW, textureH, 285, 229, sortWidth, 9).setPos(openedSortTop.getX(), selectionY);
    }

    @Override
    protected void rebuildWidgets() {}

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false; // todo 滚动条
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int x = leftPos + renderedEntryX;
        int y = topPos + renderedEntryY;
        if (mouseX >= x && mouseX < x + 141 && mouseY >= y && mouseY < y + 211) {
            int lastPage = page;
            updateRenderedEntries(page + Mth.sign(-scrollY));
            return lastPage != page;
        } else if (openedFilter.isHovered(mouseX, mouseY)) {
            int lastPage = filterPage;
            this.filterPage = Mth.clamp(filterPage + Mth.sign(-scrollY), 0, filterMaxPage);
            if (lastPage != filterPage) {
                if (filterPage == 0) {
                    this.renderedFilters = Iterables.limit(bestiary.getFilterEntries(), 28);
                } else {
                    this.renderedFilters = Iterables.limit(Iterables.skip(bestiary.getFilterEntries(), filterPage * 28), 28);
                }
                return true;
            }
        }
        // todo 描述滚动
        return false;
    }

    private void updateRenderedEntries(int setPage) {
        if (entries.size() > 24) {
            this.maxPage = entries.size() / 24;
            this.page = Mth.clamp(setPage, 0, maxPage);
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
    }

    private void resetRenderedEntries() {
        this.entries = bestiary.getSortedEntries();
        updateRenderedEntries(0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = leftPos + renderedEntryX;
        int y = topPos + renderedEntryY;
        for (ClientBestiaryEntry entry : renderedEntries) {
            if (mouseX >= x && mouseX < x + 36 && mouseY >= y && mouseY < y + 36) {
                if (entry.isLocked()) return false;
                this.showedEntry = entry;
                return true;
            }
            x += 36;
            if (x >= leftPos + renderedEntryX + 4 * 36) {
                x = leftPos + renderedEntryX;
                y += 36;
            }
        }

        if (searchBoxOpened) {
            if (editBoxArea.isHovered(mouseX, mouseY)) {
                setFocused(editBox);
                return true;
            } else if (searchButtonArea.isHovered(mouseX, mouseY)) {
                this.entries = bestiary.search(editBox.getValue().trim());
                updateRenderedEntries(0);
                return true;
            } else if (closeSearchBoxArea.isHovered(mouseX, mouseY)) {
                this.searchBoxOpened = false;
                editBox.setVisible(false);
                return true;
            }
        } else if (closedSearchBox.isHovered(mouseX, mouseY)) {
            this.searchBoxOpened = true;
            editBox.setVisible(true);
            return true;
        }
        if (editBox.isFocused()) {
            setFocused(null);
            if (editBox.getValue().trim().isEmpty()) {
                resetRenderedEntries();
            }
        }

        if (filterOpened) {
            if (closeFilterArea.isHovered(mouseX, mouseY)) {
                this.filterOpened = false;
                return true;
            } else {
                x = openedFilter.getX();
                y = openedFilter.getY() + 15;
                for (FilterEntry filter : renderedFilters) {
                    if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                        bestiary.toggleFilter(filter);
                        resetRenderedEntries();
                        return true;
                    }
                    x += 16;
                    if (x >= openedFilter.getX() + 4 * 16) {
                        x = openedFilter.getX();
                        y += 16;
                    }
                }
            }
        } else if (closedFilter.isHovered(mouseX, mouseY)) {
            this.filterOpened = true;
            return true;
        }

        if (sortOpened) {
            if (closeSortArea.isHovered(mouseX, mouseY)) {
                this.sortOpened = false;
                return true;
            } else if (reverseSortArea.isHovered(mouseX, mouseY)) {
                bestiary.setSortType(bestiary.getSortType(), !bestiary.isSortReversed());
                resetRenderedEntries();
                return true;
            } else {
                for (Map.Entry<ClientBestiary.SortType, GuiSprite> entry : sortSelections.entrySet()) {
                    if (entry.getValue().isHovered(mouseX, mouseY)) {
                        bestiary.setSortType(entry.getKey(), bestiary.isSortReversed());
                        resetRenderedEntries();
                        return true;
                    }
                }
            }
        } else if (closedSort.isHovered(mouseX, mouseY)) {
            this.sortOpened = true;
            return true;
        }

        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(BACKGROUND, textureW, textureH, 0, 0, leftPos, topPos, imageWidth, imageHeight);

        int x1 = leftPos + renderedEntryX;
        int y1 = topPos + renderedEntryY;
        for (ClientBestiaryEntry entry : renderedEntries) {
            guiGraphics.blitSprite(BACKGROUND, textureW, textureH, 220, 0, x1, y1, 36, 36);
            if (entry.isLocked()) {
                renderFilter(guiGraphics, FilterEntry.UNKNOWN, x1, y1, 36, 36);
            } else {
                LivingEntity living = entry.getRenderedEntity(getMinecraft().level);
                float size = (float) Math.max(living.getBoundingBox().getXsize(), living.getBoundingBox().getYsize());
                float factor = 2 / size;
                int scale = Mth.ceil(10 * factor);
                float yOffset = 0.3F / factor;
                InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, x1, y1, x1 + 36, y1 + 36, scale, yOffset, mouseX, mouseY, living);
            }

            x1 += 35;
            if (x1 >= leftPos + renderedEntryX + 4 * 35) {
                x1 = leftPos + renderedEntryX;
                y1 += 35;
            }
        }

        // 滚动条
        if (maxPage > 0) {
            slider.setY(topPos + Mth.lerpInt((float) page / maxPage, 6, 222 - slider.getH()));
            slider.render(guiGraphics);
        }

        // 搜索框
        if (searchBoxOpened) {
            openedSearchBox.render(guiGraphics);
            searchButtonArea.renderHovered(guiGraphics, mouseX, mouseY);
            closeSearchBoxArea.renderHovered(guiGraphics, mouseX, mouseY);
        } else {
            closedSearchBox.renderSelfAndHovered(guiGraphics, mouseX, mouseY);
        }

        // 过滤器
        if (filterOpened) {
            openedFilter.render(guiGraphics);
            closeFilterArea.renderHovered(guiGraphics, mouseX, mouseY);
            x1 = openedFilter.getX();
            y1 = openedFilter.getY() + 15;
            for (FilterEntry filter : renderedFilters) {
                boolean enabled = bestiary.isFilterEnabled(filter);
                if (!enabled) RenderSystem.setShaderColor(1, 1, 1, 0.3F);
                renderFilter(guiGraphics, filter, x1, y1, 16, 16);
                if (!enabled) RenderSystem.setShaderColor(1, 1, 1, 1);
                x1 += 16;
                if (x1 >= openedFilter.getX() + 4 * 16) {
                    x1 = openedFilter.getX();
                    y1 += 16;
                }
            }
        } else {
            closedFilter.renderSelfAndHovered(guiGraphics, mouseX, mouseY);
        }

        // 排序器
        if (sortOpened) {
            openedSortTop.render(guiGraphics);
            closeSortArea.renderHovered(guiGraphics, mouseX, mouseY);
            if (bestiary.isSortReversed()) {
                decSort.renderSelfAndHovered(guiGraphics, mouseX, mouseY);
            } else {
                incSort.renderSelfAndHovered(guiGraphics, mouseX, mouseY);
            }
            for (Map.Entry<ClientBestiary.SortType, GuiSprite> entry : sortSelections.entrySet()) {
                GuiSprite sprite = entry.getValue();
                if (entry.getKey() == bestiary.getSortType()) {
                    selectedSort.setPos(sprite.getX(), sprite.getY()).render(guiGraphics);
                } else {
                    sprite.render(guiGraphics);
                }
                Component name = entry.getKey().getTranslatedName();
                guiGraphics.drawString(font, name, sprite.getX() + 6 + (sprite.getW() - 6 - font.width(name)) / 2, sprite.getY() + (sprite.getH() - font.lineHeight) / 2, 0xFFFFFF);
            }
            openedSortBottom.render(guiGraphics);
        } else {
            closedSort.renderSelfAndHovered(guiGraphics, mouseX, mouseY);
        }

        // 正在显示条目
        if (showedEntry != null) {
            float progress = showedEntry.getUnlockedProgress();
            PoseStack pose = guiGraphics.pose();
            int x2, y2;

            x1 = leftPos + showedEntryX;
            y1 = topPos + showedEntryY;
            // 背景图
            guiGraphics.blitSprite(showedEntry.getBackground(), showedEntryWH, showedEntryWH, 0, 0, x1, y1, showedEntryWH, showedEntryWH);
            // 实体
            LivingEntity living = showedEntry.getRenderedEntity(getMinecraft().level);
            InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, x1, y1, x1 + showedEntryWH, y1 + showedEntryWH, 20, 0.3F, mouseX, mouseY, living);
            // 稀有度
            pose.pushPose();
            pose.translate(0, 0, 180);
            guiGraphics.blitSprite(BACKGROUND, textureW, textureH, 221, 53, x1, y1, 23, 7);
            for (int i = 0; i < showedEntry.getRarity(); i++) {
                if (i == 0) {
                    // 大星
                    guiGraphics.blitSprite(BACKGROUND, textureW, textureH, 222, 61, x1 + 1, y1 + 1, 5, 5);
                } else {
                    // 小星
                    guiGraphics.blitSprite(BACKGROUND, textureW, textureH, 227, 61, x1 + 2 + i * 4, y1 + 1, 4, 5);
                }
            }
            pose.popPose();
            // 名字
            if (mouseX >= x1 && mouseX < x1 + showedEntryWH && mouseY >= y1 && mouseY < y1 + showedEntryWH) {
                guiGraphics.renderTooltip(font, showedEntry.getDisplayName(), mouseX, mouseY);
            }
            // 杀死次数
            float lineHeight = font.lineHeight * textScale;
            renderScaledString(guiGraphics, pose, Integer.toString(showedEntry.killedByCount), leftPos + 212 - 1, topPos + 73, lineHeight);

            boolean p20 = progress >= 0.2F;
            x1 = leftPos + 187 - 1;
            y1 = topPos + 84 - 1;
            x2 = leftPos + 212 - 1;
            y2 = topPos + 95 - 1;
            // 生命值
            renderScaledString(guiGraphics, pose, p20 ? ATTRIBUTE_MODIFIER_FORMAT.format(showedEntry.maxHealth) : "?", x1, y1, lineHeight);
            // 击退抗性
            renderScaledString(guiGraphics, pose, p20 ? ATTRIBUTE_MODIFIER_FORMAT.format(showedEntry.knockbackResistance) : "?", x2, y1, lineHeight);
            // 攻击力
            renderScaledString(guiGraphics, pose, p20 ? ATTRIBUTE_MODIFIER_FORMAT.format(showedEntry.attackDamage) : "?", x1, y2, lineHeight);
            // 防御力
            renderScaledString(guiGraphics, pose, p20 ? ATTRIBUTE_MODIFIER_FORMAT.format(showedEntry.armor) : "?", x2, y2, lineHeight);
            // 钱币
            x1 = leftPos + 174 - 1;
            y1 = topPos + 109;
            for (Integer count : showedEntry.getCoins().platinum2Copper()) {
                renderScaledString(guiGraphics, pose, p20 ? count.toString() : "?", x1, y1, lineHeight);
                x1 += 13;
            }
            // 群系
            x1 = leftPos + 164;
            y1 = topPos + 116;
            x2 = x1 + 48;
            int t = x1;
            for (FilterEntry filter : showedEntry.getFilters()) {
                renderFilter(guiGraphics, filter, x1, y1, 16, 16);
                if (mouseX >= x1 && mouseX < x1 + 16 && mouseY >= y1 && mouseY < y1 + 16) {
                    guiGraphics.renderTooltip(font, filter.tooltip(), Optional.empty(), mouseX, mouseY);
                }
                x1 += 16;
                if (x1 >= x2) {
                    x1 = t;
                    y1 += 16;
                }
            }
            // 描述
            if (p20) {
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

    private void renderFilter(GuiGraphics guiGraphics, FilterEntry filter, int x, int y, int alignX, int alignY) {
        RenderSystem.enableBlend();
        ResourceLocation sprite = filter.sprite == null ? FILTERS : filter.sprite;
        guiGraphics.blitSprite(sprite, 256, 256, filter.u(), filter.v(), x + (alignX - filter.w()) / 2, y + (alignY - filter.h()) / 2, filter.w(), filter.h());
        RenderSystem.disableBlend();
    }

    private void renderScaledString(GuiGraphics guiGraphics, PoseStack pose, String text, int x, int y, float lineHeight) {
        pose.pushPose();
        pose.translate(x - font.width(text) * textScale, y - lineHeight, 0);
        pose.scale(textScale, textScale, 1);
        guiGraphics.drawString(font, text, 0, 0, 0xFFFFFF);
        pose.popPose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
