package org.confluence.mod.common.data.gen.recipe;

import com.google.gson.JsonElement;
import com.mojang.serialization.JavaOps;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.confluence.lib.common.recipe.AbstractRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.gen.npc_trade.MoneyTradeItem;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.entity.npc.NPCTrades;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.init.item.TEWhipItems;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成单个NPC单个配方
 * @see org.confluence.terraentity.registries.npc_trade.ITrade
 */
public class NPCShopProvider extends AbstractRecipeProvider {

    public NPCShopProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void run() {

//        gen(Confluence.asResource("guild"))
        gen(TENpcEntities.GUIDE)
                .add(new ItemStack(TGItems.MUSKET_BULLET.get(),100),80)
                .add(new ItemStack(ConsumableItems.GRENADE.get(), 1), 75)
                .add(new ItemStack(ConsumableItems.BOMB.get(), 1), 300)
                .add(new ItemStack(ConsumableItems.DYNAMITE.get(), 1), 2000)
                .add(new ItemStack(ConsumableItems.PURIFICATION_POWDER.get(), 1), 75)
                .add(new ItemStack(NatureBlocks.YELLOW_WILLOW_SAPLING.asItem(), 1), 10000)
                .add(new ItemStack(Blocks.OAK_SAPLING.asItem(), 1), 50)
                .add(new ItemStack(Blocks.TORCH.asItem(), 10), 500)
                .add(new ItemStack(Items.ARROW.asItem(), 10), 50)
                .add(new ItemStack(ModBlocks.ROPE.get(), 10), 50)
                .add(new ItemStack(ConsumableItems.SHURIKEN.get(), 10), 150)
                .add(new ItemStack(HookItems.GRAPPLING_HOOK.asItem(), 1), 10000)
                .add(new ItemStack(TCItems.ROCKET_BOOTS.get(), 1), 50000)
                .add(new ItemStack(TCItems.TOOLBELT.get(), 1), 100000)
                .add(new ItemStack(TCItems.WORKSHOP.get(), 1), 100000)
                .add(new ItemStack(PickaxeItems.COPPER_PICKAXE.get(), 1), 500)
                .add(new ItemStack(AxeItems.COPPER_AXE.get(), 1), 400)
                .add(new ItemStack(PotionItems.LESSER_HEALING_POTION.get(), 1), 300)
                .add(new ItemStack(AccessoryItems.PAINT_SPRAYER.get(), 1), 100000)
                .add(new ItemStack(TCItems.PORTABLE_CEMENT_MIXER.get(), 1), 100000)
                .add(new ItemStack(TCItems.EXTENDO_GRIP.get(), 1), 100000)
                .add(new ItemStack(TCItems.BRICK_LAYER.get(), 1), 100000)
                .add(new ItemStack(TCItems.STOPWATCH.get(), 1), 50000)
                .add(new ItemStack(TCItems.LIFE_FORM_ANALYZER.get(), 1), 50000)
                .add(new ItemStack(TCItems.DPS_METER.get(), 1), 50000)
                .add(new ItemStack(SwordItems.KATANA.get(), 1), 100000)
                .add(new ItemStack(FoodItems.PAD_THAI.get(), 1), 750)
                .add(new ItemStack(TEWhipItems.LEATHER_WHIP.get(), 1), 100000)
                .build();
    }

    private <T extends Entity>Builder gen(DeferredHolder<EntityType<?>,EntityType<T>> entityType){
        return gen(entityType.getId());
    }
    private Builder gen(ResourceLocation location){
        return new Builder(location);
    }

    private void genRecipe(NPCTrades trades, ResourceLocation location){
        JsonElement res = parseCodec(NPCTrades.CODEC.encodeStart(JavaOps.INSTANCE,trades));
        addJson(res.getAsJsonObject(),ItemStack.EMPTY,location.toString());
    }

    private class Builder {
        ResourceLocation location;
        private int money;
        private List<MoneyTradeItem> trades;
        public Builder(ResourceLocation location){
            this.location = location;
            trades = new ArrayList<>();
        }
        public Builder add(ItemStack it,int cost){
            trades.add(new MoneyTradeItem(it,cost));
            return this;
        }
        public Builder add(DeferredItem<Item> it, int amount, int cost){
            return add(it.toStack(amount),cost);
        }

        public void build(){
            genRecipe(new NPCTrades(trades),location);
        }

    }


    @Override
    public String getName() {
        return "NPC Shop";
    }

    protected Path getRoot(ResourceLocation loc){
        return this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(loc.getNamespace()).resolve(NPCTrades.KEY);
    }

    protected Path getPath(ResourceLocation loc, String nameSuffix) {
        return getRoot(loc).resolve(loc.getPath()+".json");
    }
}