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
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;

public class NinjaSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", critChanceBonus(VanillaEquippable.HEAD, ArmorItems.NINJA_HELMET));
        equippableGroup.addEquippableSet("chestplate", critChanceBonus(VanillaEquippable.CHEST, ArmorItems.NINJA_CHESTPLATE));
        equippableGroup.addEquippableSet("leggings", critChanceBonus(VanillaEquippable.LEGS, ArmorItems.NINJA_LEGGINGS));
        equippableGroup.addEquippableSet("boots", critChanceBonus(VanillaEquippable.FEET, ArmorItems.NINJA_BOOTS));

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.NINJA_HELMET,
                        VanillaEquippable.CHEST, ArmorItems.NINJA_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.NINJA_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.NINJA_BOOTS
                )
                .bindHook(builder -> builder.addBonus(Attributes.MOVEMENT_SPEED, new AttributeModifier(Confluence.asResource("ninja_set"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
    }

    private static EquipmentSetBranch critChanceBonus(VanillaEquippable slot, DeferredItem<ArmorItem> item) {
        return new EquipmentSetBranch.Builder().addEquippable(slot, item)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(item.getId(), 0.02, AttributeModifier.Operation.ADD_VALUE)))
                .build();
    }
}
