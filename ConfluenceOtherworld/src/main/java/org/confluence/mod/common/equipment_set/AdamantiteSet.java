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

public class AdamantiteSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("headgear", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.ADAMANTITE_HEADGEAR)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.ADAMANTITE_HEADGEAR.getId(), 0.12, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.ADAMANTITE_HEADGEAR.getId(), 0.12, AttributeModifier.Operation.ADD_VALUE))
                )
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + 80)
                .build());
        equippableGroup.addEquippableSet("mask", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.ADAMANTITE_MASK)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.ADAMANTITE_MASK.getId(), 0.14, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.ADAMANTITE_MASK.getId(), 0.10, AttributeModifier.Operation.ADD_VALUE))
                ).build());
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.ADAMANTITE_HELMET)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.ADAMANTITE_HELMET.getId(), 0.07, AttributeModifier.Operation.ADD_VALUE))
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.ADAMANTITE_HELMET.getId(), 0.14, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.CHEST, ArmorItems.ADAMANTITE_CHESTPLATE)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.ADAMANTITE_CHESTPLATE.getId(), 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.ADAMANTITE_CHESTPLATE.getId(), 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.ADAMANTITE_CHESTPLATE.getId(), 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TEAttributes.MARK_DAMAGE, new AttributeModifier(ArmorItems.ADAMANTITE_CHESTPLATE.getId(), 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.LEGS, ArmorItems.ADAMANTITE_LEGGINGS)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.ADAMANTITE_LEGGINGS.getId(), 0.07, AttributeModifier.Operation.ADD_VALUE)))
                .build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.FEET, ArmorItems.ADAMANTITE_BOOTS)
                .bindHook(builder -> builder.addBonus(Attributes.MOVEMENT_SPEED, new AttributeModifier(ArmorItems.ADAMANTITE_BOOTS.getId(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());

        equippableGroup.addEquippableSet("helmet_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.ADAMANTITE_HELMET,
                        VanillaEquippable.CHEST, ArmorItems.ADAMANTITE_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.ADAMANTITE_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.ADAMANTITE_BOOTS
                )
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_SPEED, new AttributeModifier(Confluence.asResource("helmet_set"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(Attributes.MOVEMENT_SPEED, new AttributeModifier(Confluence.asResource("helmet_set"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("mask_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.ADAMANTITE_MASK,
                        VanillaEquippable.CHEST, ArmorItems.ADAMANTITE_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.ADAMANTITE_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.ADAMANTITE_BOOTS
                )
                .bindHook(ModHookTypes.SKIP_AMMO_CONSUME.get(), (owner, player, ammoStack) -> player.getRandom().nextFloat() < 0.25F)
                .build());
        equippableGroup.addEquippableSet("headgear_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.ADAMANTITE_HEADGEAR,
                        VanillaEquippable.CHEST, ArmorItems.ADAMANTITE_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.ADAMANTITE_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.ADAMANTITE_BOOTS
                )
                .bindHook(ModHookTypes.MANA_CONSUME.get(), (owner, itemStack, original) -> () -> original.getAsFloat() * 0.81F)
                .build());
    }
}
