package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.data.map.ExtractinatorData;
import org.confluence.mod.common.data.map.TreasureBagDrop;

@EventBusSubscriber(modid = Confluence.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModDataMaps {
    public static final DataMapType<Item, ValueComponent> VALUE = DataMapType.builder(Confluence.asResource("value"), Registries.ITEM, ValueComponent.CODEC).synced(ValueComponent.CODEC, false).build();
    public static final DataMapType<Item, ExtractinatorData> EXTRACTINATOR = DataMapType.builder(Confluence.asResource("extractinator"), Registries.ITEM, ExtractinatorData.CODEC).build();
    public static final DataMapType<EntityType<?>, TreasureBagDrop> TREASURE_BAG = DataMapType.builder(Confluence.asResource("treasure_bag"), Registries.ENTITY_TYPE, TreasureBagDrop.CODEC).build();

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(VALUE);
        event.register(EXTRACTINATOR);
        event.register(TREASURE_BAG);
    }
}
