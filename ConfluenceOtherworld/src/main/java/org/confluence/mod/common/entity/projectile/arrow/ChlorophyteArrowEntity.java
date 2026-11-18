package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

/// 叶绿箭：打在方块上会反弹（最多 4 次），反弹后伤害降低 33%。
///
/// 与其它射弹不同：没有可追踪目标时它**原路返回**（转向 180°），不会按碰撞面做镜像反射；
/// 半径内存在敌怪时改为朝该敌怪的方向反弹。命中敌怪则与普通箭一样直接消失（不穿透）。
public class ChlorophyteArrowEntity extends BaseArrowEntity {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/arrow/chlorophyte_arrow.png");
    /// 最大反弹次数。
    private static final int MAX_BOUNCES = 4;
    /// 反弹后寻找敌怪的方向修正半径。
    private static final double HOMING_RANGE = 16.0;
    /// 每次反弹后伤害保留的比例（降低 33%）。
    private static final float POWER_AFTER_BOUNCE = 0.67F;
    /// 反弹后的最小速度，避免弹几下就停住。
    private static final double MIN_BOUNCE_SPEED = 1.2;
    private static final String BOUNCE_TAG = "ChlorophyteBounces";

    private int bounces;

    public ChlorophyteArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public ChlorophyteArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
    }

    @Override
    protected double getAdditionalKnockback() {
        return 1.75;
    }

    @Override
    protected float getCalculatedDamage() {
        float damage = super.getCalculatedDamage();
        for (int i = 0; i < bounces; ++i) {
            damage *= POWER_AFTER_BOUNCE;
        }
        return damage;
    }

    /// 反弹：先用 BaseArrowEntity 的落空回调（若有），然后自己决定新方向而不是停在方块上。
    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (bounces >= MAX_BOUNCES) {
            super.onHitBlock(result);
            return;
        }
        ++bounces;
        Vec3 direction = bounceDirection(result);
        // 从碰撞面法线方向推出一点，避免下一 tick 立刻又撞到同一格方块。
        Vec3 normal = Vec3.atLowerCornerOf(result.getDirection().getNormal());
        setPos(result.getLocation().add(normal.scale(0.35)));
        xOld = getX();
        yOld = getY();
        zOld = getZ();
        setDeltaMovement(direction);
        setYRot((float) (Math.atan2(direction.x, direction.z) * (180.0 / Math.PI)) - 90.0F);
        yRotO = getYRot();
        playSound(SoundEvents.ARROW_HIT, 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
    }

    /// 有敌怪就朝敌怪，没有就原路返回（180°）。
    private Vec3 bounceDirection(BlockHitResult result) {
        double speed = Math.max(getDeltaMovement().length(), MIN_BOUNCE_SPEED);
        LivingEntity target = findTarget();
        if (target != null) {
            Vec3 aim = target.getBoundingBox().getCenter().subtract(position());
            if (aim.lengthSqr() > 1.0E-4) {
                return aim.normalize().scale(speed);
            }
        }
        Vec3 back = getDeltaMovement().scale(-1.0);
        if (back.lengthSqr() < 1.0E-6) {
            back = Vec3.atLowerCornerOf(result.getDirection().getNormal());
        }
        return back.normalize().scale(speed);
    }

    private @Nullable LivingEntity findTarget() {
        LivingEntity nearest = null;
        double best = HOMING_RANGE * HOMING_RANGE;
        AABB box = new AABB(blockPosition()).inflate(HOMING_RANGE);
        for (Entity candidate : LibEntityUtils.queryHitCandidates(level(), this, box, entity -> LibEntityUtils.effectTarget(entity) != null)) {
            LivingEntity living = LibEntityUtils.effectTarget(candidate);
            if (living == null || living == getOwner() || !living.isAlive() || living.isSpectator()) continue;
            if (!LibEntityUtils.allowsAttack(getOwner(), candidate)) continue;
            double distance = living.distanceToSqr(this);
            if (distance < best) {
                best = distance;
                nearest = living;
            }
        }
        return nearest;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(BOUNCE_TAG, bounces);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.bounces = tag.getInt(BOUNCE_TAG);
    }

    @Override
    public ResourceLocation getTexturePath() {
        return TEXTURE;
    }
}
