package org.confluence.mod.common.entity.yoyo;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/// 悠悠球的短寿命衍生攻击，保留发射伤害，不读取玩家后来切换的武器。
public abstract class BaseYoyoProjectile extends Projectile implements Immunity {
    private final Set<UUID> hitTargets = new HashSet<>();
    private float damage;
    private float criticalChance;
    private float knockback;
    private @Nullable UUID excludedTarget;
    private boolean spent;

    protected BaseYoyoProjectile(EntityType<? extends BaseYoyoProjectile> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    public void shoot(YoyoEntity source, Vec3 direction, @Nullable LivingEntity excluded) {
        BaseYoyoProjectile shot = this;
        shot.setOwner(source.getOwner());
        shot.criticalChance = source.getCriticalChance();
        shot.knockback = source.getKnockback();
        shot.excludedTarget = excluded == null || !excludesInitialTarget() ? null : excluded.getUUID();
        shot.damage = source.getDamage() * damageMultiplier();
        shot.setPos(source.position().add(0, source.getBbHeight() * 0.5, 0));
        shot.setDeltaMovement(direction.normalize().scale(speed()));
        source.level().addFreshEntity(shot);
    }

    /// 只在发射时选择目标，发射后沿直线运动。
    public void shootAtNearest(YoyoEntity source, @Nullable LivingEntity excluded) {
        Entity owner = source.getOwner();
        if (owner == null) return;
        double range = targetRange();
        double distance = range * range;
        LivingEntity nearest = null;
        for (LivingEntity candidate : source.level().getEntitiesOfClass(LivingEntity.class, source.getBoundingBox().inflate(range))) {
            if (candidate == excluded || !EnemyDamageRules.isEnemy(candidate) || !ProjectileHitRules.canHit(owner, candidate))
                continue;
            if (requiresLineOfSight() && !candidate.noPhysics && source.level().clip(new ClipContext(source.position(), candidate.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, source)).getType() != HitResult.Type.MISS)
                continue;
            double next = source.distanceToSqr(candidate);
            if (next < distance) {
                nearest = candidate;
                distance = next;
            }
        }
        Vec3 direction;
        if (nearest != null)
            direction = nearest.getBoundingBox().getCenter().subtract(source.position());
        else if (fallsBackToExcludedTarget() && excluded != null)
            direction = excluded.getBoundingBox().getCenter().subtract(source.position());
        else
            direction = new Vec3(source.getRandom1211().nextGaussian(), source.getRandom1211().nextGaussian(), source.getRandom1211().nextGaussian());
        shoot(source, direction, excluded);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.tick();
        Vec3 motion = getDeltaMovement();
        if (level().isClientSide) {
            level().addParticle(particle(), getX(), getY(), getZ(), 0, 0, 0);
        } else {
            if (!(getOwner() instanceof ServerPlayer owner) || !owner.isAlive() || owner.isSpectator() || tickCount > 20) {
                discard();
                return;
            }
            if (!spent) {
                for (Entity candidate : level().getEntities(this, getBoundingBox().expandTowards(motion).inflate(0.3))) {
                    /// 自动衍生弹幕的命中与索敌采用同一敌怪规则，不误伤沿途城镇 NPC。
                    if (!EnemyDamageRules.isEnemy(candidate) || !ProjectileHitRules.canHit(owner, candidate))
                        continue;
                    Entity identity = ProjectileHitRules.dedupeIdentity(candidate);
                    UUID id = identity.getUUID();
                    LivingEntity logical = ProjectileHitRules.logicalLivingTarget(candidate);
                    if (logical == null) continue;
                    if (logical.getUUID().equals(excludedTarget) && ignoresExcludedTarget())
                        continue;
                    if (hitTargets.contains(id)) continue;
                    if (!candidate.getBoundingBox().inflate(0.3).contains(position()) && candidate.getBoundingBox().inflate(0.3).clip(position(), position().add(motion)).isEmpty())
                        continue;
                    Entity recipient = ProjectileHitRules.damageRecipient(candidate);
                    DamageSource damageSource = LibDamageTypes.of(level(), LibDamageTypes.SWORD_PROJECTILE, this, owner);
                    Immunity immunity = confluence$getImmunityType() == Type.LOCAL ? this : (Immunity) getType();
                    if (!Immunity.hurt(immunity, recipient, logical, damageSource, damage))
                        continue;
                    hitTargets.add(id);
                    logical.knockback(knockback, -motion.x, -motion.z);
                    onHit(recipient);
                    if (!pierces()) {
                        spent = true;
                        break;
                    }
                }
            }
        }
        setPos(position().add(motion));
        if (tickCount > 10) setDeltaMovement(motion.scale(0.85));
    }

    public float getCriticalChance() {return criticalChance;}

    protected abstract ParticleOptions particle();

    protected float damageMultiplier() {return 1;}

    protected double speed() {return 0.8;}

    protected double targetRange() {return 25;}

    protected boolean requiresLineOfSight() {return false;}

    protected boolean fallsBackToExcludedTarget() {return false;}

    protected boolean excludesInitialTarget() {return true;}

    protected boolean ignoresExcludedTarget() {return true;}

    protected boolean pierces() {return false;}

    protected void onHit(Entity recipient) {}

    @Override
    public boolean shouldBeSaved() {return false;}

    @Override
    public boolean canChangeDimensions() {return false;}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {return NetworkHooks.getEntitySpawningPacket(this);}

    @Override
    public Type confluence$getImmunityType() {return Type.LOCAL;}

    @Override
    public int confluence$getImmunityDuration(DamageSource source) {return 4;}
}
