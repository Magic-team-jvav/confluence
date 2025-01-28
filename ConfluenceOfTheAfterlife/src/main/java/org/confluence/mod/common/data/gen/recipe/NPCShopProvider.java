package org.confluence.mod.common.data.gen.recipe;

import com.google.gson.JsonElement;
import com.mojang.serialization.JavaOps;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.confluence.mod.common.entity.npc.NPCTrades;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.terra_curio.common.data.gen.AbstractRecipeProvider;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成单个NPC单个配方
 * @see NPCTrades.Trade
 */
public class NPCShopProvider extends AbstractRecipeProvider {

    public NPCShopProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void run() {

        gen(ModEntities.GUIDE)
                .add(MaterialItems.BLACK_INK,10,10)
                .add(MaterialItems.AMBER,20,9)
                .add(MaterialItems.BLOOD_CLOT_POWDER,30,8)
                .add(MaterialItems.FLOATING_WHEAT_HEADS,40,7)
                .build();


    }

    private <T extends Entity>Builder gen(DeferredHolder<EntityType<?>,EntityType<T>> entityType){
        return new Builder(entityType.getId());
    }


    private void genRecipe(NPCTrades trades, ResourceLocation location){
        JsonElement res = parseCodec(NPCTrades.CODEC.encodeStart(JavaOps.INSTANCE,trades));
        addJson(res.getAsJsonObject(),ItemStack.EMPTY,location.toString());
    }

    private class Builder {
        ResourceLocation location;
        private int money;
        private List<NPCTrades.Trade> trades;
        public Builder(ResourceLocation location){
            this.location = location;
            trades = new ArrayList<>();
        }
        public Builder add(ItemStack it,int cost){
            trades.add(new NPCTrades.Trade(it,cost));
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