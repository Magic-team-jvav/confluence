package org.confluence.mod.common.hook;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntSupplier;

public interface ManaConsumeHook extends IHook {
    IntSupplier onManaConsume(IBenediction owner, ItemStack itemStack, IntSupplier original);
}
