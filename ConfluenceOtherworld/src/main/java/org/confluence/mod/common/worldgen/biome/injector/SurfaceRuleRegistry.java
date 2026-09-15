package org.confluence.mod.common.worldgen.biome.injector;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.confluence.mod.util.OverworldUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/// 地表规则注册表。
///
/// ## 与 TerraBlender 的区别
///
/// TerraBlender 为了在「原版规则之前 / 之后」两个位置插入规则，整份复制了原版
/// `SurfaceRuleData.overworldLike` / `nether`（700+ 行），并且必须随 MC 版本同步维护。
/// 这里只用 `SurfaceRules.sequence` 在**真正的**原版规则对象前后拼接：
///
/// - {@link Stage#PREPEND}：本模组规则在前，抢在原版之前。等价于 TerraBlender 的
///   `BEFORE_BEDROCK`（它在序列里就是第一个），因为 `sequence` 是「首个非 null 获胜」。
/// - {@link Stage#APPEND}：本模组规则在后，只在该列原版规则返回 null 时生效，作为兜底。
///
/// 原版规则对象来自 `settings.value().surfaceRule()`，永远不漂移。
public final class SurfaceRuleRegistry {
    public enum Stage {
        PREPEND,
        APPEND
    }

    public enum Category {
        OVERWORLD,
        NETHER,
        END
    }

    private record Rule(String owner, Stage stage, int priority, SurfaceRules.RuleSource source) {}

    private static final Map<Category, List<Rule>> RULES = new EnumMap<>(Category.class);

    static {
        for (Category category : Category.values()) {
            RULES.put(category, new CopyOnWriteArrayList<>());
        }
    }

    private SurfaceRuleRegistry() {}

    /// 注册一条规则。
    ///
    /// ## 替换语义
    ///
    /// 重复注册的判定键是 **(owner, stage, priority)** 三元组，不是单独的 owner。
    /// 这一点很关键：同一个模组在同一维度上经常要注册多条规则
    /// （例如「本模组群系的地表」和「改写原版群系的地表」），
    /// 如果只按 owner 去重，后一条会把前一条静默删掉。
    /// 按三元组去重则既能容纳多条，又能让重复调用 `bootstrap()` 不会累积重复项。
    ///
    /// @param owner    注册方标识，用于成组移除
    /// @param priority 越大越先被求值（优先级越高）
    public static void add(Category category, String owner, Stage stage, int priority, SurfaceRules.RuleSource source) {
        List<Rule> rules = RULES.get(category);
        rules.removeIf(rule -> rule.owner().equals(owner) && rule.stage() == stage && rule.priority() == priority);
        rules.add(new Rule(owner, stage, priority, source));
    }

    /// 移除某个注册方在该类别下的**全部**规则。
    public static void remove(Category category, String owner) {
        RULES.get(category).removeIf(rule -> rule.owner().equals(owner));
    }

    /// 移除某个注册方在该类别下指定优先级的那一条。
    public static void remove(Category category, String owner, Stage stage, int priority) {
        RULES.get(category).removeIf(rule -> rule.owner().equals(owner) && rule.stage() == stage && rule.priority() == priority);
    }

    /// 供启动日志使用的描述，用来确认「到底注册进去了几条」。
    public static String describe(Category category) {
        List<Rule> rules = RULES.get(category);
        if (rules.isEmpty()) return "<none>";
        StringBuilder builder = new StringBuilder();
        for (Rule rule : rules) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(rule.owner()).append('(').append(rule.stage()).append(",p=").append(rule.priority()).append(')');
        }
        return builder.toString();
    }

    public static boolean hasRules(Category category) {
        return !RULES.get(category).isEmpty();
    }

    /// 把原版规则与注册的规则拼成最终规则源。没有任何注册时直接返回原对象。
    public static SurfaceRules.RuleSource compose(Category category, SurfaceRules.RuleSource vanilla) {
        List<Rule> rules = RULES.get(category);
        if (rules.isEmpty()) return vanilla;

        List<SurfaceRules.RuleSource> prepend = collect(rules, Stage.PREPEND);
        List<SurfaceRules.RuleSource> append = collect(rules, Stage.APPEND);
        if (prepend.isEmpty() && append.isEmpty()) return vanilla;

        List<SurfaceRules.RuleSource> sequence = new ArrayList<>(prepend.size() + append.size() + 1);
        sequence.addAll(prepend);
        sequence.add(vanilla);
        sequence.addAll(append);
        return SurfaceRules.sequence(sequence.toArray(SurfaceRules.RuleSource[]::new));
    }

    private static List<SurfaceRules.RuleSource> collect(List<Rule> rules, Stage stage) {
        return rules.stream()
                .filter(rule -> rule.stage() == stage)
                .sorted(Comparator.comparingInt(Rule::priority).reversed())
                .map(Rule::source)
                .toList();
    }

    /// 按维度类型解析类别。`ConfluenceBiomeInjector#install` 用它给每个 `LevelStem`
    /// 生成器算好规则；返回 `null` 表示该维度不在本框架覆盖范围内，规则源保持原样。
    @Nullable
    public static Category categoryOf(Holder<DimensionType> dimensionType) {
        if (dimensionType.is(BuiltinDimensionTypes.OVERWORLD)) return Category.OVERWORLD;
        if (dimensionType.is(BuiltinDimensionTypes.NETHER)) return Category.NETHER;
        if (dimensionType.is(BuiltinDimensionTypes.END)) return Category.END;
        return null;
    }

    @Nullable
    public static Category categoryOf(ResourceKey<Level> dimension) {
        if (OverworldUtils.dimension().equals(dimension)) return Category.OVERWORLD;
        if (OverworldUtils.underworld().equals(dimension)) return Category.NETHER;
        if (Level.END.equals(dimension)) return Category.END;
        return null;
    }
}
