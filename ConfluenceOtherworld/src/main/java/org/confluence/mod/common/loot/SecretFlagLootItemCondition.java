package org.confluence.mod.common.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.confluence.mod.api.SecretFlagMatcher;
import org.confluence.mod.common.init.ModLootTables;

public record SecretFlagLootItemCondition(long secretFlag, boolean flipMatch) implements LootItemCondition, SecretFlagMatcher {
    public static final MapCodec<SecretFlagLootItemCondition> CODEC = SecretFlagMatcher.createMapCodec(SecretFlagLootItemCondition::new);

    @Override
    public LootItemConditionType getType() {
        return ModLootTables.ItemConditions.SECRET_FLAG.get();
    }

    @Override
    public boolean test(LootContext context) {
        return matchesSecretFlag();
    }
}
