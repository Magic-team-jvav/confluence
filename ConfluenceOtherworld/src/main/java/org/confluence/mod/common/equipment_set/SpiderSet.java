package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.util.TCUtils;
import org.confluence.terraentity.init.TEAttributes;

public class SpiderSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.HEAD, ArmorItems.SPIDER_HELMET)
                .bindHook(builder -> builder
                        .addBonus(TEAttributes.MINION_CAPACITY, new AttributeModifier(ArmorItems.SPIDER_HELMET.getId(), 1, AttributeModifier.Operation.ADD_VALUE))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.SPIDER_HELMET.getId(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.CHEST, ArmorItems.SPIDER_CHESTPLATE)
                .bindHook(builder -> builder
                        .addBonus(TEAttributes.MINION_CAPACITY, new AttributeModifier(ArmorItems.SPIDER_CHESTPLATE.getId(), 1, AttributeModifier.Operation.ADD_VALUE))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.SPIDER_CHESTPLATE.getId(), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.LEGS, ArmorItems.SPIDER_LEGGINGS)
                .bindHook(builder -> builder.addBonus(TEAttributes.MINION_CAPACITY, new AttributeModifier(ArmorItems.SPIDER_LEGGINGS.getId(), 1, AttributeModifier.Operation.ADD_VALUE))).build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaEquippable.FEET, ArmorItems.SPIDER_BOOTS)
                .bindHook(builder -> builder.addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.SPIDER_BOOTS.getId(), 0.06, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD, ArmorItems.SPIDER_HELMET,
                        VanillaEquippable.CHEST, ArmorItems.SPIDER_CHESTPLATE,
                        VanillaEquippable.LEGS, ArmorItems.SPIDER_LEGGINGS,
                        VanillaEquippable.FEET, ArmorItems.SPIDER_BOOTS
                )
                .bindHook(builder -> builder.addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(Confluence.asResource("spider_set"), 0.12, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .bindHook(EBHookTypes.ENTITY_INVULNERABILITY_CHECK.get(), (owner, event) -> {
                    if (TCUtils.isFire(event.getSource())) {
                        event.setInvulnerable(true);
                    }
                }).build());
    }
}
