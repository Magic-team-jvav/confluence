package org.confluence.mod.common.entity.boss.hillofflesh;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
import net.minecraftforge.entity.PartEntity;
import org.confluence.lib.util.LibMathUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class HillOfFleshPart extends PartEntity<HillOfFlesh> {
    public final HillOfFlesh parentMob;
    public final String name;
    private EntityDimensions size;
    public LivingEntity target;
    public Vec3 modelOffset;
    public int stareCount = 10;
    public float stareYaw = 0;
    public float starePitch = 0;
    public float stareStartYaw = 0;
    public float stareStartPitch = 0;
    float randomDeathSpeed;
    float width;
    float height;

    public HillOfFleshPart(HillOfFlesh parentMob, String name, float width, float height) {
        super(parentMob);
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
        this.parentMob = parentMob;
        this.name = name;
        this.randomDeathSpeed = this.getRandom().nextFloat() * 0.5f + 1f;
        this.width = width;
        this.height = height;
        this.modelOffset = Vec3.ZERO;
    }

    public void setScale(float scale) {
        this.size = EntityDimensions.scalable(this.width * scale, this.height * scale);
        this.refreshDimensions();
    }

    @Override
    public void tick() {}

    // 切换目标，需要改变插值起始点
    public void changeTarget(LivingEntity target) {

        if (this.target != null) {
            this.stareStartYaw = this.stareYaw;
            this.stareStartPitch = this.starePitch;
            this.stareCount = 0;
        }
        this.target = target;
    }

    protected abstract void tickPart(double offsetX, double offsetY, double offsetZ, int index);

    public float lerpYaw(float partialTick) {
        return Mth.lerp(Mth.clamp(this.stareCount + partialTick, 0, 10) / 10, this.stareStartYaw, this.stareYaw);
    }

    public float lerpPitch(float partialTick) {
        return Mth.lerp(Mth.clamp(this.stareCount + partialTick, 0, 10) / 10, this.stareStartPitch, this.starePitch);
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
            if (this.getParent().tickCount % 25 == this.getId() % 25) {
                float r = this.getParent().getOutRadius();

                // 优先索敌玩家
                LivingEntity living = null;
                LivingEntity player = null;
                for (LivingEntity e : this.getParent().nearbyLivings) {
                    boolean isPlayer = e instanceof Player && e.canBeSeenAsEnemy();
                    if (!isPlayer) {
                        if (!this.hasLineOfSight(e)) {
                            continue;
                        }
                    }
                    double angle = LibMathUtils.angleBetween(this.modelOffset.add(0, -getParent().getBbHeight(), 0), e.position().subtract(this.position()));

                    boolean isTarget = e.isAlive() && angle < Math.PI / 2
                            && e.position().distanceToSqr(this.getParent().position().add(0, 10, 0)) < r * r * 1.2
                            && e.position().subtract(this.getParent().position()).horizontalDistanceSqr() <= r * r;
                    if (isTarget) {
                        if (isPlayer) {
                            player = e;
                            break;
                        } else {
                            living = e;
                        }
                    }
                }
                if (player != null) {
                    this.changeTarget(player);
                } else {
                    this.changeTarget(living);
                }
            }

            if (this.target == null) {
                if (this.getParent().deathTime <= 0) {
                    this.stareCount = Math.max(0, this.stareCount - 1);
                }
            } else if (this.getParent().deathTime <= 0) {
                stareCount = Math.min(11, stareCount + 1);
            }
        }
    }

    public float getDeathOffsetY(float partialTick) {
        double lp = Mth.lerp((this.getParent().deathTime + partialTick) / this.getParent().getMaxDeathTime(), 0, -this.randomDeathSpeed);
        lp = -lp * lp * 13;
        lp = Mth.clamp(lp, -1, 0);
        return (float) (lp * this.modelOffset.y);
    }

    private float wrapRotation(float current, float target) {
        while (target - current > Math.PI / 2) {
            current += (float) Math.PI;
        }
        while (target - current < -Math.PI / 2) {
            current -= (float) Math.PI;
        }
        return current;
    }

    public void setModelOffset(Vec3 modelOffset) {
        this.modelOffset = modelOffset;
    }


    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
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
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return !this.isInvulnerableTo(source) && this.parentMob.hurt(this, source, amount * this.getHurtFactor(source));
    }


    protected float getHurtFactor(@NotNull DamageSource source) {
        return 2.0f;
    }

    @Override
    public boolean is(@NotNull Entity entity) {
        return this == entity || this.parentMob == entity;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return this.size;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    protected void onParentChangeState(int state) {}
}
