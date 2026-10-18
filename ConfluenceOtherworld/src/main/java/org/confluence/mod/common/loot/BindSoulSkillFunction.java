package org.confluence.mod.common.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModLootTables;

/// 1.20.1 的战利品函数同时更新 PortLib 技能组件和持久化数据。
public record BindSoulSkillFunction(ResourceLocation skillId) implements LootItemFunction {
    public static final MapCodec<BindSoulSkillFunction> CODEC = ResourceLocation.CODEC.fieldOf("skill").xmap(BindSoulSkillFunction::new, BindSoulSkillFunction::skillId);

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        stack.set(ModDataComponentTypes.SOUL_SKILL.get(), skillId);
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return ModLootTables.ItemFunctions.BIND_SOUL_SKILL.get();
    }

    public static LootItemFunction.Builder bind(ResourceLocation skillId) {
        return () -> new BindSoulSkillFunction(skillId);
    }
}
