package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade.MoneyTradeHealthFull;
import org.confluence.mod.integration.terra_entity.npc_trade.MoneyTradeItem;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.terra_curio.common.init.TCItems;
//import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.data.gen.recipe.TENPCShopProvider;
import org.confluence.terraentity.entity.npc.trade.NPCTradeManager;
import org.confluence.terraentity.init.TEItems;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.init.item.TEWhipItems;
import org.confluence.terraentity.registries.npc_trade.ITrade;
import org.confluence.terraentity.registries.npc_trade.variant.ItemTradeItem;
import org.confluence.terraentity.registries.npc_trade.variant.ItemTradeLootTable;
import org.confluence.terraentity.registries.npc_trade.variant.TradeTask;
import org.confluence.terraentity.registries.npc_trade_task.variant.DynamicAnglerTradeTask;
import org.confluence.terraentity.registries.npc_trade_task.variant.ProgressTradeTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 生成单个NPC单个配方
 * @see org.confluence.terraentity.registries.npc_trade.ITrade 生成配方
 * @see NPCTradeManager 读取配方
 */
public class NPCShopProvider extends AbstractRecipeProvider {
    private final PackOutput.PathProvider npcShopPathProvider;

    public NPCShopProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
        this.npcShopPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "npc_shop");
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        // 命名空间替换成confluence，本体不能覆盖子模块的数据包
        shop(TENpcEntities.GUIDE.getId()).addRecipe(new Builder()
                //TODO 枪！
   //旅商的       .add(AccessoryItems.PAINT_SPRAYER.get(), 100000)
                .add(TCItems.PORTABLE_CEMENT_MIXER.get(), 100000)
                .add(TCItems.EXTENDO_GRIP.get(), 100000)
                .add(TCItems.BRICK_LAYER.get(), 100000)
                .add(TCItems.STOPWATCH.get(), 50000)
                .add(TCItems.LIFE_FORM_ANALYZER.get(), 50000)
                .add(TCItems.DPS_METER.get(), 50000)
                .add(SwordItems.KATANA.get(), 100000)
                .add(FoodItems.PAD_THAI.get(), 750)
  //动物学家的              .add(TEWhipItems.LEATHER_WHIP.get(), 100000)
                .build());

        shop(TENpcEntities.DEMOLITIONIST.getId()).addRecipe(new Builder()
                .add(ConsumableItems.GRENADE.get(), 75)
                .add(ConsumableItems.BOMB.get(), 300)
                .add(ConsumableItems.DYNAMITE.get(), 2000)
                .build());

        shop(TENpcEntities.MERCHANT.getId()).addRecipe(new Builder()
                .add(Blocks.TORCH.asItem(), 10, 500)
                .add(Items.ARROW.asItem(), 10, 50)
                .add(ModBlocks.ROPE.get(), 10, 50)
                .add(ConsumableItems.SHURIKEN.get(), 10, 150)
                .add(PickaxeItems.COPPER_PICKAXE.get(), 500)
                .add(AxeItems.COPPER_AXE.get(), 400)
                .add(PotionItems.LESSER_HEALING_POTION.get(), 300)
                .build());

        shop(TENpcEntities.GOBLIN_TINKERER.getId()).addRecipe(new Builder()
                .add(HookItems.GRAPPLING_HOOK.asItem(), 10000)
                .add(TCItems.ROCKET_BOOTS.get(), 50000)
                .add(TCItems.TOOLBELT.get(), 100000)
                .add(TCItems.WORKSHOP.get(), 100000)
                .build());

        shop(TENpcEntities.NURSE.getId()).addRecipe(new Builder()
                .add(MoneyTradeHealthFull.create())
                .build());

        shop(TENpcEntities.ARMS_DEALER.getId()).addRecipe(new Builder()
                .add(TGItems.MUSKET_BULLET.get(),100, 700)
                .build());

        shop(TENpcEntities.DRYAD.getId()).addRecipe(new Builder()
                .add(ConsumableItems.PURIFICATION_POWDER.get(), 75)
                .add(NatureBlocks.YELLOW_WILLOW_SAPLING.asItem(), 10000)
                .add(Blocks.OAK_SAPLING.asItem(), 50)
                .add(Items.PUMPKIN_SEEDS.asItem(), 250)
                .build());

        shop(TENpcEntities.DYE_TRADER.getId()).addRecipe(new Builder()
                .add(VanityArmorItems.SILVER_DYE.get(), 10000)
                .add(VanityArmorItems.BROWN_DYE.get(), 10000)
                .add(VanityArmorItems.TEAM_DYE.get(), 10000)
                .build());


        shop(TENpcEntities.PAINTER.getId()).addRecipe(new Builder()
                .add(PaintItems.PAINTBRUSH.get(),10000)
                .add(PaintItems.PAINT_ROLLER.get(),10000)
                .add(PaintItems.PAINT_SCRAPER.get(),10000)
                .add(PaintItems.RED_PAINT.get(),25)
                .add(PaintItems.DEEP_RED_PAINT.get(),25)
                .add(PaintItems.ORANGE_PAINT.get(),25)
                .add(PaintItems.DEEP_ORANGE_PAINT.get(),25)
                .add(PaintItems.YELLOW_PAINT.get(),25)
                .add(PaintItems.DEEP_YELLOW_PAINT.get(),25)
                .add(PaintItems.LIME_PAINT.get(),25)
                .add(PaintItems.DEEP_LIME_PAINT.get(),25)
                .add(PaintItems.GREEN_PAINT.get(),25)
                .add(PaintItems.DEEP_GREEN_PAINT.get(),25)
                .add(PaintItems.TEAL_PAINT.get(),25)
                .add(PaintItems.DEEP_TEAL_PAINT.get(),25)
                .add(PaintItems.CYAN_PAINT.get(),25)
                .add(PaintItems.DEEP_CYAN_PAINT.get(),25)
                .add(PaintItems.SKY_BLUE_PAINT.get(),25)
                .add(PaintItems.DEEP_SKY_BLUE_PAINT.get(),25)
                .add(PaintItems.BLUE_PAINT.get(),25)
                .add(PaintItems.DEEP_BLUE_PAINT.get(),25)
                .add(PaintItems.PURPLE_PAINT.get(),25)
                .add(PaintItems.DEEP_PURPLE_PAINT.get(),25)
                .add(PaintItems.VIOLET_PAINT.get(),25)
                .add(PaintItems.DEEP_VIOLET_PAINT.get(),25)
                .add(PaintItems.PINK_PAINT.get(),25)
                .add(PaintItems.DEEP_PINK_PAINT.get(),25)
                .add(PaintItems.BLACK_PAINT.get(),25)
                .add(PaintItems.GRAY_PAINT.get(),25)
                .add(PaintItems.WHITE_PAINT.get(),25)
                .add(PaintItems.BROWN_PAINT.get(),25)
                .build());

        shop(TENpcEntities.ANGLER.getId()).addRecipe(new Builder()
                // 渔夫任务
                .add(TradeTask.create(
                        DynamicAnglerTradeTask.builder(
                                        ItemTradeLootTable.builder()
                                                .setCost(Items.COD, 1)
                                                .setLootTable(TerraEntity.fromSpaceAndPath("minecraft", "entities/zombie"))
                                                .setSprite(TerraEntity.space("random_gift"))
                                                .build(),
                                        List.of(Items.DIRT.getDefaultInstance(), Items.ICE.getDefaultInstance(), Items.EMERALD.getDefaultInstance())
                                )
                                .addResult(1, List.of(Items.DIAMOND.getDefaultInstance(), Items.EMERALD.getDefaultInstance()))
                                .addResult(3, List.of(Items.ICE.getDefaultInstance(), Items.EMERALD.getDefaultInstance()))
//                                .setTitle("title.terra_entity.npc_trade.task.fishman")
                                .build()
                ))
                .build());
    }

    protected Appender<NPCTradeManager> shop(ResourceLocation id) {
        // 预处理命名空间
        return recipe(NPCTradeManager.CODEC, pathProvider().json(Confluence.asResource(id.getPath())));
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
        public Builder add(ItemStack it, int cost) {
            trades.add(new MoneyTradeItem(it, cost, null));
            return this;
        }
        /**
         * 钱换物
         */
        public Builder add(ItemLike it, int amount, int cost) {
            return add(new ItemStack(it, amount), cost);
        }
        /**
         * 钱换物
         */
        public Builder add(ItemLike it, int cost) {
            return add(new ItemStack(it), cost);
        }

        /**
         * 通用交易表
         */
        public Builder add(ITrade trade){
            trades.add(trade);
            return this;
        }

        /**
         * 护士独有的回满血
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