package org.confluence.mod.common.init;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.LootComponent;
import org.confluence.mod.common.component.SingleBooleanComponent;
import org.confluence.mod.common.component.prefix.PrefixComponent;

import java.util.function.Supplier;

public final class ModDataComponentTypes {
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPE = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Confluence.MODID);

    public static final Supplier<DataComponentType<LootComponent>> LOOT = DATA_COMPONENT_TYPE.registerComponentType(
            "loot", builder -> builder.persistent(LootComponent.CODEC).networkSynchronized(LootComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<SingleBooleanComponent>> BOOMERANG_READY = DATA_COMPONENT_TYPE.registerComponentType(
            "boomerang_ready", builder -> builder.persistent(SingleBooleanComponent.CODEC).networkSynchronized(SingleBooleanComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<PrefixComponent>> PREFIX = DATA_COMPONENT_TYPE.registerComponentType(
            "prefix", builder -> builder.persistent(PrefixComponent.CODEC) // 不能使用networkSynchronized
    );
}
