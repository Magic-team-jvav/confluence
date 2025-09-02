package org.confluence.mod.common.equipment_set;

import com.google.common.collect.Multimap;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.hook.hooks.AfterLivingHurtEntityHook;
import com.xiaohunao.equipment_benediction.common.hook.hooks.LivingIncomingDamageHook;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEquipmentSets;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.init.TEAttributes;

public class HallowedSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        equippableGroup.addEquippableSet("mask", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.HALLOWED_MASK)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.HALLOWED_MASK.getId(), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.HALLOWED_MASK.getId(), 0.1, AttributeModifier.Operation.ADD_VALUE))
                        .addBonus(Attributes.ATTACK_SPEED, new AttributeModifier(ArmorItems.HALLOWED_MASK.getId(), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("headgear", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.HALLOWED_HEADGEAR)
                .bindHook(ModHookTypes.ADDITIONAL_MANA.get(), (owner, player, original) -> original + 100)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.HALLOWED_HEADGEAR.getId(), 0.12, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.HALLOWED_HEADGEAR.getId(), 0.12, AttributeModifier.Operation.ADD_VALUE))
                ).build());
        equippableGroup.addEquippableSet("hood", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.HALLOWED_HOOD)
                .bindHook(builder -> builder
                        .addBonus(TEAttributes.MINION_CAPACITY, new AttributeModifier(ArmorItems.HALLOWED_HOOD.getId(), 1, AttributeModifier.Operation.ADD_VALUE))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.HALLOWED_HOOD.getId(), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("helmet", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.HEAD, ArmorItems.HALLOWED_HELMET)
                .bindHook(builder -> builder
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.HALLOWED_HELMET.getId(), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.HALLOWED_HELMET.getId(), 0.08, AttributeModifier.Operation.ADD_VALUE))
                ).build());
        equippableGroup.addEquippableSet("chestplate", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.CHEST, ArmorItems.HALLOWED_CHESTPLATE)
                .bindHook(builder -> builder.addBonus(TCAttributes.getCriticalChance(), new AttributeModifier(ArmorItems.HALLOWED_CHESTPLATE.getId(), 0.07, AttributeModifier.Operation.ADD_VALUE))).build());
        equippableGroup.addEquippableSet("leggings", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.LEGS, ArmorItems.HALLOWED_LEGGINGS)
                .bindHook(builder -> builder
                        .addBonus(Attributes.ATTACK_DAMAGE, new AttributeModifier(ArmorItems.HALLOWED_CHESTPLATE.getId(), 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getRangedDamage(), new AttributeModifier(ArmorItems.HALLOWED_CHESTPLATE.getId(), 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TCAttributes.getMagicDamage(), new AttributeModifier(ArmorItems.HALLOWED_CHESTPLATE.getId(), 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                        .addBonus(TEAttributes.SUMMON_DAMAGE, new AttributeModifier(ArmorItems.HALLOWED_CHESTPLATE.getId(), 0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ).build());
        equippableGroup.addEquippableSet("boots", new EquipmentSetBranch.Builder()
                .addEquippable(VanillaWearable.FEET, ArmorItems.HALLOWED_BOOTS)
                .bindHook(builder -> builder.addBonus(Attributes.MOVEMENT_SPEED, new AttributeModifier(ArmorItems.HALLOWED_BOOTS.getId(), 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)))
                .build());

        LivingIncomingDamageHook protection = (owner, event) -> {
            if (event.getEntity() instanceof Player player && player.hasEffect(ModEffects.HOLY_PROTECTION)) {
                player.removeEffect(ModEffects.HOLY_PROTECTION);
                LibUtils.getOrCreatePersistedData(player).putLong("confluence:last_holy_protection", player.level().getGameTime());
                event.setCanceled(true);
            }
        };
        AfterLivingHurtEntityHook hurt = (owner, data) -> {
            Player attacker = data.attacker();
            if (attacker.level().getGameTime() - LibUtils.getOrCreatePersistedData(attacker).getLong("confluence:last_holy_protection") > 600) {
                attacker.addEffect(new MobEffectInstance(ModEffects.HOLY_PROTECTION, 600));
            }
        };
        equippableGroup.addEquippableSet("common_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, Ingredient.of(ArmorItems.HALLOWED_HELMET, ArmorItems.HALLOWED_MASK, ArmorItems.HALLOWED_HEADGEAR),
                        VanillaWearable.CHEST, ArmorItems.HALLOWED_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.HALLOWED_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.HALLOWED_BOOTS
                )
                .bindHook(EBHookTypes.LIVING_INCOMING_DAMAGE.get(), protection)
                .bindHook(EBHookTypes.AFTER_LIVING_HURT_ENTITY.get(), hurt)
                .build());
        equippableGroup.addEquippableSet("hood_set", new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD, ArmorItems.HALLOWED_HOOD,
                        VanillaWearable.CHEST, ArmorItems.HALLOWED_CHESTPLATE,
                        VanillaWearable.LEGS, ArmorItems.HALLOWED_LEGGINGS,
                        VanillaWearable.FEET, ArmorItems.HALLOWED_BOOTS
                )
                .bindHook(EBHookTypes.LIVING_INCOMING_DAMAGE.get(), protection)
                .bindHook(EBHookTypes.AFTER_LIVING_HURT_ENTITY.get(), hurt)
                .bindHook(builder -> builder.addBonus(TEAttributes.MINION_CAPACITY, new AttributeModifier(Confluence.asResource("hood_set"), 2, AttributeModifier.Operation.ADD_VALUE)))
                .build());
    }

    public static void checkHead(Entity attacker) { // todo
        if (attacker instanceof Player player && player.level().getGameTime() - LibUtils.getOrCreatePersistedData(player).getLong("confluence:last_holy_protection") > 600) {
            Multimap<EquipmentSet, EquipmentSetBranch> activatedEquipped = player.getData(EBAttachments.ENTITY_HOOK_MANAGER).getSetHookManager().getActivatedSetBranch();
            if (activatedEquipped.containsKey(ModEquipmentSets.HALLOWED_SET.get())) {
                player.addEffect(new MobEffectInstance(ModEffects.HOLY_PROTECTION, 600));
            }
        }
    }
}
