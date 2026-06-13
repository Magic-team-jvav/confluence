package org.confluence.mod.common.init;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.loot.DateLootItemCondition;
import org.confluence.mod.common.loot.GamePhaseLootItemCondition;
import org.confluence.mod.common.loot.SecretFlagLootItemCondition;

public final class ModLootTables {
    public static final ResourceLocation WOODEN_CRATE = register("gameplay/crate/wooden_crate");
    public static final ResourceLocation IRON_CRATE = register("gameplay/crate/iron_crate");
    public static final ResourceLocation GOLDEN_CRATE = register("gameplay/crate/golden_crate");
    public static final ResourceLocation JUNGLE_CRATE = register("gameplay/crate/jungle_crate");
    public static final ResourceLocation SAVANNA_CRATE = register("gameplay/crate/savanna_crate");
    public static final ResourceLocation SKY_CRATE = register("gameplay/crate/sky_crate");
    public static final ResourceLocation CORRUPT_CRATE = register("gameplay/crate/corrupt_crate");
    public static final ResourceLocation CRIMSON_CRATE = register("gameplay/crate/crimson_crate");
    public static final ResourceLocation HALLOWED_CRATE = register("gameplay/crate/hallowed_crate");
    public static final ResourceLocation DUNGEON_CRATE = register("gameplay/crate/dungeon_crate");
    public static final ResourceLocation FROZEN_CRATE = register("gameplay/crate/frozen_crate");
    public static final ResourceLocation OASIS_CRATE = register("gameplay/crate/oasis_crate");
    public static final ResourceLocation OBSIDIAN_CRATE = register("gameplay/crate/obsidian_crate");
    public static final ResourceLocation OCEAN_CRATE = register("gameplay/crate/ocean_crate");

    public static final ResourceLocation PEARLWOOD_CRATE = register("gameplay/crate/pearlwood_crate");
    public static final ResourceLocation MYTHRIL_CRATE = register("gameplay/crate/mythril_crate");
    public static final ResourceLocation TITANIUM_CRATE = register("gameplay/crate/titanium_crate");
    public static final ResourceLocation BRAMBLE_CRATE = register("gameplay/crate/bramble_crate");
    public static final ResourceLocation WILD_CRATE = register("gameplay/crate/wild_crate");
    public static final ResourceLocation AZURE_CRATE = register("gameplay/crate/azure_crate");
    public static final ResourceLocation DEFILED_CRATE = register("gameplay/crate/defiled_crate");
    public static final ResourceLocation HEMATIC_CRATE = register("gameplay/crate/hematic_crate");
    public static final ResourceLocation DIVINE_CRATE = register("gameplay/crate/divine_crate");
    public static final ResourceLocation STOCKADE_CRATE = register("gameplay/crate/stockade_crate");
    public static final ResourceLocation BOREAL_CRATE = register("gameplay/crate/boreal_crate");
    public static final ResourceLocation MIRAGE_CRATE = register("gameplay/crate/mirage_crate");
    public static final ResourceLocation HELLSTONE_CRATE = register("gameplay/crate/hellstone_crate");
    public static final ResourceLocation SEASIDE_CRATE = register("gameplay/crate/seaside_crate");

    public static final ResourceLocation CLAM = register("gameplay/clam");
    public static final ResourceLocation PINE_CONE = register("gameplay/pine_cone");
    public static final ResourceLocation CHRISTMAS_GIFT = register("gameplay/christmas_gift");
    public static final ResourceLocation GOODIE_GIFT = register("gameplay/goodie_gift");
    public static final ResourceLocation RED_ENVELOPE = register("gameplay/red_envelope");
    public static final ResourceLocation SUGAR_TANGERINE = register("gameplay/sugar_tangerine");
    public static final ResourceLocation DELUXE_PACKAGE = register("gameplay/deluxe_package");
    public static final ResourceLocation HERB_BAG = register("gameplay/herb_bag");
    public static final ResourceLocation CAN_OF_WORMS = register("gameplay/can_of_worms");

    public static final ResourceLocation FISHING_LAVA = register("gameplay/fishing/lava");
    public static final ResourceLocation FISHING_HONEY = register("gameplay/fishing/honey");
    public static final ResourceLocation FISHING = register("gameplay/fishing");
    public static final ResourceLocation FISH = register("gameplay/fishing/fish");
    public static final ResourceLocation JUNK = register("gameplay/fishing/junk");
    public static final ResourceLocation TREASURE = register("gameplay/fishing/treasure");
    public static final ResourceLocation CRATE = register("gameplay/crate");
    public static final ResourceLocation CRATE_HARDMODE = register("gameplay/crate_hardmode");
    public static final ResourceLocation QUESTS_0 = register("gameplay/fishing_quests_0");
    public static final ResourceLocation QUESTS_1 = register("gameplay/fishing_quests_1");
    public static final ResourceLocation QUESTS_2 = register("gameplay/fishing_quests_2");
    public static final ResourceLocation QUESTS_3 = register("gameplay/fishing_quests_3");
    public static final ResourceLocation QUESTS_4 = register("gameplay/fishing_quests_4");
    public static final ResourceLocation QUESTS_AFTER_10 = register("gameplay/fishing_quests_after_10");
    public static final ResourceLocation QUESTS_AFTER_75 = register("gameplay/fishing_quests_after_75");

    public static final ResourceLocation CORRUPTION_CARRY = register("gameplay/corruption_carry");
    public static final ResourceLocation SLIME_CARRY = register("gameplay/slime_carry");

    public static final ResourceLocation HERB_BAG_INNER = register("gameplay/herb_bag_inner");

    public static final ResourceLocation SHEEP_RAINBOW_WOOL = register("entities/sheep_rainbow_wool");

    public static final ResourceLocation CAVE_CHESTS = register("chests/cave_chests");

    public static final ResourceLocation LIVING_MAHOGANY_CARRY = register("gameplay/living_mahogany_carry");

    public static final ResourceLocation OPAL_BLOCK = register("archaeology/opal_ore");

    public static final ResourceLocation GOLDEN_LOCK_BOX = register("gameplay/crate/golden_lock_box");
    public static final ResourceLocation OBSIDIAN_LOCK_BOX = register("gameplay/crate/obsidian_lock_box");

    private static ResourceLocation register(String name) {
        return Confluence.asResource(name);
    }

    public static class ItemConditions {
        public static final DeferredRegister<LootItemConditionType> TYPES = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, Confluence.MODID);

        public static final RegistryObject<LootItemConditionType> DATE = register("date", DateLootItemCondition.CODEC);
        public static final RegistryObject<LootItemConditionType> GAME_PHASE = register("game_phase", GamePhaseLootItemCondition.CODEC);
        public static final RegistryObject<LootItemConditionType> SECRET_FLAG = register("secret_flag", SecretFlagLootItemCondition.CODEC);

        private static <T extends LootItemCondition> RegistryObject<LootItemConditionType> register(String name, MapCodec<T> codec) {
            return TYPES.register(name, () -> new LootItemConditionType(new Serializer<T>() {
                @Override
                public void serialize(JsonObject json, T value, JsonSerializationContext serializationContext) {
                    codec.encode(value, JsonOps.INSTANCE, codec.compressedBuilder(JsonOps.INSTANCE)).build(json);
                }

                @Override
                public T deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
                    return PortDataResultExtension.getOrThrow(codec.compressedDecode(JsonOps.INSTANCE, json));
                }
            }));
        }
    }
}
