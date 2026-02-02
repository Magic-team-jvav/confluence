package org.confluence.terraentity.entity.boss.thedestroyer;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.entity.proj.LineProj;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class TheDestroyerPart extends AbstractTerraBossBase implements Boss.BossPart {

    // --- 同步 Owner ---
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER = SynchedEntityData.defineId(TheDestroyerPart.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(TheDestroyerPart.class, EntityDataSerializers.INT);

    // --- 状态数据 ---
    private static final EntityDataAccessor<Boolean> DATA_PROBE_RELEASED = SynchedEntityData.defineId(TheDestroyerPart.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_FLAPS_OPEN = SynchedEntityData.defineId(TheDestroyerPart.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_SEGMENT_ROLL = SynchedEntityData.defineId(TheDestroyerPart.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_IS_TAIL = SynchedEntityData.defineId(TheDestroyerPart.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_PROBE_SEGMENT = SynchedEntityData.defineId(TheDestroyerPart.class, EntityDataSerializers.BOOLEAN);

    public TheDestroyer owner; // 引用头部
    public Entity lookAtTgt = null;
    public float prevPartRoll; // 渲染插值

    public TheDestroyerPart(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    // --- 关键配置：必须调用 super 以防止 SynchedEntityData 崩溃 ---
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER, Optional.empty());
        builder.define(DATA_OWNER_ID, 0);
        builder.define(DATA_PROBE_RELEASED, false);
        builder.define(DATA_FLAPS_OPEN, false);
        builder.define(DATA_SEGMENT_ROLL, 0f);
        builder.define(DATA_IS_TAIL, false);
        builder.define(DATA_IS_PROBE_SEGMENT, false);
    }

    public void setOwner(TheDestroyer owner) {
        this.owner = owner;
        if(owner != null) {
            entityData.set(DATA_OWNER, Optional.of(owner.getUUID()));
            entityData.set(DATA_OWNER_ID, owner.getId());
        }
    }

    // --- 伤害转发逻辑 (Health Sharing) ---
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(this.isInvulnerableTo(source)) return false;

        if (this.owner != null && this.owner.isAlive()) {
            boolean hurtResult = this.owner.hurt(source, amount);

            // 受伤释放探针逻辑 (仅服务端)
            if (hurtResult && !level().isClientSide) {
                if (isProbeSegment() && !hasReleasedProbe() && owner.getPhase() != TheDestroyer.Phase.UNDERGROUND) {
                    if (random.nextFloat() < 0.2f) { // 20% 概率
                        tryReleaseProbe();
                    }
                }
            }
            return hurtResult;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        this.prevPartRoll = getSegmentRoll();
        super.tick();

        if (lookAtTgt != null) lookAt(EntityAnchorArgument.Anchor.EYES, lookAtTgt.getEyePosition());

        // 客户端：寻找 Owner
        if (level().isClientSide) {
            if (this.owner == null) {
                this.owner = (TheDestroyer) level().getEntity(entityData.get(DATA_OWNER_ID));
            }
            // 粒子特效
            if (areFlapsOpen() && random.nextFloat() < 0.1) {
                level().addParticle(ParticleTypes.SMOKE, getX(), getY() + 0.5, getZ(), 0, 0.1, 0);
            }
        } else {
            // 服务端：Owner 丢失或死亡则自毁
            if (tickCount == 1) {
                // 加载时恢复引用
                if(owner == null && entityData.get(DATA_OWNER).isPresent()) {
                    ServerLevel sl = (ServerLevel) level();
                    Entity e = sl.getEntity(entityData.get(DATA_OWNER).get());
                    if(e instanceof TheDestroyer d) this.owner = d;
                }
            }
            if (tickCount > 5 && (owner == null || !owner.isAlive())) {
                this.discard();
                return;
            }
        }

        // 天空模式：蓝色火焰伤害
        if (owner != null && owner.getPhase() == TheDestroyer.Phase.SKY) {
            level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.5),
                            e -> e != this && e != owner && !(e instanceof TheDestroyerPart))
                    .forEach(e -> {
                        // TODO - 蓝色火焰攻击特殊处理
                        e.hurt(damageSources().mobAttack(this), 10.0f);
                    });
        }
    }

    public void tryReleaseProbe() {
        if (!level().isClientSide && !hasReleasedProbe() && owner != null) {
            entityData.set(DATA_PROBE_RELEASED, true);
            this.playSound(SoundEvents.PISTON_EXTEND, 1.0f, 1.0f);

            if (level() instanceof ServerLevel sl) {
                TheDestroyerProbe probe = TEBossEntities.THE_DESTROYER_PROBE.get().create(sl);
                if (probe != null) {
                    probe.moveTo(this.getX(), this.getY() + 1.5, this.getZ(), this.getYRot(), 0);
                    probe.setHead(this.owner);
                    probe.finalizeSpawn(sl, sl.getCurrentDifficultyAt(blockPosition()), MobSpawnType.TRIGGERED, null);
                    sl.addFreshEntity(probe);
                }
            }
        }
    }

    public void tryShootLaser(LivingEntity target) {
        if (target == null || level().isClientSide) return;
        if (!isProbeSegment() || hasReleasedProbe() || !areFlapsOpen()) return;
        if (level().getBlockState(blockPosition()).isSolid()) return;

        Vec3 shootPos = this.position().add(0, getBbHeight()/2f, 0);
        LineProj laser = TEProjectileEntities.TRAIL_PROJECTILE.get().create(level()); // 使用你现有的投射物
        if (laser != null) {
            laser.setOwner(this.owner != null ? this.owner : this);
            laser.setPos(shootPos);
            laser.setDamage(20.0f);

            Vec3 dir = target.getEyePosition().subtract(shootPos).normalize();
            laser.shoot(dir.x, dir.y, dir.z, 1.5F, 1.0F);

            level().addFreshEntity(laser);
            this.playSound(SoundEvents.BEACON_ACTIVATE, 0.5f, 2.0f);
        }
    }

    // --- 数据存取 ---

    public void setPartType(int i) { /* 可选用于记录是第几节 */ }

    public float getSegmentRoll() { return entityData.get(DATA_SEGMENT_ROLL); }
    public void setSegmentRoll(float r) { entityData.set(DATA_SEGMENT_ROLL, r); }
    public boolean isTail() { return entityData.get(DATA_IS_TAIL); }
    public void setTail(boolean tail) { entityData.set(DATA_IS_TAIL, tail); }
    public boolean isProbeSegment() { return entityData.get(DATA_IS_PROBE_SEGMENT); }
    public void setProbeSegment(boolean isProbe) { entityData.set(DATA_IS_PROBE_SEGMENT, isProbe); }
    public boolean hasReleasedProbe() { return entityData.get(DATA_PROBE_RELEASED); }
    public void setFlapsOpen(boolean open) {
        if (open != entityData.get(DATA_FLAPS_OPEN)) {
            entityData.set(DATA_FLAPS_OPEN, open);
            if(open) this.playSound(SoundEvents.IRON_TRAPDOOR_OPEN, 0.3f, 1.5f);
        }
    }
    public boolean areFlapsOpen() { return entityData.get(DATA_FLAPS_OPEN); }

    // --- 序列化 ---

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if(owner != null) tag.putUUID("Owner", owner.getUUID());
        tag.putBoolean("ProbeReleased", hasReleasedProbe());
        tag.putBoolean("IsProbeSegment", isProbeSegment());
        tag.putBoolean("IsTail", isTail());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Owner")) entityData.set(DATA_OWNER, Optional.of(tag.getUUID("Owner")));
        entityData.set(DATA_PROBE_RELEASED, tag.getBoolean("ProbeReleased"));
        entityData.set(DATA_IS_PROBE_SEGMENT, tag.getBoolean("IsProbeSegment"));
        entityData.set(DATA_IS_TAIL, tag.getBoolean("IsTail"));
    }

    // --- 基础重写 ---
    @Override public boolean isNoGravity() { return true; }
    @Override public boolean shouldShowBossBar() { return false; }
    @Override public boolean isMainBody() { return false; }
    @Override public boolean isPickable() { return true; }
    @Override public boolean isInvulnerableTo(DamageSource s) { return super.isInvulnerableTo(s) || s.is(DamageTypes.IN_WALL) || s.is(DamageTypes.FALL) || s.is(DamageTypes.DROWN); }
    @Override public void addSkills() {}

    @Override
    public boolean canAttack(LivingEntity entity) {
        if (!super.canAttack(entity)) return false;
        return !(entity instanceof TheDestroyer || entity instanceof TheDestroyerPart || entity instanceof TheDestroyerProbe);
    }
}
