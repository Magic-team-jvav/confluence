package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.tags.DamageTypeTags;
import org.confluence.mod.common.init.item.ArmorItems;

public class AshSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.ASH_HELMET,
                        VanillaEquippable.CHEST, ArmorItems.ASH_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.ASH_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.ASH_BOOTS
                )
                .bindHook(EBHookTypes.BEFORE_LIVING_DAMAGE.get(), (owner, event) -> {
                    if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
                        event.setNewDamage(event.getNewDamage() * 0.5F);
                    }
                })
                .build());
    }
}
