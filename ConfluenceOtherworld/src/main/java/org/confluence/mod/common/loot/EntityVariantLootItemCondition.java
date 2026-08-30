package org.confluence.mod.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.confluence.mod.common.init.ModLootTables;

import java.util.Locale;

/// 按实体当前变体的稳定名称筛选掉落。
///
/// 不使用枚举序号，因为不同实体可以用不同顺序声明同一组语义变体；名称条件能保证
/// 宝石兔和宝石松鼠都把相同外观映射到相同材料。
public record EntityVariantLootItemCondition(String variant) implements LootItemCondition {
    public static final MapCodec<EntityVariantLootItemCondition> CODEC = Codec.STRING
            .xmap(EntityVariantLootItemCondition::new, EntityVariantLootItemCondition::variant)
            .fieldOf("variant");

    @Override
    public LootItemConditionType getType() {
        return ModLootTables.ItemConditions.ENTITY_VARIANT.get();
    }

    @Override
    public boolean test(LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof VariantHolder<?> holder)) {
            return false;
        }
        Object value = holder.getVariant();
        if (value instanceof StringRepresentable named) {
            return variant.equals(named.getSerializedName());
        }
        return value instanceof Enum<?> enumValue
                && variant.equals(enumValue.name().toLowerCase(Locale.ROOT));
    }

    public static Builder of(String variant) {
        return () -> new EntityVariantLootItemCondition(variant);
    }
}
