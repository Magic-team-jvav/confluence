package org.confluence.terraentity.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Aim Utils的作用是根据 实体的位置/预判射出的位置，要预判的实体以及预判的设置 进行预判 <br>
 * 注意：本类的返回值是瞄准的速度；详见各部分的docs。
 */
public class AimUtils {
    public static int MAX_TICKS = 60; // 弹道预判最长时间，单位：刻

    /**
     * 预判的设置；可对于一类预判类型通用（注：不同弓箭之间的速度、加速度等可能有细微差别，注意甄别）<br>
     * 注意：ticksMonsterExtra的用法要取决于使用案例。若是玩家发射弹幕则弹幕发射后会被调用一次tick；
     * 若是叶绿箭撞墙弹回则被预判的怪物相当于多了一次移动tick，此时建议将ticksMonsterExtra设为1（注：老羊在1.12.2的观察所得；以对高速单位的实践经验为准）。 <br>
     * 建议在预判不精准时，确认箭矢的重力加速度/速度乘数/起始速度 被正确设置，而后考虑调整ticksMonsterExtra。
     */
    public static class AimHelperOptions {
        double projectileGravity = 0.05d, // 箭矢的重力加速度
                projectileSpeed = 0d, // 箭矢的起始速度；**需要设置！**
                projectileSpeedMax = 99d, // 箭矢最大速度（仅：恶魔锄刀等加速弹幕）
                projectileSpeedMulti = 0.99d, // 箭矢每刻速度乘数
                randomOffsetRadius = 0d, // 追踪位置的随机偏移，单位：格
                ticksTotal = 10, // 若预判方案为固定tick（MC刻），则预判实体在此时间后的位置。
                ticksMonsterExtra = 0; // 根据MC实体tick的先后顺序，有时怪物会多出一次移动tick，此时将其设为1
        boolean useAcceleration = false, useTickEstimation = false;
        int epoch = 5, // 对弹道预判的逼近循环次数上限（收敛时会提前结束）
                noGravityTicks = 0; // 若弹幕前几tick无重力影响，请更新此特性。
        // 敌人加速度的额外偏移量
        Vec3 accelerationOffset = new Vec3(0, 0, 0);
        // 构造器
        public AimHelperOptions() {
            super();
        }
        // 构造器 - 可后续调整；对于特殊的弹射物类型酌情设置重力、速度乘数信息
        public AimHelperOptions(Projectile projectile) {
            this();
            // TODO 完善此处，使构造器实现的效果与弹幕本身一致
            setProjectileGravity(projectile.getGravity());
        }

        public AimHelperOptions setTicksTotal(double ticksTotal) {
            this.ticksTotal = ticksTotal;
            return this;
        }
        public AimHelperOptions setTicksMonsterExtra(double ticksMonsterExtra) {
            this.ticksMonsterExtra = ticksMonsterExtra;
            return this;
        }
        public AimHelperOptions setProjectileGravity(double projectileGravity) {
            this.projectileGravity = projectileGravity;
            return this;
        }
        public AimHelperOptions setProjectileSpeed(double projectileSpeed) {
            this.projectileSpeed = projectileSpeed;
            return this;
        }
        public AimHelperOptions setProjectileSpeedMax(double projectileSpeedMax) {
            this.projectileSpeedMax = projectileSpeedMax;
            return this;
        }
        public AimHelperOptions setProjectileSpeedMulti(double projectileSpeedMulti) {
            this.projectileSpeedMulti = projectileSpeedMulti;
            return this;
        }
        public AimHelperOptions setRandomOffsetRadius(double randomOffsetRadius) {
            this.randomOffsetRadius = randomOffsetRadius;
            return this;
        }
        public AimHelperOptions setEpoch(int epoch) {
            this.epoch = epoch;
            return this;
        }
        public AimHelperOptions setNoGravityTicks(int noGravityTicks) {
            this.noGravityTicks = noGravityTicks;
            return this;
        }
        public AimHelperOptions setAccelerationMode(boolean useAcceleration) {
            this.useAcceleration = useAcceleration;
            return this;
        }
        public AimHelperOptions setAimMode(boolean useTickEstimation) {
            this.useTickEstimation = useTickEstimation;
            return this;
        }
        public AimHelperOptions setAccOffset(Vec3 accelerationOffset) {
            this.accelerationOffset = accelerationOffset;
            return this;
        }
        public AimHelperOptions addAccOffset(Vec3 accelerationOffset) {
            this.accelerationOffset.add(accelerationOffset);
            return this;
        }
        public AimHelperOptions subtractAccOffset(Vec3 accelerationOffset) {
            this.accelerationOffset.subtract(accelerationOffset);
            return this;
        }
    }

