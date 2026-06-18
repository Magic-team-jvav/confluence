package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;

public class FlamingArrowEntity extends BaseArrowEntity {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/arrow/flaming_arrow.png");
    private static final ResourceLocation PARTICLE = Confluence.asResource("flaming_arrow_flame");

    public FlamingArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public FlamingArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
    }

    @Override
    public double getBaseDamage() {
        return 4.4;
    }

    @Override
    protected int getLuminance() {
        return 12;
    }

    @Override
    protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) {
        target.setRemainingFireTicks(target.getRemainingFireTicks() + 200 - tickCount);
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
