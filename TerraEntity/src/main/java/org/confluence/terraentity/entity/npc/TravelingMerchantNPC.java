package org.confluence.terraentity.entity.npc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.api.event.NPCEvent;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeGenerator;
import org.confluence.terraentity.registries.npc_trade_list.variant.WeightMapGenerator;
import org.confluence.terraentity.utils.AdapterUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public class TravelingMerchantNPC extends AbstractTerraNPC {
    private long spawnDayTime;

    public TravelingMerchantNPC(EntityType<? extends AbstractTerraNPC> entityType, Level level) {
        super(entityType, level);
        if (!level.isClientSide) {
            this.spawnDayTime = level.getDayTime();
        }
    }

    @Override
    protected void customServerAiStep() {
        long dayTime = level().getDayTime();
        if (dayTime < spawnDayTime || dayTime % 24000 >= 12000) {
            // confluence mixin here
            discard();
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.spawnDayTime = tag.getLong("SpawnDayTime");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putLong("SpawnDayTime", spawnDayTime);
    }

    @Override
    public List<ITrade> generateTrades(ITradeGenerator tradeGenerator) {
        if (tradeGenerator instanceof WeightMapGenerator generator) {
            NPCEvent.TravelingMerchantGenerateTradeEvent event = AdapterUtils.postGameEvent(new NPCEvent.TravelingMerchantGenerateTradeEvent(this, generator.getCount()));
            int count = event.getGenerateCount();
            return Stream.concat(generator.generateTradesDynamic(count).stream(), event.getTrades().stream()).toList();
        }
        return null;
    }


}
