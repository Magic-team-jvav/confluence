package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;

public class WizardSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("wizard_hat", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.WIZARD_HAT)
                .bindHook(builder -> builder.addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.WIZARD_HAT.getId(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());
        equippableGroup.addEquippableSet("magic_hat", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.MAGIC_HAT)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.MAGIC_HAT.getId(), 0.06, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.MAGIC_HAT.getId(), 0.06, AttributeModifier.Operation.ADD_VALUE))
                ).build());

        equippableGroup.addEquippableSet("amethyst_robe", robeBonus(ArmorItems.AMETHYST_ROBE, 20, 0.95F));
        equippableGroup.addEquippableSet("topaz_robe", robeBonus(ArmorItems.TOPAZ_ROBE, 40, 0.93F));
        equippableGroup.addEquippableSet("sapphire_robe", robeBonus(ArmorItems.SAPPHIRE_ROBE, 40, 0.91F));
        equippableGroup.addEquippableSet("emerald_robe", robeBonus(ArmorItems.JADE_ROBE, 60, 0.89F));
        equippableGroup.addEquippableSet("ruby_robe", robeBonus(ArmorItems.RUBY_ROBE, 60, 0.87F));
        equippableGroup.addEquippableSet("amethyst_robe", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.CHEST, ArmorItems.MYSTIC_ROBE)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.MYSTIC_ROBE.getId(), 0.06, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.MYSTIC_ROBE.getId(), 0.06, AttributeModifier.Operation.ADD_VALUE))
                )
                .bindHook(ModHookTypes.MANA_CONSUME.get(), (owner, itemStack, original) -> () -> original.get() * 0.9F)
                .build());
        equippableGroup.addEquippableSet("diamond_robe", robeBonus(ArmorItems.DIAMOND_ROBE, 80, 0.85F));
        equippableGroup.addEquippableSet("amber_robe", robeBonus(ArmorItems.AMBER_ROBE, 60, 0.87F));

        Ingredient robes = Ingredient.of(
                ArmorItems.AMETHYST_ROBE,
                ArmorItems.TOPAZ_ROBE,
                ArmorItems.SAPPHIRE_ROBE,
                ArmorItems.JADE_ROBE,
                ArmorItems.RUBY_ROBE,
                ArmorItems.MYSTIC_ROBE,
                ArmorItems.DIAMOND_ROBE,
                ArmorItems.AMBER_ROBE
        );
        equippableGroup.addEquippableSet("wizard_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.WIZARD_HAT,
                        VanillaEquippable.CHEST, robes
                )
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(Confluence.asResource("wizard_set"), 0.1, AttributeModifier.Operation.ADD_VALUE)))
                .build());
        equippableGroup.addEquippableSet("magic_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.MAGIC_HAT,
                        VanillaEquippable.CHEST, robes
                )
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + 100)
                .build());
    }

    private static EquipmentSetBranch robeBonus(DeferredItem<ArmorItem> item, int additionalMana, float manaConsume) {
        return new EquipmentSetBranch.Builder().addEquippable(VanillaEquippable.CHEST, item)
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + additionalMana)
                .bindHook(ModHookTypes.MANA_CONSUME.get(), (owner, itemStack, original) -> () -> original.get() * manaConsume)
                .build();
    }
}
