package org.confluence.mod.common.entity.animal;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HostileBunny extends Bunny {
    public HostileBunny(EntityType<? extends Bunny> type, Level level) {
        super(type, level);
        super.setVariant(Rabbit.Variant.EVIL);
        setCustomName(null);
        setBunnyVariant(Variant.CORRUPT);
    }

    @Override
    public void setVariant(Rabbit.Variant variant) {
        if (getVariant() != Rabbit.Variant.EVIL) super.setVariant(Rabbit.Variant.EVIL);
    }

    @Override
    public void setBunnyVariant(Variant variant) {
        boolean crimson = variant == Variant.VICIOUS;
        super.setBunnyVariant(crimson ? Variant.VICIOUS : Variant.CORRUPT);
        if (!level().isClientSide) {
            float healthRatio = getHealth() / getMaxHealth();
            getAttribute(Attributes.MAX_HEALTH).setBaseValue(crimson ? 75.0 : 70.0);
            getAttribute(Attributes.ARMOR).setBaseValue(crimson ? 5.0 : 4.0);
            getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(crimson ? 21.0 : 20.0);
            setHealth(getMaxHealth() * healthRatio);
        }
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable(getBunnyVariant() == Variant.VICIOUS ? "entity.confluence.vicious_bunny" : "entity.confluence.corrupt_bunny");
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        return target.hurt(damageSources().mobAttack(this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    protected void initializeSpawnVariant() {
        setBunnyVariant(CritterCorruption.selectsCrimson(this) ? Variant.VICIOUS : Variant.CORRUPT);
    }
}
