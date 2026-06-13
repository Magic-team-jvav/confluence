package org.confluence.mod.client.gui;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.Util;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.AchievementOffset;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.util.AchievementUtils;
import org.mesdag.portlib.client.gui.components.PortSprite;
import org.mesdag.portlib.client.gui.components.PortWidgetSprites;

import java.util.*;

public class AchievementScreen extends Screen {
    public static final PortWidgetSprites SPRITES = new PortWidgetSprites(
            new PortSprite(Confluence.asResource("achievement_icon"), 20, 20),
            new PortSprite(Confluence.asResource("achievement_icon_highlighted"), 20, 20)
    );
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/achievement.png");
    private static final int ENTRY_X = 4;
    private static final int ENTRY_Y = 22;
    private static final int ENTRY_U = 9;
    private static final int ENTRY_V = 206;
    private static final int ENTRY_W = 194;
    private static final int ENTRY_H = 45;
    private static final int ENTRY_FRAME_X = 4;
    private static final int ENTRY_FRAME_Y = 4;
    private static final int ENTRY_FRAME_U = 209;
    private static final int ENTRY_FRAME_V = 31;
    private static final int ENTRY_FRAME_W = 38;
    private static final int ENTRY_FRAME_H = 38;
    private static final int ENTRY_ICON_X = 3;
    private static final int ENTRY_ICON_Y = 3;
    private static final int CATEGORY_X = 2;
    private static final int CATEGORY_Y = 2;
    private static final int CATEGORY_DISABLED_OFFSET_U = -16;
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int LIMIT_ENTRY_SIZE = 4;
    private static final int LIMIT_DESC_LINES = 3;
    private static final EnumMap<AchievementOffset.Category, int[]> CATEGORIES_UVWH = Util.make(new EnumMap<>(AchievementOffset.Category.class), map -> {
        map.put(AchievementOffset.Category.CHALLENGER, new int[]{225, 70, 16, 16});
        map.put(AchievementOffset.Category.EXPLORER, new int[]{225, 70 + 16, 16, 16});
        map.put(AchievementOffset.Category.SLAYER, new int[]{225, 70 + 32, 16, 16});
        map.put(AchievementOffset.Category.COLLECTOR, new int[]{225, 70 + 48, 16, 16});
    });
    private static final AchievementOffset.Category[] INDEX_2_CATEGORY = {
            AchievementOffset.Category.CHALLENGER,
            AchievementOffset.Category.EXPLORER,
            AchievementOffset.Category.SLAYER,
            AchievementOffset.Category.COLLECTOR
    };
    private static final Object2IntArrayMap<AchievementOffset.Category> CATEGORY_2_INDEX = Util.make(new Object2IntArrayMap<>(4), map -> {
        map.put(AchievementOffset.Category.CHALLENGER, 0);
        map.put(AchievementOffset.Category.EXPLORER, 1);
        map.put(AchievementOffset.Category.SLAYER, 2);
        map.put(AchievementOffset.Category.COLLECTOR, 3);
    });
    private static Map<ResourceLocation, AchievementOffset> achievements;
    private static Map<ResourceLocation, Triple<ResourceLocation, Component, Component>> achievementDisplays;

    private int imageWidth;
    private int imageHeight;
    private int leftPos;
    private int topPos;

    private Iterable<Map.Entry<ResourceLocation, AchievementOffset>> rendered;
    private int lines;
    private int skip;
    private PlayerAdvancements.Data data;
    private boolean completedGoingOldSchool;
    private boolean[] categoriesDisabled;

    private int hovered = -1;
    private final int[][] descLinesAndSkips = {
            {0, 0}, {0, 0}, {0, 0}, {0, 0}
    };
    private boolean scrolling;

    public AchievementScreen() {
        super(CommonComponents.EMPTY);
    }

