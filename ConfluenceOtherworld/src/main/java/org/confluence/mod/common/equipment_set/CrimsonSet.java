package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.neoforge.registries.DeferredItem;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.init.TEAttributes;

public class CrimsonSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", attackDamageBonus(VanillaEquippable.HEAD, ArmorItems.CRIMSON_HELMET));
        equippableGroup.addEquippableSet("chestplate", attackDamageBonus(VanillaEquippable.CHEST, ArmorItems.CRIMSON_CHESTPLATE));
        equippableGroup.addEquippableSet("leggings", attackDamageBonus(VanillaEquippable.LEGS, ArmorItems.CRIMSON_LEGGINGS));
        equippableGroup.addEquippableSet("boots", attackDamageBonus(VanillaEquippable.FEET, ArmorItems.CRIMSON_BOOTS));

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.CRIMSON_HELMET,
                        VanillaEquippable.CHEST, ArmorItems.CRIMSON_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.CRIMSON_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.CRIMSON_BOOTS
                )
                .bindHook(EBHookTypes.LIVING_HEAL.get(), (owner, original) -> original.setAmount(original.getAmount() * 1.5F))
                .build());
    }

    private static EquipmentSetBranch attackDamageBonus(VanillaEquippable slot, DeferredItem<ArmorItem> item) {
        return new EquipmentSetBranch.Builder().addEquippable(slot, item)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(item.getId(), 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(item.getId(), 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(item.getId(), 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(item.getId(), 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build();
    }
}
