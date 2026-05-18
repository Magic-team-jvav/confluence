package org.confluence.mod.common.soulskill;

import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.confluence.mod.common.init.ModSoulSkills;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class SoulSkillStack {
    public static final Supplier<SoulSkillStack> EMPTY = new Supplier<>() {
        private boolean isInit;
        private SoulSkillStack stack;

        @Override
        public SoulSkillStack get() {
            if (!isInit) {
                stack = ModSoulSkills.EMPTY.get().getStack();
                isInit = true;
            }
            return stack;
        }
    };

    private final SoulSkill soulSkill;
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
    public Component getNarrationComponent() {
        return soulSkill.getComponent(this);
    }

    public List<FormattedCharSequence> getSkillTooltipLines(){
        return getSkillDescriptionLines().stream().map(Component::getVisualOrderText).toList();
    }

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

    @NotNull
    public Component getNameComponent() {
        return soulSkill.getNameComponent(this);
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

    public SoulSkillStack copy(){
        SoulSkillStack soulSkillStack = new SoulSkillStack(soulSkill);
        soulSkillStack.setCd(cd);
        soulSkillStack.setMaxCd(maxCd);
        return soulSkillStack;
    }

    public static SoulSkillStack getEmptyStack() {
        return SoulSkillStack.EMPTY.get();
    }

    public static boolean isEmpty(@Nullable SoulSkillStack stack) {
        return stack == null || stack == SoulSkillStack.getEmptyStack();
    }
}
