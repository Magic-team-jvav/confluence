package org.confluence.mod.common.init.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluids;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.component.LootComponent;
import org.confluence.mod.common.entity.boss.*;
import org.confluence.mod.common.entity.projectile.SparkleSlimeBalloonProjectile;
import org.confluence.mod.common.entity.projectile.ThrownWaterProjectile;
import org.confluence.mod.common.entity.projectile.bomb.*;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.gameevent.GoblinArmyGameEvent;
import org.confluence.mod.common.gameevent.PirateInvasionGameEvent;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.common.*;
import org.confluence.mod.common.item.mana.ArcaneCrystalItem;
import org.confluence.mod.common.item.mana.ManaCrystalItem;
import org.confluence.mod.common.item.mana.RecallManaCrystalItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class ConsumableItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<ManaCrystalItem> MANA_CRYSTAL = registerItem("mana_crystal", "魔力水晶", ManaCrystalItem::new);
    public static final PortDeferredItem<RecallManaCrystalItem> RECALL_MANA_CRYSTAL = registerItem("recall_mana_crystal", "回溯魔力水晶", RecallManaCrystalItem::new);
    public static final PortDeferredItem<EverBeneficialItem> LIFE_CRYSTAL = registerItem("life_crystal", "生命水晶", () -> new EverBeneficialItem(ModRarity.GREEN, PermanentUpgrade.LIFE_CRYSTAL, ModSoundEvents.LIFE_CRYSTAL_USE));
    public static final PortDeferredItem<EverBeneficialItem> RECALL_LIFE_CRYSTAL = registerItem("recall_life_crystal", "回溯生命水晶", () -> new EverBeneficialItem(ModRarity.GREEN, PermanentUpgrade.RECALL_LIFE_CRYSTAL, ModSoundEvents.LIFE_CRYSTAL_USE));
    public static final PortDeferredItem<EverBeneficialItem> LIFE_FRUIT = registerItem("life_fruit", "生命果", () -> new EverBeneficialItem(ModRarity.LIME, PermanentUpgrade.LIFE_FRUITS, ModSoundEvents.LIFE_CRYSTAL_USE));
    public static final PortDeferredItem<EverBeneficialItem> VITAL_CRYSTAL = registerItem("vital_crystal", "活力水晶", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, PermanentUpgrade.VITAL_CRYSTAL, ModSoundEvents.TRANSMUTATION_USE));
    public static final PortDeferredItem<ArcaneCrystalItem> ARCANE_CRYSTAL = registerItem("arcane_crystal", "奥术水晶", ArcaneCrystalItem::new);
    public static final PortDeferredItem<EverBeneficialItem> AEGIS_APPLE = registerItem("aegis_apple", "神盾果", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, PermanentUpgrade.AEGIS_APPLE, ModSoundEvents.TRANSMUTATION_USE));
    public static final PortDeferredItem<EverBeneficialItem> AMBROSIA = registerItem("ambrosia", "仙馔密酒", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, PermanentUpgrade.AMBROSIA, ModSoundEvents.TRANSMUTATION_USE));
    public static final PortDeferredItem<EverBeneficialItem> GUMMY_WORM = registerItem("gummy_worm", "黏性蠕虫", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, PermanentUpgrade.GUMMY_WORM, ModSoundEvents.TRANSMUTATION_USE));
    public static final PortDeferredItem<EverBeneficialItem> GALAXY_PEARL = registerItem("galaxy_pearl", "星系珍珠", () -> new EverBeneficialItem(ModRarity.LIGHT_PURPLE, PermanentUpgrade.GALAXY_PEARL, ModSoundEvents.TRANSMUTATION_USE));
    public static final PortDeferredItem<EverBeneficialItem> MINECART_UPGRADE_KIT = registerItem("minecart_upgrade_kit", "矿车升级包", () -> new EverBeneficialItem(ModRarity.EXPERT, PermanentUpgrade.MINECART_UPGRADE_KIT, ModSoundEvents.TRANSMUTATION_USE));
    public static final PortDeferredItem<EverBeneficialItem> ARTISAN_LOAF = registerItem("artisan_loaf", "工匠面包", () -> new EverBeneficialItem(ModRarity.ORANGE, PermanentUpgrade.ARTISAN_LOAF, ModSoundEvents.TRANSMUTATION_USE));
    public static final PortDeferredItem<AdvancedCombatTechniquesItem> ADVANCED_COMBAT_TECHNIQUES = registerItem("advanced_combat_techniques", "先进战斗技术", AdvancedCombatTechniquesItem::new);
    public static final PortDeferredItem<AdvancedCombatTechniquesVolumeTwoItem> ADVANCED_COMBAT_TECHNIQUES_VOLUME_TWO = registerItemWithEnglishName("advanced_combat_techniques_volume_two", "先进战斗技术：卷二", "Advanced Combat Techniques: Volume Two", AdvancedCombatTechniquesVolumeTwoItem::new);
    public static final PortDeferredItem<PeddlersSatchelItem> PEDDLERS_SATCHEL = registerItemWithEnglishName("peddlers_satchel", "商贩背包", "Peddler's Satchel", PeddlersSatchelItem::new);
