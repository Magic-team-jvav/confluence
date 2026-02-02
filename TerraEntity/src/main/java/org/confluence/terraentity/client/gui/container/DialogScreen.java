package org.confluence.terraentity.client.gui.container;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.terraentity.api.event.NPCEvent;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.misc.NPCDialogs;
import org.confluence.terraentity.entity.npc.mood.MoodInfo;
import org.confluence.terraentity.entity.npc.mood.NPCMood;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.mixed.IPlayer;
import org.confluence.terraentity.network.c2s.ServerBoundEventPacket;
import org.confluence.terraentity.utils.AdapterUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DialogScreen extends Screen {
    Button tradeButton;
    Button summonButton; // 仅老人有效
    Button dialogButton;
    @Nullable Screen parent;
    private final boolean trade;
    ITradeHolder holder;
    Component dialogText;

    protected DialogScreen(@Nullable Screen parent, boolean trade) {
        super(Component.empty());
        this.parent = parent;
        this.trade = trade;
    }

    @Override
    protected void init() {
        super.init();

        holder = ((IPlayer) Minecraft.getInstance().player).terra_entity$getTradeHolder();

        if (trade) {
            initTradeButton();
        }

        if (holder instanceof AbstractTerraNPC npc) {
            initDialog(npc);
            addRenderableWidget(dialogButton = Button.builder(Component.translatable("dialogs.terra_entity.dialog"), b -> initDialog(npc)
            ).width(50).pos(width / 2, height / 2 + 25).build());

            if (LibDateUtils.isNight(npc.level()) && npc.getType() == TENpcEntities.OLD_MAN.get()) {
                summonButton = Button.builder(Component.translatable("dialogs.terra_entity.summon"), p -> {
                    ServerBoundEventPacket.summonSkeletron();
                    Minecraft.getInstance().setScreen(null); // 关闭对话框
                }).width(50).pos(width / 2 - 160, height / 2 + 25).build();
                addRenderableWidget(summonButton);
            }
        }
    }

    protected void initDialog(AbstractTerraNPC npc) {
        Component dialog = getRandomDialog(npc);
        if (dialog != null) {
            dialogText = AdapterUtils.postGameEvent(new NPCEvent.NPCDialogEvent(npc, dialog)).getNeoDialog();
        }
    }

    protected void initTradeButton() {
        addRenderableWidget(tradeButton = Button.builder(Component.translatable("dialogs.terra_entity.trade"), p -> {
            if (parent != null) {
                Minecraft.getInstance().setScreen(parent);
            }
        }).width(50).pos(width / 2 - 80, height / 2 + 25).build());
    }

    protected Component getRandomDialog(AbstractTerraNPC npc) {
        String dialog = NPCDialogs.Loader.getInstance().getRandomDialog(npc.getRandom(), npc.getType());
        return dialog == null ? null : Component.translatable(dialog);
    }

    @Override
    protected void rebuildWidgets() {
        if (tradeButton != null) {
            tradeButton.setPosition(width / 2 - 80, height / 2 + 25);
        }
        if (summonButton != null) {
            summonButton.setPosition(width / 2 - 160, height / 2 + 25);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (dialogText != null) {
            if (font.width(dialogText) > 128) {
                List<FormattedCharSequence> list = font.split(dialogText, 128);
                int l = height / 2 - list.size() * 9 / 2;
                for (FormattedCharSequence formattedcharsequence : list) {
                    guiGraphics.drawString(font, formattedcharsequence, 20, l, 0xFFFFFF);
                    l += 9;
                }
            } else {
                guiGraphics.drawString(font, dialogText, 20, height / 2 - 10, 0xFFFFFF);
            }
        }

        // todo draw
        if (holder.getMood() == null) {
            return;
        }
        var list = holder.getMood().getMoodInfoList();
        for (int i = 0; i < list.size(); i++) {
            ResourceLocation location = list.get(i);

            MoodInfo moodInfo = NPCMood.Loader.getInstance().getMoodInfo(location);

            if (moodInfo == null) continue;
            guiGraphics.drawString(font, Component.translatable(moodInfo.info()), 20, height / 2 - 100 + i * 10, 0xFFFFFF);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (minecraft != null && minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
            minecraft.setScreen(trade ? parent : null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
