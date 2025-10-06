package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terraentity.init.TEAttributes;

public class ObsidianSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.OBSIDIAN_HELMET)
                .bindHook(builder -> builder.addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.OBSIDIAN_HELMET.getId(), 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.CHEST, ArmorItems.OBSIDIAN_CHESTPLATE)
                .bindHook(builder -> builder.addBonus(TEAttributes.MINION_CAPACITY, new AttributeModifier(ArmorItems.OBSIDIAN_CHESTPLATE.getId(), 1, AttributeModifier.Operation.ADD_VALUE)))
                .build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.LEGS, ArmorItems.OBSIDIAN_LEGGINGS)
                .bindHook(builder -> builder.addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.OBSIDIAN_LEGGINGS.getId(), 0.04, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.FEET, ArmorItems.OBSIDIAN_BOOTS)
                .bindHook(builder -> builder.addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.OBSIDIAN_BOOTS.getId(), 0.04, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, ArmorItems.OBSIDIAN_HELMET,
                        VanillaWearable.CHEST, ArmorItems.OBSIDIAN_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.OBSIDIAN_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.OBSIDIAN_BOOTS
                )
                .bindHook(builder -> builder
                        .addBonus(TEAttributes.WHIP_RANGE, new AttributeModifier(Confluence.asResource("obsidian_set"), 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(Confluence.asResource("obsidian_set"), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(Attributes.ATTACK_SPEED, new AttributeModifier(Confluence.asResource("obsidian_set"), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
    }
}
