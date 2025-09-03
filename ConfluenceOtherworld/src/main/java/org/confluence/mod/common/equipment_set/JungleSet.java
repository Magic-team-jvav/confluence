package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;

public class JungleSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.JUNGLE_HELMET)
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + 40)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.JUNGLE_HELMET.getId(), 0.06, AttributeModifier.Operation.ADD_VALUE)))
                .build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.CHEST, ArmorItems.JUNGLE_CHESTPLATE)
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + 20)
                .bindHook(builder -> builder.addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.JUNGLE_CHESTPLATE.getId(), 0.06, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.LEGS, ArmorItems.JUNGLE_LEGGINGS)
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + 10)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.JUNGLE_LEGGINGS.getId(), 0.03, AttributeModifier.Operation.ADD_VALUE)))
                .build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.FEET, ArmorItems.JUNGLE_BOOTS)
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + 10)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.JUNGLE_BOOTS.getId(), 0.03, AttributeModifier.Operation.ADD_VALUE)))
                .build());

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, ArmorItems.JUNGLE_HELMET,
                        VanillaWearable.CHEST, ArmorItems.JUNGLE_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.JUNGLE_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.JUNGLE_BOOTS
                )
                .bindHook(ModHookTypes.MANA_CONSUME.get(), (owner, itemStack, original) -> () -> original.getAsFloat() * 0.84F)
                .build());
    }
}
