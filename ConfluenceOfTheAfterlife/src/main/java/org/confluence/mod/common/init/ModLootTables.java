package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import org.confluence.mod.Confluence;

public final class ModLootTables {
    public static final ResourceKey<LootTable> WOODEN_BOX = register("wooden_box");
    public static final ResourceKey<LootTable> IRON_BOX = register("iron_box");
    public static final ResourceKey<LootTable> GOLDEN_BOX = register("golden_box");
    public static final ResourceKey<LootTable> JUNGLE_BOX = register("jungle_box");
    public static final ResourceKey<LootTable> SKY_BOX = register("sky_box");
    public static final ResourceKey<LootTable> CORRUPT_BOX = register("corrupt_box");
    public static final ResourceKey<LootTable> TR_CRIMSON_BOX = register("tr_crimson_box");
    public static final ResourceKey<LootTable> SACRED_BOX = register("sacred_box");
    public static final ResourceKey<LootTable> DUNGEON_BOX = register("dungeon_box");
    public static final ResourceKey<LootTable> FREEZE_BOX = register("freeze_box");
    public static final ResourceKey<LootTable> OASIS_BOX = register("oasis_box");
    public static final ResourceKey<LootTable> OBSIDIAN_BOX = register("obsidian_box");
    public static final ResourceKey<LootTable> OCEAN_BOX = register("ocean_box");

    public static final ResourceKey<LootTable> PEARLWOOD_BOX = register("pearlwood_box");
    public static final ResourceKey<LootTable> MITHRIL_BOX = register("mithril_box");
    public static final ResourceKey<LootTable> TITANIUM_BOX = register("titanium_box");
    public static final ResourceKey<LootTable> THORNS_BOX = register("thorns_box");
    public static final ResourceKey<LootTable> SPACE_BOX = register("space_box");
    public static final ResourceKey<LootTable> DEFACED_BOX = register("defaced_box");
    public static final ResourceKey<LootTable> BLOOD_BOX = register("blood_box");
    public static final ResourceKey<LootTable> PROVIDENTIAL_BOX = register("providential_box");
    public static final ResourceKey<LootTable> FENCING_BOX = register("fencing_box");
    public static final ResourceKey<LootTable> CONIFEROUS_WOOD_BOX = register("coniferous_wood_box");
    public static final ResourceKey<LootTable> ILLUSION_BOX = register("illusion_box");
    public static final ResourceKey<LootTable> HELL_STONE_BOX = register("hell_stone_box");
    public static final ResourceKey<LootTable> BEACH_BOX = register("beach_box");

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

    public static ResourceKey<LootTable> getOrCreate(ResourceLocation location) {
        return ResourceKey.create(Registries.LOOT_TABLE, location);
    }
}
