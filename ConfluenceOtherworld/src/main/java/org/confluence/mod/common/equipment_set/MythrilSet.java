package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.init.TEAttributes;

public class MythrilSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("hood", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.MYTHRIL_HOOD)
                .bindHook(builder -> builder.addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.MYTHRIL_HOOD.getId(), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + 60)
                .build());
        equippableGroup.addEquippableSet("hat", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.MYTHRIL_HAT)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.MYTHRIL_HAT.getId(), 0.12, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.MYTHRIL_HAT.getId(), 0.07, AttributeModifier.Operation.ADD_VALUE))
                ).build());
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.MYTHRIL_HELMET)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.MYTHRIL_HELMET.getId(), 0.08, AttributeModifier.Operation.ADD_VALUE))
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.MYTHRIL_HELMET.getId(), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.CHEST, ArmorItems.MYTHRIL_CHESTPLATE)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.COBALT_LEGGINGS.getId(), 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.COBALT_LEGGINGS.getId(), 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.COBALT_LEGGINGS.getId(), 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.COBALT_LEGGINGS.getId(), 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.LEGS, ArmorItems.MYTHRIL_LEGGINGS)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.MYTHRIL_LEGGINGS.getId(), 0.05, AttributeModifier.Operation.ADD_VALUE)))
                .build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.FEET, ArmorItems.MYTHRIL_BOOTS)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.MYTHRIL_BOOTS.getId(), 0.05, AttributeModifier.Operation.ADD_VALUE)))
                .build());

        equippableGroup.addEquippableSet("helmet_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, ArmorItems.MYTHRIL_HELMET,
                        VanillaWearable.CHEST, ArmorItems.MYTHRIL_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.MYTHRIL_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.MYTHRIL_BOOTS
                )
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(Confluence.asResource("helmet_set"), 0.1, AttributeModifier.Operation.ADD_VALUE)))
                .build());
        equippableGroup.addEquippableSet("hood_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, ArmorItems.MYTHRIL_HOOD,
                        VanillaWearable.CHEST, ArmorItems.MYTHRIL_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.MYTHRIL_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.MYTHRIL_BOOTS
                )
                .bindHook(ModHookTypes.MANA_CONSUME.get(), (owner, itemStack, original) -> () -> original.getAsFloat() * 0.83F)
                .build());
        equippableGroup.addEquippableSet("hat_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, ArmorItems.MYTHRIL_HAT,
                        VanillaWearable.CHEST, ArmorItems.MYTHRIL_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.MYTHRIL_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.MYTHRIL_BOOTS
                )
                .bindHook(ModHookTypes.SKIP_AMMO_CONSUME.get(), (owner, player, ammoStack) -> player.getRandom().nextFloat() < 0.2F)
                .build());
    }
}
