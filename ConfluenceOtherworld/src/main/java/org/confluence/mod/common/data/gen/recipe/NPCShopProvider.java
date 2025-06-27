package org.confluence.mod.common.data.gen.recipe;

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
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.Keys;
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
import org.confluence.mod.integration.terra_entity.npc_trade_lock.ConditionsLock;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.PositionLock;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.SecretFlagLock;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.npc.trade.NPCTradeManager;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.init.item.TEWhipItems;
import org.confluence.terraentity.registries.npc_trade.ITrade;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.variant.ItemTradeLootTable;
import org.confluence.terraentity.registries.npc_trade.variant.TradeTask;
import org.confluence.terraentity.registries.npc_trade_lock.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.variant.BiomeLock;
import org.confluence.terraentity.registries.npc_trade_task.variant.DynamicAnglerTradeTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 生成单个NPC单个配方
 *
 * @see org.confluence.terraentity.registries.npc_trade.ITrade 生成配方
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
        TradeProperties hardmodeLock = TradeProperties.builder().setLock(new SecretFlagLock(IWorldOptions.HARDMODE, false)).build();
        // 女仆商店
        shop(Keys.MAID_SHOP).addRecipe(withDefaultPylon()
                .add(TCItems.PORTABLE_CEMENT_MIXER)
                .add(TCItems.EXTENDO_GRIP)
                .add(TCItems.BRICK_LAYER)
                .add(TCItems.STOPWATCH)
                .add(TCItems.LIFE_FORM_ANALYZER)
                .add(TCItems.DPS_METER)
                .add(TCItems.DPS_METER)
                .add(AccessoryItems.PAINT_SPRAYER)
                .add(TEWhipItems.LEATHER_WHIP)
                .add(SwordItems.KATANA)
                .add(FoodItems.PAD_THAI)
                .add(SellTrade.INSTANCE)
                .build());

        shop(TENpcEntities.GUIDE.getId()).addRecipe(new Builder()
                //旅商的       .add(AccessoryItems.PAINT_SPRAYER)
                //.add(TCItems.PORTABLE_CEMENT_MIXER)
                //.add(TCItems.EXTENDO_GRIP)
                //.add(TCItems.BRICK_LAYER)
                //.add(TCItems.STOPWATCH)
                // .add(TCItems.LIFE_FORM_ANALYZER)
                // .add(TCItems.DPS_METER)
                // .add(SwordItems.KATANA)
                // .add(FoodItems.PAD_THAI)
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
                        .setProperties(hardmodeLock)
                        .build())
                .add(new MoneyTradeItem.Builder()
                        .setResult(TGItems.SILVER_BULLET.toStack(100))
                        .setProperties(hardmodeLock)
                        .build())
                .add(TGItems.FLINTLOCK_PISTOL)
                .add(TGItems.MINISHARK)
                .add(SellTrade.INSTANCE)
                .build());

        shop(TENpcEntities.DRYAD.getId()).addRecipe(withDefaultPylon()
                .add(ConsumableItems.PURIFICATION_POWDER)
                .add(NatureBlocks.YELLOW_WILLOW_SAPLING)
                .add(Blocks.OAK_SAPLING)
                .add(Items.PUMPKIN_SEEDS)
                .add(ModItems.GRASS_SEED)
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

        shop(TENpcEntities.OLD_MAN.getId()).addRecipe(new Builder()
                .build());
    }


    protected Appender<NPCTradeManager> shop(ResourceLocation id) {
        // 预处理命名空间，命名空间替换成confluence，本体不能覆盖子模块的数据包
        return recipe(NPCTradeManager.CODEC, pathProvider().json(Confluence.asResource(id.getPath())));
    }

    protected Builder withDefaultPylon() { // todo 心情
        return new Builder()
                .add(new DeferredMoneyTradeItem(WaystonesHelper.FOREST_PYLON.getId(), 1, withModLoaded(new BiomeLock(Optional.empty(), Optional.of(List.of(Tags.Biomes.IS_FOREST, Tags.Biomes.IS_PLAINS))), WaystonesHelper.MODID)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.SNOW_PYLON.getId(), 1, withModLoaded(new BiomeLock(Optional.empty(), Optional.of(List.of(Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_ICY))), WaystonesHelper.MODID)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.DESERT_PYLON.getId(), 1, withModLoaded(new BiomeLock(Optional.empty(), Optional.of(List.of(Tags.Biomes.IS_DESERT, Tags.Biomes.IS_BADLANDS))), WaystonesHelper.MODID)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.CAVERN_PYLON.getId(), 1, withModLoaded(new PositionLock(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.atMost(40), MinMaxBounds.Ints.ANY), WaystonesHelper.MODID)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.OCEAN_PYLON.getId(), 1, withModLoaded(new BiomeLock(Optional.empty(), Optional.of(Collections.singletonList(Tags.Biomes.IS_OCEAN))), WaystonesHelper.MODID)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.JUNGLE_PYLON.getId(), 1, withModLoaded(new BiomeLock(Optional.empty(), Optional.of(Collections.singletonList(Tags.Biomes.IS_JUNGLE))), WaystonesHelper.MODID)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.HALLOW_PYLON.getId(), 1, withModLoaded(new BiomeLock(Optional.empty(), Optional.of(Collections.singletonList(ModTags.Biomes.THE_HALLOW))), WaystonesHelper.MODID)))
                .add(new DeferredMoneyTradeItem(WaystonesHelper.MUSHROOM_PYLON.getId(), 1, withModLoaded(new BiomeLock(Optional.of(Collections.singletonList(ModBiomes.GLOWING_MUSHROOM)), Optional.empty()), WaystonesHelper.MODID)));
    }

    protected ITradeLock withModLoaded(ITradeLock subLock, String modid) {
        return new ConditionsLock(subLock, new ModLoadedCondition(modid));
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