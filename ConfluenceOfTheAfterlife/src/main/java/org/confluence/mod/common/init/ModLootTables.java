package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.confluence.mod.Confluence;

public final class ModLootTables {
    public static final ResourceKey<LootTable> WOODEN_BOX = register("gameplay/box/wooden_box");
    public static final ResourceKey<LootTable> IRON_BOX = register("gameplay/box/iron_box");
    public static final ResourceKey<LootTable> GOLDEN_BOX = register("gameplay/box/golden_box");
    public static final ResourceKey<LootTable> JUNGLE_BOX = register("gameplay/box/jungle_box");
    public static final ResourceKey<LootTable> SKY_BOX = register("gameplay/box/sky_box");
    public static final ResourceKey<LootTable> CORRUPT_BOX = register("gameplay/box/corrupt_box");
    public static final ResourceKey<LootTable> TR_CRIMSON_BOX = register("gameplay/box/tr_crimson_box");
    public static final ResourceKey<LootTable> SACRED_BOX = register("gameplay/box/sacred_box");
    public static final ResourceKey<LootTable> DUNGEON_BOX = register("gameplay/box/dungeon_box");
    public static final ResourceKey<LootTable> FREEZE_BOX = register("gameplay/box/freeze_box");
    public static final ResourceKey<LootTable> OASIS_BOX = register("gameplay/box/oasis_box");
    public static final ResourceKey<LootTable> OBSIDIAN_BOX = register("gameplay/box/obsidian_box");
    public static final ResourceKey<LootTable> OCEAN_BOX = register("gameplay/box/ocean_box");

    public static final ResourceKey<LootTable> PEARLWOOD_BOX = register("gameplay/box/pearlwood_box");
    public static final ResourceKey<LootTable> MITHRIL_BOX = register("gameplay/box/mithril_box");
    public static final ResourceKey<LootTable> TITANIUM_BOX = register("gameplay/box/titanium_box");
    public static final ResourceKey<LootTable> THORNS_BOX = register("gameplay/box/thorns_box");
    public static final ResourceKey<LootTable> SPACE_BOX = register("gameplay/box/space_box");
    public static final ResourceKey<LootTable> DEFACED_BOX = register("gameplay/box/defaced_box");
    public static final ResourceKey<LootTable> BLOOD_BOX = register("gameplay/box/blood_box");
    public static final ResourceKey<LootTable> PROVIDENTIAL_BOX = register("gameplay/box/providential_box");
    public static final ResourceKey<LootTable> FENCING_BOX = register("gameplay/box/fencing_box");
    public static final ResourceKey<LootTable> CONIFEROUS_WOOD_BOX = register("gameplay/box/coniferous_wood_box");
    public static final ResourceKey<LootTable> ILLUSION_BOX = register("gameplay/box/illusion_box");
    public static final ResourceKey<LootTable> HELL_STONE_BOX = register("gameplay/box/hell_stone_box");
    public static final ResourceKey<LootTable> BEACH_BOX = register("gameplay/box/beach_box");

    public static final ResourceKey<LootTable> FISHING_LAVA = register("gameplay/fishing/lava");
    public static final ResourceKey<LootTable> FISH = register("gameplay/fishing");

    public static final ResourceKey<LootTable> EXTRACT_DESERT_FOSSIL = register("gameplay/extract/with_desert_fossil");
    public static final ResourceKey<LootTable> EXTRACT_GRAVEL = register("gameplay/extract/with_gravel");
    public static final ResourceKey<LootTable> EXTRACT_JUNK = register("gameplay/extract/with_junk");
    public static final ResourceKey<LootTable> EXTRACT_SLUSH = register("gameplay/extract/with_slush");
    public static final ResourceKey<LootTable> EXTRACT_MARINE_GRAVEL = register("gameplay/extract/with_marine_gravel");


    public static ResourceKey<LootTable> register(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, Confluence.asResource(name));
    }
}
