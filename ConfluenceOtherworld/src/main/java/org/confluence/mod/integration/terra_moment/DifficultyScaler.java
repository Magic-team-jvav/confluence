package org.confluence.mod.integration.terra_moment;

import com.xiaohunao.terra_moment.common.api.DifficultyScalerFunction;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.gameevent.GoblinArmyGameEvent;
import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
public class DifficultyScaler {
    public static void init() {
        DifficultyScalerFunction.setSlimeRainScaler((baseValue, level) ->
                KillBoard.INSTANCE.isDefeated(GoblinArmyGameEvent.KEY) ? 1.0F : 0.5F
        );
    }
}
