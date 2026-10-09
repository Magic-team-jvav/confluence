package org.confluence.mod.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
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

// 高概率战利品池（权重掉落率不低于 5%）专用的抢夺适配。
// 掉落概率完全由池自身的权重决定，本函数只在掉落成立之后叠加一个固定数量：
//
//     无抢夺：数量不变（原来掉 1 个还是 1 个）
//     抢夺 I 及以上：数量 +bonus（默认 +1），且只吃 1 级——
//     等级再高也还是 +bonus，不随等级继续放大。
//
// 这正是原版 LootingEnchantFunction 做不到的：那个函数是
// stack.grow(level * value)，无抢夺时直接跳出不生效，有抢夺时又会按等级线性放大，
// 无法表达“等级大于等于 1 就固定加 1”。
//
public class LootingBonusCountFunction extends LootItemConditionalFunction {
    private final int bonus;

    public LootingBonusCountFunction(LootItemCondition[] predicates, int bonus) {
        super(predicates);
        if (bonus < 1 || bonus > 64)
            throw new IllegalArgumentException("bonus must be between 1 and 64");
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
        // 掉落成立后固定增加数量，不随抢夺等级继续增加。
        stack.setCount(stack.getCount() + bonus);
        return stack;
    }

    // 构造一个抢夺大于等于 1 级时固定加 1 的掉落数量函数。
    //
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

    // Use the vanilla conditional serializer to preserve when(...) predicates in JSON.
    public static class Serializer extends LootItemConditionalFunction.Serializer<LootingBonusCountFunction> {
        @Override
        public void serialize(JsonObject json, LootingBonusCountFunction value, JsonSerializationContext context) {
            super.serialize(json, value, context);
            json.addProperty("bonus", value.bonus);
        }

        @Override
        public LootingBonusCountFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
            return new LootingBonusCountFunction(conditions, GsonHelper.getAsInt(json, "bonus", 1));
        }
    }
}
