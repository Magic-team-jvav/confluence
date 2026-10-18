package org.confluence.mod.common.soulcerer.skill;

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
    private float basicSoulCost;
    private int basicCooldown;

    public SoulSkill(ResourceLocation icon, ResourceLocation id, float basicDamage) {
        this(icon, id, basicDamage, DEFAULT_BASIC_SOUL_COST, DEFAULT_BASIC_COOLDOWN);
    }

    public SoulSkill(ResourceLocation icon, ResourceLocation id, float basicDamage, float basicSoulCost, int basicCooldown) {
        this.icon = icon;
        this.id = id;
        this.basicDamage = basicDamage;
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

    /// 按技能 ID 生成可翻译的名称。
    @NotNull
    public Component getNameComponent(SoulSkillStack skillStack) {
        return Component.translatable(id.getNamespace() + ".soulcerer_skill." + id.getPath() + ".name");
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

    /// 判断技能自身的使用条件，默认不附加限制。
    public boolean canUse(ServerPlayer player, SoulSkillStack skillStack) {
        return true;
    }

    /// 服务端判定通过后，在两端分别执行对应技能效果。
    public void use(Player player, SoulSkillStack skillStack) {
    }

    /// 提供技能补充说明，默认不提供。
    @Nullable
    public Component getComponent(SoulSkillStack skillStack) {
        return null;
    }

    /// 空技能复用共享空栈，其余创建新技能栈。
    public SoulSkillStack getStack() {
        return this == ModSoulSkills.EMPTY.get() ? SoulSkillStack.Empty : new SoulSkillStack(this);
    }
}

