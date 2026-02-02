package org.confluence.terra_curio.common.item;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCArmorMaterials;
import org.confluence.terra_curio.common.init.TCTags;

public class DivingHelmet extends ArmorItem {
    private static final float DIVISION = 1.0F / 6.0F;

    public DivingHelmet() {
        super(TCArmorMaterials.DIVING, Type.HELMET, new Properties().fireResistant()
                .component(ConfluenceMagicLib.MOD_RARITY, ModRarity.GREEN)
                .durability(ArmorItem.Type.HELMET.getDurability(15))
        );
    }

    @Override
    public boolean isEnderMask(ItemStack stack, Player player, EnderMan endermanEntity) {
        return true;
    }

    public static float apply(LivingEntity living, DamageSource damageSource, float amount) {
        if (damageSource.is(DamageTypeTags.IS_DROWNING) && living.getItemBySlot(EquipmentSlot.HEAD).is(TCTags.DIVING)) {
            return amount * DIVISION;
        }
        return amount;
    }
}
