package org.confluence.mod.common.data.gen;

import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import org.confluence.mod.common.init.block.CrateBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.*;

import java.nio.file.Path;
import java.util.ArrayList;
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
        tasks.add(DataProvider.saveStable(output, accessoryDescriptions(), directory.resolve("accessory_descriptions.json")));
        tasks.add(DataProvider.saveStable(output, armorDescriptions(), directory.resolve("armor_descriptions.json")));
        tasks.add(DataProvider.saveStable(output, baitUsage(), directory.resolve("bait_usage.json")));
        tasks.add(DataProvider.saveStable(output, bossSummoning(), directory.resolve("boss_summoning.json")));
        tasks.add(DataProvider.saveStable(output, eventSummoning(), directory.resolve("event_summoning.json")));
        tasks.add(DataProvider.saveStable(output, functionalBlockDescriptions(), directory.resolve("functional_block_descriptions.json")));
        tasks.add(DataProvider.saveStable(output, lootContainerUsage(), directory.resolve("loot_container_usage.json")));
        tasks.add(DataProvider.saveStable(output, materialDescriptions(), directory.resolve("material_descriptions.json")));
        tasks.add(DataProvider.saveStable(output, permanentUpgradeDescriptions(), directory.resolve("permanent_upgrade_descriptions.json")));
        tasks.add(DataProvider.saveStable(output, potionDescriptions(), directory.resolve("potion_descriptions.json")));
        tasks.add(DataProvider.saveStable(output, sponsorItemLore(), directory.resolve("sponsor_item_lore.json")));
        tasks.add(DataProvider.saveStable(output, storageItemUsage(), directory.resolve("storage_item_usage.json")));
        tasks.add(DataProvider.saveStable(output, toolUsage(), directory.resolve("tool_usage.json")));
        tasks.add(DataProvider.saveStable(output, weaponLore(), directory.resolve("weapon_lore.json")));
        tasks.add(DataProvider.saveStable(output, weaponUsage(), directory.resolve("weapon_usage.json")));
        return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Confluence DataTip";
    }

    private static void add(JsonObject root, ItemLike item, JsonObject content) {
        root.add(BuiltInRegistries.ITEM.getKey(item.asItem()).toString(), content);
    }

    private static JsonObject accessoryDescriptions() {
        JsonObject root = new JsonObject();
        // Confluence 注册的饰品固定能力说明。
        add(root, AccessoryItems.ADHESIVE_BANDAGE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.adhesive_bandage.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.ANGLER_TACKLE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.angler_tackle_bag.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.ARCANE_FLOWER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.arcane_flower.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.arcane_flower.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.arcane_flower.2", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.ARGON_MOSS_FISHING_BOBBER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.argon_moss_fishing_bobber.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.ARMOR_BRACING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.armor_bracing.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.ARMOR_POLISH.get(), entry(vbox(0,
                translate("tooltip.item.confluence.armor_polish.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.BAND_OF_STARPOWER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.band_of_starpower.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.BLACK_COUNTERWEIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.black_counterweight.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.BLACK_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.black_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.BLUE_COUNTERWEIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.blue_counterweight.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.BLUE_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.blue_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.BROWN_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.brown_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.CELESTIAL_CUFFS.get(), entry(vbox(0,
                translate("tooltip.item.confluence.celestial_cuffs.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.celestial_cuffs.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.celestial_cuffs.2", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.CELESTIAL_EMBLEM.get(), entry(vbox(0,
                translate("tooltip.item.confluence.celestial_emblem.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.CELESTIAL_MAGNET.get(), entry(vbox(0,
                translate("tooltip.item.confluence.celestial_magnet.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.CHARM_OF_MYTHS.get(), entry(vbox(0,
                translate("tooltip.item.confluence.charm_of_myths.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.CHROMATIC_CLOAK.get(), entry(vbox(0,
                translate("tooltip.item.confluence.chromatic_cloak.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.CLOTHIER_VOODOO_DOLL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.clothier_voodoo_doll.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.COIN_RING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.coin_ring.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.COUNTERCURSE_MANTRA.get(), entry(vbox(0,
                translate("tooltip.item.confluence.countercurse_mantra.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.CYAN_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.cyan_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.DISCOUNT_CARD.get(), entry(vbox(0,
                translate("tooltip.item.confluence.discount_card.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.EXPERIENCE_PIPETTE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.experience_pipette.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.FISHING_BOBBER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.fishing_bobber.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.GLOWING_FISHING_BOBBER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.glowing_fishing_bobber.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.GOLD_RING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.gold_ring.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.GREEDY_RING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.greedy_ring.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.GREEN_COUNTERWEIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.green_counterweight.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.GREEN_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.green_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.GUIDE_TO_PLANT_FIBER_CORDAGE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.guide_to_plant_fiber_cordage.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.guide_to_plant_fiber_cordage.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.guide_to_plant_fiber_cordage.2", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.guide_to_plant_fiber_cordage.3", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.HELIUM_MOSS_FISHING_BOBBER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.helium_moss_fishing_bobber.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.HIGH_TEST_FISHING_LINE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.high_test_fishing_line.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.KRYPTON_MOSS_FISHING_BOBBER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.krypton_moss_fishing_bobber.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.LAVA_MOSS_FISHING_BOBBER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.lava_moss_fishing_bobber.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.LAVAPROOF_FISHING_HOOK.get(), entry(vbox(0,
                translate("tooltip.item.confluence.lavaproof_fishing_hook.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.LAVAPROOF_TACKLE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.lavaproof_tackle_bag.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.lavaproof_tackle_bag.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.LIME_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.lime_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.LUCKY_COIN.get(), entry(vbox(0,
                translate("tooltip.item.confluence.lucky_coin.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MAGIC_CUFFS.get(), entry(vbox(0,
                translate("tooltip.item.confluence.magic_cuffs.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.magic_cuffs.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MAGIC_YOYO_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.magic_yoyo_bag.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MAGNET_FLOWER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.magnet_flower.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.magnet_flower.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.magnet_flower.2", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MANA_CLOAK.get(), entry(vbox(0,
                translate("tooltip.item.confluence.mana_cloak.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.mana_cloak.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.mana_cloak.2", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.mana_cloak.3", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MANA_FLOWER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.mana_flower.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.mana_flower.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MANA_REGENERATION_BAND.get(), entry(vbox(0,
                translate("tooltip.item.confluence.mana_regeneration_band.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.mana_regeneration_band.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MECHANICAL_LENS.get(), entry(vbox(0,
                translate("tooltip.item.confluence.mechanical_lens.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.mechanical_lens.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MEDICATED_BANDAGE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.medicated_bandage.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.MEGAPHONE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.megaphone.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.NATURES_GIFT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.natures_gift.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.NAZAR.get(), entry(vbox(0,
                translate("tooltip.item.confluence.nazar.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.NEON_MOSS_FISHING_BOBBER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.neon_moss_fishing_bobber.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.ORANGE_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.orange_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.PAINT_SPRAYER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.paint_sprayer.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.PHILOSOPHERS_STONE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.philosophers_stone.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.PINK_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.pink_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.POCKET_MIRROR.get(), entry(vbox(0,
                translate("tooltip.item.confluence.pocket_mirror.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.PURPLE_COUNTERWEIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.purple_counterweight.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.PURPLE_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.purple_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.RADIO_THING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.radio_thing.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.radio_thing.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.RAINBOW_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.rainbow_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.RED_COUNTERWEIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.red_counterweight.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.RED_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.red_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.REFLECTIVE_SHADES.get(), entry(vbox(0,
                translate("tooltip.item.confluence.reflective_shades.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.SHINY_PIPETTE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.shiny_pipette.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.SKY_BLUE_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.sky_blue_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.SPECTRE_GOGGLES.get(), entry(vbox(0,
                translate("tooltip.item.confluence.spectre_goggles.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.terra_curio.spectre_goggles.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.STRUNG_COUNTERWEIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.strung_counterweight.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.TACKLE_BOX.get(), entry(vbox(0,
                translate("tooltip.item.confluence.tackle_box.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.TEAL_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.teal_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.TINSEL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.tinsel.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.VIOLET_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.violet_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.WHITE_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.white_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.XENON_MOSS_FISHING_BOBBER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.xenon_moss_fishing_bobber.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.YELLOW_COUNTERWEIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.yellow_counterweight.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.YELLOW_YOYO_STRING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.yellow_string.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.YOYO_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.yoyo_bag.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, AccessoryItems.YOYO_GLOVE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.yoyo_glove.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject armorDescriptions() {
        JsonObject root = new JsonObject();
        // 盔甲的固定能力说明；实际属性与套装提示继续由 Java 输出。
        add(root, ArmorItems.ADAMANTITE_HEADGEAR.get(), entry(vbox(0,
                translate("tooltip.item.confluence.adamantite_headgear.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.AMBER_ROBE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.amber_robe.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.amber_robe.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.AMETHYST_ROBE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.amethyst_robe.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.amethyst_robe.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.ANGLER_HAT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.angler_hat.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.ANGLER_PANTS.get(), entry(vbox(0,
                translate("tooltip.item.confluence.angler_pants.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.ANGLER_VEST.get(), entry(vbox(0,
                translate("tooltip.item.confluence.angler_vest.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.COBALT_HAT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.cobalt_hat.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.COLD_CRYSTAL_CHESTPLATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.cold_crystal_chestplate.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.COLD_CRYSTAL_HELMET.get(), entry(vbox(0,
                translate("tooltip.item.confluence.cold_crystal_helmet.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.DIAMOND_ROBE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.diamond_robe.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.diamond_robe.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.HEIM_HELMET.get(), entry(vbox(0,
                translate("tooltip.item.confluence.heim_helmet.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.JADE_ROBE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.jade_robe.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.jade_robe.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.JUNGLE_CHESTPLATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.jungle_chestplate.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.JUNGLE_HELMET.get(), entry(vbox(0,
                translate("tooltip.item.confluence.jungle_helmet.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.JUNGLE_LEGGINGS.get(), entry(vbox(0,
                translate("tooltip.item.confluence.jungle_leggings.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.MINING_HELMET.get(), entry(vbox(0,
                translate("tooltip.item.confluence.mining_helmet.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.MYTHRIL_HOOD.get(), entry(vbox(0,
                translate("tooltip.item.confluence.mythril_hood.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.ORICHALCUM_HEADGEAR.get(), entry(vbox(0,
                translate("tooltip.item.confluence.orichalcum_headgear.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.PALLADIUM_HEADGEAR.get(), entry(vbox(0,
                translate("tooltip.item.confluence.palladium_headgear.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.RUBY_ROBE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.ruby_robe.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.ruby_robe.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.SAPPHIRE_ROBE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.sapphire_robe.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.sapphire_robe.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.TITANIUM_HEADGEAR.get(), entry(vbox(0,
                translate("tooltip.item.confluence.titanium_headgear.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ArmorItems.TOPAZ_ROBE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.topaz_robe.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.topaz_robe.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject baitUsage() {
        JsonObject root = new JsonObject();
        // 所有本体鱼饵共用的自动使用指引；实际渔力继续由 Java 输出。
        add(root, BaitItems.APPRENTICE_BAIT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.BLACK_DRAGONFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.BLACK_SCORPION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.BLUE_DRAGONFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.BLUE_JELLYFISH.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.BUGGY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.ENCHANTED_NIGHTCRAWLER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.FIREFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.GLOWING_SNAIL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.GOLD_BUTTERFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.GOLD_DRAGONFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.GOLD_GRASSHOPPER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.GOLD_LADYBUG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.GOLD_WORM.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.GOLD_WATER_STRIDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.GRASSHOPPER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.GREEN_DRAGONFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.GREEN_JELLYFISH.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.GRUBBY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.HELL_BUTTERFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.JOURNEYMAN_BAIT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.JULIA_BUTTERFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.LADYBUG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.LAVAFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.LIGHTNING_BUG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.MAGGOT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.MAGMA_SNAIL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.MASTER_BAIT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.MONARCH_BUTTERFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.ORANGE_DRAGONFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.PINK_JELLYFISH.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.PRISMATIC_LACEWING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.PURPLE_EMPEROR_BUTTERFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.RED_ADMIRAL_BUTTERFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.RED_DRAGONFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.SCORPION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.SLUGGY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.SNAIL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.STINKBUG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.SULPHUR_BUTTERFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.TREE_NYMPH_BUTTERFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.TRUFFLE_WORM.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.ULYSSES_BUTTERFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.WATER_STRIDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.WORM.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.YELLOW_DRAGONFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, BaitItems.ZEBRA_SWALLOWTAIL_BUTTERFLY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bait.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject bossSummoning() {
        JsonObject root = new JsonObject();
        // Boss 召唤物的使用条件、背景故事和风味文字；每个物品按原行号排列。
        add(root, ConsumableItems.ABEEMINATION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.abeemination.0", textOptions().color(ChatFormatting.YELLOW.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.abeemination.1", textOptions().color(ChatFormatting.YELLOW.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.abeemination.2", textOptions().color(ChatFormatting.YELLOW.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.abeemination.3", textOptions().color(ChatFormatting.YELLOW.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.BLOODY_SPINE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bloody_spine.0", textOptions().color(ChatFormatting.RED.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.bloody_spine.1", textOptions().color(ChatFormatting.RED.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.bloody_spine.2", textOptions().color(ChatFormatting.RED.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.DEER_THING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.deer_thing.0", textOptions().color(ChatFormatting.AQUA.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.deer_thing.1", textOptions().color(ChatFormatting.AQUA.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.deer_thing.2", textOptions().color(ChatFormatting.AQUA.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.SLIME_CROWN.get(), entry(vbox(0,
                translate("tooltip.item.confluence.slime_crown.0", textOptions().color(ChatFormatting.BLUE.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.slime_crown.1", textOptions().color(ChatFormatting.BLUE.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.slime_crown.2", textOptions().color(ChatFormatting.BLUE.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.SUSPICIOUS_LOOKING_EYE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.suspicious_looking_eye.0", textOptions().color(ChatFormatting.RED.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.suspicious_looking_eye.1", textOptions().color(ChatFormatting.RED.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.suspicious_looking_eye.2", textOptions().color(ChatFormatting.RED.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.WORM_FOOD.get(), entry(vbox(0,
                translate("tooltip.item.confluence.worm_food.0", textOptions().color(ChatFormatting.DARK_PURPLE.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.worm_food.1", textOptions().color(ChatFormatting.DARK_PURPLE.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.worm_food.2", textOptions().color(ChatFormatting.DARK_PURPLE.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject eventSummoning() {
        JsonObject root = new JsonObject();
        // 血月、哥布林军队、海盗入侵等事件召唤物的说明和风味文字。
        add(root, ConsumableItems.BLOOD_TEAR.get(), entry(vbox(0,
                translate("tooltip.item.confluence.blood_tear.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.blood_tear.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.GOBLIN_BATTLE_STANDARD.get(), entry(vbox(0,
                translate("tooltip.item.confluence.goblin_battle_standard.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.PIRATE_MAP.get(), entry(vbox(0,
                translate("tooltip.item.confluence.pirate_map.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject functionalBlockDescriptions() {
        JsonObject root = new JsonObject();
        // 制作站、家具、功能方块与可接线方块的固定说明；同一方块的提示合并在同一条目。
        add(root, FunctionalBlocks.ADAMANTITE_FORGE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.hardmode_forge.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.ALCHEMY_TABLE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.alchemy_table.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.AMMO_BOX.get(), entry(vbox(0,
                translate("tooltip.item.confluence.ammo_box.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.BEWITCHING_TABLE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bewitching_table.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.BLEND_O_MATIC.get(), entry(vbox(0,
                translate("tooltip.item.confluence.blend_o_matic.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.BOUNCY_BOULDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.CHLOROPHYTE_EXTRACTINATOR.get(), entry(vbox(0,
                translate("tooltip.item.confluence.chlorophyte_extractinator.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.chlorophyte_extractinator.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.chlorophyte_extractinator.2", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.DART_TRAP.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.DEEPSLATE_DART_TRAP.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.DEEPSLATE_PRESSURE_BLOCK.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.DEEPSLATE_PRESSURE_PLATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.DETONATOR.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.DYE_VAT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.dye_vat.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.ECHO_BLOCK.get(), entry(vbox(0,
                translate("tooltip.item.confluence.echo_block.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.EXPLODE_BOULDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.EXTRACTINATOR.get(), entry(vbox(0,
                translate("tooltip.item.confluence.extractinator.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.FLAME_TRAP.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.FOLLOWER_BOULDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.GEYSER_BLOCK.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.GHOULDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.HEART_LANTERN.get(), entry(vbox(0,
                translate("tooltip.item.confluence.heart_lantern.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.HEAVY_WORK_BENCH.get(), entry(vbox(0,
                translate("tooltip.item.confluence.heavy_work_bench.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.HELLFORGE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.hellforge.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.INSTANTANEOUS_EXPLOSION_TNT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.KEG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.keg.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.LAND_MINE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.LAVA_BOULDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.LEVER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.LIFE_CAMPFIRE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.life_campfire.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.LOOM.get(), entry(vbox(0,
                translate("tooltip.item.confluence.loom.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.MEAT_GRINDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.meat_grinder.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.MECHANICAL_FRAGILE_OBSIDIAN_BRICKS.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.MECHANICAL_FRAGILE_SANDSTONE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.MYTHRIL_ANVIL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.hardmode_anvil.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.NORMAL_BOULDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.OAK_LOG_BOULDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.ORICHALCUM_ANVIL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.hardmode_anvil.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.PEACE_CANDLE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.peace_candle.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.PIGGY_BANK.get(), entry(vbox(0,
                translate("tooltip.item.confluence.piggy_bank.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.piggy_bank.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.PLAYER_PRESSURE_PLATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.POO_BOULDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.RAINBOW_BOULDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.ROLLING_CACTUS_BOULDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SAFE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.safe.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.safe.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SAWMILL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.sawmill.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SHARPENING_STATION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.sharpening_station.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SIGNAL_ADAPTER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SKY_MILL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.sky_mill.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SOLIDIFIER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.solidifier.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SPEAR_TRAP.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SPIDER_BOULDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SPIKY_BALL_TRAP.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.STAR_IN_A_BOTTLE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.star_in_a_bottle.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.STONE_DART_TRAP.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.STONE_PRESSURE_BLOCK.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.STONE_PRESSURE_PLATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SUPER_DART_TRAP.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SWITCH.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.TIMERS_BLOCK_1_1.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.TIMERS_BLOCK_1_2.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.TIMERS_BLOCK_1_4.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.TIMERS_BLOCK_3_1.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.TIMERS_BLOCK_5_1.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.TITANIUM_FORGE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.hardmode_forge.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.TUFF_BOOTH.get(), entry(vbox(0,
                translate("tooltip.item.confluence.tuff_booth.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.WATER_CANDLE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.water_candle.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.SHADOW_CANDLE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.shadow_candle.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.wireable.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject lootContainerUsage() {
        JsonObject root = new JsonObject();
        // 宝匣、宝藏袋、礼包和锁盒的打开及放置指引。
        add(root, CrateBlocks.AZURE_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.BOREAL_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, TreasureBagItems.BRAIN_OF_CTHULHU_TREASURE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.BRAMBLE_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.CAN_OF_WORMS.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.CHRISTMAS_GIFT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.CLAM.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.CORRUPT_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.CRIMSON_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, TreasureBagItems.DEERCLOPS_TREASURE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.DEFILED_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.DELUXE_PACKAGE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.DIVINE_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.DUNGEON_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, TreasureBagItems.EATER_OF_WORLDS_TREASURE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, TreasureBagItems.EYE_OF_CTHULHU_TREASURE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.FROZEN_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.GOLDEN_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.GOLDEN_LOCK_BOX.get(), entry(vbox(0,
                translate("tooltip.item.confluence.golden_lock_box.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.golden_lock_box.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.GOODIE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.HALLOWED_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.HELLSTONE_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.HEMATIC_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.HERB_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, TreasureBagItems.HILL_OF_FLESH_TREASURE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.IRON_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.JUNGLE_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, TreasureBagItems.KING_SLIME_TREASURE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, TreasureBagItems.LUNATIC_CULTIST_TREASURE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.MIRAGE_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.MYTHRIL_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.OASIS_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.OBSIDIAN_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.OBSIDIAN_LOCK_BOX.get(), entry(vbox(0,
                translate("tooltip.item.confluence.obsidian_lock_box.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.obsidian_lock_box.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.OCEAN_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.PEARLWOOD_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.PINE_CONE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, TreasureBagItems.PLANTERA_TREASURE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, TreasureBagItems.QUEEN_BEE_TREASURE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.RED_ENVELOPE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.SAVANNA_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.SEASIDE_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, TreasureBagItems.SKELETRON_PRIME_TREASURE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, TreasureBagItems.SKELETRON_TREASURE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.SKY_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.STOCKADE_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.SUGAR_TANGERINE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, TreasureBagItems.THE_DESTROYER_TREASURE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, TreasureBagItems.THE_TWINS_TREASURE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.TITANIUM_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, TreasureBagItems.WALL_OF_FLESH_TREASURE_BAG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.right_click.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.WILD_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, CrateBlocks.WOODEN_CRATE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate.common.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject materialDescriptions() {
        JsonObject root = new JsonObject();
        // 材料的背景描述和固定制作指引。
        add(root, MaterialItems.GEL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.gel.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.RAW_ASPHALT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.raw_asphalt.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_BRIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.soul_of_bright.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_FLIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.soul_of_flight.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_FRIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.soul_of_fright.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_LIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.soul_of_light.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_MIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.soul_of_might.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_NIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.soul_of_night.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_SIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.soul_of_sight.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, MaterialItems.SOUL_OF_VOIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.soul_of_voight.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject permanentUpgradeDescriptions() {
        JsonObject root = new JsonObject();
        // 玩家、NPC、商贩及矿车永久提升道具的固定说明。
        add(root, ConsumableItems.ADVANCED_COMBAT_TECHNIQUES.get(), entry(vbox(0,
                translate("tooltip.item.confluence.advanced_combat_techniques.0", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.advanced_combat_techniques.1", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.ADVANCED_COMBAT_TECHNIQUES_VOLUME_TWO.get(), entry(vbox(0,
                translate("tooltip.item.confluence.advanced_combat_techniques_volume_two.0", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.advanced_combat_techniques_volume_two.1", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.AEGIS_APPLE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.aegis_apple.0", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.AMBROSIA.get(), entry(vbox(0,
                translate("tooltip.item.confluence.ambrosia.0", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.ARCANE_CRYSTAL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.arcane_crystal.0", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.ARTISAN_LOAF.get(), entry(vbox(0,
                translate("tooltip.item.confluence.artisan_loaf.0", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.artisan_loaf.1", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.GALAXY_PEARL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.galaxy_pearl.0", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.GUMMY_WORM.get(), entry(vbox(0,
                translate("tooltip.item.confluence.gummy_worm.0", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.MANA_CRYSTAL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.mana_crystal.0", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.MINECART_UPGRADE_KIT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.minecart_upgrade_kit.0", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.minecart_upgrade_kit.1", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.PEDDLERS_SATCHEL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.peddlers_satchel.0", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.RECALL_MANA_CRYSTAL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.recall_mana_crystal.0", textOptions().color(ChatFormatting.AQUA.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.VITAL_CRYSTAL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.vital_crystal.0", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject potionDescriptions() {
        JsonObject root = new JsonObject();
        // 药水的固定效果说明；实际效果、持续时间等运行时信息继续由 Java 输出。
        add(root, PotionItems.ALE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.ale.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.AMMO_RESERVATION_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.ammo_reservation_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.BATTLE_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.battle_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.BUILDER_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.builder_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.CALMING_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.calming_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.CHAOS_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.chaos_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.CRATE_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crate_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.DANGERSENSE_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.dangersense_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.EGGNOG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.eggnog.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.ENDURANCE_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.endurance_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.FEATHERFALL_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.featherfall_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.FISHING_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.fishing_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.FLASK_OF_FIRE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.flask_of_fire.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.FLASK_OF_GOLD.get(), entry(vbox(0,
                translate("tooltip.item.confluence.flask_of_gold.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.FLIPPER_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.flipper_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.GILLS_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.gills_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.GRAVITATION_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.gravitation_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.GREATER_HEALING_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.greater_healing_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.GREATER_LUCK_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.greater_luck_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.GREATER_MANA_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.greater_mana_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.HEALING_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.healing_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.HEART_REACH_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.heart_reach_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.HUNTER_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.hunter_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.INFERNO_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.inferno_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.INVISIBILITY_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.invisibility_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.IRON_SKIN_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.iron_skin_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.LESSER_HEALING_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.lesser_healing_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.LESSER_LUCK_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.lesser_luck_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.LESSER_MANA_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.lesser_mana_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.LOVE_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.love_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.LUCK_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.luck_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.MANA_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.mana_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.MANA_REGENERATION_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.mana_regeneration_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.MINING_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.mining_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.MUG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.mug.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.NIGHT_OWL_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.night_owl_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.OBSIDIAN_SKIN_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.obsidian_skin_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.RANDOM_TELEPORT_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.random_teleport_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.RECALL_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.recall_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.RED_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.red_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.REGENERATION_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.regeneration_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.RESTORATION_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.restoration_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.SATIETY_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.satiety_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.SHIMMER_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.shimmer_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.SHINE_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.shine_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.SPELUNKER_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.spelunker_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.STINK_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.stink_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.STRANGE_BREW.get(), entry(vbox(0,
                translate("tooltip.item.confluence.strange_brew.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.SUMMONING_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.summoning_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.SUPER_HEALING_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.super_healing_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.SUPER_MANA_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.super_mana_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.THORNS_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.thorns_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.TITAN_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.titan_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.WATER_WALKING_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.water_walking_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PotionItems.WORMHOLE_POTION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wormhole_potion.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject sponsorItemLore() {
        JsonObject root = new JsonObject();
        // 赞助纪念物品、特殊食物与彩蛋的背景描述；赞助武器的故事统一放在 weapon_lore.json。
        add(root, FoodItems.DONGDONGS_FLATBREAD.get(), entry(vbox(0,
                translate("tooltip.item.confluence.dongdongs_flatbread.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ModItems.FAILED_SKULL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.failed_skull.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ModItems.ICE_TOFU_BRICK.get(), entry(vbox(0,
                translate("tooltip.item.confluence.ice_tofu_brick.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ModItems.KIND_MISIDE_RING.get(), entry(vbox(0,
                translate("tooltip.item.confluence.kind_miside_ring.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FoodItems.PIGLIN_STEW.get(), entry(vbox(0,
                translate("tooltip.item.confluence.piglin_stew.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FoodItems.PINK_COLA.get(), entry(vbox(0,
                translate("tooltip.item.confluence.pink_cola.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ModItems.TOKYO_TEDDY_BEAR.get(), entry(vbox(0,
                translate("tooltip.item.confluence.tokyo_teddy_bear.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.tokyo_teddy_bear.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.tokyo_teddy_bear.2", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.tokyo_teddy_bear.3", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.tokyo_teddy_bear.4", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.tokyo_teddy_bear.5", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject storageItemUsage() {
        JsonObject root = new JsonObject();
        // 随身储物物品的使用与状态切换说明。
        add(root, PetItems.CHESTER_STAFF.get(), entry(vbox(0,
                translate("tooltip.confluence.storage_companion.retrieve", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PetItems.MONEY_TROUGH.get(), entry(vbox(0,
                translate("tooltip.confluence.storage_companion.retrieve", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.VOID_BAG.get(), entry(vbox(0,
                translate("tooltip.confluence.void_storage.shared", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.confluence.void_storage.overflow", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.confluence.void_storage.toggle", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, FunctionalBlocks.VOID_VAULT.get(), entry(vbox(0,
                translate("tooltip.confluence.void_storage.shared", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject toolUsage() {
        JsonObject root = new JsonObject();
        // 钥匙、油漆工具、捕虫网、海螺及复合工具的固定操作或能力说明。当前模式、记录坐标等状态继续由 Java 输出。
        add(root, HoeShovelItems.ADAMANTITE_HOE_SHOVEL.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.BINOCULARS.get(), entry(vbox(0,
                translate("tooltip.item.confluence.binoculars.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.BLOODSTAINED_POWDER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bloodstained_powder.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.BUG_NET.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bug_net.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HoeShovelItems.CHLOROPHYTE_HOE_SHOVEL.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HoeShovelItems.COBALT_HOE_SHOVEL.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.CORRUPTION_KEY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.corruption_key.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.CRIMSON_KEY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.crimson_key.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.DEMON_CONCH.get(), entry(vbox(0,
                translate("tooltip.item.confluence.demon_conch.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.DESERT_KEY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.desert_key.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.DEV_BUG_NET.get(), entry(vbox(0,
                translate("tooltip.item.confluence.dev_bug_net.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.DUNGEON_COMPASS.get(), entry(vbox(0,
                translate("tooltip.item.confluence.dungeon_compass.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.EMPTY_DROPPER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.empty_dropper.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.ENCUMBERING_STONE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.encumbering_stone.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.encumbering_stone.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.encumbering_stone.2", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.FROZEN_KEY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.frozen_key.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.GOLDEN_BUG_NET.get(), entry(vbox(0,
                translate("tooltip.item.confluence.golden_bug_net.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.golden_bug_net.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.GOLDEN_DUNGEON_KEY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.golden_dungeon_key.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.GOLDEN_KEY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.golden_key.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.GUIDE_TO_CRITTER_COMPANIONSHIP.get(), entry(vbox(0,
                translate("tooltip.item.confluence.guide_to_critter_companionship.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.guide_to_critter_companionship.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.GUIDE_TO_ENVIRONMENTAL_PRESERVATION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.guide_to_environmental_preservation.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.guide_to_environmental_preservation.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.GUIDE_TO_PEACEFUL_COEXISTENCE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.guide_to_peaceful_coexistence.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.guide_to_peaceful_coexistence.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.guide_to_peaceful_coexistence.2", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HamaxeItems.HAEMORRHAXE.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HoeShovelItems.HALLOWED_HOE_SHOVEL.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.HALLOWED_KEY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.hallowed_key.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ModItems.HARDMODE_CONVERTOR.get(), entry(vbox(0,
                translate("tooltip.item.confluence.hardmode_convertor.0", textOptions().color(ChatFormatting.GOLD.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.HONEY_ABSORBANT_SPONGE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.honey_absorbant_sponge.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.JUNGLE_KEY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.jungle_key.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.KEY_OF_LIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.key_of_light.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.KEY_OF_NIGHT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.key_of_night.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.LAVA_ABSORBANT_SPONGE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.lava_absorbant_sponge.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.LAVAPROOF_BUG_NET.get(), entry(vbox(0,
                translate("tooltip.item.confluence.lavaproof_bug_net.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.LIFE_CRYSTAL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.life_crystal.0", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.LIFE_FRUIT.get(), entry(vbox(0,
                translate("tooltip.item.confluence.life_fruit.0", textOptions().color(ChatFormatting.GREEN.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.MAGIC_CONCH.get(), entry(vbox(0,
                translate("tooltip.item.confluence.magic_conch.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.MECHANIC_SAFE_KEY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.mechanic_safe_key.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.METEOR_COMPASS.get(), entry(vbox(0,
                translate("tooltip.item.confluence.meteor_compass.0", textOptions().color(ChatFormatting.AQUA.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HamaxeItems.METEOR_HAMAXE.get(), entry(modifiers(vbox(0,
                        translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))),
                modifierOptions().margins(0, 0, 8, 0)), Map.of(), false, true));
        add(root, HoeShovelItems.METEOR_HOE_SHOVEL.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HamaxeItems.MOLTEN_HAMAXE.get(), entry(modifiers(vbox(0,
                        translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))),
                modifierOptions().margins(0, 0, 8, 0)), Map.of(), false, true));
        add(root, HoeShovelItems.MOLTEN_HOE_SHOVEL.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HoeShovelItems.MYTHRIL_HOE_SHOVEL.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HamaxeItems.NEBULA_HAMAXE.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.NPC_INVITATION.get(), entry(vbox(0,
                translate("tooltip.item.confluence.npc_invitation.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.OCEAN_KEY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.ocean_key.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HoeShovelItems.ORICHALCUM_HOE_SHOVEL.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PaintItems.PAINT_ROLLER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.paint_roller.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.paint_roller.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PaintItems.PAINT_SCRAPER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.paint_scraper.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.paint_scraper.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PaintItems.PAINTBRUSH.get(), entry(vbox(0,
                translate("tooltip.item.confluence.paintbrush.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.paintbrush.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HoeShovelItems.PALLADIUM_HOE_SHOVEL.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ModItems.PARADOX_INTERACTIVE_MEDAL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.paradox_interactive_medal.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.RECALL_LIFE_CRYSTAL.get(), entry(vbox(0,
                translate("tooltip.item.confluence.recall_life_crystal.0", textOptions().color(ChatFormatting.RED.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ConsumableItems.ROTTEN_BONE_DUST.get(), entry(vbox(0,
                translate("tooltip.item.confluence.rotten_bone_dust.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.RUST_IRON_KEY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.rust_iron_key.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.SHADOW_KEY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.shadow_key.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HamaxeItems.SOLAR_FLARE_HAMAXE.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HamaxeItems.SPECTRE_HAMAXE.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PaintItems.SPECTRE_PAINT_ROLLER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.paint_roller.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.paint_roller.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PaintItems.SPECTRE_PAINT_SCRAPER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.paint_scraper.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.paint_scraper.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, PaintItems.SPECTRE_PAINTBRUSH.get(), entry(vbox(0,
                translate("tooltip.item.confluence.paintbrush.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320)),
                translate("tooltip.item.confluence.paintbrush.1", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HamaxeItems.STARDUST_HAMAXE.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.SUPER_ABSORBANT_SPONGE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.super_absorbant_sponge.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.TEMPLE_KEY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.temple_key.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HamaxeItems.THE_AXE.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HoeShovelItems.TITANIUM_HOE_SHOVEL.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.ULTRA_ABSORBANT_SPONGE.get(), entry(vbox(0,
                translate("tooltip.item.confluence.ultra_absorbant_sponge.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.UNIVERSE_KEY.get(), entry(vbox(0,
                translate("tooltip.item.confluence.universe_key.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, HamaxeItems.VORTEX_HAMAXE.get(), entry(vbox(0,
                translate("message.confluence.toolmode.tip", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        add(root, ToolItems.WIRE_CUTTER.get(), entry(vbox(0,
                translate("tooltip.item.confluence.wire_cutter.0", textOptions().color(ChatFormatting.GRAY.getName()).maxWidth(320))), Map.of(), false, true));
        return root;
    }

    private static JsonObject weaponLore() {
        JsonObject root = new JsonObject();
        // 武器的背景故事与风味文字。伤害、攻速、暴击、前缀和可动组件由原有 Java 实现负责。
        add(root, SwordItems.BEE_KEEPER.get(), entry(modifiers(vbox(0,
                        cycleText(translate("tooltip.item.confluence.bee_keeper.0", textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#FFE082", "#FFC857", "#FFF1AD"),
                        cycleText(translate("tooltip.item.confluence.bee_keeper.1", textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#FFE082", "#FFC857", "#FFF1AD")),
                modifierOptions().margins(0, 0, 8, 0)), Map.of(), false, true));
        add(root, SwordItems.COPPER_SHORT_SWORD.get(), entry(vbox(0,
                cycleText(translate("tooltip.item.confluence.copper_short_sword.0", textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#DCA078", "#F2C69C", "#C98B65"),
                cycleText(translate("tooltip.item.confluence.copper_short_sword.1", textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#DCA078", "#F2C69C", "#C98B65")), Map.of(), false, true));
        add(root, SwordItems.ENCHANTED_SWORD.get(), entry(vbox(0,
                cycleText(translate("tooltip.item.confluence.enchanted_sword.0", textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#9CCBFF", "#C6B5FF", "#B8E9FF"),
                cycleText(translate("tooltip.item.confluence.enchanted_sword.1", textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#9CCBFF", "#C6B5FF", "#B8E9FF")), Map.of(), false, true));
        add(root, AxeItems.LUCY_THE_AXE.get(), entry(vbox(0,
                cycleText(translate("tooltip.item.confluence.lucy_the_axe.0", textOptions().color(ChatFormatting.WHITE.getName()).maxWidth(320)), 12.0, "smooth", "#F0A3A3", "#FFC58F", "#EAB0CB")), Map.of(), false, true));
        add(root, SwordItems.STARFURY.get(), entry(vbox(0,
                cycleText(translate("tooltip.item.confluence.starfury.0", textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#FFB3DA", "#D3B4FF", "#FFF1AD"),
                cycleText(translate("tooltip.item.confluence.starfury.1", textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#FFB3DA", "#D3B4FF", "#FFF1AD")), Map.of(), false, true));
        add(root, SwordItems.SWEET_SWORD.get(), entry(vbox(0,
                cycleText(translate("tooltip.item.confluence.sweet_sword.0", textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#FFB1D4", "#FFD0EB", "#C9B8FF")), Map.of(), false, true));
        add(root, SwordItems.TRUE_COPPER_SHORTSWORD.get(), entry(vbox(0,
                cycleText(translate("tooltip.item.confluence.true_copper_shortsword.0", textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#E1A16E", "#FFE0A3", "#F3BC70"),
                cycleText(translate("tooltip.item.confluence.true_copper_shortsword.1", textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#E1A16E", "#FFE0A3", "#F3BC70"),
                cycleText(translate("tooltip.item.confluence.true_copper_shortsword.2", textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#E1A16E", "#FFE0A3", "#F3BC70")), Map.of(), false, true));
        add(root, SwordItems.ZENITH.get(), entry(vbox(0,
                cycleText(translate("tooltip.item.confluence.zenith.0", textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#FFAAAA", "#FFD594", "#FFF1A0", "#ACEDC1", "#A8D8FF", "#D0B4FF"),
                cycleText(translate("tooltip.item.confluence.zenith.1", textOptions().color(ChatFormatting.WHITE.getName()).italic(true).maxWidth(320)), 12.0, "smooth", "#FFAAAA", "#FFD594", "#FFF1A0", "#ACEDC1", "#A8D8FF", "#D0B4FF")), Map.of(), false, true));
        return root;
    }

    private static JsonObject weaponUsage() {
        JsonObject root = new JsonObject();
        // 芦苇、雨伞等兼作工具的武器固定用途说明。
        add(root, SwordItems.BREATHING_REED.get(), entry(vbox(0,
                translate("tooltip.item.confluence.breathing_reed.0", textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320))), Map.of(), false, true));
        add(root, SwordItems.TRAGIC_UMBRELLA.get(), entry(vbox(0,
                translate("tooltip.item.confluence.tragic_umbrella.0", textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320))), Map.of(), false, true));
        add(root, SwordItems.UMBRELLA.get(), entry(vbox(0,
                translate("tooltip.item.confluence.umbrella.0", textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320))), Map.of(), false, true));
        add(root, BoomerangItems.BEIDOU_BOOMERANG.get(), entry(vbox(0,
                translate("tooltip.item.confluence.bei_dou_boomerang.0", textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320)),
                translate("tooltip.item.confluence.bei_dou_boomerang.1", textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320)),
                translate("tooltip.item.confluence.bei_dou_boomerang.2", textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320)),
                translate("tooltip.item.confluence.bei_dou_boomerang.3", textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320)),
                translate("tooltip.item.confluence.bei_dou_boomerang.4", textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320)),
                translate("tooltip.item.confluence.bei_dou_boomerang.5", textOptions().color(ChatFormatting.GRAY.getName()).italic(true).maxWidth(320))), Map.of(), false, true));
        return root;
    }
}
