package org.confluence.mod.common.soul.skill;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.confluence.mod.common.init.ModSoulSkills;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulSkill {
    private static final float DEFAULT_SOUL_COST = 0.0F;
    private static final int DEFAULT_COOLDOWN_TICKS = 0;
    private final ResourceLocation icon;
    private final ResourceLocation id;
    private final float basicDamage;
    private final float soulCost;
    private final int cooldownTicks;

    public SoulSkill(ResourceLocation icon, ResourceLocation id, float basicDamage) {
        this(icon, id, basicDamage, DEFAULT_SOUL_COST, DEFAULT_COOLDOWN_TICKS);
    }

    public SoulSkill(ResourceLocation icon, ResourceLocation id, float basicDamage, float soulCost, int cooldownTicks) {
        this.icon = icon;
        this.id = id;
        this.basicDamage = basicDamage;
        this.soulCost = soulCost;
        this.cooldownTicks = cooldownTicks;
    }

    public ResourceLocation getIcon() {
        return icon.withPrefix("hud/soul_skill/");
    }

    public ResourceLocation getId() {
        return id;
    }

    public boolean is(TagKey<SoulSkill> tag) {
        return ModSoulSkills.getRegistry().wrapAsHolder(this).is(tag);
    }

    @NotNull
    public Component getNameComponent(SoulSkillStack skillStack) {
        return Component.translatable(id.getNamespace() + ".soul_skill." + id.getPath() + ".name");
    }

    public float getBasicDamage() {
        return basicDamage;
    }

    public float getDamage(SoulSkillStack skillStack) {
        return getBasicDamage();
    }

    public float getSoulCost(SoulSkillStack skillStack) {
        return soulCost;
    }

    public int getCooldownTicks(SoulSkillStack skillStack) {
        return cooldownTicks;
    }

    public boolean canCast(SoulSkillCastContext context) {
        return false;
    }

    public void cast(SoulSkillCastContext context) {
    }

    @Nullable
    public Component getComponent(SoulSkillStack skillStack) {
        return null;
    }

    public SoulSkillStack getStack() {
        return new SoulSkillStack(this);
    }
}
