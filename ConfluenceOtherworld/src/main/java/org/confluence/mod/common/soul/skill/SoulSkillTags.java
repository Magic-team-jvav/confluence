package org.confluence.mod.common.soul.skill;

import net.minecraft.tags.TagKey;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSoulSkills;

public class SoulSkillTags {
    public static final TagKey<SoulSkill> SOUL = create("soul");
    public static final TagKey<SoulSkill> BLOOD_NATURE = create("blood_nature");
    public static final TagKey<SoulSkill> STAR_LURE = create("star_lure");

    private static TagKey<SoulSkill> create(String path) {
        return TagKey.create(ModSoulSkills.REGISTRY_KEY, Confluence.asResource(path));
    }
}
