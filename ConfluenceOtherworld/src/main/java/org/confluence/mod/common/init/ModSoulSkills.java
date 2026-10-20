package org.confluence.mod.common.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;
import org.confluence.mod.common.soulcerer.skill.SoulProjectileSkill;
import org.confluence.mod.common.soulcerer.skill.SoulBuffSkill;
import org.confluence.mod.common.soulcerer.skill.SoulSurgeSkill;
import org.confluence.mod.common.soulcerer.skill.SurgeBlastSkill;
import org.confluence.mod.common.soulcerer.skill.SoulMarkSkill;
import org.confluence.mod.common.soulcerer.skill.PassiveSoulSkill;
import org.confluence.mod.common.soulcerer.skill.KarmaFlameSkill;
import org.confluence.mod.common.soulcerer.skill.SoulAssaultSkill;
import org.confluence.mod.common.soulcerer.skill.BoilingBloodSkill;
import org.confluence.mod.common.soulcerer.skill.StatefulSoulSkill;
import org.confluence.mod.common.soulcerer.skill.NaturesWrathSkill;
import org.confluence.mod.common.soulcerer.skill.ProfaneSoulSkill;
import org.confluence.mod.common.soulcerer.skill.SoulSkill;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
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

    public static final Supplier<SoulSkill> EMPTY = register("empty", rl -> new SoulSkill(rl, rl), SkillCategory.EMPTY);

    // ===== 引魂 / 攻击弹幕 =====
    public static final Supplier<SoulSkill> SOUL_LURE = register("soul_lure", rl -> new SoulLureSkill(rl, rl), SkillCategory.SOUL);
    public static final Supplier<SoulSkill> ENHANCED_LURE = register("enhanced_lure", rl -> new SoulProjectileSkill(rl, rl, SoulProjectileSkill.builder(SoulLureProjectile.PARTICLE)
            .damage(SoulSkillParameters.ENHANCED_LURE_DAMAGE)
            .cost(SoulSkillParameters.ENHANCED_LURE_SOUL_COST, SoulSkillParameters.ENHANCED_LURE_COOLDOWN_TICKS)), SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> SOUL_MARK = register("soul_mark", rl -> new SoulMarkSkill(rl, rl), SkillCategory.SOUL);
    public static final Supplier<SoulSkill> LURE_SURGE = register("lure_surge", rl -> new SoulProjectileSkill(rl, rl, SoulProjectileSkill.builder(SoulLureProjectile.PARTICLE)
            .damage(SoulSkillParameters.LURE_SURGE_DAMAGE)
            .cost(SoulSkillParameters.LURE_SURGE_SOUL_COST, SoulSkillParameters.LURE_SURGE_COOLDOWN_TICKS)
            .aoe(SoulSkillParameters.LURE_SURGE_RADIUS, SoulSkillParameters.LURE_SURGE_AOE_DAMAGE)), SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> CONFUSE_SPORES = register("confuse_spores", rl -> new SoulProjectileSkill(rl, rl, SoulProjectileSkill.builder(SoulLureProjectile.SPORE_PARTICLE)
            .damage(SoulSkillParameters.CONFUSE_SPORES_DAMAGE)
            .cost(SoulSkillParameters.CONFUSE_SPORES_SOUL_COST, SoulSkillParameters.CONFUSE_SPORES_COOLDOWN_TICKS)
            .count(SoulSkillParameters.CONFUSE_SPORES_COUNT, SoulSkillParameters.CONFUSE_SPORES_SPREAD)
            .gravity(0.03)
            .spawnAbove(SoulSkillParameters.CONFUSE_SPORES_SPAWN_HEIGHT)
            .speed(SoulSkillParameters.CONFUSE_SPORES_SPEED)), SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> NATURES_WRATH = register("natures_wrath", rl -> new NaturesWrathSkill(rl, rl), SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> PROFANE_SOUL = register("profane_soul", rl -> new ProfaneSoulSkill(rl, rl), SkillCategory.SOUL);
    public static final Supplier<SoulSkill> KARMA_FLAME = register("karma_flame", rl -> new KarmaFlameSkill(rl, rl), SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> SOUL_ASSAULT = register("soul_assault", rl -> new SoulAssaultSkill(rl, rl), SkillCategory.SOUL);

    // ===== 灵魂激荡系列 =====
    public static final Supplier<SoulSkill> SPIRIT_SURGE = register("spirit_surge", rl -> new SpiritSurgeSkill(rl, rl), SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> EMPOWERED_SURGE = register("empowered_surge", rl -> new SoulSurgeSkill(rl, rl, SoulSkillParameters.EMPOWERED_SURGE_DAMAGE,
            SoulSkillParameters.EMPOWERED_SURGE_SOUL_COST, SoulSkillParameters.EMPOWERED_SURGE_COOLDOWN_TICKS,
            SoulSkillParameters.EMPOWERED_SURGE_RADIUS, SoulSkillParameters.EMPOWERED_SURGE_VERTICAL_RANGE,
            SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS, 0, 0.0F), SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> SPIRIT_TRIGGER = register("spirit_trigger", rl -> new SoulSurgeSkill(rl, rl, SoulSkillParameters.SPIRIT_TRIGGER_DAMAGE,
            SoulSkillParameters.SPIRIT_TRIGGER_SOUL_COST, SoulSkillParameters.SPIRIT_TRIGGER_COOLDOWN_TICKS,
            SoulSkillParameters.SPIRIT_SURGE_RADIUS, SoulSkillParameters.SPIRIT_SURGE_VERTICAL_RANGE,
            SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS, SoulSkillParameters.SPIRIT_TRIGGER_CHAIN_COUNT,
            SoulSkillParameters.SPIRIT_TRIGGER_CHAIN_DAMAGE), SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> SURGE_BLAST = register("surge_blast", rl -> new SurgeBlastSkill(rl, rl), SkillCategory.STAR_LURE);

    // ===== 附魂（自身增益） =====
    public static final Supplier<SoulSkill> ENHANCED_SOUL = register("enhanced_soul", rl -> new EnhancedSoulSkill(rl, rl), SkillCategory.SOUL);
    /// 附魂激荡：主动释放的限时增益，增益期间免费自动释放灵魂激荡（见 `PlayerUtils.tickSoulSkillStates`）。
    public static final Supplier<SoulSkill> SOUL_SURGE = register("soul_surge", rl -> new StatefulSoulSkill(rl, rl,
            SoulSkillParameters.SOUL_SURGE_SOUL_COST, SoulSkillParameters.SOUL_SURGE_COOLDOWN_TICKS,
            () -> ModEffects.SOUL_SURGE.get(), SoulSkillParameters.SOUL_SURGE_DURATION_TICKS, false), SkillCategory.SOUL);
    public static final Supplier<SoulSkill> BLOOD_RAGE = register("blood_rage", rl -> new SoulBuffSkill(rl, rl,
            SoulSkillParameters.BLOOD_RAGE_SOUL_COST, SoulSkillParameters.BLOOD_RAGE_COOLDOWN_TICKS,
            () -> ModEffects.BLOOD_RAGE.get(), SoulSkillParameters.BLOOD_RAGE_DURATION_TICKS, SoulSkillParameters.BLOOD_RAGE_HEALTH_COST), SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> SOUL_PLUNDER = register("soul_plunder", rl -> new SoulBuffSkill(rl, rl,
            SoulSkillParameters.SOUL_PLUNDER_SOUL_COST, SoulSkillParameters.SOUL_PLUNDER_COOLDOWN_TICKS,
            () -> ModEffects.SOUL_PLUNDER.get(), SoulSkillParameters.SOUL_PLUNDER_DURATION_TICKS, 0.0F), SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SOUL_DRAIN = register("soul_drain", rl -> new StatefulSoulSkill(rl, rl, 0.0F,
            SoulSkillParameters.SOUL_DRAIN_COOLDOWN_TICKS, () -> ModEffects.SOUL_DRAIN.get(),
            SoulSkillParameters.SOUL_DRAIN_DURATION_TICKS, false), SkillCategory.SOUL);
    public static final Supplier<SoulSkill> STAR_CALL = register("star_call", rl -> new StatefulSoulSkill(rl, rl,
            SoulSkillParameters.STAR_CALL_SOUL_COST, SoulSkillParameters.STAR_CALL_COOLDOWN_TICKS,
            () -> ModEffects.STAR_CALL.get(), SoulSkillParameters.STAR_CALL_DURATION_TICKS, false), SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> BOILING_BLOOD = register("boiling_blood", rl -> new BoilingBloodSkill(rl, rl, () -> ModEffects.BOILING_BLOOD.get()), SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> LIFE_RETURN = register("life_return", rl -> new StatefulSoulSkill(rl, rl,
            SoulSkillParameters.LIFE_RETURN_SOUL_COST, SoulSkillParameters.LIFE_RETURN_COOLDOWN_TICKS,
            () -> ModEffects.LIFE_RETURN.get(), SoulSkillParameters.LIFE_RETURN_DURATION_TICKS, true), SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> FIRMAMENT_REVERSED = register("firmament_reversed", rl -> new SoulBuffSkill(rl, rl,
            SoulSkillParameters.FIRMAMENT_REVERSED_SOUL_COST, SoulSkillParameters.FIRMAMENT_REVERSED_COOLDOWN_TICKS,
            () -> ModEffects.FIRMAMENT_REVERSED.get(), SoulSkillParameters.FIRMAMENT_REVERSED_DURATION_TICKS, 0.0F), SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> SURVIVAL_INSTINCT = register("survival_instinct", rl -> new SurvivalInstinctSkill(rl, rl), SkillCategory.SOUL);

    // ===== 被动 =====
    public static final Supplier<SoulSkill> LAW_OF_NATURE = register("law_of_nature", rl -> new PassiveSoulSkill(rl, rl), SkillCategory.BLOOD_NATURE);
    public static final Supplier<SoulSkill> STAR_REVERSAL = register("star_reversal", rl -> new PassiveSoulSkill(rl, rl), SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> STAR_LINK = register("star_link", rl -> new PassiveSoulSkill(rl, rl), SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> SOUL_HARVEST = register("soul_harvest", rl -> new PassiveSoulSkill(rl, rl), SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SOUL_DEVOUR = register("soul_devour", rl -> new PassiveSoulSkill(rl, rl), SkillCategory.SOUL);
    public static final Supplier<SoulSkill> BACKLASH_RESISTANCE = register("backlash_resistance", rl -> new PassiveSoulSkill(rl, rl), SkillCategory.SOUL);
    public static final Supplier<SoulSkill> SOUL_CONTROL = register("soul_control", rl -> new PassiveSoulSkill(rl, rl), SkillCategory.STAR_LURE);
    public static final Supplier<SoulSkill> SOUL_ROTATION = register("soul_rotation", rl -> new PassiveSoulSkill(rl, rl), SkillCategory.STAR_LURE);
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
