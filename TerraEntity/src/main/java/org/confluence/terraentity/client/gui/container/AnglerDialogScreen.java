package org.confluence.terraentity.client.gui.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.AnglerNPC;
import org.confluence.terraentity.network.s2c.SetAnglerDialogPacketS2C;
import org.confluence.terraentity.network.s2c.SyncLevelNamePacketS2C;
import org.confluence.terraentity.registries.npc_trade_task.variant.DynamicAnglerTradeTask;

import java.util.Random;

public class AnglerDialogScreen extends DialogScreen {
    private static final Random random = new Random();
    private boolean taskSucceed;
    private boolean wakeup;
    private int lastTradeLevel = -1;
    private boolean taskFinished = false;
    private boolean taskReady = false;

    protected AnglerDialogScreen(Screen parent, byte data) {
        super(parent, true);
        this.taskSucceed = (data & SetAnglerDialogPacketS2C.TASK_SUCCEED) != 0;
        this.wakeup = (data & SetAnglerDialogPacketS2C.WAKEUP) != 0;
    }

    @Override
    protected void initTradeButton() {
        if (wakeup) return;
        addRenderableWidget(this.tradeButton = Button.builder(Component.translatable("dialogs.terra_entity.quest"), p -> {
            AnglerNPC angler = (AnglerNPC) holder;
            String key = angler.getType().builtInRegistryHolder().unwrapKey().orElseThrow().location().toLanguageKey();
            if (taskFinished) {
                if (random.nextBoolean()) {
                    this.dialogText = Component.translatable("dialogs." + key + ".task_finished.5", angler.getName());
                } else {
                    this.dialogText = Component.translatable("dialogs." + key + ".task_finished." + random.nextInt(5));
                }
            } else if (taskReady) {
                this.taskReady = false;
                DynamicAnglerTradeTask task = angler.getFirstTask();
                if (task != null && task.canTrade(angler, 0)) {
                    this.dialogText = Component.translatable("dialogs." + key + "." + task.getCurrentCost().getDescriptionId());
                }
            } else {
                Minecraft.getInstance().setScreen(parent);
            }
        }).width(50).pos(width / 2 - 80, height / 2 + 25).build());
    }

    @Override
    protected Component getRandomDialog(AbstractTerraNPC npc) {
        AnglerNPC angler = (AnglerNPC) npc;
        String key = angler.getType().builtInRegistryHolder().unwrapKey().orElseThrow().location().toLanguageKey();

        if (wakeup) {
            this.wakeup = false;
            return Component.translatable("dialogs." + key + ".wakeup." + random.nextInt(3));
        }

        if (taskSucceed) {
            this.taskSucceed = false;
            return Component.translatable("dialogs." + key + ".task_succeed." + random.nextInt(5));
        }

        int level = npc.getTradeParams().getLevel(0);
        if (level == lastTradeLevel) {
            this.taskFinished = true;
            this.taskReady = false;
        }
        DynamicAnglerTradeTask task = angler.getFirstTask();
        if (task != null && task.canTrade(angler, 0)) {
            this.lastTradeLevel = level;
            this.taskFinished = false;
            this.taskReady = true;
            int i = random.nextInt(3);
            return Component.translatable("dialogs." + key + ".task_ready." + i, i == 2
                    ? new Object[]{angler.getName(), SyncLevelNamePacketS2C.levelName}
                    : TranslatableContents.NO_ARGS);
        }

        if (random.nextFloat() < 0.2F) {
            return Component.translatable("dialogs." + key + ".stat." + random.nextInt(2), level);
        }

        int i = random.nextInt(8);
        return Component.translatable("dialogs." + key + "." + i, i == 2 || i == 7
                ? new Object[]{SyncLevelNamePacketS2C.levelName}
                : TranslatableContents.NO_ARGS);
    }

    public static class Handler {
        public static void handleTaskSucceed(byte data) {
            Minecraft.getInstance().setScreen(new AnglerDialogScreen(Minecraft.getInstance().screen, data));
        }
    }
}
