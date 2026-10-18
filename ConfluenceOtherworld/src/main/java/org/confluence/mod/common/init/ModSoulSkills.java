package org.confluence.mod.common.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.soulcerer.skill.SoulSkill;
import org.confluence.mod.common.soulcerer.skill.SoulLureSkill;
import org.confluence.mod.common.soulcerer.skill.EnhancedSoulSkill;
import org.confluence.mod.common.soulcerer.skill.SpiritSurgeSkill;
import org.confluence.mod.common.soulcerer.skill.SurvivalInstinctSkill;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.registries.PortCustomRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.*;
import java.util.function.Supplier;
import java.util.function.Function;

public final class ModSoulSkills {
    public static final ResourceKey<Registry<SoulSkill>> REGISTRY_KEY = Confluence.asResourceKey("soulcerer_skill");
    public static final PortCustomRegistration<SoulSkill> TYPES = PortRegisterHandler.custom(Confluence.MODID, REGISTRY_KEY, builder -> {
        builder.sync(true);
        builder.registerToRootRegistry();
    });
    public static final PortCustomRegistration<SoulSkill> REGISTRY = TYPES;
    private static final DeferredRegister<SoulSkill> DEFINITIONS = DeferredRegister.create(REGISTRY_KEY, Confluence.MODID);
    private static final List<Supplier<SoulSkill>> SKILLS = new ArrayList<>();
    private static final Map<ResourceLocation, SkillCategory> CATEGORIES = new HashMap<>();

    public static final Supplier<SoulSkill> EMPTY = register("empty", 0, SkillCategory.EMPTY);
    public static final Supplier<SoulSkill> BLOOD_RAGE = register("blood_rage", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> BOILING_BLOOD = register("boiling_blood", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> CONFUSE_SPORES = register("confuse_spores", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> EMPOWERED_SURGE = register("empowered_surge", 0, SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> ENHANCED_LURE = register("enhanced_lure", 0, SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> ENHANCED_SOUL = register("enhanced_soul", rl -> new EnhancedSoulSkill(rl, rl), SkillCategory.SOUL);
    public static final Supplier<SoulSkill> KARMA_FLAME = register("karma_flame", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> LAW_OF_NATURE = register("law_of_nature", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> LURE_SURGE = register("lure_surge", 0, SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> NATURES_WRATH = register("natures_wrath", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> PROFANE_SOUL = register("profane_soul", 0, SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SOUL_DRAIN = register("soul_drain", 0, SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SOUL_LURE = register("soul_lure", rl -> new SoulLureSkill(rl, rl), SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SOUL_MARK = register("soul_mark", 0, SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SOUL_PLUNDER = register("soul_plunder", 0, SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SOUL_SURGE = register("soul_surge", 0, SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SPIRIT_SURGE = register("spirit_surge", rl -> new SpiritSurgeSkill(rl, rl), SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> SPIRIT_TRIGGER = register("spirit_trigger", 0, SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> STAR_CALL = register("star_call", 0, SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> STAR_LINK = register("star_link", 0, SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> STAR_REVERSAL = register("star_reversal", 0, SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> SURGE_BLAST = register("surge_blast", 0, SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> SURVIVAL_INSTINCT = register("survival_instinct", rl -> new SurvivalInstinctSkill(Confluence.asResource("soul_lure"), rl), SkillCategory.SOUL);

    public enum SkillCategory {
        EMPTY("", 0xFFFFFFFF, "confluence.screen.soulcerer_overview.tab.empty"),
        SOUL("S", 0xFF6080FF, "confluence.screen.soulcerer_overview.tab.soul"),
        BLOOD_NATURE("B", 0xFF60CC60, "confluence.screen.soulcerer_overview.tab.blood_nature"),
        STAR_LURE("L", 0xFFFFCC40, "confluence.screen.soulcerer_overview.tab.star_lure");

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
        return register(id, rl -> new SoulSkill(rl, rl, basicDamage), category);
    }

    /// 注册共享技能定义；缓存供应器允许空栈在注册事件前安全初始化。
    private static Supplier<SoulSkill> register(String id, Function<ResourceLocation, SoulSkill> factory, SkillCategory category) {
        ResourceLocation rl = Confluence.asResource(id);
        SoulSkill cached = factory.apply(rl);
        DEFINITIONS.register(id, () -> cached);
        SKILLS.add(() -> cached);
        CATEGORIES.put(cached.getId(), category);
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
        return CATEGORIES.getOrDefault(supplier.get().getId(), SkillCategory.BLOOD_NATURE);
    }

    public static void register(IEventBus eventBus) {
        DEFINITIONS.register(eventBus);
    }
}
