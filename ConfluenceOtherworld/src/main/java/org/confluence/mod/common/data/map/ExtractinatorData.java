package org.confluence.mod.common.data.map;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.init.ModDataMaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record ExtractinatorData(List<Pool> pools) {
    public static final Codec<ExtractinatorData> CODEC = Pool.CODEC.listOf().xmap(ExtractinatorData::new, ExtractinatorData::pools);

    public static void extract(Level level, BlockPos pos, Player player, InteractionHand hand, ServerLevel serverLevel, ItemStack itemStack, ExtractinatorData data) {
        ParticleOptions options = itemStack.getItem() instanceof BlockItem blockItem
                ? new BlockParticleOption(ParticleTypes.BLOCK, blockItem.getBlock().defaultBlockState())
                : new ItemParticleOption(ParticleTypes.ITEM, itemStack);
        serverLevel.sendParticles(options, pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F, 100, 0F, 0.0625F, 0F, 0.25F);

        RandomSource random = player.getRandom();
        for (Pool pool : data.pools) {
            for (ItemStack item : pool.getRandomItems(random)) {
                LibEntityUtils.createItemEntity(item, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, level, 40);
            }
        }

        itemStack.shrink(1);
        if (itemStack.isEmpty()) player.setItemInHand(hand, ItemStack.EMPTY);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static void addTooltip(Holder<Item> holder, List<Component> toolTip) {
        if (holder.getData(ModDataMaps.EXTRACTINATOR) != null ||
                holder.getData(ModDataMaps.CHLOROPHYTE_EXTRACTINATOR) != null
        ) {
            toolTip.add(Component.translatable("tooltip.item.confluence.can_be_extractinated.0").withStyle(ChatFormatting.GRAY));
        }
    }

    public record Entry(
            Item item,
            int minCount,
            int maxCount,
            Weight weight
    ) implements WeightedEntry {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PortCodecExtension.lenientOptionalFieldOf(BuiltInRegistries.ITEM.byNameCodec(), "item", Items.AIR).forGetter(entry -> entry.item),
                PortCodecExtension.lenientOptionalFieldOf(Codec.INT, "min_count", 1).forGetter(entry -> entry.minCount),
                PortCodecExtension.lenientOptionalFieldOf(Codec.INT, "max_count", 1).forGetter(entry -> entry.maxCount),
                PortCodecExtension.lenientOptionalFieldOf(Weight.CODEC, "weight", Weight.of(1)).forGetter(Entry::getWeight)
        ).apply(instance, Entry::new));

        public Entry {
            if (maxCount < minCount) {
                throw new IllegalArgumentException("minCount=" + minCount + " must greater or equals than maxCount=" + maxCount);
            }
            if (maxCount < 0) {
                throw new IllegalArgumentException("maxCount=" + maxCount + " must greater or equals than zero");
            }
        }

        public Entry(Item item, int minCount, int maxCount, int weight) {
            this(item, minCount, maxCount, Weight.of(weight));
        }

        public ItemStack randomItem(RandomSource randomSource) {
            if (item == Items.AIR) return ItemStack.EMPTY;
            return new ItemStack(item, minCount == maxCount ? minCount : Mth.randomBetweenInclusive(randomSource, minCount, maxCount));
        }

        @Override
        public Weight getWeight() {
            return weight;
        }

        public boolean isEmpty() {
            return item == Items.AIR;
        }

        public static Entry empty(Weight weight) {
            return new Entry(Items.AIR, 1, 1, weight);
        }

        public static Entry empty(int weight) {
            return new Entry(Items.AIR, 1, 1, weight);
        }

        public static Entry of(ItemLike item, int minCount, int maxCount, int weight) {
            return new Entry(item.asItem(), minCount, maxCount, weight);
        }

        public static Entry of(ItemLike item, int count, int weight) {
            return new Entry(item.asItem(), count, count, weight);
        }
    }

    public static class Pool {
        public static final Codec<Pool> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PortCodecExtension.lenientOptionalFieldOf(ExtraCodecs.POSITIVE_INT, "min_roll", 1).forGetter(pool -> pool.minRoll),
                PortCodecExtension.lenientOptionalFieldOf(ExtraCodecs.POSITIVE_INT, "max_roll", 1).forGetter(pool -> pool.maxRoll),
                Entry.CODEC.listOf().fieldOf("entries").forGetter(pool -> pool.entries)
        ).apply(instance, Pool::new));
        public final int minRoll;
        public final int maxRoll;
        public final int totalWeight;
        public final ImmutableList<Entry> entries;

        public Pool(int minRoll, int maxRoll, List<Entry> entries) {
            if (maxRoll < minRoll) {
                throw new IllegalArgumentException("minRoll=" + minRoll + " must greater or equals than maxRoll=" + maxRoll);
            }
            if (maxRoll < 0) {
                throw new IllegalArgumentException("maxRoll=" + maxRoll + " must greater or equals than zero");
            }
            this.minRoll = minRoll;
            this.maxRoll = maxRoll;
            this.totalWeight = WeightedRandom.getTotalWeight(entries);
            if (totalWeight == 0) {
                throw new IllegalArgumentException("Invalid entries, which total weight is 0");
            }
            this.entries = ImmutableList.copyOf(entries);
        }

        public Pool(Entry... entries) {
            this(1, 1, Arrays.stream(entries).toList());
        }

        public List<ItemStack> getRandomItems(RandomSource random) {
            List<ItemStack> itemStacks = new ArrayList<>();
            int i = minRoll == maxRoll ? minRoll : Mth.randomBetweenInclusive(random, minRoll, maxRoll);
            for (int j = 0; j < i; j++) {
                WeightedRandom.getWeightedItem(entries, random.nextInt(totalWeight)).ifPresent(entry -> {
                    ItemStack itemStack = entry.randomItem(random);
                    if (!itemStack.isEmpty()) {
                        itemStacks.add(itemStack);
                    }
                });
            }
            return itemStacks;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private int minRoll = 1;
            private int maxRoll = 1;
            private final List<Entry> entries = new ArrayList<>();

            public Builder setRolls(int minRoll, int maxRoll) {
                this.minRoll = minRoll;
                this.maxRoll = maxRoll;
                return this;
            }

            public Builder setRolls(int rolls) {
                this.minRoll = rolls;
                this.maxRoll = rolls;
                return this;
            }

            public Builder add(Entry entry) {
                entries.add(entry);
                return this;
            }

            public Pool build() {
                return new Pool(minRoll, maxRoll, entries);
            }
        }
    }

    public static class Builder {
        private final List<Pool> pools = new ArrayList<>();

        public Builder withPool(Pool pool) {
            pools.add(pool);
            return this;
        }

        public Builder withPool(Pool.Builder pool) {
            pools.add(pool.build());
            return this;
        }

        public ExtractinatorData build() {
            return new ExtractinatorData(pools);
        }
    }
}
