package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.init.TEAttributes;

public class CobaltSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("mask", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.COBALT_MASK)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.COBALT_MASK.getId(), 0.1, AttributeModifier.Operation.ADD_VALUE))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.COBALT_MASK.getId(), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("hat", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.COBALT_HAT)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.COBALT_HAT.getId(), 0.09, AttributeModifier.Operation.ADD_VALUE))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.COBALT_HAT.getId(), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                )
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + 40)
                .build());
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.COBALT_HELMET)
                .bindHook(builder -> builder
                        .addBonus(Attributes.MOVEMENT_SPEED, new AttributeModifier(ArmorItems.COBALT_HELMET.getId(), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.COBALT_HELMET.getId(), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.CHEST, ArmorItems.COBALT_CHESTPLATE)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.COBALT_CHESTPLATE.getId(), 0.05, AttributeModifier.Operation.ADD_VALUE)))
                .build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.LEGS, ArmorItems.COBALT_LEGGINGS)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.COBALT_LEGGINGS.getId(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.COBALT_LEGGINGS.getId(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.COBALT_LEGGINGS.getId(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TEAttributes.MARK_DAMAGE, new AttributeModifier(ArmorItems.COBALT_LEGGINGS.getId(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.FEET, ArmorItems.COBALT_BOOTS)
                .bindHook(builder -> builder.addBonus(Attributes.MOVEMENT_SPEED, new AttributeModifier(ArmorItems.COBALT_BOOTS.getId(), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());

        equippableGroup.addEquippableSet("helmet_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.COBALT_HELMET,
                        VanillaEquippable.CHEST, ArmorItems.COBALT_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.COBALT_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.COBALT_BOOTS
                )
                .bindHook(builder -> builder.addBonus(Attributes.ATTACK_SPEED, new AttributeModifier(Confluence.asResource("helmet_set"), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("mask_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.COBALT_MASK,
                        VanillaEquippable.CHEST, ArmorItems.COBALT_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.COBALT_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.COBALT_BOOTS
                )
                .bindHook(ModHookTypes.SKIP_AMMO_CONSUME.get(), (owner, player, ammoStack) -> player.getRandom().nextFloat() < 0.2F)
                .build());
        equippableGroup.addEquippableSet("hat_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.COBALT_HAT,
                        VanillaEquippable.CHEST, ArmorItems.COBALT_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.COBALT_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.COBALT_BOOTS
                )
                .bindHook(ModHookTypes.MANA_CONSUME.get(), (owner, itemStack, original) -> () -> original.getAsFloat() * 0.86F)
                .build());
    }
}
