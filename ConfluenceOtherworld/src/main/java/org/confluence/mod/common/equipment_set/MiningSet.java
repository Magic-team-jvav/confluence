package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.neoforge.registries.DeferredItem;
import org.confluence.mod.common.init.item.ArmorItems;

public class MiningSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("chestplate", blockBreakSpeedBonus(VanillaEquippable.CHEST, ArmorItems.MINING_CHESTPLATE));
        equippableGroup.addEquippableSet("leggings", blockBreakSpeedBonus(VanillaEquippable.LEGS, ArmorItems.MINING_LEGGINGS));
        equippableGroup.addEquippableSet("boots", blockBreakSpeedBonus(VanillaEquippable.FEET, ArmorItems.MINING_BOOTS));
    }

    private static EquipmentSetBranch blockBreakSpeedBonus(VanillaEquippable slot, DeferredItem<ArmorItem> item) {
        return new EquipmentSetBranch.Builder().addEquippable(slot, item)
                .bindHook(builder -> builder.addBonus(Attributes.BLOCK_BREAK_SPEED, new AttributeModifier(item.getId(), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build();
    }
}
