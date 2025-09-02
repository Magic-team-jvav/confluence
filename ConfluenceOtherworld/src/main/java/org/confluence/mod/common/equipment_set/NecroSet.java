package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.neoforge.registries.DeferredItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;

public class NecroSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", rangedDamageBonus(VanillaWearable.HEAD, ArmorItems.NECRO_HELMET, 0.05));
        equippableGroup.addEquippableSet("chestplate", rangedDamageBonus(VanillaWearable.CHEST, ArmorItems.NECRO_CHESTPLATE, 0.05));
        equippableGroup.addEquippableSet("leggings", rangedDamageBonus(VanillaWearable.LEGS, ArmorItems.NECRO_LEGGINGS, 0.025));
        equippableGroup.addEquippableSet("boots", rangedDamageBonus(VanillaWearable.FEET, ArmorItems.NECRO_BOOTS, 0.025));

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, ArmorItems.NECRO_HELMET,
                        VanillaWearable.CHEST, ArmorItems.NECRO_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.NECRO_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.NECRO_BOOTS
                )
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(Confluence.asResource("necro_set"), 0.1, AttributeModifier.Operation.ADD_VALUE)))
                .build());
    }

    private static EquipmentSetBranch rangedDamageBonus(VanillaWearable slot, DeferredItem<? extends ArmorItem> item, double value) {
        return new EquipmentSetBranch.Builder().addEquippable(slot, item)
                .bindHook(builder -> builder.addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(item.getId(), value, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build();
    }
}
