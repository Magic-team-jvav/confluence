package org.confluence.mod.common.data.gen;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.MoonPhase;
import org.confluence.mod.common.entity.npc.trade.NPCTradeOffer;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.*;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_furniture.common.init.TFBlocks;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/// 生成内置 NPC 商店的当前格式数据。
///
/// 生成端与加载端共用 {@link NPCTradeOffer#CODEC}。
public final class NPCShopProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public NPCShopProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "npc/trades");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<ResourceLocation, List<NPCTradeOffer>> shops = new LinkedHashMap<>();
        TradeCondition bloodMoon = new GameEventCondition(BloodMoonGameEvent.KEY);
        TradeCondition graveyard = GraveyardCondition.INSTANCE;
        TradeCondition corruptionWorld = new WorldFlagCondition(IWorldOptions.THE_CORRUPTION);
        TradeCondition crimsonWorld = new WorldFlagCondition(IWorldOptions.THE_CRIMSON);
        shops.put(Confluence.asResource("merchant"), List.of(
                offer(ToolItems.BUG_NET.toStack()),
                offer(ArmorItems.MINING_HELMET.toStack()),
                offer(new ItemStack(Items.ANVIL)),
                offer(new ItemStack(Items.TORCH)),
                offer(new ItemStack(Items.ARROW)),
                offer(new ItemStack(Items.ARROW, 100)),
                offer(ModBlocks.ROPE.toStack()),
                offer(ConsumableItems.SHURIKEN.toStack()),
                offer(FunctionalBlocks.PIGGY_BANK.toStack()),
                offer(FunctionalBlocks.SAFE.toStack()),
                offer(PickaxeItems.COPPER_PICKAXE.toStack()),
                offer(AxeItems.COPPER_AXE.toStack()),
                offer(PotionItems.LESSER_HEALING_POTION.toStack()),
                offer(PotionItems.LESSER_MANA_POTION.toStack()),
                offer(FoodItems.MARSHMALLOW.toStack()),
                offer(TFBlocks.PIN_WHEEL.toStack()),
                offer(PotionItems.HEALING_POTION.toStack(), HardmodeCondition.INSTANCE),
                offer(PotionItems.MANA_POTION.toStack(), HardmodeCondition.INSTANCE),
                offer(FunctionalBlocks.SHARPENING_STATION.toStack(), HardmodeCondition.INSTANCE),
                offer(MaterialItems.GOLD_DUST.toStack(), HardmodeCondition.INSTANCE)
        ));
        shops.put(Confluence.asResource("dye_trader"), List.of(
                offer(FunctionalBlocks.DYE_VAT.toStack()),
                offer(VanityArmorItems.SILVER_DYE.toStack()),
                offer(VanityArmorItems.BROWN_DYE.toStack()),
                offer(VanityArmorItems.TEAM_DYE.toStack())
        ));
        shops.put(Confluence.asResource("painter"), List.of(
                offer(PaintItems.PAINTBRUSH.toStack()),
                offer(PaintItems.PAINT_ROLLER.toStack()),
                offer(PaintItems.PAINT_SCRAPER.toStack()),
                offer(PaintItems.RED_PAINT.toStack()),
                offer(PaintItems.DEEP_RED_PAINT.toStack()),
                offer(PaintItems.ORANGE_PAINT.toStack()),
                offer(PaintItems.DEEP_ORANGE_PAINT.toStack()),
                offer(PaintItems.YELLOW_PAINT.toStack()),
                offer(PaintItems.DEEP_YELLOW_PAINT.toStack()),
                offer(PaintItems.LIME_PAINT.toStack()),
                offer(PaintItems.DEEP_LIME_PAINT.toStack()),
                offer(PaintItems.GREEN_PAINT.toStack()),
                offer(PaintItems.DEEP_GREEN_PAINT.toStack()),
                offer(PaintItems.TEAL_PAINT.toStack()),
                offer(PaintItems.DEEP_TEAL_PAINT.toStack()),
                offer(PaintItems.CYAN_PAINT.toStack()),
                offer(PaintItems.DEEP_CYAN_PAINT.toStack()),
                offer(PaintItems.SKY_BLUE_PAINT.toStack()),
                offer(PaintItems.DEEP_SKY_BLUE_PAINT.toStack()),
                offer(PaintItems.BLUE_PAINT.toStack()),
                offer(PaintItems.DEEP_BLUE_PAINT.toStack()),
                offer(PaintItems.PURPLE_PAINT.toStack()),
                offer(PaintItems.DEEP_PURPLE_PAINT.toStack()),
                offer(PaintItems.VIOLET_PAINT.toStack()),
                offer(PaintItems.DEEP_VIOLET_PAINT.toStack()),
                offer(PaintItems.PINK_PAINT.toStack()),
                offer(PaintItems.DEEP_PINK_PAINT.toStack()),
                offer(PaintItems.BLACK_PAINT.toStack()),
                offer(PaintItems.GRAY_PAINT.toStack()),
                offer(PaintItems.WHITE_PAINT.toStack()),
                offer(PaintItems.BROWN_PAINT.toStack()),
                offer(PaintItems.SHADOW_PAINT.toStack(), HardmodeCondition.INSTANCE),
                offer(PaintItems.NEGATIVE_PAINT.toStack(), HardmodeCondition.INSTANCE),
                offer(PaintItems.ILLUMINANT_COATING.toStack(), graveyard)
        ));
        shops.put(Confluence.asResource("dryad"), List.of(
                offer(ConsumableItems.PURIFICATION_POWDER.toStack()),
                offer(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.SAPLING.toStack()),
                offer(new ItemStack(Items.OAK_SAPLING)),
                offer(new ItemStack(Items.SUNFLOWER)),
                offer(new ItemStack(Items.FLOWER_POT)),
                offer(TFBlocks.HANGING_POT_ITEM.toStack()),
                offer(new ItemStack(Items.PUMPKIN_SEEDS)),
                offer(ModItems.GRASS_SEED.toStack()),
                offer(ToolItems.GUIDE_TO_ENVIRONMENTAL_PRESERVATION.toStack()),
                offer(ModItems.HALLOWED_SEED.toStack(), HardmodeCondition.INSTANCE),
                offer(ModItems.ASH_GRASS_SEED.toStack(), new DimensionCondition(OverworldUtils.underworld())),
                offer(ModItems.MUSHROOM_GRASS_SEED.toStack(), new BiomeCondition(List.of(ModBiomes.GLOWING_MUSHROOM), List.of())),
                offer(ModItems.CRIMSON_SEED.toStack(), corruptionWorld.and(bloodMoon.or(graveyard))),
                offer(ConsumableItems.VILE_POWDER.toStack(), corruptionWorld.and(bloodMoon)),
                offer(ConsumableItems.VICIOUS_POWDER.toStack(), crimsonWorld.and(bloodMoon)),
                offer(ModItems.CORRUPT_SEED.toStack(), crimsonWorld.and(bloodMoon.or(graveyard)))
        ));
        shops.put(Confluence.asResource("witch_doctor"), List.of(
                offer(GunItems.BLOWGUN.toStack()),
                offer(FunctionalBlocks.CAULDRON.toStack(), halloween()),
                offer(AccessoryItems.PYGMY_NECKLACE.toStack(), new TimeCondition(LibDateUtils._19$30, LibDateUtils._04$30, false))
        ));
        shops.put(Confluence.asResource("clothier"), List.of(
                offer(VanityArmorItems.FAMILIAR_WIG.toStack()),
                offer(VanityArmorItems.FAMILIAR_SHIRT.toStack()),
                offer(VanityArmorItems.FAMILIAR_PANTS.toStack()),
                offer(VanityArmorItems.FAMILIAR_SHOES.toStack()),
                offer(VanityArmorItems.GUY_FAWKES_HAT.toStack(), halloween()),
                offer(VanityArmorItems.GUY_FAWKES_MASK.toStack(), halloween()),
                offer(VanityArmorItems.GUY_FAWKES_MASK_SET.toStack(), halloween()),
                offer(VanityArmorItems.CLOTHIERS_JACKET.toStack(), halloween()),
                offer(VanityArmorItems.CLOTHIERS_PANTS.toStack(), halloween()),
                offer(VanityArmorItems.CLOTHIERS_SHOES.toStack(), halloween())
        ));
        shops.put(Confluence.asResource("demolitionist"), List.of(
                offer(ConsumableItems.GRENADE.toStack()),
                offer(ConsumableItems.BOMB.toStack()),
                offer(ConsumableItems.DYNAMITE.toStack()),
                offer(new ItemStack(Items.GUNPOWDER), crimsonWorld.and(bloodMoon.or(graveyard))),
                offer(MaterialItems.EXPLOSIVE_POWDER.toStack(), HardmodeCondition.INSTANCE),
                offer(ArrowItems.HELLFIRE_ARROW.toStack(), HardmodeCondition.INSTANCE)
        ));
        shops.put(Confluence.asResource("arms_dealer"), List.of(
                offer(GunItems.MUSKET_BULLET.toStack()),
                offer(new ItemStack(GunItems.MUSKET_BULLET.get(), 100)),
                offer(GunItems.SILVER_BULLET.toStack(), HardmodeCondition.INSTANCE),
                offer(new ItemStack(GunItems.SILVER_BULLET.get(), 100), HardmodeCondition.INSTANCE),
                offer(GunItems.TUNGSTEN_BULLET.toStack(), HardmodeCondition.INSTANCE),
                offer(new ItemStack(GunItems.TUNGSTEN_BULLET.get(), 100), HardmodeCondition.INSTANCE),
                offer(MaterialItems.EMPTY_BULLET.toStack(), HardmodeCondition.INSTANCE),
                offer(FunctionalBlocks.AMMO_BOX.toStack(), HardmodeCondition.INSTANCE),
                offer(GunItems.SHOTGUN.toStack(), HardmodeCondition.INSTANCE),
                offer(ArrowItems.UNHOLY_ARROW.toStack(), HardmodeCondition.INSTANCE),
                offer(GunItems.FLINTLOCK_PISTOL.toStack()),
                offer(GunItems.MINISHARK.toStack())
        ));
        shops.put(Confluence.asResource("mechanic"), List.of(
                offer(ToolItems.RED_WRENCH.toStack()),
                offer(ToolItems.BLUE_WRENCH.toStack()),
                offer(ToolItems.GREEN_WRENCH.toStack()),
                offer(ToolItems.YELLOW_WRENCH.toStack()),
                offer(ToolItems.WIRE_CUTTER.toStack()),
                offer(FishingPoleItems.MECHANICS_ROD.toStack(), new MoonPhaseCondition(MoonPhase.WANING_GIBBOUS, MoonPhase.WANING_CRESCENT, MoonPhase.WAXING_CRESCENT, MoonPhase.WAXING_GIBBOUS)),
                offer(FunctionalBlocks.SWITCH.toStack()),
                offer(FunctionalBlocks.SIGNAL_ADAPTER.toStack()),
                offer(FunctionalBlocks.TIMERS_BLOCK_1_1.toStack()),
                offer(FunctionalBlocks.TIMERS_BLOCK_3_1.toStack()),
                offer(FunctionalBlocks.TIMERS_BLOCK_5_1.toStack()),
                offer(FunctionalBlocks.TIMERS_BLOCK_1_2.toStack()),
                offer(FunctionalBlocks.TIMERS_BLOCK_1_4.toStack()),
                offer(FunctionalBlocks.EVER_POWERED_RAIL.toStack()),
                offer(AccessoryItems.MECHANICAL_LENS.toStack()),
                offer(new ItemStack(Items.PISTON)),
                offer(new ItemStack(Items.STICKY_PISTON)),
                offer(new ItemStack(Items.REDSTONE_LAMP)),
                offer(new ItemStack(Items.DAYLIGHT_DETECTOR)),
                offer(AccessoryItems.SPECTRE_GOGGLES.toStack(), graveyard)
        ));
        shops.put(Confluence.asResource("party_girl"), List.of(
                offer(FunctionalBlocks.SILLY_BALLOON_MACHINE.toStack()),
                offer(ConsumableItems.SMOKE_BOMB.toStack()),
                offer(MaterialItems.CONFETTI.toStack()),
                offer(MinecartItems.PARTY_WAGON.toStack()),
                offer(FoodItems.BALLOON_SEED.toStack()),
                offer(NatureBlocks.BALLOON_MELON.toStack()),
                offer(DecorativeBlocks.WHITE_BALLOON.toStack()),
                offer(DecorativeBlocks.LIGHT_GRAY_BALLOON.toStack()),
                offer(DecorativeBlocks.GRAY_BALLOON.toStack()),
                offer(DecorativeBlocks.BLACK_BALLOON.toStack()),
                offer(DecorativeBlocks.BROWN_BALLOON.toStack()),
                offer(DecorativeBlocks.RED_BALLOON.toStack()),
                offer(DecorativeBlocks.ORANGE_BALLOON.toStack()),
                offer(DecorativeBlocks.YELLOW_BALLOON.toStack()),
                offer(DecorativeBlocks.LIME_BALLOON.toStack()),
                offer(DecorativeBlocks.GREEN_BALLOON.toStack()),
                offer(DecorativeBlocks.CYAN_BALLOON.toStack()),
                offer(DecorativeBlocks.LIGHT_BLUE_BALLOON.toStack()),
                offer(DecorativeBlocks.BLUE_BALLOON.toStack()),
                offer(DecorativeBlocks.PURPLE_BALLOON.toStack()),
                offer(DecorativeBlocks.MAGENTA_BALLOON.toStack()),
                offer(DecorativeBlocks.PINK_BALLOON.toStack())
        ));
        shops.put(Confluence.asResource("wizard"), List.of(
                offer(FunctionalBlocks.CRYSTAL_BALL.toStack()),
                offer(PotionItems.GREATER_MANA_POTION.toStack()),
                offer(MaterialItems.BELL.toStack()),
                offer(MaterialItems.HARP.toStack()),
                offer(MaterialItems.SPELL_TOME.toStack()),
                offer(new ItemStack(Items.BOOK)),
                offer(ToolItems.EMPTY_DROPPER.toStack()),
                offer(VanityArmorItems.WIZARDS_HAT.toStack(), halloween())
        ));
        shops.put(Confluence.asResource("zoologist"), List.of(
                offer(ToolItems.GUIDE_TO_CRITTER_COMPANIONSHIP.toStack()),
                offer(WhipItems.LEATHER_WHIP.toStack(), new BestiaryCondition(16)),
                offer(MinecartItems.DIGGING_MOLECART.toStack(), new BestiaryCondition(85)),
                offer(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.SAPLING.toStack()),
                offer(new ItemStack(Items.CHERRY_SAPLING)),
                offer(LanceItems.JOUSTING_LANCE.toStack(), new BestiaryCondition(75))
        ));
        shops.put(Confluence.asResource("goblin_tinkerer"), List.of(
                offer(HookItems.GRAPPLING_HOOK.toStack()),
                offer(TCItems.ROCKET_BOOTS.toStack()),
                offer(TCItems.TOOLBELT.toStack()),
                offer(TCItems.WORKSHOP.toStack()),
                offer(ConsumableItems.SPIKY_BALL.toStack())
        ));
        shops.put(Confluence.asResource("traveling_merchant"), List.of(
                offer(AccessoryItems.PAINT_SPRAYER.toStack()),
                offer(TCItems.PORTABLE_CEMENT_MIXER.toStack()),
                offer(TCItems.EXTENDO_GRIP.toStack()),
                offer(TCItems.BRICK_LAYER.toStack()),
                offer(TCItems.STOPWATCH.toStack()),
                offer(TCItems.LIFE_FORM_ANALYZER.toStack()),
                offer(TCItems.DPS_METER.toStack()),
                offer(SwordItems.KATANA.toStack()),
                offer(FoodItems.PAD_THAI.toStack()),
                offer(YoyoItems.CODE_1.toStack()),
                offer(NatureBlocks.DYNASTY_LOG_BLOCKS.LOG.toStack()),
                offer(FishingPoleItems.SITTING_DUCKS_FISHING_POLE.toStack())
        ));
        shops.put(Confluence.asResource("truffle"), List.of(
                exchange(BoomerangItems.SHROOMERANG.toStack(), new ItemStack(Items.EMERALD, 10)),
                exchange(new ItemStack(Items.EMERALD), new ItemStack(Items.BROWN_MUSHROOM, 10)),
                exchange(new ItemStack(Items.EMERALD), new ItemStack(Items.RED_MUSHROOM, 10))
        ));

        return CompletableFuture.allOf(shops.entrySet().stream()
                .map(entry -> save(output, entry.getKey(), entry.getValue()))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Confluence NPC Shops";
    }

    private CompletableFuture<?> save(CachedOutput output, ResourceLocation npcId, List<NPCTradeOffer> offers) {
        DataResult<JsonElement> encoded = NPCTradeOffer.CODEC.listOf().fieldOf("offers").codec()
                .encodeStart(JsonOps.INSTANCE, offers);
        JsonElement json = encoded.result().orElseThrow(() -> new IllegalStateException(
                "Unable to encode NPC shop " + npcId + ": "
                        + encoded.error()
                        .map(DataResult.PartialResult::message)
                        .orElse("unknown error")));
        Path path = pathProvider.json(npcId);
        return DataProvider.saveStable(output, json, path);
    }

    private static NPCTradeOffer offer(ItemStack result) {
        return new NPCTradeOffer(result, TradeCondition.alwaysTrue());
    }

    private static NPCTradeOffer offer(ItemStack result, TradeCondition condition) {
        return new NPCTradeOffer(result, condition);
    }

    private static NPCTradeOffer exchange(ItemStack result, ItemStack... costs) {
        return new NPCTradeOffer(result, List.of(costs), TradeCondition.alwaysTrue());
    }

    private static DateCondition halloween() {
        return new DateCondition(Calendar.OCTOBER, 10, Calendar.NOVEMBER, 1);
    }
}
