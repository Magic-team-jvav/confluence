package org.confluence.mod.common.equipment_set.hook;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.entity.player.Player;

public interface AdditionalManaHook extends IHook {
    int additional(IBenediction owner, Player player, int original);
}
