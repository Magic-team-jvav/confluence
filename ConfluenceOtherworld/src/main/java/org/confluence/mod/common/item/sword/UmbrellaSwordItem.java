package org.confluence.mod.common.item.sword;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;

public class UmbrellaSwordItem extends GeoSwordItem {
    public UmbrellaSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, SwordDefinition.Builder builder) {
        super(tier, rarity, rawDamage, rawSpeed, builder);
    }

    @Override
    protected void onInventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && isSelected && entity instanceof LivingEntity living && !living.swinging) {
            living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 2, 2, false, false, false));
        }
    }
}
