package org.confluence.mod.common.data.gen.recipe;

import com.google.gson.*;
import com.mojang.serialization.JavaOps;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.common.recipe.HeavyWorkBenchRecipe;
import org.confluence.terra_curio.common.data.gen.AbstractRecipeProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeavyWorkBenchRecipeProvider extends AbstractRecipeProvider {
    public HeavyWorkBenchRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void run() {
        Shape(SwordItems.DEVELOPER_SWORD)
                .pattern("###")
                .pattern(" a ")
                .pattern(" a ")
                .set('#', Items.GRASS_BLOCK)
                .set('a', Items.STICK)
                .build();

    }

    @Override
    public String getName() {
        return "";
    }

    protected void genRecipe(ItemStack result, Map<Character, Ingredient> map, List<String> pattern,String suffix){
        var shape = ShapedRecipePattern.of(
                Map.of('#', Ingredient.of(Items.GRASS_BLOCK.asItem()),
                        'a', Ingredient.of(Items.STICK.asItem())
                ),
                pattern);
        var a = HeavyWorkBenchRecipe.CODEC.encodeStart(JavaOps.INSTANCE,HeavyWorkBenchRecipe.of(shape, result));
        JsonObject obj = JsonParser.parseString(new Gson().toJson(a)).getAsJsonObject().get("value").getAsJsonObject();
        addJson(obj, result, suffix);
    }

    protected ShapeBuilder Shape(ItemStack result){
        return new ShapeBuilder(result);
    }
    protected ShapeBuilder Shape(DeferredItem<? extends Item> result,int count){
        return new ShapeBuilder(result.toStack(count));
    }
    protected ShapeBuilder Shape(DeferredItem<? extends Item> result){
        return Shape(result,1);
    }
    protected final class ShapeBuilder {
        ItemStack result;
        Map<Character, Ingredient> map = new HashMap<>();
        List<String> pattern = new ArrayList<>();

        public ShapeBuilder(ItemStack result) {
            this.result = result;
        }

        public ShapeBuilder set(char c, ItemLike item) {
            map.put(c, Ingredient.of(item));
            return this;
        }

        public ShapeBuilder pattern(String pattern) {
            this.pattern.add(pattern);
            return this;
        }

        public void build(String suffix) {
            genRecipe(result, map, pattern, suffix);
        }
        public void build() {
            genRecipe(result, map, pattern, "");
        }
    }
}
