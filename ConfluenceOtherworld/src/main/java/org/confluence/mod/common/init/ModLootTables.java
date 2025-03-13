package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootTable;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.integration.jei.JeiHelper;

import java.util.ArrayList;
import java.util.List;

public final class ModLootTables {
    public static final List<Data> LOOT_TABLES = new ArrayList<>();

    public static final ResourceKey<LootTable> WOODEN_CRATE = register("gameplay/crate/wooden_crate");
    public static final ResourceKey<LootTable> IRON_CRATE = register("gameplay/crate/iron_crate");
    public static final ResourceKey<LootTable> GOLDEN_CRATE = register("gameplay/crate/golden_crate");
    public static final ResourceKey<LootTable> JUNGLE_CRATE = register("gameplay/crate/jungle_crate");
    public static final ResourceKey<LootTable> SAVANNA_CRATE = register("gameplay/crate/savanna_crate");
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
    public static final ResourceKey<LootTable> WILD_CRATE = register("gameplay/crate/wild_crate");
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
    public static final ResourceKey<LootTable> GOODIE_GIFT = register("gameplay/goodie_gift");
    public static final ResourceKey<LootTable> RED_ENVELOPE = register("gameplay/red_envelope");
    public static final ResourceKey<LootTable> SUGAR_TANGERINE = register("gameplay/sugar_tangerine");
    public static final ResourceKey<LootTable> DELUXE_PACKAGE = register("gameplay/deluxe_package");
    public static final ResourceKey<LootTable> HERB_BAG = register("gameplay/herb_bag");
    public static final ResourceKey<LootTable> CAN_OF_WORMS = register("gameplay/can_of_worms");

    public static final ResourceKey<LootTable> FISHING_LAVA = register("gameplay/fishing/lava");
    public static final ResourceKey<LootTable> FISH = register("gameplay/fishing");
    public static final ResourceKey<LootTable> CRATE = register("gameplay/crate");

    public static final ResourceKey<LootTable> EXTRACT_DESERT_FOSSIL = register("gameplay/extract/with_desert_fossil");
    public static final ResourceKey<LootTable> EXTRACT_GRAVEL = register("gameplay/extract/with_gravel");
    public static final ResourceKey<LootTable> EXTRACT_JUNK = register("gameplay/extract/with_junk");
    public static final ResourceKey<LootTable> EXTRACT_SLUSH = register("gameplay/extract/with_slush");
    public static final ResourceKey<LootTable> EXTRACT_MARINE_GRAVEL = register("gameplay/extract/with_marine_gravel");

    public static final ResourceKey<LootTable> CAVE_CHESTS = register("chests/cave_chests");

    public static final ResourceKey<LootTable> OPAL_BLOCK = register("archaeology/opal_ore");


    private static ResourceKey<LootTable> register(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, Confluence.asResource(name));
    }

    public static void registerDataForClient() {
        if (JeiHelper.IS_LOADED) {
            registerData(ConsumableItems.CLAM.get(),
                    new Stack(MaterialItems.PEARL.get(), 1),
                    new Stack(MaterialItems.BLACK_PEARL.get(), 1),
                    new Stack(MaterialItems.PINK_PEARL.get(), 1),
                    new Stack(FoodItems.SHUCKED_OYSTER.get(), 1)
            );
            registerData(ConsumableItems.RED_ENVELOPE.get(),
                    new Stack(ModItems.COPPER_COIN.get(), 8),
                    new Stack(ModItems.SILVER_COIN.get(), 8),
                    new Stack(ModItems.GOLDEN_COIN.get(), 8)
            );
        }
    }

    private static void registerData(ItemLike itemLike, Stack... stacks) {
        LOOT_TABLES.add(new Data(new ItemStack(itemLike), stacks));
    }

    public record Data(ItemStack itemStack, Stack[] stacks) {}

    public record Stack(ItemStack itemStack, int min, int max) {
        public Stack(ItemLike itemLike, int min, int max) {
            this(new ItemStack(itemLike), min, max);
        }

        public Stack(ItemLike itemLike, int count) {
            this(new ItemStack(itemLike), count, count);
        }
    }
}
