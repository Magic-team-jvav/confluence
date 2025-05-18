package org.confluence.mod.common.hook;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.entity.player.Player;

public interface FishingPowerHook extends IHook {
    float modifyFishingPower(IBenediction owner, Player player, float original);
}
