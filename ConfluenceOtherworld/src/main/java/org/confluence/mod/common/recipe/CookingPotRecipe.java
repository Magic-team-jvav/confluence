package org.confluence.mod.common.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

import java.util.Optional;

public class CookingPotRecipe extends AbstractAmountRecipe<CookingPotRecipe.Input> {
    private final Ingredient container;
    private final HeatSourcePredicate heatSource;
    private final int cookingTime;

    public CookingPotRecipe(ItemStack result, NonNullList<Ingredient> ingredients, Ingredient container, HeatSourcePredicate heatSource, int cookingTime) {
        super(result, ingredients);
        this.container = container;
        this.heatSource = heatSource;
        this.cookingTime = cookingTime;
    }

    @Override
    public boolean matches(Input input, Level pLevel) {
        return heatSource.matches(input.heatSource) && container.test(input.container) && super.matches(input, pLevel);
    }

    public Ingredient getContainer() {
        return container;
    }

    public HeatSourcePredicate getHeatSource() {
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
        return FunctionalBlocks.COOKING_POT.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.COOKING_POT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.COOKING_POT_TYPE.get();
    }

    public static class Serializer extends SimpleRecipeSerializer<CookingPotRecipe> {
        @Override
        protected MapCodec<CookingPotRecipe> getCodec() {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                    INGREDIENTS_CODEC.forGetter(recipe -> recipe.ingredients),
                    Ingredient.CODEC.fieldOf("container").forGetter(recipe -> recipe.container),
                    HeatSourcePredicate.CODEC.fieldOf("heat_source").forGetter(recipe -> recipe.heatSource),
                    Codec.INT.fieldOf("cookingtime").forGetter(recipe -> recipe.cookingTime)
            ).apply(instance, CookingPotRecipe::new));
        }

        @Override
        protected StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> getStreamCodec() {
            return new StreamCodec<>() {
                @Override
                public CookingPotRecipe decode(RegistryFriendlyByteBuf buffer) {
                    int size = buffer.readVarInt();
                    NonNullList<Ingredient> nonnulllist = NonNullList.withSize(size, AmountIngredient.EMPTY);
                    nonnulllist.replaceAll(ignore -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                    ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
                    Ingredient container = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
                    HeatSourcePredicate heatSource = HeatSourcePredicate.STREAM_CODEC.decode(buffer);
                    return new CookingPotRecipe(itemstack, nonnulllist, container, heatSource, buffer.readVarInt());
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, CookingPotRecipe recipe) {
                    buffer.writeVarInt(recipe.ingredients.size());
                    for (Ingredient ingredient : recipe.ingredients) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                    }
                    ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.container);
                    HeatSourcePredicate.STREAM_CODEC.encode(buffer, recipe.heatSource);
                    buffer.writeVarInt(recipe.cookingTime);
                }
            };
        }
    }

    public static class Input implements RecipeInput {
        private final ItemStack[] items;
        final ItemStack container;
        final BlockInWorld heatSource;

        public Input(ItemStack[] items, ItemStack container, BlockInWorld heatSource) {
            this.items = items;
            this.container = container;
            this.heatSource = heatSource;
        }

        @Override
        public ItemStack getItem(int index) {
            return items[index];
        }

        @Override
        public int size() {
            return items.length;
        }
    }

    public record HeatSourcePredicate(Optional<Either<TagKey<Block>, HolderSet<Block>>> blocks, Optional<StatePropertiesPredicate> properties, Optional<NbtPredicate> nbt) {
        public static final HeatSourcePredicate EMPTY = new HeatSourcePredicate(Optional.empty(), Optional.empty(), Optional.empty());
        public static final Codec<HeatSourcePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.either(TagKey.codec(Registries.BLOCK), RegistryCodecs.homogeneousList(Registries.BLOCK, true)).optionalFieldOf("blocks").forGetter(HeatSourcePredicate::blocks),
                StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(HeatSourcePredicate::properties),
                NbtPredicate.CODEC.optionalFieldOf("nbt").forGetter(HeatSourcePredicate::nbt)
        ).apply(instance, HeatSourcePredicate::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, HeatSourcePredicate> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ByteBufCodecs.either(LibStreamCodecUtils.tagKey(Registries.BLOCK), ByteBufCodecs.holderSet(Registries.BLOCK))), HeatSourcePredicate::blocks,
                ByteBufCodecs.optional(StatePropertiesPredicate.STREAM_CODEC), HeatSourcePredicate::properties,
                ByteBufCodecs.optional(NbtPredicate.STREAM_CODEC), HeatSourcePredicate::nbt,
                HeatSourcePredicate::new
        );

        public boolean matches(BlockInWorld block) {
            return matchesState(block.getState()) && matchesBlockEntity(block.getLevel(), block.getEntity());
        }

        private boolean matchesState(BlockState state) {
            return (blocks.isEmpty() || blocks.get().map(state::is, state::is)) && (properties.isEmpty() || properties.get().matches(state));
        }

        private boolean matchesBlockEntity(LevelReader level, @Nullable BlockEntity blockEntity) {
            return nbt.isEmpty() || (blockEntity != null && nbt.get().matches(blockEntity.saveWithFullMetadata(level.registryAccess())));
        }

        public static Builder builder() {
            return new Builder();
        }

        @SuppressWarnings({"deprecation", "OptionalUsedAsFieldOrParameterType", "unused"})
        public static class Builder {
            private Optional<Either<TagKey<Block>, HolderSet<Block>>> blocks = Optional.empty();
            private Optional<StatePropertiesPredicate> properties = Optional.empty();
            private Optional<NbtPredicate> nbt = Optional.empty();

            private Builder() {}

            public Builder of(TagKey<Block> tag) {
                this.blocks = Optional.of(Either.left(tag));
                return this;
            }

            public Builder of(HolderSet<Block> set) {
                this.blocks = Optional.of(Either.right(set));
                return this;
            }

            public Builder of(Block... set) {
                this.blocks = Optional.of(Either.right(HolderSet.direct(Block::builtInRegistryHolder, set)));
                return this;
            }

            public Builder hasNbt(CompoundTag nbt) {
                this.nbt = Optional.of(new NbtPredicate(nbt));
                return this;
            }

            public Builder setProperties(StatePropertiesPredicate.Builder properties) {
                this.properties = properties.build();
                return this;
            }

            public HeatSourcePredicate build() {
                return new HeatSourcePredicate(blocks, properties, nbt);
            }
        }
    }
}
