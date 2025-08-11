package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.api.event.LivingFreezeEvent;
import org.confluence.mod.common.hook.LivingFreezeHook;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.item.ArmorItems;

public class SnowSet extends EquipmentSet implements LivingFreezeHook {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("default", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.SNOW_CAPS,
                        VanillaEquippable.CHEST, ArmorItems.SNOW_SUITS,
                        VanillaEquippable.LEGS, ArmorItems.INSULATED_PANTS,
                        VanillaEquippable.FEET, ArmorItems.INSULATED_SHOES
                )
                .bindHook(ModHookTypes.LIVING_FREEZE.get(), this)
                .build());
        equippableGroup.addEquippableSet("pink",new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.PINK_SNOW_CAPS,
                        VanillaEquippable.CHEST, ArmorItems.PINK_SNOW_SUITS,
                        VanillaEquippable.LEGS, ArmorItems.INSULATED_PANTS,
                        VanillaEquippable.FEET, ArmorItems.INSULATED_SHOES
                )                .bindHook(ModHookTypes.LIVING_FREEZE.get(), this)
                .build());
    }

    @Override
    public void livingFreeze(IBenediction owner, LivingEntity self, LivingFreezeEvent.Pre event) {
        event.setCanFreeze(false);
    }
}
