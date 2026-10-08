package org.confluence.mod.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.confluence.mod.common.init.ModLootTables;

import java.util.List;
import java.util.Set;

/**
 * 低概率战利品池（权重掉落率低于 5%）专用的抢夺适配。
 * <p>最终掉落率 = 基础概率 × 抢夺倍率：
 * <ul>
 *     <li>无抢夺：{@code unenchantedChance}</li>
 *     <li>抢夺 I / II / III 及以上：{@code unenchantedChance * bonusForLevel(等级)}</li>
 * </ul>
 * <p>倍率默认按 2 / 3 / 4 递增，超过列表长度后使用列表最后一项封顶（抢夺 III 之后不再提升）。
 * <p>1.20.1 没有原版的 {@code minecraft:random_chance_with_enchanted_bonus}，也没有
 * {@code LevelBasedValue}，因此这里把每个等级的倍率直接写进 JSON，语义与 1.21.1 侧保持一致。
 * <p>抢夺等级统一取 {@link LootContext#getLootingModifier()}，与原版 {@code LootingEnchantFunction}
 * 一致，因此也能正确响应 Forge 的抢夺修正事件。
 */
public record LootingScaledChanceLootItemCondition(float unenchantedChance,
                                                   List<Float> bonusByLevel) implements LootItemCondition {
    public static final List<Float> DEFAULT_BONUS = List.of(2.0F, 3.0F, 4.0F);

    public static final MapCodec<LootingScaledChanceLootItemCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf("unenchanted_chance").forGetter(LootingScaledChanceLootItemCondition::unenchantedChance),
            Codec.FLOAT.listOf().optionalFieldOf("bonus_by_level", DEFAULT_BONUS).forGetter(LootingScaledChanceLootItemCondition::bonusByLevel)
    ).apply(instance, LootingScaledChanceLootItemCondition::new));

    @Override
    public LootItemConditionType getType() {
        return ModLootTables.ItemConditions.LOOTING_SCALED_CHANCE.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.KILLER_ENTITY);
    }

    @Override
    public boolean test(LootContext context) {
        int level = context.getLootingModifier();
        if (level <= 0) return context.getRandom().nextFloat() < unenchantedChance;
        if (bonusByLevel.isEmpty()) return context.getRandom().nextFloat() < unenchantedChance;
        int i = Math.min(level, bonusByLevel.size()) - 1;
        return context.getRandom().nextFloat() < unenchantedChance * bonusByLevel.get(i);
    }

    public static Builder lootingScaledChance(float base) {
        return new Builder(base);
    }

    public static class Builder implements LootItemCondition.Builder {
        private final float base;
        private List<Float> bonus = DEFAULT_BONUS;

        public Builder(float base) {
            this.base = base;
        }

        public Builder bonusByLevel(List<Float> bonus) {
            this.bonus = bonus;
            return this;
        }

        @Override
        public LootItemCondition build() {
            return new LootingScaledChanceLootItemCondition(base, bonus);
        }
    }
}
