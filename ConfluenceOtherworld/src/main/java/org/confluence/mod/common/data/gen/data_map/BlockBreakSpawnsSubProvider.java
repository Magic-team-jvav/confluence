package org.confluence.mod.common.data.gen.data_map;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.BlockBreakSpawns;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.terraentity.init.entity.TEAnimals;

import java.util.ArrayList;
import java.util.List;

public final class BlockBreakSpawnsSubProvider {
    public static void gather(ModDataMapProvider.Appender<Builder> appender, HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<Biome> biome = provider.lookupOrThrow(Registries.BIOME);
        HolderSet<Biome> jungleLike = new OrHolderSet<>(biome.getOrThrow(Tags.Biomes.IS_JUNGLE), biome.getOrThrow(Tags.Biomes.IS_LUSH));

        appender.create()
                .push(Blocks.SHORT_GRASS)
                .expand(TEAnimals.GRASSHOPPER.get(), 0.01F)
                .expand(TEAnimals.WORM.get(), 0.0025F)
                .pop()
                .push(NatureBlocks.SMALL_STONE_PILES.get())
                .expand(TEAnimals.WORM.get(), 0.0025F)
                .pop()
                .push(Blocks.FERN)
                .expand(TEAnimals.GRASSHOPPER.get(), 0.01F)
                .expand(TEAnimals.WORM.get(), 0.0025F)
                .pop()
                .push(Blocks.TALL_GRASS)
                .expand(TEAnimals.GRASSHOPPER.get(), 0.01F)
                .expand(TEAnimals.WORM.get(), 0.0025F)
                .pop()
                .push(NatureBlocks.LIFE_MUSHROOM.get())
                .expand(TEAnimals.GRASSHOPPER.get(), 0.02F)
                .expand(TEAnimals.WORM.get(), 0.005F)
                .pop()
                .push(NatureBlocks.ASH_GRASS.get())
                .expand(TEAnimals.HELL_BUTTERFLY.get(), 0.07F)
                .expand(TEAnimals.MAGMA_SNAIL.get(), 0.07F)
                .pop()
                .push(Blocks.CRIMSON_ROOTS)
                .expand(TEAnimals.HELL_BUTTERFLY.get(), 0.01F)
                .expand(TEAnimals.MAGMA_SNAIL.get(), 0.01F)
                .pop()
                .push(Blocks.WARPED_ROOTS)
                .expand(TEAnimals.HELL_BUTTERFLY.get(), 0.01F)
                .expand(TEAnimals.MAGMA_SNAIL.get(), 0.01F)
                .pop()
        ;

        SimpleWeightedRandomList<EntityType<?>> jungleBugs = SimpleWeightedRandomList.<EntityType<?>>builder()
                .add(TEAnimals.GRUBBY.get(), 8)
                .add(TEAnimals.SLUGGY.get(), 3)
                .add(TEAnimals.GRUBBY.get(), 1) // todo 改成蚜虫
                .build();
        appender.create()
                .push(NatureBlocks.JUNGLE_ROSE.get())
                .expand(jungleBugs, 0.0125F, 7, jungleLike)
                .pop()
                .push(NatureBlocks.JUNGLE_SPORE.get())
                .expand(jungleBugs, 0.0125F, 7, jungleLike)
                .pop()
                .push(NatureBlocks.NATURES_GIFT.get())
                .expand(jungleBugs, 0.0125F, 7, jungleLike)
                .pop()
                .push(Blocks.TALL_GRASS)
                .expand(jungleBugs, 0.025F, 7, jungleLike)
                .pop()
                .push(NatureBlocks.JUNGLE_DROOPING_VINE.get())
                .expand(jungleBugs, 0.004F, 7, jungleLike)
                .pop();
    }

    public static class Builder extends DataMapProvider.Builder<BlockBreakSpawns, Block> {
        public Builder() {
            super(ModDataMaps.BLOCK_BREAK_SPAWNS);
        }

        public Expander push(Block block) {
            return new Expander(Either.right(block.builtInRegistryHolder().unwrapKey().orElseThrow()));
        }

        public Expander push(TagKey<Block> tag) {
            return new Expander(Either.left(tag));
        }

        public class Expander {
            private final List<BlockBreakSpawns.Spawn> list = new ArrayList<>();
            private final Either<TagKey<Block>, ResourceKey<Block>> either;

            public Expander(Either<TagKey<Block>, ResourceKey<Block>> either) {
                this.either = either;
            }

            public Expander expand(SimpleWeightedRandomList<EntityType<?>> types, float chance, int maxAmount, HolderSet<Biome> biomes) {
                list.add(new BlockBreakSpawns.Spawn(types, biomes, chance, maxAmount));
                return this;
            }

            public Expander expand(EntityType<?> type, float chance, HolderSet<Biome> biomes) {
                return expand(SimpleWeightedRandomList.single(type), chance, 0x3F3F3F3F, biomes);
            }

            public Expander expand(EntityType<?> type, float chance) {
                return expand(type, chance, HolderSet.empty());
            }

            public Builder pop() {
                BlockBreakSpawns data = new BlockBreakSpawns(list);
                return (Builder) either.map(
                        tag -> Builder.this.add(tag, data, false),
                        key -> Builder.this.add(key, data, false)
                );
            }
        }
    }
}
