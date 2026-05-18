package org.confluence.mod.common.soulskill;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulSkill {
    private final ResourceLocation icon;
    private final ResourceLocation id;
    private final float basicDamage;

    public SoulSkill(ResourceLocation icon, ResourceLocation id, float basicDamage) {
        this.icon = icon;
        this.id = id;
        this.basicDamage = basicDamage;
    }

    public ResourceLocation getIcon() {
        return icon.withPrefix("hud/soul_skill/");
    }

    public ResourceLocation getId() {
        return id;
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

    @Nullable
    public Component getComponent(SoulSkillStack skillStack) {
        return null;
    }

    public SoulSkillStack getStack() {
        return new SoulSkillStack(this);
    }
}
