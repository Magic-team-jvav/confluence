package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.common.init.item.ArmorItems;

public class HeimSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.HEIM_HELMET)
                .bindHook(builder -> builder.addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.HEIM_HELMET.getId(), 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .bindHook(EBHookTypes.LIVING_BREATHE.get(), (owner, event) -> {
                    LivingEntity living = event.getEntity();
                    if (living.getAirSupply() > 0 && living.level().getGameTime() % 20 == 0) { // 延长5%
                        event.setConsumeAirAmount(0);
                    }
                })
                .build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.CHEST, ArmorItems.HEIM_CHESTPLATE)
//                .bindDelayHook(EBHookTypes.LIVING_SHIELD_BLOCK.get(), (owner, event) -> {
//                    event.getEntity().addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 1));
//                }, 60)
                .build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.LEGS, ArmorItems.HEIM_LEGGINGS)
                .bindHook(builder -> builder.addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.HEIM_LEGGINGS.getId(), 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.FEET, ArmorItems.HEIM_BOOTS)
                .bindHook(builder -> builder.addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.HEIM_BOOTS.getId(), 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());

        equippableGroup.addEquippableSet("heim_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, ArmorItems.HEIM_HELMET,
                        VanillaWearable.CHEST, ArmorItems.HEIM_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.HEIM_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.HEIM_BOOTS
                )
                .bindHook(EBHookTypes.UNEQUIP_EQUIPMENT.get(), (owner, changeContext) -> {
                    changeContext.livingEntity().removeEffect(MobEffects.ABSORPTION);
                })
//                .bindTimerHook(EBHookTypes.EQUIP_EQUIPMENT.get(), (Owner, changeContext) -> {
//                    changeContext.livingEntity().addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 40, 1));
//                }, 80)
                .build()
        );
    }
}
