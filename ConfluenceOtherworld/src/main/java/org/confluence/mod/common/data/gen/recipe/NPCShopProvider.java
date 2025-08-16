package org.confluence.mod.common.data.gen.recipe;

import com.xiaohunao.terra_moment.common.init.TMMoments;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.Keys;
import org.confluence.mod.common.data.saved.MoonPhase;
import org.confluence.mod.common.init.ModBiomes;
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
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.entity.npc.trade.NPCTradeManager;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.init.item.TEWhipItems;
import org.confluence.terraentity.init.item.TEYoyosItems;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.variant.ItemTradeLootTable;
import org.confluence.terraentity.registries.npc_trade.variant.TradeTask;
import org.confluence.terraentity.registries.npc_trade_list.variant.WeightMapGenerator;
import org.confluence.terraentity.registries.npc_trade_lock.variant.BiomeLock;
import org.confluence.terraentity.registries.npc_trade_lock.variant.MoodLock;
import org.confluence.terraentity.registries.npc_trade_lock.variant.TimeLock;
import org.confluence.terraentity.registries.npc_trade_task.variant.DynamicAnglerTradeTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 生成单个NPC单个配方
 *
 * @see ITrade 生成配方
 * @see NPCTradeManager 读取配方
 */
public class NPCShopProvider extends AbstractRecipeProvider {
    private final PackOutput.PathProvider npcShopPathProvider;

