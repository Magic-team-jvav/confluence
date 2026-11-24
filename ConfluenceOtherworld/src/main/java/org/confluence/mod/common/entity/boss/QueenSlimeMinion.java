package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.EnemyTargeting;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;
import org.confluence.mod.common.entity.projectile.SlimeSpikeEntity;
import org.confluence.mod.common.init.entity.BossEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class QueenSlimeMinion extends BaseMonster implements BossOwnedEntity {
    // 生成点离女皇中心的最大水平距离，按女皇碰撞箱宽度取比例。
    private static final double SPAWN_RADIUS_FACTOR = 0.4;
    // 生成点在女皇碰撞箱中的相对高度，0 为底部，1 为顶部。
    private static final double SPAWN_HEIGHT_FACTOR = 0.5;
    // 仆从生成时向外弹出的水平初速，单位为格/刻。
    private static final double SPAWN_HORIZONTAL_SPEED = 0.2;
    // 生成时随机向上弹出的初速上限，单位为格/刻。
    private static final double SPAWN_VERTICAL_SPEED = 0.55;
    // 预判可采信的单刻位移平方上限（格²），超过或达到此值时清空速度估计。
    private static final double TARGET_STEP_LIMIT_SQR = 4;
    // 最新位移在目标速度估计中的权重；越大，预判越快跟随目标转向。
    private static final double TARGET_MOTION_SMOOTHING = 0.5;
    // 与目标的水平距离平方小于等于此值（格²）时保持朝向，避免贴身抖动。
    private static final double TURN_DISTANCE_SQR = 0.25;
    // 飞翔史莱姆每刻以剩余距离的这个比例计算追击速度，接近目标时自然减速。
    private static final double FLIGHT_APPROACH = 0.18;
    // 飞翔追击和避让合成后的目标速度上限，单位为格/刻。
    private static final double FLIGHT_SPEED = 1.15;
    // 飞翔史莱姆开始避让同伴的距离，以自身碰撞箱宽度为单位。
    private static final double FLIGHT_SPACING = 1.6;
    // 同伴越近，避让速度越大；此值为单个同伴的最大贡献，单位为格/刻。
    private static final double FLIGHT_SEPARATION = 0.8;
    // 飞翔史莱姆每刻向目标速度靠拢的比例，越大转向越快。
    private static final double FLIGHT_STEERING = 0.3;
    // 地面仆从允许开火的最大水平距离，单位为格。
    private static final double SHOT_RANGE = 64;
    // 地面仆从允许开火的最大垂直高度差，单位为格。
    private static final double SHOT_HEIGHT = 64;
    // 起跳前将碰撞箱向外扩展的距离（格），范围内的地面同伴会影响起跳方向。
    private static final double GROUND_SPACING = 2;
    // 计算地面避让权重时使用的最小距离（格），限制重叠同伴对方向的影响。
    private static final double MIN_SEPARATION_DISTANCE = 0.5;
    // 地面仆从起跳时沿追击与避让方向的水平初速，单位为格/刻。
    private static final double HOP_SPEED = 0.9;
    // 地面仆从起跳时的竖直初速，单位为格/刻。
    private static final double HOP_LIFT = 0.58;
    // 同一轮相邻两枚水晶尖刺的预计落点间距，单位为格。
    private static final double SPIKE_SPREAD = 1.25;
    // 暂时找不到已绑定的女皇时，允许等待其区块重新加载的刻数。
    private static final int OWNER_TIMEOUT = 100;
    // 面向目标时每刻允许转过的最大水平角度，单位为度。
    private static final int TURN_SPEED = 14;
    // 两次起跳之间至少累计的着地刻数。
    private static final int HOP_INTERVAL = 10;
    // 按实体编号额外等待 0 到此值减一刻，使同批仆从错开起跳。
    private static final int HOP_STAGGER = 9;
    // 计算水晶尖刺抛物线时采用的最短飞行时间，单位为刻。
    private static final int SPIKE_MIN_FLIGHT = 48;
    // 按目标高度差估算飞行时间后额外预留的刻数，用于形成下落段。
    private static final int SPIKE_EXTRA_FLIGHT = 6;
    // 传给难度换算的水晶尖刺基础伤害。
    private static final int SPIKE_DAMAGE = 18;
    // 一轮齐射的尖刺数，落点以预判位置为中心横向排开。
    private static final int SPIKE_COUNT = 3;
    // 专家及以上难度下，两轮水晶尖刺之间的最短间隔，单位为刻。
    private static final int SPIKE_EXPERT_COOLDOWN = 18;
    // 普通难度下，两轮水晶尖刺之间的最短间隔，单位为刻。
    private static final int SPIKE_COOLDOWN = 34;
    // 专家及以上难度下，两发弹力凝胶之间的最短间隔，单位为刻。
    private static final int GEL_EXPERT_COOLDOWN = 20;
    // 普通难度下，两发弹力凝胶之间的最短间隔，单位为刻。
    private static final int GEL_COOLDOWN = 28;
    // 弹力凝胶向未来搜索拦截点的最大刻数。
    private static final int GEL_PREDICTION_TICKS = 100;
    // 仆从身体动画切换时的过渡时长，单位为刻。
    private static final int ANIMATION_BLEND = 2;
    // 弹力凝胶预判拦截时允许的最大发射速度，单位为格/刻。
    private static final double GEL_SPEED = 1.25;
    // 高度变化只做短时外推，避免把一次起跳预测成持续爬升。
    private static final int VERTICAL_LEAD_TICKS = 8;

    private final BossOwnerTracker<QueenSlime> ownerTracker = new BossOwnerTracker<>(QueenSlime.class);

    // 高于普通跳跃可触及的范围时，地面仆从在腾空期间也能对空开火。
    private static final double ANTI_AIR_HEIGHT = 3.0;

    // 侧飞时与目标的水平间距，单位为格。
    private static final double FLANK_RADIUS = 4.0;
    // 在玩家周围抽样的方向数；错开起始角，避免形成固定阵型。
    private static final int FLANK_SAMPLES = 12;
    // 用最近的移动方向预测六刻后的位置，围堵点随玩家一起移动。
    private static final int FLANK_LEAD = 6;
    // 到达侧翼后直接撞击；地形阻挡时也只尝试有限时间。
    private static final int FLANK_TIMEOUT = 30, STRIKE_TICKS = 16;
    // 靠近围堵点至这个距离（格）即可转入撞击。
    private static final double FLANK_ARRIVAL = 1.5;
    private Vec3 flankOffset;
    private int flankTicks;
    private int strikeTicks;
    private boolean touchingLastTick;

    // 女皇没有合格玩家目标时，仆从独立搜索玩家的半径（格）。
    public static final double FOLLOW_RANGE = 160;
    // 每次接球提供四发，每两发之间间隔八刻。
    private static final int STORED_SHOTS = 4;
    private static final int STORED_INTERVAL = 8;
    private int shotsLeft;
    private int releaseDelay;
    private RoyalGel.RelayChain storedChain;

    Vec3 motionOf(LivingEntity target) {
        return trackedTarget == target.getId() ? targetMotion : target.getDeltaMovement();
    }

    void storeGel(RoyalGel.RelayChain chain) {
        storedChain = chain;
        shotsLeft = STORED_SHOTS;
    }

    private int trackedTarget;
    private Vec3 lastPosition;
    private Vec3 targetMotion = Vec3.ZERO;
    private int shotCooldown;
    private int groundTicks;

    public QueenSlimeMinion(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public void attach(QueenSlime queen) {
        double angle = random.nextDouble() * Math.PI * 2;
        double radius = random.nextDouble() * queen.getBbWidth() * SPAWN_RADIUS_FACTOR;
        setPos(queen.getX() + Math.cos(angle) * radius, queen.getY(SPAWN_HEIGHT_FACTOR), queen.getZ() + Math.sin(angle) * radius);
        setDeltaMovement(Math.cos(angle) * SPAWN_HORIZONTAL_SPEED, random.nextDouble() * SPAWN_VERTICAL_SPEED, Math.sin(angle) * SPAWN_HORIZONTAL_SPEED);
        ownerTracker.bind(this, queen);
    }

    @Override
    public QueenSlime getBossOwner() {return ownerTracker.resolve(this);}

    @Override
    protected boolean usesSharedTargeting() {return false;}

    @Override
    protected boolean hasEntityContactAttack() {return true;}

    // 接触检测保留半格余量，使实体互相推开时仍能完成这一趟攻击。
    private static final double CONTACT_MARGIN = 0.5;

    @Override
    protected double contactAttackInflation() {
        return getType() == BossEntities.HEAVENLY_SLIME.get() ? CONTACT_MARGIN : super.contactAttackInflation();
    }

    private void fight() {
        QueenSlime queen = ownerTracker.tickDependent(this, true, OWNER_TIMEOUT);
        if (queen == null) return;
        LivingEntity target = queen.getAuthoritativeLivingTarget();
        if (target == null || !canAttack(target) || !EnemyTargeting.allowsTarget(this, target)) {
            lastPosition = null;
            setTarget(null);
            setNoGravity(false);
            groundTicks = 0;
            return;
        }

        setTarget(target);
        Vec3 step = lastPosition == null || trackedTarget != target.getId() ? Vec3.ZERO : target.position().subtract(lastPosition);
        targetMotion = lastPosition != null && trackedTarget == target.getId() && step.lengthSqr() < TARGET_STEP_LIMIT_SQR
                ? targetMotion.lerp(step, TARGET_MOTION_SMOOTHING) : Vec3.ZERO;
        if (lastPosition == null || trackedTarget != target.getId()) {
            flankOffset = null;
            strikeTicks = 0;
            touchingLastTick = false;
        }
        trackedTarget = target.getId();
        lastPosition = target.position();
        Vec3 aim = target.getBoundingBox().getCenter().subtract(getBoundingBox().getCenter());
        Vec3 horizontal = new Vec3(aim.x, 0, aim.z).normalize();


        if (getType() == BossEntities.HEAVENLY_SLIME.get()) {
            setNoGravity(true);
            if (shotsLeft > 0 && --releaseDelay <= 0) {
                RoyalGel.releaseStored(queen, this, target, targetMotion, storedChain);
                releaseDelay = STORED_INTERVAL;
                if (--shotsLeft == 0) storedChain = null;
            }
            Vec3 center = getBoundingBox().getCenter();
            Vec3 predicted = center.add(aim).add(targetMotion.scale(FLANK_LEAD));
            // 触及目标即结束本轮冲撞，目标处于受伤无敌帧时也会脱离。
            boolean touching = getBoundingBox().inflate(CONTACT_MARGIN).intersects(target.getBoundingBox());
            if (strikeTicks > 0 && touching && !touchingLastTick) {
                strikeTicks = 0;
            }
            touchingLastTick = touching;
            if (flankOffset == null && strikeTicks == 0) {
                flankOffset = chooseFlank(target, predicted);
                flankTicks = FLANK_TIMEOUT;
                if (flankOffset == null) strikeTicks = STRIKE_TICKS;
            }
            Vec3 direction = touching ? Vec3.ZERO : aim;
            if (flankOffset != null) {
                direction = predicted.add(flankOffset).subtract(center);
                if (direction.lengthSqr() <= FLANK_ARRIVAL * FLANK_ARRIVAL && hasLineOfSight(target)) {
                    flankOffset = null;
                    strikeTicks = STRIKE_TICKS;
                    direction = aim;
                } else if (--flankTicks <= 0) {
                    flankOffset = null;
                }
            }
            if (strikeTicks > 0) strikeTicks--;
            Vec3 desired = direction.scale(FLIGHT_APPROACH);
            if (desired.lengthSqr() > FLIGHT_SPEED * FLIGHT_SPEED)
                desired = desired.normalize().scale(FLIGHT_SPEED);
            double spacing = getBbWidth() * FLIGHT_SPACING;
            for (QueenSlimeMinion neighbor : level().getEntitiesOfClass(QueenSlimeMinion.class,
                    getBoundingBox().inflate(spacing), other -> other != this && other.getType() == getType() && other.isAlive())) {
                Vec3 away = position().subtract(neighbor.position());
                double distance = away.length();
                if (distance >= spacing) continue;
                if (distance < 0.0001) {
                    // 完全重叠时给双方相反的方向，避免零向量让它们一直贴在一起。
                    double angle = (getId() + neighbor.getId()) * 2.399963;
                    away = new Vec3(Math.cos(angle), 0, Math.sin(angle)).scale(getId() < neighbor.getId() ? 1 : -1);
                } else {
                    away = away.scale(1 / distance);
                }
                desired = desired.add(away.scale(FLIGHT_SEPARATION * (1 - distance / spacing)));
            }
            if (desired.lengthSqr() > FLIGHT_SPEED * FLIGHT_SPEED)
                desired = desired.normalize().scale(FLIGHT_SPEED);
            Vec3 motion = getDeltaMovement().lerp(desired, FLIGHT_STEERING);
            if (motion.horizontalDistanceSqr() > 0.0001) faceCombatDirection(motion, TURN_SPEED, 0);
            setDeltaMovement(motion);
            return;
        }

        if (aim.horizontalDistanceSqr() > TURN_DISTANCE_SQR)
            faceCombatDirection(horizontal, TURN_SPEED, 0);
        setNoGravity(false);
        if (shotCooldown > 0) shotCooldown--;
        boolean canShoot = !isInWater() && aim.horizontalDistance() < SHOT_RANGE
                && Math.abs(aim.y) < SHOT_HEIGHT && hasLineOfSight(target);
        if (canShoot && (onGround() || aim.y > ANTI_AIR_HEIGHT) && shotCooldown == 0)
            fireAt(queen, aim);
        if (!onGround()) return;
        if (++groundTicks >= HOP_INTERVAL + Math.floorMod(getId(), HOP_STAGGER)) {
            Vec3 heading = horizontal;
            for (QueenSlimeMinion other : level().getEntitiesOfClass(QueenSlimeMinion.class,
                    getBoundingBox().inflate(GROUND_SPACING), other -> other != this && other.onGround())) {
                Vec3 away = position().subtract(other.position()).multiply(1, 0, 1);
                if (away.lengthSqr() < 0.0001) {
                    double angle = (getId() + other.getId()) * 2.4;
                    away = new Vec3(Math.cos(angle), 0, Math.sin(angle)).scale(getId() < other.getId() ? 1 : -1);
                }
                heading = heading.add(away.normalize().scale(1.0 / Math.max(MIN_SEPARATION_DISTANCE, away.length())));
            }
            setDeltaMovement(heading.normalize().scale(HOP_SPEED).add(0, HOP_LIFT, 0));
            groundTicks = 0;
        }
    }

    private Vec3 chooseFlank(LivingEntity target, Vec3 predicted) {
        var others = level().getEntitiesOfClass(QueenSlimeMinion.class, target.getBoundingBox().inflate(FLANK_RADIUS * 3),
                m -> m != this && m.isAlive() && m.getType() == getType() && m.getTarget() == target);
        double start = random.nextDouble() * Math.PI * 2;
        double bestScore = Double.NEGATIVE_INFINITY;
        Vec3 best = null;
        for (int i = 0; i < FLANK_SAMPLES; i++) {
            double angle = start + i * Math.PI * 2 / FLANK_SAMPLES;
            // 围堵点按飞翔史莱姆的体型对齐脚底，较高的模型也能在平地找到侧翼。
            double height = Math.max(0, (getBbHeight() - target.getBbHeight()) * 0.5);
            Vec3 offset = new Vec3(Math.cos(angle) * FLANK_RADIUS, height, Math.sin(angle) * FLANK_RADIUS);
            Vec3 candidate = predicted.add(offset);
            if (!level().noCollision(this, getBoundingBox().move(candidate.subtract(getBoundingBox().getCenter()))))
                continue;
            if (level().clip(new ClipContext(candidate, target.getBoundingBox().getCenter(), ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS) continue;
            double clearance = FLANK_RADIUS * 2;
            for (QueenSlimeMinion other : others) {
                Vec3 occupied = other.flankOffset == null ? other.position() : predicted.add(other.flankOffset);
                clearance = Math.min(clearance, candidate.distanceTo(occupied));
            }
            double score = clearance - candidate.distanceTo(position()) / FLANK_SAMPLES;
            if (score > bestScore) {
                bestScore = score;
                best = offset;
            }
        }
        return best;
    }

    private void fireAt(QueenSlime queen, Vec3 aim) {
        boolean crystal = getType() == BossEntities.CRYSTAL_SLIME.get();
        if (crystal) {
            Vec3 origin = new Vec3(getX(), getY() + getEyeHeight() * 0.5, getZ());
            Vec3 offset = getTarget().getBoundingBox().getCenter().subtract(origin)
                    .add(0, targetMotion.y * Math.min(SPIKE_MIN_FLIGHT, VERTICAL_LEAD_TICKS), 0);
            int arcTicks = (int) Math.ceil(Math.sqrt(2 * Math.abs(offset.y) / SlimeSpikeEntity.CRYSTAL_GRAVITY));
            int flightTicks = Math.min(SlimeSpikeEntity.CRYSTAL_LIFETIME, Math.max(SPIKE_MIN_FLIGHT, arcTicks + SPIKE_EXTRA_FLIGHT));
            offset = offset.add(targetMotion.multiply(flightTicks, 0, flightTicks));
            // 每刻先移动再受重力，初速度按预计落点反算，使尖刺在下降段经过玩家。
            Vec3 velocity = offset.scale(1.0 / flightTicks).add(0, SlimeSpikeEntity.CRYSTAL_GRAVITY * (flightTicks - 1) / 2, 0);
            Vec3 lateral = new Vec3(-offset.z, 0, offset.x).normalize();
            if (lateral.lengthSqr() == 0) lateral = new Vec3(1, 0, 0);
            float damage = (float) CreatureDifficultyScaling.projectileDamage(this, SPIKE_DAMAGE, false);
            for (int i = 0; i < SPIKE_COUNT; i++) {
                Vec3 shot = velocity.add(lateral.scale((i - (SPIKE_COUNT - 1) * 0.5) * SPIKE_SPREAD / flightTicks));
                level().addFreshEntity(SlimeSpikeEntity.create(level(), this, BossEntities.CRYSTAL_SPIKE.get(),
                        shot.x, shot.y, shot.z, (float) shot.length(), 0, damage, SlimeSpikeEntity.Variant.NORMAL, true));
            }
            shotCooldown = queen.isExpert() ? SPIKE_EXPERT_COOLDOWN : SPIKE_COOLDOWN;
        } else {
            Vec3 launchVelocity = Vec3.ZERO;
            double nearestMiss = Double.POSITIVE_INFINITY;
            for (int ticks = 1; ticks <= GEL_PREDICTION_TICKS; ticks++) {
                int fallingTicks = Math.max(0, ticks - RoyalGel.GRAVITY_DELAY);
                double drop = RoyalGel.BOUNCY_GRAVITY * fallingTicks * (fallingTicks + 1) / 2;
                Vec3 intercept = aim.add(targetMotion.multiply(ticks, Math.min(ticks, VERTICAL_LEAD_TICKS), ticks)).add(0, drop, 0);
                double distance = intercept.length();
                double miss = distance - GEL_SPEED * ticks;
                if (miss <= 0) {
                    launchVelocity = intercept.scale(1.0 / ticks);
                    break;
                }
                // 目标超出可拦截范围时，以满速瞄准预计偏差最小的位置。
                if (miss < nearestMiss) {
                    nearestMiss = miss;
                    launchVelocity = intercept.scale(GEL_SPEED / distance);
                }
            }
            RoyalGel.launch(this, launchVelocity);
            shotCooldown = queen.isExpert() ? GEL_EXPERT_COOLDOWN : GEL_COOLDOWN;
        }
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    @Override
                    public BTStatus execute() {
                        fight();
                        return BTStatus.RUNNING;
                    }
                };
            }
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ownerTracker.save(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerTracker.load(tag);
    }

    @Override
    public void remove(RemovalReason reason) {
        ownerTracker.unbind(this);
        super.remove(reason);
    }

    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation JUMP = RawAnimation.begin().thenLoop("jump");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "body", ANIMATION_BLEND, state -> state.setAndContinue(
                getType() == BossEntities.HEAVENLY_SLIME.get() ? FLY : onGround() ? IDLE : JUMP)));
    }
}
