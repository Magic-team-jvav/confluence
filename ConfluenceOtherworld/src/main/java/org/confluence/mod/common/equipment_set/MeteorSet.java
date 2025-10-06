package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.neoforge.registries.DeferredItem;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.common.init.item.ManaWeaponItems;
import org.confluence.terra_curio.common.init.TCAttributes;

public class MeteorSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", magicDamageBonus(VanillaEquippable.HEAD, ArmorItems.METEOR_HELMET));
        equippableGroup.addEquippableSet("chestplate", magicDamageBonus(VanillaEquippable.CHEST, ArmorItems.METEOR_CHESTPLATE));
        equippableGroup.addEquippableSet("leggings", magicDamageBonus(VanillaEquippable.LEGS, ArmorItems.METEOR_LEGGINGS));
        equippableGroup.addEquippableSet("boots", magicDamageBonus(VanillaEquippable.FEET, ArmorItems.METEOR_BOOTS));

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.METEOR_HELMET,
                        VanillaEquippable.CHEST, ArmorItems.METEOR_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.METEOR_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.METEOR_BOOTS
                ).bindHook(ModHookTypes.MANA_CONSUME.get(), (owner, itemStack, original) -> {
                    if (itemStack.is(ManaWeaponItems.SPACE_GUN.get())) {
                        return () -> 0;
                    }
                    return original;
                }).build());
    }

    private static EquipmentSetBranch magicDamageBonus(VanillaEquippable slot, DeferredItem<? extends ArmorItem> item) {
        return new EquipmentSetBranch.Builder().addEquippable(slot, item)
                .bindHook(builder -> builder.addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(item.getId(), 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build();
    }
}
