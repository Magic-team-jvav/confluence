package org.confluence.mod.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.dialog.NPCDialogLoader;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.jetbrains.annotations.NotNull;

/// 渔夫对话界面 —— 任务按钮 + 对话按钮，无交易。
/// 三个状态: COMPLETED(今日已完成) / NO_QUEST(无可用任务) / SHOW_HINT(展示任务鱼提示)
/// CAN_SUBMIT 状态不经过此界面，AnglerNPC 直接处理。
public class AnglerDialogScreen extends NPCDialogScreen {
    public enum State {COMPLETED, NO_QUEST, SHOW_HINT, WAKE_UP}

    private final State state;
    private final ItemStack questFish;
    private final String levelName;
    private boolean showQuestFish;

    public AnglerDialogScreen(int entityId, State state, Item questFish, String levelName) {
        super(entityId);
        this.state = state;
        this.questFish = questFish.getDefaultInstance();
        this.levelName = levelName;
        this.showQuestFish = state == State.SHOW_HINT;
    }

    @Override
    protected void init() {
        // 不调 super.init() —— 渔夫没有交易按钮
        Entity entity = minecraft.level.getEntity(entityId);
        if (entity instanceof BaseNPC npc) {
            initDialog(npc);
        }

        // 沿用普通 NPC 对话界面的双按钮布局，给下方的心情文本留出空间。
        addRenderableWidget(Button.builder(Component.translatable("gui.confluence.quest"), b -> {
            showQuestText();
        }).width(80).pos(width / 2 - 85, height / 2 + 20).build());

        // 对话按钮
        addRenderableWidget(Button.builder(Component.translatable("gui.confluence.dialog"), b -> {
            if (entity instanceof BaseNPC npc) {
                String key = NPCDialogLoader.getInstance().getRandomDialogKey(npc.getRandom1211(), npc.getType());
                if (key != null) {
                    dialogText = Component.translatable(key, levelName);
                    showQuestFish = false;
                }
            }
        }).width(80).pos(width / 2 + 5, height / 2 + 20).build());
    }

    private void initDialog(BaseNPC npc) {
        switch (state) {
            case COMPLETED -> {
                String key = NPCDialogLoader.getInstance().getRandomDialogKey(npc.getRandom1211(), npc.getType());
                dialogText = key != null ? Component.translatable(key, levelName) : Component.translatable("dialogs.confluence.angler.completed");
            }
            case NO_QUEST -> {
                String key = NPCDialogLoader.getInstance().getRandomDialogKey(npc.getRandom1211(), npc.getType());
                dialogText = key != null ? Component.translatable(key, levelName) : Component.translatable("dialogs.confluence.angler.no_quest");
            }
            case SHOW_HINT -> {
                String key = "dialogs.confluence.angler." + questFish.getDescriptionId();
                dialogText = Component.translatable(key);
            }
            case WAKE_UP ->
                    dialogText = Component.translatable(anglerDialogPrefix(npc) + ".wakeup." + npc.getRandom1211().nextInt(3));
        }
    }

    private void showQuestText() {
        showQuestFish = state == State.SHOW_HINT;
        switch (state) {
            case COMPLETED -> dialogText = Component.translatable("dialogs.confluence.angler.completed");
            case NO_QUEST -> dialogText = Component.translatable("dialogs.confluence.angler.no_quest");
            case SHOW_HINT -> dialogText = Component.translatable("dialogs.confluence.angler.quest_fish", Component.translatable(questFish.getDescriptionId()));
            case WAKE_UP -> {
                Entity entity = minecraft.level.getEntity(entityId);
                if (entity instanceof BaseNPC npc) {
                    dialogText = Component.translatable(anglerDialogPrefix(npc) + ".wakeup." + npc.getRandom1211().nextInt(3));
                }
            }
        }
    }

    /// 两种渔夫共用任务界面，但人物语气使用各自的翻译前缀。
    private static String anglerDialogPrefix(BaseNPC npc) {
        return npc.getType() == NpcEntities.FEMALE_ANGLER.get()
                ? "dialogs.confluence.female_angler"
                : "dialogs.confluence.angler";
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (showQuestFish) {
            int textTop = height / 2 - font.split(dialogText, DIALOG_WIDTH).size() * font.lineHeight / 2 - 30;
            guiGraphics.renderFakeItem(questFish, width / 2 - 8, textTop - 22);
        }
    }

    public static void open(int entityId, State state, Item questFish, String levelName) {
        Minecraft.getInstance().setScreen(new AnglerDialogScreen(entityId, state, questFish, levelName));
    }
}
