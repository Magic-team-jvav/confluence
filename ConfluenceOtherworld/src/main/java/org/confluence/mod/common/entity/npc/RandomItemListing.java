package org.confluence.mod.common.entity.npc;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import org.confluence.lib.util.range.IntegerRange;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RandomItemListing implements VillagerTrades.ItemListing {
    private static final IntegerRange ONE = IntegerRange.of(1, 1);
    protected final Item price;
    protected final IntegerRange priceRange;
    protected final Item forSale;
    protected final IntegerRange forSaleRange;
    protected final int maxTrades;
    protected final int xp;
    protected final float priceMult;

    private ItemStack priceStack;
    private ItemStack forSaleStack;

    public RandomItemListing(ItemLike price, IntegerRange priceRange, ItemLike forSale, IntegerRange forSaleRange, int maxTrades, int xp, float priceMult) {
        this.price = price.asItem();
        this.priceRange = priceRange;
        this.forSale = forSale.asItem();
        this.forSaleRange = forSaleRange;
        this.maxTrades = maxTrades;
        this.xp = xp;
        this.priceMult = priceMult;
    }

    public RandomItemListing(ItemLike price, ItemLike forSale, int maxTrades, int xp, float priceMult) {
        this(price, ONE, forSale, ONE, maxTrades, xp, priceMult);
    }

    public RandomItemListing(ItemLike price, int priceCount, ItemLike forSale, int maxTrades, int xp, float priceMult) {
        this(price, priceCount, forSale, ONE, maxTrades, xp, priceMult);
    }

    public RandomItemListing(ItemLike price, ItemLike forSale, IntegerRange forSaleRange, int maxTrades, int xp, float priceMult) {
        this(price, ONE, forSale, forSaleRange, maxTrades, xp, priceMult);
    }

    public RandomItemListing(ItemLike price, ItemLike forSale, int forSaleCount, int maxTrades, int xp, float priceMult) {
        this(price, ONE, forSale, IntegerRange.of(forSaleCount, forSaleCount), maxTrades, xp, priceMult);
    }

    public RandomItemListing(ItemLike price, int priceCount, ItemLike forSale, int forSaleCount, int maxTrades, int xp, float priceMult) {
        this(price, IntegerRange.of(priceCount, priceCount), forSale, IntegerRange.of(forSaleCount, forSaleCount), maxTrades, xp, priceMult);
    }

    public RandomItemListing(ItemLike price, int priceCount, ItemLike forSale, IntegerRange forSaleRange, int maxTrades, int xp, float priceMult) {
        this(price, IntegerRange.of(priceCount, priceCount), forSale, forSaleRange, maxTrades, xp, priceMult);
    }

    public RandomItemListing(ItemLike price, IntegerRange priceRange, ItemLike forSale, int maxTrades, int xp, float priceMult) {
        this(price, priceRange, forSale, ONE, maxTrades, xp, priceMult);
    }

    public RandomItemListing(ItemLike price, IntegerRange priceRange, ItemLike forSale, int forSaleCount, int maxTrades, int xp, float priceMult) {
        this(price, priceRange, forSale, IntegerRange.of(forSaleCount, forSaleCount), maxTrades, xp, priceMult);
    }

    @Override
    public @Nullable MerchantOffer getOffer(Entity trader, RandomSource random) {
        if (priceStack == null) {
            this.priceStack = price.getDefaultInstance();
            if (Objects.equals(priceRange.min(), priceRange.max())) {
                priceStack.setCount(priceRange.min());
            } else {
                priceStack.setCount(random.nextInt(priceRange.min(), priceRange.max()));
            }
        }
        if (forSaleStack == null) {
            this.forSaleStack = forSale.getDefaultInstance();
            if (Objects.equals(forSaleRange.min(), forSaleRange.max())) {
                forSaleStack.setCount(forSaleRange.min());
            } else {
                forSaleStack.setCount(random.nextInt(forSaleRange.min(), forSaleRange.max()));
            }
        }
        return new MerchantOffer(priceStack, ItemStack.EMPTY, forSaleStack, maxTrades, xp, priceMult);
    }
}
