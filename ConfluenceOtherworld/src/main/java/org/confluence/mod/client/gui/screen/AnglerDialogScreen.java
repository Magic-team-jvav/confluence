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
///
/// 从 1.20 `org.confluence.mod.client.gui.screen.AnglerDialogScreen` 逐字搬（同一个 FQN）。
/// 落地前已跑 `tools/port2native/check_duplicates.py`（--src120 .../src/main/java --ref .）：
/// 1.21 侧只有 TerraEntity 的 `org.confluence.terraentity.client.gui.container.AnglerDialogScreen`
/// （TE 时代的 `DialogScreen` 子类，判 DIFF，不同实现、不同 FQN），本仓库主模组侧无同名类，不是重复落地。
///
/// 1.21 原生改写（相对 1.20 原文）：
/// 1. `npc.getRandom1211()` → `npc.getRandom()`：1.20 的 `getRandom1211` 是 PortLib 转发别名，
///    1.21 用原生 `Entity#getRandom()`（`RandomSource`）；同一改写见 `NPCReforgeMenu.java:29`、
///    `NPCDialogScreen.java:79`，`NPCDialogLoader#getRandomDialogKey(RandomSource, EntityType)` 收的正是它。
/// 2. 基类换成 1.21 的 `NPCDialogScreen`（`client/gui/screen/NPCDialogScreen.java`），屏体写法以本批刚落地的
///    兄弟屏 `NPCDialogScreen` / `GoblinTinkererDialogScreen` 为准（`renderBackground` 在基类里已是 4 形参形态）。
/// 3. `init()` 里按 1.21 基类的写法补了 `minecraft == null || minecraft.level == null` 的空检查
///    （1.20 原文直接取 `minecraft.level`，1.21 基类 `NPCDialogScreen#init` 也这么防）。
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
        if (minecraft == null || minecraft.level == null) return;
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
                String key = NPCDialogLoader.getInstance().getRandomDialogKey(npc.getRandom(), npc.getType());
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
                String key = NPCDialogLoader.getInstance().getRandomDialogKey(npc.getRandom(), npc.getType());
                dialogText = key != null ? Component.translatable(key, levelName) : Component.translatable("dialogs.confluence.angler.completed");
            }
            case NO_QUEST -> {
                String key = NPCDialogLoader.getInstance().getRandomDialogKey(npc.getRandom(), npc.getType());
                dialogText = key != null ? Component.translatable(key, levelName) : Component.translatable("dialogs.confluence.angler.no_quest");
            }
            case SHOW_HINT -> {
                String key = "dialogs.confluence.angler." + questFish.getDescriptionId();
                dialogText = Component.translatable(key);
            }
            case WAKE_UP ->
                    dialogText = Component.translatable(anglerDialogPrefix(npc) + ".wakeup." + npc.getRandom().nextInt(3));
        }
    }

    private void showQuestText() {
        showQuestFish = state == State.SHOW_HINT;
        switch (state) {
            case COMPLETED -> dialogText = Component.translatable("dialogs.confluence.angler.completed");
            case NO_QUEST -> dialogText = Component.translatable("dialogs.confluence.angler.no_quest");
            case SHOW_HINT -> dialogText = Component.translatable("dialogs.confluence.angler.quest_fish", Component.translatable(questFish.getDescriptionId()));
            case WAKE_UP -> {
                if (minecraft == null || minecraft.level == null) break;
                Entity entity = minecraft.level.getEntity(entityId);
                if (entity instanceof BaseNPC npc) {
                    dialogText = Component.translatable(anglerDialogPrefix(npc) + ".wakeup." + npc.getRandom().nextInt(3));
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
