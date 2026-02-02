package org.confluence.terraentity.entity.boss.wallofflesh;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.lib.util.LibUtils;
import org.confluence.terraentity.api.entity.ICollisionAttackEntity;
import org.confluence.terraentity.api.entity.IMovablePartEntity;
import org.confluence.terraentity.network.s2c.SyncWallOfFleshTargetPacket;
import org.confluence.terraentity.utils.AdapterUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class WallOfFleshPart extends PartEntity<WallOfFlesh> implements ICollisionAttackEntity, IMovablePartEntity {
    public final WallOfFlesh parentMob;
    public final String name;
    private final EntityDimensions size;
    public LivingEntity target;
    public int stareCount = 10;
    public float stareYaw = 0;
    public float starePitch = 0;
    public float stareStartYaw = 0;
    public float stareStartPitch = 0;

    public WallOfFleshPart(WallOfFlesh parentMob, String name, float width, float height) {
        super(parentMob);
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
        this.parentMob = parentMob;
        this.name = name;
        this.collisionProperties.detectInternal = 10;
    }

    @Override
    public void tick() {}

    public void changeTarget(LivingEntity target) {
        if (this.target == null) {
            this.stareStartYaw = 0.0f;
            this.stareStartPitch = 0.0f;
            this.stareCount = 10;
        } else {
            this.stareStartYaw = this.stareYaw;
            this.stareStartPitch = this.starePitch;
            this.stareCount = 0;
        }
        this.target = target;

        if (!this.level().isClientSide()) {
            int partIndex = this.parentMob.subEntities.indexOf(this);
            if (partIndex >= 0) {
                AdapterUtils.sendToAllPlayers(new SyncWallOfFleshTargetPacket(
                        this.parentMob.getId(),
                        partIndex,
                        target != null ? target.getId() : 0
                ));
            }
        }
    }

    protected abstract void tickPart(double offsetX, double offsetY, double offsetZ);

    public float lerpYaw(float partialTick) {
        return Mth.lerp(Mth.clamp(this.stareCount + partialTick, 0, 10) / 10f, this.stareStartYaw, this.stareYaw);
    }

    public float lerpPitch(float partialTick) {
        return Mth.lerp(Mth.clamp(this.stareCount + partialTick, 0, 10) / 10f, this.stareStartPitch, this.starePitch);
    }

    @Override
    public float getYRot() {
        if (this.parentMob != null) {
            return this.parentMob.getYRot();
        }
        return super.getYRot();
    }

    @Override
    public float getXRot() {
        if (this.parentMob != null) {
            return this.parentMob.getXRot();
        }
        return super.getXRot();
    }

    public boolean hasLineOfSight(Entity entity) {
        return this.level().clip(new ClipContext(this.getEyePosition(), entity.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    public void findTarget() {
        if (level().isClientSide()) {
            if (this.target == null) {
                this.starePitch = wrapRotation(this.starePitch, this.stareStartPitch);
                if (this.getParent().deathTime <= 0) {
                    this.stareCount = Math.max(0, this.stareCount - 1);
                }
            } else if (this.getParent().deathTime <= 0) {
                stareCount = Math.min(11, stareCount + 1);
            }
        } else {
            if (this.target != null && (!this.target.isAlive() || !this.canBeAttack(this.target))) {
                this.changeTarget(null);
            }
            if (this.getParent().tickCount % 25 == this.getId() % 25) {
                float r = 120;

                LivingEntity living = null;
                ;
                for (LivingEntity e : this.getParent().getNearbyPlayers()) {
                    boolean isPlayer = e instanceof Player;
                    if (e instanceof Player player && (player.isCreative() || player.isSpectator())) {
                        continue;
                    }
                    if (!this.canBeAttack(e)) {
                        continue;
                    }
                    Vec3 forwardDir = this.parentMob.getForward();
                    Vec3 toTargetHorizontal = e.position().subtract(this.position()).normalize();

                    boolean isTarget = e.isAlive()
                            && forwardDir.dot(toTargetHorizontal) >= 0
                            && e.position().distanceToSqr(this.position()) < r * r * 1.2
                            && e.position().subtract(this.position()).horizontalDistanceSqr() <= r * r;
                    if (isTarget && isPlayer) {
                        living = e;
                        break;
                    }
                }
                this.changeTarget(living);
            }
            doCollisionAttack(
                    e -> e instanceof LivingEntity living && LibUtils.getOwner(e) != getParent() && living.canBeSeenAsEnemy(),
                    parentMob::doHurtTarget
            );
            if (this.target == null) {
                if (this.getParent().deathTime <= 0) {
                    this.stareCount = Math.max(0, this.stareCount - 1);
                }
            } else if (this.getParent().deathTime <= 0) {
                stareCount = Math.min(11, stareCount + 1);
            }
        }
    }

    private float wrapRotation(float current, float target) {
        while (target - current > Math.PI / 2) {
            current += Math.PI;
        }
        while (target - current < -Math.PI / 2) {
            current -= Math.PI;
        }
        return current;
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundTag compound) {
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Nullable
    public ItemStack getPickResult() {
        return this.parentMob.getPickResult();
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        return !this.isInvulnerableTo(source) && this.parentMob.hurt(this, source, amount);
    }

    public boolean canBeAttack(@NotNull LivingEntity target) {
        return true;
    }

    @Override
    public boolean is(@Nonnull Entity entity) {
        return this == entity || this.parentMob == entity;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(@Nonnull ServerEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@Nonnull Pose pose) {
        return this.size;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    protected ICollisionAttackEntity.CollisionProperties collisionProperties = new ICollisionAttackEntity.CollisionProperties(5, 20, 0);

    @Override
    public ICollisionAttackEntity.CollisionProperties getCollisionProperties() {
        return collisionProperties;
    }

    @Override
    public boolean shouldDoCollision() {
        return this.isAlive();
    }

    protected abstract void onParentChangeState(int state);
}