    @Override
    protected void init() {
        BackgroundLayer.initLayers(width, height);
        this.imageWidth = 208;
        this.imageHeight = 204;
        this.leftPos = (width - imageWidth) / 2;
        this.topPos = (height - imageHeight) / 2;
        if (achievements == null) {
            Map<ResourceLocation, AchievementOffset> map = AchievementOffsetLoader.load(getMinecraft().getResourceManager());
            Map<ResourceLocation, AchievementOffset> values = new LinkedHashMap<>(map.size());
            Map<ResourceLocation, Triple<ResourceLocation, Component, Component>> displays = new HashMap<>();
            map.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().order()))
                    .forEachOrdered(entry -> {
                        ResourceLocation key = entry.getKey();
                        values.put(key, entry.getValue());
                        String path = AchievementUtils.asPath(key);
                        String namespace = key.getNamespace();
                        displays.put(key, new ImmutableTriple<>(
                                ResourceLocation.fromNamespaceAndPath(namespace, "textures/achievement/" + path + ".png"),
                                Component.translatable("achievements." + namespace + "." + path + ".title"),
                                Component.translatable("achievements." + namespace + "." + path + ".description")
                        ));
                    });
            achievements = values;
            achievementDisplays = displays;
        }
        this.rendered = achievements.entrySet();
        this.lines = achievements.size();
        this.data = AchievementUtils.loadData(LibClientUtils.getGameProfile().getId());
        this.completedGoingOldSchool = data.map().containsKey(AchievementUtils.GOING_OLDSCHOOL);
        this.categoriesDisabled = new boolean[4];
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        BackgroundLayer.renderLayers(guiGraphics, partialTick);
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        int x = leftPos + CATEGORY_X;
        int y = topPos + CATEGORY_Y;
        AchievementOffset.Category hoveredCategory = null;
        for (int i = 0; i < 4; i++) {
            AchievementOffset.Category category = INDEX_2_CATEGORY[i];
            int[] uvwh = CATEGORIES_UVWH.get(category);
            int u = uvwh[0];
            if (categoriesDisabled[i]) {
                u += CATEGORY_DISABLED_OFFSET_U;
            }
            int w = uvwh[2];
            int h = uvwh[3];
            guiGraphics.blit(BACKGROUND, x, y, u, uvwh[1], w, h, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            if (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h) {
                hoveredCategory = category;
            }
            x += w;
        }
        if (hoveredCategory != null) {
            guiGraphics.renderTooltip(font, hoveredCategory.getTranslatedName(), mouseX, mouseY);
        }
        x = leftPos + ENTRY_X;
        y = topPos + ENTRY_Y;
        int i = 0;
        for (Map.Entry<ResourceLocation, AchievementOffset> entry : Iterables.limit(Iterables.skip(rendered, skip), LIMIT_ENTRY_SIZE)) {
            assert entry != null;
            ResourceLocation key = entry.getKey();
            guiGraphics.blit(BACKGROUND, x, y, ENTRY_U, ENTRY_V, ENTRY_W, ENTRY_H, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            AdvancementProgress progress = data.map().get(key);
            int frameX = x + ENTRY_FRAME_X;
            int frameY = y + ENTRY_FRAME_Y;
            if (progress != null && progress.isDone()) {
                guiGraphics.blit(BACKGROUND, frameX, frameY, ENTRY_FRAME_U, ENTRY_FRAME_V, ENTRY_FRAME_W, ENTRY_FRAME_H, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }
            Triple<ResourceLocation, Component, Component> triple = achievementDisplays.get(key);
            if (triple != null) {
                guiGraphics.blit(triple.getLeft(), frameX + ENTRY_ICON_X, frameY + ENTRY_ICON_Y, 0, 0, 32, 32, 32, 32);
                int[] uvwh = CATEGORIES_UVWH.get(entry.getValue().category());
                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(x + 44, y + 2, 0);
                int w = uvwh[2];
                int h = uvwh[3];
                poseStack.scale(12F / w, 12F / h, 1);
                guiGraphics.blit(BACKGROUND, 0, 0, uvwh[0], uvwh[1], w, h, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                poseStack.popPose();
                guiGraphics.drawString(font, triple.getMiddle(), x + 44 + 12, y + 4, -1, false);
                Component desc = triple.getRight();
                int descX = x + 43;
                int descY = y + 16;
                if (mouseX > descX && mouseX < descX + 144 && mouseY > descY && mouseY < descY + font.lineHeight * LIMIT_DESC_LINES) {
                    this.hovered = i;
                }
                if (font.width(desc) > 148) {
                    List<FormattedCharSequence> list = font.split(desc, 148);
                    int descLines = list.size();
                    descLinesAndSkips[i][0] = descLines;
                    int descSkip = descLinesAndSkips[i][1];
                    int end = Math.min(descSkip + LIMIT_DESC_LINES, descLines);
                    if (descSkip > 0) {
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(0, -Mth.sin(System.currentTimeMillis() % 1000 / 1000F), 0);
                        guiGraphics.blit(BACKGROUND, x + ENTRY_W - 7, y + 3, 212, 1, 5, 4, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                        guiGraphics.pose().popPose();
                    }
                    if (end < descLines && descLines > LIMIT_DESC_LINES) {
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(0, Mth.sin(System.currentTimeMillis() % 1000 / 1000F), 0);
                        guiGraphics.blit(BACKGROUND, x + ENTRY_W - 7, y + 9, 218, 1, 5, 4, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                        guiGraphics.pose().popPose();
                    }
                    for (FormattedCharSequence sequence : Iterables.limit(Iterables.skip(list, descSkip), LIMIT_DESC_LINES)) {
                        assert sequence != null;
                        guiGraphics.drawString(font, sequence, descX, descY, -1, false);
                        descY += font.lineHeight;
                    }
                } else {
                    guiGraphics.drawString(font, desc, descX, descY, -1, false);
                    descLinesAndSkips[i][0] = 1;
                }
            }
            y += ENTRY_H;
            ++i;
        }

        x = leftPos + 197;
        float ratio = (float) skip / lines;
        y = topPos + 22 + Mth.lerpInt(ratio, 0, 180 - 15);
        int u = 210;
        if (scrolling || (mouseX > x && mouseX < x + 7 && mouseY > y && mouseY < y + 15)) {
            u += 8;
        }
        guiGraphics.blit(BACKGROUND, x, y, u, 10, 7, 15, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int delta = Mth.sign(-scrollY); // 1是向下翻页，-1是向上翻页
        if (hovered != -1) {
            int[] descLinesAndSkip = descLinesAndSkips[hovered];
            this.hovered = -1;
            int max = descLinesAndSkip[0] - LIMIT_DESC_LINES;
            if (max > 0) {
                int clamp = Mth.clamp(descLinesAndSkip[1] + delta, 0, max);
                if (clamp != descLinesAndSkip[1]) {
                    descLinesAndSkip[1] = clamp;
                    return true;
                }
            }
        }
        int clamp = Mth.clamp(skip + delta, 0, lines - LIMIT_ENTRY_SIZE);
        if (clamp != skip) {
            if (delta == 1) {
                int[] temp = descLinesAndSkips[0];
                temp[0] = 0;
                temp[1] = 0;
                descLinesAndSkips[0] = descLinesAndSkips[1];
                descLinesAndSkips[1] = descLinesAndSkips[2];
                descLinesAndSkips[2] = descLinesAndSkips[3];
                descLinesAndSkips[3] = temp;
            } else {
                int[] temp = descLinesAndSkips[3];
                temp[0] = 0;
                temp[1] = 0;
                descLinesAndSkips[3] = descLinesAndSkips[2];
                descLinesAndSkips[2] = descLinesAndSkips[1];
                descLinesAndSkips[1] = descLinesAndSkips[0];
                descLinesAndSkips[0] = temp;
            }
            this.skip = clamp;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = leftPos + 197;
        float ratio = (float) skip / lines;
        int y = topPos + 22 + Mth.lerpInt(ratio, 0, 180 - 15);
        if (mouseX > x && mouseX < x + 7 && mouseY > y && mouseY < y + 15) {
            this.scrolling = true;
            for (int[] descLinesAndSkip : descLinesAndSkips) {
                descLinesAndSkip[0] = 0;
                descLinesAndSkip[1] = 0;
            }
            return true;
        }

        x = leftPos + CATEGORY_X;
        y = topPos + CATEGORY_Y;
        for (int i = 0; i < 4; i++) {
            AchievementOffset.Category category = INDEX_2_CATEGORY[i];
            int[] uvwh = CATEGORIES_UVWH.get(category);
            int w = uvwh[2];
            if (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + uvwh[3]) {
                categoriesDisabled[i] = !categoriesDisabled[i];
                List<Map.Entry<ResourceLocation, AchievementOffset>> list = new ArrayList<>(achievements.size());
                for (Map.Entry<ResourceLocation, AchievementOffset> entry : achievements.entrySet()) {
                    if (!categoriesDisabled[CATEGORY_2_INDEX.getInt(entry.getValue().category())]) {
                        list.add(entry);
                    }
                }
                this.rendered = list;
                ratio = (float) skip / lines;
                this.lines = list.size();
                this.skip = Math.max(Mth.clamp(Mth.lerpInt(ratio, 0, lines), 0, lines - 1), 0);
                return true;
            }
            x += w;
        }
        if (button == 0 && BackgroundLayer.clickedLayers(mouseX, mouseY)) {
            if (completedGoingOldSchool != BackgroundLayer.isCompletedGoingOldSchool()) {
                this.data = AchievementUtils.loadData(LibClientUtils.getGameProfile().getId());
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrolling) {
            float ratio = (float) Mth.clamp((mouseY - topPos - 22 - 15 * 0.5) / (180 - 15), 0, 1);
            this.skip = Math.min(Mth.ceil(ratio * lines), lines - 1);
            return true;
        }
        if (button == 0 && BackgroundLayer.dragLayers(mouseX, mouseY, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        if (button == 0 && BackgroundLayer.releasedLayers(mouseX, mouseY)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        super.onClose();
        BackgroundLayer.closeLayers();
    }
}
