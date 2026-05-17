package org.confluence.mod.common.soulskill;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoulSkillStack {
    public static final SoulSkillStack EMPTY = new SoulSkillStack(SoulSkill.EMPTY);
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

    /**
     * 获取技能堆栈的提示文本列表
     *
     * @param skillStack 技能堆栈
     * @return 格式化字符序列列表
     */
    public static List<FormattedCharSequence> getSkillTooltipLines(SoulSkillStack skillStack) {
        List<FormattedCharSequence> list = new ArrayList<>();
        list.add(skillStack.getNameComponent().getVisualOrderText());
        Component component = skillStack.getNarrationComponent();
        if (component != null) {
            list.add(component.getVisualOrderText());
        }
        return Collections.synchronizedList(list);
    }

    @NotNull
    public Component getNameComponent() {
        ResourceLocation id = getSoulSkill().getId();
        return Component.translatable(id.getNamespace() + ".soul_skill." + id.getPath() + ".name");
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
