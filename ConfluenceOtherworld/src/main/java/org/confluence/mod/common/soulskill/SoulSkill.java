package org.confluence.mod.common.soulskill;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.client.gui.components.PortSprite;

public record SoulSkill(PortSprite icon, ResourceLocation id, float basicDamage) {
    @NotNull
    public Component getNameComponent(SoulSkillStack skillStack) {
        return Component.translatable(id.getNamespace() + ".soul_skill." + id.getPath() + ".name");
    }

    public float getDamage(SoulSkillStack skillStack) {
        return basicDamage();
    }

    @Nullable
    public Component getComponent(SoulSkillStack skillStack) {
        return null;
    }

    public SoulSkillStack getStack() {
        return new SoulSkillStack(this);
    }
}
