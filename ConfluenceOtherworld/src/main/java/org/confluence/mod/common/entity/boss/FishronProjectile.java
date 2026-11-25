package org.confluence.mod.common.entity.boss;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;
import org.confluence.mod.common.init.entity.BossEntities;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Map;

public class FishronProjectile extends Projectile implements GeoEntity {
    enum Kind {
        TORNADO(160, 26), STORM(0, 0), FLOOD(40, 39),
        BUBBLE(62, 39), TYPHOON(55, 41), SMALL_TYPHOON(28, 24);

        final int flightTicks;
        final int damage;

        Kind(int flightTicks, int damage) {
            this.flightTicks = flightTicks;
            this.damage = damage;
        }
    }

    // 普通龙卷风的伤害范围为圆柱，按固定间隔钻出鲨鱼龙。
    // 风体腰部半径和高度，单位为格；粒子在前 6 秒从中心展开。
    private static final double TORNADO_RADIUS = 1.15, TORNADO_HEIGHT = 12;
    // 小风眼的吸力弱于风暴，接近中心时平滑减速。
    private static final double TORNADO_PULL = 0.06, TORNADO_PULL_RADIUS = 5;
    private static final int TORNADO_GROWTH_TICKS = 120;
    private static final int TORNADO_SHARK_INTERVAL = 24, TORNADO_DAMAGE_INTERVAL = 10;
    // 泡泡爆开时的伤害半径。
    private static final double BUBBLE_BLAST_RADIUS = 2.2;
    // 利刃台风在风眼内滞留的最长刻数，离开时不再补充计时。
    private static final int STORM_TYPHOON_TICKS = 115, SLAM_FLOOD_TICKS = 85;
    // 每刻转向目标的比例，保留弹道速度。
    private static final double WEAK_HOMING = 0.035, STORM_HOMING = 0.13;
    // 鲨鱼龙初次飞出后受重力影响，接触地面或水面后按此速度再次跃起。
    private static final ResourceLocation SMALL_TORNADO_PARTICLE = Confluence.asResource("duke_fishron_tornado_vortex_1");
    private static final ResourceLocation TORNADO_PARTICLE = Confluence.asResource("duke_fishron_storm_vortex_1");
    private static final ResourceLocation FLOOD_TRAIL_PARTICLE = Confluence.asResource("duke_fishron_flood_trail_1");
    private static final EntityDataAccessor<Float> WIND_HEIGHT = SynchedEntityData.defineId(FishronProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> LARGE_TORNADO = SynchedEntityData.defineId(FishronProjectile.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private ParticleEmitter particleEmitter;
    private int expiresAt;
    private boolean stormLifetime;
    private Vec3 clientDestination = Vec3.ZERO;
    private float clientYaw, clientPitch;
    private int clientLerpSteps;

    public FishronProjectile(EntityType<? extends FishronProjectile> type, Level level) {
        super(type, level);
    }

    static FishronProjectile launch(DukeFishron owner, Kind kind, Vec3 position, Vec3 motion, boolean extended) {
        EntityType<FishronProjectile> type = switch (kind) {
            case TORNADO, STORM -> BossEntities.FISHRON_TORNADO.get();
            case FLOOD -> BossEntities.FISHRON_FLOOD.get();
            case BUBBLE -> BossEntities.DETONATING_BUBBLE.get();
            case TYPHOON -> BossEntities.RAZORBLADE_TYPHOON.get();
            case SMALL_TYPHOON -> BossEntities.SMALL_RAZORBLADE_TYPHOON.get();
        };
        FishronProjectile shot = new FishronProjectile(type, owner.level());
        shot.setOwner(owner);
        if (kind == Kind.STORM) shot.entityData.set(LARGE_TORNADO, true);
        if (kind == Kind.TORNADO || kind == Kind.STORM) {
            boolean storm = kind == Kind.STORM;
            double top = position.y + (storm ? DukeFishron.STORM_HEIGHT / 2 : TORNADO_HEIGHT);
            double bottom = storm ? position.y - DukeFishron.STORM_HEIGHT / 2 : position.y;
            Vec3 below = new Vec3(position.x, owner.level().getMinBuildHeight(), position.z);
            BlockHitResult surface = owner.level().clip(new ClipContext(position, below, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, shot));
            if (surface.getType() == HitResult.Type.BLOCK && owner.level().getFluidState(surface.getBlockPos()).is(FluidTags.WATER)) {
                bottom = surface.getLocation().y;
            }
            shot.entityData.set(WIND_HEIGHT, (float) (top - bottom));
            position = new Vec3(position.x, bottom, position.z);
        }
        shot.setPos(position);
        shot.setDeltaMovement(motion);
        shot.expiresAt = extended ? SLAM_FLOOD_TICKS : kind.flightTicks;
        if (motion.lengthSqr() > 0) shot.faceMotion(motion);
        if (!owner.level().addFreshEntity(shot)) shot.discard();
        return shot;
    }

    private Kind kind() {
        if (getType() == BossEntities.FISHRON_TORNADO.get()) {
            return entityData.get(LARGE_TORNADO) ? Kind.STORM : Kind.TORNADO;
        }
        if (getType() == BossEntities.FISHRON_FLOOD.get()) return Kind.FLOOD;
        if (getType() == BossEntities.DETONATING_BUBBLE.get()) return Kind.BUBBLE;
        if (getType() == BossEntities.SMALL_RAZORBLADE_TYPHOON.get()) return Kind.SMALL_TYPHOON;
        return Kind.TYPHOON;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) {
            tickClientInterpolation();
            updateTornadoBounds();
            tickClientParticles();
            return;
        }
        if (!(getOwner() instanceof DukeFishron duke) || !duke.isAlive() || duke.isRemoved() || !duke.allowsProjectiles()
                || !(duke.getTarget() instanceof Player target) || !duke.canAttack(target)) {
            discard();
            return;
        }

        Kind kind = kind();
        if (kind == Kind.STORM) {
            // 大龙卷风跟随公爵的风暴状态结束，玩家离开范围时也会收掉发射器。
            if (!duke.insideStorm(position())) discard();
            return;
        }
        if (kind == Kind.TORNADO) {
            updateTornadoBounds();
            tickTornado(duke);
            if (tickCount >= expiresAt) discard();
            return;
        }
        boolean insideStorm = duke.insideStorm(position());
        if (kind == Kind.TYPHOON && insideStorm && !stormLifetime) {
            stormLifetime = true;
            expiresAt = tickCount + STORM_TYPHOON_TICKS;
        }
        if (tickCount >= expiresAt) {
            expire(server, duke, kind);
            return;
        }

        Vec3 start = position();
        Vec3 motion = getDeltaMovement();
        if (insideStorm || kind == Kind.TYPHOON || kind == Kind.SMALL_TYPHOON) {
            Vec3 direction = target.getBoundingBox().getCenter().subtract(start).normalize();
            if (kind == Kind.TYPHOON && insideStorm)
                direction = duke.stormTyphoonDirection(start, direction, tickCount, getId());
            motion = motion.lerp(direction.scale(motion.length()), insideStorm ? STORM_HOMING : WEAK_HOMING).normalize().scale(motion.length());
        }

        Vec3 end = start.add(motion);
        BlockHitResult block = level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (block.getType() != HitResult.Type.MISS) end = block.getLocation();
        var hit = ProjectileUtil.getEntityHitResult(level(), this, start, end, getBoundingBox().expandTowards(motion).inflate(0.3),
                entity -> entity instanceof Player player && duke.canAttack(player));
        if (hit != null) {
            if (kind == Kind.BUBBLE || kind == Kind.FLOOD || (kind == Kind.TYPHOON && !insideStorm) || kind == Kind.SMALL_TYPHOON) {
                setPos(hit.getLocation());
                if (kind == Kind.BUBBLE) explodeBubble(server, duke);
                else {
                    damage(duke, (Player) hit.getEntity(), kind);
                    if (kind == Kind.TYPHOON && stormLifetime) expire(server, duke, kind);
                    else discard();
                }
                return;
            }
            damage(duke, (Player) hit.getEntity(), kind);
        }

        setPos(end);
        if (block.getType() != HitResult.Type.MISS) {
            Vec3 normal = Vec3.atLowerCornerOf(block.getDirection().getNormal());
            setPos(end.add(normal.scale(0.06)));
            switch (kind) {
                case BUBBLE -> {
                    explodeBubble(server, duke);
                    return;
                }
                case TYPHOON, SMALL_TYPHOON ->
                        motion = motion.subtract(normal.scale(2 * motion.dot(normal)));
                default -> {
                    discard();
                    return;
                }
            }
        }
        setDeltaMovement(motion);
        drawTrail(server, kind, motion);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps, boolean teleport) {
        if (!level().isClientSide || teleport || steps <= 0 || distanceToSqr(x, y, z) > 64 * 64) {
            setPos(x, y, z);
            setRot(yaw, pitch);
            clientLerpSteps = 0;
            return;
        }
        clientDestination = new Vec3(x, y, z);
        clientYaw = yaw;
        clientPitch = pitch;
        clientLerpSteps = steps;
    }

    // 客户端在服务端快照之间插值，入水起跳和反弹仍由服务端决定。
    private void tickClientInterpolation() {
        if (clientLerpSteps <= 0) return;
        float fraction = 1.0F / clientLerpSteps--;
        setPos(position().lerp(clientDestination, fraction));
        setRot(Mth.rotLerp(fraction, getYRot(), clientYaw), Mth.lerp(fraction, getXRot(), clientPitch));
    }

    private void tickClientParticles() {
        if (isRemoved() || (particleEmitter != null && !particleEmitter.isRemoved())) return;
        if (getType() == BossEntities.FISHRON_FLOOD.get()) {
            // 水浪跟随实际射弹，撞到玩家或障碍后停止生成尾迹。
            particleEmitter = new ParticleEmitter(level(), position(), FLOOD_TRAIL_PARTICLE);
        } else if (kind() == Kind.TORNADO || kind() == Kind.STORM) {
            boolean storm = kind() == Kind.STORM;
            String prefix = storm ? "duke_fishron_storm_" : "duke_fishron_tornado_";
            double height = entityData.get(WIND_HEIGHT);
            double width = storm ? 1 : tornadoWidth();
            startWindLayer(prefix + "mist_1", height, width, 0, 0, 0);
            startWindLayer(prefix + "stream_1", height, width, 5, 0.48, 0.86);
            startWindLayer(prefix + "stream_1", height, width, 180, 0.76, 0.96);
            particleEmitter = new ParticleEmitter(level(), position(), storm ? TORNADO_PARTICLE : SMALL_TORNADO_PARTICLE,
                    new MolangExp(Map.of("variable.height", Double.toString(height), "variable.width", Double.toString(width))));
        } else {
            return;
        }
        particleEmitter.attachEntity(this);
        MolangParticleEngine.INSTANCE.addEmitter(particleEmitter);
    }

    private void startWindLayer(String layer, double height, double width, int phase, double red, double green) {
        ParticleEmitter emitter = new ParticleEmitter(level(), position(), Confluence.asResource(layer),
                new MolangExp(Map.of("variable.height", Double.toString(height), "variable.width", Double.toString(width),
                        "variable.phase", Integer.toString(phase), "variable.red", Double.toString(red), "variable.green", Double.toString(green))));
        emitter.attachEntity(this);
        MolangParticleEngine.INSTANCE.addEmitter(emitter);
    }

    private double tornadoWidth() {
        return Math.sqrt(entityData.get(WIND_HEIGHT) / TORNADO_HEIGHT);
    }

    private void updateTornadoBounds() {
        if (kind() == Kind.TORNADO) {
            double scale = Math.min(1.0, (double) tickCount / TORNADO_GROWTH_TICKS);
            double radius = TORNADO_RADIUS * tornadoWidth() * scale;
            setBoundingBox(new AABB(getX() - radius, getY(), getZ() - radius,
                    getX() + radius, getY() + entityData.get(WIND_HEIGHT) * scale, getZ() + radius));
        }
    }

    private void tickTornado(DukeFishron duke) {
        double radius = TORNADO_PULL_RADIUS * tornadoWidth() * Math.min(1.0, (double) tickCount / TORNADO_GROWTH_TICKS);
        for (Player player : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(radius, 0, radius), duke::canAttack)) {
            Vec3 inward = position().subtract(player.position()).multiply(1, 0, 1);
            double distance = inward.length();
            if (distance >= radius || distance < 0.01) continue;
            Vec3 pull = inward.scale(TORNADO_PULL * (1 - distance / radius) / Math.max(1, distance));
            player.push(pull.x, 0, pull.z);
            player.hurtMarked = true;
        }
        if (tickCount % TORNADO_SHARK_INTERVAL == 0)
            duke.spawnShark(position().add(0, 2 + random.nextDouble() * (entityData.get(WIND_HEIGHT) - 2), 0));
        if (tickCount % TORNADO_DAMAGE_INTERVAL == 0) {
            for (Player player : level().getEntitiesOfClass(Player.class, getBoundingBox(), duke::canAttack))
                damage(duke, player, Kind.TORNADO);
        }
    }

