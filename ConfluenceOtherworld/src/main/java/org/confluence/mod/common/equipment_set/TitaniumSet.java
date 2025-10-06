package org.confluence.mod.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.mod.common.entity.projectile.TitaniumShardsProjectile;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.init.TEAttributes;

public class TitaniumSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("headgear", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.TITANIUM_HEADGEAR)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.TITANIUM_HEADGEAR.getId(), 0.16, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.TITANIUM_HEADGEAR.getId(), 0.07, AttributeModifier.Operation.ADD_VALUE))
                )
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + 100)
                .build());
        equippableGroup.addEquippableSet("mask", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.TITANIUM_MASK)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.TITANIUM_MASK.getId(), 0.09, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(Attributes.ATTACK_SPEED, new AttributeModifier(ArmorItems.TITANIUM_MASK.getId(), 0.09, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.TITANIUM_MASK.getId(), 0.09, AttributeModifier.Operation.ADD_VALUE))
                ).build());
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.TITANIUM_HELMET)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.TITANIUM_HELMET.getId(), 0.07, AttributeModifier.Operation.ADD_VALUE))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.TITANIUM_HELMET.getId(), 0.16, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.CHEST, ArmorItems.TITANIUM_CHESTPLATE)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.TITANIUM_CHESTPLATE.getId(), 0.04, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.TITANIUM_CHESTPLATE.getId(), 0.04, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.TITANIUM_CHESTPLATE.getId(), 0.04, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.TITANIUM_CHESTPLATE.getId(), 0.04, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.TITANIUM_CHESTPLATE.getId(), 0.03, AttributeModifier.Operation.ADD_VALUE))
                ).build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.LEGS, ArmorItems.TITANIUM_LEGGINGS)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.TITANIUM_CHESTPLATE.getId(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.TITANIUM_CHESTPLATE.getId(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.TITANIUM_CHESTPLATE.getId(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.TITANIUM_CHESTPLATE.getId(), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.TITANIUM_CHESTPLATE.getId(), 0.03, AttributeModifier.Operation.ADD_VALUE))
                ).build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.FEET, ArmorItems.TITANIUM_BOOTS)
                .bindHook(builder -> builder.addBonus(Attributes.MOVEMENT_SPEED, new AttributeModifier(ArmorItems.TITANIUM_BOOTS.getId(), 0.06, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());

        equippableGroup.addEquippableSet("full_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, Ingredient.of(ArmorItems.TITANIUM_HEADGEAR, ArmorItems.TITANIUM_MASK, ArmorItems.TITANIUM_HELMET),
                        VanillaWearable.CHEST, ArmorItems.TITANIUM_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.TITANIUM_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.TITANIUM_BOOTS
                )
                .bindHook(EBHookTypes.AFTER_LIVING_HURT_ENTITY.get(), (owner, data) -> {
                    Player player = data.attacker();
                    if (player instanceof IServerPlayer serverPlayer &&
                            !(data.damageSource().getDirectEntity() instanceof TitaniumShardsProjectile) &&
                            !player.hasEffect(ModEffects.TITANIUM_BARRIER)
                    ) {
                        player.addEffect(new MobEffectInstance(ModEffects.TITANIUM_BARRIER, 200));
                        if (!serverPlayer.confluence$hasTitaniumShards()) {
                            TitaniumShardsProjectile projectile = new TitaniumShardsProjectile(player);
                            serverPlayer.confluence$setTitaniumShards(projectile);
                            player.level().addFreshEntity(projectile);
                        }
                    }
                })
                .build());
    }
}
