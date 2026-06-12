package org.confluence.mod.mixed;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;

public interface IPlayer extends SelfGetter<Player> {
    /**
     * @deprecated 使用新的 {@link BaseFlailEntity} 系统，通过实体查找代替直接引用
     */
    @Deprecated
    void confluence$setFlailBall(BaseFlailEntity entity);

    /**
     * @deprecated 使用新的 {@link BaseFlailEntity} 系统，通过实体查找代替直接引用
     */
    @Deprecated
    BaseFlailEntity confluence$getFlailBall();

    void confluence$setCurrentBait(ItemStack bait);

    ItemStack confluence$getCurrentBait();

    static IPlayer of(Player player) {
        return (IPlayer) player;
    }
}
