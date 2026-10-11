package org.confluence.mod.client.handler;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;
import org.confluence.mod.common.gameevent.BlizzardGameEvent;
import org.confluence.mod.common.gameevent.SandstormGameEvent;

/// 本地玩家受风。
///
/// 风系统本身**不影响任何实体**——平时无论风速多大玩家都不受影响。
/// 这个类只处理沙尘暴与暴风雪这两个事件：服务端改不动玩家的速度
/// （`ServerPlayer` 的移动由客户端权威，服务端 `setDeltaMovement` 不会传回客户端），
/// 所以玩家必须在这里用与 {@code WindPushedEvent.inWindZone} 相同的判定单独结算。
public final class WindPushHandler {
    /// 逆风阻力：顶着风走更慢（与原作沙尘暴里举步维艰的感觉一致）。
    private static final float SANDSTORM_HEADWIND = 0.03F;
    /// 顺风推力：风从背后推着你走。
    private static final float SANDSTORM_TAILWIND = 0.01F;
    private static final float BLIZZARD_COUPLING = 0.015F;

    /// 在客户端 tick 中调用（见 {@code GameClientEvents}）。
    public static void handle(LocalPlayer player) {
        if (!(player.level() instanceof ClientLevel level)) return;
        if (player.onGround() || player.isPassenger()) return;

        Vec3 wind = WeatherHandler.windVec3(); // 方块/tick，已含阵风与曝露度
        if (wind == Vec3.ZERO) return;

        BlockPos pos = player.blockPosition();
        float coupling;
        if (ClientGameEventSystem.isEventStarted(SandstormGameEvent.KEY) && SandstormGameEvent.INSTANCE.inWindZone(level, pos)) {
            Vec3 look = player.getLookAngle();
            boolean headwind = look.x * wind.x + look.z * wind.z < 0.0;
            coupling = headwind ? SANDSTORM_HEADWIND : SANDSTORM_TAILWIND;
        } else if (ClientGameEventSystem.isEventStarted(BlizzardGameEvent.KEY) && BlizzardGameEvent.INSTANCE.inWindZone(level, pos)) {
            coupling = BLIZZARD_COUPLING;
        } else {
            return;
        }
        player.push(wind.x * coupling, 0.0, wind.z * coupling);
    }

    private WindPushHandler() {}
}
