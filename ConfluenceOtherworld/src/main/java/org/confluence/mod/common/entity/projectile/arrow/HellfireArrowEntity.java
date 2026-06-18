package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;

public class HellfireArrowEntity extends BaseArrowEntity {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/arrow/hellfire_arrow.png");
    private static final ResourceLocation PARTICLE = Confluence.asResource("ball_of_fire_trail");

    public HellfireArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public HellfireArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
    }

    @Override
    public double getBaseDamage() {
        return 5.6;
    }

    @Override
    protected int getLuminance() {
        return 8;
    }

    @Override
    protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) {
        target.setRemainingFireTicks(target.getRemainingFireTicks() + 100 - tickCount);
    }

    @Override
    protected ResourceLocation getParticleId() {
        return PARTICLE;
    }

    @Override
    public ResourceLocation getTexturePath() {
        return TEXTURE;
    }
}
