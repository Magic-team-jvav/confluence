package org.confluence.mod.common.init;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.soulskill.SoulSkill;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

// TODO 需要重写
public final class ModSoulSkills {
    private static final List<Supplier<SoulSkill>> SKILLS = new ArrayList<>();
    private static final Map<ResourceLocation, SkillCategory> CATEGORIES = new HashMap<>();

    public static final Supplier<SoulSkill> EMPTY = register("empty", 0, SkillCategory.EMPTY);
    public static final Supplier<SoulSkill> BLOOD_RAGE = register("blood_rage", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> BOILING_BLOOD = register("boiling_blood", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> CONFUSE_SPORES = register("confuse_spores", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> EMPOWERED_SURGE = register("empowered_surge", 0, SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> ENHANCED_LURE = register("enhanced_lure", 0, SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> ENHANCED_SOUL = register("enhanced_soul", 0, SkillCategory.SOUL);
    public static final Supplier<SoulSkill> KARMA_FLAME = register("karma_flame", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> LAW_OF_NATURE = register("law_of_nature", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> LURE_SURGE = register("lure_surge", 0, SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> NATURES_WRATH = register("natures_wrath", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> PROFANE_SOUL = register("profane_soul", 0, SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SOUL_DRAIN = register("soul_drain", 0, SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SOUL_LURE = register("soul_lure", 0, SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SOUL_MARK = register("soul_mark", 0, SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SOUL_PLUNDER = register("soul_plunder", 0, SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SOUL_SURGE = register("soul_surge", 0, SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SPIRIT_SURGE = register("spirit_surge", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> SPIRIT_TRIGGER = register("spirit_trigger", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> STAR_CALL = register("star_call", 0, SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> STAR_LINK = register("star_link", 0, SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> STAR_REVERSAL = register("star_reversal", 0, SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> SURGE_BLAST = register("surge_blast", 0, SkillCategory.STAR_LURE);

    public enum SkillCategory {
        EMPTY("", 0xFFFFFFFF, "confluence.screen.soul_overview.tab.empty"),
        SOUL("S", 0xFF6080FF, "confluence.screen.soul_overview.tab.soul"),
        BLOOD_NATURE("B", 0xFF60CC60, "confluence.screen.soul_overview.tab.blood_nature"),
        STAR_LURE("L", 0xFFFFCC40, "confluence.screen.soul_overview.tab.star_lure");

        public final String label;
        public final int color;
        public final String translationKey;

        SkillCategory(String label, int color, String translationKey) {
            this.label = label;
            this.color = color;
            this.translationKey = translationKey;
        }
    }

    private static Supplier<SoulSkill> register(String id, float basicDamage, SkillCategory category) {
        ResourceLocation rl = Confluence.asResource(id);
        SoulSkill cached = new SoulSkill(rl, rl, basicDamage);
        SKILLS.add(() -> cached);
        CATEGORIES.put(cached.id(), category);
        return () -> cached;
    }

    public static List<Supplier<SoulSkill>> getAllSkills() {
        return Collections.unmodifiableList(SKILLS);
    }

    public static List<Supplier<SoulSkill>> getSkillsByCategory(SkillCategory category) {
        return SKILLS.stream()
                .filter(s -> getCategory(s) == category)
                .toList();
    }

    @Nullable
    public static SoulSkill getFirstInCategory(SkillCategory category) {
        for (Supplier<SoulSkill> s : SKILLS) {
            if (getCategory(s) == category) return s.get();
        }
        return null;
    }

    public static SkillCategory getCategory(Supplier<SoulSkill> supplier) {
        return CATEGORIES.getOrDefault(supplier.get().id(), SkillCategory.BLOOD_NATURE);
    }

    public static void register(IEventBus eventBus) {
    }
}
