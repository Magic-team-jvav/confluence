package org.confluence.mod.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.confluence.mod.common.init.ModLootTables;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// 使用百分比抽取内嵌子规则或引用子战利品表。
public final class PercentageLootEntry extends LootPoolSingletonContainer {

    public static final MapCodec<PercentageLootEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Rule.CODEC.listOf().fieldOf("rules").forGetter(value -> value.rules),
            Codec.BOOL.optionalFieldOf("exclusive", false).forGetter(value -> value.exclusive),
            Codec.BOOL.optionalFieldOf("guarantee", false).forGetter(value -> value.guarantee)
    ).and(singletonFields(instance)).apply(instance, PercentageLootEntry::new));

    // 单条子规则：percent 使用 0～100 的百分比，entry 可使用原版条目或引用子表。
    public record Rule(double percent, LootPoolEntryContainer entry) {
        public static final Codec<Rule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.doubleRange(0, 100).fieldOf("percent").forGetter(Rule::percent),
                LootPoolEntries.CODEC.fieldOf("entry").forGetter(Rule::entry)
        ).apply(instance, Rule::new));

        // 同时校验代码构造和数据反序列化的输入，拒绝非有限数及越界百分比。
        public Rule {
            if (!Double.isFinite(percent) || percent < 0 || percent > 100) {
                throw new IllegalArgumentException("Child percentage must be between 0 and 100: " + percent);
            }
        }
    }

    // 保留原版权重、幸运修正、条件和函数，并保存不可变的子规则列表。
    private PercentageLootEntry(List<Rule> rules, boolean exclusive, boolean guarantee, int weight, int quality,
                                List<LootItemCondition> conditions, List<LootItemFunction> functions) {
        super(weight, quality, conditions, functions);
        checkRules(rules, exclusive);
        this.rules = List.copyOf(rules);
        this.exclusive = exclusive;
        this.guarantee = guarantee;
    }

    private final List<Rule> rules;
    private final boolean exclusive;
    private final boolean guarantee;

    // 返回已注册的条目类型，供战利品表序列化使用。
    @Override
    public LootPoolEntryType getType() {
        return ModLootTables.EntryTypes.PERCENTAGE.get();
    }

    // 先过滤不满足条件或有效权重为零的子条目，再抽取规则并按原版权重生成掉落。
    // 保底只约束本组的规则抽取，不保证子表内部再次抽取后一定生成物品。
    @Override
    protected void createItemStack(Consumer<ItemStack> output, LootContext context) {
        List<List<LootPoolEntry>> eligible = new ArrayList<>();
        double[] probabilities = new double[rules.size()];
        double[] luckMultipliers = new double[rules.size()];
        for (int i = 0; i < rules.size(); i++) {
            List<LootPoolEntry> entries = new ArrayList<>();
            Rule rule = rules.get(i);
            if (rule.percent > 0) rule.entry.expand(context, entry -> {
                if (entry.getWeight(context.getLuck()) > 0) entries.add(entry);
            });
            eligible.add(entries);
            double probability = rule.percent / 100.0;
            double baseWeight = 0;
            double luckyWeight = 0;
            for (LootPoolEntry entry : entries) {
                baseWeight += entry.getWeight(0);
                luckyWeight += entry.getWeight(context.getLuck());
            }
            // 百分比表示幸运值为零时的概率，保留原版 quality 对有效权重的修正。
            probabilities[i] = probability;
            luckMultipliers[i] = baseWeight > 0 ? luckyWeight / baseWeight : 0;
        }
        probabilities = LootRuleRolls.applyLuck(probabilities, luckMultipliers, exclusive);
        boolean[] selected = LootRuleRolls.roll(probabilities, exclusive, guarantee, () -> context.getRandom().nextDouble());
        for (int i = 0; i < selected.length; i++) {
            if (!selected[i]) continue;
            List<LootPoolEntry> entries = eligible.get(i);
            long total = 0;
            for (LootPoolEntry entry : entries) total += entry.getWeight(context.getLuck());
            double roll = context.getRandom().nextDouble() * total;
            for (LootPoolEntry entry : entries) {
                roll -= entry.getWeight(context.getLuck());
                if (roll < 0) {
                    entry.createItemStack(output, context);
                    break;
                }
            }
        }
    }

    // 递归校验子条目，并保留规则索引，方便定位数据中的错误。
    @Override
    public void validate(ValidationContext context) {
        super.validate(context);
        for (int i = 0; i < rules.size(); i++) {
            rules.get(i).entry.validate(context.forChild(".rules[" + i + "].entry"));
        }
    }

    // 为一个原版条目创建子规则；percent 直接填写百分比，例如 25 表示 25%。
    public static Rule rule(double percent, LootPoolEntryContainer.Builder<?> entry) {
        return new Rule(percent, entry.build());
    }

    // 各子规则独立抽取，可同时成功；guarantee 表示按整组至少成功一次的分布抽取。
    public static LootPoolSingletonContainer.Builder<?> independent(boolean guarantee, Rule... rules) {
        return group(false, guarantee, rules);
    }

    // 子规则互斥，至多选中一条；guarantee 会将符合条件的非零百分比归一化。
    public static LootPoolSingletonContainer.Builder<?> exclusive(boolean guarantee, Rule... rules) {
        return group(true, guarantee, rules);
    }

    // 复用原版条目构建器，使组本身仍可附加条件、函数和权重。
    private static LootPoolSingletonContainer.Builder<?> group(boolean exclusive, boolean guarantee, Rule... rules) {
        List<Rule> children = List.of(rules);
        return simpleBuilder((weight, quality, conditions, functions) ->
                new PercentageLootEntry(children, exclusive, guarantee, weight, quality, conditions, functions));
    }

    // 拒绝空规则组；互斥组的百分比总和不得超过 100%。
    private static void checkRules(List<Rule> rules, boolean exclusive) {
        if (rules.isEmpty())
            throw new IllegalArgumentException("A simple loot group needs at least one child rule");
        if (exclusive && rules.stream().mapToDouble(Rule::percent).sum() > 100.0000001) {
            throw new IllegalArgumentException("Exclusive child percentages exceed 100");
        }
    }
}
