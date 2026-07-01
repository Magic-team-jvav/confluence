package org.confluence.mod.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.dialog.NPCDialogLoader;
import org.jetbrains.annotations.NotNull;

/**
 * 渔夫对话界面 —— 任务按钮 + 对话按钮，无交易。
 * 三个状态: COMPLETED(今日已完成) / NO_QUEST(无可用任务) / SHOW_HINT(展示任务鱼提示)
 * CAN_SUBMIT 状态不经过此界面，AnglerNPC 直接处理。
 */
public class AnglerDialogScreen extends NPCDialogScreen {
    public enum State { COMPLETED, NO_QUEST, SHOW_HINT }

    private final State state;
    private final ItemStack questFish;
    private int dialogIndex;

    public AnglerDialogScreen(int entityId, State state, ItemStack questFish) {
        super(entityId);
        this.state = state;
        this.questFish = questFish;
    }

    @Override
    protected void init() {
        // 不调 super.init() —— 渔夫没有交易按钮
        Entity entity = minecraft.level.getEntity(entityId);
        if (entity instanceof BaseNPC npc) {
            initDialog(npc);
        }

        // 任务按钮
        addRenderableWidget(Button.builder(Component.translatable("gui.confluence.quest"), b -> {
            showQuestText();
        }).width(60).pos(width / 2 - 30, height / 2 + 20).build());

        // 对话按钮
        addRenderableWidget(Button.builder(Component.translatable("gui.confluence.dialog"), b -> {
            if (entity instanceof BaseNPC npc) {
                String key = NPCDialogLoader.getInstance().getRandomDialogKey(npc.getRandom(), npc.getType());
                if (key != null) dialogText = Component.translatable(key);
            }
        }).width(60).pos(width / 2 - 30, height / 2 + 50).build());
    }

    private void initDialog(BaseNPC npc) {
        switch (state) {
            case COMPLETED -> {
                String key = NPCDialogLoader.getInstance().getRandomDialogKey(npc.getRandom(), npc.getType());
                dialogText = key != null ? Component.translatable(key) : Component.translatable("dialogs.confluence.angler.completed");
            }
            case NO_QUEST -> {
                String key = NPCDialogLoader.getInstance().getRandomDialogKey(npc.getRandom(), npc.getType());
                dialogText = key != null ? Component.translatable(key) : Component.translatable("dialogs.confluence.angler.no_quest");
            }
            case SHOW_HINT -> {
                String key = "dialogs.confluence.angler.item." + questFish.getDescriptionId();
                dialogText = Component.translatable(key);
            }
        }
    }

    private void showQuestText() {
        switch (state) {
            case COMPLETED -> dialogText = Component.translatable("dialogs.confluence.angler.completed");
            case NO_QUEST -> dialogText = Component.translatable("dialogs.confluence.angler.no_quest");
            case SHOW_HINT -> dialogText = Component.translatable("dialogs.confluence.angler.quest_fish", questFish.getHoverName());
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (state == State.SHOW_HINT && !questFish.isEmpty()) {
            guiGraphics.renderFakeItem(questFish, width / 2 - 8, height / 2 - 50);
        }
    }

    public static void open(int entityId, State state, ItemStack questFish) {
        Minecraft.getInstance().setScreen(new AnglerDialogScreen(entityId, state, questFish));
    }
}
