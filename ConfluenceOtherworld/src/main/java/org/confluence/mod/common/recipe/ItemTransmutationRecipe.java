package org.confluence.mod.common.recipe;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import PortLib.extensions.net.minecraft.world.item.crafting.Ingredient.PortIngredientExtension;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.recipe.SimpleRecipeSerializer;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.item.ToolItems;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.world.item.crafting.PortRecipe;
import org.mesdag.portlib.wrapper.world.item.crafting.PortSingleRecipeInput;

import java.util.List;

public class ItemTransmutationRecipe implements PortRecipe<PortSingleRecipeInput> {
    private final Ingredient source;
    private final List<ItemStack> target;
    private final int shrink;
    private final GamePhase gamePhase;
    private ResourceLocation id;

    public ItemTransmutationRecipe(Ingredient source, List<ItemStack> target, int shrink, GamePhase gamePhase) {
        this.source = source;
        this.target = target;
        this.shrink = shrink;
        this.gamePhase = gamePhase;
    }

    @Override
    public void setId(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public boolean isValid() {
        return shrink > 0 && !target.isEmpty();
    }

    @Override
    public boolean matches(PortSingleRecipeInput input, Level level) {
        return input.item().getCount() >= shrink && source.test(input.item()) && KillBoard.INSTANCE.getGamePhase().isAtLeast(gamePhase);
    }

    @Override
    public ItemStack assemble(PortSingleRecipeInput input, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ITEM_TRANSMUTATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ITEM_TRANSMUTATION_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.withSize(1, source);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public String getGroup() {
        return "item_transmutation";
    }

    @Override
    public ItemStack getToastSymbol() {
        return ToolItems.BOTTOMLESS_SHIMMER_BUCKET.toStack();
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(PortSingleRecipeInput input) {
        return NonNullList.withSize(1, ItemStack.EMPTY);
    }

    public Ingredient source() {
        return source;
    }

    public List<ItemStack> target() {
        return target;
    }

    public int shrink() {
        return shrink;
    }

    public GamePhase gamePhase() {
        return gamePhase;
    }

    public static class Serializer extends SimpleRecipeSerializer<ItemTransmutationRecipe> {
        @Override
        protected MapCodec<ItemTransmutationRecipe> getCodec() {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    PortIngredientExtension.codec().fieldOf("source").forGetter(ItemTransmutationRecipe::source),
                    PortCodecExtension.lenientOptionalFieldOf(PortItemStackExtension.codec().listOf(), "target", List.of()).forGetter(ItemTransmutationRecipe::target),
                    PortCodecExtension.lenientOptionalFieldOf(ExtraCodecs.POSITIVE_INT, "shrink", 1).forGetter(ItemTransmutationRecipe::shrink),
                    PortCodecExtension.lenientOptionalFieldOf(GamePhase.CODEC, "game_phase", GamePhase.BEFORE_SKELETRON).forGetter(ItemTransmutationRecipe::gamePhase)
            ).apply(instance, ItemTransmutationRecipe::new));
        }

        @Override
        protected PortStreamCodec<org.mesdag.portlib.network.PortRegistryFriendlyByteBuf, ItemTransmutationRecipe> getStreamCodec() {
            return PortStreamCodec.composite(
                    PortIngredientExtension.contentsStreamCodec(), ItemTransmutationRecipe::source,
                    PortItemStackExtension.listStreamCodec(), ItemTransmutationRecipe::target,
                    PortByteBufCodecs.VAR_INT, ItemTransmutationRecipe::shrink,
                    GamePhase.STREAM_CODEC, ItemTransmutationRecipe::gamePhase,
                    ItemTransmutationRecipe::new
            );
        }
    }
}
