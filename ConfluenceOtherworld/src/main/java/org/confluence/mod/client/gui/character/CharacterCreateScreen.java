package org.confluence.mod.client.gui.character;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.confluence.mod.client.character.CharacterStore;
import org.confluence.mod.client.character.CharacterStore.TerraCharacter;
import org.confluence.mod.common.character.TerraCharacterRules;

/// 泰拉式“创建角色”：名字 + 难度（软核 / 中核 / 硬核）。外观在下一步加（由部件拼皮肤）。
public class CharacterCreateScreen extends net.minecraft.client.gui.screens.Screen {
    private static final int PANEL_WIDTH = 260;
    private static final int OPTION_HEIGHT = 26;
    private final CharacterSelectScreen parent;
    private EditBox nameBox;
    private Button createButton;
    private byte difficulty = TerraCharacterRules.SOFTCORE;

    public CharacterCreateScreen(CharacterSelectScreen parent) {
        super(Component.translatable("gui.confluence.character.create.title"));
        this.parent = parent;
    }

    private int left() {
        return (width - PANEL_WIDTH) / 2;
    }

    private int nameY() {
        return 50;
    }

    private int optionsY() {
        return nameY() + 38;
    }

    @Override
    protected void init() {
        String previous = nameBox == null ? "" : nameBox.getValue();
        this.nameBox = new EditBox(font, left(), nameY(), PANEL_WIDTH, 20, Component.translatable("gui.confluence.character.name"));
        nameBox.setMaxLength(TerraCharacterRules.MAX_NAME_LENGTH);
        nameBox.setValue(previous);
        nameBox.setResponder(text -> updateButtons());
        addRenderableWidget(nameBox);
        setInitialFocus(nameBox);
        int buttonWidth = 120;
        int y = height - 28;
        this.createButton = addRenderableWidget(Button.builder(Component.translatable("gui.confluence.character.create"), button -> create())
                .bounds(width / 2 - buttonWidth - 2, y, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose()).bounds(width / 2 + 2, y, buttonWidth, 20).build());
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

    private int optionAt(double mouseX, double mouseY) {
        if (mouseX < left() || mouseX >= left() + PANEL_WIDTH || mouseY < optionsY()) return -1;
        int index = (int) (mouseY - optionsY()) / OPTION_HEIGHT;
        return index <= TerraCharacterRules.HARDCORE ? index : -1;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(font, title, width / 2, 14, 0xFFFFFF);
        guiGraphics.drawString(font, Component.translatable("gui.confluence.character.name"), left(), nameY() - 11, 0xE0E0E0, true);
        guiGraphics.drawString(font, Component.translatable("gui.confluence.character.difficulty"), left(), optionsY() - 11, 0xE0E0E0, true);
        int hovered = optionAt(mouseX, mouseY);
        for (byte i = 0; i <= TerraCharacterRules.HARDCORE; i++) {
            int y = optionsY() + i * OPTION_HEIGHT;
            boolean chosen = i == difficulty;
            guiGraphics.fill(left(), y, left() + PANEL_WIDTH, y + OPTION_HEIGHT - 2, chosen ? 0x70FFFFFF : i == hovered ? 0x40FFFFFF : 0x60000000);
            if (chosen) guiGraphics.renderOutline(left(), y, PANEL_WIDTH, OPTION_HEIGHT - 2, 0xFFFFFFFF);
            guiGraphics.drawString(font, CharacterSelectScreen.difficultyName(i), left() + 6, y + 3, CharacterSelectScreen.difficultyColor(i), true);
            guiGraphics.drawString(font, Component.translatable("gui.confluence.character.difficulty." + i + ".desc"), left() + 6, y + 13, 0xC0C0C0, true);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int option = optionAt(mouseX, mouseY);
        if (option >= 0 && button == 0) {
            this.difficulty = (byte) option;
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
