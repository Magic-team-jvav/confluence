package org.confluence.mod.client.handler;

import net.minecraft.client.KeyMapping;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.client.gui.hud.soul.CurrentSelectedSkillHud;
import org.confluence.mod.client.gui.hud.soul.quick_skill.*;
import org.confluence.mod.client.util.SoulQuickSkillHudUtils;
import org.confluence.mod.common.soulskill.SoulSkillStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class SoulQuickSkillHudHolder {
    public static final SoulQuickSkillHudHolder INSTANCE = new SoulQuickSkillHudHolder();
    public static final RouletteWheelBigHud ROULETTE_WHEEL_BIG_HUD_INSTANCE = new RouletteWheelBigHud();
    public static final RouletteWheelSmallHud ROULETTE_WHEEL_SMALL_HUD_INSTANCE = new RouletteWheelSmallHud();
    public static final CardHorizontalHud CARD_HORIZONTAL_L_HUD_INSTANCE = new CardHorizontalHud(false);
    public static final CardHorizontalHud CARD_HORIZONTAL_R_HUD_INSTANCE = new CardHorizontalHud(true);
    public static final CurrentSelectedSkillHud CURRENT_SELECTED_SKILL_HUD_INSTANCE = new CurrentSelectedSkillHud();

    // TODO 需要对接玩家本身的

    public boolean active = false;
    private int skillTotalNumber = 6;   // 技能总数
    private int currentIndex = 0;       // 当前技能索引
    private boolean wasSpellWheelDown = false;
    private final List<SoulSkillStack> skillStackList = new ArrayList<>();

    public void allUpdate() {
        holderUpdate();

        ROULETTE_WHEEL_BIG_HUD_INSTANCE.update();
        ROULETTE_WHEEL_SMALL_HUD_INSTANCE.update();
        CARD_HORIZONTAL_L_HUD_INSTANCE.update();
        CARD_HORIZONTAL_R_HUD_INSTANCE.update();
        CURRENT_SELECTED_SKILL_HUD_INSTANCE.update(getCurrentSkillStack(currentIndex));
    }

    private void holderUpdate() {
        if (skillTotalNumber <= 0) {
            skillStackList.clear();
        }

        int currentSize = skillStackList.size();
        int disparity = skillTotalNumber - currentSize;

        if (disparity > 0) {
            for (int i = 0; i < disparity; i++) {
                skillStackList.add(SoulSkillStack.EMPTY);
            }
        } else if (disparity < 0) {
            for (int i = 0; i > disparity; i--) {
                skillStackList.removeLast();
            }
        }

        for (int i = 0; i < skillTotalNumber; i++){
            skillStackList.set(i, getCurrentSkillStack(i));
        }

        skillStackList.removeIf(Objects::isNull);
    }

    public void handler() {
        while (getKeyMapping().consumeClick()) {
            if (!wasSpellWheelDown) {
                allOpen();
            }
        }

        handleSpellWheelRelease();
    }

    public boolean scrolling(double scrollDeltaY) {
        if (ClientConfigs.soulQuickSkillStyle == SoulQuickSkillHudHolder.Type.ROULETTE_WHEEL_BIG) {
            return false;
        }
        if (!active) {
            return false;
        }
        adjustTarget((int) scrollDeltaY);
        return true;
    }

    public void adjustTarget(int offset) {
        ROULETTE_WHEEL_SMALL_HUD_INSTANCE.adjustTarget(offset);
        CARD_HORIZONTAL_L_HUD_INSTANCE.adjustTarget(offset);
        CARD_HORIZONTAL_R_HUD_INSTANCE.adjustTarget(offset);
        setCurrentIndex(SoulQuickSkillHudUtils.calculateSkillIndex(offset, getCurrentIndex(), getSkillTotalNumber()));
    }

    private void handleSpellWheelRelease() {
        final boolean isDown = getKeyMapping().isDown();

        final boolean wasReleased = wasSpellWheelDown && !isDown;
        if (wasReleased) {
            allClose();
        }

        wasSpellWheelDown = isDown;
    }

    private @NotNull KeyMapping getKeyMapping() {
        return ModKeyBindings.MAGIC_QUICK_SKILL_SWITCHING.get();
    }

    private void allOpen() {
        active = true;
        open(ROULETTE_WHEEL_BIG_HUD_INSTANCE);
        open(ROULETTE_WHEEL_SMALL_HUD_INSTANCE);
        open(CARD_HORIZONTAL_L_HUD_INSTANCE);
        open(CARD_HORIZONTAL_R_HUD_INSTANCE);
    }

    private void allClose() {
        active = false;
        close(ROULETTE_WHEEL_BIG_HUD_INSTANCE);
        close(ROULETTE_WHEEL_SMALL_HUD_INSTANCE);
        close(CARD_HORIZONTAL_L_HUD_INSTANCE);
        close(CARD_HORIZONTAL_R_HUD_INSTANCE);
    }

    private void open(BasicSoulQuickSkillHud hud) {
        hud.open();
    }

    private void close(BasicSoulQuickSkillHud hud) {
        if (!hud.isActive()) {
            return;
        }
        hud.close();
    }

    public int getSkillTotalNumber() {
        return skillTotalNumber;
    }

    public void setSkillTotalNumber(int skillTotalNumber) {
        this.skillTotalNumber = Math.max(0, skillTotalNumber);
        allUpdate();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = Math.clamp(currentIndex, 0, Math.max(0, getSkillTotalNumber() - 1));
        allUpdate();
    }

    public List<SoulSkillStack> getSkillStackList() {
        return Collections.synchronizedList(skillStackList);
    }

    // TODO 应该从玩家中获取
    @Nullable
    public SoulSkillStack getCurrentSkillStack(int index) {
        return SoulSkillStack.EMPTY;
    }

    public enum Type {
        ROULETTE_WHEEL_BIG,
        ROULETTE_WHEEL_SMALL,
        CARD_HORIZONTAL_L,
        CARD_HORIZONTAL_R
    }
}
