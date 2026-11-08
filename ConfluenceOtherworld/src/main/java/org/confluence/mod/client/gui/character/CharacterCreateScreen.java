package org.confluence.mod.client.gui.character;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import org.confluence.mod.client.character.CharacterAppearance;
import org.confluence.mod.client.character.CharacterStore;
import org.confluence.mod.client.character.CharacterStore.TerraCharacter;
import org.confluence.mod.common.character.TerraCharacterRules;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/// 泰拉式“创建角色”：左边是可拖动旋转的小人，右边是名字和三个页签——外观（体型、肤色、发型、发色、瞳色、启动器皮肤）、
/// 服装（上衣、内衬、裤子、鞋的颜色）、难度（软核 / 中核 / 硬核）。底部：随机、创建、取消。
public class CharacterCreateScreen extends net.minecraft.client.gui.screens.Screen {
    private static final int PANEL_WIDTH = 230;
    private static final int ROW = 17;
    private static final int SWATCH = 10;
    private static final int LABEL_WIDTH = 64;
    private static final int OPTION_HEIGHT = 26;
    private static final String[] TABS = {"look", "clothes", "difficulty"};
    private final CharacterSelectScreen parent;
    private final RandomSource random = RandomSource.create();
    private CharacterAppearance appearance = CharacterAppearance.random(random);
    private byte difficulty = TerraCharacterRules.SOFTCORE;
    private int tab;
    private EditBox nameBox;
    private Button createButton;
    private float yaw = -25.0F;
    private boolean dragging;

    public CharacterCreateScreen(CharacterSelectScreen parent) {
        super(Component.translatable("gui.confluence.character.create.title"));
        this.parent = parent;
    }

    // ---------- 布局 ----------
    private int previewX() {
        return width / 2 - 165;
    }

    private int panelLeft() {
        return width / 2 - 90;
    }

    private int nameY() {
        return 44;
    }

    private int tabsY() {
        return nameY() + 28;
    }

    private int contentY() {
        return tabsY() + 16;
    }

    @Override
    protected void init() {
        String previous = nameBox == null ? "" : nameBox.getValue();
        this.nameBox = new EditBox(font, panelLeft(), nameY(), PANEL_WIDTH, 18, Component.translatable("gui.confluence.character.name"));
        nameBox.setMaxLength(TerraCharacterRules.MAX_NAME_LENGTH);
        nameBox.setValue(previous);
        nameBox.setResponder(text -> updateButtons());
        addRenderableWidget(nameBox);
        setInitialFocus(nameBox);
        int buttonWidth = 90;
        int y = height - 28;
        int left = width / 2 - (buttonWidth * 3 + 8) / 2;
        addRenderableWidget(Button.builder(Component.translatable("gui.confluence.character.random"), button -> {
            boolean launcher = appearance.launcherSkin;
            appearance = CharacterAppearance.random(random);
            appearance.launcherSkin = launcher;
        }).bounds(left, y, buttonWidth, 20).build());
        this.createButton = addRenderableWidget(Button.builder(Component.translatable("gui.confluence.character.create"), button -> create())
                .bounds(left + buttonWidth + 4, y, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose()).bounds(left + (buttonWidth + 4) * 2, y, buttonWidth, 20).build());
        updateButtons();
    }

    private void updateButtons() {
        if (createButton != null) createButton.active = !TerraCharacterRules.sanitize(nameBox.getValue()).isEmpty();
    }

