package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.mod.common.init.item.ArmorItems;

public class MiningSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        EquippableSetData setData1 = new EquippableSetData.Builder()
                .addEquippable(
                        VanillaEquippable.CHEST , Ingredient.of(ArmorItems.MINING_CHESTPLATE.get())
                )
                .bindHook(EBHookTypes.BREAK_SPEED.get(), (owner, entity, state, originalSpeed) -> originalSpeed * 1.15F)
                .build();

        EquippableSetData setData2 = new EquippableSetData.Builder()
                .addEquippable(
                        VanillaEquippable.LEGS , Ingredient.of(ArmorItems.MINING_LEGGINGS.get())
                )
                .bindHook(EBHookTypes.BREAK_SPEED.get(), (owner, entity, state,originalSpeed) -> originalSpeed * 1.15F)
                .build();

        EquippableSetData setData3 = new EquippableSetData.Builder()
                .addEquippable(
                        VanillaEquippable.FEET , Ingredient.of(ArmorItems.MINING_BOOTS.get())
                )
                .bindHook(EBHookTypes.BREAK_SPEED.get(), (owner, entity, state,originalSpeed) -> originalSpeed * 1.15F)
                .build();

        equippableGroup.addEquippableSet("chest", setData1)
                .addEquippableSet("legs", setData2)
                .addEquippableSet("feet", setData3);

    }
}