    private void drawTrail(ServerLevel server, Kind kind, Vec3 motion) {
        if (kind != Kind.TYPHOON && kind != Kind.SMALL_TYPHOON) return;
        Vec3 forward = motion.normalize();
        Vec3 reference = Math.abs(forward.y) < 0.98 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 side = forward.cross(reference).normalize();
        Vec3 up = forward.cross(side);
        double radius = kind == Kind.TYPHOON ? 0.95 : 0.52;
        for (int i = 0; i < 6; i++) {
            double angle = tickCount * 0.53 + i * Math.PI / 3;
            Vec3 point = position().add(side.scale(Math.cos(angle) * radius)).add(up.scale(Math.sin(angle) * radius));
            server.sendParticles(ParticleTypes.SWEEP_ATTACK, point.x, point.y, point.z, 1, 0, 0, 0, 0);
        }
    }


    private void expire(ServerLevel server, DukeFishron duke, Kind kind) {
        if (kind == Kind.BUBBLE) {
            explodeBubble(server, duke);
            return;
        }
        if (kind == Kind.TYPHOON && stormLifetime) {
            for (int side : new int[]{-1, 1}) {
                launch(duke, Kind.SMALL_TYPHOON, position(), getDeltaMovement().yRot(side * 0.6F), false);
            }
        }
        discard();
    }

