package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.FlowerPetalProjectile;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.init.TEAttributes;

public class OrichalcumSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("headgear", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.ORICHALCUM_HEADGEAR)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.ORICHALCUM_HEADGEAR.getId(), 0.18, AttributeModifier.Operation.ADD_VALUE)))
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + 80)
                .build());
        equippableGroup.addEquippableSet("mask", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.ORICHALCUM_MASK)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.ORICHALCUM_MASK.getId(), 0.11, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(Attributes.ATTACK_SPEED, new AttributeModifier(ArmorItems.ORICHALCUM_MASK.getId(), 0.011, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(Attributes.MOVEMENT_SPEED, new AttributeModifier(ArmorItems.ORICHALCUM_MASK.getId(), 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.ORICHALCUM_HELMET)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.ORICHALCUM_HELMET.getId(), 0.15, AttributeModifier.Operation.ADD_VALUE))
                        .addBonus(Attributes.MOVEMENT_SPEED, new AttributeModifier(ArmorItems.ORICHALCUM_HELMET.getId(), 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.CHEST, ArmorItems.ORICHALCUM_CHESTPLATE)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.ORICHALCUM_BOOTS.getId(), 0.06, AttributeModifier.Operation.ADD_VALUE))).build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.LEGS, ArmorItems.ORICHALCUM_LEGGINGS)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.COBALT_LEGGINGS.getId(), 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.COBALT_LEGGINGS.getId(), 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.COBALT_LEGGINGS.getId(), 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.COBALT_LEGGINGS.getId(), 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                )
                .build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.FEET, ArmorItems.ORICHALCUM_BOOTS)
                .bindHook(builder -> builder.addBonus(Attributes.MOVEMENT_SPEED, new AttributeModifier(ArmorItems.ORICHALCUM_BOOTS.getId(), 0.11, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, ArmorItems.ORICHALCUM_HELMET,
                        VanillaWearable.CHEST, ArmorItems.ORICHALCUM_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.ORICHALCUM_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.ORICHALCUM_BOOTS
                )
                .bindHook(EBHookTypes.AFTER_LIVING_HURT_ENTITY.get(), (owner, data) -> {
                    Player attacker = data.attacker();
                    FlowerPetalProjectile projectile = new FlowerPetalProjectile(attacker);
                    Vec3 position = data.victim().position();
                    Vec3 offset = position.offsetRandom(attacker.getRandom(), 10);
                    projectile.setPos(offset);
                    projectile.shoot(position.x - offset.x, position.y - offset.x, position.z - offset.z, 0.4F, 0.0F);
                    attacker.level().addFreshEntity(projectile);
                })
                .build());
    }
}