    public NPCShopProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
        this.npcShopPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "npc/shop");
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        TradeProperties hardmode = TradeProperties.builder().setLock(new SecretFlagLock(IWorldOptions.HARDMODE)).build();
        TradeProperties halloweens = TradeProperties.builder().setLock(DateLock.HALLOWEENS).build();
        TradeProperties night = TradeProperties.builder().setLock(new TimeLock(LibDateUtils._19$30, LibDateUtils._04$30, false)).build();
        MomentLock bloodMoonLock = new MomentLock(TMMoments.BLOOD_MOON.getKey());
        SecretFlagLock theCorruptionLock = new SecretFlagLock(IWorldOptions.THE_CORRUPTION);
        SecretFlagLock theCrimsonLock = new SecretFlagLock(IWorldOptions.THE_CRIMSON);
        EnvironmentLock graveyardLock = new EnvironmentLock(EnvironmentLevelAccess.matcher(null, null, true));
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
                .add(FunctionalBlocks.SAFE)
                .add(PickaxeItems.COPPER_PICKAXE)
                .add(AxeItems.COPPER_AXE)
                .add(PotionItems.LESSER_HEALING_POTION)
                .add(PotionItems.LESSER_MANA_POTION)
                .add(SellTrade.INSTANCE)
                .build());

        shop(TENpcEntities.GOBLIN_TINKERER.getId()).addRecipe(withDefaultPylon()
                .add(HookItems.GRAPPLING_HOOK)
                .add(TCItems.ROCKET_BOOTS)
                .add(TCItems.TOOLBELT)
                .add(TCItems.WORKSHOP)
                .add(ConsumableItems.SPIKY_BALL)
                .add(SellTrade.INSTANCE)
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
                .add(TGItems.FLINTLOCK_PISTOL)
                .add(TGItems.MINISHARK)
                .add(SellTrade.INSTANCE)
                .build());

        shop(TENpcEntities.DRYAD.getId()).addRecipe(withDefaultPylon()
                .add(ConsumableItems.PURIFICATION_POWDER)
                .add(NatureBlocks.YELLOW_WILLOW_SAPLING)
                .add(Blocks.OAK_SAPLING)
                .add(Blocks.SUNFLOWER)
                .add(Items.PUMPKIN_SEEDS)
                .add(ModItems.GRASS_SEED)
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
                        .setProperties(TradeProperties.builder().setLock(BiomeLock.of(ModBiomes.GLOWING_MUSHROOM)).build())
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(ModItems.CRIMSON_SEED)
                        .setProperties(TradeProperties.builder().setLock(ITradeLock.and(ITradeLock.or(bloodMoonLock, graveyardLock), theCorruptionLock)).build())
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(ConsumableItems.VILE_POWDER.toStack())
                        .setProperties(TradeProperties.builder().setLock(ITradeLock.and(bloodMoonLock, theCorruptionLock)).build())
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(ConsumableItems.VICIOUS_POWDER.toStack())
                        .setProperties(TradeProperties.builder().setLock(ITradeLock.and(bloodMoonLock, theCrimsonLock)).build())
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(ModItems.CORRUPT_SEED)
                        .setProperties(TradeProperties.builder().setLock(ITradeLock.and(ITradeLock.or(bloodMoonLock, graveyardLock), theCrimsonLock)).build())
                        .build())
                .add(SellTrade.INSTANCE)
                .build());

        shop(TENpcEntities.DYE_TRADER.getId()).addRecipe(withDefaultPylon()
                .add(VanityArmorItems.SILVER_DYE)
                .add(VanityArmorItems.BROWN_DYE)
                .add(VanityArmorItems.TEAM_DYE)
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
                .add(SellTrade.INSTANCE)
                .build());

        NPCTradeManager angler = new Builder()
                .add(TradeTask.create(DynamicAnglerTradeTask.builder(ItemTradeLootTable.builder()
                                .addCost(CrateBlocks.WOODEN_CRATE.toStack()) // 在没有任务鱼机制前，用木匣代替
                                .setLootTable(Confluence.asResource("gameplay/fishing_quests_0"))
                                .setSprite(TerraEntity.space("random_gift"))
                                .build(), List.of(Items.DIRT.getDefaultInstance(), Items.ICE.getDefaultInstance(), Items.EMERALD.getDefaultInstance()))
                        .addResult(10, Collections.singletonList(ArmorItems.ANGLER_HAT.toStack()))
                        .addResult(15, Collections.singletonList(ArmorItems.ANGLER_VEST.toStack()))
                        .addResult(20, Collections.singletonList(ArmorItems.ANGLER_PANTS.toStack()))
                        .addResult(25, Collections.singletonList(ToolItems.BOTTOMLESS_WATER_BUCKET.toStack()))
                        .addResult(30, Collections.singletonList(FishingPoleItems.GOLDEN_FISHING_ROD.toStack()))
//                        .setTitle("title.terra_entity.npc_trade.task.fishman")
                        .build()))
                .build();
        shop(TENpcEntities.ANGLER.getId()).addRecipe(angler);
        shop(TENpcEntities.FEMALE_ANGLER.getId()).addRecipe(angler);

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
                        .addTrade(new MoneyTradeItem.Builder().setResult(FishingPoleItems.SITTING_DUCKS_FISHING_POLE).build(), 1)
                        .build()
                )
        );

        shop(TENpcEntities.CLOTHIER.getId()).addRecipe(withDefaultPylon()
                .add(VanityArmorItems.FAMILIAR_WIG)
                .add(VanityArmorItems.FAMILIAR_SHIRT)
                .add(VanityArmorItems.FAMILIAR_PANTS)
                .add(VanityArmorItems.FAMILIAR_SHOES)
                .add(SellTrade.INSTANCE)
                .build());

        shop(TENpcEntities.PARTY_GIRL.getId()).addRecipe(withDefaultPylon()
                .add(FunctionalBlocks.SILLY_BALLOON_MACHINE)
                .add(ConsumableItems.SMOKE_BOMB)
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
                .add(SellTrade.INSTANCE)
                .build());
    }


    protected Appender<NPCTradeManager> shop(ResourceLocation id) {
        // 预处理命名空间，命名空间替换成confluence，本体不能覆盖子模块的数据包
        return recipe(NPCTradeManager.CODEC, pathProvider().json(Confluence.asResource(id.getPath())));
    }

    protected Builder withDefaultPylon() {
        ITradeLock waystonesLock = withModLoaded(WaystonesHelper.MODID);
        MoodLock goodMoodLock = MoodLock.greater(120);
        return new Builder()
                .add(new DeferredMoneyTradeItem(WaystonesHelper.FOREST_PYLON.getId(), 1, ITradeLock.and(BiomeLock.of(Tags.Biomes.IS_FOREST, Tags.Biomes.IS_PLAINS), goodMoodLock, waystonesLock)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.SNOW_PYLON.getId(), 1, ITradeLock.and(BiomeLock.of(Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_ICY), goodMoodLock, waystonesLock)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.DESERT_PYLON.getId(), 1, ITradeLock.and(BiomeLock.of(Tags.Biomes.IS_DESERT, Tags.Biomes.IS_BADLANDS), goodMoodLock, waystonesLock)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.CAVERN_PYLON.getId(), 1, ITradeLock.and(PositionLock.ofY(MinMaxBounds.Ints.atMost(OverworldUtils.getSurfaceY())), goodMoodLock, waystonesLock)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.OCEAN_PYLON.getId(), 1, ITradeLock.and(BiomeLock.of(Tags.Biomes.IS_OCEAN), goodMoodLock, waystonesLock)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.JUNGLE_PYLON.getId(), 1, ITradeLock.and(BiomeLock.of(Tags.Biomes.IS_JUNGLE), goodMoodLock, waystonesLock)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.HALLOW_PYLON.getId(), 1, ITradeLock.and(BiomeLock.of(ModTags.Biomes.THE_HALLOW), goodMoodLock, waystonesLock)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.MUSHROOM_PYLON.getId(), 1, ITradeLock.and(BiomeLock.of(ModBiomes.GLOWING_MUSHROOM), goodMoodLock, waystonesLock)));
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