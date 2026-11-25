package org.confluence.mod.common.data.gen;

import com.cooobird.datatip.api.content.TextContent;
import com.cooobird.datatip.datagen.TextOptions;
import com.cooobird.datatip.datagen.TipContentBuilder;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.confluence.mod.common.data.TranslationConsumer;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.cooobird.datatip.datagen.TipContentBuilder.*;

/// 生成物品用途、效果说明和背景描述，统一保留文本样式与布局。
public final class ModDataTipProvider implements DataProvider {
    private final Path directory;

    public ModDataTipProvider(PackOutput output) {
        directory = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve("confluence/datatip");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> tasks = new ArrayList<>();
        buildTips(null).forEach((file, tips) -> tasks.add(DataProvider.saveStable(output, tips, directory.resolve(file))));
        return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Confluence DataTip";
    }

    static void addTranslations(TranslationConsumer language) {
        buildTips(language);
    }

    private static Map<String, JsonObject> buildTips(TranslationConsumer language) {
        Map<String, JsonObject> sections = new LinkedHashMap<>();
        sections.put("accessory_descriptions.json", accessoryDescriptions(language));
        sections.put("armor_descriptions.json", armorDescriptions(language));
        sections.put("bait_usage.json", baitUsage(language));
        sections.put("boss_summoning.json", bossSummoning(language));
        sections.put("event_summoning.json", eventSummoning(language));
        sections.put("functional_block_descriptions.json", functionalBlockDescriptions(language));
        sections.put("loot_container_usage.json", lootContainerUsage(language));
        sections.put("material_descriptions.json", materialDescriptions(language));
        sections.put("permanent_upgrade_descriptions.json", permanentUpgradeDescriptions(language));
        sections.put("potion_descriptions.json", potionDescriptions(language));
        sections.put("sponsor_item_lore.json", sponsorItemLore(language));
        sections.put("storage_item_usage.json", storageItemUsage(language));
        sections.put("tool_usage.json", toolUsage(language));
        sections.put("weapon_lore.json", weaponLore(language));
        sections.put("weapon_usage.json", weaponUsage(language));
        return sections;
    }

    private static void add(JsonObject root, ItemLike item, JsonObject content) {
        root.add(BuiltInRegistries.ITEM.getKey(item.asItem()).toString(), content);
    }

    private static void add(JsonObject root, TagKey<Item> items, JsonObject content) {
        root.add("#" + items.location(), content);
    }

    private static TextContent translate(TranslationConsumer language, String key, String english, String chinese, TextOptions style) {
        if (language != null) language.add(key, english, chinese);
        return TipContentBuilder.translate(key, style);
    }

