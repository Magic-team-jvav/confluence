package org.confluence.terraentity.data.gen.recipe;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITradeModifier;
import org.confluence.terraentity.data.gen.AbstractExistCodecProvider;
import org.confluence.terraentity.entity.npc.trade.TradeModifiers;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.registries.npc_trade.variant.ItemTradeItemList;
import org.confluence.terraentity.registries.npc_trade_modify.variant.TradeItemModifier;
import org.confluence.terraentity.registries.npc_trade_modify.variant.TradeListModifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * 用来测试，发布时应该删掉
 */
@Deprecated(forRemoval = true)  // 只是为了rundata时提示
public class TENPCShopModifierProvider extends AbstractExistCodecProvider<List<ITradeModifier>> {

    public TENPCShopModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void run(HolderLookup.Provider provider) {

        this.gen(TerraEntity.space(TradeModifiers.KEY + "/modifies"), ImmutableList.<ITradeModifier>builder()
                .add(new TradeItemModifier(1, 0, TENpcEntities.MERCHANT.getId(),  ITradeModifier.OperatorType.ADD, ItemTradeItemList.builder()
                        .addCost(Items.DIAMOND, 10).addResult(Items.BLACK_BED, 1)
                        .build()))
                .add(new TradeItemModifier(2, 3, TENpcEntities.MERCHANT.getId(),  ITradeModifier.OperatorType.DEL, null))
                .add(new TradeItemModifier(2, 5, TENpcEntities.GUIDE.getId(),  ITradeModifier.OperatorType.REPLACE, ItemTradeItemList.builder()
                        .addCost(Items.DIAMOND, 10).addResult(Items.RED_BED, 1)
                        .build()))
                .build());

//        this.gen(TerraEntity.space(TradeModifiers.KEY + "/modify_merchant2"), ImmutableList.<ITradeModifier>builder()
//                .add(new TradeItemModifier(1, 3, TENpcEntities.MERCHANT.getId(),  ITradeModifier.OperatorType.ADD, ItemTradeItemList.builder()
//                        .addCost(Items.DIAMOND, 15).addResult(Items.RED_BED, 2)
//                        .build()))
//                .build());

        this.gen(TerraEntity.space(TradeModifiers.KEY + "/del_nurse"), ImmutableList.<ITradeModifier>builder()
                .add(new TradeListModifier(1, TENpcEntities.NURSE.getId(),  ITradeModifier.OperatorType.DEL,null))
                .build()
        );
        this.gen(TerraEntity.space(TradeModifiers.KEY + "/add_dye_trader"), ImmutableList.<ITradeModifier>builder()
                .add(new TradeListModifier(1, TENpcEntities.DYE_TRADER.getId(),  ITradeModifier.OperatorType.ADD,List.of(
                        ItemTradeItemList.builder().addCost(Items.DIAMOND, 10).addResult(Items.BLACK_BED, 1).build(),
                        ItemTradeItemList.builder().addCost(Items.DIAMOND, 10).addResult(Items.RED_BED, 1).build()
                )))
                .build()
        );
        this.gen(TerraEntity.space(TradeModifiers.KEY + "/replace_painter"), ImmutableList.<ITradeModifier>builder()
                .add(new TradeListModifier(1, TENpcEntities.PAINTER.getId(),  ITradeModifier.OperatorType.REPLACE,List.of(
                        ItemTradeItemList.builder().addCost(Items.DIAMOND, 10).addResult(Items.BLACK_BED, 1).build(),
                        ItemTradeItemList.builder().addCost(Items.DIAMOND, 10).addResult(Items.RED_BED, 1).build()
                )))
                .build()
        );
    }


    @Override
    protected Codec<List<ITradeModifier>> getCodec() {
        return TradeModifiers.CODEC;
    }

    @Override
    public String getName() {
        return "Trade Modifier";
    }
}
