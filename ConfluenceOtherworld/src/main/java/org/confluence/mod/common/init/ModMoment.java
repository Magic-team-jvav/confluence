package org.confluence.mod.common.init;

import com.xiaohunao.heaven_destiny_moment.common.context.SpawnCategoryMultiplierModifier;
import com.xiaohunao.heaven_destiny_moment.common.init.HDMRegistries;
import com.xiaohunao.heaven_destiny_moment.common.moment.Moment;
import com.xiaohunao.heaven_destiny_moment.common.moment.moment.DefaultMoment;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import org.confluence.mod.Confluence;

public final class ModMoment {
    public static final ResourceKey<Moment> BATTLE_MOB_EFFECT = Confluence.asResourceKey(HDMRegistries.Keys.MOMENT, "battle_mob_effect");
    public static final ResourceKey<Moment> WATER_CANDLE_MOB_EFFECT = Confluence.asResourceKey(HDMRegistries.Keys.MOMENT, "water_candle_mob_effect");
    public static final ResourceKey<Moment> HAPPY_MOB_EFFECT = Confluence.asResourceKey(HDMRegistries.Keys.MOMENT, "happy_mob_effect");
    public static final ResourceKey<Moment> CALM_MOB_EFFECT = Confluence.asResourceKey(HDMRegistries.Keys.MOMENT, "calm_mob_effect");
    public static final ResourceKey<Moment> PEACE_CANDLE_MOB_EFFECT = Confluence.asResourceKey(HDMRegistries.Keys.MOMENT, "peace_candle_mob_effect");

    public static void bootstrap(BootstrapContext<Moment> context) {
        context.register(BATTLE_MOB_EFFECT, new DefaultMoment()
                .setMomentData(momentData -> momentData
                        .entitySpawnSettings(entitySpawnSettings -> entitySpawnSettings
                                .biomeEntitySpawnSettings(biomeEntitySpawnSettings -> biomeEntitySpawnSettings
                                        .spawnCategoryMultiplier(MobCategory.MONSTER,new SpawnCategoryMultiplierModifier(Confluence.asResource("battle_mob_effect"),1.0, SpawnCategoryMultiplierModifier.Operation.ADD_MULTIPLIED_BASE))
                                        .spawnCategoryMultiplier(MobCategory.CREATURE,new SpawnCategoryMultiplierModifier(Confluence.asResource("battle_mob_effect"),1.0, SpawnCategoryMultiplierModifier.Operation.ADD_MULTIPLIED_BASE))
                                )
                        )
                )
        );
        registerSimpleSpawnCategoryMultiplier(context, WATER_CANDLE_MOB_EFFECT, MobCategory.MONSTER,
                Confluence.asResource("water_candle_mob_effect"),0.5, SpawnCategoryMultiplierModifier.Operation.ADD_MULTIPLIED_BASE
        );

        registerSimpleSpawnCategoryMultiplier(context, HAPPY_MOB_EFFECT, MobCategory.MONSTER,
                Confluence.asResource("happy_mob_effect"),-0.2, SpawnCategoryMultiplierModifier.Operation.ADD_MULTIPLIED_BASE
        );

        registerSimpleSpawnCategoryMultiplier(context, CALM_MOB_EFFECT, MobCategory.MONSTER,
                Confluence.asResource("calm_mob_effect"),-0.4, SpawnCategoryMultiplierModifier.Operation.ADD_MULTIPLIED_BASE
        );
        registerSimpleSpawnCategoryMultiplier(context, PEACE_CANDLE_MOB_EFFECT, MobCategory.MONSTER,
                Confluence.asResource("peace_candle_mob_effect"),-0.5, SpawnCategoryMultiplierModifier.Operation.ADD_MULTIPLIED_BASE
        );


    }

    public static void registerSimpleSpawnCategoryMultiplier(BootstrapContext<Moment> context, ResourceKey<Moment> key, MobCategory mobCategory, ResourceLocation id, double amount, SpawnCategoryMultiplierModifier.Operation operation) {
        context.register(key, new DefaultMoment()
                .setMomentData(momentData -> momentData
                        .entitySpawnSettings(entitySpawnSettings -> entitySpawnSettings
                                .biomeEntitySpawnSettings(biomeEntitySpawnSettings -> biomeEntitySpawnSettings
                                        .spawnCategoryMultiplier(mobCategory, new SpawnCategoryMultiplierModifier(id, amount, operation))
                                )
                        )
                )
        );
    }
}
