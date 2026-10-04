package org.confluence.mod.common.item.sword;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.jetbrains.annotations.Nullable;

public class MomentumSwordItem extends BaseSwordItem {
    private static final String MOMENTUM_KEY = "confluence:sword_momentum";

    public MomentumSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, SwordDefinition.Builder builder) {
        super(tier, rarity, rawDamage, rawSpeed, builder);
    }

    @Override
    protected void onInventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;
        LibUtils.updateItemStackNbt(stack, tag -> {
            float bonus = tag.getFloat(MOMENTUM_KEY);
            if (bonus > 0.0F && level.getGameTime() % 20 == 0) {
                tag.putFloat(MOMENTUM_KEY, Math.max(bonus - 0.3F, 0.0F));
            }
        });
    }

    @Override
    public float modifyDamage(ItemStack stack, DamageSource source, @Nullable Entity attacker, LivingEntity victim, float amount) {
        CompoundTag tag = LibUtils.getItemStackNbtNoCopy(stack);
        return amount * (1.0F + tag.getFloat(MOMENTUM_KEY));
    }

    @Override
    protected void afterHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        LibUtils.updateItemStackNbt(stack, tag -> {
            float bonus = tag.getFloat(MOMENTUM_KEY);
            if (bonus < 0.5F) tag.putFloat(MOMENTUM_KEY, Math.min(bonus + 0.12F, 0.5F));
        });
    }
}
