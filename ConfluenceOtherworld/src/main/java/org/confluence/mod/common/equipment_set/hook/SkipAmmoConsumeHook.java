package org.confluence.mod.common.equipment_set.hook;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface SkipAmmoConsumeHook extends IHook {
    boolean shouldSkipConsume(IBenediction owner, LivingEntity shooter, ItemStack ammoStack);
}
