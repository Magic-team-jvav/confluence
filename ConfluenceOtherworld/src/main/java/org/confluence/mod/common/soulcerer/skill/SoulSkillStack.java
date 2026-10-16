package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.confluence.mod.common.init.ModSoulSkills;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoulSkillStack {
    public static final SoulSkillStack Empty = new SoulSkillStack(ModSoulSkills.EMPTY.get());

    private final SoulSkill soulSkill;
    private int cd = 0;
    private int maxCd = 0;

    public SoulSkillStack(SoulSkill soulSkill) {
        this.soulSkill = soulSkill;
    }

    public SoulSkill getSoulSkill() {
        return soulSkill;
    }

    /// 读取所属技能针对本栈计算的伤害。
    public float getDamage() {
        return soulSkill.getDamage(this);
    }

    /// 读取所属技能提供的补充说明。
    @Nullable
    public Component getNarrationComponent() {
        return soulSkill.getComponent(this);
    }

    /// 把技能说明转换为可直接绘制的文本行。
    public List<FormattedCharSequence> getSkillTooltipLines(){
        return getSkillDescriptionLines().stream().map(Component::getVisualOrderText).toList();
    }

    /// 组合技能名称与可选说明。
    public List<Component> getSkillDescriptionLines(){
        List<Component> list = new ArrayList<>();
        list.add(getNameComponent());
        Component component = getNarrationComponent();
        if (component != null) {
            list.add(component);
        }
        return Collections.synchronizedList(list);
    }

    /**
     * 获取技能堆栈的提示文本列表
     *
     * @param skillStack 技能堆栈
     * @return 格式化字符序列列表
     */
    public static List<FormattedCharSequence> getSkillTooltipLines(SoulSkillStack skillStack) {
        return skillStack.getSkillTooltipLines();
    }

    /// 读取当前技能栈的可翻译名称。
    @NotNull
    public Component getNameComponent() {
        return soulSkill.getNameComponent(this);
    }

    public int getCd() {
        return cd;
    }

    /// 设置当前冷却，空栈保持不变。
    public void setCd(int cd) {
        if (!isEmpty()) {
            this.cd = cd;
        }
    }

    public int getMaxCd() {
        return maxCd;
    }

    /// 设置冷却上限，空栈保持不变。
    public void setMaxCd(int maxCd) {
        if (!isEmpty()) {
            this.maxCd = maxCd;
        }
    }

    /// 空栈复用共享实例，其余复制技能和冷却。
    public SoulSkillStack copy(){
        if (isEmpty()) {
            return Empty;
        }
        SoulSkillStack soulSkillStack = new SoulSkillStack(soulSkill);
        soulSkillStack.setCd(cd);
        soulSkillStack.setMaxCd(maxCd);
        return soulSkillStack;
    }

    /// 判断当前栈是否为空技能栈。
    public boolean isEmpty() {
        return this == Empty || soulSkill == ModSoulSkills.EMPTY.get();
    }
}
