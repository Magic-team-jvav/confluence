package org.confluence.mod.common.block.palettes;

import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.item.GroupItem;
import org.confluence.mod.common.init.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class DecoBlockSet {
    public static final List<DecoBlockSet> DECO_BLOCK_SETS = new ArrayList<>();

    public final String id;
    public final List<ObjectIntPair<Supplier<? extends ItemLike>>> materials;
    public final boolean stonecutting;

    public final RegistryObject<Block> FULL;
    public final RegistryObject<StairBlock> STAIRS;
    public final RegistryObject<SlabBlock> SLAB;
    public final RegistryObject<WallBlock> WALL;

    DecoBlockSet(Builder builder) {
        this.id = builder.id;
        this.materials = builder.materials;
        this.stonecutting = builder.stonecutting;

        this.FULL = ModBlocks.registerWithItem(id, () -> builder.full.apply(builder.properties.get()), builder.itemProperties);
        this.STAIRS = ModBlocks.registerWithItem(id + "_stairs", () -> builder.stairs.apply(FULL.get().defaultBlockState(), builder.properties.get()), builder.itemProperties);
        this.SLAB = ModBlocks.registerWithItem(id + "_slab", () -> builder.slab.apply(builder.properties.get()), builder.itemProperties);
        this.WALL = ModBlocks.registerWithItem(id + "_wall", () -> builder.wall.apply(builder.properties.get().forceSolidOn()), builder.itemProperties);

        DECO_BLOCK_SETS.add(this);
    }

    public static void acceptBuilding(CreativeModeTab.Output output) {
        for (DecoBlockSet blockSet : DecoBlockSet.DECO_BLOCK_SETS) {
            CreativeModeTab.Output o = GroupItem.belongsTo(blockSet.id, output);

            o.accept(blockSet.FULL.get());
            o.accept(blockSet.STAIRS.get());
            o.accept(blockSet.SLAB.get());
            o.accept(blockSet.WALL.get());
        }
    }

    public static void acceptDecoTags(Function<TagKey<Block>, IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block>> provider) {
        var stairs = provider.apply(BlockTags.STAIRS);
        var slabs = provider.apply(BlockTags.SLABS);
        var walls = provider.apply(BlockTags.WALLS);

        for (DecoBlockSet blockSet : DecoBlockSet.DECO_BLOCK_SETS) {
            {
                Block value = blockSet.STAIRS.get();
                stairs.add(value);
            }
            {
                Block value = blockSet.SLAB.get();
                slabs.add(value);
            }
            {
                Block value = blockSet.WALL.get();
                walls.add(value);
            }
        }
    }

    public static Builder builder(String id, Supplier<BlockBehaviour.Properties> supplier) {
        return new Builder(id, supplier);
    }

    public static class Builder {
        private final String id;
        private final Supplier<BlockBehaviour.Properties> properties;
        private final List<ObjectIntPair<Supplier<? extends ItemLike>>> materials = new ArrayList<>();
        private boolean stonecutting = false;
        private Item.Properties itemProperties = new Item.Properties();
        private Function<BlockBehaviour.Properties, ? extends Block> full = Block::new;
        private BiFunction<BlockState, BlockBehaviour.Properties, ? extends StairBlock> stairs = StairBlock::new;
        private Function<BlockBehaviour.Properties, ? extends SlabBlock> slab = SlabBlock::new;
        private Function<BlockBehaviour.Properties, ? extends WallBlock> wall = WallBlock::new;

        Builder(String id, Supplier<BlockBehaviour.Properties> supplier) {
            this.id = id;
            this.properties = supplier;
        }

        public Builder stonecutting() {
            this.stonecutting = true;
            return this;
        }

        public Builder itemProperties(Item.Properties properties) {
            this.itemProperties = properties;
            return this;
        }

        public Builder material(Supplier<? extends ItemLike> supplier, int toAmount) {
            materials.add(new ObjectIntImmutablePair<>(supplier, toAmount));
            return this;
        }

        public Builder full(Function<BlockBehaviour.Properties, ? extends Block> function) {
            this.full = function;
            return this;
        }

        public Builder slab(@Nullable Function<BlockBehaviour.Properties, ? extends SlabBlock> function) {
            this.slab = function;
            return this;
        }

        public Builder stair(@Nullable BiFunction<BlockState, BlockBehaviour.Properties, ? extends StairBlock> function) {
            this.stairs = function;
            return this;
        }

        public Builder wall(@Nullable Function<BlockBehaviour.Properties, ? extends WallBlock> function) {
            this.wall = function;
            return this;
        }

        public DecoBlockSet build() {
            return new DecoBlockSet(this);
        }
    }
}
