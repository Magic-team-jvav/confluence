package org.confluence.mod.common.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.data.map.*;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

@EventBusSubscriber(modid = Confluence.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModDataMaps {
    private static List<DataMapType<?, ?>> types = new LinkedList<>();
    private static final boolean jei = ModList.get().isLoaded("jei");

    public static final DataMapType<Item, ValueComponent> VALUE = register("value", Registries.ITEM, ValueComponent.CODEC, true);
    public static final DataMapType<Item, ExtractinatorData> EXTRACTINATOR = register("extractinator", Registries.ITEM, ExtractinatorData.CODEC, jei);
    public static final DataMapType<Item, ExtractinatorData> CHLOROPHYTE_EXTRACTINATOR = register("chlorophyte_extractinator", Registries.ITEM, ExtractinatorData.CODEC, jei);
    public static final DataMapType<Item, DiggingPower> DIGGING_POWER = register("digging_power", Registries.ITEM, DiggingPower.CODEC, true);
    public static final DataMapType<EntityType<?>, TreasureBagDrop> TREASURE_BAG = register("treasure_bag", Registries.ENTITY_TYPE, TreasureBagDrop.CODEC, false);
    public static final DataMapType<EntityType<?>, ImmunityDataMap> IMMUNITY = register("immunity", Registries.ENTITY_TYPE, ImmunityDataMap.CODEC, true);
    public static final DataMapType<EntityType<?>, BugNetEntityToItem> BUG_NET_ENTITY_TO_ITEM = register("bug_net_entity_to_item", Registries.ENTITY_TYPE, BugNetEntityToItem.CODEC, false);
    public static final DataMapType<EntityType<?>, LivingInvulnerableEffects> LIVING_INVULNERABLE_EFFECTS = register("living_invulnerable_effects", Registries.ENTITY_TYPE, LivingInvulnerableEffects.CODEC, true);
    public static final DataMapType<Block, BlockBreakSpawns> BLOCK_BREAK_SPAWNS = register("block_break_spawns", Registries.BLOCK, BlockBreakSpawns.CODEC, false);

    private static <R, T> DataMapType<R, T> register(String path, ResourceKey<Registry<R>> resourceKey, Codec<T> codec, boolean synced) {
        DataMapType.Builder<T, R> builder = DataMapType.builder(Confluence.asResource(path), resourceKey, codec);
        if (synced) builder.synced(codec, false);
        DataMapType<R, T> type = builder.build();
        types.add(type);
        return type;
    }

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        for (DataMapType<?, ?> type : types) {
            event.register(type);
        }
        types = null;
    }

    public static <T> @Nullable T getEntityData(DataMapType<EntityType<?>, T> type, Entity entity) {
        return BuiltInRegistries.ENTITY_TYPE.getData(type, entity.getType().builtInRegistryHolder().unwrapKey().orElseThrow());
    }
}
