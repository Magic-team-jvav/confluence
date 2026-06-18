package org.confluence.mod.common.recipe.special;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.NbtComponent;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class BoomBunnyRecipe extends ShapelessRecipe {
    public static final ResourceLocation ID = Confluence.asResource("boom_bunny");
    private static BoomBunnyRecipe INSTANCE;

    private BoomBunnyRecipe() {
        super(ID, "", CraftingBookCategory.MISC, ModItems.ENTITY_DISPLAY.toStack(), NonNullList.of(Ingredient.EMPTY,
                Ingredient.of(ModItems.ENTITY_DISPLAY),
                Ingredient.of(ConsumableItems.DYNAMITE)
        ));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.BOOM_BUNNY_SERIALIZER.get();
    }

    @Override
    public boolean matches(CraftingContainer input, Level level) {
        if (super.matches(input, level)) {
            for (ItemStack stack : input.getItems()) {
                if (stack.isEmpty() || !stack.is(ModItems.ENTITY_DISPLAY)) continue;
                CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
                if (tag == null) continue;
                String id = tag.getString(Entity.ID_TAG);
                if ("confluence:bunny".equals(id) || "minecraft:rabbit".equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registryAccess) {
        ItemStack assemble = super.assemble(input, registryAccess);
        for (ItemStack stack : input.getItems()) {
            if (stack.isEmpty() || !stack.is(ModItems.ENTITY_DISPLAY)) continue;
            CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
            if (tag == null) continue;
            tag = tag.copy();
            tag.putString(Entity.ID_TAG, BuiltInRegistries.ENTITY_TYPE.getKey(TEAnimals.EXPLOSIVE_BUNNY.get()).toString());
            tag.putUUID(Entity.UUID_TAG, Mth.createInsecureUUID());
            assemble.setData(ConfluenceMagicLib.NBT, new NbtComponent(tag));
            assemble.setCustomName(stack.getCustomName());
            assemble.removeData(ConfluenceMagicLib.MOD_RARITY);
            break;
        }
        return assemble;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    public static BoomBunnyRecipe getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BoomBunnyRecipe();
        }
        return INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<BoomBunnyRecipe> {
        @Override
        public BoomBunnyRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            return getInstance();
        }

        @Override
        public @Nullable BoomBunnyRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return getInstance();
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BoomBunnyRecipe recipe) {}
    }
}
