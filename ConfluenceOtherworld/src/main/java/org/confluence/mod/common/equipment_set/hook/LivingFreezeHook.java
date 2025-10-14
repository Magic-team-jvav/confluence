package org.confluence.mod.common.equipment_set.hook;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.api.event.LivingFreezeEvent;

public interface LivingFreezeHook extends IHook {
    void livingFreeze(IBenediction owner, LivingEntity self, LivingFreezeEvent event);
}
