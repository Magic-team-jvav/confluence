package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.neoforge.registries.DeferredItem;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.item.ArmorItems;

// todo
public class AnglerSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", fishingPowerBonus(VanillaWearable.HEAD, ArmorItems.ANGLER_HAT));
        equippableGroup.addEquippableSet("chestplate", fishingPowerBonus(VanillaWearable.CHEST, ArmorItems.ANGLER_VEST));
        equippableGroup.addEquippableSet("leggings", fishingPowerBonus(VanillaWearable.LEGS, ArmorItems.ANGLER_PANTS));
    }

    private static EquipmentSetBranch fishingPowerBonus(VanillaWearable slot, DeferredItem<? extends ArmorItem> item) {
        return new EquipmentSetBranch.Builder().addEquippable(slot, item)
                .bindHook(ModHookTypes.FISHING_POWER.get(), (owner, player, original) -> original * 1.05F)
                .build();
    }
}
