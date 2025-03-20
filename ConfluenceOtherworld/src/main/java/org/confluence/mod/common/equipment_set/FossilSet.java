package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;

public class FossilSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", new EquippableSetData.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.FOSSIL_HELMET)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.FOSSIL_HELMET.getId(), 0.04, AttributeModifier.Operation.ADD_VALUE)))
                .build());
        equippableGroup.addEquippableSet("chestplate", new EquippableSetData.Builder()
                .addEquippable(VanillaEquippable.CHEST, ArmorItems.FOSSIL_CHESTPLATE)
                .bindHook(builder -> builder.addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.FOSSIL_CHESTPLATE.getId(), 0.025, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("leggings", new EquippableSetData.Builder()
                .addEquippable(VanillaEquippable.LEGS, ArmorItems.FOSSIL_LEGGINGS)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.FOSSIL_LEGGINGS.getId(), 0.04, AttributeModifier.Operation.ADD_VALUE)))
                .build());
        equippableGroup.addEquippableSet("boots", new EquippableSetData.Builder()
                .addEquippable(VanillaEquippable.FEET, ArmorItems.FOSSIL_BOOTS)
                .bindHook(builder -> builder.addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.FOSSIL_BOOTS.getId(), 0.025, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());

        equippableGroup.addEquippableSet("full_set", new EquippableSetData.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.FOSSIL_HELMET,
                        VanillaEquippable.CHEST, ArmorItems.FOSSIL_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.FOSSIL_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.FOSSIL_BOOTS
                )
                .bindHook(EBHookTypes.LIVING_GET_PROJECTILE.get(), (owner, event) -> {
                    ItemStack projectileItemStack = event.getProjectileItemStack();
                    if (!projectileItemStack.isEmpty() && event.getEntity().getRandom().nextFloat() < 0.2F) {
                        event.setProjectileItemStack(projectileItemStack.copy());
                    }
                }).build());
    }
}
