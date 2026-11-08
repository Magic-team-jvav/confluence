package org.confluence.mod.client.gui.screen;

import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.neoforged.fml.ModList;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.AchievementScreen;
import org.confluence.mod.client.gui.character.CharacterSelectScreen;

import java.util.List;

/// 泰拉风格的主菜单（只在泰拉界面模式）：背景是成就界面同款的 BackgroundLayer（日月可以拖动），
/// 上方是模组标志（轻轻上下浮动；像素图不旋转不缩放，免得闪烁），中间是一列大号文字菜单，悬停变黄并“嗒”一声，像泰拉。
/// 所有画面都是代码画的，不使用泰拉素材；标志用模组自带的 title.png。底部左边版本号，右边 Mojang 版权（点击看制作人员）。
public class TerraTitleScreen extends Screen {
    private static final ResourceLocation LOGO = Confluence.asResource("textures/gui/mod_icon/title.png");
    private static final int LOGO_WIDTH = 300;
    private static final int LOGO_HEIGHT = 69;
    /// 底部一行文字的高度
    private static final int BOTTOM_BAR = 16;
    private static final int LOGO_TOP = 12;
    /// 点了“经典菜单”：本次游戏里都用原版主菜单（重启游戏后恢复泰拉风格）
    private static boolean vanillaThisSession;
    private final long openedAt = Util.getMillis();
    private final List<Item> items = new java.util.ArrayList<>();
    private int hoveredItem = -1;

    private record Item(Component text, Runnable action, boolean active, float[] grow) {}

    public TerraTitleScreen() {
        super(Component.translatable("narrator.screen.title"));
    }

    public static boolean useVanilla() {
        return vanillaThisSession;
    }

    /// 原版主菜单右上角的“泰拉菜单”按钮：回到泰拉风格
    public static void returnFromVanilla(net.minecraft.client.Minecraft minecraft) {
        vanillaThisSession = false;
        minecraft.setScreen(new TerraTitleScreen());
    }

