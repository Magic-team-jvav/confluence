package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ArmorItems;

public class GladiatorSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.GLADIATOR_HELMET,
                        VanillaEquippable.CHEST, ArmorItems.GLADIATOR_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.GLADIATOR_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.GLADIATOR_BOOTS
                )
                .bindHook(builder -> builder.addBonus(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(Confluence.asResource("gladiator_set"), 1.0, AttributeModifier.Operation.ADD_VALUE)))
                .build());
    }
}
