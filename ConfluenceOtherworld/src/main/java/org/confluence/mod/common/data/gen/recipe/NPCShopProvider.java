package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.common.entity.npc.NPCTrades;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.init.item.TEWhipItems;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 生成单个NPC单个配方
 *
 * @see NPCTrades.Trade
 */
public class NPCShopProvider extends AbstractRecipeProvider {
    private final PackOutput.PathProvider npcShopPathProvider;

    public NPCShopProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
        this.npcShopPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "npc_shop");
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        recipe(NPCTrades.CODEC, pathProvider().json(ModEntities.GUIDE.getId())).addRecipe(new Builder()
                .add(TGItems.MUSKET_BULLET.get(), 100, 80)
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
    }

    @Override
    protected PackOutput.PathProvider pathProvider() {
        return npcShopPathProvider;
    }

    public static class Builder {
        private final List<NPCTrades.Trade> trades;

        public Builder() {
            this.trades = new ArrayList<>();
        }

        public Builder add(ItemStack it, int cost) {
            trades.add(new NPCTrades.Trade(it, cost));
            return this;
        }

        public Builder add(ItemLike it, int amount, int cost) {
            return add(new ItemStack(it, amount), cost);
        }

        public Builder add(ItemLike it, int cost) {
            return add(new ItemStack(it), cost);
        }

        public NPCTrades build() {
            return new NPCTrades(trades);
        }
    }
}