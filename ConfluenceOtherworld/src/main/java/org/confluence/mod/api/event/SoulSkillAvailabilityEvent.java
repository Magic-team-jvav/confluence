package org.confluence.mod.api.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.confluence.mod.common.soul.skill.SoulSkill;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/// 每次刷新重新收集临时可用技能；永久学会的技能始终可用，不受此事件限制。
public class SoulSkillAvailabilityEvent extends PlayerEvent {
    private final Set<ResourceLocation> skills = new HashSet<>();

    public SoulSkillAvailabilityEvent(ServerPlayer player) {
        super(player);
    }

    public void addSkill(ResourceLocation skillId) {
        skills.add(skillId);
    }

    public void addSkill(SoulSkill skill) {
        addSkill(skill.getId());
    }

    public Set<ResourceLocation> getSkills() {
        return Collections.unmodifiableSet(skills);
    }
}