    private void create() {
        String name = TerraCharacterRules.sanitize(nameBox.getValue());
        if (name.isEmpty()) return;
        TerraCharacter character = new TerraCharacter();
        character.name = name;
        character.difficulty = difficulty;
        character.appearance = appearance.copy();
        CharacterStore.add(character);
        parent.onCreated(character);
        minecraft.setScreen(parent);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if ((keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER) && createButton.active) {
            create();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // ---------- 页签内容（绘制和点击共用一套描述） ----------
    private interface Row {
        void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY);

        boolean click(int x, int y, double mouseX, double mouseY);
    }

    private Row[] rows() {
        return switch (tab) {
            case 0 -> new Row[]{
                    toggle("body", () -> appearance.slim ? 1 : 0, value -> appearance.slim = value == 1, "body.wide", "body.slim"),
                    palette("skin", CharacterAppearance.SKIN_COLORS, () -> appearance.skin, value -> appearance.skin = value),
                    hairStyle(),
                    palette("hair", CharacterAppearance.HAIR_COLORS, () -> appearance.hair, value -> appearance.hair = value),
                    palette("eyes", CharacterAppearance.EYE_COLORS, () -> appearance.eyes, value -> appearance.eyes = value),
                    toggle("launcher_skin", () -> appearance.launcherSkin ? 1 : 0, value -> appearance.launcherSkin = value == 1, "no", "yes")
            };
            case 1 -> new Row[]{
                    palette("shirt", CharacterAppearance.CLOTH_COLORS, () -> appearance.shirt, value -> appearance.shirt = value),
                    palette("undershirt", CharacterAppearance.CLOTH_COLORS, () -> appearance.undershirt, value -> appearance.undershirt = value),
                    palette("pants", CharacterAppearance.CLOTH_COLORS, () -> appearance.pants, value -> appearance.pants = value),
                    palette("shoes", CharacterAppearance.CLOTH_COLORS, () -> appearance.shoes, value -> appearance.shoes = value)
            };
            default -> new Row[0];
        };
    }

    private static Component label(String key) {
        return Component.translatable("gui.confluence.character." + key);
    }

    /// 一行色块
    private Row palette(String key, int[] colors, IntSupplier getter, IntConsumer setter) {
        return new Row() {
            @Override
            public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
                guiGraphics.drawString(font, label(key), x, y + 1, 0xE0E0E0, true);
                int current = getter.getAsInt();
                for (int i = 0; i < colors.length; i++) {
                    int sx = x + LABEL_WIDTH + i * (SWATCH + 1);
                    guiGraphics.fill(sx, y, sx + SWATCH, y + SWATCH, 0xFF000000 | colors[i]);
                    boolean hovered = mouseX >= sx && mouseX < sx + SWATCH && mouseY >= y && mouseY < y + SWATCH;
                    if (colors[i] == current) guiGraphics.renderOutline(sx - 1, y - 1, SWATCH + 2, SWATCH + 2, 0xFFFFFFFF);
                    else if (hovered) guiGraphics.renderOutline(sx - 1, y - 1, SWATCH + 2, SWATCH + 2, 0xFFF0C030);
                }
            }

            @Override
            public boolean click(int x, int y, double mouseX, double mouseY) {
                for (int i = 0; i < colors.length; i++) {
                    int sx = x + LABEL_WIDTH + i * (SWATCH + 1);
                    if (mouseX >= sx && mouseX < sx + SWATCH && mouseY >= y && mouseY < y + SWATCH) {
                        setter.accept(colors[i]);
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /// 两个文字选项（选中白色，否则灰色，悬停黄色）
    private Row toggle(String key, IntSupplier getter, IntConsumer setter, String first, String second) {
        return new Row() {
            private int optionX(int x, int index) {
                return x + LABEL_WIDTH + (index == 0 ? 0 : font.width(label(first)) + 12);
            }

            @Override
            public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
                guiGraphics.drawString(font, label(key), x, y + 1, 0xE0E0E0, true);
                for (int i = 0; i < 2; i++) {
                    Component text = label(i == 0 ? first : second);
                    int ox = optionX(x, i);
                    boolean hovered = mouseX >= ox && mouseX < ox + font.width(text) && mouseY >= y && mouseY < y + 10;
                    int color = getter.getAsInt() == i ? 0xFFFFFF : hovered ? 0xF0C030 : 0x808080;
                    guiGraphics.drawString(font, text, ox, y + 1, color, true);
                }
            }

            @Override
            public boolean click(int x, int y, double mouseX, double mouseY) {
                for (int i = 0; i < 2; i++) {
                    int ox = optionX(x, i);
                    if (mouseX >= ox && mouseX < ox + font.width(label(i == 0 ? first : second)) && mouseY >= y && mouseY < y + 10) {
                        setter.accept(i);
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /// 发型：‹ 名称 ›
    private Row hairStyle() {
        return new Row() {
            private static final int NAME_WIDTH = 90;

            @Override
            public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
                guiGraphics.drawString(font, label("hair_style"), x, y + 1, 0xE0E0E0, true);
                int ax = x + LABEL_WIDTH;
                boolean leftHovered = mouseX >= ax && mouseX < ax + 8 && mouseY >= y && mouseY < y + 10;
                boolean rightHovered = mouseX >= ax + NAME_WIDTH + 10 && mouseX < ax + NAME_WIDTH + 18 && mouseY >= y && mouseY < y + 10;
                guiGraphics.drawString(font, "<", ax, y + 1, leftHovered ? 0xF0C030 : 0xFFFFFF, true);
                Component name = label("hair_style." + CharacterAppearance.HAIR_STYLES[appearance.hairStyle]);
                guiGraphics.drawString(font, name, ax + 9 + (NAME_WIDTH - font.width(name)) / 2, y + 1, 0xFFFFFF, true);
                guiGraphics.drawString(font, ">", ax + NAME_WIDTH + 10, y + 1, rightHovered ? 0xF0C030 : 0xFFFFFF, true);
            }

            @Override
            public boolean click(int x, int y, double mouseX, double mouseY) {
                int ax = x + LABEL_WIDTH;
                if (mouseY < y || mouseY >= y + 10) return false;
                int count = CharacterAppearance.HAIR_STYLES.length;
                if (mouseX >= ax && mouseX < ax + 8) {
                    appearance.hairStyle = Math.floorMod(appearance.hairStyle - 1, count);
                    return true;
                }
                if (mouseX >= ax + NAME_WIDTH + 10 && mouseX < ax + NAME_WIDTH + 18) {
                    appearance.hairStyle = Math.floorMod(appearance.hairStyle + 1, count);
                    return true;
                }
                return false;
            }
        };
    }

    private int tabX(int index) {
        int x = panelLeft();
        for (int i = 0; i < index; i++) x += font.width(label("tab." + TABS[i])) + 14;
        return x;
    }

    // ---------- 绘制 ----------
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(font, title, width / 2, 14, 0xFFFFFF);
        // 小人
        int px = previewX();
        guiGraphics.fill(px - 45, nameY() - 4, px + 45, height - 40, 0x60000000);
        CharacterPreview.render(guiGraphics, appearance, px, height - 54, 50.0F, yaw);
        guiGraphics.drawCenteredString(font, label("preview.hint"), px, height - 50, 0x909090);
        // 名字
        guiGraphics.drawString(font, label("name"), panelLeft(), nameY() - 11, 0xE0E0E0, true);
        // 页签
        for (int i = 0; i < TABS.length; i++) {
            Component text = label("tab." + TABS[i]);
            int x = tabX(i);
            boolean hovered = mouseX >= x && mouseX < x + font.width(text) && mouseY >= tabsY() && mouseY < tabsY() + 10;
            guiGraphics.drawString(font, text, x, tabsY(), i == tab ? 0xFFFFFF : hovered ? 0xF0C030 : 0x909090, true);
            if (i == tab) guiGraphics.fill(x, tabsY() + 10, x + font.width(text), tabsY() + 11, 0xFFFFFFFF);
        }
        if (tab == 2) {
            renderDifficulty(guiGraphics, mouseX, mouseY);
            return;
        }
        Row[] rows = rows();
        for (int i = 0; i < rows.length; i++) rows[i].render(guiGraphics, panelLeft(), contentY() + i * ROW, mouseX, mouseY);
    }

    private int optionAt(double mouseX, double mouseY) {
        if (mouseX < panelLeft() || mouseX >= panelLeft() + PANEL_WIDTH || mouseY < contentY()) return -1;
        int index = (int) (mouseY - contentY()) / OPTION_HEIGHT;
        return index <= TerraCharacterRules.HARDCORE ? index : -1;
    }

    private void renderDifficulty(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int hovered = optionAt(mouseX, mouseY);
        int left = panelLeft();
        for (byte i = 0; i <= TerraCharacterRules.HARDCORE; i++) {
            int y = contentY() + i * OPTION_HEIGHT;
            boolean chosen = i == difficulty;
            guiGraphics.fill(left, y, left + PANEL_WIDTH, y + OPTION_HEIGHT - 2, chosen ? 0x70FFFFFF : i == hovered ? 0x40FFFFFF : 0x60000000);
            if (chosen) guiGraphics.renderOutline(left, y, PANEL_WIDTH, OPTION_HEIGHT - 2, 0xFFFFFFFF);
            guiGraphics.drawString(font, CharacterSelectScreen.difficultyName(i), left + 6, y + 3, CharacterSelectScreen.difficultyColor(i), true);
            guiGraphics.drawString(font, Component.translatable("gui.confluence.character.difficulty." + i + ".desc"), left + 6, y + 13, 0xC0C0C0, true);
        }
    }

    // ---------- 输入 ----------
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < TABS.length; i++) {
                int x = tabX(i);
                if (mouseX >= x && mouseX < x + font.width(label("tab." + TABS[i])) && mouseY >= tabsY() && mouseY < tabsY() + 10) {
                    tab = i;
                    click();
                    return true;
                }
            }
            if (tab == 2) {
                int option = optionAt(mouseX, mouseY);
                if (option >= 0) {
                    difficulty = (byte) option;
                    click();
                    return true;
                }
            } else {
                Row[] rows = rows();
                for (int i = 0; i < rows.length; i++) {
                    if (rows[i].click(panelLeft(), contentY() + i * ROW, mouseX, mouseY)) {
                        click();
                        return true;
                    }
                }
            }
            int px = previewX();
            if (mouseX >= px - 45 && mouseX < px + 45 && mouseY >= nameY() && mouseY < height - 40) {
                dragging = true;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging) {
            yaw += (float) dragX * 2.0F;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void click() {
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