    /**
     * 取得实体的速度用来进行预判。
     * TODO：未来进行boss预判冲刺时，玩家的速度可能需要特殊处理。
     * @param entity 要计算的实体
     * @return 实体的速度
     */
    private static Vec3 getEntityVelocity(Entity entity) {
        return entity.getDeltaMovement();
    }

    /**
     * 取得实体的加速度用来进行预判。
     * TODO：需要迷信一下来让实体类存储上一刻的速度用以计算加速度。
     * @param entity 要计算的实体
     * @return 实体的加速度
     */
    private static Vec3 getEntityAcceleration(Entity entity) {
        // TODO: Mixin this...
        Vec3 lastVel = entity.getDeltaMovement();
        Vec3 currVel = entity.getDeltaMovement();
        return currVel.subtract(lastVel);
    }


    /**
     * 预测实体在一定tick后的位置；注意，本方法返回位置而非速度。
     * @param target 预判的实体
     * @param enemyVel 实体的速度
     * @param enemyAcc 实体的加速度；不使用加速度则设为(0,0,0)
     * @param ticksMonsterMovement 预判时长
     * @param physics 是否计算方块碰撞
     * @return 预判结果位置
     */
    public static Vec3 predictEntityPositionAfter(Entity target, Vec3 enemyVel, Vec3 enemyAcc, int ticksMonsterMovement, boolean physics) {
        Vec3 predictedLoc = target.getBoundingBox().getCenter();
        // 速度导致的位移
        predictedLoc = predictedLoc.add(enemyVel.scale(ticksMonsterMovement));
        // 加速度导致的位移
        // 第一刻的加速度影响剩余n-1刻；第二刻影响剩余n-2，以此类推
        // 即，加速度导致的位移为：sum(1, 2, ..., n-2, n-1) = n(n-1) / 2
        double accFactor = ticksMonsterMovement * (ticksMonsterMovement - 1) / 2d;
        predictedLoc = predictedLoc.add(enemyAcc.scale(accFactor));
        // 方块碰撞逻辑；撞到方块时速度的相应方向改为0
        if ( physics ) {
            Vec3 loopBeginLoc = target.position().add(0, 1e-5, 0);
            Vec3 loopEndLoc = predictedLoc;
            Level level = target.level();
            // 最多计算3次碰撞（三次后X,Y,Z速度都为0）
            for (int blockCheckIdx = 0; blockCheckIdx < 3; blockCheckIdx ++) {
                // 起始结束位置非常相近时提前结束循环
                if (loopBeginLoc.distanceToSqr(loopEndLoc) < 1e-5)
                    break;
                BlockHitResult blockCollInfo = level.clip(new ClipContext(loopBeginLoc, loopEndLoc, net.minecraft.world.level.ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, target));
                // 无方块碰撞，结束循环
                if (blockCollInfo.getType() == HitResult.Type.MISS)
                    break;

                // 方块碰撞，更新速度
                Direction collDir = blockCollInfo.getDirection();
                // 向碰撞方向对侧略微移动防止浮点数精度问题造成的后续错误碰撞检测
                loopBeginLoc = blockCollInfo.getLocation().add(
                        new Vec3(collDir.getStepX(), collDir.getStepY(), collDir.getStepZ()).scale(0.1));
                Vec3 updatedMoveDir = loopEndLoc.subtract(loopBeginLoc);
                updatedMoveDir = updatedMoveDir.with(collDir.getAxis(), 0);
                // 根据新速度更新位置
                loopEndLoc = loopBeginLoc.add(updatedMoveDir);
            }
        }
        return predictedLoc;
    }


    /**
     * 瞄准实体后返回弹幕射出的速度向量；使用source的eyePosition为起始位置。<br>
     * 注意，本方法返回的是速度向量。
     * @param source 射出弹幕的实体
     * @param target 被瞄准的实体
     * @param aimHelperOption 瞄准的信息
     * @return 预判的弹幕速度向量
     */
    public static Vec3 helperAimEntity(Entity source, Entity target, AimHelperOptions aimHelperOption) {
        Vec3 shootLoc = source.getEyePosition();
        return helperAimEntity(shootLoc, target, aimHelperOption);
    }

