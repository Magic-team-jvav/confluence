package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terraentity.init.TEAttributes;

public class BeeSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.BEE_HELMET)
                .bindHook(builder -> builder.addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.BEE_HELMET.getId(), 0.04, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .bindHook(builder -> builder.addBonus(TEAttributes.MINION_CAPACITY, new AttributeModifier(ArmorItems.BEE_HELMET.getId(), 1, AttributeModifier.Operation.ADD_VALUE)))
                .build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.CHEST, ArmorItems.BEE_CHESTPLATE)
                .bindHook(builder -> builder.addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.BEE_CHESTPLATE.getId(), 0.04, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.LEGS, ArmorItems.BEE_LEGGINGS)
                .bindHook(builder -> builder.addBonus(TEAttributes.MINION_CAPACITY, new AttributeModifier(ArmorItems.BEE_LEGGINGS.getId(), 1, AttributeModifier.Operation.ADD_VALUE)))
                .build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.FEET, ArmorItems.BEE_BOOTS)
                .bindHook(builder -> builder.addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.BEE_BOOTS.getId(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.BEE_HELMET,
                        VanillaEquippable.CHEST, ArmorItems.BEE_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.BEE_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.BEE_BOOTS
                )
                .bindHook(builder -> builder.addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(Confluence.asResource("bee_set"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
    }
}
