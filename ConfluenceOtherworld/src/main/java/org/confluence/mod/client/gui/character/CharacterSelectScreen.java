package org.confluence.mod.client.gui.character;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.confluence.mod.client.character.CharacterStore;
import org.confluence.mod.client.character.CharacterStore.TerraCharacter;

import java.util.List;

/// 泰拉式“选择角色”：主菜单的“单人游戏/多人游戏”先到这里，选好角色再进入原版的世界/服务器列表。
/// 角色 = 名字 + 难度（外观下一步加）；背包仍按世界保存。
public class CharacterSelectScreen extends Screen {
    private static final int LIST_WIDTH = 260;
    private static final int ROW_HEIGHT = 24;
    private final Screen parent;
    private final Runnable onPlay;
    private TerraCharacter selected;
    private int scroll;
    private long lastClick;
    private Button deleteButton;
    private Button playButton;

    public CharacterSelectScreen(Screen parent, Runnable onPlay) {
        super(Component.translatable("gui.confluence.character.select.title"));
        this.parent = parent;
        this.onPlay = onPlay;
        this.selected = CharacterStore.selected();
    }

    /// 难度颜色（泰拉：软核白、中核蓝、硬核红）
    public static int difficultyColor(byte difficulty) {
        return switch (difficulty) {
            case 1 -> 0x8FB3FF;
            case 2 -> 0xFF6060;
            default -> 0xFFFFFF;
        };
    }

    public static Component difficultyName(byte difficulty) {
        return Component.translatable("gui.confluence.character.difficulty." + Mth.clamp(difficulty, 0, 2));
    }

    @Override
    protected void init() {
        int buttonWidth = 74;
        int gap = 4;
        int left = (width - (buttonWidth * 4 + gap * 3)) / 2;
        int y = height - 28;
        addRenderableWidget(Button.builder(Component.translatable("gui.confluence.character.new"),
                button -> minecraft.setScreen(new CharacterCreateScreen(this))).bounds(left, y, buttonWidth, 20).build());
        this.deleteButton = addRenderableWidget(Button.builder(Component.translatable("gui.confluence.character.delete"),
                button -> confirmDelete()).bounds(left + (buttonWidth + gap), y, buttonWidth, 20).build());
        this.playButton = addRenderableWidget(Button.builder(Component.translatable("gui.confluence.character.play"),
                button -> play()).bounds(left + (buttonWidth + gap) * 2, y, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, button -> onClose()).bounds(left + (buttonWidth + gap) * 3, y, buttonWidth, 20).build());
        updateButtons();
    }

    /// 新建后回到这里并选中新角色
    void onCreated(TerraCharacter character) {
        this.selected = character;
        this.scroll = 0;
    }

    private void updateButtons() {
        if (selected != null && !CharacterStore.characters().contains(selected)) selected = null;
        deleteButton.active = selected != null;
        playButton.active = selected != null && !selected.dead;
    }

    private void play() {
        if (selected == null || selected.dead) return;
        CharacterStore.select(selected);
        onPlay.run();
    }

    private void confirmDelete() {
        if (selected == null) return;
        TerraCharacter target = selected;
        minecraft.setScreen(new ConfirmScreen(confirmed -> {
            if (confirmed) {
                CharacterStore.remove(target);
                selected = null;
            }
            minecraft.setScreen(this);
        }, Component.translatable("gui.confluence.character.delete.confirm", target.name),
                Component.translatable("gui.confluence.character.delete.warning"),
                Component.translatable("gui.confluence.character.delete"), CommonComponents.GUI_CANCEL));
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    // ---------- 列表 ----------
    private int listTop() {
        return 36;
    }

    private int listBottom() {
        return height - 36;
    }

    /// 屏幕够宽时列表靠左，右边画选中角色的小人
    private boolean showPreview() {
        return width >= 520;
    }

    private int listLeft() {
        return showPreview() ? width / 2 - 200 : (width - LIST_WIDTH) / 2;
    }

    private int visibleRows() {
        return Math.max(1, (listBottom() - listTop()) / ROW_HEIGHT);
    }

    private int maxScroll() {
        return Math.max(0, CharacterStore.characters().size() - visibleRows());
    }

    private int rowAt(double mouseX, double mouseY) {
        if (mouseX < listLeft() || mouseX >= listLeft() + LIST_WIDTH || mouseY < listTop()) return -1;
        int row = (int) (mouseY - listTop()) / ROW_HEIGHT;
        if (row >= visibleRows()) return -1;
        int index = row + scroll;
        return index < CharacterStore.characters().size() ? index : -1;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateButtons();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(font, title, width / 2, 14, 0xFFFFFF);
        List<TerraCharacter> characters = CharacterStore.characters();
        int left = listLeft();
        guiGraphics.fill(left - 2, listTop() - 2, left + LIST_WIDTH + 2, listBottom(), 0x80000000);
        if (showPreview() && selected != null) {
            int previewX = width / 2 + 140;
            guiGraphics.fill(previewX - 50, listTop() - 2, previewX + 50, listBottom(), 0x60000000);
            CharacterPreview.render(guiGraphics, selected.appearance, previewX, listBottom() - 12, 48.0F, -25.0F);
        }
        if (characters.isEmpty()) {
            guiGraphics.drawCenteredString(font, Component.translatable("gui.confluence.character.empty"), width / 2, listTop() + 12, 0xA0A0A0);
            return;
        }
        int hovered = rowAt(mouseX, mouseY);
        for (int row = 0; row < visibleRows() && row + scroll < characters.size(); row++) {
            TerraCharacter character = characters.get(row + scroll);
            int y = listTop() + row * ROW_HEIGHT;
            if (character == selected) {
                guiGraphics.fill(left, y, left + LIST_WIDTH, y + ROW_HEIGHT - 2, 0x60FFFFFF);
                guiGraphics.renderOutline(left, y, LIST_WIDTH, ROW_HEIGHT - 2, 0xFFFFFFFF);
            } else if (row + scroll == hovered) {
                guiGraphics.fill(left, y, left + LIST_WIDTH, y + ROW_HEIGHT - 2, 0x30FFFFFF);
            }
            int nameColor = character.dead ? 0x808080 : 0xFFFFFF;
            guiGraphics.drawString(font, character.name, left + 6, y + 3, nameColor, true);
            Component difficulty = difficultyName(character.difficulty);
            if (character.dead) {
                difficulty = Component.empty().append(difficulty).append(" — ").append(Component.translatable("gui.confluence.character.dead").withStyle(ChatFormatting.DARK_RED));
            }
            guiGraphics.drawString(font, difficulty, left + 6, y + 12, character.dead ? 0x808080 : difficultyColor(character.difficulty), true);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int index = rowAt(mouseX, mouseY);
        if (index >= 0 && button == 0) {
            TerraCharacter clicked = CharacterStore.characters().get(index);
            long now = System.currentTimeMillis();
            boolean doubleClick = clicked == selected && now - lastClick < 300;
            this.selected = clicked;
            this.lastClick = now;
            updateButtons();
            if (doubleClick) play(); // 双击直接开始
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll = Mth.clamp(scroll - (int) Math.signum(scrollY), 0, maxScroll());
        return true;
    }
}
