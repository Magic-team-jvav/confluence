package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

/// 寒霜箭：冰雪弓把任何箭矢替换成的高速箭（不注册物品形态）。
///
/// 除了飞得更快，没有别的特殊效果。
public class FrostArrowEntity extends BaseArrowEntity {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/arrow/frost_arrow.png");
    /// 相对普通箭矢的速度倍率。
    private static final double SPEED_SCALE = 1.35;

    public FrostArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public FrostArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        setDeltaMovement(getDeltaMovement().scale(SPEED_SCALE));
    }

    @Override
    public ResourceLocation getTexturePath() {
        return TEXTURE;
    }
}
