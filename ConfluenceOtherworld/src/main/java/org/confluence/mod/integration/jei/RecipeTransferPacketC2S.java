package org.confluence.mod.integration.jei;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.confluence.lib.common.menu.EitherAmountContainerMenu4x;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.lib.common.recipe.EitherAmountRecipe4x;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.confluence.mod.network.IPacket;
import org.confluence.mod.network.c2s.IPacketC2S;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record RecipeTransferPacketC2S(ResourceLocation recipeId, boolean maxTransfer, boolean isFake) implements IPacketC2S {
    public static final Type<RecipeTransferPacketC2S> TYPE = IPacket.createType("recipe_transfer");
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeTransferPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, RecipeTransferPacketC2S::recipeId,
            ByteBufCodecs.BOOL, RecipeTransferPacketC2S::maxTransfer,
            ByteBufCodecs.BOOL, RecipeTransferPacketC2S::isFake,
            RecipeTransferPacketC2S::new
    );

    @Override
    public Type<RecipeTransferPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        Optional<RecipeHolder<?>> optional = player.server.getRecipeManager().byKey(recipeId);
        if (optional.isEmpty()) return;
        Recipe<?> recipe = optional.get().value();
        @Nullable EitherAmountRecipe4x<?> recipe4x = getRecipe4x(EitherAmountRecipe4x.class, recipe, either -> new FakeAmountRecipe4x(player.registryAccess(), recipe, either));
        if (recipe4x != null && player.containerMenu instanceof EitherAmountContainerMenu4x<?, ?, ?, ?> menu4x) {
            menu4x.clearContainerNoUpdate(player);
            List<Slot> slots = menu4x.slots.stream().filter(slot -> !slot.isFake() && slot.hasItem()).toList();
            recipe4x.either.ifLeft(pattern -> {
                NonNullList<Ingredient> ingredients = pattern.ingredients();
                int width = pattern.width();
                int height = pattern.height();
                boolean symmetrical = pattern.symmetrical;
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        Ingredient ingredient = ingredients.get(symmetrical ? width - j - 1 + i * width : j + i * width);
                        for (Slot slot : slots) {
                            if (ingredient.test(slot.getItem())) {
                                ItemStack itemStack = player.getInventory().removeItem(slot.getSlotIndex(), AmountIngredient.getAmount(ingredient));
                                int index = j + i * width;
                                for (int k = 3; k > 0; k--) {
                                    if (index >= width * k) {
                                        index += k * (4 - width);
                                        break;
                                    }
                                }
                                menu4x.getContainer().setItem(index, itemStack);
                                break;
                            }
                        }
                    }
                }
            }).ifRight(ingredients -> {
                int index = 0;
                for (Ingredient ingredient : ingredients) {
                    for (Slot slot : slots) {
                        if (ingredient.test(slot.getItem())) {
                            ItemStack itemStack = player.getInventory().removeItem(slot.getSlotIndex(), AmountIngredient.getAmount(ingredient));
                            menu4x.getContainer().setItem(index++, itemStack);
                        }
                    }
                }
            });
            menu4x.broadcastChanges();
        }
    }

    @SuppressWarnings("unchecked")
    public static <R extends EitherAmountRecipe4x<?>> @Nullable R getRecipe4x(Class<R> clazz, Recipe<?> recipe, Function<Either<ShapedRecipePattern, NonNullList<Ingredient>>, R> factory) {
        R recipe4x = null;
        if (clazz.isInstance(recipe)) {
            recipe4x = (R) recipe;
        } else {
            Class<?> clazz1 = recipe.getClass();
            if (clazz1 == ShapedRecipe.class) {
                recipe4x = factory.apply(Either.left(((ShapedRecipe) recipe).pattern));
            } else if (clazz1 == ShapelessRecipe.class) {
                recipe4x = factory.apply(Either.right(recipe.getIngredients()));
            }
        }
        return recipe4x;
    }

    public static class FakeAmountRecipe4x extends EitherAmountRecipe4x<MenuRecipeInput> {
        private final Recipe<?> recipe;

        public FakeAmountRecipe4x(RegistryAccess registryAccess, Recipe<?> recipe, Either<ShapedRecipePattern, NonNullList<Ingredient>> either) {
            super(recipe.getResultItem(registryAccess), either);
            this.recipe = recipe;
        }

        @Override
        public String getGroup() {
            return recipe.getGroup();
        }

        @Override
        public ItemStack getToastSymbol() {
            return recipe.getToastSymbol();
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return recipe.getSerializer();
        }

        @Override
        public RecipeType<?> getType() {
            return recipe.getType();
        }
    }
}
