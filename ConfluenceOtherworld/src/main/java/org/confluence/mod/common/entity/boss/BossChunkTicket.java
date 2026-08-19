package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/// 管理单场 Boss 遭遇的临时区块票据。
///
/// <p>Boss 的脱战计时依赖服务端实体持续 tick。若最后一名玩家死亡或离开后，Boss 所在区块
/// 立刻卸载，{@link BaseBoss#DISENGAGE_TICKS} 就会被冻结，Boss 可能永久残留。因此战斗实体
/// 存在期间，以本体当前区块为中心维持一个强制 tick 的区域票据。</p>
///
/// <p>该能力属于具体玩法生命周期，不属于 PortLib。Forge 1.20.1 与 NeoForge 1.21.1 在此处
/// 都直接提供相同的原版 {@code ServerChunkCache} 区域票据 API；只有未来平台签名真正分叉时，
/// 才应把两行平台调用下沉为 PortLib 薄桥。</p>
final class BossChunkTicket {
    /// 距离 4 使中心区块及其对角相邻区块都达到实体 ticking 层级。
    /// 这样即使 Boss 位于区块边角，环绕本体活动的持久从属也不会在战斗中途被卸载。
    /// 票据仍会随本体迁移，并在遭遇结束后释放。
    static final int REGION_DISTANCE = 4;
    /// 即使异常路径漏掉主动释放，停止刷新 15 秒后票据也会自行过期。
    /// 正常脱战宽限为 10 秒，因此故障保险必须略长于宽限时间。
    private static final int FAILSAFE_TIMEOUT_TICKS = 300;
    /// 保持包内可见，供同包的 GameTest 直接核对底层区块管理器中的真实票据。
    /// 这里不公开为玩法 API，外部代码仍只通过 Boss 生命周期间接管理票据。
    static final TicketType<UUID> TYPE = TicketType.create("confluence:boss_encounter", UUID::compareTo, FAILSAFE_TIMEOUT_TICKS);

    private final UUID bossId;
    private @Nullable ServerLevel ticketLevel;
    private @Nullable ChunkPos ticketedChunk;
    private int ticketedDistance;

    BossChunkTicket(UUID bossId) {
        this.bossId = bossId;
    }

    /// 刷新当前票据。Boss 跨区块时先取得新区块票据，再释放旧票据，避免迁移帧出现卸载空窗。
    void refresh(Entity boss, int regionDistance) {
        if (!(boss.level() instanceof ServerLevel currentLevel)) {
            release();
            return;
        }

        refresh(currentLevel, boss.chunkPosition(), regionDistance);
    }

    /// 刷新指定区块的票据。巨型 Boss 可在本体迁移前预载落点，或用少量票据
    /// 组合出非正方形战斗区域，避免为了覆盖长条实体而加载整片无关区块。
    void refresh(ServerLevel currentLevel, ChunkPos currentChunk, int regionDistance) {

        if (ticketLevel != currentLevel || !currentChunk.equals(ticketedChunk) || ticketedDistance != regionDistance) {
            currentLevel.getChunkSource().addRegionTicket(TYPE, currentChunk, regionDistance, bossId, true);
            release();
            ticketLevel = currentLevel;
            ticketedChunk = currentChunk;
            ticketedDistance = regionDistance;
            return;
        }

        // addOrGet 会更新同一票据的创建 tick，从而刷新故障保险超时。
        currentLevel.getChunkSource().addRegionTicket(TYPE, currentChunk, regionDistance, bossId, true);
    }

    /// 主动释放当前票据；重复调用安全。
    void release() {
        if (ticketLevel != null && ticketedChunk != null) {
            ticketLevel.getChunkSource().removeRegionTicket(TYPE, ticketedChunk, ticketedDistance, bossId, true);
        }
        ticketLevel = null;
        ticketedChunk = null;
        ticketedDistance = 0;
    }

    boolean isActive() {
        return ticketLevel != null && ticketedChunk != null;
    }

    @Nullable ChunkPos ticketedChunk() {
        return ticketedChunk;
    }
}
