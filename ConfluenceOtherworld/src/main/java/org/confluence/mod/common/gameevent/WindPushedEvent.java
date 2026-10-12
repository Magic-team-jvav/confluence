package org.confluence.mod.common.gameevent;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.wind.WindField;
import org.confluence.mod.util.OverworldUtils;

/// 会**推实体**的天气事件（目前只有沙尘暴与暴风雪）。
///
/// 风系统本身**不影响任何实体**：平时无论风速多大，掉落物、箭、生物、玩家的运动学表现都完全一致。
/// 实体受风只发生在本接口的实现者激活期间，且由事件自己判定范围与系数。
///
/// 玩家不在这里处理：`ServerPlayer` 的移动由客户端权威，服务端 `setDeltaMovement` 不会传回客户端，
/// 所以本地玩家由客户端 {@code WindPushHandler} 用同一套范围判定单独结算。
public interface WindPushedEvent extends GameEvent {
    /// 该位置是否在本事件的受力范围内。两端共用（服务端结算非玩家实体，客户端结算本地玩家）。
    boolean inWindZone(Level level, BlockPos pos);

    /// 该实体的推力系数（施加到 `deltaMovement` 的风向量比例）。
    float couplingFor(Entity entity);

    /// 本事件是否配置为推实体。
    default boolean windPushEnabled() {
        return true;
    }

    default void pushWindOn(Entity entity) {
        if (!windPushEnabled()) return;
        if (!(entity.level() instanceof ServerLevel level)) return;
        if (level.dimension() != OverworldUtils.dimension()) return;
        if (entity instanceof Player) return; // 客户端结算
        if (entity.onGround() || entity.isPassenger() || entity.isVehicle()) return;

        Vec3 wind = WindField.windAt(level, entity.position());
        if (wind == Vec3.ZERO) return;
        if (!inWindZone(level, entity.blockPosition())) return;

        entity.setDeltaMovement(entity.getDeltaMovement().add(wind.scale(couplingFor(entity))));
        entity.hurtMarked = true; // 让服务端速度能被同步给客户端
    }
}
