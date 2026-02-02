package org.confluence.terraentity.entity.boss.thedestroyer;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import org.confluence.lib.api.entity.Boss;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.utils.CameraShakeData;
import org.confluence.terraentity.utils.CameraShakeManager;
import org.confluence.terraentity.utils.TEUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 毁灭者 (The Destroyer) - 头部
 */
public class TheDestroyer extends AbstractTerraBossBase implements Boss {

    // --- 同步数据 ---
    public static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_TEXTURE_VARIANT = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> DATA_BODY_ROLL = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> DATA_HEAD_SHELL_OPEN = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.BOOLEAN);

    public enum Phase {UNDERGROUND, GROUND, SKY}

    // --- 配置 ---
    private final int segmentCount = 80;
    private final float segmentInterval = 3.2f;
    private final float turnSpeedBase = 9f;
    private final float moveSpeedBase = 1f;

    // --- 部件管理 ---
    // 手动管理 List，不使用 NeoForge 的 PartEntity 接口以避免继承冲突
    private final List<TheDestroyerPart> parts = new ArrayList<>();

    // --- 运行时 ---
    private boolean isInsideBlock = false;
    private int phaseTimer = 0;
    public float prevBodyRoll;
    private int groundAttackState = 0;
    private int laserSequenceIndex = -1;
    private int laserTick = 0;
    private int volleyCooldown = 0;

    //    private static final int Y_LEVEL_DEEP = 50;
