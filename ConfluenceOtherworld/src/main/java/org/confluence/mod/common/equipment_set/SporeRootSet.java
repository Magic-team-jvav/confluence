package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terraentity.init.TEAttributes;

public class SporeRootSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.SPORE_ROOT_HELMET)
                .bindHook(builder -> builder.addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.SPORE_ROOT_HELMET.getId(), 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .bindHook(builder -> builder.addBonus(TEAttributes.MINION_CAPACITY, new AttributeModifier(ArmorItems.SPORE_ROOT_HELMET.getId(), 1, AttributeModifier.Operation.ADD_VALUE)))
                .build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.CHEST, ArmorItems.SPORE_ROOT_CHESTPLATE)
                .bindHook(builder -> builder.addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.SPORE_ROOT_CHESTPLATE.getId(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.LEGS, ArmorItems.SPORE_ROOT_LEGGINGS)
                .bindHook(builder -> builder.addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.SPORE_ROOT_LEGGINGS.getId(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.FEET, ArmorItems.SPORE_ROOT_BOOTS)
                .bindHook(builder -> builder.addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.SPORE_ROOT_BOOTS.getId(), 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.SPORE_ROOT_HELMET,
                        VanillaEquippable.CHEST, ArmorItems.SPORE_ROOT_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.SPORE_ROOT_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.SPORE_ROOT_BOOTS
                )
                .bindHook(builder -> builder.addBonus(TEAttributes.MINION_CAPACITY, new AttributeModifier(Confluence.asResource("spore_root_set"), 1, AttributeModifier.Operation.ADD_VALUE)))
                .build());
    }
}
