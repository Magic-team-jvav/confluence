package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.mana.ThunderstrikePattern;
import org.confluence.mod.network.s2c.ThunderstrikePacketS2C;
import org.mesdag.portlib.network.PortPacketDistributor;

import java.util.*;

/** Ten server ticks from cast to strike, including time spent attached to a victim. */
public final class ThunderstrikeProjectile extends AbstractManaProjectile {
    public static final int TRAVEL_TICKS = ThunderstrikePattern.TRAVEL_TICKS;
    private Vec3 destination = Vec3.ZERO;
    private LivingEntity attached;
    private UUID attachedId;
    private final Set<UUID> sparkHits = new HashSet<>();
    private int hits;

    public ThunderstrikeProjectile(EntityType<ThunderstrikeProjectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
        withParticle(Confluence.asResource("thunderstrike_spark"));
    }

    public ThunderstrikeProjectile(LivingEntity owner) {
        this(ModEntities.THUNDERSTRIKE_SPARK.get(), owner.level());
    }

    public void aimAt(Vec3 target) {
        destination = target;
        setDeltaMovement(target.subtract(position()).scale(1.0 / TRAVEL_TICKS));
    }

    @Override public void baseTick() {
        super.baseTick();
        if (level().isClientSide) {
            doSimpleMove();
            return;
        }
        if (!(getOwner() instanceof LivingEntity owner) || !owner.isAlive()) {
            discard();
            return;
        }
        if (attached == null && attachedId != null && level() instanceof net.minecraft.server.level.ServerLevel server) {
            if (server.getEntity(attachedId) instanceof LivingEntity living) attached = living;
        }
        Vec3 previous = position();
        if (attached != null && attached.isAlive()) {
            // Keep the attachment on the owner's side of solid collision shapes.
            setPos(clip(previous, attached.getBoundingBox().getCenter()));
            setDeltaMovement(Vec3.ZERO);
        } else {
            // Losing the original enemy never transfers the spark to a second enemy.
            Vec3 next = clip(previous, ThunderstrikePattern.step(previous, destination, tickCount));
            var candidates = level().getEntitiesOfClass(LivingEntity.class, new AABB(previous, next).inflate(0.75),
                    this::validEnemy);
            Vec3 segmentEnd = next;
            candidates.sort(Comparator.comparingDouble(e -> contact(e, previous, segmentEnd).distanceToSqr(previous)));
            for (LivingEntity enemy : candidates) {
                var contact = enemy.getBoundingBox().inflate(0.75).clip(previous, next);
                if (enemy.getBoundingBox().inflate(0.75).contains(previous) || contact.isPresent()) {
                    Vec3 point = contact.orElse(previous);
                    if (attachedId == null && clip(point, enemy.getBoundingBox().getCenter())
                            .distanceToSqr(enemy.getBoundingBox().getCenter()) < 1.0E-6) {
                        attached = enemy;
                        attachedId = enemy.getUUID();
                        next = point;
                        break;
                    }
                }
            }
            hurtLine(previous, next, 0.18, sparkHits, false);
            if (attached != null && sparkHits.add(attached.getUUID())) hurtEnemy(attached, false);
            setPos(next);
            setDeltaMovement(attached != null ? Vec3.ZERO : destination.subtract(next)
                    .scale(1.0 / Math.max(1, TRAVEL_TICKS - tickCount)));
        }
        if (tickCount >= TRAVEL_TICKS) {
            strike();
            discard();
        }
    }

