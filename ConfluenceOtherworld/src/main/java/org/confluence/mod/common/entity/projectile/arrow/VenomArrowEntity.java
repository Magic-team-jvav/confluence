package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.jetbrains.annotations.Nullable;

/// 毒液箭：命中时附加 10 秒酸性毒液与 2 秒烈性酸液。
public class VenomArrowEntity extends BaseArrowEntity {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/arrow/venom_arrow.png");
    private static final int ACID_VENOM_TICKS = 10 * 20;
    private static final int VENOM_TICKS = 2 * 20;

    public VenomArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public VenomArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
    }

    @Override
    protected double getAdditionalKnockback() {
        return 2.1;
    }

    @Override
    protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) {
        target.addEffect(new MobEffectInstance(ModEffects.ACID_VENOM.get(), ACID_VENOM_TICKS), owner);
        target.addEffect(new MobEffectInstance(ModEffects.VENOM.get(), VENOM_TICKS), owner);
    }

    @Override
    public ResourceLocation getTexturePath() {
        return TEXTURE;
    }
}
