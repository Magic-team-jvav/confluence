package org.confluence.mod.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.loot.DateLootItemCondition;
import org.confluence.mod.common.loot.GamePhaseLootItemCondition;
import org.confluence.mod.common.loot.SecretFlagLootItemCondition;

import java.util.function.Supplier;

public final class ModLootTables {
    public static final ResourceKey<LootTable> WOODEN_CRATE = register("gameplay/crate/wooden_crate");
    public static final ResourceKey<LootTable> IRON_CRATE = register("gameplay/crate/iron_crate");
    public static final ResourceKey<LootTable> GOLDEN_CRATE = register("gameplay/crate/golden_crate");
    public static final ResourceKey<LootTable> JUNGLE_CRATE = register("gameplay/crate/jungle_crate");
    public static final ResourceKey<LootTable> SAVANNA_CRATE = register("gameplay/crate/savanna_crate");
    public static final ResourceKey<LootTable> SKY_CRATE = register("gameplay/crate/sky_crate");
    public static final ResourceKey<LootTable> CORRUPT_CRATE = register("gameplay/crate/corrupt_crate");
    public static final ResourceKey<LootTable> CRIMSON_CRATE = register("gameplay/crate/crimson_crate");
    public static final ResourceKey<LootTable> HALLOWED_CRATE = register("gameplay/crate/hallowed_crate");
    public static final ResourceKey<LootTable> DUNGEON_CRATE = register("gameplay/crate/dungeon_crate");
    public static final ResourceKey<LootTable> FROZEN_CRATE = register("gameplay/crate/frozen_crate");
    public static final ResourceKey<LootTable> OASIS_CRATE = register("gameplay/crate/oasis_crate");
    public static final ResourceKey<LootTable> OBSIDIAN_CRATE = register("gameplay/crate/obsidian_crate");
    public static final ResourceKey<LootTable> OCEAN_CRATE = register("gameplay/crate/ocean_crate");

    public static final ResourceKey<LootTable> PEARLWOOD_CRATE = register("gameplay/crate/pearlwood_crate");
    public static final ResourceKey<LootTable> MYTHRIL_CRATE = register("gameplay/crate/mythril_crate");
    public static final ResourceKey<LootTable> TITANIUM_CRATE = register("gameplay/crate/titanium_crate");
    public static final ResourceKey<LootTable> BRAMBLE_CRATE = register("gameplay/crate/bramble_crate");
    public static final ResourceKey<LootTable> WILD_CRATE = register("gameplay/crate/wild_crate");
    public static final ResourceKey<LootTable> AZURE_CRATE = register("gameplay/crate/azure_crate");
    public static final ResourceKey<LootTable> DEFILED_CRATE = register("gameplay/crate/defiled_crate");
    public static final ResourceKey<LootTable> HEMATIC_CRATE = register("gameplay/crate/hematic_crate");
    public static final ResourceKey<LootTable> DIVINE_CRATE = register("gameplay/crate/divine_crate");
    public static final ResourceKey<LootTable> STOCKADE_CRATE = register("gameplay/crate/stockade_crate");
    public static final ResourceKey<LootTable> BOREAL_CRATE = register("gameplay/crate/boreal_crate");
    public static final ResourceKey<LootTable> MIRAGE_CRATE = register("gameplay/crate/mirage_crate");
    public static final ResourceKey<LootTable> HELLSTONE_CRATE = register("gameplay/crate/hellstone_crate");
    public static final ResourceKey<LootTable> SEASIDE_CRATE = register("gameplay/crate/seaside_crate");

