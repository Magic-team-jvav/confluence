package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.confluence.mod.Confluence;

public final class ModLootTables {
    public static final ResourceKey<LootTable> WOODEN_CRATE = register("gameplay/crate/wooden_crate");
    public static final ResourceKey<LootTable> IRON_CRATE = register("gameplay/crate/iron_crate");
    public static final ResourceKey<LootTable> GOLDEN_CRATE = register("gameplay/crate/golden_crate");
    public static final ResourceKey<LootTable> JUNGLE_CRATE = register("gameplay/crate/jungle_crate");
    public static final ResourceKey<LootTable> SKY_CRATE = register("gameplay/crate/sky_crate");
    public static final ResourceKey<LootTable> CORRUPT_CRATE = register("gameplay/crate/corrupt_crate");
    public static final ResourceKey<LootTable> TR_CRIMSON_CRATE = register("gameplay/crate/tr_crimson_crate");
    public static final ResourceKey<LootTable> SACRED_CRATE = register("gameplay/crate/sacred_crate");
    public static final ResourceKey<LootTable> DUNGEON_CRATE = register("gameplay/crate/dungeon_crate");
    public static final ResourceKey<LootTable> FREEZE_CRATE = register("gameplay/crate/freeze_crate");
    public static final ResourceKey<LootTable> OASIS_CRATE = register("gameplay/crate/oasis_crate");
    public static final ResourceKey<LootTable> OBSIDIAN_CRATE = register("gameplay/crate/obsidian_crate");
    public static final ResourceKey<LootTable> OCEAN_CRATE = register("gameplay/crate/ocean_crate");

    public static final ResourceKey<LootTable> PEARLWOOD_CRATE = register("gameplay/crate/pearlwood_crate");
    public static final ResourceKey<LootTable> MITHRIL_CRATE = register("gameplay/crate/mithril_crate");
    public static final ResourceKey<LootTable> TITANIUM_CRATE = register("gameplay/crate/titanium_crate");
    public static final ResourceKey<LootTable> THORNS_CRATE = register("gameplay/crate/thorns_crate");
    public static final ResourceKey<LootTable> SPACE_CRATE = register("gameplay/crate/space_crate");
    public static final ResourceKey<LootTable> DEFACED_CRATE = register("gameplay/crate/defaced_crate");
    public static final ResourceKey<LootTable> BLOOD_CRATE = register("gameplay/crate/blood_crate");
    public static final ResourceKey<LootTable> PROVIDENTIAL_CRATE = register("gameplay/crate/providential_crate");
    public static final ResourceKey<LootTable> FENCING_CRATE = register("gameplay/crate/fencing_crate");
    public static final ResourceKey<LootTable> CONIFEROUS_WOOD_CRATE = register("gameplay/crate/coniferous_wood_crate");
    public static final ResourceKey<LootTable> ILLUSION_CRATE = register("gameplay/crate/illusion_crate");
    public static final ResourceKey<LootTable> HELL_STONE_CRATE = register("gameplay/crate/hell_stone_crate");
    public static final ResourceKey<LootTable> BEACH_CRATE = register("gameplay/crate/beach_crate");

    public static final ResourceKey<LootTable> CLAM = register("gameplay/clam");
    public static final ResourceKey<LootTable> CHRISTMAS_GIFT = register("gameplay/christmas_gift");
    public static final ResourceKey<LootTable> RED_ENVELOPE = register("gameplay/red_envelope");
    public static final ResourceKey<LootTable> HERB_BAG = register("gameplay/herb_bag");
    public static final ResourceKey<LootTable> CAN_OF_WORMS = register("gameplay/can_of_worms");

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
