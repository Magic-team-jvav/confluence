package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.item.ArmorItems;

public class CactusSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, ArmorItems.CACTUS_HELMET,
                        VanillaWearable.CHEST, ArmorItems.CACTUS_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.CACTUS_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.CACTUS_BOOTS
                )
                .bindHook(EBHookTypes.LIVING_INCOMING_DAMAGE.get(), (owner, event) -> {
                    if (event.getSource().getEntity() instanceof LivingEntity living) {
                        living.hurt(living.damageSources().cactus(), switch (living.level().getDifficulty()) {
                            case EASY -> 3;
                            case NORMAL -> 8;
                            case HARD -> 11;
                            default -> 0;
                        });
                    }
                })
                .build());
    }
}
