package org.confluence.mod.common.entity.boss;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 骷髅王之手。绕头部轨道运行，周期性挥击玩家。
public class SkeletronHand extends BaseBossPart<Skeletron> implements GeoEntity {
    // 两次拍击之间的基础间隔，单位为 tick；专家模式缩短准备时间。
    private static final int CLASSIC_SLAP_INTERVAL = 45;
    private static final int EXPERT_SLAP_INTERVAL = 30;
    // 给双手加入最多 5 tick 的确定性错峰，避免同一 tick 完全重叠出手。
    private static final int RANDOM_INTERVAL = 6;
    // 拍击阶段的每 tick 速度（方块/tick）。
    private static final double CLASSIC_SLAP_SPEED = 1.0;
    private static final double EXPERT_SLAP_SPEED = 1.2;
    // PREPARE 是手掌退到目标外侧的距离，PASS 是挥击越过目标后的距离，单位为方块。
    private static final double PREPARE_DISTANCE = 6.0;
    private static final double PASS_DISTANCE = 4.0;
    // 到达判定直接与距离平方比较，避免每 tick 开平方。
    private static final double ARRIVAL_DISTANCE_SQUARED = 1.5;
    // 部件属性的基础值；实际最大生命由本体统一应用难度和多人倍率。
    private static final float BASE_MAX_PART_HEALTH = 405.0F;
    private static final float PART_ARMOR = 4.0F;
    private static final String HAND_INDEX_TAG = "HandIndex";
    private static final EntityDataAccessor<Integer> HAND_INDEX = SynchedEntityData.defineId(SkeletronHand.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int slapInterval;
    private double slapSpeed;
    private int slapTick;
    /// -1 表示待机，0 表示向后蓄势，1 表示穿过目标。
    private int slapPhase = -1;
    private int slapTargetId = -1;
    private Vec3 phaseTarget = Vec3.ZERO;
    private int clientLerpSteps;
    private double clientLerpX;
    private double clientLerpY;
    private double clientLerpZ;
    private float clientLerpYaw;
    private float clientLerpPitch;

    public SkeletronHand(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    /// 手掌保留射线选取和受伤判定，但不作为原版实体障碍挤压玩家姿态空间。
    /// 接触伤害由自身扫掠检测负责，不依赖 canBeCollidedWith。
    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    public void setMaster(Skeletron master, int index) {
        if (index < 0 || index > 1) throw new IllegalArgumentException("Hand index must be 0 or 1");
        bindTo(master);
        this.entityData.set(HAND_INDEX, index);
        this.slapInterval = (master.isExpert()
                ? EXPERT_SLAP_INTERVAL : CLASSIC_SLAP_INTERVAL)
                + master.getRandom().nextInt(RANDOM_INTERVAL);
        this.slapSpeed = master.isExpert()
                ? EXPERT_SLAP_SPEED : CLASSIC_SLAP_SPEED;
        this.slapTick = slapInterval;
        Vec3 standbyPosition = getStandbyPosition(master);
        setPos(standbyPosition.x, standbyPosition.y, standbyPosition.z);
        faceRoot(master);
    }

    public int getHandIndex() {
        return entityData.get(HAND_INDEX);
    }

    @Override
    protected void tickPart(Skeletron master) {
        if (level().isClientSide) {
            tickClientInterpolation();
            return;
        }

        faceRoot(master);

        LivingEntity target = master.getTarget();
        boolean hasCombatTarget = target != null && target.isAlive() && master.canAttack(target);
        if (slapPhase >= 0 && !hasCombatTarget) {
            cancelSlapWithoutConsumingCooldown();
            tickStandby(master);
        } else if (slapPhase >= 0) {
            tickSlap(master);
        } else if (!master.isSpinning() && hasCombatTarget && slapTick >= slapInterval) {
            beginSlap(master);
            tickSlap(master);
        } else {
            tickStandby(master);
        }
        faceRoot(master);
        damagePalmContacts(master);
    }

    /// 手掌始终指向头部侧面的连接根点；
    /// 若只移动实体而不写俯仰，手掌会平移滑行，骨链在视觉上像完全没有关节运动。
    private void faceRoot(Skeletron master) {
        Vec3 root = getRootPosition(master);
        Vec3 direction = root.subtract(position());
        if (direction.lengthSqr() <= 1.0E-7) return;
        float targetYaw = (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        float targetPitch = (float) (-Mth.atan2(direction.y, direction.horizontalDistance()) * Mth.RAD_TO_DEG);
        setYRot(targetYaw);
        setXRot(targetPitch);
    }

    public Vec3 getRootPosition() {
        Skeletron master = getOwner();
        return master == null ? position() : getRootPosition(master);
    }

    public Vec3 getRootPosition(float partialTick) {
        Skeletron master = getOwner();
        if (master == null) return getPosition(partialTick);
        float yaw = master.getFacingYaw(partialTick) * Mth.DEG_TO_RAD;
        double side = getHandIndex() == 0 ? 1.0 : -1.0;
        double scale = master.getScale();
        return master.getPosition(partialTick).add(
                Mth.cos(yaw) * 2.0 * side * scale, 0.0, Mth.sin(yaw) * 2.0 * side * scale);
    }

    private Vec3 getRootPosition(Skeletron master) {
        float yaw = master.getYRot() * Mth.DEG_TO_RAD;
        double side = getHandIndex() == 0 ? 1.0 : -1.0;
        double scale = master.getScale();
        return master.position().add(Mth.cos(yaw) * 2.0 * side * scale, 0.0, Mth.sin(yaw) * 2.0 * side * scale);
    }

    private void tickStandby(Skeletron master) {
        slapTick = Math.min(slapInterval, slapTick + 1);
        double maximumSpeed = master.isFtw() ? 2.0 : master.isExpert() ? 1.0 : 0.7;
        moveSmoothlyToward(getStandbyPosition(master), maximumSpeed);
    }

    private Vec3 getStandbyPosition(Skeletron master) {
        float yaw = master.getYRot() * Mth.DEG_TO_RAD;
        double side = getHandIndex() == 0 ? 1.0 : -1.0;
        double scale = master.getScale();
        double vertical = (master.isSpinning() ? 4.0 : -3.5) * scale;
        return master.position().add(Mth.cos(yaw) * 5.0 * side * scale, vertical, Mth.sin(yaw) * 5.0 * side * scale);
    }

    private void beginSlap(Skeletron master) {
        LivingEntity target = master.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        slapTargetId = target.getId();
        Vec3 away = position().subtract(target.position());
        if (away.lengthSqr() <= 1.0E-7) {
            away = new Vec3(0.0, 0.0, 1.0);
        }
        phaseTarget = position().add(away.normalize().scale(PREPARE_DISTANCE * master.getScale()));
        slapPhase = 0;
    }

    private void tickSlap(Skeletron master) {
        LivingEntity target = master.getTarget();
        if (target == null || !target.isAlive() || !master.canAttack(target)) {
            cancelSlapWithoutConsumingCooldown();
            return;
        }
        if (master.isSpinning()) {
            finishSlap();
            return;
        }
        if (target.getId() != slapTargetId) {
            beginSlap(master);
        }
        if (position().distanceToSqr(phaseTarget) <= ARRIVAL_DISTANCE_SQUARED) {
            if (slapPhase == 0) {
                Vec3 through = target.position().subtract(position());
                if (through.lengthSqr() <= 1.0E-7) {
                    through = new Vec3(0.0, 0.0, 1.0);
                }
                phaseTarget = target.position().add(through.normalize().scale(PASS_DISTANCE * master.getScale()));
                slapPhase = 1;
            } else {
                finishSlap();
                return;
            }
        }
        moveToward(phaseTarget, slapSpeed);
    }

    private void damagePalmContacts(Skeletron master) {
        if (master.getTarget() == null || !master.getTarget().isAlive()) return;
        for (net.minecraft.world.entity.Entity entity : SweptContactAttack.findTargets(this, 0.0D,
                SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE,
                candidate -> candidate instanceof LivingEntity living && living != master && master.canAttack(living))) {
            entity.hurt(damageSources().mobAttack(master), master.getHandContactDamage());
        }
    }

    /// 丢失战斗目标时保持挥击就绪，避免目标恢复后重新等待一整轮冷却。
    private void cancelSlapWithoutConsumingCooldown() {
        slapPhase = -1;
        slapTargetId = -1;
        slapTick = slapInterval;
        phaseTarget = Vec3.ZERO;
        setDeltaMovement(Vec3.ZERO);
    }

    private void finishSlap() {
        slapPhase = -1;
        slapTargetId = -1;
        slapTick = 0;
        phaseTarget = Vec3.ZERO;
        setDeltaMovement(Vec3.ZERO);
    }

    private void moveToward(Vec3 target, double maximumSpeed) {
        Vec3 difference = target.subtract(position());
        if (difference.lengthSqr() <= 1.0E-7) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }
        if (difference.lengthSqr() <= maximumSpeed * maximumSpeed) {
            setPos(target.x, target.y, target.z);
            setDeltaMovement(Vec3.ZERO);
            return;
        }
        Vec3 velocity = difference.normalize().scale(maximumSpeed);
        setDeltaMovement(velocity);
        setPos(getX() + velocity.x, getY() + velocity.y, getZ() + velocity.z);
    }

    private void moveSmoothlyToward(Vec3 target, double maximumSpeed) {
        Vec3 difference = target.subtract(position());
        double distance = difference.length();
        if (distance <= 0.08D) {
            setPos(target.x, target.y, target.z);
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        Vec3 desiredVelocity = difference.scale(0.18D);
        if (desiredVelocity.length() > maximumSpeed) {
            desiredVelocity = desiredVelocity.normalize().scale(maximumSpeed);
        }
        Vec3 velocity = getDeltaMovement().scale(0.62D).add(desiredVelocity.scale(0.38D));
        if (velocity.length() > maximumSpeed) {
            velocity = velocity.normalize().scale(maximumSpeed);
        }
        if (velocity.length() > distance) {
            velocity = difference;
        }
        setDeltaMovement(velocity);
        setPos(getX() + velocity.x, getY() + velocity.y, getZ() + velocity.z);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Skeletron owner = getOwner();
        if (owner == null || !owner.isAlive() || isRemoved() || isInvulnerableTo(source))
            return false;
        if (source.getEntity() instanceof net.minecraft.world.entity.player.Player player) {
            owner.registerCombatParticipant(player);
        }
        float appliedDamage = source.is(DamageTypeTags.BYPASSES_ARMOR) ? amount : CombatRules.getDamageAfterAbsorb(amount, PART_ARMOR, 0.0F);
        if (appliedDamage <= 0.0F) return false;
        float remaining = Math.max(0.0F, getPartHealth() - appliedDamage);
        setPartHealth(remaining);
        indicateHurt();
        playSound(SoundEvents.SKELETON_HURT, 0.9F, 0.9F + random.nextFloat() * 0.2F);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                    getX(), getY() + getBbHeight() * 0.5F, getZ(), 4,
                    getBbWidth() * 0.2F, getBbHeight() * 0.2F, getBbWidth() * 0.2F, 0.05D);
        }
        onPartHealthChanged(owner, remaining);
        if (remaining <= 0.0F) {
            onPartDestroyed(owner);
            discard();
        }
        return true;
    }

    @Override
    protected float getMaxPartHealth() {
        Skeletron owner = getOwner();
        return owner == null ? BASE_MAX_PART_HEALTH : owner.getHandMaxHealth();
    }

    @Override
    protected Class<Skeletron> getOwnerType() {
        return Skeletron.class;
    }

    @Override
    protected void onPartDestroyed(Skeletron owner) {
        owner.onHandDestroyed(getHandIndex(), this);
    }

    @Override
    protected void onPartHealthChanged(Skeletron owner, float remainingHealth) {
        owner.onHandHealthChanged(getHandIndex(), remainingHealth);
    }

    @Override
    protected void definePartSynchedData() {
        entityData.define(HAND_INDEX, 0);
    }

    @Override
    protected void readPartSaveData(CompoundTag tag) {
        entityData.set(HAND_INDEX, tag.getInt(HAND_INDEX_TAG));
        slapPhase = -1;
        slapTargetId = -1;
        slapTick = slapInterval;
        phaseTarget = Vec3.ZERO;
    }

    @Override
    protected void addPartSaveData(CompoundTag tag) {
        tag.putInt(HAND_INDEX_TAG, getHandIndex());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps, boolean teleport) {
        if (!level().isClientSide || teleport || distanceToSqr(x, y, z) > 4096.0D) {
            setPos(x, y, z);
            setRot(yaw, pitch);
            clientLerpSteps = 0;
            return;
        }
        clientLerpX = x;
        clientLerpY = y;
        clientLerpZ = z;
        clientLerpYaw = yaw;
        clientLerpPitch = pitch;
        // 保留服务端位置包给出的插值窗口。强制一刻到位会让手掌在
        // 相邻网络快照之间跳动，而手臂的两段骨链又以手掌为末端，
        // 因此整条手臂都会表现为卡顿和抖动。
        clientLerpSteps = Math.max(1, steps);
    }

    private void tickClientInterpolation() {
        if (clientLerpSteps <= 0) return;
        double progress = 1.0D / clientLerpSteps;
        setPos(
                Mth.lerp(progress, getX(), clientLerpX),
                Mth.lerp(progress, getY(), clientLerpY),
                Mth.lerp(progress, getZ(), clientLerpZ));
        setRot(
                Mth.rotLerp((float) progress, getYRot(), clientLerpYaw),
                Mth.lerp((float) progress, getXRot(), clientLerpPitch));
        clientLerpSteps--;
    }
}
