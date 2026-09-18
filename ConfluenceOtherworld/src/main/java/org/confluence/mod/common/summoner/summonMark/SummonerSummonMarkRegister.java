package org.confluence.mod.common.summoner.summonMark;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.SummonerRegistries;

public final class SummonerSummonMarkRegister {
    public static final DeferredRegister<SummonMarkType> TYPES =
            DeferredRegister.create(SummonerRegistries.SUMMON_MARK_TYPE_KEY, Confluence.MODID);

    public static final RegistryObject<SummonMarkType> BASE =
            TYPES.register("base", () -> new SummonMarkTypeBuild(Confluence.asResource("base"))
                    .damage(1.0F)
                    .criticalRate(0.0F)
                    .armorPierce(0.0F)
                    .build());

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }
}
