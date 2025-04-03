package org.confluence.mod.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.terra_curio.common.recipe.AbstractAmountRecipe;
import org.confluence.terra_curio.common.recipe.AmountIngredient;

public class CookingPotRecipe extends AbstractAmountRecipe {
    private final Ingredient container;
    private final TagKey<Block> heatSource;
    private final int cookingTime;

    public CookingPotRecipe(ItemStack result, NonNullList<Ingredient> ingredients, Ingredient container, TagKey<Block> heatSource, int cookingTime) {
        super(result, ingredients);
        this.container = container;
        this.heatSource = heatSource;
        this.cookingTime = cookingTime;
    }

    public Ingredient getContainer() {
        return container;
    }

    public TagKey<Block> getHeatSource() {
        return heatSource;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    @Override
    protected int maxIngredientSize() {
        return 4;
    }

    @Override
    public String getGroup() {
        return "cooking_pot";
    }

    @Override
    public ItemStack getToastSymbol() {
        return Items.CAULDRON.getDefaultInstance();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.COOKING_POT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.COOKING_POT_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CookingPotRecipe> {
        public static final MapCodec<CookingPotRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                INGREDIENTS_CODEC.forGetter(recipe -> recipe.ingredients),
                Ingredient.CODEC.lenientOptionalFieldOf("container", Ingredient.EMPTY).forGetter(recipe -> recipe.container),
                TagKey.codec(Registries.BLOCK).lenientOptionalFieldOf("heat_source", BlockTags.CAMPFIRES).forGetter(recipe -> recipe.heatSource),
                Codec.INT.lenientOptionalFieldOf("cookingtime", 200).forGetter(recipe -> recipe.cookingTime)
        ).apply(instance, CookingPotRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<CookingPotRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static CookingPotRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(size, AmountIngredient.EMPTY);
            nonnulllist.replaceAll(ignore -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            Ingredient container = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            TagKey<Block> heatSource = TagKey.create(Registries.BLOCK, ResourceLocation.STREAM_CODEC.decode(buffer));
            return new CookingPotRecipe(itemstack, nonnulllist, container, heatSource, buffer.readVarInt());
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, CookingPotRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.container);
            ResourceLocation.STREAM_CODEC.encode(buffer, recipe.heatSource.location());
            buffer.writeVarInt(recipe.cookingTime);
        }
    }
}