    @Override
    protected void init() {
        items.clear();
        add("singleplayer", () -> minecraft.setScreen(new CharacterSelectScreen(this, () -> minecraft.setScreen(new SelectWorldScreen(this)))), true);
        add("multiplayer", () -> minecraft.setScreen(new CharacterSelectScreen(this, () -> minecraft.setScreen(
                minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this)))), minecraft.allowsMultiplayer());
        add("achievements", () -> minecraft.pushGuiLayer(new AchievementScreen()), true);
        add("mods", () -> minecraft.setScreen(new net.neoforged.neoforge.client.gui.ModListScreen(this)), true);
        add("options", () -> minecraft.setScreen(new OptionsScreen(this, minecraft.options)), true);
        add("quit", () -> minecraft.stop(), true);
    }

    private void add(String key, Runnable action, boolean active) {
        items.add(new Item(Component.translatable("gui.confluence.title." + key), action, active, new float[1]));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // ---------- 布局（宽屏、矮屏都不重叠） ----------
    /// 放得下就用 2 倍大字（行距 24，挤一点 20），再放不下用 1 倍（都是整数倍，文字不抖）
    private boolean fits(int line) {
        return logoBottom() + 8 + items.size() * line <= height - BOTTOM_BAR;
    }

    private boolean largeText() {
        return fits(20);
    }

    private float textScale() {
        return largeText() ? 2.0F : 1.0F;
    }

    private int line() {
        return fits(24) ? 24 : fits(20) ? 20 : 13;
    }

    /// 菜单在标志和底部文字之间居中
    private int menuTop() {
        int free = height - BOTTOM_BAR - logoBottom();
        return logoBottom() + Math.max(4, (free - items.size() * line()) / 2);
    }

    private int logoBottom() {
        return LOGO_TOP + logoHeight();
    }

    /// 标志不超过屏幕宽度，也不超过高度的 26%（超宽屏/矮屏时缩小）
    private float logoScale() {
        return Math.min(1.0F, Math.min((width - 40) / (float) LOGO_WIDTH, height * 0.26F / LOGO_HEIGHT));
    }

    private int logoHeight() {
        return Math.round(LOGO_HEIGHT * logoScale());
    }

    private int itemAt(double mouseX, double mouseY) {
        for (int i = 0; i < items.size(); i++) {
            float textWidth = font.width(items.get(i).text) * textScale();
            int y = menuTop() + i * line();
            if (mouseY >= y - 2 && mouseY < y + 9 * textScale() + 2 && Math.abs(mouseX - width / 2.0) <= textWidth / 2 + 4) return i;
        }
        return -1;
    }

    private Component classic() {
        return Component.translatable("gui.confluence.title.classic");
    }

    private Component copyright() {
        return Component.translatable("title.credits");
    }

    // ---------- 绘制 ----------
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        TerraMenuBackground.render(guiGraphics, partialTick);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        long now = Util.getMillis();
        // 标志：只轻轻上下浮动（小数平移，平滑）
        float time = now / 1000.0F;
        float scale = logoScale();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(width / 2.0F, LOGO_TOP + logoHeight() / 2.0F + 2.5F * Mth.sin(time * 1.1F), 0);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        guiGraphics.blit(LOGO, -LOGO_WIDTH / 2, -LOGO_HEIGHT / 2, 0, 0, LOGO_WIDTH, LOGO_HEIGHT, LOGO_WIDTH, LOGO_HEIGHT);
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
        guiGraphics.pose().popPose();
        // 菜单
        int hovered = itemAt(mouseX, mouseY);
        if (hovered != hoveredItem) {
            if (hovered >= 0 && items.get(hovered).active) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.8F, 0.25F));
            }
            hoveredItem = hovered;
        }
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            float target = i == hovered && item.active ? 1.0F : 0.0F;
            item.grow[0] += (target - item.grow[0]) * 0.3F; // 颜色渐变（不缩放）
            float itemScale = textScale();
            int color = !item.active ? 0x808080 : lerpColor(0xE6E6E6, 0xFFE34A, item.grow[0]);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(width / 2.0F, menuTop() + i * line() + 9 * textScale() / 2, 0);
            guiGraphics.pose().scale(itemScale, itemScale, 1.0F);
            guiGraphics.drawString(font, item.text, -font.width(item.text) / 2, -4, color, true);
            guiGraphics.pose().popPose();
        }
        // 底部
        String version = "Minecraft " + SharedConstants.getCurrentVersion().getName() + " · Confluence "
                + ModList.get().getModContainerById(Confluence.MODID).map(c -> c.getModInfo().getVersion().toString()).orElse("?");
        guiGraphics.drawString(font, version, 4, height - 12, 0xD0D0D0, true);
        Component copyright = copyright();
        int copyrightX = width - font.width(copyright) - 4;
        boolean copyrightHovered = mouseX >= copyrightX && mouseY >= height - 12;
        guiGraphics.drawString(font, copyright, copyrightX, height - 12, copyrightHovered ? 0xFFE34A : 0xD0D0D0, true);
        // “经典菜单”在右上角（不和底部文字、菜单挤在一起）
        Component classic = classic();
        int classicX = width - font.width(classic) - 4;
        boolean classicHovered = mouseX >= classicX && mouseY < 14;
        guiGraphics.drawString(font, classic, classicX, 4, classicHovered ? 0xFFE34A : 0xB0B0B0, true);
        // 打开时淡入
        float fade = Mth.clamp(1.0F - (now - openedAt) / 900.0F, 0.0F, 1.0F);
        if (fade > 0) guiGraphics.fill(0, 0, width, height, ((int) (fade * 255) << 24));
    }

    private static int lerpColor(int from, int to, float t) {
        int r = Math.round(Mth.lerp(t, (from >> 16) & 255, (to >> 16) & 255));
        int g = Math.round(Mth.lerp(t, (from >> 8) & 255, (to >> 8) & 255));
        int b = Math.round(Mth.lerp(t, from & 255, to & 255));
        return r << 16 | g << 8 | b;
    }

    // ---------- 输入 ----------
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int index = itemAt(mouseX, mouseY);
            if (index >= 0 && items.get(index).active) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                items.get(index).action.run();
                return true;
            }
            if (mouseY >= height - 12 && mouseX >= width - font.width(copyright()) - 4) {
                minecraft.setScreen(new CreditsAndAttributionScreen(this));
                return true;
            }
            if (mouseY < 14 && mouseX >= width - font.width(classic()) - 4) {
                vanillaThisSession = true;
                minecraft.setScreen(new TitleScreen());
                return true;
            }
            // 太阳/月亮可以拖（与成就界面相同，拖动解锁“老派玩家”成就）
            if (TerraMenuBackground.clicked(mouseX, mouseY)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && TerraMenuBackground.dragged(mouseX, mouseY, dragX, dragY)) return true;
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && TerraMenuBackground.released(mouseX, mouseY)) return true;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
