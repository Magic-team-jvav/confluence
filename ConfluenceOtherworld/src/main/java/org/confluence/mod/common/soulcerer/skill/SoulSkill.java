package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.init.ModSoulSkills;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulSkill {
    private static final float DEFAULT_BASIC_SOUL_COST = 0.0F;
    private static final int DEFAULT_BASIC_COOLDOWN = 0;

    private final ResourceLocation icon;
    private final ResourceLocation id;
    private final float basicDamage;
    private final Kind kind;
    private float basicSoulCost;
    private int basicCooldown;

    /// 技能类型：主动释放、附魂增益（自身 Buff）、被动。
    ///
    /// 只有 `ACTIVE` 与 `BUFF` 能被装备并主动释放；`PASSIVE` 学会即生效，
    /// 装备栏自动跳过（见 `PlayerUtils.getSoulSkillIndexes` 与术式轮转）。
    public enum Kind {
        ACTIVE,
        BUFF,
        PASSIVE
    }

    public SoulSkill(ResourceLocation icon, ResourceLocation id, float basicDamage) {
        this(icon, id, basicDamage, Kind.ACTIVE);
    }

    /// 无伤害技能（如空技能）的简写构造。
    public SoulSkill(ResourceLocation icon, ResourceLocation id) {
        this(icon, id, 0.0F);
    }

    public SoulSkill(ResourceLocation icon, ResourceLocation id, float basicDamage, Kind kind) {
        this(icon, id, basicDamage, kind, DEFAULT_BASIC_SOUL_COST, DEFAULT_BASIC_COOLDOWN);
    }

    public SoulSkill(ResourceLocation icon, ResourceLocation id, float basicDamage, float basicSoulCost, int basicCooldown) {
        this(icon, id, basicDamage, Kind.ACTIVE, basicSoulCost, basicCooldown);
    }

    public SoulSkill(ResourceLocation icon, ResourceLocation id, float basicDamage, Kind kind, float basicSoulCost, int basicCooldown) {
        this.icon = icon;
        this.id = id;
        this.basicDamage = basicDamage;
        this.kind = kind;
        this.basicSoulCost = basicSoulCost;
        this.basicCooldown = basicCooldown;
    }

    /// 获取技能图标在 HUD 图集中的资源路径。
    public ResourceLocation getIcon() {
        return icon.withPrefix("hud/soulcerer_skill/");
    }

    public ResourceLocation getId() {
        return id;
    }

    public Kind getKind() {
        return kind;
    }

    /// 被动技能无法装备也无法主动释放。
    public boolean isPassive() {
        return kind == Kind.PASSIVE;
    }

    /// 附魂类技能会给自己上同名药水效果，用于 `附魂` 标签归类与查询。
    public boolean isBuff() {
        return kind == Kind.BUFF;
    }

    /// 按技能 ID 生成可翻译的名称。
    @NotNull
    public Component getNameComponent(SoulSkillStack skillStack) {
        return Component.translatable(id.getNamespace() + ".soulcerer_skill." + id.getPath() + ".name");
    }

    /// 技能说明的翻译键，与名称同前缀，便于一一对应维护。
    @NotNull
    public String getDescriptionKey() {
        return id.getNamespace() + ".soulcerer_skill." + id.getPath() + ".desc";
    }

    public float getBasicDamage() {
        return basicDamage;
    }

    /// 根据技能栈读取伤害，默认使用基础伤害。
    public float getDamage(SoulSkillStack skillStack) {
        return getBasicDamage();
    }

    public float getBasicSoulCost() {
        return basicSoulCost;
    }

    /// 根据技能栈读取灵魂消耗，默认使用基础消耗。
    public float getSoulCost(SoulSkillStack skillStack) {
        return getBasicSoulCost();
    }

    /// 读取基础冷却时间，单位为 tick。
    public int getBasicCooldown() {
        return basicCooldown;
    }

    /// 根据技能栈读取冷却时间，单位为 tick。
    public int getCooldown(SoulSkillStack skillStack) {
        return getBasicCooldown();
    }

    /// 判断已获得技能在当前条件下是否可用，失效时不清除装备。
    public boolean isAvailable(Player player) {
        return true;
    }

    /// 查询数据包定义的技能标签。
    public boolean is(TagKey<SoulSkill> tag) {
        return ModSoulSkills.REGISTRY.getHolder(id).map(holder -> holder.is(tag)).orElse(false);
    }

    /// 判断技能自身的使用条件，被动技能默认不可释放。
    public boolean canUse(ServerPlayer player, SoulSkillStack skillStack) {
        return !isPassive();
    }

    /// 服务端判定通过后，在两端分别执行对应技能效果。
    public void use(Player player, SoulSkillStack skillStack) {
    }

    /// 技能总览（C 键）与技能框的说明行；空技能不提供。
    @Nullable
    public Component getComponent(SoulSkillStack skillStack) {
        if (this == ModSoulSkills.EMPTY.get()) {
            return null;
        }
        return Component.translatable(getDescriptionKey()).withStyle(ChatFormatting.GRAY);
    }

    /// 空技能复用共享空栈，其余创建新技能栈。
    public SoulSkillStack getStack() {
        return this == ModSoulSkills.EMPTY.get() ? SoulSkillStack.Empty : new SoulSkillStack(this);
    }
}
