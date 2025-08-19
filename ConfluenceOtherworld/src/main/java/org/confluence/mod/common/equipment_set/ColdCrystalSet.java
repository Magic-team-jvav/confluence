package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.init.TEEffects;

public class ColdCrystalSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.COLD_CRYSTAL_HELMET)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.COLD_CRYSTAL_HELMET.getId(), 0.04, AttributeModifier.Operation.ADD_VALUE)))
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + 20)
                .build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.CHEST, ArmorItems.COLD_CRYSTAL_CHESTPLATE)
                .bindHook(builder -> builder.addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.COLD_CRYSTAL_CHESTPLATE.getId(), 0.04, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + 20)
                .build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.LEGS, ArmorItems.COLD_CRYSTAL_LEGGINGS)
                .bindHook(builder -> builder.addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.COLD_CRYSTAL_LEGGINGS.getId(), 0.04, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.FEET, ArmorItems.COLD_CRYSTAL_BOOTS)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.COLD_CRYSTAL_BOOTS.getId(), 0.04, AttributeModifier.Operation.ADD_VALUE)))
                .build());

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.COLD_CRYSTAL_HELMET,
                        VanillaEquippable.CHEST, ArmorItems.COLD_CRYSTAL_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.COLD_CRYSTAL_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.COLD_CRYSTAL_BOOTS
                )
                .bindHook(EBHookTypes.BEFORE_LIVING_DAMAGE.get(), (owner, event) -> {
                    if (event.getSource().is(Tags.DamageTypes.IS_MAGIC)) {
                        event.getEntity().addEffect(new MobEffectInstance(TEEffects.FROST_BURN, 100));
                    }
                })
                .build());
    }
}
