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
import org.confluence.mod.common.entity.npc.trade.NPCTradeOffer;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.HardmodeCondition;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.terra_curio.common.init.TCItems;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/// 生成内置 NPC 商店的当前格式数据。
///
/// <p>交易系统运行时直接读取这里生成的 JSON，生成端与加载端共用
/// {@link NPCTradeOffer#CODEC}。这样字段名、
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
        Map<ResourceLocation, List<NPCTradeOffer>> shops = new LinkedHashMap<>();
        shops.put(Confluence.asResource("merchant"), List.of(
                offer(new ItemStack(Items.TORCH)),
                offer(new ItemStack(Items.ANVIL)),
                offer(new ItemStack(Items.ARROW, 50)),
                offer(ToolItems.ROPE_COIL.toStack()),
                offer(ToolItems.BUG_NET.toStack()),
                offer(ArmorItems.MINING_HELMET.toStack())
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
                offer(PaintItems.NEGATIVE_PAINT.toStack(), HardmodeCondition.INSTANCE)
        ));
        shops.put(Confluence.asResource("dryad"), List.of(
                offer(ModItems.GRASS_SEED.toStack()),
                offer(ModItems.JUNGLE_GRASS_SEED.toStack()),
                offer(ModItems.MUSHROOM_GRASS_SEED.toStack()),
                offer(ModItems.CORRUPT_SEED.toStack()),
                offer(ModItems.CRIMSON_SEED.toStack()),
                offer(ModItems.HALLOWED_SEED.toStack()),
                offer(ModItems.ASH_GRASS_SEED.toStack())
        ));
        shops.put(Confluence.asResource("witch_doctor"), List.of(
                offer(PotionItems.BOTTLE.toStack()),
                offer(PotionItems.BOTTLED_WATER.toStack()),
                offer(PotionItems.FLASK_OF_FIRE.toStack()),
                offer(PotionItems.FLASK_OF_GOLD.toStack()),
                offer(PotionItems.THORNS_POTION.toStack()),
                offer(PotionItems.WATER_WALKING_POTION.toStack())
        ));
        shops.put(Confluence.asResource("clothier"), List.of(
                offer(VanityArmorItems.ROBE.toStack()),
                offer(VanityArmorItems.TOP_HAT.toStack()),
                offer(VanityArmorItems.TUXEDO_SHIRT.toStack()),
                offer(VanityArmorItems.TUXEDO_PANTS.toStack()),
                offer(VanityArmorItems.TUXEDO_SHOES.toStack()),
                offer(VanityArmorItems.FAMILIAR_WIG.toStack()),
                offer(VanityArmorItems.FAMILIAR_SHIRT.toStack()),
                offer(VanityArmorItems.FAMILIAR_PANTS.toStack()),
                offer(VanityArmorItems.FAMILIAR_SHOES.toStack())
        ));
        shops.put(Confluence.asResource("demolitionist"), List.of(
                offer(ConsumableItems.GRENADE.toStack()),
                offer(ConsumableItems.BOMB.toStack()),
                offer(ConsumableItems.DYNAMITE.toStack())
        ));
        shops.put(Confluence.asResource("arms_dealer"), List.of(
                offer(new ItemStack(GunItems.MUSKET_BULLET.get(), 50)),
                offer(GunItems.FLINTLOCK_PISTOL.toStack()),
                offer(GunItems.MINISHARK.toStack())
        ));
        shops.put(Confluence.asResource("mechanic"), List.of(
                offer(ToolItems.RED_WRENCH.toStack()),
                offer(ToolItems.BLUE_WRENCH.toStack()),
                offer(ToolItems.GREEN_WRENCH.toStack()),
                offer(ToolItems.YELLOW_WRENCH.toStack()),
                offer(ToolItems.WIRE_CUTTER.toStack()),
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
                offer(new ItemStack(Items.DAYLIGHT_DETECTOR))
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
        shops.put(Confluence.asResource("stylist"), List.of(
                offer(SwordItems.STYLISH_SCISSORS.toStack()),
                offer(ToolItems.ICE_MIRROR.toStack()),
                offer(VanityArmorItems.FAMILIAR_WIG.toStack()),
                offer(VanityArmorItems.SUNGLASSES.toStack())
        ));
        shops.put(Confluence.asResource("truffle"), List.of(
                offer(ModItems.MUSHROOM_GRASS_SEED.toStack()),
                offer(new ItemStack(MaterialItems.GLOWING_MUSHROOM.get(), 10)),
                offer(BaitItems.TRUFFLE_WORM.toStack()),
                offer(MaterialItems.SHROOMITE_INGOT.toStack())
        ));
        shops.put(Confluence.asResource("wizard"), List.of(
                offer(PotionItems.BOTTLE.toStack()),
                offer(PotionItems.LESSER_MANA_POTION.toStack()),
                offer(PotionItems.MANA_POTION.toStack()),
                offer(PotionItems.GREATER_MANA_POTION.toStack()),
                offer(ManaWeaponItems.WAND_OF_SPARKING.toStack()),
                offer(ToolItems.ICE_MIRROR.toStack())
        ));
        shops.put(Confluence.asResource("zoologist"), List.of(
                offer(ToolItems.BUG_NET.toStack()),
                offer(ToolItems.GUIDE_TO_CRITTER_COMPANIONSHIP.toStack()),
                offer(ToolItems.GUIDE_TO_ENVIRONMENTAL_PRESERVATION.toStack()),
                offer(ToolItems.GUIDE_TO_PEACEFUL_COEXISTENCE.toStack()),
                offer(VanityArmorItems.BUNNY_HOOD.toStack()),
                offer(new ItemStack(Items.APPLE))
        ));
        shops.put(Confluence.asResource("goblin_tinkerer"), List.of(
                offer(HookItems.GRAPPLING_HOOK.toStack()),
                offer(new ItemStack(ConsumableItems.SPIKY_BALL.get(), 25))
        ));
        // 渔夫和渔女是微光外观变体，必须共享同一份商品定义，避免两侧内容逐渐产生差异。
        List<NPCTradeOffer> anglerSupplyOffers = List.of(
                offer(FishingPoleItems.WOOD_FISHING_POLE.toStack()),
                offer(BaitItems.APPRENTICE_BAIT.toStack()),
                offer(BaitItems.JOURNEYMAN_BAIT.toStack()),
                offer(BaitItems.MASTER_BAIT.toStack())
        );
        shops.put(Confluence.asResource("angler"), anglerSupplyOffers);
        shops.put(Confluence.asResource("female_angler"), anglerSupplyOffers);
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
                offer(NatureBlocks.DYNASTY_LOG_BLOCKS.LOG.toStack()),
                offer(FishingPoleItems.SITTING_DUCKS_FISHING_POLE.toStack())
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
}
