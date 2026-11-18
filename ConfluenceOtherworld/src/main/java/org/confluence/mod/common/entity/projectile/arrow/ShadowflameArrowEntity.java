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

/// 暗影焰箭：暗影焰弓把任何箭矢替换成的特殊箭（不注册物品形态）。
///
/// 命中时附加暗影焰；比耀星之箭再慢一点（耀星之箭是 ×0.8，这里 ×0.75），
/// 受重力影响也比普通箭略小；最多穿过 2 个敌怪，击中第 3 个时消失。
public class ShadowflameArrowEntity extends BaseArrowEntity {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/arrow/shadowflame_arrow.png");
    private static final int SHADOWFLAME_TICKS = 5 * 20;
    /// 飞行速度：耀星之箭是 0.8，这里再少一点。
    private static final double SPEED_SCALE = 0.75;
    /// 受重力影响比普通箭（0.05）略小。
    private static final double GRAVITY = 0.04;

    public ShadowflameArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public ShadowflameArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
    }

    @Override
    public double getDefaultGravity() {
        return GRAVITY;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        setDeltaMovement(getDeltaMovement().scale(SPEED_SCALE));
    }

    /// 可穿过 2 个敌怪，命中第 3 个后消失。
    @Override
    protected int getPenetrationCount() {
        return 3;
    }

    @Override
    protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) {
        target.addEffect(new MobEffectInstance(ModEffects.SHADOWFLAME, SHADOWFLAME_TICKS), owner);
    }

    @Override
    public ResourceLocation getTexturePath() {
        return TEXTURE;
    }
}
