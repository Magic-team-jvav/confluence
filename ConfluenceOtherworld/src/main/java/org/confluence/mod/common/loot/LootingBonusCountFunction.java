package org.confluence.mod.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.confluence.mod.common.init.ModLootTables;

import java.util.Set;

/**
 * 高概率战利品池（权重掉落率不低于 5%）专用的抢夺适配。
 * <p>掉落概率完全由池自身的权重决定，本函数只在掉落成立之后叠加一个<b>固定</b>数量：
 * <ul>
 *     <li>无抢夺：数量不变（原来掉 1 个还是 1 个）</li>
 *     <li>抢夺 I 及以上：数量 {@code +bonus}（默认 +1），且<b>只吃 1 级</b>——
 *     等级再高也还是 +bonus，不随等级继续放大。</li>
 * </ul>
 * <p>这正是原版 {@code LootingEnchantFunction} 做不到的：那个函数是
 * {@code stack.grow(level * value)}，无抢夺时直接跳出不生效，有抢夺时又会按等级线性放大，
 * 无法表达“等级大于等于 1 就固定加 1”。
 */
public class LootingBonusCountFunction extends LootItemConditionalFunction {
    // NOTE: 1.20.1's LootItemConditionalFunction has no `commonFields` helper (added in 1.21),
    // LootItemCondition has no codec with which to embed a `conditions` list, and DFU 6.0.8 has no
    // `Codec#validate`.  So the codec carries only `bonus`; `when(...)` still works on the builder
    // but such conditions cannot be represented in the generated JSON.
    public static final MapCodec<LootingBonusCountFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, 64).optionalFieldOf("bonus", 1)
                    .forGetter(LootingBonusCountFunction::bonus)
    ).apply(instance, bonus -> new LootingBonusCountFunction(new LootItemCondition[0], bonus)));

    private final int bonus;

    public LootingBonusCountFunction(LootItemCondition[] predicates, int bonus) {
        super(predicates);
        this.bonus = bonus;
    }

    public int bonus() {
        return bonus;
    }

    @Override
    public LootItemFunctionType getType() {
        return ModLootTables.ItemFunctions.LOOTING_BONUS_COUNT.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.KILLER_ENTITY);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
        if (!(entity instanceof LivingEntity)) return stack;
        if (context.getLootingModifier() <= 0) return stack;
        // 用 setCount 而不是 grow：grow 受 reserved 空间限制，某些堆叠会静默失败
        stack.setCount(stack.getCount() + bonus);
        return stack;
    }

    /**
     * 构造一个抢夺大于等于 1 级时固定加 1 的掉落数量函数。
     */
    public static Builder lootingBonusCount() {
        return new Builder(1);
    }

    public static Builder lootingBonusCount(int bonus) {
        return new Builder(bonus);
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final int bonus;

        public Builder(int bonus) {
            this.bonus = bonus;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemConditionalFunction build() {
            return new LootingBonusCountFunction(getConditions(), bonus);
        }
    }
}
