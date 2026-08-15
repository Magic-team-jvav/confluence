package org.confluence.mod.api.summon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 由玩家拥有、能够触发召唤伤害与鞭痕效果的实体。
 *
 * <p>本体召唤物和附属模组召唤物都只需公开所有者 UUID。伤害事件会在目标所在服务端维度解析玩家，
 * 不要求实体长期保存强引用，也不会把坐骑、Boss 仆从等普通从属实体误判为召唤物。</p>
 */
public interface OwnedSummon {
    UUID getSummonOwnerId();

    /**
     * 在当前服务端维度解析在线所有者；所有者离线或不在该维度时返回 {@code null}。
     */
    default @Nullable Player resolveSummonOwner(ServerLevel level) {
        return level.getPlayerByUUID(getSummonOwnerId());
    }
}
