package org.confluence.mod.common.recipe.special;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PortRegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.NbtComponent;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.terraentity.init.entity.TEAnimals;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BoomBunnyRecipe extends ShapelessRecipe {
    private static BoomBunnyRecipe INSTANCE;

    private BoomBunnyRecipe() {
        super("", CraftingBookCategory.MISC, ModItems.ENTITY_DISPLAY.toStack(), NonNullList.of(Ingredient.EMPTY,
                Ingredient.of(ModItems.ENTITY_DISPLAY),
                Ingredient.of(ConsumableItems.DYNAMITE)
        ));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.BOOM_BUNNY_SERIALIZER.get();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (super.matches(input, level)) {
            for (int i = 0; i < input.ingredientCount(); i++) {
                ItemStack itemStack = input.getItem(i);
                if (itemStack.isEmpty() || !itemStack.is(ModItems.ENTITY_DISPLAY)) continue;
                CompoundTag tag = LibUtils.getItemStackNbtIfPresent(itemStack);
                if (tag == null) continue;
                String id = tag.getString(Entity.ID_TAG);
                if ("terra_entity:bunny".equals(id) || "minecraft:rabbit".equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack assemble = super.assemble(input, registries);
        for (int i = 0; i < input.ingredientCount(); i++) {
            ItemStack itemStack = input.getItem(i);
            if (itemStack.isEmpty() || !itemStack.is(ModItems.ENTITY_DISPLAY)) continue;
            CompoundTag tag = LibUtils.getItemStackNbtIfPresent(itemStack);
            if (tag == null) continue;
            tag = tag.copy();
            tag.putString(Entity.ID_TAG, BuiltInRegistries.ENTITY_TYPE.getKey(TEAnimals.EXPLOSIVE_BUNNY.get()).toString());
            tag.putUUID(Entity.UUID_TAG, Mth.createInsecureUUID());
            assemble.set(ConfluenceMagicLib.NBT, new NbtComponent(tag));
            assemble.set(DataComponents.CUSTOM_NAME, itemStack.get(DataComponents.CUSTOM_NAME));
            assemble.remove(ConfluenceMagicLib.MOD_RARITY);
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
        public static final MapCodec<BoomBunnyRecipe> CODEC = MapCodec.unit(BoomBunnyRecipe::getInstance);
        public static final StreamCodec<PortRegistryFriendlyByteBuf, BoomBunnyRecipe> STREAM_CODEC = LibStreamCodecUtils.unit(BoomBunnyRecipe::getInstance);

        @Override
        public MapCodec<BoomBunnyRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<PortRegistryFriendlyByteBuf, BoomBunnyRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