    private void explodeBubble(ServerLevel server, DukeFishron duke) {
        server.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 14, 0.4, 0.4, 0.4, 0.08);
        for (Player player : playersNear(duke, BUBBLE_BLAST_RADIUS))
            damage(duke, player, Kind.BUBBLE);
        discard();
    }

    private List<Player> playersNear(DukeFishron duke, double radius) {
        return level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(radius), player -> duke.canAttack(player) && distanceToSqr(player) <= radius * radius);
    }

    private void damage(DukeFishron duke, Player player, Kind kind) {
        player.hurt(damageSources().mobProjectile(this, duke), (float) CreatureDifficultyScaling.projectileDamage(duke, duke.isEnraged() ? kind.damage * 2 : kind.damage, false));
    }

    private void faceMotion(Vec3 motion) {
        LibEntityUtils.turnEntityTowards(this, motion, 180, 180);
    }

    @Override
    public boolean isPickable() {return kind() == Kind.BUBBLE;}

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide || !isPickable() || !(source.getEntity() instanceof LivingEntity) || source.getEntity() == getOwner() || amount <= 0)
            return false;
        discard();
        return true;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(LARGE_TORNADO, false);
        entityData.define(WIND_HEIGHT, (float) TORNADO_HEIGHT);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {return new ClientboundAddEntityPacket(this);}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}
}