//    private static final int Y_LEVEL_SKY = 120;
    private static final int Y_LEVEL_DEEP = 60;
    private static final int Y_LEVEL_SKY = 100;
    private int caveAttackState = 0; // 地下模式状态机
    private int skyAttackState = 0;  // 天空模式状态机
    private boolean isPerformingBarrelRoll = false; // 天空模式：是否正在进行特技翻滚
    private Vec3 velocity = getDeltaMovement();
    private Vec3 wanderPos = null;
    private boolean lastInBlock = false;

    public TheDestroyer(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
        this.xpReward = 2000;
        this.setHealth(this.getMaxHealth());
    }

    public TheDestroyer(Level level) {
        this(TEBossEntities.THE_DESTROYER.get(), level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PHASE, 0);
        builder.define(DATA_TEXTURE_VARIANT, 0);
        builder.define(DATA_BODY_ROLL, 0f);
        builder.define(DATA_HEAD_SHELL_OPEN, false);
    }

    // --- 首次生成逻辑 (参考 SkeletronPrime) ---
    @Override
    public void firstSpawn() {
        if (level() instanceof ServerLevel serverLevel && parts.isEmpty()) {
            Vec3 lastPos = position();
            Vec3 backward = this.getForward().scale(-segmentInterval);

            for (int i = 0; i < segmentCount; i++) {
                lastPos = lastPos.add(backward);

                // 修正：调用 TEUtils.spawnEntity 或直接 create + addFreshEntity
                // 这里为了方便设置属性，手动创建
                TheDestroyerPart part = TEBossEntities.THE_DESTROYER_PART.get().create(serverLevel);
                if (part != null) {
                    part.moveTo(lastPos.x, lastPos.y, lastPos.z, this.getYRot(), this.getXRot());
                    part.setOwner(this); // 设置归属
                    part.setPartType(i); // 记录是第几节 (可选)

                    // 特殊部位标记
                    if (i == segmentCount - 1) part.setTail(true);
                    if (i % 2 == 0) part.setProbeSegment(true);

                    serverLevel.addFreshEntity(part);
                    this.parts.add(part);
                }
            }

            // 初始化 BossBar
            final TargetingConditions attackTargeting = TargetingConditions.forNonCombat().range(128.0);
            level().getNearbyPlayers(attackTargeting, this, this.getBoundingBox().inflate(200))
                    .forEach(p -> bossEvent.addPlayer((ServerPlayer) p));
        }
    }

    // --- 核心更新逻辑 ---

    @Override
    public void tick() {
        this.prevBodyRoll = this.getBodyRoll();
        super.tick();
    }

    @Override
    public void aiStep() {
        super.aiStep(); // 处理基础移动 AI

        if (level().isClientSide) return;

        // 1. 部件链条物理更新 (Chain Physics)
        updatePartsPhysics();

        // 2. AI 行为树
        tickPhaseLogic();
        checkBlockCollisionEffects();
        tickLaserControl();

        // 3. 状态与血量视觉同步
        updateBossState();
    }

    /**
     * 核心：集中管理所有体节的移动和状态
     */
    private void updatePartsPhysics() {
        // 清理已死亡的部件引用
        parts.removeIf(p -> p == null || !p.isAlive());

        if (parts.isEmpty()) return;

        Entity prev = this;
        Phase phase = getPhase();

        for (TheDestroyerPart part : parts) {
            // A. 位置跟随 (贪吃蛇逻辑)
            double distSq = part.distanceToSqr(prev);
            if (distSq > 4000) { // 防丢失
                part.setPos(prev.position());
            } else if (distSq > 0.5) {
                // 计算目标位置：前一节位置 - (朝向 * 间距)
                // 这里使用简单的向量追踪，使动作更像生物
                Vec3 dirToPrev = prev.position().subtract(part.position()).normalize();
                double currentDist = Math.sqrt(distSq);
                double moveDist = currentDist - segmentInterval;

                if (moveDist > 0) {
                    Vec3 moveVec = dirToPrev.scale(moveDist);
                    part.setPos(part.position().add(moveVec));
                }

                // 强制看向前一节
                part.lookAtTgt = prev;
                part.lookAt(EntityAnchorArgument.Anchor.EYES, prev.getEyePosition());
            }

            // B. 旋转(Roll)传递 (DNA螺旋)
            float prevRoll = (prev instanceof TheDestroyer d) ? d.getBodyRoll() : ((TheDestroyerPart) prev).getSegmentRoll();
            float currentRoll = part.getSegmentRoll();
            // 平滑传递旋转
            float diff = Mth.degreesDifference(currentRoll, prevRoll);
            if (Math.abs(diff) >= 10.0f) {
                currentRoll += (diff > 0 ? (diff - 9.0f) : (diff + 9.0f));
            } else {
                currentRoll += diff * 0.15f;
            }
            part.setSegmentRoll(Mth.wrapDegrees(currentRoll));

            // C. 侧翼开闭同步
            boolean isSolid = level().getBlockState(part.blockPosition()).isSolid();
            boolean shouldOpen = phase == Phase.SKY || (phase == Phase.GROUND && !isSolid);
            part.setFlapsOpen(shouldOpen);

            prev = part;
        }
    }

    private void tickPhaseLogic() {
        LivingEntity target = getTarget();
        phaseTimer++;

        // 出入方块声音
        if (this.onGround() != lastInBlock) {
            lastInBlock = this.onGround();
            CameraShakeManager.addCameraShake(new CameraShakeData(20, this.position(), 50));
        }

        // 1. 目标丢失处理
        if (target == null) {
            if (wanderPos == null) wanderPos = getEyePosition();
            if (getPhase() != Phase.GROUND) setPhase(Phase.GROUND);
            tickGroundMode(null);
            return;
        } else {
            wanderPos = target.position();
        }

        // 2. 根据高度判定目标阶段
        double targetY = target.getY();
        Phase currentPhase = getPhase();
        Phase targetPhase;

        if (targetY < Y_LEVEL_DEEP) targetPhase = Phase.UNDERGROUND;
        else if (targetY > Y_LEVEL_SKY) targetPhase = Phase.SKY;
        else targetPhase = Phase.GROUND;

        // 3. 阶段切换初始化
        if (currentPhase != targetPhase) {
            setPhase(targetPhase);
            phaseTimer = 0;
            // 重置所有子状态
            groundAttackState = 0;
            caveAttackState = 0;
            skyAttackState = 0;
            isPerformingBarrelRoll = false;
        }

        // 4. 执行对应模式逻辑
        switch (currentPhase) {
            case UNDERGROUND -> tickCaveMode(target);
            case GROUND -> tickGroundMode(target);
            case SKY -> tickSkyMode(target);
        }
    }

    /**
     * 地下模式 (Cave Mode)
     * 行为：DNA螺旋钻头冲撞
     * 限制：不发激光，不放探针，头甲关闭
     */
    private void tickCaveMode(LivingEntity target) {
        // 强制关闭头甲
        if (entityData.get(DATA_HEAD_SHELL_OPEN)) setHeadShellOpen(false);

        // 基础旋转：模拟钻头持续旋转
        float baseRollSpeed = 15.0f;
        float rollSpeed = baseRollSpeed, adjustAngle = turnSpeedBase, fwdSpeed = moveSpeedBase;
        switch (caveAttackState) {
            case 0 -> { // 追踪与调整 (Track)
                rollSpeed = baseRollSpeed;
                adjustAngle = turnSpeedBase;
                fwdSpeed = moveSpeedBase;
                // 每 100 tick 尝试一次冲刺
                if (phaseTimer > 100) {
                    caveAttackState = 1;
                    phaseTimer = 0;
                }
            }
            case 1 -> { // 蓄力 (Rev up)
                float progMulti = phaseTimer / 20f;
                rollSpeed = baseRollSpeed * (1f + 1.5f * progMulti); // 1x -> 2.5x
                adjustAngle = turnSpeedBase;
                fwdSpeed = moveSpeedBase * (1f - 0.4f * progMulti); // 1x -> 0.4x

                // 播放蓄力音效
                if (phaseTimer % 3 == 0) {
                    this.playSound(SoundEvents.MINECART_RIDING, 1.0f, 2.0f);
                }

                if (phaseTimer > 20) {
                    caveAttackState = 2;
                    phaseTimer = 0;
                    // 初始化冲刺方向
                    Vec3 dashDir = target.getEyePosition().subtract(this.getEyePosition()).normalize();
                    velocity = dashDir.scale(moveSpeedBase * 1.5); // 高速冲撞
                    this.playSound(SoundEvents.TRIDENT_RIPTIDE_3.value(), 2.0f, 0.5f);
                }
            }
            case 2 -> { // 冲刺 (Dash)
                rollSpeed = baseRollSpeed * 2.5f;
                adjustAngle = turnSpeedBase * 0.05f; // 转向能力很弱
                fwdSpeed = moveSpeedBase * 1.5f;

                // 冲刺 30 tick 后结束
                if (phaseTimer > 30) {
                    caveAttackState = 3;
                    phaseTimer = 0;
                }
            }
            case 3 -> { // 惯性恢复 (Recovery)
                float progMulti = phaseTimer / 20f;
                rollSpeed = baseRollSpeed * (2.5f - 1.5f * progMulti); // 2.5 -> 1.0
                adjustAngle = turnSpeedBase * (0.05f + 0.95f * progMulti); // 0.05 -> 1.0
                fwdSpeed = moveSpeedBase * (1.5f - 0.5f * progMulti); // 1.5 -> 1
                if (phaseTimer > 20) {
                    caveAttackState = 0;
                    phaseTimer = 0;
                }
            }
        }

        // 移动
        float finalAdjustAngle = adjustAngle * Mth.DEG_TO_RAD;
        Vec3 tgt = target.getEyePosition().subtract(this.getEyePosition());
        tgt = tgt.normalize().scale(fwdSpeed);
        velocity = TEUtils.interpolateBasis(velocity, tgt,
                (angle) -> Math.min(angle, finalAdjustAngle), (spd) -> spd * 0.25);
        this.lookAt(EntityAnchorArgument.Anchor.EYES, getEyePosition().add(velocity));
        setDeltaMovement(velocity);
        // 旋转
        setBodyRoll((getBodyRoll() + rollSpeed) % 360.0f);
    }

    private void tickGroundMode(LivingEntity target) {
        Vec3 targetPos = target != null ?
                target.getEyePosition() :
                wanderPos;
        switch (groundAttackState) {
            case 0 -> { // 潜伏
                targetPos = targetPos.with(Direction.Axis.Y,
                        level().getHeight(Heightmap.Types.WORLD_SURFACE, (int) targetPos.x, (int) targetPos.z));
                targetPos = targetPos.subtract(0, 20, 0);
                velocity = targetPos.subtract(position()).normalize().scale(moveSpeedBase * 1.35);
                setBodyRoll(getBodyRoll() + 20);
                if (distanceToSqr(targetPos) < 16) {
                    groundAttackState = 1;
                    phaseTimer = 0;
                }
            }
            case 1 -> { // 跳跃
                wanderPos = wanderPos.add(Mth.sin(tickCount * 3.6f * Mth.DEG_TO_RAD) * 25, 0, Mth.cos(tickCount * 3.6f * Mth.DEG_TO_RAD) * 25);
                velocity = targetPos.subtract(getEyePosition()).normalize().scale(moveSpeedBase * 1.5);
                setBodyRoll(getBodyRoll() + 12.5f);
                // 6格以内开始下坠 (需要存在目标)
                if (targetPos.distanceToSqr(getEyePosition()) < 36 && target != null) {
                    groundAttackState = 2;
                    phaseTimer = 0;
                }
            }
            case 2 -> { // 下坠和冷却
                phaseTimer++;
                if (phaseTimer > 45) {
                    velocity = velocity.subtract(0, 0.05, 0);
                    setBodyRoll(getBodyRoll() + 5);
                } else {
                    smoothResetRoll();
                }
                if (phaseTimer > 80) {
                    groundAttackState = 0;
                    phaseTimer = 0;
                }
            }
        }
        this.lookAt(EntityAnchorArgument.Anchor.EYES, getEyePosition().add(velocity));
        setDeltaMovement(velocity);
    }

    /**
     * 天空模式 (Sky Mode)
     * 行为：高空飞行，蓝色火焰，激光齐射配合
     * 限制：头甲常开，偶尔翻滚
     */
    private void tickSkyMode(LivingEntity target) {
        // 强制打开头甲
        if (!entityData.get(DATA_HEAD_SHELL_OPEN)) setHeadShellOpen(true);

        // 随机触发“绕轴旋转”(Barrel Roll)
        if (!isPerformingBarrelRoll && random.nextInt(300) == 0) {
            isPerformingBarrelRoll = true;
        }

        // 处理旋转逻辑
        if (isPerformingBarrelRoll) {
            // 快速翻滚一圈
            float roll = getBodyRoll() + 25.0f;
            setBodyRoll(roll);
            // 滚完几圈后停止
            if (roll > 720.0f) { // 滚两圈
                setBodyRoll(0);
                isPerformingBarrelRoll = false;
            }
        } else {
            // 平时缓慢摆动或归正
            smoothResetRoll();
        }

        switch (skyAttackState) {
            case 0 -> { // 盘旋 (Hover/Circle)
                // 尝试飞到玩家上方 20-30 格处
                Vec3 hoverTarget = target.position().add(0, 25, 0);
                // 像世吞一样平滑飞行
                this.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, hoverTarget);
                velocity = this.getForward().scale(moveSpeedBase * 1.5);

                // 如果接近盘旋点，或者随机时间，进入俯冲攻击
                if (distanceToSqr(hoverTarget) < 256 || phaseTimer > 60) {
                    skyAttackState = 1;
                    phaseTimer = 0;
                    volleyCooldown = 20; // 准备触发齐射
                }
            }
            case 1 -> { // 俯冲/齐射准备 (Dive/Volley Prep)
                // 这是一个进攻性的动作，通常配合激光齐射
                // 飞向玩家侧上方，通过侧翼对准玩家
                float agl = tickCount * 6 * Mth.DEG_TO_RAD;
                Vec3 attackPos = target.position().add(Mth.sin(agl) * 15, 10, Mth.cos(agl) * 15);
                this.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, attackPos);
                velocity = this.getForward().scale(moveSpeedBase * 2.25);

                // 期间触发 tickLaserControl 中的齐射

                if (phaseTimer > 60) {
                    skyAttackState = 2;
                    phaseTimer = 0;
                }
            }
            case 2 -> { // 直冲
                Vec3 attackPos = target.getEyePosition();
                this.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, attackPos);
                velocity = this.getForward().scale(moveSpeedBase * 1.6);

                if (phaseTimer > 40) {
                    skyAttackState = 0;
                    phaseTimer = 0;
                }
            }
        }
        setDeltaMovement(velocity);
    }

    private void tickLaserControl() {
        LivingEntity target = getTarget();
        if (target == null || getPhase() == Phase.UNDERGROUND) return;

        // 齐射模式 (Sky Phase)
        if (getPhase() == Phase.SKY) {
            if (volleyCooldown-- <= 0) {
                parts.forEach(p -> p.tryShootLaser(target));
                this.playSound(SoundEvents.BEACON_ACTIVATE, 2.0f, 1.0f);
                volleyCooldown = 80 + random.nextInt(40);
                return;
            }
        }

        // 顺序发射模式
        if (laserSequenceIndex >= 0) {
            if (laserSequenceIndex < parts.size()) {
                TheDestroyerPart part = parts.get(laserSequenceIndex);
                if (part.isAlive()) part.tryShootLaser(target);
                laserSequenceIndex++;
            } else {
                laserSequenceIndex = -1;
                laserTick = 0;
            }
        } else if (random.nextInt(150) == 0) {
            laserSequenceIndex = 0;
            laserTick = 0;
        }
    }

    private void updateBossState() {
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        // 二阶段视觉
        if (getHealth() / getMaxHealth() < 0.5f && entityData.get(DATA_TEXTURE_VARIANT) == 0) {
            entityData.set(DATA_TEXTURE_VARIANT, 1);
            this.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 3.0f, 0.5f);
        }
    }

    private void checkBlockCollisionEffects() {
        BlockPos pos = blockPosition();
        BlockState state = level().getBlockState(pos);
        boolean inBlock = !state.isAir() && state.isRedstoneConductor(level(), pos);

        if (inBlock != isInsideBlock) {
            this.playSound(SoundEvents.GRINDSTONE_USE, inBlock ? 0.5f : 1.2f, 1.0f);
            CameraShakeManager.addCameraShake(new CameraShakeData(inBlock ? 10 : 20, this.position(), inBlock ? 7 : 14));
            isInsideBlock = inBlock;
        }
        if (inBlock && level() instanceof ServerLevel sl) {
            sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), getX(), getY(), getZ(), 3, 0.5, 0.5, 0.5, 0.05);
        }
    }

    private void smoothResetRoll() {
        float r = Mth.wrapDegrees(getBodyRoll());
        if (Math.abs(r) > 2) setBodyRoll(r * 0.8f);
        else setBodyRoll(0);
    }

    // --- 接口与存取器 ---

    public Phase getPhase() {return Phase.values()[Mth.clamp(entityData.get(DATA_PHASE), 0, 2)];}

    public void setPhase(Phase p) {
        entityData.set(DATA_PHASE, p.ordinal());
        setHeadShellOpen(p == Phase.SKY);
        if (p == Phase.GROUND || p == Phase.SKY) setNoGravity(true);
    }

    public float getBodyRoll() {return entityData.get(DATA_BODY_ROLL);}

    public void setBodyRoll(float roll) {entityData.set(DATA_BODY_ROLL, roll);}

    public void setHeadShellOpen(boolean open) {entityData.set(DATA_HEAD_SHELL_OPEN, open);}

    @Override
    public void onRemovedFromLevel() {
        if (!level().isClientSide) {
            parts.forEach(Entity::discard);
        }
        this.bossEvent.removeAllPlayers();
        super.onRemovedFromLevel();
    }

    @Override
    public void die(DamageSource source) {
        if (!CommonHooks.onLivingDeath(this, source)) {
            parts.forEach(Entity::discard);
            super.die(source);
        }
    }

    @Override
    public boolean shouldShowBossBar() {return true;}

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {return BossEvent.BossBarColor.RED;}

    @Override
    public boolean isInvulnerableTo(DamageSource s) {
        return super.isInvulnerableTo(s) || s.is(DamageTypes.IN_WALL) || s.is(DamageTypes.FALL);
    }

    @Override
    public boolean isNoGravity() {return true;}

    @Override
    public void addSkills() {}

    @Override
    public boolean canAttack(LivingEntity entity) {
        if (!super.canAttack(entity)) return false;
        return !(entity instanceof TheDestroyer || entity instanceof TheDestroyerPart || entity instanceof TheDestroyerProbe);
    }
}
