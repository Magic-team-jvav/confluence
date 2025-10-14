package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.init.TEAttributes;

public class PalladiumSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("mask", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.PALLADIUM_MASK)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.PALLADIUM_MASK.getId(), 0.12, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(Attributes.ATTACK_SPEED, new AttributeModifier(ArmorItems.PALLADIUM_MASK.getId(), 0.12, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("headgear", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.PALLADIUM_HEADGEAR)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.PALLADIUM_HEADGEAR.getId(), 0.09, AttributeModifier.Operation.ADD_VALUE))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.PALLADIUM_HEADGEAR.getId(), 0.09, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                )
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + 60)
                .build());
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.PALLADIUM_HELMET)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.PALLADIUM_HELMET.getId(), 0.09, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.PALLADIUM_HELMET.getId(), 0.09, AttributeModifier.Operation.ADD_VALUE))
                ).build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.CHEST, ArmorItems.PALLADIUM_CHESTPLATE)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.PALLADIUM_CHESTPLATE.getId(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.PALLADIUM_CHESTPLATE.getId(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.PALLADIUM_CHESTPLATE.getId(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.PALLADIUM_CHESTPLATE.getId(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.PALLADIUM_CHESTPLATE.getId(), 0.02, AttributeModifier.Operation.ADD_VALUE))
                ).build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.LEGS, ArmorItems.PALLADIUM_LEGGINGS)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.PALLADIUM_LEGGINGS.getId(), 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.PALLADIUM_LEGGINGS.getId(), 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.PALLADIUM_LEGGINGS.getId(), 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.PALLADIUM_LEGGINGS.getId(), 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.FEET, ArmorItems.PALLADIUM_BOOTS)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.PALLADIUM_LEGGINGS.getId(), 0.01, AttributeModifier.Operation.ADD_VALUE)))
                .build());

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, Ingredient.of(ArmorItems.PALLADIUM_MASK, ArmorItems.PALLADIUM_HEADGEAR, ArmorItems.PALLADIUM_HELMET),
                        VanillaWearable.CHEST, ArmorItems.PALLADIUM_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.PALLADIUM_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.PALLADIUM_BOOTS
                )
                .bindHook(EBHookTypes.AFTER_LIVING_HURT_ENTITY.get(), (owner, data) -> data.attacker().addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1)))
                .build());
    }
}
