package org.confluence.mod.common.data.gen;

import com.google.common.collect.ImmutableMap;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.Keys;
import org.confluence.mod.common.data.saved.DateStamp;
import org.confluence.mod.common.data.saved.MoonPhase;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.gameevent.SlimeRainGameEvent;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.CrateBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.integration.terra_entity.npc_trade.DeferredMoneyTradeItem;
import org.confluence.mod.integration.terra_entity.npc_trade.MoneyTradeHealthFull;
import org.confluence.mod.integration.terra_entity.npc_trade.MoneyTradeItem;
import org.confluence.mod.integration.terra_entity.npc_trade.SellTrade;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.*;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.entity.npc.trade.NPCTradeManager;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.init.item.TEWhipItems;
import org.confluence.terraentity.init.item.TEYoyosItems;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.variant.ItemTradeLootTable;
import org.confluence.terraentity.registries.npc_trade.variant.TradeTask;
import org.confluence.terraentity.registries.npc_trade_list.variant.WeightMapGenerator;
import org.confluence.terraentity.registries.npc_trade_lock.variant.*;
import org.confluence.terraentity.registries.npc_trade_task.variant.DynamicAnglerTradeTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/// 生成单个NPC单个配方
///
/// @see ITrade 生成配方
/// @see NPCTradeManager 读取配方
public class NPCShopProvider extends AbstractRecipeProvider {
    private static final boolean ENABLE_DEBUG_SHOPS = false;
    private final PackOutput.PathProvider npcShopPathProvider;

