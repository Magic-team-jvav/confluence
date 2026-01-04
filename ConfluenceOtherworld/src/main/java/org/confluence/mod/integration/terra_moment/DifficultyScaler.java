package org.confluence.mod.integration.terra_moment;

import com.xiaohunao.terra_moment.common.api.DifficultyScalerFunction;
import com.xiaohunao.terra_moment.common.init.TMMoments;
import org.confluence.mod.common.data.saved.KillBoard;

public class DifficultyScaler {
    public static void init() {
        DifficultyScalerFunction.setSlimeRainScaler((baseValue, level) ->
            KillBoard.INSTANCE.isDefeated(TMMoments.GOBLIN_ARMY.getKey()) ? 1.0F :0.5F
        );
    }
}
