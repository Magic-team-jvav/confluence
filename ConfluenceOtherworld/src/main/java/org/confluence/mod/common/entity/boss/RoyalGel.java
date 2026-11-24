package org.confluence.mod.common.entity.boss;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;
import org.confluence.mod.common.init.entity.BossEntities;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RoyalGel extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    // 普通弹道在发射后先平飞的刻数，预判从下一刻起计入下坠。
    static final int GRAVITY_DELAY = 2;
    // 普通凝胶每刻损失的竖直速度，单位为格/刻²。
    static final double GRAVITY = 0.084375;
    // 弹力凝胶采用较平的弹道，以现有初速也能打到高空玩家；发射预判使用同一重力。
    static final double BOUNCY_GRAVITY = 0.009;
    // 女皇凝胶经仆从弹射后追踪玩家的速度，单位为格/刻。
    private static final double RELAY_SPEED = 8.0;
    // 凝胶的飞行时限（刻），通过新的仆从弹射时重新计时。
    private static final int FLIGHT_TICKS = 120;
    private int impactsLeft = 3;
    private final Set<UUID> hitTargets = new HashSet<>();
    private int remainingTicks = FLIGHT_TICKS;
    // 初次齐射由三发瞄准弹和五发传递弹组成；水晶接球后向八方散射。
    private static final int AIMED_COUNT = 3, RELAY_COUNT = 5, SPLIT_COUNT = 8;
    // 初速（格/刻）、瞄准弹间隔角（弧度）和寻找接球仆从的半径（格）。
    private static final double LAUNCH_SPEED = 3.5, FAN_ANGLE = 0.085, RECEIVER_RANGE = 96;
    // 传递弹碰到方块可反弹两至四次，每次有 75% 概率尝试重新瞄准玩家。
    private static final int MIN_BOUNCES = 2, MAX_BOUNCES = 4;
    private static final float RETARGET_CHANCE = 0.75F;

    // 每发初始弹及其后代共享已接球名单，使水晶分裂和飞翔连射可继续传递但不会循环。
    static class RelayChain {
        final Set<UUID> visited = new HashSet<>();
    }

    private RelayChain chain = new RelayChain();
    private boolean relayed;
    private boolean doubleDamage;

    public RoyalGel(EntityType<? extends RoyalGel> type, Level level) {super(type, level);}

    static void launch(LivingEntity owner, Vec3 velocity) {
        RoyalGel gel = new RoyalGel(owner instanceof QueenSlimeMinion ? BossEntities.BOUNCY_GEL.get() : BossEntities.ROYAL_GEL.get(), owner.level());
        gel.setOwner(owner);
        if (owner instanceof QueenSlimeMinion)
            gel.impactsLeft = owner.getType() == BossEntities.CRYSTAL_SLIME.get() ? 1 : 3;
        gel.setPos(owner.getBoundingBox().getCenter());
        gel.setDeltaMovement(velocity);
        owner.level().addFreshEntity(gel);
    }

    private static Vec3 aimed(Vec3 from, LivingEntity target, Vec3 motion, double speed) {
        Vec3 delta = target.getBoundingBox().getCenter().subtract(from);
        return delta.add(motion.scale(delta.length() / speed)).normalize().scale(speed);
    }

    static void volley(QueenSlime queen, LivingEntity target) {
        Vec3 from = queen.getBoundingBox().getCenter();
        var receivers = queen.level().getEntitiesOfClass(QueenSlimeMinion.class,
                queen.getBoundingBox().inflate(RECEIVER_RANGE), m -> m.isAlive() && m.getBossOwner() == queen && queen.hasLineOfSight(m));
        receivers.sort(java.util.Comparator.comparingDouble(m -> queen.distanceToSqr(m)));
        Vec3 forward = aimed(from, target, target.getDeltaMovement(), LAUNCH_SPEED);
        for (int i = 0; i < AIMED_COUNT; i++) {
            launch(queen, forward.yRot((float) ((i - 1) * FAN_ANGLE)));
        }
        double heading = Math.atan2(forward.z, forward.x);
        for (int i = 0; i < RELAY_COUNT; i++) {
            double angle = heading + (i + 0.5) * Math.PI * 2 / RELAY_COUNT;
            Vec3 shot = new Vec3(Math.cos(angle), 0.2, Math.sin(angle)).normalize().scale(LAUNCH_SPEED);
            if (i < receivers.size()) {
                QueenSlimeMinion receiver = receivers.get(i);
                shot = aimed(from, receiver, receiver.getDeltaMovement(), LAUNCH_SPEED);
            }
            launch(queen, shot);
        }
    }

    private static void launchRelay(QueenSlime queen, Vec3 from, Vec3 velocity, RelayChain chain, boolean doubleDamage) {
        RoyalGel gel = new RoyalGel(BossEntities.ROYAL_GEL.get(), queen.level());
        gel.setOwner(queen);
        gel.setPos(from);
        gel.setDeltaMovement(velocity);
        gel.chain = chain;
        gel.relayed = true;
        gel.doubleDamage = doubleDamage;
        gel.impactsLeft = 1 + MIN_BOUNCES + queen.getRandom().nextInt(MAX_BOUNCES - MIN_BOUNCES + 1);
        queen.level().addFreshEntity(gel);
    }

    static void releaseStored(QueenSlime queen, QueenSlimeMinion minion, LivingEntity target, Vec3 motion, RelayChain chain) {
        Vec3 from = minion.getBoundingBox().getCenter();
        launchRelay(queen, from, aimed(from, target, motion, RELAY_SPEED), chain, false);
    }

    private void relay(QueenSlime queen, QueenSlimeMinion minion, LivingEntity target) {
        chain.visited.add(minion.getUUID());
        Vec3 from = minion.getBoundingBox().getCenter();
        Vec3 aim = aimed(from, target, minion.motionOf(target), RELAY_SPEED);
        if (minion.getType() == BossEntities.HEAVENLY_SLIME.get()) {
            minion.storeGel(chain);
        } else if (minion.getType() == BossEntities.CRYSTAL_SLIME.get()) {
            if (aim.lengthSqr() < 1.0E-8)
                aim = Vec3.directionFromRotation(0, minion.getYRot()).scale(RELAY_SPEED);
            // 让散射平面随预判方向倾斜，目标在正上方时八条弹道仍然分开。
            double heading = Math.atan2(aim.z, aim.x);
            Vec3 side = new Vec3(-Math.sin(heading), 0, Math.cos(heading)).scale(RELAY_SPEED);
            for (int i = 0; i < SPLIT_COUNT; i++) {
                double angle = i * Math.PI * 2 / SPLIT_COUNT;
                Vec3 shot = aim.scale(Math.cos(angle)).add(side.scale(Math.sin(angle)));
                launchRelay(queen, from, shot, chain, false);
            }
        } else {
            launchRelay(queen, from, aim, chain, true);
        }
        discard();
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 from = position();
        if (!level().isClientSide && !relayed && tickCount > GRAVITY_DELAY) {
            setDeltaMovement(getDeltaMovement().add(0, getType() == BossEntities.BOUNCY_GEL.get() ? -BOUNCY_GRAVITY : -GRAVITY, 0));
            hasImpulse = true;
        }
        Vec3 end = from.add(getDeltaMovement());
        if (!level().isClientSide) {
            if (--remainingTicks <= 0 || !(getOwner() instanceof Mob source) || !source.isAlive()) {
                discard();
                return;
            }
            QueenSlime queen = source instanceof QueenSlime q ? q : source instanceof QueenSlimeMinion m ? m.getBossOwner() : null;
            if (queen == null) {
                discard();
                return;
            }
            LivingEntity target = queen.getAuthoritativeLivingTarget();
            boolean hasTarget = target != null;
            BlockHitResult block = level().clip(new ClipContext(from, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            boolean canRelay = hasTarget && source == queen;
            var hit = ProjectileUtil.getEntityHitResult(level(), this, from, block.getLocation(),
                    getBoundingBox().expandTowards(getDeltaMovement()).inflate(0.4),
                    e -> e != source && (canRelay && e instanceof QueenSlimeMinion m && m.isAlive() && m.getBossOwner() == queen && !chain.visited.contains(m.getUUID())
                            || e instanceof LivingEntity living && (living == target || living instanceof Player)
                            && !hitTargets.contains(living.getUUID()) && queen.canAttack(living)));
            if (hit != null) {
                switch (hit.getEntity()) {
                    case QueenSlimeMinion minion -> {
                        relay(queen, minion, target);
                    }
                    case LivingEntity living -> {
                        living.hurt(damageSources().mobProjectile(this, source), (float) CreatureDifficultyScaling.projectileDamage(source, source instanceof QueenSlime ? 32 : 18, false) * (doubleDamage ? 2 : 1));
                        hitTargets.add(living.getUUID());
                        if (source.getType() != BossEntities.BOUNCY_SLIME.get() || --impactsLeft <= 0)
                            discard();
                        else setPos(hit.getLocation());
                    }
                    default -> {}
                }
                return;
            }
            if (block.getType() != HitResult.Type.MISS) {
                if (--impactsLeft <= 0) {
                    discard();
                    return;
                }
                Vec3 normal = Vec3.atLowerCornerOf(block.getDirection().getNormal());
                Vec3 motion = getDeltaMovement();
                setPos(block.getLocation().add(normal.scale(0.02)));
                setDeltaMovement(motion.subtract(normal.scale((normal.y == 0 ? 1.4 : 1.95) * motion.dot(normal))));
                if (relayed) {
                    Vec3 reflected = getDeltaMovement().normalize().scale(RELAY_SPEED);
                    if (hasTarget && random.nextFloat() < RETARGET_CHANCE) {
                        Vec3 toward = aimed(position(), target, target.getDeltaMovement(), RELAY_SPEED);
                        if (toward.dot(normal) > 0) reflected = toward;
                    }
                    setDeltaMovement(reflected);
                }
                hasImpulse = true;
                return;
            }
        }
        setPos(end);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {return new ClientboundAddEntityPacket(this, entity);}
}
