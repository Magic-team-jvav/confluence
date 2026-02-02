package org.confluence.terra_curio.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;

public final class TCDataMaps {
    public static final AdvancedDataMapType<Item, PrimitiveValueComponent, PrimitiveValueComponent.Remover> ACCESSORIES = AdvancedDataMapType.builder(TerraCurio.asResource("accessories"), Registries.ITEM, PrimitiveValueComponent.CODEC)
            .synced(PrimitiveValueComponent.CODEC, false)
            .remover(PrimitiveValueComponent.Remover.CODEC)
            .merger(PrimitiveValueComponent.Merger.INSTANCE).build();

    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(ACCESSORIES);
    }
}
