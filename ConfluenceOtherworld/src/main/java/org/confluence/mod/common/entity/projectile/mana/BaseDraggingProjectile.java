package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.api.ITrackType;
import org.confluence.mod.common.item.mana.BaseDraggingStaffItem;
import org.confluence.mod.util.track.variant.BasisTrack;

/**
 * 支持“按住拖拽、松开后追踪”两阶段行为的魔法弹幕基类。
 *
 * <p>{@link #shot} 决定弹幕是继续贴近施法者，还是进入自主追踪与碰撞阶段。该状态不能从
 * 当前速度或玩家是否仍在使用物品可靠推断，因此使用独立的当前格式根保存；缺失或损坏时
 * 复用玩家弹幕的安全失效通道。</p>
 */
public abstract class BaseDraggingProjectile extends AbstractManaProjectile {
    private static final String RUNTIME_TAG = "ConfluenceDraggingRuntime";
    private static final int RUNTIME_VERSION = 1;

    protected boolean shot;
    protected ITrackType trackType = new BasisTrack(90, 0.4F);

    public BaseDraggingProjectile(EntityType<? extends BaseDraggingProjectile> entityType, Level level) {
        super(entityType, level);
        withParticle(getParticleId());
    }

    @Override
    public void baseTick() {
        super.baseTick();

        LivingEntity owner = getLivingOwner();
        if (owner == null) return;

        if (shot) {
            doTracking();
        } else {
            dragOrShoot(owner);
        }
    }

    protected abstract int getCooldown();

    protected abstract BaseDraggingStaffItem<?> getDraggingStaff();

    protected abstract ResourceLocation getParticleId();

    protected void doTracking() {
        LivingEntity target = LibEntityUtils.getAABBAngleTarget(position(), position().add(getDeltaMovement().normalize().scale(10)), level(), this, getTrackingRange(), 30, this::canHitEntity);
        if (target != null) {
            Vec3 motion = getDeltaMovement();
            Vec3 dir = target.position().add(0, target.getEyeHeight() * 0.5f, 0).subtract(position());
            double angle = LibMathUtils.angleBetween(motion, dir);
            Vec3 movement = trackType.calDeltaMovement(getDeltaMovement(), dir, angle);
            setDeltaMovement(movement);
        }

        doSimpleMove();
    }

    protected int getTrackingRange() {
        return 50 * 2 / 3;
    }

    protected void dragOrShoot(LivingEntity owner) {
        if (owner.isUsingItem() && owner.getUseItem().is(getDraggingStaff())) {
            Vec3 vector = owner.getViewVector(1);
            setPos(owner.getX() + vector.x * 2, owner.getEyeY() - 0.1 + vector.y * 2, owner.getZ() + vector.z * 2);
            setDeltaMovement(Vec3.ZERO);
        } else {
            shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, getDefaultVelocity(), 0.0F);
            if (owner instanceof Player player) {
                player.getCooldowns().addCooldown(getDraggingStaff(), getCooldown());
            }
            this.shot = true;
        }
    }

    protected void doExplosion(double range, double knockback) {
        if (level().isClientSide) return;
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.VOICE, 1, 1);
        for (LivingEntity living : level().getEntities(EntityTypeTest.forClass(LivingEntity.class), new AABB(blockPosition()).inflate(range / 2), this::canHitEntity)) {
            doHurtAndKnockback(living, knockback, 0.2);
        }
        discard();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", RUNTIME_VERSION);
        runtime.putBoolean("Shot", shot);
        compound.put(RUNTIME_TAG, runtime);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (combatState().isInvalid()) {
            return;
        }

        // 1.20 的重写不迁移旧实体；阶段不明确时禁止重新发射和重复结算冷却。
        if (!compound.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) {
            combatState().invalidate("Missing or invalid dragging projectile runtime state");
            return;
        }
        CompoundTag runtime = compound.getCompound(RUNTIME_TAG);
        if (!runtime.contains("Version", Tag.TAG_INT)
                || runtime.getInt("Version") != RUNTIME_VERSION
                || !runtime.contains("Shot", Tag.TAG_BYTE)) {
            combatState().invalidate("Malformed dragging projectile runtime state");
            return;
        }
        shot = runtime.getBoolean("Shot");
    }
}
