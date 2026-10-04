package org.confluence.mod.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModLootTables;

import java.util.Set;

public record DifficultyChanceLootItemCondition(float normalChance,
                                                float expertChance) implements LootItemCondition {
    public static final MapCodec<DifficultyChanceLootItemCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.floatRange(0, 1).fieldOf("normal_chance").forGetter(DifficultyChanceLootItemCondition::normalChance),
            Codec.floatRange(0, 1).fieldOf("expert_chance").forGetter(DifficultyChanceLootItemCondition::expertChance)
    ).apply(instance, DifficultyChanceLootItemCondition::new));

    @Override
    public LootItemConditionType getType() {
        return ModLootTables.ItemConditions.DIFFICULTY_CHANCE.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.ORIGIN);
    }

    @Override
    public boolean test(LootContext context) {
        boolean expert = LibUtils.isAtLeastExpert(context.getLevel(), BlockPos.containing(context.getParam(LootContextParams.ORIGIN)));
        return context.getRandom().nextFloat() < (expert ? expertChance : normalChance);
    }
}
