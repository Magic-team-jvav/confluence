package org.confluence.mod.common.init;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.LootComponent;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.item.GroupItem;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;

import java.util.function.Supplier;

public final class ModDataComponentTypes {
    public static final DeferredRegister.DataComponents TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Confluence.MODID);

    public static final Supplier<DataComponentType<LootComponent>> LOOT = TYPES.registerComponentType("loot", builder -> builder.persistent(LootComponent.CODEC).networkSynchronized(LootComponent.STREAM_CODEC));
    public static final Supplier<DataComponentType<PrefixComponent>> PREFIX = TYPES.registerComponentType("prefix", builder -> builder.persistent(PrefixComponent.CODEC)); // 不能使用同步
    public static final Supplier<DataComponentType<ValueComponent>> VALUE = TYPES.registerComponentType("value", builder -> builder.persistent(ValueComponent.CODEC).networkSynchronized(ValueComponent.STREAM_CODEC));
    public static final Supplier<DataComponentType<SwordProjectileComponent>> SWORD_PROJECTILE = TYPES.registerComponentType("sword_projectile", builder -> builder.persistent(SwordProjectileComponent.CODEC).networkSynchronized(SwordProjectileComponent.STREAM_CODEC));
    public static final Supplier<DataComponentType<GroupItem.Stacks>> GROUP_STACKS = TYPES.registerComponentType("group_stacks", builder -> builder.persistent(GroupItem.Stacks.CODEC).networkSynchronized(GroupItem.Stacks.STREAM_CODEC));
    public static final Supplier<DataComponentType<PrimitiveValueComponent>> ARMOR_BONUS = TYPES.registerComponentType("armor_bonus", builder -> builder.persistent(PrimitiveValueComponent.CODEC).networkSynchronized(PrimitiveValueComponent.STREAM_CODEC));
}
