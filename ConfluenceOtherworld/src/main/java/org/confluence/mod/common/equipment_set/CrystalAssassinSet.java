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

public class CrystalAssassinSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.CRYSTAL_ASSASSIN_HELMET)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.CRYSTAL_ASSASSIN_HELMET.getId(), 0.05, AttributeModifier.Operation.ADD_VALUE)))
                .bindHook(ModHookTypes.MANA_CONSUME.get(), (owner, itemStack, original) -> () -> (int) (original.getAsInt() * 0.9))
                .build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.CHEST, ArmorItems.CRYSTAL_ASSASSIN_CHESTPLATE)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.CRYSTAL_ASSASSIN_CHESTPLATE.getId(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.CRYSTAL_ASSASSIN_CHESTPLATE.getId(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.CRYSTAL_ASSASSIN_CHESTPLATE.getId(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.CRYSTAL_ASSASSIN_CHESTPLATE.getId(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                )
                .bindHook(ModHookTypes.SKIP_AMMO_CONSUME.get(), (owner, shooter, ammoStack) -> shooter.getRandom().nextFloat() < 0.1F)
                .build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.LEGS, ArmorItems.CRYSTAL_ASSASSIN_LEGGINGS)
                .bindHook(builder -> builder.addBonus(Attributes.ATTACK_SPEED, new AttributeModifier(ArmorItems.CRYSTAL_ASSASSIN_LEGGINGS.getId(), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.FEET, ArmorItems.CRYSTAL_ASSASSIN_BOOTS)
                .bindHook(builder -> builder.addBonus(Attributes.MOVEMENT_SPEED, new AttributeModifier(ArmorItems.CRYSTAL_ASSASSIN_BOOTS.getId(), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.CRYSTAL_ASSASSIN_HELMET,
                        VanillaEquippable.CHEST, ArmorItems.CRYSTAL_ASSASSIN_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.CRYSTAL_ASSASSIN_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.CRYSTAL_ASSASSIN_BOOTS
                )
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(Confluence.asResource("crystal_assassin_set"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(Confluence.asResource("crystal_assassin_set"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(Confluence.asResource("crystal_assassin_set"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(Confluence.asResource("crystal_assassin_set"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(Confluence.asResource("crystal_assassin_set"), 0.1, AttributeModifier.Operation.ADD_VALUE))
                )
                .build());
    }
}
