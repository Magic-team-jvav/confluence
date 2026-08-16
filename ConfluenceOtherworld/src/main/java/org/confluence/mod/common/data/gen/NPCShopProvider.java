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
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.trade.NPCShopDefinition;
import org.confluence.mod.common.entity.npc.trade.NPCTradeOffer;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.common.init.item.*;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/// 生成内置 NPC 商店的当前格式数据。
///
/// <p>交易系统运行时直接读取这里生成的 JSON，生成端与加载端共用
/// {@link NPCShopDefinition#CODEC} 和 {@link NPCTradeOffer#CODEC}。这样字段名、
/// 商品栈或条件声明一旦写错，会在数据生成或测试阶段直接失败，不会拖到玩家打开商店时才暴露。</p>
///
/// <p>这里先覆盖适合普通买卖语义的 NPC。向导、老人、护士这类以对话、召唤或服务为主的 NPC
/// 后续应接到对应系统，不在这里硬塞成普通商品表。</p>
public final class NPCShopProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public NPCShopProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "npc/trades");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<ResourceLocation, NPCShopDefinition> shops = new LinkedHashMap<>();
        shops.put(Confluence.asResource("merchant"), new NPCShopDefinition(
                NpcEntities.MERCHANT.get(),
                List.of(
                        offer("merchant/torch", new ItemStack(Items.TORCH)),
                        offer("merchant/anvil", new ItemStack(Items.ANVIL)),
                        offer("merchant/arrow_bundle", new ItemStack(Items.ARROW, 50)),
                        offer("merchant/rope_coil", ToolItems.ROPE_COIL.toStack()),
                        offer("merchant/bug_net", ToolItems.BUG_NET.toStack()),
                        offer("merchant/mining_helmet", ArmorItems.MINING_HELMET.toStack())
                )));
        shops.put(Confluence.asResource("dye_trader"), new NPCShopDefinition(
                NpcEntities.DYE_TRADER.get(),
                List.of(
                        offer("dye_trader/dye", VanityArmorItems.DYE.toStack()),
                        offer("dye_trader/red_dye", VanityArmorItems.RED_DYE.toStack()),
                        offer("dye_trader/orange_dye", VanityArmorItems.ORANGE_DYE.toStack()),
                        offer("dye_trader/yellow_dye", VanityArmorItems.YELLOW_DYE.toStack()),
                        offer("dye_trader/lime_dye", VanityArmorItems.LIME_DYE.toStack()),
                        offer("dye_trader/green_dye", VanityArmorItems.GREEN_DYE.toStack()),
                        offer("dye_trader/teal_dye", VanityArmorItems.TEAL_DYE.toStack()),
                        offer("dye_trader/cyan_dye", VanityArmorItems.CYAN_DYE.toStack()),
                        offer("dye_trader/sky_blue_dye", VanityArmorItems.SKY_BLUE_DYE.toStack()),
                        offer("dye_trader/blue_dye", VanityArmorItems.BLUE_DYE.toStack()),
                        offer("dye_trader/purple_dye", VanityArmorItems.PURPLE_DYE.toStack()),
                        offer("dye_trader/violet_dye", VanityArmorItems.VIOLET_DYE.toStack()),
                        offer("dye_trader/pink_dye", VanityArmorItems.PINK_DYE.toStack()),
                        offer("dye_trader/black_dye", VanityArmorItems.BLACK_DYE.toStack()),
                        offer("dye_trader/gray_dye", VanityArmorItems.GRAY_DYE.toStack()),
                        offer("dye_trader/silver_dye", VanityArmorItems.SILVER_DYE.toStack()),
                        offer("dye_trader/brown_dye", VanityArmorItems.BROWN_DYE.toStack())
                )));
        shops.put(Confluence.asResource("painter"), new NPCShopDefinition(
                NpcEntities.PAINTER.get(),
                List.of(
                        offer("painter/paintbrush", PaintItems.PAINTBRUSH.toStack()),
                        offer("painter/paint_roller", PaintItems.PAINT_ROLLER.toStack()),
                        offer("painter/paint_scraper", PaintItems.PAINT_SCRAPER.toStack()),
                        offer("painter/paint", PaintItems.PAINT.toStack()),
                        offer("painter/red_paint", PaintItems.RED_PAINT.toStack()),
                        offer("painter/orange_paint", PaintItems.ORANGE_PAINT.toStack()),
                        offer("painter/yellow_paint", PaintItems.YELLOW_PAINT.toStack()),
                        offer("painter/lime_paint", PaintItems.LIME_PAINT.toStack()),
                        offer("painter/green_paint", PaintItems.GREEN_PAINT.toStack()),
                        offer("painter/teal_paint", PaintItems.TEAL_PAINT.toStack()),
                        offer("painter/cyan_paint", PaintItems.CYAN_PAINT.toStack()),
                        offer("painter/sky_blue_paint", PaintItems.SKY_BLUE_PAINT.toStack()),
                        offer("painter/blue_paint", PaintItems.BLUE_PAINT.toStack()),
                        offer("painter/purple_paint", PaintItems.PURPLE_PAINT.toStack()),
                        offer("painter/violet_paint", PaintItems.VIOLET_PAINT.toStack()),
                        offer("painter/pink_paint", PaintItems.PINK_PAINT.toStack()),
                        offer("painter/black_paint", PaintItems.BLACK_PAINT.toStack()),
                        offer("painter/gray_paint", PaintItems.GRAY_PAINT.toStack()),
                        offer("painter/white_paint", PaintItems.WHITE_PAINT.toStack()),
                        offer("painter/brown_paint", PaintItems.BROWN_PAINT.toStack())
                )));
        shops.put(Confluence.asResource("dryad"), new NPCShopDefinition(
                NpcEntities.DRYAD.get(),
                List.of(
                        offer("dryad/grass_seed", ModItems.GRASS_SEED.toStack()),
                        offer("dryad/jungle_grass_seed", ModItems.JUNGLE_GRASS_SEED.toStack()),
                        offer("dryad/mushroom_grass_seed", ModItems.MUSHROOM_GRASS_SEED.toStack()),
                        offer("dryad/corrupt_seed", ModItems.CORRUPT_SEED.toStack()),
                        offer("dryad/crimson_seed", ModItems.CRIMSON_SEED.toStack()),
                        offer("dryad/hallowed_seed", ModItems.HALLOWED_SEED.toStack()),
                        offer("dryad/ash_grass_seed", ModItems.ASH_GRASS_SEED.toStack())
                )));
        shops.put(Confluence.asResource("witch_doctor"), new NPCShopDefinition(
                NpcEntities.WITCH_DOCTOR.get(),
                List.of(
                        offer("witch_doctor/bottle", PotionItems.BOTTLE.toStack()),
                        offer("witch_doctor/bottled_water", PotionItems.BOTTLED_WATER.toStack()),
                        offer("witch_doctor/flask_of_fire", PotionItems.FLASK_OF_FIRE.toStack()),
                        offer("witch_doctor/flask_of_gold", PotionItems.FLASK_OF_GOLD.toStack()),
                        offer("witch_doctor/thorns_potion", PotionItems.THORNS_POTION.toStack()),
                        offer("witch_doctor/water_walking_potion", PotionItems.WATER_WALKING_POTION.toStack())
                )));
        shops.put(Confluence.asResource("clothier"), new NPCShopDefinition(
                NpcEntities.CLOTHIER.get(),
                List.of(
                        offer("clothier/robe", VanityArmorItems.ROBE.toStack()),
                        offer("clothier/top_hat", VanityArmorItems.TOP_HAT.toStack()),
                        offer("clothier/tuxedo_shirt", VanityArmorItems.TUXEDO_SHIRT.toStack()),
                        offer("clothier/tuxedo_pants", VanityArmorItems.TUXEDO_PANTS.toStack()),
                        offer("clothier/tuxedo_shoes", VanityArmorItems.TUXEDO_SHOES.toStack()),
                        offer("clothier/familiar_wig", VanityArmorItems.FAMILIAR_WIG.toStack()),
                        offer("clothier/familiar_shirt", VanityArmorItems.FAMILIAR_SHIRT.toStack()),
                        offer("clothier/familiar_pants", VanityArmorItems.FAMILIAR_PANTS.toStack()),
                        offer("clothier/familiar_shoes", VanityArmorItems.FAMILIAR_SHOES.toStack())
                )));
        shops.put(Confluence.asResource("demolitionist"), new NPCShopDefinition(
                NpcEntities.DEMOLITIONIST.get(),
                List.of(
                        offer("demolitionist/grenade", ConsumableItems.GRENADE.toStack()),
                        offer("demolitionist/bomb", ConsumableItems.BOMB.toStack()),
                        offer("demolitionist/dynamite", ConsumableItems.DYNAMITE.toStack())
                )));
        shops.put(Confluence.asResource("arms_dealer"), new NPCShopDefinition(
                NpcEntities.ARMS_DEALER.get(),
                List.of(
                        offer("arms_dealer/musket_bullet", new ItemStack(GunItems.MUSKET_BULLET.get(), 50)),
                        offer("arms_dealer/flintlock_pistol", GunItems.FLINTLOCK_PISTOL.toStack()),
                        offer("arms_dealer/minishark", GunItems.MINISHARK.toStack())
                )));
        shops.put(Confluence.asResource("mechanic"), new NPCShopDefinition(
                NpcEntities.MECHANIC.get(),
                List.of(
                        offer("mechanic/red_wrench", ToolItems.RED_WRENCH.toStack()),
                        offer("mechanic/green_wrench", ToolItems.GREEN_WRENCH.toStack()),
                        offer("mechanic/blue_wrench", ToolItems.BLUE_WRENCH.toStack()),
                        offer("mechanic/yellow_wrench", ToolItems.YELLOW_WRENCH.toStack()),
                        offer("mechanic/wire_cutter", ToolItems.WIRE_CUTTER.toStack()),
                        offer("mechanic/repeater_bundle", new ItemStack(Items.REPEATER, 5)),
                        offer("mechanic/comparator_bundle", new ItemStack(Items.COMPARATOR, 5)),
                        offer("mechanic/redstone_lamp_bundle", new ItemStack(Items.REDSTONE_LAMP, 5))
                )));
        shops.put(Confluence.asResource("party_girl"), new NPCShopDefinition(
                NpcEntities.PARTY_GIRL.get(),
                List.of(
                        offer("party_girl/pink_paint", PaintItems.PINK_PAINT.toStack()),
                        offer("party_girl/bright_pink_dye", VanityArmorItems.BRIGHT_PINK_DYE.toStack()),
                        offer("party_girl/confetti_bundle", new ItemStack(MaterialItems.CONFETTI.get(), 25)),
                        offer("party_girl/cake", new ItemStack(Items.CAKE)),
                        offer("party_girl/cookie_bundle", new ItemStack(Items.COOKIE, 8))
                )));
        shops.put(Confluence.asResource("stylist"), new NPCShopDefinition(
                NpcEntities.STYLIST.get(),
                List.of(
                        offer("stylist/stylish_scissors", SwordItems.STYLISH_SCISSORS.toStack()),
                        offer("stylist/mirror", ToolItems.ICE_MIRROR.toStack()),
                        offer("stylist/familiar_wig", VanityArmorItems.FAMILIAR_WIG.toStack()),
                        offer("stylist/sunglasses", VanityArmorItems.SUNGLASSES.toStack())
                )));
        shops.put(Confluence.asResource("tax_collector"), new NPCShopDefinition(
                NpcEntities.TAX_COLLECTOR.get(),
                List.of(
                        offer("tax_collector/copper_coin_bundle", new ItemStack(ModItems.COPPER_COIN.get(), 100)),
                        offer("tax_collector/silver_coin", ModItems.SILVER_COIN.toStack()),
                        offer("tax_collector/gold_coin", ModItems.GOLD_COIN.toStack()),
                        offer("tax_collector/platinum_coin", ModItems.PLATINUM_COIN.toStack())
                )));
        shops.put(Confluence.asResource("truffle"), new NPCShopDefinition(
                NpcEntities.TRUFFLE.get(),
                List.of(
                        offer("truffle/mushroom_grass_seed", ModItems.MUSHROOM_GRASS_SEED.toStack()),
                        offer("truffle/glowing_mushroom_bundle", new ItemStack(MaterialItems.GLOWING_MUSHROOM.get(), 10)),
                        offer("truffle/truffle_worm", BaitItems.TRUFFLE_WORM.toStack()),
                        offer("truffle/shroomite_ingot", MaterialItems.SHROOMITE_INGOT.toStack())
                )));
        shops.put(Confluence.asResource("wizard"), new NPCShopDefinition(
                NpcEntities.WIZARD.get(),
                List.of(
                        offer("wizard/bottle", PotionItems.BOTTLE.toStack()),
                        offer("wizard/lesser_mana_potion", PotionItems.LESSER_MANA_POTION.toStack()),
                        offer("wizard/mana_potion", PotionItems.MANA_POTION.toStack()),
                        offer("wizard/greater_mana_potion", PotionItems.GREATER_MANA_POTION.toStack()),
                        offer("wizard/wand_of_sparking", ManaWeaponItems.WAND_OF_SPARKING.toStack()),
                        offer("wizard/ice_mirror", ToolItems.ICE_MIRROR.toStack())
                )));
        shops.put(Confluence.asResource("zoologist"), new NPCShopDefinition(
                NpcEntities.ZOOLOGIST.get(),
                List.of(
                        offer("zoologist/bug_net", ToolItems.BUG_NET.toStack()),
                        offer("zoologist/guide_to_critter_companionship", ToolItems.GUIDE_TO_CRITTER_COMPANIONSHIP.toStack()),
                        offer("zoologist/guide_to_environmental_preservation", ToolItems.GUIDE_TO_ENVIRONMENTAL_PRESERVATION.toStack()),
                        offer("zoologist/guide_to_peaceful_coexistence", ToolItems.GUIDE_TO_PEACEFUL_COEXISTENCE.toStack()),
                        offer("zoologist/bunny_hood", VanityArmorItems.BUNNY_HOOD.toStack()),
                        offer("zoologist/apple", new ItemStack(Items.APPLE))
                )));
        shops.put(Confluence.asResource("goblin_tinkerer"), new NPCShopDefinition(
                NpcEntities.GOBLIN_TINKERER.get(),
                List.of(
                        offer("goblin_tinkerer/grappling_hook", HookItems.GRAPPLING_HOOK.toStack()),
                        offer("goblin_tinkerer/spiky_ball_bundle", new ItemStack(ConsumableItems.SPIKY_BALL.get(), 25))
                )));
        // 渔夫和渔女是微光外观变体，必须共享同一份商品定义，避免两侧内容逐渐产生差异。
        List<NPCTradeOffer> anglerSupplyOffers = List.of(
                offer("angler_supply/wood_fishing_pole", FishingPoleItems.WOOD_FISHING_POLE.toStack()),
                offer("angler_supply/apprentice_bait", BaitItems.APPRENTICE_BAIT.toStack()),
                offer("angler_supply/journeyman_bait", BaitItems.JOURNEYMAN_BAIT.toStack()),
                offer("angler_supply/master_bait", BaitItems.MASTER_BAIT.toStack())
        );
        shops.put(Confluence.asResource("angler_supply"),
                new NPCShopDefinition(NpcEntities.ANGLER.get(), anglerSupplyOffers));
        shops.put(Confluence.asResource("female_angler_supply"),
                new NPCShopDefinition(NpcEntities.FEMALE_ANGLER.get(), anglerSupplyOffers));
        shops.put(Confluence.asResource("traveling_merchant"), new NPCShopDefinition(
                NpcEntities.TRAVELING_MERCHANT.get(),
                List.of(
                        offer("traveling_merchant/leather_bundle", new ItemStack(Items.LEATHER, 10)),
                        offer("traveling_merchant/coal_bundle", new ItemStack(Items.COAL, 10)),
                        offer("traveling_merchant/copper_bundle", new ItemStack(Items.COPPER_INGOT, 10)),
                        offer("traveling_merchant/iron_bundle", new ItemStack(Items.IRON_INGOT, 8)),
                        offer("traveling_merchant/gold_bundle", new ItemStack(Items.GOLD_INGOT, 4)),
                        offer("traveling_merchant/redstone_bundle", new ItemStack(Items.REDSTONE, 10)),
                        offer("traveling_merchant/lapis_bundle", new ItemStack(Items.LAPIS_LAZULI, 10)),
                        offer("traveling_merchant/amethyst_bundle", new ItemStack(Items.AMETHYST_CLUSTER, 4)),
                        offer("traveling_merchant/diamond_pair", new ItemStack(Items.DIAMOND, 2))
                )));

        return CompletableFuture.allOf(shops.entrySet().stream()
                .map(entry -> save(output, entry.getKey(), entry.getValue()))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Confluence NPC Shops";
    }

    private CompletableFuture<?> save(
            CachedOutput output,
            ResourceLocation contributionId,
            NPCShopDefinition shop
    ) {
        DataResult<JsonElement> encoded = NPCShopDefinition.CODEC
                .encodeStart(JsonOps.INSTANCE, shop);
        JsonElement json = encoded.result().orElseThrow(() -> new IllegalStateException(
                "Unable to encode NPC shop contribution " + contributionId + ": "
                        + encoded.error()
                        .map(DataResult.PartialResult::message)
                        .orElse("unknown error")));
        Path path = pathProvider.json(contributionId);
        return DataProvider.saveStable(output, json, path);
    }

    private static NPCTradeOffer offer(String id, ItemStack result) {
        return new NPCTradeOffer(Confluence.asResource(id), result, TradeCondition.alwaysTrue());
    }
}