    private Vec3 clip(Vec3 a, Vec3 b) {
        return level().clip(new ClipContext(a, b, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getLocation();
    }

    private Vec3 contact(LivingEntity enemy, Vec3 a, Vec3 b) {
        return enemy.getBoundingBox().inflate(0.75).clip(a, b).orElse(a);
    }

    private boolean validEnemy(LivingEntity enemy) {
        return enemy.isAlive() && !enemy.isSpectator() && canHitEntity(enemy)
                && (!(enemy instanceof Player player) || getOwner() instanceof Player owner && owner.canHarmPlayer(player));
    }

    private void hurtEnemy(LivingEntity enemy, boolean debuffs) {
        if (enemy.hurt(getDamageSource(), (float) (getCalculatedDamage() * Math.pow(0.8, hits)))) {
            hits++;
            if (debuffs) {
                int duration = 80 + random.nextInt(61);
                enemy.addEffect(new MobEffectInstance(ModEffects.ELECTRIFIED.get(), duration));
                enemy.addEffect(new MobEffectInstance(ModEffects.HELLFIRE.get(), duration));
            }
        }
    }

    private void hurtLine(Vec3 a, Vec3 b, double radius, Set<UUID> seen, boolean debuffs) {
        var enemies = level().getEntitiesOfClass(LivingEntity.class, new AABB(a, b).inflate(radius), this::validEnemy);
        enemies.sort(Comparator.comparingDouble(e -> e.distanceToSqr(a)));
        for (var enemy : enemies) {
            var bounds = enemy.getBoundingBox().inflate(radius);
            if ((bounds.contains(a) || bounds.clip(a, b).isPresent()) && seen.add(enemy.getUUID())) hurtEnemy(enemy, debuffs);
        }
    }

    private void strike() {
        Vec3 end = position();
        Vec3 start = end.add(0, Math.max(32, level().getMaxBuildHeight() - end.y), 0);
        long seed = random.nextLong();
        Vec3 look = getOwner().getLookAngle();
        Vec3 spreadAxis = new Vec3(look.z, 0, -look.x).normalize();
        var paths = ThunderstrikePattern.paths(start, end, seed, spreadAxis);
        Set<UUID> struck = new HashSet<>();
        Set<BlockPos> fluidSeeds = new HashSet<>();
        boolean ownerHit = false;
        for (var path : paths) {
            var points = path.points();
            for (int i = 1; i < points.size(); i++) {
                Vec3 a = points.get(i - 1), b = points.get(i);
                hurtLine(a, b, 0.22, struck, true);
                if (getOwner() instanceof LivingEntity owner && (owner.getBoundingBox().inflate(0.22).contains(a)
                        || owner.getBoundingBox().inflate(0.22).clip(a, b).isPresent())) ownerHit = true;
                int steps = Math.max(1, (int) Math.ceil(a.distanceTo(b) * 4));
                for (int j = 0; j <= steps; j++) {
                    BlockPos pos = BlockPos.containing(a.lerp(b, j / (double) steps));
                    if (level().hasChunkAt(pos) && !level().getFluidState(pos).isEmpty()) fluidSeeds.add(pos);
                }
            }
        }
        var nearby = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(1.5), this::validEnemy);
        for (var enemy : nearby) {
            if (enemy.getBoundingBox().distanceToSqr(end) <= 2.25 && struck.add(enemy.getUUID())) hurtEnemy(enemy, true);
        }
        // Flood only the struck, connected liquid inside an eight-block impact radius.
        Set<BlockPos> liquid = new HashSet<>(fluidSeeds);
        ArrayDeque<BlockPos> queue = new ArrayDeque<>(fluidSeeds);
        while (!queue.isEmpty() && liquid.size() < 4096) {
            BlockPos pos = queue.removeFirst();
            for (Direction direction : Direction.values()) {
                BlockPos next = pos.relative(direction);
                if (next.distToCenterSqr(end) > 64 || !level().hasChunkAt(next) || liquid.contains(next)) continue;
                if (!level().getFluidState(next).isEmpty()
                        && level().getFluidState(next).getType().isSame(level().getFluidState(pos).getType())) {
                    liquid.add(next); queue.addLast(next);
                }
            }
        }
        if (!liquid.isEmpty()) {
            for (var enemy : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(8))) {
                boolean immersed = BlockPos.betweenClosedStream(enemy.getBoundingBox().deflate(0.001))
                        .anyMatch(liquid::contains);
                if (enemy == getOwner()) {
                    ownerHit |= immersed || enemy.getBoundingBox().distanceToSqr(end) <= 2.25;
                } else if (immersed && validEnemy(enemy) && struck.add(enemy.getUUID())) hurtEnemy(enemy, true);
            }
            if (ownerHit && getOwner() instanceof LivingEntity owner
                    && owner.hurt(getDamageSource(), 120)) {
                owner.addEffect(new MobEffectInstance(ModEffects.ELECTRIFIED.get(), 80 + random.nextInt(61)));
            }
        }
        PortPacketDistributor.sendToPlayersNear(level().dimension(), null, end.x, end.y, end.z, 128,
                new ThunderstrikePacketS2C(start, end, seed, spreadAxis));
        level().playSound(null, end.x, end.y, end.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1, 1.2F);
    }

    @Override protected void doHitCheck() {} // Swept spark and penetrating lightning are resolved above.

    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putDouble("TargetX", destination.x); tag.putDouble("TargetY", destination.y); tag.putDouble("TargetZ", destination.z);
        tag.putInt("ThunderstrikeHits", hits);
        if (attachedId != null) tag.putUUID("Attached", attachedId);
        var savedHits = new net.minecraft.nbt.ListTag();
        for (UUID id : sparkHits) savedHits.add(net.minecraft.nbt.NbtUtils.createUUID(id));
        tag.put("SparkHits", savedHits);
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        destination = new Vec3(tag.getDouble("TargetX"), tag.getDouble("TargetY"), tag.getDouble("TargetZ"));
        hits = tag.getInt("ThunderstrikeHits");
        attachedId = tag.hasUUID("Attached") ? tag.getUUID("Attached") : null;
        sparkHits.clear();
        for (var id : tag.getList("SparkHits", net.minecraft.nbt.Tag.TAG_INT_ARRAY)) sparkHits.add(net.minecraft.nbt.NbtUtils.loadUUID(id));
    }
}
