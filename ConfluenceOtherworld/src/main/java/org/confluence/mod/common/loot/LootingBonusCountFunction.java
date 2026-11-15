package org.confluence.mod.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.confluence.mod.common.init.ModLootTables;

import java.util.List;
import java.util.Set;

// 高概率战利品池（权重掉落率不低于 5%）专用的抢夺适配。
// 掉落概率完全由池自身的权重决定，本函数只在掉落成立之后叠加一个固定数量：
//
//     无抢夺：数量不变（原来掉 1 个还是 1 个）
//     抢夺 I 及以上：数量 +bonus（默认 +1），且只吃 1 级——
//     等级再高也还是 +bonus，不随等级继续放大。
//
// 这正是原版 minecraft:enchanted_count_increase 做不到的：那个函数是
// stack.grow(level * value)，无抢夺时直接跳出不生效，有抢夺时又会按等级线性放大，
// 无法表达“等级大于等于 1 就固定加 1”。
//
public class LootingBonusCountFunction extends LootItemConditionalFunction {
    public static final MapCodec<LootingBonusCountFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(instance.group(
                    Enchantment.CODEC.fieldOf("enchantment").forGetter(LootingBonusCountFunction::enchantment),
                    Codec.intRange(1, 64).optionalFieldOf("bonus", 1).forGetter(LootingBonusCountFunction::bonus)
            ))
            .apply(instance, LootingBonusCountFunction::new));

    private final Holder<Enchantment> enchantment;
    private final int bonus;

    public LootingBonusCountFunction(List<LootItemCondition> predicates, Holder<Enchantment> enchantment, int bonus) {
        super(predicates);
        this.enchantment = enchantment;
        this.bonus = bonus;
    }

    public Holder<Enchantment> enchantment() {
        return enchantment;
    }

    public int bonus() {
        return bonus;
    }

    @Override
    public LootItemFunctionType<LootingBonusCountFunction> getType() {
        return ModLootTables.ItemFunctions.LOOTING_BONUS_COUNT.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.ATTACKING_ENTITY);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
        if (!(entity instanceof LivingEntity living)) return stack;
        if (EnchantmentHelper.getEnchantmentLevel(enchantment, living) <= 0) return stack;
        // 掉落成立后固定增加数量，不随抢夺等级继续增加。
        stack.setCount(stack.getCount() + bonus);
        return stack;
    }

    // 构造一个抢夺大于等于 1 级时固定增加 bonus 数量的掉落函数。
    //
    public static Builder lootingBonusCount(HolderLookup.Provider registries, int bonus) {
        Holder<Enchantment> looting = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING);
        return new Builder(looting, bonus);
    }

    // 构造一个抢夺大于等于 1 级时固定加 1 的掉落数量函数。
    //
    public static Builder lootingBonusCount(HolderLookup.Provider registries) {
        return lootingBonusCount(registries, 1);
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final Holder<Enchantment> enchantment;
        private final int bonus;

        public Builder(Holder<Enchantment> enchantment, int bonus) {
            this.enchantment = enchantment;
            this.bonus = bonus;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemConditionalFunction build() {
            return new LootingBonusCountFunction(getConditions(), enchantment, bonus);
        }
    }
}
