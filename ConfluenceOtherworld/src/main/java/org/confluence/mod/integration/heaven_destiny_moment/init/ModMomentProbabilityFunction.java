package org.confluence.mod.integration.heaven_destiny_moment.init;

import com.xiaohunao.heaven_destiny_moment.common.function.MomentProbabilityFunction;
import com.xiaohunao.heaven_destiny_moment.common.init.HDMRegistries;
import com.xiaohunao.terra_moment.common.moment.SlimeRainMoment;
import com.xiaohunao.xhn_lib.api.register.holder.FlexibleHolder;
import com.xiaohunao.xhn_lib.api.register.register.FlexibleRegister;
import net.minecraft.world.Difficulty;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.phase_journey.common.util.PhaseUtils;

public class ModMomentProbabilityFunction {
    public static final FlexibleRegister<MomentProbabilityFunction> MOMENT_PROBABILITY_FUNCTION = FlexibleRegister.create(HDMRegistries.Keys.MOMENT_PROBABILITY_FUNCTION, Confluence.MODID);

    public static final FlexibleHolder<MomentProbabilityFunction,MomentProbabilityFunction> GOBLIN_ARMY = MOMENT_PROBABILITY_FUNCTION.registerStatic("goblin_army",
            () -> level -> {
                double spawnChance = PhaseUtils.getValueBasedOnPhase(Confluence.asResource("goblin_army_victory"), level, 0.03F, 0.33F);
                if (KillBoard.INSTANCE.getGamePhase().isHardmode() && spawnChance == 0.03) {
                    spawnChance = 0.0167; //在肉后阶段,概率降低到1.67%
                }
                return spawnChance;
            }
    );


    public static final FlexibleHolder<MomentProbabilityFunction,MomentProbabilityFunction> SLIME_RAIN = MOMENT_PROBABILITY_FUNCTION.registerStatic("slime_rain",
            () -> level -> {
                // 检查是否是困难模式（对应泰拉瑞亚的专家模式）
                boolean isHardMode = level.getDifficulty() == Difficulty.HARD;
                // 检查是否是困难游戏阶段（对应泰拉瑞亚的困难模式）
                boolean isHardmodePhase = KillBoard.INSTANCE.getGamePhase().isHardmode();

                if (isHardmodePhase) {
                    // 困难游戏阶段
                    if (isHardMode) {
                        // 困难游戏阶段 + 困难难度（专家模式）
                        return PhaseUtils.getValueBasedOnPhase(
                                SlimeRainMoment.SLIME_RAIN_VICTORY,
                                level,
                                SlimeRainMoment.CHANCE_HARD_NOCONDITION_DEFEATED,  // 史莱姆王已击败
                                SlimeRainMoment.CHANCE_HARD_NOCONDITION            // 史莱姆王未击败
                        );
                    } else {
                        // 困难游戏阶段 + 非困难难度
                        // 使用普通模式的概率，但因为是困难游戏阶段，所以概率会降低
                        return PhaseUtils.getValueBasedOnPhase(
                                SlimeRainMoment.SLIME_RAIN_VICTORY,
                                level,
                                SlimeRainMoment.CHANCE_HARD_DEFEATED,  // 史莱姆王已击败，概率降低
                                SlimeRainMoment.CHANCE_HARD            // 史莱姆王未击败，概率降低
                        );
                    }
                } else {
                    // 普通游戏阶段
                    if (isHardMode) {
                        // 普通游戏阶段 + 困难难度（专家模式）
                        // 使用普通模式的概率，但因为是困难难度，所以概率会降低
                        return PhaseUtils.getValueBasedOnPhase(
                                SlimeRainMoment.SLIME_RAIN_VICTORY,
                                level,
                                SlimeRainMoment.CHANCE_NORMAL_NOCONDITION_DEFEATED,  // 史莱姆王已击败，概率降低
                                SlimeRainMoment.CHANCE_NORMAL_NOCONDITION            // 史莱姆王未击败，概率降低
                        );
                    } else {
                        // 普通游戏阶段 + 非困难难度
                        return PhaseUtils.getValueBasedOnPhase(
                                SlimeRainMoment.SLIME_RAIN_VICTORY,
                                level,
                                SlimeRainMoment.CHANCE_NORMAL_DEFEATED,  // 史莱姆王已击败
                                SlimeRainMoment.CHANCE_NORMAL            // 史莱姆王未击败
                        );
                    }
                }
            }
    );

}
