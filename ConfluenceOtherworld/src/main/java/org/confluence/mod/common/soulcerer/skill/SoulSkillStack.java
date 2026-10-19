package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.confluence.mod.common.init.ModSoulSkills;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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

    /// tooltip 的数值行前缀：伤害 / 冷却 / BUFF持续时间 / 所需消耗的灵魂数 / 技能描述。
    public static final String TOOLTIP_KEY_PREFIX = "confluence.soulcerer_skill.tooltip.";

    /// 名称与数值行：名称，其后依次是伤害、冷却、各条 BUFF 持续时间、所需灵魂数。
    ///
    /// 数值为 0（无伤害 / 无冷却 / 不耗魂）的项不出现；顺序固定，便于横向对比不同技能。
    public List<Component> getSkillHeaderLines() {
        List<Component> list = new ArrayList<>();
        list.add(getNameComponent());
        if (isEmpty()) {
            return list;
        }
        SoulSkill skill = getSoulSkill();
        float damage = skill.getDamage(this);
        if (damage > 0.0F) {
            list.add(statLine("damage", formatNumber(damage)));
        }
        int cooldown = skill.getCooldown(this);
        if (cooldown > 0) {
            list.add(statLine("cooldown", formatNumber(cooldown / (float) SharedConstants.TICKS_PER_SECOND)));
        }
        for (SoulSkill.BuffDuration buff : skill.getBuffDurations(this)) {
            list.add(Component.translatable(TOOLTIP_KEY_PREFIX + "buff_duration",
                    formatNumber(buff.durationTicks() / (float) SharedConstants.TICKS_PER_SECOND),
                    buff.effectName()).withStyle(ChatFormatting.GRAY));
        }
        float soulCost = skill.getSoulCost(this);
        if (soulCost > 0.0F) {
            list.add(statLine("soul_cost", formatNumber(soulCost)));
        }
        return list;
    }

    /// 统一话术的技能说明行：「技能描述：…」；空技能返回 null。
    @Nullable
    public Component getSkillDescriptionLine() {
        Component description = getNarrationComponent();
        if (description == null) {
            return null;
        }
        return Component.translatable(TOOLTIP_KEY_PREFIX + "description", description).withStyle(ChatFormatting.GRAY);
    }

    /// 组合技能名称、数值与可选说明。
    public List<Component> getSkillDescriptionLines(){
        List<Component> list = new ArrayList<>(getSkillHeaderLines());
        Component description = getSkillDescriptionLine();
        if (description != null) {
            list.add(description);
        }
        return Collections.synchronizedList(list);
    }

    private static Component statLine(String type, String value) {
        return Component.translatable(TOOLTIP_KEY_PREFIX + type, value).withStyle(ChatFormatting.GRAY);
    }

    /// 数值显示：整数值不带小数点，其余保留一位。
    private static String formatNumber(float value) {
        if (Math.abs(value - Math.round(value)) < 1.0E-4F) {
            return Integer.toString(Math.round(value));
        }
        return String.format(Locale.ROOT, "%.1f", value);
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
