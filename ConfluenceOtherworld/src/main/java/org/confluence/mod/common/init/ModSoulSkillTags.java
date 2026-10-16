package org.confluence.mod.common.init;

import net.minecraft.tags.TagKey;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.soulcerer.skill.SoulSkill;

public class ModSoulSkillTags {
    public static final TagKey<SoulSkill> SOUL = Confluence.asTagKey(ModSoulSkills.REGISTRY_KEY, "soul");
    public static final TagKey<SoulSkill> BLOOD_NATURE = Confluence.asTagKey(ModSoulSkills.REGISTRY_KEY, "blood_nature");
    public static final TagKey<SoulSkill> STAR_LURE = Confluence.asTagKey(ModSoulSkills.REGISTRY_KEY, "star_lure");
}
