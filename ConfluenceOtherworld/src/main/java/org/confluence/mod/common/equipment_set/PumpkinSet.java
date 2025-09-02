package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.init.TEAttributes;

public class PumpkinSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        ResourceLocation id = Confluence.asResource("pumpkin_set");
        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, ArmorItems.PUMPKIN_HELMET,
                        VanillaWearable.CHEST, ArmorItems.PUMPKIN_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.PUMPKIN_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.PUMPKIN_BOOTS
                )
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
    }
}
