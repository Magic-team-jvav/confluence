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
import org.confluence.terraentity.entity.npc.NPCTrades;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.init.item.TEWhipItems;
import org.confluence.terraentity.registries.npc_trade.ITrade;
import org.confluence.terraentity.registries.npc_trade.variant.ItemTradeItem;
import org.confluence.terraentity.registries.npc_trade.variant.TradeTask;
import org.confluence.terraentity.registries.npc_trade_task.variant.ProgressTradeTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 生成单个NPC单个配方
 * @see org.confluence.terraentity.registries.npc_trade.ITrade 生成配方
 * @see NPCTrades 读取配方
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
        recipe(TENpcEntities.GUIDE.getId()).addRecipe(new Builder()
                //TODO 枪！
//                .add(TGItems.MUSKET_BULLET.get(), 100, 80)
                .add(ConsumableItems.GRENADE.get(), 75)
                .add(ConsumableItems.BOMB.get(), 300)
                .add(ConsumableItems.DYNAMITE.get(), 2000)
                .add(ConsumableItems.PURIFICATION_POWDER.get(), 75)
                .add(NatureBlocks.YELLOW_WILLOW_SAPLING.asItem(), 10000)
                .add(Blocks.OAK_SAPLING.asItem(), 50)
                .add(Blocks.TORCH.asItem(), 10, 500)
                .add(Items.ARROW.asItem(), 10, 50)
                .add(ModBlocks.ROPE.get(), 10, 50)
                .add(ConsumableItems.SHURIKEN.get(), 10, 150)
                .add(HookItems.GRAPPLING_HOOK.asItem(), 10000)
                .add(TCItems.ROCKET_BOOTS.get(), 50000)
                .add(TCItems.TOOLBELT.get(), 100000)
                .add(TCItems.WORKSHOP.get(), 100000)
                .add(PickaxeItems.COPPER_PICKAXE.get(), 500)
                .add(AxeItems.COPPER_AXE.get(), 400)
                .add(PotionItems.LESSER_HEALING_POTION.get(), 300)
                .add(AccessoryItems.PAINT_SPRAYER.get(), 100000)
                .add(TCItems.PORTABLE_CEMENT_MIXER.get(), 100000)
                .add(TCItems.EXTENDO_GRIP.get(), 100000)
                .add(TCItems.BRICK_LAYER.get(), 100000)
                .add(TCItems.STOPWATCH.get(), 50000)
                .add(TCItems.LIFE_FORM_ANALYZER.get(), 50000)
                .add(TCItems.DPS_METER.get(), 50000)
                .add(SwordItems.KATANA.get(), 100000)
                .add(FoodItems.PAD_THAI.get(), 750)
                .add(TEWhipItems.LEATHER_WHIP.get(), 100000)
                .build());

        recipe(TENpcEntities.NURSE.getId()).addRecipe(new Builder()
                .add(MoneyTradeHealthFull.create())
                .build());

        recipe(TENpcEntities.ANGLER.getId()).addRecipe(new Builder()
                // 渔夫任务
                .add(TradeTask.create(new ProgressTradeTask(List.of(
                        ItemTradeItem.of(ConsumableItems.BOMB.toStack(),FoodItems.ARMORED_CAVE_FISH.toStack()),
                        ItemTradeItem.of(ConsumableItems.DYNAMITE.toStack(),FoodItems.COOK_FISH.toStack()),
                        ItemTradeItem.of(ConsumableItems.GRENADE.toStack(),FoodItems.CHAOS_FISH.toStack()),
                        ItemTradeItem.of(ConsumableItems.PURIFICATION_POWDER.toStack(),FoodItems.GOLDFISH.toStack())
                ))))
                // 这里还可以加其他的任务或者交易栏
//                .add(FoodItems.PAD_THAI.get(), 750)
                .build());
    }

    protected Appender<NPCTrades> recipe(ResourceLocation id) {
        // 预处理命名空间
        return recipe(NPCTrades.CODEC, pathProvider().json(Confluence.asResource(id.getPath())));
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
            trades.add(new MoneyTradeItem(it, cost));
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

        public NPCTrades build() {
            return new NPCTrades(trades);
        }
    }
}