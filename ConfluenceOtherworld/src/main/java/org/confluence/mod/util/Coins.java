package org.confluence.mod.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntSortedMap;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.common.CoinItem;

public final class Coins {
//    public static final Codec<Coins> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            Codec.INT.fieldOf("copper").forGetter(Coins::copper),
//            Codec.INT.fieldOf("silver").forGetter(Coins::silver),
//            Codec.INT.fieldOf("gold").forGetter(Coins::gold),
//            Codec.INT.fieldOf("platinum").forGetter(Coins::platinum)
//    ).apply(instance, Coins::new));
//    public static final StreamCodec<ByteBuf, Coins> STREAM_CODEC = StreamCodec.composite(
//            ByteBufCodecs.VAR_INT, Coins::copper,
//            ByteBufCodecs.VAR_INT, Coins::silver,
//            ByteBufCodecs.VAR_INT, Coins::gold,
//            ByteBufCodecs.VAR_INT, Coins::platinum,
//            Coins::new
//    );

    private int copper;
    private int silver;
    private int gold;
    private int platinum;

    private IntArrayList copper2Platinum;
    private IntArrayList platinum2Copper;
    private Object2IntLinkedOpenHashMap<CoinItem> copper2PlatinumMap;
    private Object2IntLinkedOpenHashMap<CoinItem> platinum2CopperMap;

    public Coins(int copper, int silver, int gold, int platinum) {
        this.copper = copper;
        this.silver = silver;
        this.gold = gold;
        this.platinum = platinum;
    }

    public int copper() {
        return copper;
    }

    public int silver() {
        return silver;
    }

    public int gold() {
        return gold;
    }

    public int platinum() {
        return platinum;
    }

    public void update(CoinItem coin, int count) {
        int index;
        if (coin == ModItems.COPPER_COIN.get()) {
            this.copper = count;
            index = 0;
        } else if (coin == ModItems.SILVER_COIN.get()) {
            this.silver = count;
            index = 1;
        } else if (coin == ModItems.GOLD_COIN.get()) {
            this.gold = count;
            index = 2;
        } else if (coin == ModItems.PLATINUM_COIN.get()) {
            this.platinum = count;
            index = 3;
        } else {
            return;
        }
        if (copper2Platinum != null) copper2Platinum.set(index, count);
        if (platinum2Copper != null) platinum2Copper.set(3 - index, count);
        if (copper2PlatinumMap != null) copper2PlatinumMap.put(coin, count);
        if (platinum2CopperMap != null) platinum2CopperMap.put(coin, count);
    }

    public void increase(CoinItem coin, int count) {
        update(coin, byItem(coin) + count);
    }

    public int byItem(CoinItem coin) {
        if (coin == ModItems.COPPER_COIN.get()) return copper;
        if (coin == ModItems.SILVER_COIN.get()) return silver;
        if (coin == ModItems.GOLD_COIN.get()) return gold;
        if (coin == ModItems.PLATINUM_COIN.get()) return platinum;
        return 0;
    }

    public IntList copper2Platinum() {
        if (copper2Platinum == null) {
            this.copper2Platinum = IntArrayList.of(copper, silver, gold, platinum);
        }
        return copper2Platinum;
    }

    public IntList platinum2Copper() {
        if (platinum2Copper == null) {
            this.platinum2Copper = IntArrayList.of(platinum, gold, silver, copper);
        }
        return platinum2Copper;
    }

    public Object2IntSortedMap.FastSortedEntrySet<CoinItem> copper2PlatinumEntries() {
        if (copper2PlatinumMap == null) {
            this.copper2PlatinumMap = new Object2IntLinkedOpenHashMap<>();
            copper2PlatinumMap.put(ModItems.COPPER_COIN.get(), copper);
            copper2PlatinumMap.put(ModItems.SILVER_COIN.get(), silver);
            copper2PlatinumMap.put(ModItems.GOLD_COIN.get(), gold);
            copper2PlatinumMap.put(ModItems.PLATINUM_COIN.get(), platinum);
        }
        return copper2PlatinumMap.object2IntEntrySet();
    }

    public Object2IntSortedMap.FastSortedEntrySet<CoinItem> platinum2CopperEntries() {
        if (platinum2CopperMap == null) {
            this.platinum2CopperMap = new Object2IntLinkedOpenHashMap<>();
            platinum2CopperMap.put(ModItems.PLATINUM_COIN.get(), platinum);
            platinum2CopperMap.put(ModItems.GOLD_COIN.get(), gold);
            platinum2CopperMap.put(ModItems.SILVER_COIN.get(), silver);
            platinum2CopperMap.put(ModItems.COPPER_COIN.get(), copper);
        }
        return platinum2CopperMap.object2IntEntrySet();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof Coins that &&
                this.copper == that.copper &&
                this.silver == that.silver &&
                this.gold == that.gold &&
                this.platinum == that.platinum);
    }

    @Override
    public int hashCode() {
        int result = copper;
        result = 31 * result + silver;
        result = 31 * result + gold;
        result = 31 * result + platinum;
        return result;
    }

    @Override
    public String toString() {
        return "Coins[copper=" + copper +
                ", silver=" + silver +
                ", golden=" + gold +
                ", platinum=" + platinum + ']';
    }

    public static Coins createEmpty() {
        return new Coins(0, 0, 0, 0);
    }
}