//    public static final PortDeferredItem<EverBeneficialItem> FALLEN_SOUL_CORE = ITEMS.register("fallen_soul_core", () -> new EverBeneficialItem(ModRarity.BLUE, EverBeneficialItem.FALLEN_SOUL_CORE, ModSoundEvents.LIFE_CRYSTAL_USE, TooltipItem.getTooltipsFromString("fallen_soul_core", 1, ChatFormatting.GREEN)));

    public static final PortDeferredItem<ThrowableItem<BaseBombEntity>> BOMB = registerItem("bomb", "炸弹", () -> new ThrowableItem<>(0.8F, BaseBombEntity::new));
    public static final PortDeferredItem<ThrowableItem<BouncyBombEntity>> BOUNCY_BOMB = registerItem("bouncy_bomb", "弹力炸弹", () -> new ThrowableItem<>(0.8F, BouncyBombEntity::new));
    public static final PortDeferredItem<ThrowableItem<StickyBombEntity>> STICKY_BOMB = registerItem("sticky_bomb", "粘性炸弹", () -> new ThrowableItem<>(0.8F, StickyBombEntity::new));
    public static final PortDeferredItem<ThrowableItem<SmokeBombEntity>> SMOKE_BOMB = registerItem("smoke_bomb", "烟雾弹", () -> new ThrowableItem<>(0.8F, SmokeBombEntity::new));
    public static final PortDeferredItem<ThrowableItem<SparkleSlimeBalloonProjectile>> SPARKLE_SLIME_BALLOON = registerItem("sparkle_slime_balloon", "闪耀史莱姆气球", () -> new ThrowableItem<>(0.8F, SparkleSlimeBalloonProjectile::new));
    public static final PortDeferredItem<ThrowableItem<BombFishEntity>> BOMB_FISH = registerItem("bomb_fish", "炸弹鱼", () -> new ThrowableItem<>(0.8F, BombFishEntity::new));
    public static final PortDeferredItem<ThrowableItem<ScarabBombEntity>> SCARAB_BOMB = registerItem("scarab_bomb", "甲虫炸弹", () -> new ThrowableItem<>(0.8F, ScarabBombEntity::new));
    public static final PortDeferredItem<ThrowableItem<BaseDynamiteEntity>> DYNAMITE = registerItem("dynamite", "雷管", () -> new ThrowableItem<>(0.75F, BaseDynamiteEntity::new));
    public static final PortDeferredItem<ThrowableItem<BouncyDynamiteEntity>> BOUNCY_DYNAMITE = registerItem("bouncy_dynamite", "弹力雷管", () -> new ThrowableItem<>(0.75F, BouncyDynamiteEntity::new));
    public static final PortDeferredItem<ThrowableItem<StickyDynamiteEntity>> STICKY_DYNAMITE = registerItem("sticky_dynamite", "粘性雷管", () -> new ThrowableItem<>(0.75F, StickyDynamiteEntity::new));
    public static final PortDeferredItem<ThrowableItem<BaseGrenadeEntity>> GRENADE = registerItem("grenade", "手榴弹", () -> new ThrowableItem<>(0.7F, BaseGrenadeEntity::new));
    public static final PortDeferredItem<ThrowableItem<BouncyGrenadeEntity>> BOUNCY_GRENADE = registerItem("bouncy_grenade", "弹力手榴弹", () -> new ThrowableItem<>(0.7F, BouncyGrenadeEntity::new));
    public static final PortDeferredItem<ThrowableItem<StickyGrenadeEntity>> STICKY_GRENADE = registerItem("sticky_grenade", "粘性手榴弹", () -> new ThrowableItem<>(0.7F, StickyGrenadeEntity::new));
    public static final PortDeferredItem<ThrowableItem<BeenadeEntity>> BEENADE = registerItem("beenade", "蜜蜂手榴弹", () -> new ThrowableItem<>(0.7F, BeenadeEntity::new));
    public static final PortDeferredItem<ThrowableItem<BaseDirtBombEntity>> DIRT_BOMB = registerItem("dirt_bomb", "土炸弹", () -> new ThrowableItem<>(0.8F, BaseDirtBombEntity::new));
    public static final PortDeferredItem<ThrowableItem<StickyDirtBombEntity>> STICKY_DIRT_BOMB = registerItem("sticky_dirt_bomb", "粘性土炸弹", () -> new ThrowableItem<>(0.8F, StickyDirtBombEntity::new));
    public static final PortDeferredItem<ThrowableItem<DryBombEntity>> DRY_BOMB = registerItem("dry_bomb", "干炸弹", () -> new ThrowableItem<>(0.8F, DryBombEntity::new));
    public static final PortDeferredItem<ThrowableItem<LiquidBombEntity>> WET_BOMB = registerItem("wet_bomb", "湿炸弹", () -> new ThrowableItem<>(0.8F, player -> new LiquidBombEntity(ModEntities.WET_BOMB.get(), player, Fluids.WATER, 3)));
    public static final PortDeferredItem<ThrowableItem<LiquidBombEntity>> LAVA_BOMB = registerItem("lava_bomb", "熔岩炸弹", () -> new ThrowableItem<>(0.8F, player -> new LiquidBombEntity(ModEntities.LAVA_BOMB.get(), player, Fluids.LAVA, 3)));
    public static final PortDeferredItem<ThrowableItem<LiquidBombEntity>> HONEY_BOMB = registerItem("honey_bomb", "蜂蜜炸弹", () -> new ThrowableItem<>(0.8F, player -> new LiquidBombEntity(ModEntities.HONEY_BOMB.get(), player, ModFluids.HONEY.fluid().get(), 3)));
    public static final PortDeferredItem<ThrowableDropSelfItem> SHURIKEN = registerItem("shuriken", "手里剑", () -> new ThrowableDropSelfItem(ModEntities.SHURIKEN::get, 4.2f, 1.2f, 0.5f, 5, 3, true));
    public static final PortDeferredItem<ThrowableDropSelfItem> THROWING_KNIVE = registerItem("throwing_knive", "投刀", () -> new ThrowableDropSelfItem(ModEntities.THROWN_KNIVE::get, 5f, 1.2f, 0.5f, 5, 3, true));
    public static final PortDeferredItem<ThrowableDropSelfItem> BONE_THROWING_KNIFE = registerItem("bone_throwing_knife", "骨投刀", () -> new ThrowableDropSelfItem(ModEntities.BONE_THROWN_KNIVE::get, 6f, 1.2f, 0.5f, 5, 3, false));
    public static final PortDeferredItem<ThrowableDropSelfItem> FROST_DAGGERFISH = registerItem("frost_daggerfish", "寒霜飞鱼", () -> new ThrowableDropSelfItem(ModEntities.FROST_DAGGERFISH::get, 7f, 1.7f, 0.5f, 5, 3, false));
    public static final PortDeferredItem<ThrowableDropSelfItem> DUNGEON_DEMON_BONE = registerItem("dungeon_demon_bone", "地牢魔骨", () -> new ThrowableDropSelfItem(ModEntities.DUNGEON_DEMON_BONE::get, 5f, 1.2f, 0.5f, 3, 3, false));
    public static final PortDeferredItem<ThrowableDropSelfItem> JAVELIN = registerItem("javelin", "标枪", () -> new ThrowableDropSelfItem(ModEntities.JAVELIN::get, 5f, 1.2f, 0.5f, 5, 5, true));
    public static final PortDeferredItem<SpikyBallItem> SPIKY_BALL = registerItem("spiky_ball", "尖球", SpikyBallItem::new);
    public static final PortDeferredItem<ThrowableItem<ThrownWaterProjectile>> HOLY_WATER = registerItem("holy_water", "圣水", () -> new ThrowableItem<>(0.8F, player -> new ThrownWaterProjectile(player, ISpreadable.Type.PURE)));
    public static final PortDeferredItem<ThrowableItem<ThrownWaterProjectile>> UNHOLY_WATER = registerItem("unholy_water", "邪水", () -> new ThrowableItem<>(0.8F, player -> new ThrownWaterProjectile(player, ISpreadable.Type.CORRUPT)));
    public static final PortDeferredItem<ThrowableItem<ThrownWaterProjectile>> BLOOD_WATER = registerItem("blood_water", "血水", () -> new ThrowableItem<>(0.8F, player -> new ThrownWaterProjectile(player, ISpreadable.Type.CRIMSON)));

    public static final PortDeferredItem<RightClickLootItem> CLAM = registerItem("clam", "牡蛎", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.CLAM));
    public static final PortDeferredItem<RightClickLootItem> PINE_CONE = registerItem("pine_cone", "松果", () -> new RightClickLootItem(ModRarity.WHITE, ModLootTables.PINE_CONE));
    public static final PortDeferredItem<RightClickLootItem> HERB_BAG = registerItem("herb_bag", "草药袋", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.HERB_BAG));
    public static final PortDeferredItem<RightClickLootItem> CAN_OF_WORMS = registerItem("can_of_worms", "蠕虫罐头", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.CAN_OF_WORMS));
    public static final PortDeferredItem<RightClickLootItem> RED_ENVELOPE = registerItem("red_envelope", "红包", () -> new RightClickLootItem(ModRarity.RED, ModLootTables.RED_ENVELOPE));
    public static final PortDeferredItem<RightClickLootItem> SUGAR_TANGERINE = registerItem("sugar_tangerine", "砂糖橘", () -> new RightClickLootItem(ModRarity.RED, ModLootTables.SUGAR_TANGERINE));
    public static final PortDeferredItem<RightClickLootItem> DELUXE_PACKAGE = registerItem("deluxe_package", "豪华套餐", () -> new RightClickLootItem(ModRarity.RED, ModLootTables.DELUXE_PACKAGE));
    public static final PortDeferredItem<RightClickLootItem> GOODIE_BAG = registerItem("goodie_bag", "礼袋", () -> new RightClickLootItem(ModRarity.ORANGE, ModLootTables.GOODIE_GIFT));
    public static final PortDeferredItem<RightClickLootItem> CHRISTMAS_GIFT = registerItem("christmas_gift", "圣诞礼物", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.CHRISTMAS_GIFT));

    public static final PortDeferredItem<ThrownPowderItem> PURIFICATION_POWDER = registerItem("purification_powder", "净化粉", () -> new ThrownPowderItem(ISpreadable.Type.PURE));
    public static final PortDeferredItem<ThrownPowderItem> VILE_POWDER = registerItem("vile_powder", "魔粉", () -> new ThrownPowderItem(ISpreadable.Type.CORRUPT));
    public static final PortDeferredItem<ThrownPowderItem> VICIOUS_POWDER = registerItem("vicious_powder", "毒粉", () -> new ThrownPowderItem(ISpreadable.Type.CRIMSON));
    public static final PortDeferredItem<FertilizerItem> FERTILIZER = registerItem("fertilizer", "肥料", FertilizerItem::new);
    public static final PortDeferredItem<ModBoneMealItem> ROTTEN_BONE_DUST = registerItem("rotten_bone_dust", "朽骨齑尘", () -> new ModBoneMealItem(ModRarity.BLUE, "rotten_bone_dust"));
    public static final PortDeferredItem<ModBoneMealItem> BLOODSTAINED_POWDER = registerItem("bloodstained_powder", "血凝痂粉", () -> new ModBoneMealItem(ModRarity.BLUE, "bloodstained_powder"));

    public static final PortDeferredItem<BossSummoningItem> SUSPICIOUS_LOOKING_EYE = registerItem("suspicious_looking_eye", "可疑眼球", () -> new BossSummoningItem(player -> LibDateUtils.isNight(player.level()), level -> new EyeOfCthulhu(BossEntities.EYE_OF_CTHULHU.get(), level)));
    public static final PortDeferredItem<BossSummoningItem> SLIME_CROWN = registerItem("slime_crown", "史莱姆王冠", () -> new BossSummoningItem(player -> true, level -> new KingSlime(BossEntities.KING_SLIME.get(), level)));
    public static final PortDeferredItem<BossSummoningItem> WORM_FOOD = registerItem("worm_food", "蠕虫诱饵", () -> new BossSummoningItem(player -> player.level().getBiome(player.blockPosition()).is(ModTags.Biomes.THE_CORRUPTION), level -> new EaterOfWorlds(BossEntities.EATER_OF_WORLDS.get(), level)));
    public static final PortDeferredItem<BossSummoningItem> BLOODY_SPINE = registerItem("bloody_spine", "血腥脊椎", () -> new BossSummoningItem(player -> player.level().getBiome(player.blockPosition()).is(ModTags.Biomes.THE_CRIMSON), level -> new BrainOfCthulhu(BossEntities.BRAIN_OF_CTHULHU.get(), level)));
    public static final PortDeferredItem<BossSummoningItem> ABEEMINATION = registerItem("abeemination", "憎恶之蜂", () -> new BossSummoningItem(player -> {
        Holder<Biome> biome = player.level().getBiome(player.blockPosition());
        return biome.is(PortTags.Biomes.IS_JUNGLE) || biome.is(PortTags.Biomes.IS_LUSH);
    }, level -> new QueenBee(BossEntities.QUEEN_BEE.get(), level)));
    public static final PortDeferredItem<BossSummoningItem> DEER_THING = registerItem("deer_thing", "鹿华", () -> new BossSummoningItem(player -> {
        Holder<Biome> biome = player.level().getBiome(player.blockPosition());
        return biome.is(PortTags.Biomes.IS_SNOWY) || biome.is(PortTags.Biomes.IS_ICY);
    }, level -> new DeerClops(BossEntities.DEERCLOPS.get(), level)));

    public static final PortDeferredItem<CustomRarityItem> GOLDEN_LOCK_BOX = registerItem("golden_lock_box", "金锁盒", () -> new CustomRarityItem(new Item.Properties().component(ModDataComponentTypes.LOOT, new LootComponent(ModLootTables.GOLDEN_LOCK_BOX)), ModRarity.GREEN));
    public static final PortDeferredItem<CustomRarityItem> OBSIDIAN_LOCK_BOX = registerItem("obsidian_lock_box", "黑曜石锁盒", () -> new CustomRarityItem(new Item.Properties().component(ModDataComponentTypes.LOOT, new LootComponent(ModLootTables.OBSIDIAN_LOCK_BOX)), ModRarity.GREEN));

    public static final PortDeferredItem<GameEventItem> BLOOD_TEAR = registerItem("blood_tear", "血泪", () -> new GameEventItem(new Item.Properties(), ModRarity.GREEN, BloodMoonGameEvent.KEY));
    public static final PortDeferredItem<GameEventItem> GOBLIN_BATTLE_STANDARD = registerItem("goblin_battle_standard", "哥布林战旗", () -> new GameEventItem(new Item.Properties(), ModRarity.GREEN, GoblinArmyGameEvent.KEY));
    public static final PortDeferredItem<GameEventItem> PIRATE_MAP = registerItem("pirate_map", "海盗地图", () -> new GameEventItem(new Item.Properties(), ModRarity.LIGHT_RED, PirateInvasionGameEvent.KEY));

    // todo
    public static final PortDeferredItem<Item> GLOWSTICK = registerItemSimple("glowstick", "荧光棒", new Item.Properties().stacksTo(9999));
    public static final PortDeferredItem<Item> STICKY_GLOWSTICK = registerItemSimple("sticky_glowstick", "粘性荧光棒", new Item.Properties().stacksTo(9999));

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredItem<Item> registerItemSimple(String registryId, String chineseName, Item.Properties properties) {
        PortDeferredItem<Item> entry = ITEMS.registerSimpleItem(registryId, properties);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
