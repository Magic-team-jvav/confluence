package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/// 昏晓的弹幕。
///
/// 出手后自动追踪并吸附到最近的敌人身上，吸附期间按固定间隔向外扩散一圈光波，
/// 每一圈光波的伤害都比上一圈更低。光波扩散结束后弹幕爆炸，并向四周释放一道
/// 强力的横向斩击。
public class DuskDawnProjectile extends AbstractManaProjectile {
    /// 光波扩散间隔（tick）。
    private static final int WAVE_INTERVAL = 8;
    /// 光波圈数。
    private static final int WAVES = 6;
    /// 第一圈光波半径。
    private static final double WAVE_BASE_RADIUS = 2.5;
    /// 每多一圈光波增加的半径。
    private static final double WAVE_RADIUS_GROWTH = 1.2;
    /// 每多一圈光波衰减的伤害比例。
    private static final float WAVE_DECAY = 0.14F;
    /// 光波伤害的最低倍率。
    private static final float WAVE_MIN_FACTOR = 0.25F;
    /// 追踪范围。
    private static final double HOMING_RANGE = 20.0;
    /// 追踪速度。
    private static final double HOMING_SPEED = 0.45;
    /// 视为已吸附到目标的距离。
    private static final double ATTACH_DISTANCE = 1.3;
    /// 弹幕总存在时间，光波放完后立刻引爆。
    private static final int LIFETIME = WAVE_INTERVAL * WAVES + 6;
    /// 爆炸半径。
    private static final double EXPLODE_RADIUS = 4.0;
    /// 爆炸伤害倍率。
    private static final float EXPLODE_FACTOR = 1.5F;
    /// 横向斩击的水平半长。
    private static final double SLASH_HALF_LENGTH = 4.5;
    /// 横向斩击的水平半宽。
    private static final double SLASH_HALF_WIDTH = 2.5;
    /// 横向斩击的垂直半高（1 格高）。
    private static final double SLASH_HALF_HEIGHT = 0.5;
    /// 横向斩击伤害倍率。
    private static final float SLASH_FACTOR = 2.0F;

    private int waves;
    private int nextWaveTick = WAVE_INTERVAL;
    private @Nullable UUID targetUuid;

    public DuskDawnProjectile(EntityType<DuskDawnProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public DuskDawnProjectile(LivingEntity living) {
        this(ModEntities.DUSK_DAWN.get(), living.level());
    }

    /// 昏晓本体不参与常规碰撞，只靠自身的吸附与光波结算。
    @Override
    public boolean canHitEntity(Entity target) {
        return false;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {}

    @Override
    public void baseTick() {
        super.baseTick();
        LivingEntity target = resolveTarget();
        if (target != null) {
            Vec3 center = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0);
            Vec3 offset = center.subtract(position());
            if (offset.lengthSqr() > ATTACH_DISTANCE * ATTACH_DISTANCE) {
                setDeltaMovement(offset.normalize().scale(HOMING_SPEED));
            } else {
                // 吸附：直接贴在目标身上，跟随目标移动。
                setDeltaMovement(Vec3.ZERO);
                setPos(center.x, center.y, center.z);
            }
        } else {
            setDeltaMovement(getDeltaMovement().scale(0.985));
        }
        doSimpleMove();
        if (!level().isClientSide && tickCount >= nextWaveTick && waves < WAVES) {
            this.nextWaveTick += WAVE_INTERVAL;
            emitWave();
        }
        if (!level().isClientSide && tickCount >= LIFETIME) {
            explode();
            slash();
            discardInTicks(1);
        }
        doAgeCheck(LIFETIME + 40);
    }

    private @Nullable LivingEntity resolveTarget() {
        if (targetUuid != null) {
            if (level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(targetUuid);
                if (entity instanceof LivingEntity living && living.isAlive() && canHurt(living)) {
                    return living;
                }
            }
            this.targetUuid = null;
        }
        LivingEntity nearest = null;
        double best = HOMING_RANGE * HOMING_RANGE;
        for (LivingEntity candidate : level().getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(HOMING_RANGE))) {
            if (!canHurt(candidate)) continue;
            double distance = candidate.distanceToSqr(this);
            if (distance < best) {
                best = distance;
                nearest = candidate;
            }
        }
        if (nearest != null) this.targetUuid = nearest.getUUID();
        return nearest;
    }

    private boolean canHurt(LivingEntity entity) {
        return entity != getOwner() && entity.isAlive() && !entity.isSpectator()
                && org.confluence.lib.util.LibEntityUtils.canHit(getOwner(), entity);
    }

    /// 向外扩散一圈光波，伤害逐圈递减。
    private void emitWave() {
        double radius = WAVE_BASE_RADIUS + waves * WAVE_RADIUS_GROWTH;
        float factor = Math.max(WAVE_MIN_FACTOR, 1.0F - waves * WAVE_DECAY);
        for (LivingEntity victim : level().getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(radius))) {
            if (victim == getOwner() || !victim.isAlive() || victim.isSpectator()) continue;
            if (!org.confluence.lib.util.LibEntityUtils.canHit(getOwner(), victim)) continue;
            victim.invulnerableTime = 0;
            victim.hurt(getDamageSource(), getCalculatedDamage() * factor);
        }
        this.waves++;
    }

    private void explode() {
        for (LivingEntity victim : level().getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(EXPLODE_RADIUS))) {
            if (victim == getOwner() || !victim.isAlive() || victim.isSpectator()) continue;
            if (!org.confluence.lib.util.LibEntityUtils.canHit(getOwner(), victim)) continue;
            victim.invulnerableTime = 0;
            victim.hurt(getDamageSource(), getCalculatedDamage() * EXPLODE_FACTOR);
        }
    }

    /// 一道贴地的强力横向斩击。
    private void slash() {
        AABB area = new AABB(
                getX() - SLASH_HALF_LENGTH, getY() - SLASH_HALF_HEIGHT, getZ() - SLASH_HALF_WIDTH,
                getX() + SLASH_HALF_LENGTH, getY() + SLASH_HALF_HEIGHT, getZ() + SLASH_HALF_WIDTH
        );
        for (LivingEntity victim : level().getEntitiesOfClass(LivingEntity.class, area)) {
            if (victim == getOwner() || !victim.isAlive() || victim.isSpectator()) continue;
            if (!org.confluence.lib.util.LibEntityUtils.canHit(getOwner(), victim)) continue;
            victim.invulnerableTime = 0;
            victim.hurt(getDamageSource(), getCalculatedDamage() * SLASH_FACTOR);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Waves", waves);
        compound.putInt("NextWaveTick", nextWaveTick);
        if (targetUuid != null) compound.putUUID("Target", targetUuid);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.waves = compound.getInt("Waves");
        this.nextWaveTick = compound.getInt("NextWaveTick");
        this.targetUuid = compound.hasUUID("Target") ? compound.getUUID("Target") : null;
    }
}