    public NPCShopProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
        this.npcShopPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "npc/shop");
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        SecretFlagLock theCorruptionWorldLock = new SecretFlagLock(IWorldOptions.THE_CORRUPTION);
        SecretFlagLock theCrimsonWorldLock = new SecretFlagLock(IWorldOptions.THE_CRIMSON);
        SecretFlagLock hardmodeLock = new SecretFlagLock(IWorldOptions.HARDMODE);
        GameEventLock bloodMoonLock = new GameEventLock(BloodMoonGameEvent.KEY);
        EnvironmentLock ectoMistLock = new EnvironmentLock(EnvironmentLevelAccess.matcher(null, null, true));
        BiomeLock glowingMushroomLock = BiomeLock.of(ModBiomes.GLOWING_MUSHROOM);
        BiomeLock theHallowLock = BiomeLock.of(ModTags.Biomes.THE_HALLOW);
        BiomeLock snowyLikeLock = BiomeLock.of(Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_ICY);
        BiomeLock jungleLikeLock = BiomeLock.of(Tags.Biomes.IS_JUNGLE, Tags.Biomes.IS_LUSH);
        BiomeLock forestLock = BiomeLock.of(ModTags.Biomes.IS_FOREST);
        BiomeLock desertLock = BiomeLock.of(Tags.Biomes.IS_DESERT);
        BiomeLock oceanLock = BiomeLock.of(Tags.Biomes.IS_OCEAN);
        PositionLock caveThroughSurfaceLock = PositionLock.ofY(MinMaxBounds.Ints.between(OverworldUtils.getCaveY(), OverworldUtils.getSurfaceY()));
        PositionLock caveThroughUndergroundLock = PositionLock.ofY(MinMaxBounds.Ints.between(OverworldUtils.getCaveY(), OverworldUtils.getUndergroundY()));
        PositionLock surfaceThroughUltraLock = PositionLock.ofY(MinMaxBounds.Ints.between(OverworldUtils.getSurfaceY(), OverworldUtils.getUltraY()));
        PositionLock surfaceThroughSpaceLock = PositionLock.ofY(MinMaxBounds.Ints.between(OverworldUtils.getSurfaceY(), OverworldUtils.getSpaceY()));
        PositionLock spaceThroughUltraLock = PositionLock.ofY(MinMaxBounds.Ints.between(OverworldUtils.getSpaceY(), OverworldUtils.getUltraY()));
        ITradeLock notEvilBiomes = ITradeLock.or(BiomeLock.of(ModTags.Biomes.THE_CORRUPTION), BiomeLock.of(ModTags.Biomes.THE_CRIMSON), theHallowLock).invert();

        TradeProperties hardmode = TradeProperties.builder().setLock(hardmodeLock).build();
        TradeProperties halloweens = TradeProperties.builder().setLock(DateLock.HALLOWEENS).build();
        TradeProperties night = TradeProperties.builder().setLock(new TimeLock(LibDateUtils._19$30, LibDateUtils._04$30, false)).build();

        // 女仆商店
        shop(Keys.MAID_SHOP).addRecipe(withDefaultPylon()
                .add(TCItems.PORTABLE_CEMENT_MIXER)
                .add(TCItems.EXTENDO_GRIP)
                .add(TCItems.BRICK_LAYER)
                .add(TCItems.STOPWATCH)
                .add(TCItems.LIFE_FORM_ANALYZER)
                .add(TCItems.DPS_METER)
                .add(AccessoryItems.PAINT_SPRAYER)
                .add(Objects.requireNonNull(TEWhipItems.LEATHER_WHIP))
                .add(SwordItems.KATANA)
                .add(FoodItems.PAD_THAI)
                .add(SellTrade.INSTANCE)
                .build());

        shop(TENpcEntities.GUIDE.getId()).addRecipe(new Builder()
                //旅商的
                //动物学家的              .add(TEWhipItems.LEATHER_WHIP)
                .build());

        shop(TENpcEntities.DEMOLITIONIST.getId()).addRecipe(withDefaultPylon()
                .add(ConsumableItems.GRENADE)
                .add(ConsumableItems.BOMB)
                .add(ConsumableItems.DYNAMITE)
                .add(new MoneyTradeItem.Builder()
                        .setResult(Items.GUNPOWDER)
                        .setProperties(TradeProperties.builder().setLock(ITradeLock.and(ITradeLock.or(bloodMoonLock, ectoMistLock), theCrimsonWorldLock)).build())
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(MaterialItems.EXPLOSIVE_POWDER.toStack())
                        .setProperties(hardmode)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(ArrowItems.HELLFIRE_ARROW.toStack())
                        .setProperties(hardmode)
                        .build())
                // 毒刺矢 石巨人后
                // 地雷 世纪之花且海盗后
                //.add(ConsumableItems.DRY_BOMB) todo当持有干炸弹时
                //.add(ConsumableItems.WET_BOMB) todo当持有湿炸弹时
                //.add(ConsumableItems.HONEY_BOMB) todo当持有蜂蜜炸弹时
                //.add(ConsumableItems.LAVA_BOMB) todo当持有熔岩炸弹时

                .add(SellTrade.INSTANCE)
                .build());

        shop(TENpcEntities.MERCHANT.getId()).addRecipe(withDefaultPylon()
                .add(ToolItems.BUG_NET)
                .add(ArmorItems.MINING_HELMET)
                .add(Blocks.ANVIL)
                .add(Blocks.TORCH)
                .add(Items.ARROW)
                .add(Items.ARROW, 100)
                .add(ModBlocks.ROPE)
                .add(ConsumableItems.SHURIKEN)
                .add(FunctionalBlocks.PIGGY_BANK)
                .add(FunctionalBlocks.SAFE) // todo骷髅王后
                .add(PickaxeItems.COPPER_PICKAXE)
                .add(AxeItems.COPPER_AXE)
                .add(PotionItems.LESSER_HEALING_POTION)
                .add(PotionItems.LESSER_MANA_POTION)
                .add(FoodItems.MARSHMALLOW) // todo当在雪原时
                .add(TFBlocks.PIN_WHEEL) // todo当在大风天时

                .add(new MoneyTradeItem.Builder()
                        .setResult(PotionItems.HEALING_POTION.toStack())
                        .setProperties(hardmode)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(PotionItems.MANA_POTION.toStack())
                        .setProperties(hardmode)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(FunctionalBlocks.SHARPENING_STATION.toStack())
                        .setProperties(hardmode)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(MaterialItems.GOLD_DUST.toStack())
                        .setProperties(hardmode)
                        .build())
                .add(SellTrade.INSTANCE)
                // 照明弹
                // 蓝照明弹
                // 迪斯科球 困难模式后
                // 镰刀
                // 钉子 拥有钉枪时
                // 鼓组 boss后
                // 鼓槌 boss后
                // 荧光棒
                .build());

        shop(TENpcEntities.GOBLIN_TINKERER.getId()).addRecipe(withDefaultPylon()
                .add(HookItems.GRAPPLING_HOOK)
                .add(TCItems.ROCKET_BOOTS)
                .add(TCItems.TOOLBELT)
                .add(TCItems.WORKSHOP)
                .add(ConsumableItems.SPIKY_BALL)
                .add(SellTrade.INSTANCE)
                // 标尺
                // 堆石器 （困难模式）

                .build());

        shop(TENpcEntities.NURSE.getId()).addRecipe(withDefaultPylon()
                .add(MoneyTradeHealthFull.create())
                .build());

        shop(TENpcEntities.ARMS_DEALER.getId()).addRecipe(withDefaultPylon()
                .add(TGItems.MUSKET_BULLET)
                .add(TGItems.MUSKET_BULLET, 100)
                .add(new MoneyTradeItem.Builder()
                        .setResult(TGItems.SILVER_BULLET.toStack())
                        .setProperties(hardmode)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(TGItems.SILVER_BULLET.toStack(100))
                        .setProperties(hardmode)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(TGItems.TUNGSTEN_BULLET.toStack())
                        .setProperties(hardmode)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(TGItems.TUNGSTEN_BULLET.toStack(100))
                        .setProperties(hardmode)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(MaterialItems.EMPTY_BULLET.toStack())
                        .setProperties(hardmode)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(FunctionalBlocks.AMMO_BOX.toStack())
                        .setProperties(hardmode)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(TGItems.SHOTGUN.toStack())
                        .setProperties(hardmode)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(ArrowItems.UNHOLY_ARROW.toStack())
                        .setProperties(hardmode)
                        .build())
                // 钉子 当拥有钉枪时
                // 玉米糖 当拥有玉米发射器时
                // 毒刺矢 当拥有毒刺发射器时
                // 杰克南瓜灯 当拥有杰克南瓜灯发射器时
                // 四管霰弹枪 骷髅王后且灵雾
                // 护士帽 万圣节
                // 护士衣服 万圣节
                // 护士短裙 万圣节
                // 护士高跟鞋 万圣节
                .add(TGItems.FLINTLOCK_PISTOL)
                .add(TGItems.MINISHARK)
                .add(SellTrade.INSTANCE)
                .build());

        shop(TENpcEntities.DRYAD.getId()).addRecipe(withDefaultPylon()
                .add(ConsumableItems.PURIFICATION_POWDER)
                .add(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.SAPLING)
                .add(Blocks.OAK_SAPLING)
                .add(Blocks.SUNFLOWER)
                .add(Blocks.FLOWER_POT)
                .add(TFBlocks.HANGING_POT)
                .add(Items.PUMPKIN_SEEDS)
                .add(ModItems.GRASS_SEED)
                .add(ToolItems.GUIDE_TO_ENVIRONMENTAL_PRESERVATION)
                .add(new MoneyTradeItem.Builder()
                        .setResult(ModItems.HALLOWED_SEED.toStack())
                        .setProperties(hardmode)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(ModItems.ASH_GRASS_SEED)
                        .setProperties(TradeProperties.builder().setLock(new DimensionLock(OverworldUtils.underworld())).build())
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(ModItems.MUSHROOM_GRASS_SEED)
                        .setProperties(TradeProperties.builder().setLock(glowingMushroomLock).build())
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(ModItems.CRIMSON_SEED)
                        .setProperties(TradeProperties.builder().setLock(ITradeLock.and(ITradeLock.or(bloodMoonLock, ectoMistLock), theCorruptionWorldLock)).build())
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(ConsumableItems.VILE_POWDER.toStack())
                        .setProperties(TradeProperties.builder().setLock(ITradeLock.and(bloodMoonLock, theCorruptionWorldLock)).build())
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(ConsumableItems.VICIOUS_POWDER.toStack())
                        .setProperties(TradeProperties.builder().setLock(ITradeLock.and(bloodMoonLock, theCrimsonWorldLock)).build())
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(ModItems.CORRUPT_SEED)
                        .setProperties(TradeProperties.builder().setLock(ITradeLock.and(ITradeLock.or(bloodMoonLock, ectoMistLock), theCrimsonWorldLock)).build())
                        .build())
                .add(SellTrade.INSTANCE)
                .build());

        shop(TENpcEntities.DYE_TRADER.getId()).addRecipe(withDefaultPylon()
                .add(FunctionalBlocks.DYE_VAT)
                .add(VanityArmorItems.SILVER_DYE)
                .add(VanityArmorItems.BROWN_DYE)
                .add(VanityArmorItems.TEAM_DYE)
                // 染料商长袍
                // 染料商头巾
                // 暗影染料
                // 阴暗染料
                // 灰雾染料
                // 大屠杀染料
                .add(SellTrade.INSTANCE)
                .build());


        shop(TENpcEntities.PAINTER.getId()).addRecipe(withDefaultPylon()
                .add(PaintItems.PAINTBRUSH)
                .add(PaintItems.PAINT_ROLLER)
                .add(PaintItems.PAINT_SCRAPER)
                .add(PaintItems.RED_PAINT)
                .add(PaintItems.DEEP_RED_PAINT)
                .add(PaintItems.ORANGE_PAINT)
                .add(PaintItems.DEEP_ORANGE_PAINT)
                .add(PaintItems.YELLOW_PAINT)
                .add(PaintItems.DEEP_YELLOW_PAINT)
                .add(PaintItems.LIME_PAINT)
                .add(PaintItems.DEEP_LIME_PAINT)
                .add(PaintItems.GREEN_PAINT)
                .add(PaintItems.DEEP_GREEN_PAINT)
                .add(PaintItems.TEAL_PAINT)
                .add(PaintItems.DEEP_TEAL_PAINT)
                .add(PaintItems.CYAN_PAINT)
                .add(PaintItems.DEEP_CYAN_PAINT)
                .add(PaintItems.SKY_BLUE_PAINT)
                .add(PaintItems.DEEP_SKY_BLUE_PAINT)
                .add(PaintItems.BLUE_PAINT)
                .add(PaintItems.DEEP_BLUE_PAINT)
                .add(PaintItems.PURPLE_PAINT)
                .add(PaintItems.DEEP_PURPLE_PAINT)
                .add(PaintItems.VIOLET_PAINT)
                .add(PaintItems.DEEP_VIOLET_PAINT)
                .add(PaintItems.PINK_PAINT)
                .add(PaintItems.DEEP_PINK_PAINT)
                .add(PaintItems.BLACK_PAINT)
                .add(PaintItems.GRAY_PAINT)
                .add(PaintItems.WHITE_PAINT)
                .add(PaintItems.BROWN_PAINT)
                .add(new MoneyTradeItem.Builder()
                        .setResult(PaintItems.SHADOW_PAINT.toStack())
                        .setProperties(hardmode)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(PaintItems.NEGATIVE_PAINT.toStack())
                        .setProperties(hardmode)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(PaintItems.ILLUMINANT_COATING.toStack())
                        .setProperties(TradeProperties.builder()
                                .setLock(ectoMistLock)
                                .build())
                        .build())
                .add(SellTrade.INSTANCE)
                .build());

        NPCTradeManager anglerTradeManager = new Builder().add(TradeTask.create(DynamicAnglerTradeTask.builder(
                        ItemTradeLootTable.builder()
                                .addCost(CrateBlocks.WOODEN_CRATE.toStack()) // 在没有任务鱼机制前，用木匣代替
                                .setLootTable(ModLootTables.QUESTS_0)
                                .setSprite(TerraEntity.space("random_gift"))
                                .build(),
                        ImmutableMap.<ItemStack, ITradeLock>builder()
                                .put(QuestedFishes.AMANITA_FUNGIFIN.toStack(), glowingMushroomLock)
                                .put(QuestedFishes.BLOODY_MANOWAR.toStack(), new QuestedFishPrecheckLock(false))
                                .put(QuestedFishes.ICHORFISH.toStack(), new QuestedFishPrecheckLock(false))
                                .put(QuestedFishes.CURSEDFISH.toStack(), new QuestedFishPrecheckLock(true))
                                .put(QuestedFishes.EATER_OF_PLANKTON.toStack(), new QuestedFishPrecheckLock(true))
                                .put(QuestedFishes.INFECTED_SCABBARDFISH.toStack(), new QuestedFishPrecheckLock(true))
                                .put(QuestedFishes.FISHRON.toStack(), ITradeLock.and(snowyLikeLock, caveThroughSurfaceLock))
                                .put(QuestedFishes.MUTANT_FLINXFIN.toStack(), ITradeLock.and(snowyLikeLock, caveThroughSurfaceLock))
                                .put(QuestedFishes.PENGFISH.toStack(), ITradeLock.and(snowyLikeLock, surfaceThroughUltraLock))
                                .put(QuestedFishes.TUNDRA_TROUT.toStack(), ITradeLock.and(snowyLikeLock, surfaceThroughUltraLock))
                                .put(QuestedFishes.CATFISH.toStack(), ITradeLock.and(jungleLikeLock, surfaceThroughSpaceLock))
                                .put(QuestedFishes.DERPFISH.toStack(), ITradeLock.and(jungleLikeLock, surfaceThroughSpaceLock))
                                .put(QuestedFishes.MUDFISH.toStack(), jungleLikeLock)
                                .put(QuestedFishes.TROPICAL_BARRACUDA.toStack(), surfaceThroughSpaceLock)
                                .put(QuestedFishes.BUNNYFISH.toStack(), ITradeLock.and(forestLock, surfaceThroughSpaceLock))
                                .put(QuestedFishes.SLIMEFISH.toStack(), ITradeLock.and(forestLock, surfaceThroughSpaceLock))
                                .put(QuestedFishes.ZOMBIE_FISH.toStack(), ITradeLock.and(forestLock, surfaceThroughSpaceLock))
                                .put(QuestedFishes.SCARAB_FISH.toStack(), desertLock)
                                .put(QuestedFishes.SCORPIO_FISH.toStack(), desertLock)
                                .put(QuestedFishes.CAPN_TUNABEARD.toStack(), ITradeLock.and(oceanLock, surfaceThroughUltraLock))
                                .put(QuestedFishes.CLOWNFISH.toStack(), ITradeLock.and(oceanLock, surfaceThroughUltraLock))
                                .put(QuestedFishes.BUMBLEBEE_TUNA.toStack(), FishingHookInFluidLock.of(true, Tags.Fluids.HONEY))
                                .put(QuestedFishes.ANGELFISH.toStack(), ITradeLock.and(notEvilBiomes, spaceThroughUltraLock, AnyBossDefeatedLock.INSTANCE))
                                .put(QuestedFishes.CLOUDFISH.toStack(), ITradeLock.and(notEvilBiomes, spaceThroughUltraLock, AnyBossDefeatedLock.INSTANCE))
                                .put(QuestedFishes.WYVERNTAIL.toStack(), ITradeLock.and(notEvilBiomes, spaceThroughUltraLock))
                                .put(QuestedFishes.DEMONIC_HELLFISH.toStack(), ITradeLock.and(notEvilBiomes, caveThroughUndergroundLock))
                                .put(QuestedFishes.FISHOTRON.toStack(), ITradeLock.and(notEvilBiomes, caveThroughUndergroundLock))
                                .put(QuestedFishes.GUIDE_VOODOO_FISH.toStack(), ITradeLock.and(notEvilBiomes, caveThroughUndergroundLock))
                                .put(QuestedFishes.HUNGERFISH.toStack(), ITradeLock.and(notEvilBiomes, caveThroughUndergroundLock))
                                .put(QuestedFishes.BATFISH.toStack(), ITradeLock.and(notEvilBiomes, caveThroughSurfaceLock))
                                .put(QuestedFishes.BONEFISH.toStack(), ITradeLock.and(notEvilBiomes, caveThroughSurfaceLock))
                                .put(QuestedFishes.JEWELFISH.toStack(), ITradeLock.and(notEvilBiomes, caveThroughSurfaceLock))
                                .put(QuestedFishes.MIRAGE_FISH.toStack(), ITradeLock.and(theHallowLock, caveThroughSurfaceLock))
                                .put(QuestedFishes.SPIDERFISH.toStack(), ITradeLock.and(notEvilBiomes, caveThroughSurfaceLock))
                                .put(QuestedFishes.DIRTFISH.toStack(), ITradeLock.and(notEvilBiomes, PositionLock.ofY(MinMaxBounds.Ints.between(OverworldUtils.getUndergroundY(), OverworldUtils.getSpaceY()))))
                                .put(QuestedFishes.DYNAMITE_FISH.toStack(), ITradeLock.and(notEvilBiomes, surfaceThroughSpaceLock))
                                .put(QuestedFishes.FALLEN_STARFISH.toStack(), ITradeLock.and(notEvilBiomes, surfaceThroughUltraLock))
                                .put(QuestedFishes.THE_FISH_OF_CTHULHU.toStack(), ITradeLock.and(notEvilBiomes, surfaceThroughUltraLock))
                                .put(QuestedFishes.HARPYFISH.toStack(), ITradeLock.and(notEvilBiomes, surfaceThroughUltraLock))
                                .put(QuestedFishes.PIXIEFISH.toStack(), ITradeLock.and(theHallowLock, surfaceThroughUltraLock))
                                .put(QuestedFishes.UNICORN_FISH.toStack(), ITradeLock.and(theHallowLock, surfaceThroughSpaceLock))
                                .build())
                .addResult(10, List.of(ArmorItems.ANGLER_HAT.toStack()))
                .addResult(15, List.of(ArmorItems.ANGLER_VEST.toStack()))
                .addResult(20, List.of(ArmorItems.ANGLER_PANTS.toStack()))
                .addResult(25, List.of(ToolItems.BOTTOMLESS_WATER_BUCKET.toStack()))
                .addResult(30, List.of(FishingPoleItems.GOLDEN_FISHING_ROD.toStack()))
                .addLootTable(10, ModLootTables.QUESTS_AFTER_10)
                .addLootTable(75, ModLootTables.QUESTS_AFTER_75)
                .build())).build();
        shop(TENpcEntities.ANGLER.getId()).addRecipe(anglerTradeManager);
        shop(TENpcEntities.FEMALE_ANGLER.getId()).addRecipe(anglerTradeManager);

        shop(TENpcEntities.MECHANIC.getId()).addRecipe(withDefaultPylon()
                .add(ToolItems.RED_WRENCH)
                .add(ToolItems.BLUE_WRENCH)
                .add(ToolItems.GREEN_WRENCH)
                .add(ToolItems.YELLOW_WRENCH)
                .add(ToolItems.WIRE_CUTTER)
                .add(new MoneyTradeItem.Builder()
                        .setResult(FishingPoleItems.MECHANICS_ROD.toStack())
                        .setProperties(TradeProperties.builder().setLock(new MoonPhaseLock(
                                MoonPhase.WANING_GIBBOUS,
                                MoonPhase.WANING_CRESCENT,
                                MoonPhase.WAXING_CRESCENT,
                                MoonPhase.WAXING_GIBBOUS
                        )).build())
                        .build())
                .add(FunctionalBlocks.SWITCH)
                .add(FunctionalBlocks.SIGNAL_ADAPTER)
                .add(FunctionalBlocks.TIMERS_BLOCK_1_1)
                .add(FunctionalBlocks.TIMERS_BLOCK_3_1)
                .add(FunctionalBlocks.TIMERS_BLOCK_5_1)
                .add(FunctionalBlocks.TIMERS_BLOCK_1_2)
                .add(FunctionalBlocks.TIMERS_BLOCK_1_4)
                .add(FunctionalBlocks.EVER_POWERED_RAIL)
                .add(AccessoryItems.MECHANICAL_LENS)
                .add(Items.PISTON)
                .add(Items.STICKY_PISTON)
                .add(Items.REDSTONE_LAMP)
                .add(Items.DAYLIGHT_DETECTOR)
                // 控制杆
                // 7色的压力板
                // 青绿压力垫板
                // 机械标尺
                // 工程头盔
                // 彩线灯泡
                .add(SellTrade.INSTANCE)
                .build());

        shop(TENpcEntities.TRAVELING_MERCHANT.getId()).addRecipe(
                new NPCTradeManager(WeightMapGenerator.builder(5) // 这个数字其实无意义，因为旅商是通过事件修改是数量
                        .addTrade(new MoneyTradeItem.Builder().setResult(AccessoryItems.PAINT_SPRAYER).build(), 1)
                        .addTrade(new MoneyTradeItem.Builder().setResult(TCItems.PORTABLE_CEMENT_MIXER).build(), 1)
                        .addTrade(new MoneyTradeItem.Builder().setResult(TCItems.EXTENDO_GRIP).build(), 1)
                        .addTrade(new MoneyTradeItem.Builder().setResult(TCItems.BRICK_LAYER).build(), 1)
                        .addTrade(new MoneyTradeItem.Builder().setResult(TCItems.STOPWATCH).build(), 1)
                        .addTrade(new MoneyTradeItem.Builder().setResult(TCItems.LIFE_FORM_ANALYZER).build(), 1)
                        .addTrade(new MoneyTradeItem.Builder().setResult(TCItems.DPS_METER).build(), 1)
                        .addTrade(new MoneyTradeItem.Builder().setResult(SwordItems.KATANA).build(), 1)
                        .addTrade(new MoneyTradeItem.Builder().setResult(FoodItems.PAD_THAI).build(), 1)
                        .addTrade(new MoneyTradeItem.Builder().setResult(TEYoyosItems.CODE_1).build(), 1)
                        .addTrade(new MoneyTradeItem.Builder().setResult(NatureBlocks.DYNASTY_LOG_BLOCKS.LOG).build(), 1)
                        .addTrade(new MoneyTradeItem.Builder().setResult(FishingPoleItems.SITTING_DUCKS_FISHING_POLE).build(), 1) //todo骷髅王后
                        .build()
                )
        );

        shop(TENpcEntities.CLOTHIER.getId()).addRecipe(withDefaultPylon()
                .add(VanityArmorItems.FAMILIAR_WIG)
                .add(VanityArmorItems.FAMILIAR_SHIRT)
                .add(VanityArmorItems.FAMILIAR_PANTS)
                .add(VanityArmorItems.FAMILIAR_SHOES)
                .add(new MoneyTradeItem.Builder()
                        .setResult(VanityArmorItems.GUY_FAWKES_HAT.toStack())
                        .setProperties(halloweens)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(VanityArmorItems.GUY_FAWKES_MASK.toStack())
                        .setProperties(halloweens)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(VanityArmorItems.GUY_FAWKES_MASK_SET.toStack())
                        .setProperties(halloweens)
                        .build())
                .add(SellTrade.INSTANCE)
                .build());

        shop(TENpcEntities.PARTY_GIRL.getId()).addRecipe(withDefaultPylon()
                .add(FunctionalBlocks.SILLY_BALLOON_MACHINE)
                .add(ConsumableItems.SMOKE_BOMB)
                .add(MaterialItems.CONFETTI)
                .add(MinecartItems.PARTY_WAGON)
                .add(SellTrade.INSTANCE)
                .build());

        shop(TENpcEntities.WITCH_DOCTOR.getId()).addRecipe(withDefaultPylon()
                .add(TGItems.BLOWGUN)
                .add(new MoneyTradeItem.Builder()
                        .setResult(FunctionalBlocks.CAULDRON.toStack())
                        .setProperties(halloweens)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(AccessoryItems.PYGMY_NECKLACE.toStack())
                        .setProperties(night)
                        .build())
                // 毒刺矢
                // 尖桩
                // .add(MaterialItems.VIAL_OF_VENOM) 世纪之花后
                //   .add(AccessoryItems.HERCULES_BEETLE) 世纪之花后，丛林
                // 各种喷泉
                // 提基套  todo 花后
//                .add(new MoneyTradeItem.Builder()
//                        .setResult(ArmorItems.TIKI_MASK.toStack())
//                        .setProperties(hardmode)
//                        .build())
//                .add(new MoneyTradeItem.Builder()
//                        .setResult(ArmorItems.TIKI_SHIRT.toStack())
//                        .setProperties(hardmode)
//                        .build())
//                .add(new MoneyTradeItem.Builder()
//                        .setResult(ArmorItems.TIKI_LEGGINGS.toStack())
//                        .setProperties(hardmode)
//                        .build())
//                .add(new MoneyTradeItem.Builder()
//                        .setResult(ArmorItems.TIKI_BOOTS.toStack())
//                        .setProperties(hardmode)
//                        .build())

                .add(SellTrade.INSTANCE)
                .build());

        shop(TENpcEntities.ZOOLOGIST.getId()).addRecipe(withDefaultPylon()
                .add(ToolItems.GUIDE_TO_CRITTER_COMPANIONSHIP)
                .add(new MoneyTradeItem.Builder()
                        .setResult(TEWhipItems.LEATHER_WHIP.toStack())
                        .setProperties(TradeProperties.builder().setLock(new BestiaryUnlockedCountLock(16)).build())
                        .build())
                .add(MinecartItems.DIGGING_MOLECART)
                .add(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.SAPLING)
                .add(Items.CHERRY_SAPLING)
                .add(SellTrade.INSTANCE)
                .add(new MoneyTradeItem.Builder()
                        .setResult(LanceItems.JOUSTING_LANCE.toStack())
                        .setProperties(hardmode)
                        .setProperties(TradeProperties.builder().setLock(new BestiaryUnlockedCountLock(75)).build())
                        .build())
                .build());

        if (ENABLE_DEBUG_SHOPS) {
            addDebugShops(holderLookup);
        }
    }

    /**
     * Adds debug shops where each trade contains single lock types
     */
    private void addDebugShops(HolderLookup.Provider holderLookup) {
        shop(Confluence.asResource("debug_shop")).addRecipe(
                new Builder().add(
                        new MoneyTradeItem.Builder()
                                .setResult(ToolItems.BUG_NET)
                                .setProperties(
                                        TradeProperties.builder().setLock(
                                                ITradeLock.and(
                                                        new DimensionLock(
                                                                Level.NETHER
                                                        )
                                                )
                                        ).build()
                                ).build()
                ).add(
                        new MoneyTradeItem.Builder()
                                .setResult(ToolItems.BUG_NET)
                                .setProperties(
                                        TradeProperties.builder().setLock(
                                                ITradeLock.and(
                                                        new EnvironmentLock(
                                                                EnvironmentLevelAccess.matcher(
                                                                        holderLookup.lookupOrThrow(Registries.BIOME).getOrThrow(Tags.Biomes.IS_COLD_OVERWORLD),
                                                                        null,
                                                                        true
                                                                )
                                                        ),
                                                        new EnvironmentLock(
                                                                EnvironmentLevelAccess.matcher(
                                                                        null,
                                                                        new EnvironmentLevelAccess.SearchContext(
                                                                                5,
                                                                                Optional.of(holderLookup.lookupOrThrow(Registries.BLOCK).getOrThrow(Tags.Blocks.CHESTS_ENDER)),
                                                                                List.of(),
                                                                                Optional.empty()
                                                                        ),
                                                                        true
                                                                )
                                                        ),
                                                        new EnvironmentLock(
                                                                EnvironmentLevelAccess.matcher(
                                                                        null,
                                                                        new EnvironmentLevelAccess.SearchContext(
                                                                                5,
                                                                                Optional.empty(),
                                                                                List.of(
                                                                                        StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 7).build().get()
                                                                                ),
                                                                                Optional.empty()
                                                                        ),
                                                                        true
                                                                )
                                                        ),
                                                        new EnvironmentLock(
                                                                EnvironmentLevelAccess.matcher(
                                                                        null,
                                                                        new EnvironmentLevelAccess.SearchContext(
                                                                                5,
                                                                                Optional.empty(),
                                                                                List.of(),
                                                                                Optional.of(holderLookup.lookupOrThrow(Registries.FLUID).getOrThrow(Tags.Fluids.LAVA))
                                                                        ),
                                                                        true
                                                                )
                                                        )
                                                )
                                        ).build()
                                ).build()
                ).add(
                        new MoneyTradeItem.Builder()
                                .setResult(ToolItems.BUG_NET)
                                .setProperties(
                                        TradeProperties.builder().setLock(
                                                ITradeLock.and(
                                                        new FishingHookInFluidLock(
                                                                List.of(Tags.Fluids.WATER, Tags.Fluids.HONEY),
                                                                true
                                                        )
                                                )
                                        ).build()
                                ).build()
                ).add(
                        new MoneyTradeItem.Builder()
                                .setResult(ToolItems.BUG_NET)
                                .setProperties(
                                        TradeProperties.builder().setLock(
                                                ITradeLock.and(
                                                        new GameEventLock(BloodMoonGameEvent.KEY),
                                                        new GameEventLock(SlimeRainGameEvent.KEY)
                                                )
                                        ).build()
                                ).build()
                ).add(
                        new MoneyTradeItem.Builder()
                                .setResult(ToolItems.BUG_NET)
                                .setProperties(
                                        TradeProperties.builder().setLock(
                                                ITradeLock.and(
                                                        new BiomeLock(
                                                                List.of(
                                                                        Biomes.PLAINS,
                                                                        ModBiomes.GLOWING_MUSHROOM //Test names for modded biomes
                                                                ),
                                                                List.of(
                                                                        Tags.Biomes.IS_JUNGLE,
                                                                        ModTags.Biomes.THE_HALLOW
                                                                )
                                                        )
                                                )
                                        ).build()
                                ).build()
                ).add(
                        new MoneyTradeItem.Builder()
                                .setResult(ToolItems.BUG_NET)
                                .setProperties(
                                        TradeProperties.builder().setLock(
                                                ITradeLock.and(
                                                        new KillEntityLock(TEBossEntities.BRAIN_OF_CTHULHU.get()),
                                                        new KillEntityLock(EntityType.ZOMBIE)
                                                )
                                        ).build()
                                ).build()
                ).add(
                        new MoneyTradeItem.Builder()
                                .setResult(ToolItems.BUG_NET)
                                .setProperties(
                                        TradeProperties.builder().setLock(
                                                ITradeLock.and(
                                                        new NPCExistLock(TENpcEntities.GOBLIN_TINKERER.get()),
                                                        new NPCExistLock(EntityType.ZOMBIE)
                                                )
                                        ).build()
                                ).build()
                ).add(
                        new MoneyTradeItem.Builder()
                                .setResult(ToolItems.BUG_NET)
                                .setProperties(
                                        TradeProperties.builder().setLock(
                                                ITradeLock.and(
                                                        AnyBossDefeatedLock.INSTANCE
                                                )
                                        ).build()
                                ).build()
                ).add(
                        new MoneyTradeItem.Builder()
                                .setResult(ToolItems.BUG_NET)
                                .setProperties(
                                        TradeProperties.builder().setLock(
                                                ITradeLock.and(
                                                        new BestiaryUnlockedCountLock(10)
                                                )
                                        ).build()
                                ).build()
                ).add(
                        new MoneyTradeItem.Builder()
                                .setResult(ToolItems.BUG_NET)
                                .setProperties(
                                        TradeProperties.builder().setLock(
                                                ITradeLock.and(
                                                        new DateLock(false, new DateStamp(2, 28), new DateStamp(13, 37)),
                                                        new DateLock(true, new DateStamp(2, 28), new DateStamp(13, 37)),
                                                        DateLock.HALLOWEENS,
                                                        DateLock.CHRISTMAS
                                                )
                                        ).build()
                                ).build()
                ).add(
                        new MoneyTradeItem.Builder()
                                .setResult(ToolItems.BUG_NET)
                                .setProperties(
                                        TradeProperties.builder().setLock(
                                                ITradeLock.and(
                                                        new PositionLock(MinMaxBounds.Ints.between(1, 10), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY),
                                                        new PositionLock(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.between(1, 10), MinMaxBounds.Ints.ANY),
                                                        new PositionLock(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.between(1, 10)),
                                                        new PositionLock(MinMaxBounds.Ints.between(1, 10), MinMaxBounds.Ints.between(2, 20), MinMaxBounds.Ints.between(3, 30))
                                                )
                                        ).build()
                                ).build()
                ).add(
                        new MoneyTradeItem.Builder()
                                .setResult(ToolItems.BUG_NET)
                                .setProperties(
                                        TradeProperties.builder().setLock(
                                                ITradeLock.and(
                                                        new MoodLock(100, false),
                                                        new MoodLock(50, true)
                                                )
                                        ).build()
                                ).build()
                ).add(
                        new MoneyTradeItem.Builder()
                                .setResult(ToolItems.BUG_NET)
                                .setProperties(
                                        TradeProperties.builder().setLock(
                                                ITradeLock.and(
                                                        new TimeLock(2500, 13000, false),
                                                        new TimeLock(2500, 13000, true)
                                                )
                                        ).build()
                                ).build()
                ).build()
        );
    }


    protected Appender<NPCTradeManager> shop(ResourceLocation id) {
        // 预处理命名空间，命名空间替换成confluence，本体不能覆盖子模块的数据包
        return recipe(NPCTradeManager.CODEC, pathProvider().json(Confluence.asResource(id.getPath())));
    }

    protected Builder withDefaultPylon() {
        ITradeLock waystonesLock = withModLoaded(WaystonesHelper.MODID);
        return new Builder()
                .add(new DeferredMoneyTradeItem(WaystonesHelper.FOREST_PYLON.getId(), 1, ITradeLock.and(BiomeLock.of(Tags.Biomes.IS_FOREST, Tags.Biomes.IS_PLAINS), waystonesLock)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.SNOW_PYLON.getId(), 1, ITradeLock.and(BiomeLock.of(Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_ICY), waystonesLock)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.DESERT_PYLON.getId(), 1, ITradeLock.and(BiomeLock.of(Tags.Biomes.IS_DESERT, Tags.Biomes.IS_BADLANDS), waystonesLock)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.CAVERN_PYLON.getId(), 1, ITradeLock.and(PositionLock.ofY(MinMaxBounds.Ints.atMost(OverworldUtils.getSurfaceY())), waystonesLock)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.OCEAN_PYLON.getId(), 1, ITradeLock.and(BiomeLock.of(Tags.Biomes.IS_OCEAN), waystonesLock)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.JUNGLE_PYLON.getId(), 1, ITradeLock.and(BiomeLock.of(Tags.Biomes.IS_JUNGLE), waystonesLock)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.HALLOW_PYLON.getId(), 1, ITradeLock.and(BiomeLock.of(ModTags.Biomes.THE_HALLOW), waystonesLock)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.MUSHROOM_PYLON.getId(), 1, ITradeLock.and(BiomeLock.of(ModBiomes.GLOWING_MUSHROOM), waystonesLock)));
    }

    protected ITradeLock withModLoaded(String modid) {
        return new ConditionsLock(new ModLoadedCondition(modid));
    }

    @Override
    protected PackOutput.PathProvider pathProvider() {
        return npcShopPathProvider;
    }

    public static class Builder {
        private final List<ITrade> trades;

        public Builder() {
            this.trades = new ArrayList<>();
        }

        /**
         * 钱换物
         */
        public Builder add(ItemStack it) {
            trades.add(new MoneyTradeItem(it, null));
            return this;
        }

        /**
         * 钱换物
         */
        public Builder add(ItemLike it, int amount) {
            return add(new ItemStack(it, amount));
        }

        /**
         * 钱换物
         */
        public Builder add(ItemLike it) {
            return add(new ItemStack(it));
        }

        /**
         * 通用交易表
         */
        public Builder add(ITrade trade) {
            trades.add(trade);
            return this;
        }

        /**
         * 护士独有的回满血
         *
         * @param trade instance
         */
        public Builder add(MoneyTradeHealthFull trade) {
            trades.add(trade);
            return this;
        }

        public NPCTradeManager build() {
            return new NPCTradeManager(trades);
        }
    }
}
