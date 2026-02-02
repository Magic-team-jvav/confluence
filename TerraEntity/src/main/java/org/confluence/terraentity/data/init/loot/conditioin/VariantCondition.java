package org.confluence.terraentity.data.init.loot.conditioin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.confluence.terraentity.data.init.loot.TELootParams;
import org.confluence.terraentity.init.TELoots;
import org.jetbrains.annotations.NotNull;

public record VariantCondition(int variant) implements LootItemCondition {

    public static final MapCodec<VariantCondition> CODEC = Codec.INT.xmap(VariantCondition::new, VariantCondition::variant).fieldOf("variant");

    @Override
    public @NotNull LootItemConditionType getType() {
        return TELoots.TELootItemConditions.VARIANT_CONDITION.get();
    }

    @Override
    public boolean test(LootContext context) {
        Integer var3 = context.getParamOrNull(TELootParams.VARIANT);
        return var3!= null && var3 == variant;
    }

    public static Builder of(int variant) {
        return ()-> new VariantCondition(variant);
    }
}
