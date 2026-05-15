package org.confluence.mod.common.soulskill;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

public class SoulSkill {
    public static final SoulSkill EMPTY = new SoulSkill(
            Confluence.asResource(""),
            Confluence.asResource("empty"),
            123f);

    private final ResourceLocation icon;
    private final ResourceLocation id;
    private final float basicDamage;

    public SoulSkill(ResourceLocation icon, ResourceLocation id, float basicDamage) {
        this.icon = icon;
        this.id = id;
        this.basicDamage = basicDamage;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public ResourceLocation getId() {
        return id;
    }

    public float getBasicDamage() {
        return basicDamage;
    }

    public float getDamage(SoulSkillStack skillStack) {
        return getBasicDamage();
    }

    @Nullable
    public Component getComponent(SoulSkillStack skillStack) {
        return Component.empty();
    }
}
