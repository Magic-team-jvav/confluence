package org.confluence.mod.integration.jei;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.lib.common.menu.EitherAmountContainerMenu4x;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.lib.common.recipe.EitherAmountRecipe4x;
import org.confluence.mod.Confluence;

import java.util.List;

public record RecipeTransferPacketC2S(ResourceLocation recipeId, boolean maxTransfer) implements CustomPacketPayload {
    public static final Type<RecipeTransferPacketC2S> TYPE = new Type<>(Confluence.asResource("recipe_transfer"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeTransferPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, RecipeTransferPacketC2S::recipeId,
            ByteBufCodecs.BOOL, RecipeTransferPacketC2S::maxTransfer,
            RecipeTransferPacketC2S::new
    );

    @Override
    public Type<RecipeTransferPacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                player.server.getRecipeManager().byKey(recipeId).ifPresent(recipeHolder -> {
                    if (player.containerMenu instanceof EitherAmountContainerMenu4x<?, ?, ?, ?> menu4x && recipeHolder.value() instanceof EitherAmountRecipe4x<?> recipe4x) {
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
                        });
                        recipe4x.either.ifRight(ingredients -> {
                            // todo
                        });
                        menu4x.broadcastChanges();
                    }
                });
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }
}
