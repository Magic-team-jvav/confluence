package org.confluence.mod.common.equipment_set.hook;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.util.FloatSupplier;

public interface ManaConsumeHook extends IHook {
    FloatSupplier onManaConsume(IBenediction owner, ItemStack itemStack, FloatSupplier original);
}
