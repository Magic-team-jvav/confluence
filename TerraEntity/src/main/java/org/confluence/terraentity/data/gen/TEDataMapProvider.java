package org.confluence.terraentity.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.confluence.terraentity.init.TEDataMaps;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.concurrent.CompletableFuture;

public class TEDataMapProvider extends DataMapProvider{

    protected TEDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);

    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
//        Builder<WhipData, Item> whipDataItemBuilder = this.builder(TEDataMaps.WHIP_DATA_MAP);
//        whipDataItemBuilder.add(TEWhipItems.SWAMP_WHIP.getDelegate(),
//                WhipData.builder().setDamage(10).setMarkDamage(2).build(), true);
        this.builder(TEDataMaps.ENTITY_XP_DATA_MAP)
                .add(TEMonsterEntities.BASE_BONES, Enemy.XP_REWARD_MEDIUM, true);
    }
}
