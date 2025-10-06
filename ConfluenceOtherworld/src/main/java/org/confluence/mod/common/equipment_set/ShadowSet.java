package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.neoforge.registries.DeferredItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;

public class ShadowSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", critChanceBonus(VanillaWearable.HEAD, ArmorItems.SHADOW_HELMET));
        equippableGroup.addEquippableSet("chestplate", critChanceBonus(VanillaWearable.CHEST, ArmorItems.SHADOW_CHESTPLATE));
        equippableGroup.addEquippableSet("leggings", critChanceBonus(VanillaWearable.LEGS, ArmorItems.SHADOW_LEGGINGS));
        equippableGroup.addEquippableSet("boots", critChanceBonus(VanillaWearable.FEET, ArmorItems.SHADOW_BOOTS));

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, ArmorItems.SHADOW_HELMET,
                        VanillaWearable.CHEST, ArmorItems.SHADOW_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.SHADOW_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.SHADOW_BOOTS
                )
                .bindHook(builder -> builder.addBonus(Attributes.MOVEMENT_SPEED, new AttributeModifier(Confluence.asResource("shadow_set"), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
    }

    private static EquipmentSetBranch critChanceBonus(VanillaWearable slot, DeferredItem<? extends ArmorItem> item) {
        return new EquipmentSetBranch.Builder().addEquippable(slot, item)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(item.getId(), 0.035, AttributeModifier.Operation.ADD_VALUE)))
                .build();
    }
}
