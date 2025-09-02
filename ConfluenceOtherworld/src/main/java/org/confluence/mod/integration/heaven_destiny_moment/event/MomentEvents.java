package org.confluence.mod.integration.heaven_destiny_moment.event;

import com.xiaohunao.heaven_destiny_moment.common.context.condition.common.*;
import com.xiaohunao.heaven_destiny_moment.common.context.condition.level.DifficultyCondition;
import com.xiaohunao.heaven_destiny_moment.common.context.condition.level.LevelCondition;
import com.xiaohunao.heaven_destiny_moment.common.context.condition.level.LevelRunningTimeCondition;
import com.xiaohunao.heaven_destiny_moment.common.context.condition.moment.MomentHistoryCondition;
import com.xiaohunao.heaven_destiny_moment.common.context.condition.player.PlayerCondition;
import com.xiaohunao.heaven_destiny_moment.common.init.HDMRegistries;
import com.xiaohunao.heaven_destiny_moment.common.moment.IMoment;
import com.xiaohunao.heaven_destiny_moment.common.moment.MomentInstanceManager;
import com.xiaohunao.heaven_destiny_moment.common.predicate.AttributePredicate;
import com.xiaohunao.heaven_destiny_moment.common.trigger.triggers.TimeProbabilityTrigger;
import com.xiaohunao.heaven_destiny_moment.compat.phase_journey.condition.PhaseJourneyCondition;
import com.xiaohunao.terra_moment.TerraMoment;
import com.xiaohunao.terra_moment.common.init.TMMomentTypes;
import com.xiaohunao.terra_moment.common.init.TMMoments;
import com.xiaohunao.xhn_lib.api.register.PostRegisterResult;
import com.xiaohunao.xhn_lib.common.event.FlexibleRegisterEvent;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.heaven_destiny_moment.init.ModMomentProbabilityFunction;
import org.confluence.mod.integration.heaven_destiny_moment.context.condition.EverBeneficialCondition;

import java.util.List;

@EventBusSubscriber
public class MomentEvents {
    @SubscribeEvent
    public static void onFlexibleRegisterBefore(FlexibleRegisterEvent.Before<IMoment> event) {
        if (!(event.getValue() instanceof IMoment)) {
            return;
        }

        IMoment value = event.getValue();
        if (value == TMMoments.GOBLIN_ARMY.value()) {
            value.setMomentData(momentData -> {
                momentData.autoActuatorGroupSettings(autoActuatorGroupSettings -> {
                    autoActuatorGroupSettings.remove(TerraMoment.asResource("goblin_army_create"));

                    autoActuatorGroupSettings.create(
                            TerraMoment.asResource("goblin_army_create"),
                            TimeProbabilityTrigger.exactly(1000, ModMomentProbabilityFunction.GOBLIN_ARMY.get()),
                            PlayerCondition.builder(PlayerCondition.Type.ANY)
                                    .attributePredicate(Attributes.ARMOR, AttributePredicate.ValueType.CURRENT, MinMaxBounds.Doubles.atLeast(18.0D))
                                    .subConditions(EverBeneficialCondition.builder().lifeCrystals(5).build())
                                    .build(),
                            PhaseJourneyCondition.of(PhaseJourneyCondition.Type.LEVEL, Confluence.asResource("has_it_evil_ever_been_broken")),
                            WorldUniqueMomentCondition.DEFAULT,
                            OrCondition.of(ModLoadedCondition.of("confluence_dimension_patch"),
                                List.of(LocationCondition.Builder.inDimension(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse("confluence_dimension_patch:otherworld"))).build()),
                                List.of(LocationCondition.Builder.inDimension(Level.OVERWORLD).build())
                            ),
                            InvertCondition.of(DifficultyCondition.PEACEFUL),
                            LevelRunningTimeCondition.atLeast(24000 + 1000)
                    );

                    return autoActuatorGroupSettings;
                });


                return momentData;
            });
        }

        if (value == TMMoments.SLIME_RAIN.value()) {
            value.setMomentData(momentData -> {
                momentData.autoActuatorGroupSettings(autoActuatorGroupSettings -> {
                    autoActuatorGroupSettings.remove(TerraMoment.asResource("slime_rain_create"));

                    autoActuatorGroupSettings.create(
                            TerraMoment.asResource("slime_rain_create"),
                            TimeProbabilityTrigger.between(22500, 6000, ModMomentProbabilityFunction.SLIME_RAIN.get()),
                            LevelRunningTimeCondition.atLeast(30 * 60 * 20),
                            MomentHistoryCondition.randomTicks(85 * 60 * 20,180 * 60 * 20, TMMomentTypes.SLIME_RAIN.get()),
                            PlayerCondition.builder(PlayerCondition.Type.ANY)
                                    .subConditions(EverBeneficialCondition.builder().lifeCrystals(2).build())
                                    .build(),
                            WorldUniqueMomentCondition.DEFAULT,
                            OrCondition.of(ModLoadedCondition.of("confluence_dimension_patch"),
                                    List.of(LocationCondition.Builder.inDimension(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse("confluence_dimension_patch:otherworld"))).build()),
                                    List.of(LocationCondition.Builder.inDimension(Level.OVERWORLD).build())
                            ),
                            InvertCondition.of(DifficultyCondition.PEACEFUL)
                    );

                    return autoActuatorGroupSettings;
                });
                return momentData;
            });
        }

        if (value == TMMoments.BLOOD_MOON.value()){
            value.setMomentData(momentData -> {
                momentData.autoActuatorGroupSettings(autoActuatorGroupSettings -> {
                    autoActuatorGroupSettings.remove(TerraMoment.asResource("blood_moon_create"));

                    autoActuatorGroupSettings.create(
                            TerraMoment.asResource("blood_moon_create"),
                            TimeProbabilityTrigger.exactly(13800,0.06f),
                            WorldUniqueMomentCondition.DEFAULT,
                            InvertCondition.of(LevelCondition.validMoonPhases(4)),
                            PlayerCondition.builder(PlayerCondition.Type.ANY)
                                    .subConditions(EverBeneficialCondition.builder().lifeCrystals(1).build())
                                    .build(),
                            OrCondition.of(ModLoadedCondition.of("confluence_dimension_patch"),
                                    List.of(LocationCondition.Builder.inDimension(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse("confluence_dimension_patch:otherworld"))).build()),
                                    List.of(LocationCondition.Builder.inDimension(Level.OVERWORLD).build())
                            ),
                            InvertCondition.of(DifficultyCondition.PEACEFUL),
                            LevelRunningTimeCondition.atLeast(24000 + 1000)
                    );

                    return autoActuatorGroupSettings;
                });
                return momentData;
            });
        }

//        event.setResult(PostRegisterResult.modify(value));
    }

    @SubscribeEvent
    public static void onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        MomentInstanceManager momentInstanceManager = MomentInstanceManager.of(level);
        Registry<IMoment> moment = HDMRegistries.MOMENT;
//        System.out.println(moment);
//        System.out.println(momentInstanceManager);
    }

}
