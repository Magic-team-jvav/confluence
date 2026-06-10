package org.confluence.mod.client.handler;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.client.gui.hud.soul.CurrentSelectedSkillHud;
import org.confluence.mod.client.gui.hud.soul.quick_skill.*;
import org.confluence.mod.client.util.SoulQuickSkillHudUtils;
import org.confluence.mod.common.soulskill.SoulSkill;
import org.confluence.mod.common.soulskill.SoulSkillStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 客户端类，服务端请勿调用
 */
public final class SoulSkillClientHolder {
    public static final SoulSkillClientHolder INSTANCE = new SoulSkillClientHolder();
    public static final RouletteWheelBigHud ROULETTE_WHEEL_BIG_HUD_INSTANCE = new RouletteWheelBigHud();
    public static final RouletteWheelSmallHud ROULETTE_WHEEL_SMALL_HUD_INSTANCE = new RouletteWheelSmallHud();
    public static final CardHorizontalHud CARD_HORIZONTAL_L_HUD_INSTANCE = new CardHorizontalHud(false);
    public static final CardHorizontalHud CARD_HORIZONTAL_R_HUD_INSTANCE = new CardHorizontalHud(true);
    public static final CurrentSelectedSkillHud CURRENT_SELECTED_SKILL_HUD_INSTANCE = new CurrentSelectedSkillHud();

    // TODO 需要对接玩家本身的

    public boolean active = false;
    private int equippedSkillMaxNumber = 6;
    private int currentIndex = 0;
    private boolean wasSpellWheelDown = false;
    private final List<SoulSkillStack> equippedSkills = new ArrayList<>(6);

    private final Map<ResourceLocation, SoulSkill> unlockedSkills = new HashMap<>(6); // 已解锁技能

    public void init() {
        maxNumberUpdate();
    }

    public void allUpdate() {
        maxNumberUpdate();

        ROULETTE_WHEEL_BIG_HUD_INSTANCE.update();
        ROULETTE_WHEEL_SMALL_HUD_INSTANCE.update();
        CARD_HORIZONTAL_L_HUD_INSTANCE.update();
        CARD_HORIZONTAL_R_HUD_INSTANCE.update();
        CURRENT_SELECTED_SKILL_HUD_INSTANCE.updateSkill(getCurrentSkillStack(currentIndex));
    }

    public void maxNumberUpdate() {
        if (equippedSkillMaxNumber <= 0) {
            equippedSkills.clear();
            return;
        }

        int currentSize = equippedSkills.size();
        int disparity = equippedSkillMaxNumber - currentSize;

        if (disparity > 0) {
            for (int i = 0; i < disparity; i++) {
                equippedSkills.add(SoulSkillStack.getEmptyStack());
            }
        } else if (disparity < 0) {
            for (int i = 0; i > disparity; i--) {
                equippedSkills.removeLast();
            }
        }

        equippedSkills.removeIf(Objects::isNull);
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
        if (ClientConfigs.soulQuickSkillStyle == SoulSkillClientHolder.Type.ROULETTE_WHEEL_BIG) {
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
        setCurrentIndex(SoulQuickSkillHudUtils.calculateSkillIndex(offset, getCurrentIndex(), getEquippedSkillMaxNumber()));
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

    public int getEquippedSkillMaxNumber() {
        return equippedSkillMaxNumber;
    }

    public void setEquippedSkillMaxNumber(int equippedSkillMaxNumber) {
        this.equippedSkillMaxNumber = Math.max(0, equippedSkillMaxNumber);
        allUpdate();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    @Nullable
    public SoulSkillStack getCurrentSkill() {
        return getCurrentSkillStack(currentIndex);
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = Math.clamp(currentIndex, 0, Math.max(0, getEquippedSkillMaxNumber() - 1));
        allUpdate();
    }

    public List<Integer> getSkillIndexes(SoulSkill skill) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < equippedSkills.size(); i++) {
            SoulSkillStack equipped = equippedSkills.get(i);
            if (equipped == null || !equipped.getSoulSkill().equals(skill)) {
                continue;
            }
            indexes.add(i);
        }
        return Collections.unmodifiableList(indexes);
    }

    public List<SoulSkillStack> getEquippedSkills() {
        return Collections.unmodifiableList(equippedSkills);
    }

    public boolean equipSkill(int index, @Nullable SoulSkillStack skillStack) {
        return setSoulSkillStack(index, skillStack == null ? SoulSkillStack.getEmptyStack() : skillStack);
    }

    public boolean unequipSkill(int index) {
        return setSoulSkillStack(index, SoulSkillStack.getEmptyStack());
    }

    public boolean setSoulSkillStack(int index, @Nullable SoulSkillStack skillStack) {
        if (index < 0 || index >= equippedSkillMaxNumber) return false;
        if (index >= equippedSkills.size()) return false;
        equippedSkills.set(index, skillStack == null ? SoulSkillStack.getEmptyStack() : skillStack);
        allUpdate();
        return true;
    }

    public boolean isSkillEquipped(ResourceLocation skillId) {
        for (SoulSkillStack equipped : equippedSkills) {
            if (equipped == null || !equipped.getSoulSkill().id().equals(skillId)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public boolean isSkillEquipped(SoulSkillStack stack) {
        for (SoulSkillStack equipped : equippedSkills) {
            if (equipped == null || !equipped.equals(stack)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public boolean isSkillEquipped(SoulSkill skill) {
        ResourceLocation id = skill.id();
        for (SoulSkillStack equipped : equippedSkills) {
            if (equipped != null && equipped.getSoulSkill().id().equals(id)) {
                return true;
            }
        }
        return false;
    }

    // ---- 解锁技能 ----

    public void addUnlockedSkill(SoulSkill skill) {
        unlockedSkills.put(skill.id(), skill);
    }

    public void removeUnlockedSkill(ResourceLocation skillId) {
        unlockedSkills.remove(skillId);
    }

    public boolean isSkillUnlocked(ResourceLocation skillId) {
        return unlockedSkills.containsKey(skillId);
    }

    public Map<ResourceLocation, SoulSkill> getUnlockedSkills() {
        return unlockedSkills;
    }

    // TODO 应该从玩家中获取
    @Nullable
    public SoulSkillStack getCurrentSkillStack(int index) {
        if (index < 0 || index >= equippedSkillMaxNumber) return null;
        if (index >= equippedSkills.size()) return null;
        return equippedSkills.get(index);
    }

    public enum Type {
        ROULETTE_WHEEL_BIG,
        ROULETTE_WHEEL_SMALL,
        CARD_HORIZONTAL_L,
        CARD_HORIZONTAL_R
    }
}