    public static final ResourceKey<LootTable> CLAM = register("gameplay/clam");
    public static final ResourceKey<LootTable> PINE_CONE = register("gameplay/pine_cone");
    public static final ResourceKey<LootTable> CHRISTMAS_GIFT = register("gameplay/christmas_gift");
    public static final ResourceKey<LootTable> GOODIE_GIFT = register("gameplay/goodie_gift");
    public static final ResourceKey<LootTable> RED_ENVELOPE = register("gameplay/red_envelope");
    public static final ResourceKey<LootTable> SUGAR_TANGERINE = register("gameplay/sugar_tangerine");
    public static final ResourceKey<LootTable> DELUXE_PACKAGE = register("gameplay/deluxe_package");
    public static final ResourceKey<LootTable> HERB_BAG = register("gameplay/herb_bag");
    public static final ResourceKey<LootTable> CAN_OF_WORMS = register("gameplay/can_of_worms");

    public static final ResourceKey<LootTable> FISHING_LAVA = register("gameplay/fishing/lava");
    public static final ResourceKey<LootTable> FISHING_HONEY = register("gameplay/fishing/honey");
    public static final ResourceKey<LootTable> FISHING = register("gameplay/fishing");
    public static final ResourceKey<LootTable> FISH = register("gameplay/fishing/fish");
    public static final ResourceKey<LootTable> JUNK = register("gameplay/fishing/junk");
    public static final ResourceKey<LootTable> TREASURE = register("gameplay/fishing/treasure");
    public static final ResourceKey<LootTable> CRATE = register("gameplay/crate");
    public static final ResourceKey<LootTable> CRATE_HARDMODE = register("gameplay/crate_hardmode");
    public static final ResourceKey<LootTable> QUESTS_0 = register("gameplay/fishing_quests_0");
    public static final ResourceKey<LootTable> QUESTS_1 = register("gameplay/fishing_quests_1");
    public static final ResourceKey<LootTable> QUESTS_2 = register("gameplay/fishing_quests_2");
    public static final ResourceKey<LootTable> QUESTS_3 = register("gameplay/fishing_quests_3");
    public static final ResourceKey<LootTable> QUESTS_4 = register("gameplay/fishing_quests_4");
    public static final ResourceKey<LootTable> QUESTS_AFTER_10 = register("gameplay/fishing_quests_after_10");
    public static final ResourceKey<LootTable> QUESTS_AFTER_75 = register("gameplay/fishing_quests_after_75");

    public static final ResourceKey<LootTable> CORRUPTION_CARRY = register("gameplay/corruption_carry");
    public static final ResourceKey<LootTable> SLIME_CARRY = register("gameplay/slime_carry");

    public static final ResourceKey<LootTable> HERB_BAG_INNER = register("gameplay/herb_bag_inner");

    public static final ResourceKey<LootTable> SHEEP_RAINBOW_WOOL = register("entities/sheep_rainbow_wool");

    public static final ResourceKey<LootTable> CAVE_CHESTS = register("chests/cave_chests");

    public static final ResourceKey<LootTable> LIVING_MAHOGANY_CARRY = register("gameplay/living_mahogany_carry");

    public static final ResourceKey<LootTable> OPAL_BLOCK = register("archaeology/opal_ore");

    public static final ResourceKey<LootTable> GOLDEN_LOCK_BOX = register("gameplay/crate/golden_lock_box");
    public static final ResourceKey<LootTable> OBSIDIAN_LOCK_BOX = register("gameplay/crate/obsidian_lock_box");

    private static ResourceKey<LootTable> register(String name) {
        return Confluence.asResourceKey(Registries.LOOT_TABLE, name);
    }

    public static class ItemConditions {
        public static final DeferredRegister<LootItemConditionType> TYPES = DeferredRegister.create(BuiltInRegistries.LOOT_CONDITION_TYPE, Confluence.MODID);

        public static final Supplier<LootItemConditionType> DATE = TYPES.register("date", () -> new LootItemConditionType(DateLootItemCondition.CODEC));
        public static final Supplier<LootItemConditionType> GAME_PHASE = TYPES.register("game_phase", () -> new LootItemConditionType(GamePhaseLootItemCondition.CODEC));
        public static final Supplier<LootItemConditionType> SECRET_FLAG = TYPES.register("secret_flag", () -> new LootItemConditionType(SecretFlagLootItemCondition.CODEC));
    }
}
