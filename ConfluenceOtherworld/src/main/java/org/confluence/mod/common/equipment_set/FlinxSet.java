package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terraentity.init.TEAttributes;

public class FlinxSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.CHEST, ArmorItems.FLINX_FUR_COAT)
                .bindHook(builder -> builder.addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.FLINX_FUR_COAT.getId(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .bindHook(builder -> builder.addBonus(TEAttributes.MINION_CAPACITY, new AttributeModifier(ArmorItems.FLINX_FUR_COAT.getId(), 1, AttributeModifier.Operation.ADD_VALUE)))
                .build());
    }
}
