package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terra_curio.util.TCUtils;

public class MoltenSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.MOLTEN_HELMET)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.MOLTEN_HELMET.getId(), 0.07, AttributeModifier.Operation.ADD_VALUE)))
                .build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.CHEST, ArmorItems.MOLTEN_CHESTPLATE)
                .bindHook(builder -> builder.addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.MOLTEN_CHESTPLATE.getId(), 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.LEGS, ArmorItems.MOLTEN_LEGGINGS)
                .bindHook(builder -> builder.addBonus(Attributes.ATTACK_SPEED, new AttributeModifier(ArmorItems.MOLTEN_LEGGINGS.getId(), 0.035, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.FEET, ArmorItems.MOLTEN_BOOTS)
                .bindHook(builder -> builder.addBonus(Attributes.ATTACK_SPEED, new AttributeModifier(ArmorItems.MOLTEN_BOOTS.getId(), 0.035, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.MOLTEN_HELMET,
                        VanillaEquippable.CHEST, ArmorItems.MOLTEN_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.MOLTEN_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.MOLTEN_BOOTS
                )
                .bindHook(builder -> builder.addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(Confluence.asResource("molten_set"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .bindHook(EBHookTypes.ENTITY_INVULNERABILITY_CHECK.get(), (owner, event) -> {
                    if (TCUtils.isFire(event.getSource())) {
                        event.setInvulnerable(true);
                    }
                }).build());
    }
}
