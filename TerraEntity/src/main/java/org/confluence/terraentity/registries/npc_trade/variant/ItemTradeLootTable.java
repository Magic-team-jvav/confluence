package org.confluence.terraentity.registries.npc_trade.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootTable;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.terraentity.api.npc.trade.IIngredientTrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLootTable;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;
import org.confluence.terraentity.registries.npc_trade.TradeProviderTypes;

import java.util.List;
import java.util.Optional;

/**
 * 战利品交易表
 * @param lootTable 战利品掉落表
 */
public record ItemTradeLootTable(
        List<AmountIngredient> costs,
        ResourceKey<LootTable> lootTable,
        ResourceLocation sprite,
        String translationKey,
        TradeProperties properties
) implements IIngredientTrade, ITradeLootTable {
    public static final MapCodec<ItemTradeLootTable> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AmountIngredient.CODEC.codec().listOf().fieldOf("costs").forGetter(ItemTradeLootTable::costs),
            ResourceLocation.CODEC.fieldOf("loot_table").forGetter(i -> i.lootTable().location()),
            ResourceLocation.CODEC.optionalFieldOf("sprite").forGetter(i -> Optional.ofNullable(i.sprite())),
            Codec.STRING.optionalFieldOf("translation_key").forGetter(i -> Optional.ofNullable(i.translationKey())),
            TradeProperties.CODEC.optionalFieldOf("properties").forGetter(i -> Optional.ofNullable(i.properties))
    ).apply(instance, (item, lootTable, sprite, translationKey, properties1) -> new ItemTradeLootTable(
            item,
            ResourceKey.create(Registries.LOOT_TABLE, lootTable),
            sprite.orElse(null),
            translationKey.orElse(null),
            properties1.orElse(null)
    )));

    @Override
    public void onTrade(ServerPlayer player, ITradeHolder npc, int index) {
        IIngredientTrade.super.onTrade(player, npc, index);
        ITradeLootTable.super.onTrade(player, npc, index);
    }

    @Override
    public TradeProvider getCodec() {
        return TradeProviderTypes.ITEM_TRADE_LOOT_TABLE.get();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends IIngredientTrade.Builder<ItemTradeLootTable, Builder> {
        private ResourceKey<LootTable> lootTable;
        private ResourceLocation sprite;
        private String translationKey;

        public Builder setLootTable(ResourceLocation lootTable) {
            this.lootTable = ResourceKey.create(Registries.LOOT_TABLE, lootTable);
            return this;
        }

        public Builder setLootTable(ResourceKey<LootTable> lootTable) {
            this.lootTable = lootTable;
            return this;
        }

        public Builder setSprite(ResourceLocation sprite) {
            this.sprite = sprite;
            return this;
        }

        public Builder setTranslationKey(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        public ItemTradeLootTable build() {
            return new ItemTradeLootTable(costs, lootTable, sprite, translationKey, properties);
        }
    }
}