    private static JsonObject accessoryDescriptions(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        // Confluence 注册的饰品固定能力说明。
        add(root, AccessoryItems.ADHESIVE_BANDAGE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.adhesive_bandage.0",
                        "Immune to bleeding",
                        "对流血免疫",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.ANGLER_TACKLE_BAG.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.angler_tackle_bag.0",
                        "Fishing line will never break, decreases chance of bait consumption, increases fishing power by 10",
                        "钓鱼线永远不会断，降低鱼饵消耗几率，渔力增加10",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.ARCANE_FLOWER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.arcane_flower.0",
                        "Reduces mana usage by 8%",
                        "魔力消耗降低8%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.arcane_flower.1",
                        "Automatically uses mana potions when needed",
                        "需要时自动使用魔力药水",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.arcane_flower.2",
                        "Enemies are less likely to target you",
                        "敌人不太可能以你为目标",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.ARGON_MOSS_FISHING_BOBBER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.argon_moss_fishing_bobber.0",
                        "Increases fishing power, fishing bobber glows",
                        "增加渔力，钓鱼浮标会发光",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.ARMOR_BRACING.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.armor_bracing.0",
                        "Immune to armor degradation and weakness",
                        "对盔甲破损和虚弱免疫",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.ARMOR_POLISH.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.armor_polish.0",
                        "Immune to armor degradation",
                        "对盔甲破损免疫",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.BAND_OF_STARPOWER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.band_of_starpower.0",
                        "Increases maximum mana by 20",
                        "最大魔力增加20",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.BLACK_COUNTERWEIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.black_counterweight.0",
                        "Throws a counterweight on yoyo hits; counterweights do not stack",
                        "悠悠球命中后掷出平衡锤；不与其他平衡锤重复叠加",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ModTags.Items.YOYO_STRING, entry(vbox(0,
                translate(language, "tooltip.item.confluence.yoyo_string.0",
                        "Increases yoyo range by 25% + 2 blocks and spin duration by 50%",
                        "增加悠悠球射程 25% + 2 格，滞空时间增加 50%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.BLUE_COUNTERWEIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.blue_counterweight.0",
                        "Throws a counterweight on yoyo hits; counterweights do not stack",
                        "悠悠球命中后掷出平衡锤；不与其他平衡锤重复叠加",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.CELESTIAL_CUFFS.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.celestial_cuffs.0",
                        "Increases pickup range for mana stars",
                        "扩大魔力星的拾取范围",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.celestial_cuffs.1",
                        "Restores mana when damaged",
                        "受到伤害时会恢复魔力",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.celestial_cuffs.2",
                        "Increases maximum mana by 20",
                        "最大魔力增加20",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.CELESTIAL_EMBLEM.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.celestial_emblem.0",
                        "Increases pickup range for mana stars, increases magic damage by 15%",
                        "扩大魔力星的拾取范围，魔法伤害提高15%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.CELESTIAL_MAGNET.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.celestial_magnet.0",
                        "Increases pickup range for mana stars",
                        "扩大魔力星的拾取范围",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.CHARM_OF_MYTHS.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.charm_of_myths.0",
                        "Provides health regeneration, reduces healing potion cooldown",
                        "提供生命恢复，减少治疗药水的冷却时间",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.CHROMATIC_CLOAK.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.chromatic_cloak.0",
                        "Immunity to Shimmer Phasing, Hold Sneak to Phase while submerged in Shimmer",
                        "对微光相位化免疫，当淹没在微光中时潜行可以相位化",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.CLOTHIER_VOODOO_DOLL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.clothier_voodoo_doll.0",
                        "'You are a terrible person'",
                        "“你这人真可怕”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.COIN_RING.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.coin_ring.0",
                        "Hitting enemies may drop extra coins, increases coin pickup range",
                        "击中敌人有时会掉落额外的钱币，扩大钱币的拾取范围",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.COUNTERCURSE_MANTRA.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.countercurse_mantra.0",
                        "Immune to silence and curse",
                        "对沉默和诅咒免疫",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.DISCOUNT_CARD.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.discount_card.0",
                        "Reduces shop prices by 20%",
                        "商店价格降低20%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.HUNTING_TOTEM.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.hunting_totem.0",
                        "Minions pick up experience from defeated enemies",
                        "召唤物会拾取被打败敌人的经验",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.FISHING_BOBBER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.fishing_bobber.0",
                        "Increases fishing power",
                        "增加渔力",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.GLOWING_FISHING_BOBBER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.glowing_fishing_bobber.0",
                        "Increases fishing power, fishing bobber glows",
                        "增加渔力，钓鱼浮标会发光",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.GOLD_RING.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.gold_ring.0",
                        "Increases coin pickup range",
                        "扩大钱币的拾取范围",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.GREEDY_RING.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.greedy_ring.0",
                        "Hitting enemies may drop extra coins, increases coin pickup range, reduces shop prices by 20%",
                        "击中敌人有时会掉落额外的钱币，扩大钱币的拾取范围，商店价格降低20%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.GREEN_COUNTERWEIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.green_counterweight.0",
                        "Throws a counterweight on yoyo hits; counterweights do not stack",
                        "悠悠球命中后掷出平衡锤；不与其他平衡锤重复叠加",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.GUIDE_TO_PLANT_FIBER_CORDAGE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.guide_to_plant_fiber_cordage.0",
                        "Allows the collection of Vine Rope from vines",
                        "可从藤蔓收集藤蔓绳",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.guide_to_plant_fiber_cordage.1",
                        "Right-click with an empty hand to eat Grass Blocks and plants",
                        "空手右击可以食用草方块与植物",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.guide_to_plant_fiber_cordage.2",
                        "Grass Blocks become Dirt, Jungle Grass Blocks become Mud; each bite restores half a hunger point",
                        "草方块转为土块，丛林草方块转为泥块；每次食用回复半格饱食度",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.guide_to_plant_fiber_cordage.3",
                        "Eating flowers grants the matching Suspicious Stew effect; two-block-tall plants count as two bites",
                        "食用花可获得对应的迷之炖菜效果；两格高的植物计为两次",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.HELIUM_MOSS_FISHING_BOBBER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.helium_moss_fishing_bobber.0",
                        "Increases fishing power, fishing bobber glows",
                        "增加渔力，钓鱼浮标会发光",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.HIGH_TEST_FISHING_LINE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.high_test_fishing_line.0",
                        "Fishing line will never break",
                        "鱼线永远不会断",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.KRYPTON_MOSS_FISHING_BOBBER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.krypton_moss_fishing_bobber.0",
                        "Increases fishing power, fishing bobber glows",
                        "增加渔力，钓鱼浮标会发光",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.LAVA_MOSS_FISHING_BOBBER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.lava_moss_fishing_bobber.0",
                        "Increases fishing power, fishing bobber glows",
                        "增加渔力，钓鱼浮标会发光",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.LAVAPROOF_FISHING_HOOK.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.lavaproof_fishing_hook.0",
                        "Allows fishing in lava",
                        "可在熔岩中钓鱼",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.LAVAPROOF_TACKLE_BAG.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.lavaproof_tackle_bag.0",
                        "Fishing line will never break, decreases chance of bait consumption, increases fishing power by 10",
                        "钓鱼线永远不会断，降低鱼饵消耗几率，渔力增加10",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.lavaproof_tackle_bag.1",
                        "Allows fishing in lava",
                        "可在熔岩中钓鱼",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.LUCKY_COIN.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.lucky_coin.0",
                        "Hitting enemies may drop extra coins",
                        "击中敌人有时会掉落额外的钱币",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MAGIC_CUFFS.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.magic_cuffs.0",
                        "Increases maximum mana by 20",
                        "最大魔力值增加20",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.magic_cuffs.1",
                        "Restores mana when damaged",
                        "受伤时恢复魔力",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MAGIC_YOYO_BAG.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.magic_yoyo_bag.0",
                        "Combines yoyo bag and magic yoyo string effects",
                        "结合悠悠球袋与魔法绳的效果",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MAGNET_FLOWER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.magnet_flower.0",
                        "Reduces mana usage by 8%",
                        "魔力消耗降低8%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.magnet_flower.1",
                        "Automatically uses mana potions when needed",
                        "需要时自动使用魔力药水",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.magnet_flower.2",
                        "Increases pickup range for mana stars",
                        "扩大魔力星的拾取范围",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MANA_CLOAK.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.mana_cloak.0",
                        "Collecting stars restores mana",
                        "收集星星会恢复魔力",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.mana_cloak.1",
                        "Reduces mana usage by 8%",
                        "魔力消耗降低8%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.mana_cloak.2",
                        "Automatically uses mana potions when needed",
                        "需要时自动使用魔力药水",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.mana_cloak.3",
                        "Stars will fall when you take damage",
                        "受到伤害后会使星星坠落",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MANA_FLOWER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.mana_flower.0",
                        "Reduces mana usage by 8%",
                        "魔力消耗降低8%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.mana_flower.1",
                        "Automatically uses mana potions when needed",
                        "需要时自动使用魔力药水",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MANA_REGENERATION_BAND.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.mana_regeneration_band.0",
                        "Increases maximum mana by 20",
                        "最大魔力值增加20",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.mana_regeneration_band.1",
                        "Increases mana regeneration speed",
                        "提高魔力再生速度",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MECHANICAL_LENS.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.mechanical_lens.0",
                        "Gives enhanced wiring vision",
                        "给予改良的布线视野",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.mechanical_lens.1",
                        "Right-click in the backpack to toggle on/off.",
                        "背包内右键点击可开关",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MEDICATED_BANDAGE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.medicated_bandage.0",
                        "Immune to poison and bleeding",
                        "对中毒和流血免疫",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MEGAPHONE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.megaphone.0",
                        "Immune to silence",
                        "对沉默免疫",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.NATURES_GIFT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.natures_gift.0",
                        "Reduces mana usage by 6%",
                        "魔力消耗减少6%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.NAZAR.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.nazar.0",
                        "Immune to curse",
                        "对诅咒免疫",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.NEON_MOSS_FISHING_BOBBER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.neon_moss_fishing_bobber.0",
                        "Increases fishing power, fishing bobber glows",
                        "增加渔力，钓鱼浮标会发光",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.PAINT_SPRAYER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.paint_sprayer.0",
                        "Automatically paints or coats placed objects.",
                        "自动给放置的物体刷油漆或涂料",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.PHILOSOPHERS_STONE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.philosophers_stone.0",
                        "Reduces healing potion cooldown",
                        "减少治疗药水的冷却时间",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.POCKET_MIRROR.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.pocket_mirror.0",
                        "Immune to petrification",
                        "对石化免疫",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.PURPLE_COUNTERWEIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.purple_counterweight.0",
                        "Throws a counterweight on yoyo hits; counterweights do not stack",
                        "悠悠球命中后掷出平衡锤；不与其他平衡锤重复叠加",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.RADIO_THING.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.radio_thing.0",
                        "Allows the user to see the world differently",
                        "允许使用者以不同的方式看世界",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.radio_thing.1",
                        "'Forbidden Knowledge echoes from the radio...'",
                        "“禁忌的知识在其中回响……”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.RED_COUNTERWEIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.red_counterweight.0",
                        "Throws a counterweight on yoyo hits; counterweights do not stack",
                        "悠悠球命中后掷出平衡锤；不与其他平衡锤重复叠加",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.REFLECTIVE_SHADES.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.reflective_shades.0",
                        "Immune to blindness and petrification",
                        "对失明和石化免疫",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.HARVEST_TOTEM.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.harvest_totem.0",
                        "Minions pick up experience and coins from defeated enemies",
                        "召唤物会拾取被打败敌人的经验和钱币",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.SPECTRE_GOGGLES.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.spectre_goggles.0",
                        "Provides ghost vision to interact with echo blocks",
                        "提供幽灵视觉以与回声块交互",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.terra_curio.spectre_goggles.1",
                        "Right-click in the backpack to toggle on/off.",
                        "背包内右键点击可开关",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.STRUNG_COUNTERWEIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.strung_counterweight.0",
                        "Combines the effects of a white yoyo string and a counterweight",
                        "结合白绳和平衡锤的效果",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.TACKLE_BOX.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.tackle_box.0",
                        "Decreases chance of bait consumption",
                        "降低鱼饵消耗几率",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.STICKY_CRYSTAL_PENDANT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.sticky_crystal_pendant.0",
                        "Minions pick up coins from defeated enemies",
                        "召唤物会拾取被打败敌人的钱币",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.XENON_MOSS_FISHING_BOBBER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.xenon_moss_fishing_bobber.0",
                        "Increases fishing power, fishing bobber glows",
                        "增加渔力，钓鱼浮标会发光",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.YELLOW_COUNTERWEIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.yellow_counterweight.0",
                        "Throws a counterweight on yoyo hits; counterweights do not stack",
                        "悠悠球命中后掷出平衡锤；不与其他平衡锤重复叠加",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.YOYO_BAG.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.yoyo_bag.0",
                        "Combines yoyo string, glove and counterweight effects",
                        "结合绳、手套和平衡锤的效果",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.YOYO_GLOVE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.yoyo_glove.0",
                        "Spawns a second yoyo on hit; the duplicate consumes spin duration faster",
                        "命中后生成副悠悠球；副球使有限滞空时间消耗加快",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject armorDescriptions(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        // 盔甲的固定能力说明；实际属性与套装提示继续由 Java 输出。
        add(root, ArmorItems.ADAMANTITE_HEADGEAR.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.adamantite_headgear.0",
                        "Increases maximum mana by 80",
                        "最大魔力增加80",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.AMBER_ROBE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.amber_robe.0",
                        "Increases maximum mana by 60",
                        "最大魔力增加60",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.amber_robe.1",
                        "13% reduced mana cost",
                        "魔力消耗降低13%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.AMETHYST_ROBE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.amethyst_robe.0",
                        "Increases maximum mana by 20",
                        "最大魔力增加20",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.amethyst_robe.1",
                        "5% reduced mana cost",
                        "魔力消耗降低5%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.COBALT_HAT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.cobalt_hat.0",
                        "Increases maximum mana by 40",
                        "最大魔力增加40",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.COLD_CRYSTAL_CHESTPLATE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.cold_crystal_chestplate.0",
                        "Increases maximum mana by 20",
                        "最大魔力值增加20",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.COLD_CRYSTAL_HELMET.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.cold_crystal_helmet.0",
                        "Increases maximum mana by 20",
                        "最大魔力值增加20",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.DIAMOND_ROBE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.diamond_robe.0",
                        "Increases maximum mana by 80",
                        "最大魔力增加80",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.diamond_robe.1",
                        "15% reduced mana cost",
                        "魔力消耗降低15%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.HEIM_HELMET.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.heim_helmet.0",
                        "Extend underwater breathing time by 5%",
                        "延长水下呼吸时间5%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.JADE_ROBE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.jade_robe.0",
                        "Increases maximum mana by 60",
                        "最大魔力增加60",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.jade_robe.1",
                        "11% reduced mana cost",
                        "魔力消耗降低11%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.JUNGLE_CHESTPLATE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.jungle_chestplate.0",
                        "Increases maximum mana by 20",
                        "最大魔力增加20",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.JUNGLE_HELMET.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.jungle_helmet.0",
                        "Increases maximum mana by 40",
                        "最大魔力增加40",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.JUNGLE_LEGGINGS.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.jungle_leggings.0",
                        "Increases maximum mana by 20",
                        "最大魔力增加20",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.MINING_HELMET.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.mining_helmet.0",
                        "Provides light when worn",
                        "穿戴时可提供照明",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.MYTHRIL_HOOD.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.mythril_hood.0",
                        "Increases maximum mana by 60",
                        "最大魔力增加60",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.ORICHALCUM_HEADGEAR.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.orichalcum_headgear.0",
                        "Increases maximum mana by 80",
                        "最大魔力增加80",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.PALLADIUM_HEADGEAR.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.palladium_headgear.0",
                        "Increases maximum mana by 60",
                        "最大魔力增加60",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.RUBY_ROBE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.ruby_robe.0",
                        "Increases maximum mana by 60",
                        "最大魔力增加60",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.ruby_robe.1",
                        "13% reduced mana cost",
                        "魔力消耗降低13%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.SAPPHIRE_ROBE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.sapphire_robe.0",
                        "Increases maximum mana by 40",
                        "最大魔力增加40",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.sapphire_robe.1",
                        "9% reduced mana cost",
                        "魔力消耗降低9%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.TITANIUM_HEADGEAR.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.titanium_headgear.0",
                        "Increases maximum mana by 100",
                        "最大魔力增加100",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.TOPAZ_ROBE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.topaz_robe.0",
                        "Increases maximum mana by 40",
                        "最大魔力增加40",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.topaz_robe.1",
                        "7% reduced mana cost",
                        "魔力消耗降低7%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject baitUsage(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        TextContent baitUsageTip = translate(language, "tooltip.item.confluence.bait.common.0",
                "When placed in the inventory, it will be automatically used while fishing, prioritizing the bait in the off-hand.",
                "放入背包即可钓鱼时自动使用，优先使用副手的鱼饵",
                textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));

        // 所有本体鱼饵共用的自动使用指引；实际渔力继续由 Java 输出。
        add(root, ModTags.Items.BAIT, entry(vbox(0, baitUsageTip), Map.of(), false, true));
        return root;
    }

    private static JsonObject bossSummoning(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        // Boss 召唤物的使用条件、背景故事和风味文字；每个物品按原行号排列。
        add(root, ConsumableItems.ABEEMINATION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.abeemination.0",
                        "Right - click to summon the Queen Bee. Only the dirty miasma in the tropical broad - leaved rainforest can suppress her fury.",
                        "右键使用以召唤蜂王，只有热带阔叶雨林间的污浊瘴气才能压制住她的狂怒",
                        textOptions().color(ChatFormatting.YELLOW.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.abeemination.1",
                        "She seems to extremely dislike the smell of fluorescent fungal spores.",
                        "她似乎极度厌恶那些荧光真菌孢子的气息",
                        textOptions().color(ChatFormatting.YELLOW.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.abeemination.2",
                        "A mass of unformed young bees. It feels like sticky honey when you touch it... The Queen Bee and her subordinates' protectiveness of their sweet territory has somehow gradually developed into extreme repulsion and hatred towards non - similar creatures.",
                        "一团尚未成型的幼蜂，摸上去感觉像黏稠的蜂蜜……蜂王与她的下属们对它们甜蜜领地的保护欲不知何时渐渐发展为对非同类生物的极度排斥与憎恶。",
                        textOptions().color(ChatFormatting.YELLOW.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.abeemination.3",
                        "“The flapping of the bees' wings shakes the thickest leaves in the jungle”",
                        "“群蜂振翅之声撼动了丛林中最为厚重的叶片”",
                        textOptions().color(ChatFormatting.YELLOW.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.BLOODY_SPINE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.bloody_spine.0",
                        "Right - click to summon the Brain of Cthulhu. It will wake up after smelling the smell of flesh and pus in the Crimson.",
                        "右键使用以召唤克苏鲁之脑，它会在嗅到猩红之地血肉与脓浆的气息后方才苏醒",
                        textOptions().color(ChatFormatting.RED.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.bloody_spine.1",
                        "A piece of detached body tissue, mottled with scabs and muscle tissue. It's hard to imagine how it was taken out of which creature.",
                        "一截被剥离下来了的身体组织，上面斑驳着血痂块与肌肉组织，很难想象它是从哪种生物体内，以何种方式取出来的。",
                        textOptions().color(ChatFormatting.RED.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.bloody_spine.2",
                        "“It seems to be trying to talk to you... or maybe...”",
                        "“它似乎在尝试着与你对话…要不蘨睈錗㧴氓旳義厡…”",
                        textOptions().color(ChatFormatting.RED.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.DEER_THING.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.deer_thing.0",
                        "Right-click to summon the Deerclops, which will emerge amidst the frigid winds of the Tundra",
                        "右键使用以召唤独眼巨鹿，它会在冰原的寒风中现身",
                        textOptions().color(ChatFormatting.AQUA.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.deer_thing.1",
                        "A frigid eye that glimmers with the sight of another world in its socket",
                        "一只透着寒意的眼球，它的眼眶中映出了另一个世界的光景",
                        textOptions().color(ChatFormatting.AQUA.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.deer_thing.2",
                        "\"Something big is coming... Don't draw Klaus here again!\"",
                        "“有个大家伙要来了…别再引来克劳斯！”",
                        textOptions().color(ChatFormatting.AQUA.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.SLIME_CROWN.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.slime_crown.0",
                        "Right - click to summon the King Slime",
                        "右键使用以召唤史莱姆王",
                        textOptions().color(ChatFormatting.BLUE.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.slime_crown.1",
                        "A small crown that seems to be prepared for the coronation ceremony of those cute and harmless gel - like creatures.",
                        "一只小巧的王冠，看上去是为那些人畜无害的可爱凝胶生物的加冕仪式所准备的。",
                        textOptions().color(ChatFormatting.BLUE.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.slime_crown.2",
                        "“Wearing it may not be a good choice”",
                        "“戴上它可能不是个好选择”",
                        textOptions().color(ChatFormatting.BLUE.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.SUSPICIOUS_LOOKING_EYE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.suspicious_looking_eye.0",
                        "Right - click to summon the Eye of Cthulhu. It only appears at night.",
                        "右键使用以召唤克苏鲁之眼，它只在夜间出没",
                        textOptions().color(ChatFormatting.RED.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.suspicious_looking_eye.1",
                        "A lifeless and dull - looking eyeball. Although it is not aggressive, it seems more dangerous than its peers that fly in the air at night.",
                        "一颗死气沉沉，目光呆滞的眼球，尽管它不具备攻击性，但它似乎比它们那些夜间会在半空中飞来飞去的同僚们更为危险。",
                        textOptions().color(ChatFormatting.RED.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.suspicious_looking_eye.2",
                        "“It seems to be looking at you…”",
                        "“它好像在看着你…”",
                        textOptions().color(ChatFormatting.RED.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.WORM_FOOD.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.worm_food.0",
                        "Right - click to summon the Eater of Worlds. It shuttles through the huge rift filled with corruptive poisonous fog.",
                        "右键使用以召唤世界吞噬者，它会在充满腐化毒雾的巨大裂谷中穿梭",
                        textOptions().color(ChatFormatting.DARK_PURPLE.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.worm_food.1",
                        "It smells like a rotten rib and seems to have a strong attraction to those mutated Terraria creatures.",
                        "闻起来像一块烂掉了的排骨，似乎对那些病变了的泰拉生物有极强的吸引力。",
                        textOptions().color(ChatFormatting.DARK_PURPLE.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.worm_food.2",
                        "“It wants to devour more... more and more and more and more…”",
                        "“它还要吞噬更多…更多更多更多更多…”",
                        textOptions().color(ChatFormatting.DARK_PURPLE.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject eventSummoning(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        // 血月、哥布林军队、海盗入侵等事件召唤物的说明和风味文字。
        add(root, ConsumableItems.BLOOD_TEAR.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.blood_tear.0",
                        "Summons the Blood Moon",
                        "召唤血月",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.blood_tear.1",
                        "'What a horrible night to have a curse.'",
                        "“诅咒之夜太可怕了。”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.GOBLIN_BATTLE_STANDARD.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.goblin_battle_standard.0",
                        "Summons a Goblin Army ",
                        "召唤哥布林军队",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.PIRATE_MAP.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.pirate_map.0",
                        "Summons a Pirate Invasion",
                        "召唤海盗入侵",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject functionalBlockDescriptions(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        TextContent hardmodeForge = translate(language, "tooltip.item.confluence.hardmode_forge.0",
                "Used to smelt adamantite and titanium ore",
                "用于熔炼精金矿和钛金矿",
                textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));
        TextContent wireable = translate(language, "tooltip.item.confluence.wireable.0",
                "Wireable",
                "可接线",
                textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));
        TextContent hardmodeAnvil = translate(language, "tooltip.item.confluence.hardmode_anvil.0",
                "Used to craft items from mythril, orichalcum, adamantite, and titanium ingots",
                "用于制作以秘银锭、山铜锭、精金锭和钛金锭为原料的物品",
                textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));

        // 制作站、家具、功能方块与可接线方块的固定说明；同一方块的提示合并在同一条目。
        add(root, FunctionalBlocks.ADAMANTITE_FORGE.get(), entry(vbox(0,
                hardmodeForge), Map.of(), false, true));
        add(root, FunctionalBlocks.ALCHEMY_TABLE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.alchemy_table.0",
                        "33% chance to not consume potion crafting ingredients",
                        "33%几率不消耗药水的制作材料",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.AMMO_BOX.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.ammo_box.0",
                        "20% chance to save ammo",
                        "20%几率省下弹药",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.BEWITCHING_TABLE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.bewitching_table.0",
                        "Right click to have more minions",
                        "右键点击可拥有更多仆从",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.BLEND_O_MATIC.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.blend_o_matic.0",
                        "Used to craft objects",
                        "用于制作物件",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ModTags.Items.WIREABLE, entry(vbox(0, wireable), Map.of(), false, true));
        add(root, FunctionalBlocks.BREWING_VIAL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.brewing_vial.0",
                        "Potions can be crafted with recipes",
                        "可通过配方制作药水",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.CHLOROPHYTE_EXTRACTINATOR.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.chlorophyte_extractinator.0",
                        "Placing silt/slush/fossil/gravel/marine_gravel piles into the extractinator turns them into something more useful",
                        "将泥沙/雪泥/化石/沙砾/海洋沙砾堆放到提炼机中可将其变成更有用的东西",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.chlorophyte_extractinator.1",
                        "Place contaminated blocks into the extractinator to purify them",
                        "将受污染的物块放入提炼机可净化之",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.chlorophyte_extractinator.2",
                        "Other items placed inside may have interesting effects",
                        "放入其他物品可能会产生有趣的效果",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.DYE_VAT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.dye_vat.0",
                        "Used to Craft Dyes",
                        "用于制作染料",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.ECHO_BLOCK.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.echo_block.0",
                        "Only visible with Echo Sight",
                        "只在回声视觉下才能看到",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.EXTRACTINATOR.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.extractinator.0",
                        "Placing silt/slush/fossil/gravel/marine_gravel piles into the extractinator turns them into something more useful",
                        "将泥沙/雪泥/化石/沙砾/海洋沙砾堆放到提炼机中可将其变成更有用的东西",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.HEART_LANTERN.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.heart_lantern.0",
                        "Increases life regeneration when placed nearby",
                        "放在附近时提高生命再生速度",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.HEAVY_WORK_BENCH.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.heavy_work_bench.0",
                        "Used for advanced crafting",
                        "用于高级制作",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.HELLFORGE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.hellforge.0",
                        "Used for smelting hellstone materials. Smelting regular minerals has a residual heat effect.",
                        "用于狱石类烧炼制作，烧炼常规矿物具有余温效果",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.KEG.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.keg.0",
                        "Used for brewing ale",
                        "用于酿造麦芽酒",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.LIFE_CAMPFIRE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.life_campfire.0",
                        "Life regen is increased when near a campfire",
                        "靠近篝火时生命再生提速",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.LOOM.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.loom.0",
                        "Used for crafting cloth",
                        "用于制作衣物",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.MEAT_GRINDER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.meat_grinder.0",
                        "Used to craft objects",
                        "用于制作物件",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.MYTHRIL_ANVIL.get(), entry(vbox(0,
                hardmodeAnvil), Map.of(), false, true));
        add(root, FunctionalBlocks.ORICHALCUM_ANVIL.get(), entry(vbox(0,
                hardmodeAnvil), Map.of(), false, true));
        add(root, FunctionalBlocks.PEACE_CANDLE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.peace_candle.0",
                        "Makes surrounding creatures less hostile",
                        "降低周围生物的敌意",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                wireable), Map.of(), false, true));
        add(root, FunctionalBlocks.PIGGY_BANK.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.piggy_bank.0",
                        "Can be used to store your items",
                        "可用来储存你的物品",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.piggy_bank.1",
                        "Stored items can only be accessed by you",
                        "储存的物品只有你能存取",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SAFE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.safe.0",
                        "Can be used to store your items",
                        "可用来储存你的物品",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.safe.1",
                        "Stored items can only be accessed by you",
                        "储存的物品只有你能存取",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SAWMILL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.sawmill.0",
                        "Used for advanced wood crafting",
                        "用于高级木工制作",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SHARPENING_STATION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.sharpening_station.0",
                        "Increases armor penetration for melee weapons",
                        "提高近战武器的盔甲穿透力",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SKY_MILL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.sky_mill.0",
                        "Used for special crafting",
                        "用于特殊制作",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SOLIDIFIER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.solidifier.0",
                        "Used to craft objects",
                        "用于制作物件",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.STAR_IN_A_BOTTLE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.star_in_a_bottle.0",
                        "Increases mana regeneration when placed nearby",
                        "放在附近时提高魔力再生速度",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.TITANIUM_FORGE.get(), entry(vbox(0,
                hardmodeForge), Map.of(), false, true));
        add(root, FunctionalBlocks.TUFF_BOOTH.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.tuff_booth.0",
                        "Right-click the upper part to place items, right-click the lower part with a carpet to lay it, and right-click the lower part with a name tag to display the item name",
                        "右键上半部分放置物品，使用地毯右键下半部分铺设，使用命名牌右键下半部分显示物品名称",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.WATER_CANDLE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.water_candle.0",
                        "Holding this may attract unwanted attention",
                        "手持此物可能会引起不必要的注意",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                wireable), Map.of(), false, true));
        add(root, FunctionalBlocks.SHADOW_CANDLE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.shadow_candle.0",
                        "Cancels town protection when held or placed and lit",
                        "手持或点亮放置时取消城镇保护",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                wireable), Map.of(), false, true));
        return root;
    }

    private static JsonObject lootContainerUsage(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        TextContent crateUsage = translate(language, "tooltip.item.confluence.crate.common.0",
                "Hold the right mouse button to open, and Sneak while clicking the right mouse button to place.",
                "手持右键打开，潜行右键可放置",
                textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));
        TextContent openContainer = translate(language, "tooltip.item.confluence.right_click.common.0",
                "Hold the right mouse button to open.",
                "手持右键打开",
                textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));

        // 宝匣、宝藏袋、礼包和锁盒的打开及放置指引。
        add(root, ModTags.Items.CRATE, entry(vbox(0, crateUsage), Map.of(), false, true));
        add(root, ModTags.Items.OPENABLE_CONTAINER, entry(vbox(0, openContainer), Map.of(), false, true));
        add(root, ConsumableItems.GOLDEN_LOCK_BOX.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.golden_lock_box.0",
                        "“Right click to open”",
                        "“使用钥匙在背包中右键打开”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.golden_lock_box.1",
                        "“Requires a Dungeon Golden Key”",
                        "“需要地牢金钥匙”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.OBSIDIAN_LOCK_BOX.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.obsidian_lock_box.0",
                        "“Right click to open”",
                        "“使用钥匙在背包中右键打开”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.obsidian_lock_box.1",
                        "“Requires a Shadow Key”",
                        "“需要暗影钥匙”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject materialDescriptions(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        add(root, MaterialItems.GREEN_SOLUTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.green_solution.0",
                        "Purifies Corruption, Crimson and Hallow",
                        "净化腐化、猩红和神圣环境",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.green_solution.1",
                        "Restores Glowing Mushroom biomes to Jungle",
                        "将发光蘑菇群系恢复为丛林",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.BLUE_SOLUTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.blue_solution.0",
                        "Converts pure, Corrupt or Crimson environments into Hallow",
                        "将纯净、腐化或猩红环境转化为神圣之地",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.blue_solution.1",
                        "Deserts and snow biomes become their Hallowed variants",
                        "沙漠和雪原转化为对应的神圣变种",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.PURPLE_SOLUTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.purple_solution.0",
                        "Converts pure, Hallowed or Crimson environments into Corruption",
                        "将纯净、神圣或猩红环境转化为腐化之地",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.purple_solution.1",
                        "Deserts and snow biomes become their Corrupt variants",
                        "沙漠和雪原转化为对应的腐化变种",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.DARK_BLUE_SOLUTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.dark_blue_solution.0",
                        "Converts Jungle into Glowing Mushroom biomes",
                        "将丛林转化为发光蘑菇群系",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.RED_SOLUTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.red_solution.0",
                        "Converts pure, Corrupt or Hallowed environments into Crimson",
                        "将纯净、腐化或神圣环境转化为猩红之地",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.red_solution.1",
                        "Deserts and snow biomes become their Crimson variants",
                        "沙漠和雪原转化为对应的猩红变种",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.YELLOW_SOLUTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.yellow_solution.0",
                        "Converts forests, snow biomes and their infected variants into desert",
                        "将森林、雪原及其感染变种转化为沙漠",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.WHITE_SOLUTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.white_solution.0",
                        "Converts forests, deserts and their infected variants into snow biomes",
                        "将森林、沙漠及其感染变种转化为雪原",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.BROWN_SOLUTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.brown_solution.0",
                        "Converts deserts, snow biomes and their infected variants into forest",
                        "将沙漠、雪原及其感染变种转化为森林",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        // 材料的背景描述和固定制作指引。
        add(root, MaterialItems.GEL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.gel.0",
                        "'Both tasty and flammable'",
                        "“既好吃，又易燃”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.RAW_ASPHALT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.raw_asphalt.0",
                        "Use a Blend-O-Matic to make asphalt blocks",
                        "使用搅拌机以制成沥青块",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_BRIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.soul_of_bright.0",
                        "The essence of wisdom forged in rebirth",
                        "“于重铸中智能的精华”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_FLIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.soul_of_flight.0",
                        "'The essence of powerful flying creatures'",
                        "“强大飞行生物的精华”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_FRIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.soul_of_fright.0",
                        "'The essence of pure terror'",
                        "“纯粹恐惧的精华”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_LIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.soul_of_light.0",
                        "'The essence of light creatures'",
                        "“光明生物的精华”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_MIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.soul_of_might.0",
                        "'The essence of the destroyer'",
                        "“毁灭者的精华”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_NIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.soul_of_night.0",
                        "'The essence of dark creatures'",
                        "“黑暗生物的精华”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_SIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.soul_of_sight.0",
                        "'The essence of omniscient watchers'",
                        "“全知看守者的精华”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_VOIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.soul_of_voight.0",
                        "The essence of the dragon born from the void",
                        "“虚空中诞生巨龙的精华”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject permanentUpgradeDescriptions(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        // 玩家、NPC、商贩及矿车永久提升道具的固定说明。
        add(root, ConsumableItems.ADVANCED_COMBAT_TECHNIQUES.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.advanced_combat_techniques.0",
                        "Increases the defense and strength of all villagers",
                        "增强所有城镇居民的防御力和力量",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.advanced_combat_techniques.1",
                        "'Contains offensive and defensive fighting techniques'",
                        "“包含攻防战斗技能”",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.ADVANCED_COMBAT_TECHNIQUES_VOLUME_TWO.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.advanced_combat_techniques_volume_two.0",
                        "Increases the defense and strength of all villagers",
                        "增强所有城镇居民的防御力和力量",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.advanced_combat_techniques_volume_two.1",
                        "'Contains offensive and defensive fighting techniques, volume two!'",
                        "“包含攻防战斗技术，卷二！”",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.AEGIS_APPLE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.aegis_apple.0",
                        "Permanently increases defense",
                        "永久提高防御力",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.AMBROSIA.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.ambrosia.0",
                        "Permanently increases mining and building speed",
                        "永久提高挖矿速度和建造速度",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.ARCANE_CRYSTAL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.arcane_crystal.0",
                        "Permanently increases mana regeneration",
                        "永久提高魔力再生",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.ARTISAN_LOAF.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.artisan_loaf.0",
                        "Consume to permanently increase block interaction range",
                        "使用后可永久扩大方块交互范围",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.artisan_loaf.1",
                        "'Legendary Bread that once reminded Teddy of home'",
                        "“曾经让泰迪想家的传奇面包”",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.GALAXY_PEARL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.galaxy_pearl.0",
                        "Permanently increases luck",
                        "永久提高运气",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.GUMMY_WORM.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.gummy_worm.0",
                        "Permanently increases fishing skill",
                        "永久提高钓鱼技能",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.MANA_CRYSTAL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.mana_crystal.0",
                        "Permanently increases maximum mana by 20",
                        "最大魔力永久增加20",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.MINECART_UPGRADE_KIT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.minecart_upgrade_kit.0",
                        "Permanently grants boosted speed and a defensive probe for minecarts",
                        "永久提高矿车的速度并加装防御探测器",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.minecart_upgrade_kit.1",
                        "'Free Mechanical Cart included!'",
                        "“内含免费的机械货车！”",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.PEDDLERS_SATCHEL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.peddlers_satchel.0",
                        "Permanently increases items sold by the Traveling Merchant",
                        "永久增加旅商售卖的物品",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.RECALL_MANA_CRYSTAL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.recall_mana_crystal.0",
                        "Permanently reduces maximum mana by 20",
                        "最大魔力永久减少20",
                        textOptions().color(ChatFormatting.AQUA.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.VITAL_CRYSTAL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.vital_crystal.0",
                        "Permanently boosts life regeneration",
                        "永久加速生命再生",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject potionDescriptions(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        // 药水的固定效果说明；实际效果、持续时间等运行时信息继续由 Java 输出。
        add(root, PotionItems.ALE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.ale.0",
                        "Minor improvements to melee stats & lowered defense'Down the hatch!'",
                        "近战属性小幅提升、防御力降低“干杯！”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.AMMO_RESERVATION_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.ammo_reservation_potion.0",
                        "20% chance to save ammo",
                        "20%几率省下弹药",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.BATTLE_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.battle_potion.0",
                        "Increases enemy spawn rate",
                        "提高敌人生成速度",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.BUILDER_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.builder_potion.0",
                        "Increased placement speed and range",
                        "提高放置速度、扩大放置范围",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.CALMING_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.calming_potion.0",
                        "Decreases enemy spawn rate",
                        "降低敌人生成速度",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.CHAOS_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.chaos_potion.0",
                        "Grants random effects",
                        "赋予随机效果",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.CRATE_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.crate_potion.0",
                        "Increases chance to get a crate",
                        "提高获得宝匣的几率",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.DANGERSENSE_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.dangersense_potion.0",
                        "Allows you to see nearby danger sources",
                        "让你看到附近的危险源",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.EGGNOG.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.eggnog.0",
                        "Restores 16 health",
                        "恢复16生命",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.ENDURANCE_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.endurance_potion.0",
                        "10% reduced damage",
                        "所受伤害降低10%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.FEATHERFALL_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.featherfall_potion.0",
                        "Slows falling speed",
                        "减缓坠落速度",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.FISHING_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.fishing_potion.0",
                        "Increases fishing power by 15",
                        "渔力增加15",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.FLASK_OF_FIRE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.flask_of_fire.0",
                        "Melee and Whip attacks set enemies on fire",
                        "近战攻击和鞭子攻击会使敌人着火",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.FLASK_OF_GOLD.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.flask_of_gold.0",
                        "Melee and Whip attacks make enemies drop more gold",
                        "近战攻击和鞭子攻击会使敌人掉落更多金子",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.FLIPPER_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.flipper_potion.0",
                        "Lets you move swiftly in liquids",
                        "让你在液体中快速移动",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.GILLS_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.gills_potion.0",
                        "Breathe water instead of air",
                        "可在水中呼吸",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.GRAVITATION_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.gravitation_potion.0",
                        "Press UP to reverse gravity",
                        "可控制重力",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.GREATER_HEALING_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.greater_healing_potion.0",
                        "Restores 40 health",
                        "恢复40生命",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.GREATER_LUCK_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.greater_luck_potion.0",
                        "Increases the Luck of the user",
                        "提高使用者的运气",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.GREATER_MANA_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.greater_mana_potion.0",
                        "Restores 200 mana",
                        "恢复200魔力",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.HEALING_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.healing_potion.0",
                        "Restores 20 health",
                        "恢复20生命",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.HEART_REACH_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.heart_reach_potion.0",
                        "Increased heart pickup range",
                        "扩大生命心的拾取范围",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.HUNTER_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.hunter_potion.0",
                        "Shows the location of enemies",
                        "显示敌人位置",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.INFERNO_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.inferno_potion.0",
                        "Nearby enemies are ignited",
                        "点燃附近的敌人",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.INVISIBILITY_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.invisibility_potion.0",
                        "Grants invisibility and lowers the spawn rate of enemies",
                        "隐身并降低敌人的生成速度",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.IRON_SKIN_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.iron_skin_potion.0",
                        "Increases defense by 4",
                        "防御力增加4",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.LESSER_HEALING_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.lesser_healing_potion.0",
                        "Restores 10 health",
                        "恢复10生命",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.LESSER_LUCK_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.lesser_luck_potion.0",
                        "Increases the Luck of the user",
                        "提高使用者的运气",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.LESSER_MANA_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.lesser_mana_potion.0",
                        "Restores 50 mana",
                        "恢复50魔力",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.LOVE_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.love_potion.0",
                        "Throw this to make someone fall in love(or enables animal breeding)",
                        "投掷此物可让人坠入爱河（或使动物繁衍）",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.LUCK_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.luck_potion.0",
                        "Increases the Luck of the user",
                        "提高使用者的运气",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.MANA_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.mana_potion.0",
                        "Restores 100 mana",
                        "恢复100魔力",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.MANA_REGENERATION_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.mana_regeneration_potion.0",
                        "Increased mana regeneration",
                        "提高魔力再生速度",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.MINING_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.mining_potion.0",
                        "Increases mining speed by 25%",
                        "挖矿速度提高25%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.MUG.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.mug.0",
                        "Collect ale at the Keg",
                        "在酒桶处接取麦芽酒",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.NIGHT_OWL_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.night_owl_potion.0",
                        "Grants night vision",
                        "赋予夜视",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.OBSIDIAN_SKIN_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.obsidian_skin_potion.0",
                        "Immune to lava",
                        "对熔岩免疫",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.RANDOM_TELEPORT_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.random_teleport_potion.0",
                        "Teleports you to a random location",
                        "将玩家传送至世界中的随机位置。",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.RECALL_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.recall_potion.0",
                        "Teleports you home",
                        "将玩家传送到其重生点。",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.RED_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.red_potion.0",
                        "'Only for those who are worthy'",
                        "“仅献给配得上的人”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.REGENERATION_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.regeneration_potion.0",
                        "Provides life regeneration",
                        "提供生命再生",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.RESTORATION_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.restoration_potion.0",
                        "Restores 18 health",
                        "恢复18生命",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.SATIETY_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.satiety_potion.0",
                        "Slows hunger depletion",
                        "延缓饥饿值下降速度",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.SHIMMER_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.shimmer_potion.0",
                        "Grants the Shimmer effect",
                        "赋予微光效果",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.SHINE_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.shine_potion.0",
                        "Emitting light",
                        "发出光芒",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.SPELUNKER_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.spelunker_potion.0",
                        "Shows the location of treasure and ore",
                        "显示宝藏和矿石的位置",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.STINK_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.stink_potion.0",
                        "Throw this to make someone smell terrible",
                        "投掷此物可让人闻起来恶心",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.STRANGE_BREW.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.strange_brew.0",
                        "Restores 14–24 health",
                        "恢复14–24生命",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.SUMMONING_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.summoning_potion.0",
                        "Increases your max number of minions by 1",
                        "仆从数量上限增加1",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.SUPER_HEALING_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.super_healing_potion.0",
                        "Restores 60 health",
                        "恢复60生命",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.SUPER_MANA_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.super_mana_potion.0",
                        "Restores 300 mana",
                        "恢复300魔力",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.THORNS_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.thorns_potion.0",
                        "Attacker also take damage",
                        "攻击者也会受到伤害",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.TITAN_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.titan_potion.0",
                        "Increases knockback",
                        "提高击退力",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.WATER_WALKING_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.water_walking_potion.0",
                        "Allows the ability to walk on water",
                        "可在水上行走",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.WORMHOLE_POTION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.wormhole_potion.0",
                        "Teleports the player to a teammate when they click their icon on the map.",
                        "当玩家在地图上点击队友图标时将玩家传送至队友的位置处。",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject sponsorItemLore(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        // 赞助纪念物品、特殊食物与彩蛋的背景描述；赞助武器的故事统一放在 weapon_lore.json。
        add(root, FoodItems.DONGDONGS_FLATBREAD.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.dongdongs_flatbread.0",
                        "Freshly baked flatbread on the Netherrack. Come and have a taste!",
                        "刚在下界岩上烤好的饼，快来尝尝吧！",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ModItems.FAILED_SKULL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.failed_skull.0",
                        "A creeper had its body forcibly transformed by piglins and can explode multiple times. The piglins wanted to use it as a biological weapon to invade the Overworld, but it escaped unexpectedly.",
                        "一只苦力怕被猪灵强制改造了身体，可以多次自爆。猪灵想用它作为入侵主世界的生物兵器，却被其意外逃脱",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ModItems.ICE_TOFU_BRICK.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.ice_tofu_brick.0",
                        "It’s not just about ramming things head-on to take them down.",
                        "不止可以一头撞死",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ModItems.KIND_MISIDE_RING.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.kind_miside_ring.0",
                        "“The ring will lead you to the right direction, dear”",
                        "“戒指会带你找到正确的方向，亲爱的”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FoodItems.PIGLIN_STEW.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.piglin_stew.0",
                        "The last thing a Piglin would crave before starving, yet they never got to taste it...",
                        "屁戈林在饿死前最想吃的东西，可他最终没能吃上…",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FoodItems.PINK_COLA.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.pink_cola.0",
                        "An ordinary bottle of pink cola. Maybe there was a whole case originally?",
                        "一瓶普通的粉色可乐，或许原来有一整箱？",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ModItems.TOKYO_TEDDY_BEAR.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.tokyo_teddy_bear.0",
                        "A self - abased girl said like a broken teddy bear:",
                        "一位自卑的少女如破碎的泰迪熊般说到：",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.tokyo_teddy_bear.1",
                        "           Let me tell you",
                        "           来让你听听",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.tokyo_teddy_bear.2",
                        "           The words of all - knowing and all - powerful",
                        "           全智全能的话吧",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.tokyo_teddy_bear.3",
                        "           Other than the mind",
                        "           头脑以外的",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.tokyo_teddy_bear.4",
                        "           Is no longer needed",
                        "           已不再需要",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.tokyo_teddy_bear.5",
                        "——A story told by a spider",
                        "——由一只蜘蛛讲述的故事",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject storageItemUsage(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        TextContent retrieveCompanion = translate(language, "tooltip.confluence.storage_companion.retrieve",
                "Sneak and right-click to recall",
                "潜行右键以收回",
                textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));
        TextContent sharedVoidStorage = translate(language, "tooltip.confluence.void_storage.shared",
                "Accesses your personal storage, shared with the Void Vault",
                "访问你的个人虚空库存，与虚空保险库共用储物空间",
                textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));

        // 随身储物物品的使用与状态切换说明。
        add(root, PetItems.CHESTER_STAFF.get(), entry(vbox(0,
                retrieveCompanion), Map.of(), false, true));
        add(root, PetItems.MONEY_TROUGH.get(), entry(vbox(0,
                retrieveCompanion), Map.of(), false, true));
        add(root, ToolItems.VOID_BAG.get(), entry(vbox(0,
                sharedVoidStorage,
                translate(language, "tooltip.confluence.void_storage.overflow",
                        "When open, collects inventory overflow and allows quick use of stored potions",
                        "开启时，背包满后自动收纳物品，支持快捷使用库存内的药水",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.confluence.void_storage.toggle",
                        "Right-click with an empty cursor in the inventory to toggle open or closed",
                        "在物品栏中空手右键切换开启／关闭状态",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.VOID_VAULT.get(), entry(vbox(0,
                sharedVoidStorage), Map.of(), false, true));
        return root;
    }

    private static JsonObject toolUsage(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        TextContent switchToolMode = TipContentBuilder.translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));
        TextContent rollerUsage = translate(language, "tooltip.item.confluence.paint_roller.0",
                "Used with paint to color walls",
                "与油漆一起用于给方块单个面涂色",
                textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));
        TextContent rollerCoatings = translate(language, "tooltip.item.confluence.paint_roller.1",
                "Can also apply coatings",
                "也可以涂覆涂料",
                textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));
        TextContent scraperUsage = translate(language, "tooltip.item.confluence.paint_scraper.0",
                "Used to remove paint or coatings. Sneak to remove only one side",
                "用于去除油漆或涂料，潜行仅去除一面",
                textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));
        TextContent scraperCollectsMoss = translate(language, "tooltip.item.confluence.paint_scraper.1",
                "Can sometimes collect moss",
                "有时可以收集苔藓",
                textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));
        TextContent brushUsage = translate(language, "tooltip.item.confluence.paintbrush.0",
                "Used with paint to color blocks",
                "与油漆一起用于给方块所有面涂色",
                textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));
        TextContent brushCoatings = translate(language, "tooltip.item.confluence.paintbrush.1",
                "Can also apply coatings",
                "也可以涂覆涂料",
                textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320));

        // 钥匙、油漆工具、捕虫网、海螺及复合工具的固定操作或能力说明。当前模式、记录坐标等状态继续由 Java 输出。
        add(root, ModTags.Items.MODE_SWITCH_TOOL, entry(vbox(0, switchToolMode), Map.of(), false, true));
        add(root, ToolItems.BINOCULARS.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.binoculars.0",
                        "Expand the FOV when in use, and adjust the zoom with the mouse wheel",
                        "使用时扩大视野，鼠标滚轮可调整缩放",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.BLOODSTAINED_POWDER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.bloodstained_powder.0",
                        "Right-click to spawn vegetation in the Crimson, or infest Creepers with Crimson",
                        "右键以在猩红之地催生植被，或猩红化苦力怕",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.BUG_NET.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.bug_net.0",
                        "Used to catch critters",
                        "用来捕捉小动物",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.CORRUPTION_KEY.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.corruption_key.0",
                        "“Opens the evil room door and corruption chest in the dungeon”",
                        "“打开地牢中的邪恶房间门和腐化箱”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.CRIMSON_KEY.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.crimson_key.0",
                        "“Opens the evil room door and crimson chest in the dungeon”",
                        "“打开地牢中的邪恶房间门和猩红箱”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.DEMON_CONCH.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.demon_conch.0",
                        "Right-click a Nether Portal block to make the Nether remember you.",
                        "右键下界传送门方块以让地狱记住你",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.DESERT_KEY.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.desert_key.0",
                        "“Opens the desert room door and desert chest in the dungeon”",
                        "“打开地牢中的沙漠房间门和沙漠箱”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.DEV_BUG_NET.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.dev_bug_net.0",
                        "Can catch most creatures!",
                        "可以捕捉绝大部分生物！",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.DUNGEON_COMPASS.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.dungeon_compass.0",
                        "--“The eerie skull will guide your way”",
                        "--“诡异的头骨会为你指引方向”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.EMPTY_DROPPER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.empty_dropper.0",
                        "Right-click on the droplet to remove it (requires block assist aiming)",
                        "右击液滴以去除（需要有方块辅助瞄准）",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.ENCUMBERING_STONE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.encumbering_stone.0",
                        "Prevents item pickups while locked",
                        "锁上时能防止拾取物品",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.encumbering_stone.1",
                        "Right click in inventory to unlock",
                        "“你负重过多”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.encumbering_stone.2",
                        "'You are over-encumbered'",
                        "背包内右键点击可开关",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.FROZEN_KEY.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.frozen_key.0",
                        "“Opens the ice room door and ice chest in the dungeon”",
                        "“打开地牢中的冰雪房间门和冰雪箱”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.GOLDEN_BUG_NET.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.golden_bug_net.0",
                        "Used to catch critters",
                        "用来捕捉小动物",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.golden_bug_net.1",
                        "Can catch lava critters too!",
                        "也可以捕捉熔岩小动物！",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.GOLDEN_DUNGEON_KEY.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.golden_dungeon_key.0",
                        "“Open a locked dungeon chest or a golden lockbox”",
                        "“打开一个锁住的地牢箱或金锁盒”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.GOLDEN_KEY.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.golden_key.0",
                        "“Opens one locked Gold Chest”",
                        "“打开一个锁住的金箱”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.GUIDE_TO_CRITTER_COMPANIONSHIP.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.guide_to_critter_companionship.0",
                        "Prevents you from hurting critters while in the inventory",
                        "放在物品栏中时能防止你伤害小动物",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.guide_to_critter_companionship.1",
                        "Right click to deactivate/reactivate effects",
                        "右键点击停用/激活其效果",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.GUIDE_TO_ENVIRONMENTAL_PRESERVATION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.guide_to_environmental_preservation.0",
                        "Prevents you from accidentally destroying the environment while in the inventory",
                        "放在物品栏中时能防止你意外破坏环境",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.guide_to_environmental_preservation.1",
                        "Right click to deactivate/reactivate effects",
                        "右键点击停用/激活其效果",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.GUIDE_TO_PEACEFUL_COEXISTENCE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.guide_to_peaceful_coexistence.0",
                        "Prevents you from hurting critters while in the inventory",
                        "放在物品栏中时能防止你伤害小动物",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.guide_to_peaceful_coexistence.1",
                        "Prevents you from accidentally destroying the environment while in the inventory",
                        "放在物品栏中时能防止你意外破坏环境",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.guide_to_peaceful_coexistence.2",
                        "Right click to deactivate/reactivate effects",
                        "右键点击停用/激活其效果",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.HALLOWED_KEY.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.hallowed_key.0",
                        "“Opens the hallowed room door and hallowed chest in the dungeon”",
                        "“打开地牢中的神圣房间门和神圣箱”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ModItems.HARDMODE_CONVERTOR.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.hardmode_convertor.0",
                        "Right-clicking on the ground immediately turns the current world into Hardmode",
                        "右击地面将当前世界立刻开始转变为困难模式",
                        textOptions().color(ChatFormatting.GOLD.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.HONEY_ABSORBANT_SPONGE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.honey_absorbant_sponge.0",
                        "Capable of soaking up an endless amount of honey",
                        "能够吸收无限多蜂蜜",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.JUNGLE_KEY.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.jungle_key.0",
                        "“Opens the jungle room door and jungle chest in the dungeon”",
                        "“打开地牢中的丛林房间门和丛林箱”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.KEY_OF_LIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.key_of_light.0",
                        "Charged with the essence of many souls",
                        "“汇聚了大量灵魂的精华”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.KEY_OF_NIGHT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.key_of_night.0",
                        "Charged with the essence of many souls",
                        "“汇聚了大量灵魂的精华”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.LAVA_ABSORBANT_SPONGE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.lava_absorbant_sponge.0",
                        "Capable of soaking up an endless amount of lava",
                        "能够吸收无限多熔岩",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.LAVAPROOF_BUG_NET.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.lavaproof_bug_net.0",
                        "'For when things get too hot to handle'",
                        "“当事情变得棘手的时候使用”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.LIFE_CRYSTAL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.life_crystal.0",
                        "Permanently increases maximum life by 4",
                        "最大生命永久增加4",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.LIFE_FRUIT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.life_fruit.0",
                        "Permanently increases maximum life by 1",
                        "最大生命永久增加1",
                        textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.MAGIC_CONCH.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.magic_conch.0",
                        "Right-click a block in the Beach biome to make the sea remember you",
                        "右键海滩群系的方块以让大海记住你",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.MECHANIC_SAFE_KEY.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.mechanic_safe_key.0",
                        "“Opens mechanic safe chest in the dungeon”",
                        "“打开地牢中的机械保险箱”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.METEOR_COMPASS.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.meteor_compass.0",
                        "Only the position of the last meteorite are saved",
                        "仅保存上一次陨石的坐标",
                        textOptions().color(ChatFormatting.AQUA.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HamaxeItems.METEOR_HAMAXE.get(), entry(modifiers(vbox(0,
                        switchToolMode),
                modifierOptions().margins(0, 0, 8, 0)), Map.of(), false, true));
        add(root, HamaxeItems.MOLTEN_HAMAXE.get(), entry(modifiers(vbox(0,
                        switchToolMode),
                modifierOptions().margins(0, 0, 8, 0)), Map.of(), false, true));
        add(root, ToolItems.NPC_INVITATION.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.npc_invitation.0",
                        "Use it to invite a new batch of NPCs in the current area!",
                        "使用它，在当前区域邀请一批新的NPC吧！",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.OCEAN_KEY.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.ocean_key.0",
                        "“Opens the ocean room door and ocean chest in the dungeon”",
                        "“打开地牢中的海洋房间门和海洋箱”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PaintItems.PAINT_ROLLER.get(), entry(vbox(0,
                rollerUsage,
                rollerCoatings), Map.of(), false, true));
        add(root, PaintItems.PAINT_SCRAPER.get(), entry(vbox(0,
                scraperUsage,
                scraperCollectsMoss), Map.of(), false, true));
        add(root, PaintItems.PAINTBRUSH.get(), entry(vbox(0,
                brushUsage,
                brushCoatings), Map.of(), false, true));
        add(root, ModItems.PARADOX_INTERACTIVE_MEDAL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.paradox_interactive_medal.0",
                        "Proof of having played Hearts of Iron, Victoria, Europa Universalis, Crusader Kings, and Cities: Skylines at the same time.",
                        "同时玩过钢铁雄心、维多利亚、欧陆风云、十字军之王、都市天际线的证明。",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.RECALL_LIFE_CRYSTAL.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.recall_life_crystal.0",
                        "Permanently reduces maximum life by 4",
                        "最大生命永久减少4",
                        textOptions().color(ChatFormatting.RED.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.ROTTEN_BONE_DUST.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.rotten_bone_dust.0",
                        "Right-click to spawn vegetation in the Corruption, or corrupt Skeletons",
                        "右键以在腐化之地催生植被，或腐化骷髅",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.RUST_IRON_KEY.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.rust_iron_key.0",
                        "“Opens the laboratory room door in the dungeon”",
                        "“打开地牢中的实验室房间门”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.SHADOW_KEY.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.shadow_key.0",
                        "“Opens all Shadow Chests and Obsidian Lock Boxes”",
                        "“打开所有暗影箱和黑曜石锁盒”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PaintItems.SPECTRE_PAINT_ROLLER.get(), entry(vbox(0,
                rollerUsage,
                rollerCoatings), Map.of(), false, true));
        add(root, PaintItems.SPECTRE_PAINT_SCRAPER.get(), entry(vbox(0,
                scraperUsage,
                scraperCollectsMoss), Map.of(), false, true));
        add(root, PaintItems.SPECTRE_PAINTBRUSH.get(), entry(vbox(0,
                brushUsage,
                brushCoatings), Map.of(), false, true));
        add(root, ToolItems.SUPER_ABSORBANT_SPONGE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.super_absorbant_sponge.0",
                        "Capable of soaking up an endless amount of water",
                        "能够吸收无限多水",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.TEMPLE_KEY.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.temple_key.0",
                        "“Opens the jungle temple door”",
                        "“打开丛林神庙的大门”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.ULTRA_ABSORBANT_SPONGE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.ultra_absorbant_sponge.0",
                        "Capable of soaking up an endless amount of liquid",
                        "能够吸收无限多液体",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.UNIVERSE_KEY.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.universe_key.0",
                        "“Opens the space room door and space chest in the dungeon”",
                        "“打开地牢中的太空房间门和太空箱”",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.WIRE_CUTTER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.wire_cutter.0",
                        "Removes wire",
                        "移除电线",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject weaponLore(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        // 武器的背景故事与风味文字。伤害、攻速、暴击、前缀和可动组件由原有 Java 实现负责。
        add(root, SwordItems.BEE_KEEPER.get(), entry(modifiers(vbox(0,
                        cycleText(translate(language, "tooltip.item.confluence.bee_keeper.0",
                                "A small part of the divine weapon's power is possessed by the swarm of bees in the jungle, becoming a buzzing hive.",
                                "神兵的一小部分力量为丛林中的蜂群所占有，沦为嗡嗡作响的蜂群",
                                textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#FFE082", "#FFC857", "#FFF1AD"),
                        cycleText(translate(language, "tooltip.item.confluence.bee_keeper.1",
                                "\"Sweet on the outside, sharp on the inside.\"",
                                "“口蜜腹剑”",
                                textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#FFE082", "#FFC857", "#FFF1AD")),
                modifierOptions().margins(0, 0, 8, 0)), Map.of(), false, true));
        add(root, SwordItems.COPPER_SHORT_SWORD.get(), entry(vbox(0,
                cycleText(translate(language, "tooltip.item.confluence.copper_short_sword.0",
                        "The smallest fragment of the divine weapon's power has been with you since the confluence of the two worlds... until the journey's end.",
                        "神兵最微末的那部分力量自两世汇流之初就伴随着你…直至旅途的终点",
                        textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#DCA078", "#F2C69C", "#C98B65"),
                cycleText(translate(language, "tooltip.item.confluence.copper_short_sword.1",
                        "\"We are so awesome!\" said the copper short sword.",
                        "“我们十个真厉害！”铜短剑说。",
                        textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#DCA078", "#F2C69C", "#C98B65")), Map.of(), false, true));
        add(root, SwordItems.ENCHANTED_SWORD.get(), entry(vbox(0,
                cycleText(translate(language, "tooltip.item.confluence.enchanted_sword.0",
                        "A small part of the divine weapon's power is buried in a cave, condensed into a bleak sword intent.",
                        "神兵的一小部分力量埋藏在洞穴中，凝成了寒碜碜的剑意",
                        textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#9CCBFF", "#C6B5FF", "#B8E9FF"),
                cycleText(translate(language, "tooltip.item.confluence.enchanted_sword.1",
                        "\"A flash of light in the dim sword tomb.\"",
                        "“昏暗剑冢中一抹流光”",
                        textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#9CCBFF", "#C6B5FF", "#B8E9FF")), Map.of(), false, true));
        add(root, AxeItems.LUCY_THE_AXE.get(), entry(vbox(0,
                cycleText(translate(language, "tooltip.item.confluence.lucy_the_axe.0",
                        "'I love Lucy!'",
                        "“我爱露西！”",
                        textOptions().color(ChatFormatting.WHITE.getName()).maxWidth(320)), 12.0, "smooth", "#F0A3A3", "#FFC58F", "#EAB0CB")), Map.of(), false, true));
        add(root, SwordItems.STARFURY.get(), entry(vbox(0,
                cycleText(translate(language, "tooltip.item.confluence.starfury.0",
                        "A small part of the divine weapon's power is condensed within the clouds, transforming into a crystalline and sparkling star.",
                        "神兵的一小部分力量凝聚在云团间，化为晶莹剔透的闪星",
                        textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#FFB3DA", "#D3B4FF", "#FFF1AD"),
                cycleText(translate(language, "tooltip.item.confluence.starfury.1",
                        "\"The wrath of the heavens pours down.\"",
                        "“天域的怒火倾泻而下”",
                        textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#FFB3DA", "#D3B4FF", "#FFF1AD")), Map.of(), false, true));
        add(root, SwordItems.SWEET_SWORD.get(), entry(vbox(0,
                cycleText(translate(language, "tooltip.item.confluence.sweet_sword.0",
                        "au'undertale: above nothingness' written by 一只屑水缡",
                        "来自一只屑水缡写的au“传说之下：虚无之上”",
                        textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#FFB1D4", "#FFD0EB", "#C9B8FF")), Map.of(), false, true));
        add(root, SwordItems.TRUE_COPPER_SHORTSWORD.get(), entry(vbox(0,
                cycleText(translate(language, "tooltip.item.confluence.true_copper_shortsword.0",
                        "Embraced by Shimmer, the divine weapon reveals the source of its power.",
                        "神兵投入微光的怀抱，将其力量之源泉展现",
                        textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#E1A16E", "#FFE0A3", "#F3BC70"),
                cycleText(translate(language, "tooltip.item.confluence.true_copper_shortsword.1",
                        "It may not be the sword that accompanied you throughout your journey, but you were to remember that distant afternoon when you first opened Confluence: The Otherworld.",
                        "它或许不是陪伴你一路走来的那把剑，但，它可能会让你回忆起，你第一次打开“汇流来世”的那个遥远的下午",
                        textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#E1A16E", "#FFE0A3", "#F3BC70"),
                cycleText(translate(language, "tooltip.item.confluence.true_copper_shortsword.2",
                        "“I told you, the ten of us are amazing!” said the brand-new, lucky Copper Shortsword.",
                        "“我就说嘛，我们十个真厉害！”那把崭新的，幸运的铜短剑如是说。",
                        textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#E1A16E", "#FFE0A3", "#F3BC70")), Map.of(), false, true));
        add(root, SwordItems.ZENITH.get(), entry(vbox(0,
                cycleText(translate(language, "tooltip.item.confluence.zenith.0",
                        "When ten scattered pieces are found again and ten powers converge, this blazing weapon will descend from the zenith of the sky, bestowed upon the hero who liberated Confluence: Otherworld.",
                        "当十块散碎的拼图被重新找回，十股力量汇流之际，这把炽热的神兵将从天穹之顶降下，赐予解放汇合交流来世之英雄",
                        textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#FFAAAA", "#FFD594", "#FFF1A0", "#ACEDC1", "#A8D8FF", "#D0B4FF"),
                cycleText(translate(language, "tooltip.item.confluence.zenith.1",
                        "“Why not try throwing it into Shimmer?”",
                        "“为什么不丢进微光里试试呢？”",
                        textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#FFAAAA", "#FFD594", "#FFF1A0", "#ACEDC1", "#A8D8FF", "#D0B4FF")), Map.of(), false, true));
        return root;
    }

    private static JsonObject weaponUsage(TranslationConsumer language) {
        JsonObject root = new JsonObject();
        add(root, ManaWeaponItems.CRYSTAL_SERPENT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.crystal_serpent.0",
                        "Shoots an explosive crystal charge",
                        "射出爆炸水晶电荷",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ManaWeaponItems.DUSK_DAWN.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.dusk_dawn.0",
                        "Alternately fires a sunlight bolt and a moonlight bolt that park a little sun or little moon overhead (sun: 10% area damage per second; moon: 80% ray every 2s)",
                        "交替发射日光与月光弹幕，命中后挂在敌怪头顶：小日光每秒 10% 范围伤害，小月光每 2 秒一道 80% 射线",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.dusk_dawn.1",
                        "Hitting a hosted foe only triggers it (no refresh, no swap); killing a hosted foe fires one homing bolt",
                        "命中已有附着的敌怪只会触发其效果（不刷新、不替换）；附着中的敌怪被击杀时补发一发索敌弹幕",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ManaWeaponItems.FLAMELASH.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.flamelash.0",
                        "Summons a controllable ball of fire",
                        "召唤可控的火球",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ManaWeaponItems.MAGIC_MISSILE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.magic_missile.0",
                        "Casts a controllable missile",
                        "发射一枚可控的飞弹",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ManaWeaponItems.OPAL_STAFF.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.opal_staff.0",
                        "Fires a wide torrent that falls, surges along the ground and then returns to the caster",
                        "射出一道大范围洪流，落地后贴地奔涌，随后折返施法者",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.opal_staff.1",
                        "The torrent deals double damage on its way back, and gains 50% of your melee damage",
                        "洪流折返途中伤害翻倍，并额外获得 50% 的近战伤害加成",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ManaWeaponItems.RAINBOW_ROD.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.rainbow_rod.0",
                        "Casts a controllable rainbow",
                        "发射可控的彩虹",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ManaWeaponItems.STAR_MUSIC.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.star_music.0",
                        "Hold right-click: every 0.5s gathers one more falling star for 5 mana, up to 20 stars; it releases automatically at full charge",
                        "长按右键凝聚坠星：每 0.5 秒多凝聚 1 颗并多消耗 5 点魔力，最多 20 颗；蓄满后自动释放",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ManaWeaponItems.THUNDERSTRIKE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.thunderstrike.0",
                        "Hold right-click: 1–2 initial bolts every 2 ticks, up to 32 blocks; extra feeders connect distant enemies. Every branch deals magic damage.",
                        "按住右键：每 2 tick 释放 1–2 道初始闪电，射程 32 格；远处敌人可引出额外上级电流，所有支干均造成魔法伤害。",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ManaWeaponItems.WIND_EYE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.wind_eye.0",
                        "Creates a tornado that pulls in enemies; recasting removes the previous one, and only the first projectile fired into its core is absorbed",
                        "制造持续吸附敌人的旋风；再次使用会移除上一道，只吸收第一枚打入中心的弹幕",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ManaWeaponItems.WIND_PAGE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.wind_page.0",
                        "Releases three wind bolts that rise, then home in on the nearest enemy",
                        "一次释放三发风弹，先向上飞再追踪最近的敌人",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ManaWeaponItems.ARC_SURGE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.arc_surge.0",
                        "Unleashes powerful lightning to strike your foes",
                        "释放强力闪电轰击敌人",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.arc_surge.1",
                        "'Unlimited power!'",
                        "“无限力量！”",
                        textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320)),
                translate(language, "tooltip.item.confluence.arc_surge.2",
                        "Hold right-click to repeatedly discharge lightning.",
                        "按住右键持续释放闪电。",
                        textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320))), Map.of(), false, true));
        // 芦苇、雨伞等兼作工具的武器固定用途说明。
        add(root, SwordItems.BREATHING_REED.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.breathing_reed.0",
                        "Increases breath time and allows breathing in water",
                        "延长呼吸时间并可在水中呼吸",
                        textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320))), Map.of(), false, true));
        add(root, SwordItems.TRAGIC_UMBRELLA.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.tragic_umbrella.0",
                        "You will fall slower while holding this",
                        "持有此物可减缓掉落速度",
                        textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320))), Map.of(), false, true));
        add(root, SwordItems.UMBRELLA.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.umbrella.0",
                        "You will fall slower while holding this",
                        "持有此物可减缓掉落速度",
                        textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320))), Map.of(), false, true));
        add(root, BoomerangItems.BEIDOU_BOOMERANG.get(), entry(vbox(0,
                TipContentBuilder.translate("tooltip.item.confluence.bei_dou_boomerang.0", textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320)),
                translate(language, "tooltip.item.confluence.bei_dou_boomerang.1",
                        "Red: Hellfire IV",
                        "红：狱炎 IV",
                        textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320)),
                translate(language, "tooltip.item.confluence.bei_dou_boomerang.2",
                        "Light Blue: Frozen IV",
                        "淡蓝：冰冻 IV",
                        textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320)),
                translate(language, "tooltip.item.confluence.bei_dou_boomerang.3",
                        "Light Gray: Stoned IV",
                        "淡灰：石化 IV",
                        textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320)),
                translate(language, "tooltip.item.confluence.bei_dou_boomerang.4",
                        "Dark Green: Poison IV / Gray Purple: Wither IV",
                        "深绿：中毒 IV／灰紫：凋零 IV",
                        textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320)),
                translate(language, "tooltip.item.confluence.bei_dou_boomerang.5",
                        "Dark Brownish Red: Instant Damage VII",
                        "深红棕：瞬间伤害 VII",
                        textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320))), Map.of(), false, true));
        // 魂师主武器：自带效果说明（与 SoulWeaponItem.Trait 一一对应）
        add(root, SoulItems.BROKEN_GLIMMER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.broken_glimmer.0",
                        "While held, collecting soul has a 20% chance to restore 4 health",
                        "手持时，每次获得灵魂有 20% 概率回复 4 点生命",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, SoulItems.STAR_SPIRIT.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.star_spirit.0",
                        "Soul skill hits have a 30% chance to call down an extra falling star on that enemy",
                        "灵魂技能命中敌人时，有 30% 概率额外召唤一颗坠星砸向该敌人",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, SoulItems.SOUL_CHANT_GUIDE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.soul_chant_guide.0",
                        "Soul lure projectiles pierce 2 more enemies",
                        "引魂系列弹幕额外穿透 2 个敌怪",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, SoulItems.SENIOR_PATHFINDER_PRO.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.senior_pathfinder_pro.0",
                        "Soul lure projectiles home in on nearby enemies",
                        "引魂系列弹幕会追踪附近的敌怪",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, SoulItems.LIFE_SPLENDOR.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.life_splendor.0",
                        "Soul skill hits also poison the target for 5 seconds",
                        "灵魂技能命中敌人时，额外附加 5 秒中毒",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, SoulItems.DAY_AND_NIGHT_ALIKE.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.day_and_night_alike.0",
                        "Soul skill hits also apply shadowflame for 5 seconds",
                        "灵魂技能命中敌人时，额外附加 5 秒暗影焰",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, SoulItems.TRUFFLE_LANTERN.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.truffle_lantern.0",
                        "While held, soul skill cooldowns are 30% shorter",
                        "手持时，灵魂技能冷却缩短 30%",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        // 鞭子命中后的特殊效果；纯数值鞭子没有特殊效果，不生成条目。
        add(root, WhipItems.SWAMP_WHIP.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.swamp_whip.0",
                        "Slows the target for 2 seconds on hit",
                        "命中后使目标减速 2 秒",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, WhipItems.SNAPTHORN.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.snapthorn.0",
                        "Grants Jungle's Fury for 3 seconds on hit (whip speed +12%)",
                        "命中后获得 3 秒丛林之怒（挥鞭速度 +12%）",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.snapthorn.1",
                        "20% chance to poison the target for 3 seconds",
                        "20% 概率使目标中毒 3 秒",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, WhipItems.FIRECRACKER.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.firecracker.0",
                        "Inflicts Hellfire on the target for 4 seconds",
                        "命中后使目标陷入 4 秒狱炎",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate(language, "tooltip.item.confluence.firecracker.1",
                        "When a summon hits the marked target it detonates: 2.75x damage in a 6-block cube around the hit, consuming the mark",
                        "召唤物攻击带标记的目标时会引爆：以命中点为中心 6 格范围内造成 2.75 倍伤害，并消耗标记",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, WhipItems.WIND_WHIP.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.wind_whip.0",
                        "Deals 6.25% more damage per block of height difference, up to +75% at 12 blocks",
                        "与目标的高度差每 1 格提高 6.25% 伤害，最高 +75%（12 格）",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, WhipItems.STAR_FEAR.get(), entry(vbox(0,
                translate(language, "tooltip.item.confluence.star_fear.0",
                        "10% chance on hit to knock out a falling star; catching it grants Star Harvest for 5 minutes (+1 minion slot)",
                        "命中时有 10% 概率冒出一颗坠星，接住它可获得 5 分钟摘星（仆从栏位 +1）",
                        textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }
}
