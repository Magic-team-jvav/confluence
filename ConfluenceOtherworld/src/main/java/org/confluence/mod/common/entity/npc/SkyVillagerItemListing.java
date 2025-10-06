package org.confluence.mod.common.entity.npc;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.IntegerRange;
import org.confluence.mod.common.init.ModVillagers;
import org.jetbrains.annotations.Nullable;

public class SkyVillagerItemListing extends RandomItemListing {
    public SkyVillagerItemListing(ItemLike price, IntegerRange priceRange, ItemLike forSale, IntegerRange forSaleRange, int maxTrades, int xp, float priceMult) {
        super(price, priceRange, forSale, forSaleRange, maxTrades, xp, priceMult);
    }

    public SkyVillagerItemListing(ItemLike price, ItemLike forSale, int maxTrades, int xp, float priceMult) {
        super(price, forSale, maxTrades, xp, priceMult);
    }

    public SkyVillagerItemListing(ItemLike price, int priceCount, ItemLike forSale, int maxTrades, int xp, float priceMult) {
        super(price, priceCount, forSale, maxTrades, xp, priceMult);
    }

    public SkyVillagerItemListing(ItemLike price, ItemLike forSale, IntegerRange forSaleRange, int maxTrades, int xp, float priceMult) {
        super(price, forSale, forSaleRange, maxTrades, xp, priceMult);
    }

    public SkyVillagerItemListing(ItemLike price, ItemLike forSale, int forSaleCount, int maxTrades, int xp, float priceMult) {
        super(price, forSale, forSaleCount, maxTrades, xp, priceMult);
    }

    public SkyVillagerItemListing(ItemLike price, int priceCount, ItemLike forSale, int forSaleCount, int maxTrades, int xp, float priceMult) {
        super(price, priceCount, forSale, forSaleCount, maxTrades, xp, priceMult);
    }

    public SkyVillagerItemListing(ItemLike price, int priceCount, ItemLike forSale, IntegerRange forSaleRange, int maxTrades, int xp, float priceMult) {
        super(price, priceCount, forSale, forSaleRange, maxTrades, xp, priceMult);
    }

    public SkyVillagerItemListing(ItemLike price, IntegerRange priceRange, ItemLike forSale, int maxTrades, int xp, float priceMult) {
        super(price, priceRange, forSale, maxTrades, xp, priceMult);
    }

    public SkyVillagerItemListing(ItemLike price, IntegerRange priceRange, ItemLike forSale, int forSaleCount, int maxTrades, int xp, float priceMult) {
        super(price, priceRange, forSale, forSaleCount, maxTrades, xp, priceMult);
    }

    @Override
    public @Nullable MerchantOffer getOffer(Entity pTrader, RandomSource random) {
        if (pTrader instanceof VillagerDataHolder holder && holder.getVillagerData().getType() == ModVillagers.SKY_TYPE.get()) {
            return super.getOffer(pTrader, random);
        } else {
            return null;
        }
    }
}
