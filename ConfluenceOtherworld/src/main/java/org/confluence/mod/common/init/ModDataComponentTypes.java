package org.confluence.mod.common.init;

import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.*;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.mesdag.portlib.component.PortDataComponentType;
import org.mesdag.portlib.registries.PortDataComponentRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.registries.PortRegistryEntry;

public final class ModDataComponentTypes {
    public static final PortDataComponentRegistration TYPES = PortRegisterHandler.dataComponent(Confluence.MODID);

    public static final PortRegistryEntry<PortDataComponentType<?>, PortDataComponentType<LootComponent>> LOOT = TYPES.builder("loot", builder -> builder.persistent(LootComponent.CODEC).networkSynchronized(LootComponent.STREAM_CODEC));
    public static final PortRegistryEntry<PortDataComponentType<?>, PortDataComponentType<PrefixComponent>> PREFIX = TYPES.builder("prefix", builder -> builder.persistent(PrefixComponent.CODEC)); // 不能使用同步
    public static final PortRegistryEntry<PortDataComponentType<?>, PortDataComponentType<ValueComponent>> VALUE = TYPES.builder("value", builder -> builder.persistent(ValueComponent.CODEC).networkSynchronized(ValueComponent.STREAM_CODEC));
    public static final PortRegistryEntry<PortDataComponentType<?>, PortDataComponentType<SwordProjectileComponent>> SWORD_PROJECTILE = TYPES.builder("sword_projectile", builder -> builder.persistent(SwordProjectileComponent.CODEC).networkSynchronized(SwordProjectileComponent.STREAM_CODEC));
    public static final PortRegistryEntry<PortDataComponentType<?>, PortDataComponentType<SpearProjectileComponent>> SPEAR_PROJECTILE = TYPES.builder("spear_projectile", builder -> builder.persistent(SpearProjectileComponent.CODEC).networkSynchronized(SpearProjectileComponent.STREAM_CODEC));
    public static final PortRegistryEntry<PortDataComponentType<?>, PortDataComponentType<PrimitiveValueComponent>> ARMOR_BONUS = TYPES.builder("armor_bonus", builder -> builder.persistent(PrimitiveValueComponent.CODEC).networkSynchronized(PrimitiveValueComponent.STREAM_CODEC));
    public static final PortRegistryEntry<PortDataComponentType<?>, PortDataComponentType<RepeaterContents>> REPEATER_CONTENTS = TYPES.builder("repeater_contents", builder -> builder.persistent(RepeaterContents.CODEC).networkSynchronized(RepeaterContents.STREAM_CODEC));
    public static final PortRegistryEntry<PortDataComponentType<?>, PortDataComponentType<FlailComponent>> FLAIL = TYPES.builder("flail", builder -> builder.persistent(FlailComponent.CODEC).networkSynchronized(FlailComponent.STREAM_CODEC));

    // 枪械数据组件
    public static final PortRegistryEntry<PortDataComponentType<?>, PortDataComponentType<GunPropertyComponent>> GUN_PROPERTY = TYPES.builder("gun_property", GunPropertyComponent::fastBuilder);
    public static final PortRegistryEntry<PortDataComponentType<?>, PortDataComponentType<BulletPropertyComponent>> BULLET_PROPERTY = TYPES.builder("bullet_property", BulletPropertyComponent::fastBuilder);
}
