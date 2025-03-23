package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.common.init.item.ArmorItems;

public class HeimSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", new EquippableSetData.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.HEIM_HELMET)
                .bindHook(builder -> builder.addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.HEIM_HELMET.getId(), 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .bindHook(EBHookTypes.LIVING_BREATHE.get(), (owner, event) -> {
                    LivingEntity living = event.getEntity();
                    if (living.getAirSupply() > 0 && living.level().getGameTime() % 20 == 0) { // 延长5%
                        event.setConsumeAirAmount(0);
                    }
                })
                .build());
        equippableGroup.addEquippableSet("chestplate", new EquippableSetData.Builder()
                .addEquippable(VanillaEquippable.CHEST, ArmorItems.HEIM_CHESTPLATE)
                .bindHook(builder -> builder.addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.HEIM_CHESTPLATE.getId(), 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .bindHook(EBHookTypes.LIVING_SHIELD_BLOCK.get(), (owner, event) -> {
                    if (event.getBlocked()) {
                        LivingEntity living = event.getEntity();
                        living.getPersistentData().putLong("confluence:last_shield_block_time", living.level().getGameTime());
                    }
                })
                // todo tick hook
                .build());
//        equippableGroup.addEquippableSet("leggings", new EquippableSetData.Builder()
//                .addEquippable(VanillaEquippable.LEGS, ArmorItems.HEIM_LEGGINGS)
//                .bindHook(builder -> builder.addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.HEIM_LEGGINGS.getId(), 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
//                .build());
//        equippableGroup.addEquippableSet("boots", new EquippableSetData.Builder()
//                .addEquippable(VanillaEquippable.FEET, ArmorItems.HEIM_BOOTS)
//                .bindHook(builder -> builder.addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.HEIM_BOOTS.getId(), 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
//                .build());
    }
}