    /**
     * 瞄准实体后返回弹幕射出的速度向量；使用自定义的起始位置。<br>
     * 注意，本方法返回的是速度向量。
     * @param shootLoc 射出弹幕的位置
     * @param target 被瞄准的实体
     * @param aimHelperOption 瞄准的信息
     * @return 预判的弹幕速度向量
     */
    public static Vec3 helperAimEntity(Vec3 shootLoc, Entity target, AimHelperOptions aimHelperOption) {
        // 确保在速度没有被初始化时抛出错误
        assert aimHelperOption.projectileSpeed != 0d;

        // 初始化敌人的速度/加速度/位置
        Vec3 enemyVel = getEntityVelocity(target);
        Vec3 enemyAcc = getEntityAcceleration(target);
        enemyAcc = enemyAcc.add(aimHelperOption.accelerationOffset); // 加速度偏移量
        Vec3 targetLoc = target.getBoundingBox().getCenter();
        // 随机偏移向量
        double randomOffset = aimHelperOption.randomOffsetRadius;
        Vec3 offsetDir = new Vec3(0, 0, 0);
        if (randomOffset > 1e-5) {
            double randomOffsetHalved = randomOffset / 2;
            offsetDir = new Vec3(Math.random() * randomOffset - randomOffsetHalved,
                    Math.random() * randomOffset - randomOffsetHalved,
                    Math.random() * randomOffset - randomOffsetHalved);
        }

        // 预测的位置
        Vec3 predictedLoc = targetLoc;
        // 不要对穿墙/坐矿车的敌人预判方块碰撞
        boolean checkBlockColl;
        Entity targetMount = target.getVehicle();
        if (targetMount == null)
            checkBlockColl = ! target.noPhysics;
        else {
            checkBlockColl = ! (targetMount instanceof Minecart);
        }

        double ticksElapse = Math.floor(targetLoc.distanceTo(shootLoc) / aimHelperOption.projectileSpeed);
        double lastTicksOffset;
        // 在至多epoch次循环逼近中试图寻找敌人被弹幕击中的位置
        for (int currEpoch = 0; currEpoch < aimHelperOption.epoch; currEpoch ++) {
            // 计算敌人移动的刻数
            double ticksMonsterMovement = ticksElapse + aimHelperOption.ticksMonsterExtra;
            // 更新敌人位置预判
            predictedLoc = predictEntityPositionAfter(target, enemyVel, enemyAcc, (int) ticksMonsterMovement, checkBlockColl);
            predictedLoc = predictedLoc.add(offsetDir);
            // 弹幕重力 - 若弹幕前几tick无重力记得更改option；noGravityTicks默认为0。
            // 为了计算简单，弹幕下坠等效于敌人上浮。
            if (ticksElapse >= aimHelperOption.noGravityTicks) {
                // 运算逻辑见predictEntityPositionAfter。
                predictedLoc = predictedLoc.add(new Vec3(0,
                        (ticksElapse - aimHelperOption.noGravityTicks + 1) * (ticksElapse - aimHelperOption.noGravityTicks + 2)
                                * aimHelperOption.projectileGravity / 2d, 0));
            }

            // 更新对击中所需时间的预判
            lastTicksOffset = ticksElapse;
            if (aimHelperOption.useTickEstimation)
                ticksElapse = aimHelperOption.ticksTotal;
            else {
                double distance = predictedLoc.distanceTo(shootLoc), currSpd = aimHelperOption.projectileSpeed;
                // 弹道速度恒定
                if (aimHelperOption.projectileSpeedMulti == 1) {
                    ticksElapse = distance / currSpd;
                }
                // 弹道存在速度倍率
                else {
                    ticksElapse = 0;
                    double distTraveled = 0;
                    // 弹幕速度递减时避免无限循环的可能性
                    while (distTraveled < distance && (aimHelperOption.projectileSpeedMulti >= 1d || ticksElapse < MAX_TICKS)) {
                        ticksElapse ++;
                        distTraveled += currSpd;
                        currSpd *= aimHelperOption.projectileSpeedMulti;
                        // 弹幕加速（如恶魔锄刀）达到最大速度后按照匀速移动计算
                        if (currSpd > aimHelperOption.projectileSpeedMax) {
                            ticksElapse += (distance - distTraveled) / aimHelperOption.projectileSpeedMax;
                            break;
                        }
                    }
                }
                // 弹道预判过久会徒增工作量且预判不可能准确；设置上限。
                ticksElapse = Math.min( Math.floor(ticksElapse),  MAX_TICKS );
            }

            // 预判所需时间与上一次循环相同，即已收敛。
            if (lastTicksOffset == ticksElapse)
                break;
        }
        // 转换为弹幕速度
        return TEUtils.getDirection(shootLoc, predictedLoc, aimHelperOption.projectileSpeed);
    }
}
