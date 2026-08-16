package org.confluence.mod.common.init;

import net.minecraft.advancements.CriteriaTriggers;
import org.confluence.mod.common.advancement.ShimmerTransmutationTrigger;
import org.confluence.terra_curio.common.advancement.CuriosEquippedTrigger;

public final class ModAdvancements {
    public static void init() {}

    public static final ShimmerTransmutationTrigger SHIMMER_TRANSMUTATION = CriteriaTriggers.register(new ShimmerTransmutationTrigger());

    /// 1.20 的 TerraCurio 暴露了触发器实例并在装备事件中调用它，但没有把实例注册进原版触发器
    /// 表。进度资源属于本体，因此由本体启动阶段补齐注册；不修改 TerraCurio，也不把玩法放入
    /// PortLib。反向同步 1.21 时应删除这一版本局部补偿。
    public static final CuriosEquippedTrigger CURIOS_EQUIPPED =
            CriteriaTriggers.register(CuriosEquippedTrigger.INSTANCE);
}
