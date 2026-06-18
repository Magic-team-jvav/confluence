package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;

public class ShimmerArrowEntity extends BaseArrowEntity {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/arrow/shimmer_arrow.png");

    public ShimmerArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public ShimmerArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
    }
    @Override
    public double getBaseDamage() {
        return 5.4;
    }

    @Override

    @Override
    public double getDefaultGravity() {
        return -0.05;
    }

    @Override
    protected int getAutoDiscardTick() {
        return 1200;
    }

    @Override
    public ResourceLocation getTexturePath() {
        return TEXTURE;
    }
}
