package org.confluence.mod.common.entity.animal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HostileBunny extends Bunny {
    private static final Variant[] HOSTILE_VARIANTS = {Variant.CORRUPT, Variant.VICIOUS};

    public HostileBunny(EntityType<? extends Bunny> type, Level level) {
        super(type, level);
        super.setVariant(Rabbit.Variant.EVIL);
        setCustomName(null);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Bunny.createAttributes().add(Attributes.ATTACK_DAMAGE, 4.0).add(Attributes.FOLLOW_RANGE, 16.0);
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
        setBunnyVariant(CritterVariantUtil.uniform(random, HOSTILE_VARIANTS));
    }
}
