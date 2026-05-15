package org.confluence.mod.common.soulskill;

import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoulSkillStack {
    public static final SoulSkillStack EMPTY = new SoulSkillStack(SoulSkill.EMPTY);
    private final SoulSkill soulSkill;
    private int level = 1;
    private int cd = 0;
    private int maxCd = 0;

    public SoulSkillStack(SoulSkill soulSkill) {
        this.soulSkill = soulSkill;
    }

    public SoulSkill getSoulSkill() {
        return soulSkill;
    }

    public float getDamage() {
        return soulSkill.getDamage(this);
    }

    @Nullable
    public Component getComponent() {
        return soulSkill.getComponent(this);
    }

    /**
     * 获取技能堆栈的提示文本列表
     *
     * @param skillStack 技能堆栈
     * @return 格式化字符序列列表
     */
    public static List<FormattedCharSequence> getSkillTooltipLines(SoulSkillStack skillStack) {
        List<FormattedCharSequence> list = new ArrayList<>();
        list.add(getMessage(skillStack).getVisualOrderText());
        Component component = skillStack.getComponent();
        if (component != null) {
            list.add(component.getVisualOrderText());
        }
        return Collections.synchronizedList(list);
    }

    public static @NotNull Component getMessage(SoulSkillStack skillStack) {
        return Component.translatable(skillStack.getSoulSkill().getId().toString())
                .append(" ")
                .append(Component.translatable(String.valueOf(skillStack.getLevel())).withColor(0x2ef0d3));
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public int getCd() {
        return cd;
    }

    public void setCd(int cd) {
        this.cd = cd;
    }

    public int getMaxCd() {
        return maxCd;
    }

    public void setMaxCd(int maxCd) {
        this.maxCd = maxCd;
    }
}
